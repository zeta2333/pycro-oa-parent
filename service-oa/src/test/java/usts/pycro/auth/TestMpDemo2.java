package usts.pycro.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pycro.usts.auth.ServiceAuthApplication;
import pycro.usts.auth.service.SysRoleService;
import pycro.usts.model.system.SysRole;

import java.util.List;

/**
 * @author Pycro
 * @version 1.0
 * 2023-05-12 8:50 AM
 */

@SpringBootTest(classes = ServiceAuthApplication.class)
public class TestMpDemo2 {
    // 注入
    @Autowired
    SysRoleService service;

    // 查询所有记录
    @Test
    public void getAll() {
        List<SysRole> list = service.list();
        list.forEach(System.out::println);
    }

}
