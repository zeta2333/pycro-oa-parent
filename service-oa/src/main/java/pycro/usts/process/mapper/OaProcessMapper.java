package pycro.usts.process.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import pycro.usts.model.process.Process;
import pycro.usts.vo.process.ProcessQueryVo;
import pycro.usts.vo.process.ProcessVo;

/**
 * <p>
 * 审批类型 Mapper 接口
 * </p>
 *
 * @author Pycro
 * @since 2023-05-27
 */
public interface OaProcessMapper extends BaseMapper<Process> {
    // 审批管理列表
    IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, @Param("vo") ProcessQueryVo processQueryVo);
}
