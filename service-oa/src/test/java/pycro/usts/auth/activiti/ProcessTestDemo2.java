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
public class ProcessTestDemo2 {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;

    /* uel-value  */
    // 流程部署
    @Test
    public void deployProcess() {
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/jiaban.bpmn20.xml")
                .name("加班流程申请")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }

    // 启动流程实例
    @Test
    public void startProcessInstance() {
        Map<String, Object> map = new HashMap<>();
        map.put("assignee1", "Lucy03");
        // map.put("assignee2", "Mary02");
        // 创建流程实例
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("jiaban", map);
        System.out.println(processInstance.getProcessDefinitionId());
        System.out.println(processInstance.getId());
    }

    @Test
    public void completeTask() {
        // 根据负责人查询一条任务
        Task task = taskService.createTaskQuery()
                .taskAssignee("Lucy03")
                .singleResult();
        // 完成任务，参数：任务id
        Map<String, Object> variables = new HashMap<>();
        variables.put("assignee2", "Fake");
        taskService.complete(task.getId(), variables);
    }

    // 查询代办任务
    @Test
    public void findTaskList() {
        String assignee = "Fake";
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
}
