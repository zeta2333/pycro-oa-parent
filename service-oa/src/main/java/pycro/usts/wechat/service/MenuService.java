package pycro.usts.wechat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pycro.usts.model.wechat.Menu;
import pycro.usts.vo.wechat.MenuVo;

import java.util.List;

/**
 * <p>
 * 菜单 服务类
 * </p>
 *
 * @author Pycro
 * @since 2023-05-29
 */
public interface MenuService extends IService<Menu> {

    // 获取全部菜单
    List<MenuVo> findMenuInfo();

    // 同步菜单
    void syncMenu();

    // 删除菜单
    void removeMenu();
}
