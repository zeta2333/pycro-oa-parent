package pycro.usts.process.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pycro.usts.auth.service.SysUserService;
import pycro.usts.model.process.ProcessRecord;
import pycro.usts.model.system.SysUser;
import pycro.usts.process.mapper.OaProcessRecordMapper;
import pycro.usts.process.service.OaProcessRecordService;
import pycro.usts.security.custom.LoginUserInfoHelper;

/**
 * <p>
 * 审批记录 服务实现类
 * </p>
 *
 * @author Pycro
 * @since 2023-05-28
 */
@Service
public class OaProcessRecordServiceImpl extends ServiceImpl<OaProcessRecordMapper, ProcessRecord> implements OaProcessRecordService {

    @Autowired
    private SysUserService sysUserService;

    // 流程历史记录
    @Override
    public void record(Long processId, Integer status, String description) {
        Long userId = LoginUserInfoHelper.getUserId();
        SysUser sysUser = sysUserService.getById(userId);
        ProcessRecord processRecord = new ProcessRecord();
        processRecord.setProcessId(processId);
        processRecord.setStatus(status);
        processRecord.setDescription(description);
        processRecord.setOperateUser(sysUser.getName());
        processRecord.setOperateUserId(userId);
        baseMapper.insert(processRecord);
    }
}
