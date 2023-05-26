package pycro.usts.auth.activiti;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * @author Pycro
 * @version 1.0
 * 2023-05-26 8:33 AM
 */
public class MyTaskListener implements TaskListener {
    @Override
    public void notify(DelegateTask task) {
        if (task.getName().equals("经理审批")) {
            task.setAssignee("Tom");
        } else if (task.getName().equals("人事审批")) {
            task.setAssignee("Jerry");
        }
    }
}
