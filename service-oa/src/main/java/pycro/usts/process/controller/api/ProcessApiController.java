package pycro.usts.process.controller.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pycro.usts.auth.service.SysUserService;
import pycro.usts.common.result.Result;
import pycro.usts.model.process.Process;
import pycro.usts.model.process.ProcessTemplate;
import pycro.usts.model.process.ProcessType;
import pycro.usts.process.service.OaProcessService;
import pycro.usts.process.service.OaProcessTemplateService;
import pycro.usts.process.service.OaProcessTypeService;
import pycro.usts.vo.process.ApprovalVo;
import pycro.usts.vo.process.ProcessFormVo;
import pycro.usts.vo.process.ProcessVo;

import java.util.List;
import java.util.Map;

/**
 * @author Pycro
 * @version 1.0
 * 2023-05-28 9:47 AM
 */
@Api(tags = "审批流管理")
@RestController
@RequestMapping(value = "/admin/process")
@CrossOrigin  // 跨域
public class ProcessApiController {
    @Autowired
    private OaProcessTypeService processTypeService;

    @Autowired
    private OaProcessTemplateService processTemplateService;

    @Autowired
    private OaProcessService processService;

    @Autowired
    private SysUserService sysUserService;

    // 查询待处理列表
    @ApiOperation(value = "待处理")
    @GetMapping("findPending/{page}/{limit}")
    public Result findPending(
            @PathVariable Long page,
            @PathVariable Long limit) {
        Page<Process> pageParam = new Page<>(page, limit);
        IPage<ProcessVo> pageModel = processService.findPending(pageParam);
        return Result.ok(pageModel);
    }

    @ApiOperation("启动流程")
    @PostMapping("startUp")
    public Result startUp(@RequestBody ProcessFormVo processFormVo) {
        processService.startUp(processFormVo);
        return Result.ok();
    }

    // 获取审批模板数据
    @ApiOperation(value = "获取审批模板")
    @GetMapping("getProcessTemplate/{processTemplateId}")
    public Result getProcessTemplate(@PathVariable Long processTemplateId) {
        ProcessTemplate processTemplate = processTemplateService.getById(processTemplateId);
        return Result.ok(processTemplate);
    }

    // 查询所有的审批分类及每个分类下的所有审批模板
    @GetMapping("findProcessType")
    public Result findProcessType() {
        List<ProcessType> list = processTypeService.findProcessType();
        return Result.ok(list);
    }

    // 查看审批详情信息
    @GetMapping("/show/{id}")
    public Result show(@PathVariable Long id) {
        Map<String, Object> map = processService.show(id);
        return Result.ok(map);
    }

    // 审批
    @ApiOperation(value = "审批")
    @PostMapping("approve")
    public Result approve(@RequestBody ApprovalVo approvalVo) {
        processService.approve(approvalVo);
        return Result.ok();
    }

    // 已处理
    @ApiOperation(value = "已处理")
    @GetMapping("/findProcessed/{page}/{limit}")
    public Result findProcessed(
            @PathVariable Long page,
            @PathVariable Long limit) {
        Page<Process> pageParam = new Page<>(page, limit);
        return Result.ok(processService.findProcessed(pageParam));
    }

    // 已发起
    @ApiOperation(value = "已发起")
    @GetMapping("/findStarted/{page}/{limit}")
    public Result findStarted(
            @PathVariable Long page,
            @PathVariable Long limit) {
        Page<ProcessVo> pageParam = new Page<>(page, limit);
        return Result.ok(processService.findStarted(pageParam));
    }

    // 获取当前用户信息
    @ApiOperation(value = "获取当前用户基本信息")
    @GetMapping("getCurrentUser")
    public Result getCurrentUser() {
        Map<String, Object> map = sysUserService.getCurrentUser();
        return Result.ok(map);
    }
}
