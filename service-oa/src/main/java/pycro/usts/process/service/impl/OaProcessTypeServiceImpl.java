package pycro.usts.process.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pycro.usts.model.process.ProcessType;
import pycro.usts.process.mapper.OaProcessTypeMapper;
import pycro.usts.process.service.OaProcessTypeService;

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

}
