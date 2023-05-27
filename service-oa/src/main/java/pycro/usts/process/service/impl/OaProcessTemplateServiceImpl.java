package pycro.usts.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pycro.usts.model.process.ProcessTemplate;
import pycro.usts.model.process.ProcessType;
import pycro.usts.process.mapper.OaProcessTemplateMapper;
import pycro.usts.process.service.OaProcessService;
import pycro.usts.process.service.OaProcessTemplateService;
import pycro.usts.process.service.OaProcessTypeService;

import java.util.List;

/**
 * <p>
 * 审批模板 服务实现类
 * </p>
 *
 * @author Pycro
 * @since 2023-05-26
 */
@Service
public class OaProcessTemplateServiceImpl extends ServiceImpl<OaProcessTemplateMapper, ProcessTemplate> implements OaProcessTemplateService {
    @Autowired
    private OaProcessTypeService processTypeService;
    @Autowired
    private OaProcessService processService;

    @Override
    public IPage<ProcessTemplate> selectPageProcessTemplate(Page<ProcessTemplate> pageParam) {
        // 1 调用mapper方法实现分页查询
        Page<ProcessTemplate> processTemplatePage = baseMapper.selectPage(pageParam, null);
        // 2 从分页查询的返回数据中获取list集合
        List<ProcessTemplate> templateList = processTemplatePage.getRecords();
        // 3 遍历list集合，获取每个模板的类型id
        for (ProcessTemplate processTemplate : templateList) {
            Long processTypeId = processTemplate.getProcessTypeId();
            // 4 根据类型id，查询类型名称并在template中设置
            LambdaQueryWrapper<ProcessType> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProcessType::getId, processTypeId);// processTypeId非必填字段，可能出现空值
            ProcessType processType = processTypeService.getOne(wrapper);
            if (processType == null) continue;
            // 5 完成最终的封装
            processTemplate.setProcessTypeName(processType.getName());
        }
        return processTemplatePage;
    }

    // 修改模板发布状态 1 已发布
    // 部署流程定义（发布）
    @Override
    public void publish(Long id) {
        // 修改模板发布状态
        ProcessTemplate processTemplate = baseMapper.selectById(id);
        processTemplate.setStatus(1);
        baseMapper.updateById(processTemplate);
        // 部署流程定义
        String definitionPath = processTemplate.getProcessDefinitionPath();
        if (!StringUtils.isEmpty(definitionPath)) {
            processService.deployByZip(definitionPath);
        }
    }
}
