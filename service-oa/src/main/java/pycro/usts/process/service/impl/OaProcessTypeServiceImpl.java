package pycro.usts.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pycro.usts.model.process.ProcessTemplate;
import pycro.usts.model.process.ProcessType;
import pycro.usts.process.mapper.OaProcessTypeMapper;
import pycro.usts.process.service.OaProcessTemplateService;
import pycro.usts.process.service.OaProcessTypeService;

import java.util.List;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author Pycro
 * @since 2023-05-26
 */
@Service
public class OaProcessTypeServiceImpl extends ServiceImpl<OaProcessTypeMapper, ProcessType> implements OaProcessTypeService {
    @Autowired
    private OaProcessTemplateService processTemplateService;

    // 查询所有的审批分类及每个分类下的所有审批模板
    @Override
    public List<ProcessType> findProcessType() {
        // 1 查询所有审批分类
        List<ProcessType> processTypeList = baseMapper.selectList(null);
        // 2 遍历，查询每个审批分类下的所有模板
        for (ProcessType processType : processTypeList) {
            // 3 根据审批分类的id，查询对应的审批模板
            LambdaQueryWrapper<ProcessTemplate> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProcessTemplate::getProcessTypeId, processType.getId());
            List<ProcessTemplate> processTemplateList = processTemplateService.list(wrapper);
            // 4 封装
            processType.setProcessTemplateList(processTemplateList);
        }
        return processTypeList;
    }
}
