package pycro.usts.auth.activiti;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author Pycro
 * @version 1.0
 * 2023-05-23 7:50 AM
 */
@SpringBootTest
public class ProcessTest {
    // 注入repositoryService
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;

    @Test
    public void SingleSuspendProcessInstance() {
        String processInstanceId = "65670ba4-f908-11ed-bd99-9c2dcd099ef4";
        // 获取单个流程实例对象
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        boolean suspended = processInstance.isSuspended();
        if (suspended) {
            runtimeService.activateProcessInstanceById(processInstanceId);
            System.out.println("流程实例:" + processInstanceId + "激活了");
        } else {
            runtimeService.suspendProcessInstanceById(processInstanceId);
            System.out.println("流程实例:" + processInstanceId + "挂起了");
        }
    }

    // 全部流程实例挂起（即将流程定义挂起）
    @Test
    public void suspendProcessInstanceAll() {
        // 获取流程定义的对象
        ProcessDefinition qingjia = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("qingjia").singleResult();
        // 判断是否处于挂起状态
        boolean suspended = qingjia.isSuspended();
        if (suspended) {
            repositoryService.activateProcessDefinitionById(qingjia.getId(), true, null);
            System.out.println("流程定义id：" + qingjia.getId() + "激活了");
        } else {
            repositoryService.suspendProcessDefinitionById(qingjia.getId(), true, null);
            System.out.println("流程定义id：" + qingjia.getId() + "挂起了");
        }
    }

    // 创建流程实例，指定BusinessKey
    @Test
    public void startUpProcessAddBusinessKey() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("qingjia", "1001");
        System.out.println("业务id:" + processInstance.getBusinessKey());

    }

    @Test
    public void findCompletedTask() {
        List<HistoricTaskInstance> list = historyService
                .createHistoricTaskInstanceQuery()
                .taskAssignee("lisi")
                .finished().list();
        list.forEach(task -> {
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        });
    }

    @Test
    public void completeTask() {
        // 根据负责人查询一条任务
        Task task = taskService.createTaskQuery()
                .taskAssignee("zhangsan")
                .singleResult();
        // 完成任务，参数：任务id
        taskService.complete(task.getId());
    }

    // 查询gr代办任务——Pycro
    @Test
    public void findTaskList() {
        String assignee = "Pycro";
        List<Task> list = taskService.createTaskQuery()
                .taskAssignee(assignee)
                .list();
        list.forEach(task -> {
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        });
    }

    @Test
    public void startProcess() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("qingjia");
        System.out.println("流程定义id：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例id：" + processInstance.getId());
        System.out.println("流程活动id：" + processInstance.getActivityId());
    }

    @Test
    public void deployProcess() {
        // 流程部署
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/qingjia.bpmn20.xml")
                .addClasspathResource("process/qingjia.png")
                .name("请假申请流程")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }
}
