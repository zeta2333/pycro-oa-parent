package pycro.usts.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import pycro.usts.model.process.ProcessTemplate;

/**
 * <p>
 * 审批模板 服务类
 * </p>
 *
 * @author Pycro
 * @since 2023-05-26
 */
public interface OaProcessTemplateService extends IService<ProcessTemplate> {

    // 分页，填充processTypeName
    IPage<ProcessTemplate> selectPageProcessTemplate(Page<ProcessTemplate> pageParam);

    // 部署流程定义（发布）
    void publish(Long id);
}
