package net.boocu.web.controller.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.druid.support.json.JSONUtils;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.EmailRecordEntity;
import net.boocu.project.service.EmailRecordService;
import net.boocu.web.Filter;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.enums.TemplateTypeEnum;
import net.boocu.web.service.EMailService;
import net.boocu.web.service.MemberGradeService;
import net.boocu.web.service.MemberService;
import net.boocu.web.service.TemplateService;

@Controller("templateController")
@RequestMapping(value = "/admin/template")
public class TemplateController {
	private static final String TEM_PATH = "/template/admin/subscriptionMng/mailTemplateMng";

	@Resource
	EMailService emailService;
	@Resource
	TemplateService templateService;
	@Resource
	EmailRecordService emaiRecordService;
	@Resource
	MemberService memberService;

	@Resource
	MemberGradeService memberGradeService;

	@RequestMapping(value = "/toTemplateList.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String toList(HttpServletRequest request, HttpServletResponse response, Model model) {
		return TEM_PATH + "/templateMngList";
	}

	@RequestMapping(value = "/sendEmail.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void sendEmail(HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();
		String templat = ReqUtil.getString(request, "template", "");
		Long gradeId = ReqUtil.getLong(request, "gradeid", 0l);
		String subject = ReqUtil.getString(request, "subject", "");
		String context = ReqUtil.getString(request, "context", "");
		String mail = ReqUtil.getString(request, "mail", "");
		String singleOrMultiple = ReqUtil.getString(request, "singleOrMultiple", "");
        String message = "发送成功";
		if(singleOrMultiple.equals("1")){//发送单个
            try {
                Map map=new HashMap<>();
                map.put("siteName", "Haochen Test Equipment");
                map.put("content", context);
                net.boocu.web.Template template = templateService.get(templat);
                emailService.send(mail, subject, template.getTemplatePath(),map);
            } catch (Exception e) {
                message = "有邮件未发送成功,请注意!";
            }
            result.put("result", 1);
            result.put("message", message);
        }else{//用户组
            if(gradeId!=0){
                // 得到用户组所有成员
                List<MemberEntity> memberList = memberService
                        .findList(Filter.eq("memberGradeEntity", memberGradeService.find(gradeId)));
                for (MemberEntity entity : memberList) {
                    EmailRecordEntity log = new EmailRecordEntity();// 存储记录
                    net.boocu.web.Template template = templateService.get(templat);
                    templateService.write(template, context);
                    try {
                        Map map=new HashMap<>();
                        if(gradeId==1000342502L) {
                            map.put("siteName", "Haochen Test Equipment");
                        }else {
                            map.put("siteName", "晧辰仪联网");
                        }
                        emailService.send(entity.getEmail(), subject, template.getTemplatePath(),map);
                        //emailService.send("itzhangpf@163.com", subject, template.getTemplatePath());
                        log.setResult(true);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        log.setDetails(e.getMessage());
                        log.setResult(false);
                        message = "有邮件未发送成功,请注意!";
                    }
                    log.setCreateDate(new Date());
                    log.setUserName(entity.getUsername());
                    emaiRecordService.save(log);
                }
            }
        }
		RespUtil.renderJson(response, result);
	}

    public static void main(String[] args) {
        Boolean a=true;
        System.out.println(a);
    }

	/** 获取模版信息 */
	@RequestMapping(value = "/getTemplate.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void getTemplate(HttpServletRequest request, HttpServletResponse response, Model model) {
		List<net.boocu.web.Template> items = templateService.getList(TemplateTypeEnum.adminMail);
		List<Map> resultList = new ArrayList<Map>();
		for (net.boocu.web.Template item : items) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			resultList.add(map);
		}
		RespUtil.renderJson(response, JSONUtils.toJSONString(resultList));
	}

	/** 获取选中模版内容 */
	@RequestMapping(value = "/getTemplateContext.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void getTemplateContext(HttpServletRequest request, HttpServletResponse response, Model model) {
		String mail = ReqUtil.getString(request, "mailId", "");
		net.boocu.web.Template item = templateService.get(mail);
		String html = templateService.read(item);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", html);
		RespUtil.renderJson(response, result);
	}

}
