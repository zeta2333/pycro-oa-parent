package pycro.usts.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pycro.usts.model.process.ProcessType;

import java.util.List;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author Pycro
 * @since 2023-05-26
 */
public interface OaProcessTypeService extends IService<ProcessType> {

    // 查询所有的审批分类及每个分类下的所有审批模板
    List<ProcessType> findProcessType();
}
