package pycro.usts.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import pycro.usts.model.system.SysMenu;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author Pycro
 * @since 2023-05-16
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    // 根据userId查询可以操作菜单列表
    List<SysMenu> findListByUserId(@Param("userId") Long userId);
}
