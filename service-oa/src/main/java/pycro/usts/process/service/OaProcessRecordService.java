package pycro.usts.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pycro.usts.model.process.ProcessRecord;

/**
 * <p>
 * 审批记录 服务类
 * </p>
 *
 * @author Pycro
 * @since 2023-05-28
 */
public interface OaProcessRecordService extends IService<ProcessRecord> {
    // 流程历史记录
    void record(Long processId, Integer status, String description);
}
