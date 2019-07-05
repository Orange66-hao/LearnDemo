package net.boocu.web.controller.admin;

import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.framework.util.SettingUtils;
import net.boocu.project.entity.MailSignatureEntity;
import net.boocu.project.entity.McMemberEntity;
import net.boocu.project.entity.McProductClassEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductSubscribeEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.McMemberService;
import net.boocu.project.service.ProductService;
import net.boocu.project.util.MessageUtil;
import net.boocu.web.Filter;
import net.boocu.web.Message;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.entity.MemberGradeEntity;
import net.boocu.web.entity.MemberEntity.UserTypeEnum;
import net.boocu.web.service.MemberService;
import net.boocu.web.service.RSAService;
import net.boocu.web.setting.security.SecuritySetting;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.support.json.JSONUtils;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

/**
 * 
 * 
 * 
 * @author fang
 *
 *         2015年8月27日
 */
@Controller("mcMemberController")
@RequestMapping("/admin/mcMember")
public class McMemberController {

	private static final String TEM_PATH = "/template/admin/mcMember";

	@Resource
	private McMemberService mcMemberService;

	@Resource
	private MemberService memberService;

	@Resource
	private ProductService productService;

	@Resource
	private JdbcTemplate JdbcTemplate;

	@Resource(name = "rsaServiceImpl")
	private RSAService rsaService;

	@RequestMapping(value = "toList.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String toList(HttpServletRequest request, HttpServletResponse response, Model model) {
		// 密钥
		RSAPublicKey publicKey = rsaService.generateKey(request);

		model.addAttribute("captchaId", UUID.randomUUID().toString());
		model.addAttribute("modulus", Base64.encodeBase64String(publicKey.getModulus().toByteArray()));
		model.addAttribute("exponent", Base64.encodeBase64String(publicKey.getPublicExponent().toByteArray()));
		return TEM_PATH + "/mcMemberlist";
	}

	// 跳转到实体添加页面
	@RequestMapping(value = "/add_mcMember.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String add_McMember(HttpServletRequest request, HttpServletResponse response, Model model) {
		// 密钥
		RSAPublicKey publicKey = rsaService.generateKey(request);

		model.addAttribute("captchaId", UUID.randomUUID().toString());
		model.addAttribute("modulus", Base64.encodeBase64String(publicKey.getModulus().toByteArray()));
		model.addAttribute("exponent", Base64.encodeBase64String(publicKey.getPublicExponent().toByteArray()));
		return TEM_PATH + "/mcMember_add";

	}

	// 获取树形行业网络图
	@ResponseBody
	@RequestMapping(value = "getData.json", method = { RequestMethod.POST, RequestMethod.GET })
	public void getDataJson(HttpServletRequest request, HttpServletResponse response, Model model) {
		String keyword = ReqUtil.getString(request, "keyword", "");
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		String sortValue = ReqUtil.getString(request, "sort", "");
		String sortOrder = ReqUtil.getString(request, "order", "");

		Pageable pageable = new Pageable(pagenumber, rows);
		if (!sortValue.isEmpty()) {
			List<Sequencer> ilSequencers = new ArrayList<Sequencer>();
			Sequencer sequencer = "desc".equals(sortOrder) ? Sequencer.desc(sortValue) : Sequencer.asc(sortValue);
			ilSequencers.add(sequencer);
			pageable.setSequencers(ilSequencers);
		}

		if (!keyword.isEmpty()) {
			List<Filter> flist = new ArrayList<Filter>();
			flist.add(Filter.like("name", "%" + keyword + "%"));
			pageable.getFilters().add(Filter.or(flist));
		}
		/*
		 * pageable.getFilters().add(Filter.eq("apprStatus", 1));
		 * pageable.getFilters().add(Filter.eq("isDel", 0));
		 */
		Page<McMemberEntity> page = mcMemberService.findPage(pageable);
		List<McMemberEntity> resultList = page.getCont();

		List<Map> resultData = new ArrayList<Map>();
		for (McMemberEntity item : resultList) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("id", item.getId());
			data.put("ids", item.getId());
			data.put("name", item.getName());
			data.put("sort", item.getSort());
			data.put("username", item.getUsername());
			data.put("status", item.getStatus());
			data.put("create_date", item.getCreateDate());
			data.put("content", item.getContent());
			data.put("phone", item.getPhone());
			data.put("usertype", item.getUsertype());
			resultData.add(data);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", page.getTotal());
		result.put("rows", resultData);
		RespUtil.renderJson(response, result);
	}

	// 获取下拉树形结构信息
	@ResponseBody
	@RequestMapping(value = "combotreeData.json", method = { RequestMethod.POST, RequestMethod.GET })
	public List<Map> getComboTreeDataJson(HttpServletRequest request, HttpServletResponse response, Model model) {
		McMemberEntity topNode = mcMemberService.find(1l);
		return getNodeData(topNode);
	}

	// 递归查找树形结构信息
	public List<Map> getNodeData(McMemberEntity topNode) {
		List<Map> resultList = new ArrayList<Map>();
		List<Filter> flist = new ArrayList<Filter>();
		flist.add(Filter.eq("parentid", topNode.getId()));
		List<McMemberEntity> items = mcMemberService.findList(flist, Sequencer.asc("sort"));
		for (McMemberEntity item : items) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			List<McMemberEntity> children = mcMemberService.findList(Filter.eq("parentid", item.getId()));
			if (children.size() != 0) {
				map.put("children", getNodeData(item));
			}
			resultList.add(map);
		}
		return resultList;
	}

	// 获取异步下拉树形结构信息
	@ResponseBody
	@RequestMapping(value = "combotree.json", method = { RequestMethod.POST, RequestMethod.GET })
	public List<Map> getComboTree(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 1l);
		McMemberEntity mcMemberEntity = mcMemberService.find(id);
		List<Map> resultList = new ArrayList<Map>();
		if (mcMemberEntity != null) {
			List<Filter> flist = new ArrayList<Filter>();
			flist.add(Filter.eq("parentid", mcMemberEntity.getId()));
			List<McMemberEntity> items = mcMemberService.findList(flist, Sequencer.asc("sort"));
			for (McMemberEntity item : items) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", item.getId());
				map.put("text", item.getName());
				map.put("state", "0".equals(item.getLeaf()) ? "closed" : "open");
				resultList.add(map);
			}
		}
		return resultList;
	}

	/**
	 * 
	 * 方法:将新添加的McMemberEntity（本身实体 ）保存到数据库中 传入参数:McMemberEntity的字段
	 * 传出参数:result（方法结果信息） 逻辑：接收前台McMemberEntity的字段，对非空字段校验，新建实体赋值保存
	 */
	@ResponseBody
	@RequestMapping(value = "/doAdd_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> saveMcMember(HttpServletRequest request, HttpServletResponse response, Model model,
			McMemberEntity mcMemberEntity) {
		/*
		 * MemberEntity memberEntity = memberService.getCurrent();
		 * if(memberEntity != null){
		 * mcMemberEntity.setCreateuser(memberEntity.getId().toString()); }
		 * if(mcMemberEntity.getParentid().isEmpty()){
		 * mcMemberEntity.setParentid("1"); }
		 */
		mcMemberEntity.setPassword(DigestUtils.md5Hex(mcMemberEntity.getPassword()));
		// 删除RSA私钥
		rsaService.removePrivateKey(request);
		mcMemberService.save(mcMemberEntity);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "保存成功");
		return result;
	}

	/**
	 * 
	 * 方法:删除选中McMemberEntity（本身实体 ）并更新数据库 传入参数:id（选中实体的id数组） 传出参数:result（方法结果信息）
	 */
	@RequestMapping(value = "/delete_mcMember.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void deleteMcMember(HttpServletRequest request, HttpServletResponse response, Model model, Long id) {
		String[] idStrings = request.getParameterValues("id");
		Long[] ids = new Long[idStrings.length];

		for (int i = 0; i < ids.length; i++) {
			ids[i] = Long.parseLong(idStrings[i]);
		}

		if (ids != null && ids.length > 0) {
			for (Long id1 : ids) {
				mcMemberService.delete(id1);
			}
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("result", 1);
			result.put("message", "删除成功");
			RespUtil.renderJson(response, result);
		}
	}

	// 跳转到实体更新页面
	@RequestMapping(value = "/edit_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String editMcMember(HttpServletRequest request, HttpServletResponse response, Model model) {
		String id = ReqUtil.getString(request, "id", "");
		// 密钥
		RSAPublicKey publicKey = rsaService.generateKey(request);

		model.addAttribute("captchaId", UUID.randomUUID().toString());
		model.addAttribute("modulus", Base64.encodeBase64String(publicKey.getModulus().toByteArray()));
		model.addAttribute("exponent", Base64.encodeBase64String(publicKey.getPublicExponent().toByteArray()));
		McMemberEntity mcMemberEntity = new McMemberEntity();

		if (!id.equals("")) {
			Long lid = Long.parseLong(id);
			mcMemberEntity = this.mcMemberService.find(lid);
			model.addAttribute("item", mcMemberEntity);
		}
		return TEM_PATH + "/mcMember_edit";

	}

	/**
	 * 
	 * 方法:保存更新之后的McMemberEntity（本身实体 ） 传入参数:McMemberEntity的字段 id（更新实体的id）
	 * 传出参数:result（方法结果信息） 逻辑：查询该id的实体，存在则读取前台McMemberEntity（村本身实体
	 * ）的字段并执行更新操作；否则提示更新错误
	 */
	@ResponseBody
	@RequestMapping(value = "/doEdit_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> saveEditMcMember(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		String name = ReqUtil.getString(request, "name", "");
		String content = ReqUtil.getString(request, "content", "");
		String username = ReqUtil.getString(request, "username", "");
		String phone = ReqUtil.getString(request, "phone", "");
		String sort = ReqUtil.getString(request, "sort", "");
		String usertype = ReqUtil.getString(request, "usertype", "");
		String password = ReqUtil.getString(request, "password", "");
		McMemberEntity OldMcMemberEntity = mcMemberService.find(id);
		// OldMcMemberEntity.setParentid(parentid);
		OldMcMemberEntity.setContent(content);
		OldMcMemberEntity.setUsername(username);
		if (!password.equals(OldMcMemberEntity.getPassword())) {
			OldMcMemberEntity.setPassword(DigestUtils.md5Hex(password));
		}
		OldMcMemberEntity.setName(name);
		OldMcMemberEntity.setSort(Integer.parseInt(sort));
		OldMcMemberEntity.setPhone(phone);
		OldMcMemberEntity.setUsertype(usertype);
		mcMemberService.update(OldMcMemberEntity);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "保存成功");
		return result;
	}

	/** 取得用户等级的集合 */
	@ResponseBody
	@RequestMapping(value = "/get_mcmember_names.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void getMcMemberGradeNames(HttpServletRequest reqeust, HttpServletResponse response, Model model,
			McMemberEntity memberGradeEntity) {
		// List<McMemberEntity> memberGradeEntities = mcMemberService.findAll();
		List<Filter> flist = new ArrayList<Filter>();
		List<McMemberEntity> memberGradeEntities = mcMemberService.findList(flist, Sequencer.asc("sort"));
		List<Map> resultList = new ArrayList<Map>();
		for (McMemberEntity item : memberGradeEntities) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			resultList.add(map);
		}
		RespUtil.renderJson(response, JSONUtils.toJSONString(resultList));
	}

	// 查询用户名是否存在
	@ResponseBody
	@RequestMapping(value = "/register.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Message register(String edit_username, String username, HttpServletRequest request, HttpSession session) {
		if (edit_username != null && edit_username != "" && edit_username.equals(username)) {
			return Message.success("true");
		} else {
			List<Map<String, Object>> row = mcMemberService.register(edit_username, username, request, session);
			if (row.size() > 0) {
				return Message.success("false");
			}
			return Message.success("true");
		}
	}

	// 根据常用联系人的id组，查询对应name（名称）
	public Map getNodeData2(String rootId) {
		List<Map<String, Object>> topNode = mcMemberService.getNodeData2(rootId);
		if (topNode == null) {
			return null;
		}
		String id[] = new String[topNode.size()];
		String name[] = new String[topNode.size()];
		int i = 0;
		for (Map<String, Object> map : topNode) {
			id[i] = (map.get("id")).toString();
			name[i] = (String) map.get("name");
			i++;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("name", name);
		return map;
	}

	// 根据常用联系人的id组，查询对应name（名称）
	public List<Map<String, Object>> quaryblame(String blame_id) {
		return mcMemberService.quaryblame(blame_id);
	}

}
