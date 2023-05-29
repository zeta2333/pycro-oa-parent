package pycro.usts.process.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pycro.usts.auth.service.SysUserService;
import pycro.usts.model.process.Process;
import pycro.usts.model.process.ProcessRecord;
import pycro.usts.model.process.ProcessTemplate;
import pycro.usts.model.system.SysUser;
import pycro.usts.process.mapper.OaProcessMapper;
import pycro.usts.process.service.OaProcessRecordService;
import pycro.usts.process.service.OaProcessService;
import pycro.usts.process.service.OaProcessTemplateService;
import pycro.usts.security.custom.LoginUserInfoHelper;
import pycro.usts.vo.process.ApprovalVo;
import pycro.usts.vo.process.ProcessFormVo;
import pycro.usts.vo.process.ProcessQueryVo;
import pycro.usts.vo.process.ProcessVo;
import pycro.usts.wechat.service.MessageService;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author Pycro
 * @since 2023-05-27
 */
@Service
public class OaProcessServiceImpl extends ServiceImpl<OaProcessMapper, Process> implements OaProcessService {

    @Autowired
    private OaProcessTemplateService processTemplateService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private OaProcessRecordService processRecordService;

    @Autowired
    private MessageService messageService;

    // 条件分页查询
    @Override
    public IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo) {
        return baseMapper.selectPage(pageParam, processQueryVo);
    }

    // 部署流程定义
    @Override
    public void deployByZip(String deployPath) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(deployPath);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        // 部署
        Deployment deployment = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .deploy();
        System.out.println(deployment.getId());
        System.out.println(deployment.getName());
    }

    // 启动流程
    @Override
    public void startUp(ProcessFormVo processFormVo) {
        // 1 根据id查询当前用户
        SysUser sysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());
        // 2 根据id查询审批模板信息
        ProcessTemplate processTemplate = processTemplateService.getById(processFormVo.getProcessTemplateId());
        // 3 保存提交审批信息到业务表
        Process process = new Process();
        // processFormVo 复制到 process中
        BeanUtils.copyProperties(processFormVo, process);
        // 其他值
        String workNo = String.valueOf(System.currentTimeMillis());
        process.setProcessCode(workNo);
        process.setUserId(LoginUserInfoHelper.getUserId());
        process.setFormValues(processFormVo.getFormValues());
        process.setTitle(sysUser.getName() + "发起" + processTemplate.getName() + "申请");
        process.setStatus(1);
        baseMapper.insert(process);

        // 4 启动流程实例
        // 流程定义id
        String processDefinitionKey = processTemplate.getProcessDefinitionKey();
        // 业务id
        String businessKey = String.valueOf(process.getId());
        // 流程参数
        // 将表单数据放入流程实例中
        JSONObject jsonObject = JSON.parseObject(process.getFormValues());
        JSONObject formData = jsonObject.getJSONObject("formData");
        Map<String, Object> map = new HashMap<>();
        // 循环转换
        for (Map.Entry<String, Object> entry : formData.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        Map<String, Object> variables = new HashMap<>();
        variables.put("data", map);
        // 启动流程实例
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey(
                        processDefinitionKey,
                        businessKey,
                        variables
                );
        // 计算下一个审批人，可能有多个（并行审批）
        List<Task> taskList = this.getCurrentTaskList(processInstance.getId());
        List<String> nameList = new ArrayList<>();
        for (Task task : taskList) {
            SysUser user = sysUserService.getUserByUsername(task.getAssignee());
            nameList.add(user.getName());
            // 推送消息给下一个审批人
            messageService.pushPendingMessage(process.getId(), user.getId(), task.getId());
        }
        process.setProcessInstanceId(processInstance.getId());
        process.setDescription("等待" + StringUtils.join(nameList.toArray(), ",") + "审批");

        // 7 业务表关联当前流程实例id
        baseMapper.updateById(process);

        // 记录操作审批信息
        processRecordService.record(process.getId(), 1, "发起申请");
    }

    // 查询待处理列表
    @Override
    public IPage<ProcessVo> findPending(Page<Process> pageParam) {
        // 根据当前人的ID查询
        TaskQuery query = taskService
                .createTaskQuery()
                .taskAssignee(LoginUserInfoHelper.getUsername())
                .orderByTaskCreateTime()
                .desc();
        // 分页查询
        List<Task> list = query.listPage(
                (int) ((pageParam.getCurrent() - 1) * pageParam.getSize()),
                (int) pageParam.getSize()
        );
        // 总记录数
        long totalCount = query.count();

        // taskList ---> processVoList
        List<ProcessVo> processVoList = new ArrayList<>();
        // 根据流程的业务ID查询实体并关联
        for (Task task : list) {
            String processInstanceId = task.getProcessInstanceId();
            ProcessInstance processInstance = runtimeService
                    .createProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();
            if (processInstance == null) continue;

            // 业务key
            String businessKey = processInstance.getBusinessKey();
            if (businessKey == null) continue;

            // process ---> processVo
            Process process = this.getById(Long.parseLong(businessKey));
            ProcessVo processVo = new ProcessVo();
            if (process != null)
                BeanUtils.copyProperties(process, processVo);
            processVo.setTaskId(task.getId());
            processVoList.add(processVo);
        }
        // 封装成Page对象返回
        IPage<ProcessVo> page = new Page<>(pageParam.getCurrent(), pageParam.getSize(), totalCount);
        page.setRecords(processVoList);
        return page;
    }

    // 查看审批详情信息
    @Override
    public Map<String, Object> show(Long id) {
        // 1 根据流程id获取流程信息process
        Process process = baseMapper.selectById(id);
        // 2 根基流程id获取流程记录信息
        LambdaQueryWrapper<ProcessRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProcessRecord::getProcessId, id);
        List<ProcessRecord> processRecordList = processRecordService.list(wrapper);
        // 3 根据模板id查询模板信息
        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());
        // 4 判断当前用户是否可以审批（可以看到信息不一定能审批，不能重复审批）
        boolean isApprove = false;
        List<Task> taskList = getCurrentTaskList(process.getProcessInstanceId());
        for (Task task : taskList) {
            // 判断任务审批人是否是当前用户
            String username = LoginUserInfoHelper.getUsername();
            if (task.getAssignee().equals(username)) {
                isApprove = true;
            }
        }
        // 5 查询数据封装到map集合，返回
        Map<String, Object> map = new HashMap<>();
        map.put("process", process);
        map.put("processRecordList", processRecordList);
        map.put("processTemplate", processTemplate);
        map.put("isApprove", isApprove);
        return map;
    }

    // 审批
    @Override
    public void approve(ApprovalVo approvalVo) {
        Map<String, Object> variables1 = taskService.getVariables(approvalVo.getTaskId());
        for (Map.Entry<String, Object> entry : variables1.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
        String taskId = approvalVo.getTaskId();
        if (approvalVo.getStatus() == 1) {
            // 已通过
            Map<String, Object> variables = new HashMap<>();
            taskService.complete(taskId, variables);
        } else {
            // 驳回
            this.endTask(taskId);
        }
        String description = approvalVo.getStatus() == 1 ? "已通过" : "驳回";
        processRecordService.record(approvalVo.getProcessId(), approvalVo.getStatus(), description);

        // 计算下一个审批人
        Process process = this.getById(approvalVo.getProcessId());
        List<Task> taskList = this.getCurrentTaskList(process.getProcessInstanceId());
        if (!CollectionUtils.isEmpty(taskList)) {
            List<String> assigneeList = new ArrayList<>();
            for (Task task : taskList) {
                SysUser sysUser = sysUserService.getUserByUsername(task.getAssignee());
                assigneeList.add(sysUser.getName());
                //  推送消息给下一个审批人
                messageService.pushPendingMessage(process.getId(), sysUser.getId(), task.getId());
            }
            process.setDescription("等待" + StringUtils.join(assigneeList.toArray(), ",") + "审批");
            process.setStatus(1);
        } else {
            if (approvalVo.getStatus() == 1) {
                process.setDescription("审批完成（同意）");
                process.setStatus(2);
            } else {
                process.setDescription("审批完成（驳回）");
                process.setStatus(-1);
            }
        }
        // 推送消息给申请人
        this.updateById(process);
    }

    // 已处理
    @Override
    public IPage<ProcessVo> findProcessed(Page<Process> pageParam) {
        // 根据当前人的ID查询,封装查询条件
        HistoricTaskInstanceQuery query = historyService
                .createHistoricTaskInstanceQuery()
                .taskAssignee(LoginUserInfoHelper.getUsername())
                .finished()
                .orderByTaskCreateTime().desc();
        // 调用方法分条件分页查询，返回list集合
        List<HistoricTaskInstance> list = query
                .listPage(
                        (int) ((pageParam.getCurrent() - 1) * pageParam.getSize()), // 开始位置
                        (int) pageParam.getSize() // 每页记录数
                );
        long totalCount = query.count();

        // 遍历list集合，封装成List<ProcessVo>
        List<ProcessVo> processList = new ArrayList<>();
        for (HistoricTaskInstance item : list) {
            // 流程实例id
            String processInstanceId = item.getProcessInstanceId();
            // 根据流程实例id获取process对象
            LambdaQueryWrapper<Process> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Process::getProcessInstanceId, processInstanceId);
            Process process = baseMapper.selectOne(wrapper);
            // process ---> processVo
            ProcessVo processVo = new ProcessVo();
            if (process != null)
                BeanUtils.copyProperties(process, processVo);
            processVo.setTaskId("0");
            processList.add(processVo);
        }
        IPage<ProcessVo> pageModel = new Page<>(pageParam.getCurrent(), pageParam.getSize(), totalCount);
        pageModel.setRecords(processList);
        return pageModel;
    }

    // 已发起
    @Override
    public IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam) {
        ProcessQueryVo processQueryVo = new ProcessQueryVo();
        processQueryVo.setUserId(LoginUserInfoHelper.getUserId());
        IPage<ProcessVo> pageModel = baseMapper.selectPage(pageParam, processQueryVo);
        for (ProcessVo item : pageModel.getRecords()) {
            item.setTaskId("0");
        }
        return pageModel;
    }

    private void endTask(String taskId) {
        //  当前任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        List<EndEvent> endEventList = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);
        // 并行任务可能为null
        if (CollectionUtils.isEmpty(endEventList)) return;

        FlowNode endFlowNode = endEventList.get(0);
        FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey());

        //  临时保存当前活动的原始方向
        List<Object> originalSequenceFlowList = new ArrayList<>();
        originalSequenceFlowList.addAll(currentFlowNode.getOutgoingFlows());
        //  清理活动方向
        currentFlowNode.getOutgoingFlows().clear();

        //  建立新方向
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlowId");
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(endFlowNode);
        List<SequenceFlow> newSequenceFlowList = new ArrayList<>();
        newSequenceFlowList.add(newSequenceFlow);
        //  当前节点指向新的方向
        currentFlowNode.setOutgoingFlows(newSequenceFlowList);

        //  完成当前任务
        taskService.complete(task.getId());
    }

    /**
     * 获取当前任务列表
     *
     * @param processInstanceId
     * @return
     */
    private List<Task> getCurrentTaskList(String processInstanceId) {
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        return tasks;
    }
}
