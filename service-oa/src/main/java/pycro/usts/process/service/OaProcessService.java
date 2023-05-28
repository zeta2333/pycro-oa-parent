package pycro.usts.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import pycro.usts.model.process.Process;
import pycro.usts.vo.process.ApprovalVo;
import pycro.usts.vo.process.ProcessFormVo;
import pycro.usts.vo.process.ProcessQueryVo;
import pycro.usts.vo.process.ProcessVo;

import java.util.Map;

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

    // 启动流程
    void startUp(ProcessFormVo processFormVo);

    // 查询待处理列表
    IPage<ProcessVo> findPending(Page<Process> pageParam);

    // 查看审批详情信息
    Map<String, Object> show(Long id);

    // 审批
    void approve(ApprovalVo approvalVo);

    // 已处理
    Object findProcessed(Page<Process> pageParam);

    // 已发起
    Object findStarted(Page<ProcessVo> pageParam);
}
