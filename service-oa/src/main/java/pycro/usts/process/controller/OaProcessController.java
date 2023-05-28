package pycro.usts.process.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pycro.usts.common.result.Result;
import pycro.usts.process.service.OaProcessService;
import pycro.usts.vo.process.ProcessQueryVo;
import pycro.usts.vo.process.ProcessVo;

/**
 * <p>
 * 审批类型 前端控制器
 * </p>
 *
 * @author Pycro
 * @since 2023-05-27
 */
@RestController
@RequestMapping("/admin/process")
public class OaProcessController {
    @Autowired
    OaProcessService processService;

    // 审批管理列表
    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(
            @PathVariable Long page,
            @PathVariable Long limit,
            ProcessQueryVo processQueryVo
    ) {
        Page<ProcessVo> pageParam = new Page<>(page, limit);
        IPage<ProcessVo> pageModel = processService.selectPage(pageParam, processQueryVo);
        return Result.ok(pageModel);
    }
}

