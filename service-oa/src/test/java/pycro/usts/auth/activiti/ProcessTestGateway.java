package pycro.usts.auth.activiti;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Pycro
 * @version 1.0
 * 2023-05-23 10:22 AM
 */
@SpringBootTest
public class ProcessTestGateway {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;

    /* 流程组任务 */
    // 1 部署流程定义和启动流程实例
    @Test
    public void deployProcess() {
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/qingjia_ult.bpmn20.xml")
                .name("请假(并行网关)")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());

        // 设置请假天数
        Map<String, Object> map = new HashMap<>();
        map.put("day", 10);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("qingjia_ult");
        System.out.println(processInstance.getProcessDefinitionId());
        System.out.println(processInstance.getId());
    }

    // 查询待办任务
    @Test
    public void findTaskList() {
        // String assignee = "maven";
        String assignee = "xml";
        List<Task> list = taskService.createTaskQuery().taskAssignee(assignee).list();
        list.forEach(task -> {
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        });
    }

    // 完成任务
    @Test
    public void completeTask() {
        // 根据负责人查询一条任务
        Task task = taskService.createTaskQuery()
                .taskAssignee("xml")
                .singleResult();
        // 完成任务，参数：任务id
        taskService.complete(task.getId());
    }

}
