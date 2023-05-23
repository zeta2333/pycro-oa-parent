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
public class ProcessTestDemo1 {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;

    /* uel-method */
    // 流程部署
    @Test
    public void deployProcess01() {
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/jiaban01.bpmn20.xml")
                .name("加班流程申请01")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }

    // 启动流程实例
    @Test
    public void startProcessInstance01() {
        // 创建流程实例
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("jiaban01");
        System.out.println(processInstance.getProcessDefinitionId());
        System.out.println(processInstance.getId());
    }

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
        map.put("assignee1", "Lucy");
        map.put("assignee2", "Mary");
        // 创建流程实例
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("jiaban", map);
        System.out.println(processInstance.getProcessDefinitionId());
        System.out.println(processInstance.getId());
    }

    // 查询gr代办任务——Lucy
    @Test
    public void findTaskList() {
        String assignee = "Lucy";
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
