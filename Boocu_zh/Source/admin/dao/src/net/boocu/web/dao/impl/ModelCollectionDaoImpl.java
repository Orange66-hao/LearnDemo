package net.boocu.web.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.framework.util.ReqUtil;
import net.boocu.project.entity.MailSignatureEntity;
import net.boocu.project.entity.McBrandAndModelEntity;
import net.boocu.project.entity.ModelCollectionEntity;
import net.boocu.web.Message;
import net.boocu.web.dao.MailSignatureDao;
import net.boocu.web.dao.MemberGradeDao;
import net.boocu.web.dao.ModelCollectionDao;
import net.boocu.web.entity.AdminEntity;
import net.boocu.web.entity.MemberGradeEntity;
import net.boocu.web.service.AdminService;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;

/**
 * 使用方法:如UserEntity 1.将 module replaceAll 成 user (注意大小写敏感,将Case sensitive勾选) 2.将
 * Module replaceAll 成 User (注意大小写敏感,将Case sensitive勾选)
 */
@Repository("modelCollectionDaoImpl")
public class ModelCollectionDaoImpl extends BaseDaoImpl<ModelCollectionEntity, Long> implements ModelCollectionDao {

	@Resource
	private JdbcTemplate JdbcTemplate;

	@Resource(name = "adminServiceImpl")
	AdminService adminService;

	@Override
	public List<Map<String, Object>> register(String edit_name, String name, HttpServletRequest request,
			HttpSession session) {
		List<Map<String, Object>> row = JdbcTemplate.queryForList("select * from mc_company where name='" + name + "'");
		return row;
	}

	@Override
	public List<Map<String, Object>> querycompany(HttpServletRequest request, HttpServletResponse response,
			Model model) {
		String keyword = ReqUtil.getString(request, "keyword", "");
		String mc_industry_class = ReqUtil.getString(request, "mc_industry_class", "");//获取搜索框内的内容
		String major_product = ReqUtil.getString(request, "major_product", "");
		String research_direction = ReqUtil.getString(request, "research_direction", "");
		String mc_productclass = ReqUtil.getString(request, "mc_productclass", "");
		String mc_brand = ReqUtil.getString(request, "mc_brand", "");
		String mc_model = ReqUtil.getString(request, "mc_model", "");
		String mode_blame = ReqUtil.getString(request, "mode_blame", "");
		String search_contact = ReqUtil.getString(request, "search_contact", "");//搜索联系人
		//String search_mc_brand = ReqUtil.getString(request, "search_mc_brand", "");
		String search_major = ReqUtil.getString(request, "search_major", "");//搜索专业
		String search_teacher = ReqUtil.getString(request, "search_teacher", "");//搜索老师名称
		String search_introducer = ReqUtil.getString(request, "search_introducer", "");//搜索介绍人
		String create_date = ReqUtil.getString(request, "create_date", "");
		String grade = ReqUtil.getString(request, "grade", "");
		String measurement_time = ReqUtil.getString(request, "measurement_time", "");
		String subscribe_status=ReqUtil.getString(request, "subscribe_status", "");
		int pagenumber = ReqUtil.getInt(request, "page", 1);
		int rows = ReqUtil.getInt(request, "rows", 10);
		int number = (pagenumber - 1) * rows;
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		String sortValue = ReqUtil.getString(request, "sort", "");
		String sortOrder = ReqUtil.getString(request, "order", "desc");
		AdminEntity admin = adminService.getCurrent();
		String sql="";
        String type = request.getParameter("type");//在公司管理和高校管理模块的跳转路径上带了一个 type的参数
		String searchNumber = "";
		//判断是首次打开还是查询操作
		if (!mc_productclass.isEmpty()||!mc_brand.isEmpty()||!mc_model.isEmpty()) {
			searchNumber = ",mc_brand_and_model m";
		}
		// 当进行条件过滤查询的时候的sql语句
        if (type != null && type.equals("university")) {//高校选择联系人表的更新时间
			sql = " select distinct c.id,c.name,c.laboratory,c.mc_industry_class,c.major_product,c.research_direction,c.contact,c.mc_brand_and_model,c.blame,c.introducer,c.remark,c.grade,c.measurement_time,c.rate,c.modify_date,c.subscribe_content," +
					"s.major,s.teacher from mc_company c,mc_company_name n,mc_contacts s"+searchNumber+" where n.id=c.name and s.id=c.contact ";
        } else {//非高校
			sql = " select distinct c.id,c.name,c.mc_industry_class,c.major_product,c.contact,c.mc_brand_and_model,c.blame,c.remark,c.website,c.grade,c.measurement_time,c.rate,c.modify_date,c.subscribe_content" +
					" from mc_company c,mc_company_name n,mc_contacts s"+searchNumber+" where n.id=c.name and s.id=c.contact ";
        }


		if (!keyword.isEmpty()) {
			sql = sql + " and n.name like '%" + keyword + "%'";
		}
		if (!mode_blame.isEmpty()) {
			sql = sql + " and c.blame in (" + mode_blame + ")";
		}

		if (!(major_product.isEmpty())) {
			String[] mplist = major_product.replaceAll("，", ",").split(",");
			sql = sql + " and c.major_product like '%" + mplist[0] + "%' ";
			for(int i=1;i<mplist.length;i++) {
				sql = sql + " or c.major_product like '%" + mplist[i]+"%'";
			}
			
		}
		if (!research_direction.isEmpty()) {
			sql = sql + " and c.research_direction like '%" + research_direction + "%'";
		}

		if (!mc_productclass.isEmpty()) {
			sql = sql +  " and (c.mc_brand_and_model LIKE m.id " +
					"OR c.mc_brand_and_model LIKE CONCAT(m.id, ',%') " +
					"OR c.mc_brand_and_model LIKE CONCAT('%,', m.id, ',%') " +
					"OR c.mc_brand_and_model LIKE CONCAT('%,', m.id)) and m.company_type=1 and m.mc_productclass_name like '%" + mc_productclass +"%'";
		}
		if (!mc_brand.isEmpty()) {
			sql = sql +  " and (c.mc_brand_and_model LIKE m.id " +
					"OR c.mc_brand_and_model LIKE CONCAT(m.id, ',%') " +
					"OR c.mc_brand_and_model LIKE CONCAT('%,', m.id, ',%') " +
					"OR c.mc_brand_and_model LIKE CONCAT('%,', m.id)) and m.company_type=1 and m.mc_brand like '%" + mc_brand +"%'";
		}
		if (!mc_model.isEmpty()) {
			sql = sql +  " and (c.mc_brand_and_model LIKE m.id " +
					"OR c.mc_brand_and_model LIKE CONCAT(m.id, ',%') " +
					"OR c.mc_brand_and_model LIKE CONCAT('%,', m.id, ',%') " +
					"OR c.mc_brand_and_model LIKE CONCAT('%,', m.id)) and m.company_type=1 and m.mc_model like '%" + mc_model +"%'";
		}
		if (!grade.isEmpty()) {
			sql = sql + " and c.grade=" + grade;
		}
		if (!measurement_time.isEmpty()) {
			sql = sql + " and c.measurement_time=" + measurement_time;
		}


		if (type != null && type.equals("company")) {//如果是公司管理模块时,不展示高校的信息
			sql = sql + "and c.mc_industry_class != '学校'";
			if (!mc_industry_class.isEmpty()) {
				sql = sql + " and c.mc_industry_class like '%" + mc_industry_class + "%'";
			}
		} else if ((type != null) && type.equals("university")){//如果是高校管理模块时仅展示高校的信息
			sql = sql + "and c.mc_industry_class = '学校'";
		}

		if ((admin.getId() == 1 )||admin.getId() == 75) {// 根据登录用户id，对其能看到的数据进行过滤
			
		}else {
			sql = sql + " and c.blame=" + admin.getId();
		}
		if (!search_contact.isEmpty()) {
			sql = sql + " and c.contact like '%" + search_contact + "%'";//联系人
		}
		if (!search_major.isEmpty()) {
			sql = sql + " and s.major like '%" + search_major + "%'";//专业
		}
		if (!search_teacher.isEmpty()) {
			sql = sql + " and s.teacher like '%" + search_teacher + "%'";//老师名称
		}
		/*if (!search_mc_brand.isEmpty()) {
			sql = sql + " and c.mc_brand like '%" + search_mc_brand + "%'";//常用仪器的品牌和型号
		}*/
		if (!search_introducer.isEmpty()) {
			sql = sql + " and c.introducer like '%" + search_introducer + "%'";//介绍人
		}
		if (create_date.equals("3")) {// 根据创建时间，对其能看到的数据进行过滤
			sql = sql + " and c.create_date > date_format(date_sub(now(), INTERVAL 2 day),'%Y-%m-%d 00:00:00')";// 三天内数据
		}
		if (create_date.equals("7")) {// 根据创建时间，对其能看到的数据进行过滤
			sql = sql + " and YEARWEEK(date_format(c.create_date,'%Y-%m-%d')) = YEARWEEK(now())";// 本周内数据
		}
		if (create_date.equals("30")) {// 根据创建时间，对其能看到的数据进行过滤
			sql = sql + " and date_format(c.create_date,'%Y-%m')=date_format(now(),'%Y-%m')";// 本月内数据
		}
		if (type != null && type.equals("university")) {//高校选择联系人表的更新时间
            sql = sql + " order by c." + sortValue + " " + sortOrder + " limit " + number + "," + rows;
        } else {
		    sql = sql + " order by c." + sortValue + " " + sortOrder + " limit " + number + "," + rows;
        }
		List<Map<String, Object>> topNode = JdbcTemplate.queryForList(sql);// 此查询获得数据
		return topNode;
	}
	@Override
	public List<Map<String, Object>> querycompanyrows(HttpServletRequest request, HttpServletResponse response,
			Model model) {
		String keyword = ReqUtil.getString(request, "keyword", "");
		String mc_industry_class = ReqUtil.getString(request, "mc_industry_class", "");
		String major_product = ReqUtil.getString(request, "major_product", "");
		String mc_productclass = ReqUtil.getString(request, "mc_productclass", "");
		String mode_blame = ReqUtil.getString(request, "mode_blame", "");
		String create_date = ReqUtil.getString(request, "create_date", "");
		String grade = ReqUtil.getString(request, "grade", "");
		String measurement_time = ReqUtil.getString(request, "measurement_time", "");
		String subscribe_status=ReqUtil.getString(request, "subscribe_status", "");
		int pagenumber = ReqUtil.getInt(request, "page", 1);
		int rows = ReqUtil.getInt(request, "rows", 10);
		int number = (pagenumber - 1) * rows;

		String sortValue = ReqUtil.getString(request, "sort", "");
		String sortOrder = ReqUtil.getString(request, "order", "desc");
		AdminEntity admin = adminService.getCurrent();
		String sql="";
		if(StringUtils.isNotBlank(subscribe_status)) {
			 sql = " select distinct c.* from mc_company c,mc_company_name n where n.id=c.name and c.subscribe_status="+subscribe_status+" ";
		}else {
			 sql = " select distinct c.* from mc_company c,mc_company_name n where n.id=c.name ";
		}
		// 当进行条件过滤查询的时候的sql语句

		if (!keyword.isEmpty()) {
			sql = sql + " and n.name like '%" + keyword + "%'";
		}
		if (!mode_blame.isEmpty()) {
			sql = sql + " and c.blame in (" + mode_blame + ")";
		}

		if (!major_product.isEmpty()) {
			String[] mplist = major_product.replaceAll("，", ",").split(",");
			sql = sql + " and c.major_product like '%" + major_product+ "%' ";
		}

		String type = request.getParameter("type");//在公司管理和高校管理模块的跳转路径上带了一个 type的参数
		if (type != null && type.equals("company")) {//如果是公司管理模块时,不展示高校的信息
			sql = sql + "and c.mc_industry_class != '学校'";
		} else if (type != null && type.equals("university")){//如果是高校管理模块时仅展示高校的信息
			sql = sql + "and c.mc_industry_class = '学校'";
		}else if (!mc_industry_class.isEmpty()) {
			sql = sql + " and c.mc_industry_class like '%" + mc_industry_class + "%'";
		}

		if (!grade.isEmpty()) {
			sql = sql + " and c.grade=" + grade;
		}
		if (!measurement_time.isEmpty()) {
			sql = sql + " and c.measurement_time=" + measurement_time;
		}
		if (!mc_industry_class.isEmpty()) {
			// sql=sql + " and c.ids in(select p.mc_company from
			// mc_change_industry p where p.mc_industry in(" +mc_industry_class+
			// "))";
			sql = sql + " and c.mc_industry_class like '%" + mc_industry_class + "%'";
		}
		if ((admin.getId() == 1 )||(admin.getId() == 75 )) {// 根据登录用户id，对其能看到的数据进行过滤

		}else {
			sql = sql + " and c.blame=" + admin.getId();
		}
		if (create_date.equals("3")) {// 根据创建时间，对其能看到的数据进行过滤
			sql = sql + " and c.create_date > date_format(date_sub(now(), INTERVAL 2 day),'%Y-%m-%d 00:00:00')";// 三天内数据
		}
		if (create_date.equals("7")) {// 根据创建时间，对其能看到的数据进行过滤
			sql = sql + " and YEARWEEK(date_format(c.create_date,'%Y-%m-%d')) = YEARWEEK(now())";// 本周内数据
		}
		if (create_date.equals("30")) {// 根据创建时间，对其能看到的数据进行过滤
			sql = sql + " and date_format(c.create_date,'%Y-%m')=date_format(now(),'%Y-%m')";// 本月内数据
		}
		System.out.println(sql);;
		List<Map<String, Object>> topNoderows = JdbcTemplate.queryForList(sql);// 此查询获得条数
		return topNoderows;
	}
}
