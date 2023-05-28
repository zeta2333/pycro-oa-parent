package pycro.usts.process.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pycro.usts.common.result.Result;
import pycro.usts.model.process.ProcessTemplate;
import pycro.usts.process.service.OaProcessTemplateService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 审批模板 前端控制器
 * </p>
 *
 * @author Pycro
 * @since 2023-05-26
 */
@Api(tags = "审批类型")
@RestController
@RequestMapping(value = "/admin/process/processTemplate")
public class OaProcessTemplateController {
    @Autowired
    private OaProcessTemplateService processTemplateService;


    @ApiOperation(value = "上传流程定义")
    @PostMapping("uploadProcessDefinition")
    public Result<?> uploadProcessDefinition(MultipartFile file) throws FileNotFoundException, UnsupportedEncodingException {
        // 获取classes目录位置
        String uploadPath = ResourceUtils.getURL(ResourceUtils.CLASSPATH_URL_PREFIX).getPath();
        String path = new File(URLDecoder.decode(uploadPath, "UTF-8")).getAbsolutePath();
        // 设置上传文件夹
        File tempFile = new File(path + "/process/");
        if (!tempFile.exists()) {
            tempFile.mkdirs();
        }
        // 创建空文件，实现文件写入
        String fileName = file.getOriginalFilename();
        File zipFile = new File(path + "/process/" + fileName);
        // 保存文件
        try {
            file.transferTo(zipFile);
        } catch (IOException e) {
            return Result.fail("上传失败");
        }
        // 返回
        Map<String, Object> map = new HashMap<>();
        // 根据上传地址后续部署流程定义，文件名称为流程定义的默认key
        map.put("processDefinitionPath", "process/" + fileName);
        map.put("processDefinitionKey", fileName.substring(0, fileName.lastIndexOf(".")));
        return Result.ok(map);
    }

    // 部署流程定义（发布）
    @ApiOperation(value = "发布")
    @GetMapping("/publish/{id}")
    public Result publish(@PathVariable Long id) {
        processTemplateService.publish(id);
        return Result.ok();
    }

    @ApiOperation("获取分页列表")
    @GetMapping("{current}/{size}")
    public Result<?> index(
            @PathVariable Long current,
            @PathVariable Long size
    ) {
        // 创建page对象
        Page<ProcessTemplate> pageParam = new Page<>(current, size);
        // 分页查询审批模板，审批类型对应名称查询
        IPage<ProcessTemplate> pageModel = processTemplateService.selectPageProcessTemplate(pageParam);
        return Result.ok(pageModel);
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.list')")
    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        ProcessTemplate processTemplate = processTemplateService.getById(id);
        return Result.ok(processTemplate);
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody ProcessTemplate processTemplate) {
        processTemplateService.save(processTemplate);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result updateById(@RequestBody ProcessTemplate processTemplate) {
        processTemplateService.updateById(processTemplate);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.remove')")
    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        processTemplateService.removeById(id);
        return Result.ok();
    }
}

