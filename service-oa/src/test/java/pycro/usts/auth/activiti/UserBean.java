package pycro.usts.auth.activiti;

import org.springframework.stereotype.Component;

/**
 * @author Pycro
 * @version 1.0
 * 2023-05-23 10:42 AM
 */
@Component
public class UserBean {
    public String getUsername(int id) {
        if (id == 1) {
            return "Pycro";
        }
        if (id == 2) {
            return "Kight";
        }
        return "Fortune";
    }
}
