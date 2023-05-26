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

import java.util.List;

/**
 * @author Pycro
 * @version 1.0
 * 2023-05-23 10:22 AM
 */
@SpringBootTest
public class ProcessTestDemo3 {
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
                .addClasspathResource("process/jiaban03.bpmn20.xml")
                .name("加班流程申请03")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("jiaban03");
        System.out.println(processInstance.getProcessDefinitionId());
        System.out.println(processInstance.getId());
    }

    // 2 查询组任务
    @Test
    public void findGroupTask() {
        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateUser("tom01").list();
        for (Task task : tasks) {
            System.out.println("----------------------------");
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
    }

    // 3 拾取组任务
    @Test
    public void claimTask() {
        Task task = taskService.createTaskQuery()
                .taskCandidateUser("tom01")
                .singleResult();
        if (task != null) {
            taskService.claim(task.getId(), "tom01");
            System.out.println("任务拾取成功");
        }
    }

    // 4 查询代办任务
    @Test
    public void findTaskList() {
        String assignee = "tom01";
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

    // 5 办理个人任务
    @Test
    public void completeTask() {
        // 根据负责人查询一条任务
        Task task = taskService.createTaskQuery()
                .taskAssignee("tom01")
                .singleResult();
        // 完成任务，参数：任务id
        taskService.complete(task.getId());
    }

}
