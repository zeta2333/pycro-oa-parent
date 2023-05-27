package pycro.usts.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import pycro.usts.model.process.Process;
import pycro.usts.vo.process.ProcessQueryVo;
import pycro.usts.vo.process.ProcessVo;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author Pycro
 * @since 2023-05-27
 */
public interface OaProcessService extends IService<Process> {

    // 条件分页查询
    IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo);

    // 部署流程定义
    void  deployByZip(String deployPath);
}
