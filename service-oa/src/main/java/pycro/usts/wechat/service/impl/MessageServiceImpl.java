package pycro.usts.wechat.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pycro.usts.auth.service.SysUserService;
import pycro.usts.model.process.Process;
import pycro.usts.model.process.ProcessTemplate;
import pycro.usts.model.system.SysUser;
import pycro.usts.process.service.OaProcessService;
import pycro.usts.process.service.OaProcessTemplateService;
import pycro.usts.security.custom.LoginUserInfoHelper;
import pycro.usts.wechat.service.MessageService;

import java.util.Map;

/**
 * @author Pycro
 * @version 1.0
 * 2023-05-29 5:14 PM
 */
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private OaProcessService processService;

    @Autowired
    private OaProcessTemplateService processTemplateService;

    @Autowired
    private SysUserService sysUserService;

    @SneakyThrows
    @Override
    public void pushPendingMessage(Long processId, Long userId, String taskId) {
        // 查询流程信息
        Process process = processService.getById(processId);
        // 查询模板信息
        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());
        // 查询推送人的信息
        SysUser sysUser = sysUserService.getById(userId);
        // 获取提交人的信息
        SysUser submitSysUser = sysUserService.getById(process.getUserId());
        String openid = sysUser.getOpenId();
        // 方便测试，给默认值（开发者本人的openId）
        if (StringUtils.isEmpty(openid)) {
            openid = "omwf25izKON9dktgoy0dogqvnGhk";
        }
        // 设置消息发送信息
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(openid)// 要推送的用户openid
                .templateId("vwUIf3JnKIsf3wuQBDB_Bbx6ugW7k0xL_vaE_ccgmxA")// 创建的推送模板的id
                .url("http://ggkt1.vipgz1.91tunnel.com/#/show/" + processId + "/" + taskId)// 点击模板消息要访问的网址
                .build();
        JSONObject jsonObject = JSON.parseObject(process.getFormValues());
        JSONObject formShowData = jsonObject.getJSONObject("formShowData");
        StringBuilder content = new StringBuilder();
        for (Map.Entry<String, Object> entry : formShowData.entrySet()) {
            content.append(entry.getKey()).append("：").append(entry.getValue()).append("\n ");
        }
        // 设置模板里的参数值
        templateMessage.addData(new WxMpTemplateData("first", submitSysUser.getName() + "提交了" + processTemplate.getName() + "审批申请，请注意查看。", "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword1", process.getProcessCode(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword2", new DateTime(process.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"), "#272727"));
        templateMessage.addData(new WxMpTemplateData("content", content.toString(), "#272727"));
        String msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        log.info("推送消息返回：{}", msg);
    }

    @SneakyThrows
    @Override
    public void pushProcessedMessage(Long processId, Long userId, Integer status) {
        Process process = processService.getById(processId);
        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());
        SysUser sysUser = sysUserService.getById(userId);
        SysUser currentSysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());
        String openid = sysUser.getOpenId();
        if (StringUtils.isEmpty(openid)) {
            openid = "omwf25izKON9dktgoy0dogqvnGhk";
        }
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(openid)// 要推送的用户openid
                .templateId("I0kVeto7T0WIDP6tyoHh-hx83wa9_pe7Nx9eT93-6sc")// 模板id
                .url("http://oa.atguigu.cn/#/show/" + processId + "/0")// 点击模板消息要访问的网址
                .build();
        JSONObject jsonObject = JSON.parseObject(process.getFormValues());
        JSONObject formShowData = jsonObject.getJSONObject("formShowData");
        StringBuffer content = new StringBuffer();
        for (Map.Entry<String, Object> entry : formShowData.entrySet()) {
            content.append(entry.getKey()).append("：").append(entry.getValue()).append("\n ");
        }
        templateMessage.addData(new WxMpTemplateData("first", "你发起的" + processTemplate.getName() + "审批申请已经被处理了，请注意查看。", "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword1", process.getProcessCode(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword2", new DateTime(process.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword3", currentSysUser.getName(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword4", status == 1 ? "审批通过" : "审批拒绝", status == 1 ? "#009966" : "#FF0033"));
        templateMessage.addData(new WxMpTemplateData("content", content.toString(), "#272727"));
        String msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        log.info("推送消息返回：{}", msg);
    }

}