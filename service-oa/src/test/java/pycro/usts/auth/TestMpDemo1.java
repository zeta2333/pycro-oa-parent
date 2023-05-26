package pycro.usts.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pycro.usts.auth.mapper.SysRoleMapper;
import pycro.usts.model.system.SysRole;

import java.util.Arrays;
import java.util.List;

/**
 * @author Pycro
 * @version 1.0
 * 2023-05-12 8:50 AM
 */

@SpringBootTest
public class TestMpDemo1 {
    // 注入
    @Autowired
    SysRoleMapper mapper;

    // 查询所有记录
    @Test
    public void getAll() {
        List<SysRole> list = mapper.selectList(null);
        list.forEach(System.out::println);
    }

    // 插入记录
    @Test
    public void add() {
        SysRole sysRole = new SysRole();
        sysRole.setRoleName("角色管理员1");
        sysRole.setRoleCode("role_mgr1");
        sysRole.setDescription("66");
        int rows = mapper.insert(sysRole);
        System.out.println(rows);
        System.out.println(sysRole.getId());
    }

    // 修改
    @Test
    public void update() {
        // 根据id查询
        SysRole sysRole = mapper.selectById(5);
        // 修改记录
        sysRole.setRoleName("角色管理员_upd");
        // 调用方法实现最终的修改
        int update = mapper.updateById(sysRole);
        System.out.println(update);
    }

    // 删除
    @Test
    public void delete() {
        int i = mapper.deleteById(4);
        System.out.println(i);
    }

    // 批量删除
    @Test
    public void deleteBatch() {
        int i = mapper.deleteBatchIds(Arrays.asList(1, 2, 3));
        System.out.println(i);
    }

    // 条件查询
    @Test
    public void testQuery1() {
        // 创建queryWrapper对象，调用方法封装条件
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        // or()实现条件或的逻辑，并且 string "1" 自动转换成 int 1
        wrapper.eq("role_name", "总经理").or().eq("id", "1");
        // 调用mp方法实现条件查询操作
        List<SysRole> list = mapper.selectList(wrapper);
        list.forEach(System.out::println);
    }

    // lambda条件查询
    @Test
    public void testQuery2() {
        // 创建queryWrapper对象，调用方法封装条件
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        // or()实现条件或的逻辑，并且 string "1" 自动转换成 int 1
        wrapper.eq(SysRole::getRoleName, "总经理").or().eq(SysRole::getId, "1");
        // 调用mp方法实现条件查询操作
        List<SysRole> list = mapper.selectList(wrapper);
        list.forEach(System.out::println);
    }
}
