/**
 * 
 */
package net.boocu.web.controller.admin.sysconfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import net.boocu.framework.util.ReqUtil;
import net.boocu.web.entity.SysConfigEntity;
import net.boocu.web.service.SysConfigService;

/**
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/sysConfig")
public class SysConfigController {
	
	private static final String TEM_PATH = "/template/admin/sysconfig";
	
	@Autowired
	private SysConfigService sysConfigService;
	
	@RequestMapping("/toSysConfig.jspx")
	public String toSysConfig(HttpServletRequest request,
			HttpServletResponse response,Model model)
	{
		model.addAttribute("sysCofnigs", sysConfigService.getSysConfigLists());
		return TEM_PATH + "/sysconfig";
	}
	
	@RequestMapping("/updateSysConfig.jspx")
	public void updateSysConfig(HttpServletRequest request,
		HttpServletResponse response)
	{
		String msg = "err";
		try {
			String id = ReqUtil.getString(request, "id", "0");
			String val = ReqUtil.getString(request, "value", "");
			SysConfigEntity sc = new SysConfigEntity();
			sc.setId(Long.parseLong(id));
			sc.setValue(val);
			if(sysConfigService.updateConfig(sc) > 0){
				msg = "ok"	;
			}
		response.getWriter().write(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/initSysConfig.jspx")
	public void initSysConfig(HttpServletRequest request,
			HttpServletResponse response)
	{
		try {
			Thread.currentThread().sleep(5000);
			sysConfigService.updateInitConfig();
			response.getWriter().write("ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
