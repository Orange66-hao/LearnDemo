package net.boocu.web.dao.impl;

import com.aliyun.common.comm.ServiceClient;
import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.framework.util.ReqUtil;
import net.boocu.project.entity.MailSignatureEntity;
import net.boocu.project.entity.ModelCollectionEntity;
import net.boocu.project.entity.SuModelCollectionEntity;
import net.boocu.web.dao.MailSignatureDao;
import net.boocu.web.dao.MemberGradeDao;
import net.boocu.web.dao.ModelCollectionDao;
import net.boocu.web.dao.SuModelCollectionDao;
import net.boocu.web.entity.AdminEntity;
import net.boocu.web.entity.MemberGradeEntity;
import net.boocu.web.service.AdminService;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;

/**
 * 使用方法:如UserEntity
 * 1.将 module replaceAll 成 user (注意大小写敏感,将Case sensitive勾选)
 * 2.将 Module replaceAll 成 User (注意大小写敏感,将Case sensitive勾选)
 * */
@Repository("suModelCollectionDaoImpl")
public class SuModelCollectionDaoImpl extends BaseDaoImpl<SuModelCollectionEntity, Long> implements SuModelCollectionDao {
	@Resource
	private JdbcTemplate JdbcTemplate;
    @Resource(name = "adminServiceImpl")
	AdminService adminService;
    
	@Override
	public List<Map<String, Object>> querysucompanyrows(HttpServletRequest request, HttpServletResponse response,
			Model model) {
		String keyword = ReqUtil.getString(request, "keyword", "");
		String su_industry_class = ReqUtil.getString(request, "su_industry_class", "");
		String major_product = ReqUtil.getString(request, "major_product", "");//主营产品名称
		String major_product_type = ReqUtil.getString(request, "major_product_type", "");
		String su_productclass = ReqUtil.getString(request, "su_productclass", "");
		String mode_blame = ReqUtil.getString(request, "mode_blame", "");
		String create_date = ReqUtil.getString(request, "create_date", "");
		
		int pagenumber = ReqUtil.getInt(request, "page", 1);
		int rows = ReqUtil.getInt(request, "rows", 10);
		int number=(pagenumber-1)*rows;
		
		String sortValue = ReqUtil.getString(request, "sort", "");
		String sortOrder = ReqUtil.getString(request, "order", "desc");
		
		
		AdminEntity admin=adminService.getCurrent();
    	
		//当进行条件过滤查询的时候的sql语句
		String sql=" select c.* from su_company c,su_company_name n where c.name=n.id ";
    	if(!keyword.isEmpty()){
    		sql=sql + " and n.name like '%" +keyword+ "%'";
    	}
    	if(!mode_blame.isEmpty()){
    		sql=sql + " and c.blame in (" +mode_blame+ ")";
    	}
    	/*if(!su_industry_class.isEmpty()){
    		sql=sql + " and c.su_industry_class in(" +su_industry_class+ ")";
    	}*/
    	/*if(!major_product.isEmpty()){
    		sql=sql + " and c.ids in(select m.su_company from su_change_major m where m.su_major in(" +major_product+ "))";
    	}
    	if(!su_productclass.isEmpty()){
    		sql=sql + " and c.ids in(select p.su_company from su_change_productclass p where p.su_productclass in(" +su_productclass+ "))";
    	}*/
    	if (!major_product.isEmpty()) {
    	      sql = sql + " and c.major_product like '%" + major_product + "%'";
    	    }
    	if (!su_productclass.isEmpty()) {
    	      sql = sql + " and c.su_productclass like '%" + su_productclass + "%'";
    	    }
    	if(!su_industry_class.isEmpty()){
    		//sql=sql + " and c.ids in(select p.su_company from su_change_industry p where p.su_industry in(" +su_industry_class+ "))";
    		sql=sql + " and c.su_industry_class like '%"+su_industry_class+"%'";
    	}
    	if(admin.getId() !=1 && !admin.getName().equals("admin")){//根据登录用户id，对其能看到的数据进行过滤
    		sql=sql + " and c.blame=" +admin.getId();
    	}
    	if(create_date.equals("3")){//根据创建时间，对其能看到的数据进行过滤
    		sql=sql + " and c.create_date > date_format(date_sub(now(), INTERVAL 2 day),'%Y-%m-%d 00:00:00')";//三天内数据
    	}
    	if(create_date.equals("7")){//根据创建时间，对其能看到的数据进行过滤
    		sql=sql + " and YEARWEEK(date_format(c.create_date,'%Y-%m-%d')) = YEARWEEK(now())";//本周内数据
    	}
    	if(create_date.equals("30")){//根据创建时间，对其能看到的数据进行过滤
    		sql=sql + " and date_format(c.create_date,'%Y-%m')=date_format(now(),'%Y-%m')";//本月内数据
    	}
    	List<Map<String, Object>> topNode =JdbcTemplate.queryForList(sql);//此查询获得数据
		return topNode;
	}

	@Override
	public List<Map<String, Object>> querysucompany(HttpServletRequest request, HttpServletResponse response,
			Model model) {
		String keyword = ReqUtil.getString(request, "keyword", "");
		String su_brand = ReqUtil.getString(request, "su_brand", "");
		String type = ReqUtil.getString(request, "type", "");
		String su_industry_class = ReqUtil.getString(request, "su_industry_class", "");
		String subscribe_content = ReqUtil.getString(request, "subscribe_content", "");	//订阅内容
		String major_product = ReqUtil.getString(request, "major_product", "");			//主营设备名称
		String major_product_type = ReqUtil.getString(request, "major_product_type", "");	//主营设备型号
		String su_productclass = ReqUtil.getString(request, "su_productclass", "");	//常用仪器名称
		String su_productclass_type = ReqUtil.getString(request, "su_productclass_type", "");//常用仪器型号
		String search_contact = ReqUtil.getString(request, "search_contact", "");//联系人
		String mode_blame_buy = ReqUtil.getString(request, "mode_blame_buy", "");//采购责任人
		String mode_blame = ReqUtil.getString(request, "mode_blame", "");//跟单责任人
		String create_date = ReqUtil.getString(request, "create_date", "");
		
		int pagenumber = ReqUtil.getInt(request, "page", 1);
		int rows = ReqUtil.getInt(request, "rows", 10);
		int number=(pagenumber-1)*rows;
		
		String sortValue = ReqUtil.getString(request, "sort", "");
		String sortOrder = ReqUtil.getString(request, "order", "desc");
		
		
		AdminEntity admin=adminService.getCurrent();
		String searchNumber = "";
		if (!major_product.isEmpty()||!major_product_type.isEmpty()) {
			searchNumber = ",mc_brand_and_model m";
		}
		//当进行条件过滤查询的时候的sql语句
		String sql=" select distinct c.* from su_company c,su_company_name n,su_contacts s"+searchNumber+" where c.name=n.id and s.id=c.contact";
		if(!type.isEmpty()){
			sql=sql + " and c.type like '%"+type+"%'";//性质
		}
    	if(!keyword.isEmpty()){
    		sql=sql + " and n.name like '%" +keyword+ "%'";
    	}
		if(!su_brand.isEmpty()){
			sql=sql + " and c.su_brand like '%" +su_brand+ "%'";
		}
		if (!search_contact.isEmpty()) {
			sql = sql + " and c.contact like '%" + search_contact + "%'";//联系人
		}
    	if(!mode_blame_buy.isEmpty()){
    		sql=sql + " and c.blame_buy in (" +mode_blame_buy+ ")";//采购责任人
    	}
		if(!mode_blame.isEmpty()){
			sql=sql + " and c.blame in (" +mode_blame+ ")";//跟单责任人
		}
    	/*if(!su_industry_class.isEmpty()){
    		sql=sql + " and c.su_industry_class in(" +su_industry_class+ ")";
    	}*/
    	/*if(!major_product.isEmpty()){
    		sql=sql + " and c.ids in(select m.su_company from su_change_major m where m.su_major in(" +major_product+ "))";
    	}
    	if(!su_productclass.isEmpty()){
    		sql=sql + " and c.ids in(select p.su_company from su_change_productclass p where p.su_productclass in(" +su_productclass+ "))";
    	}*/
    	if (!major_product.isEmpty()) {
    	      //sql = sql + " and c.major_product like '%" + major_product + "%'";
			sql = sql + " and (c.su_brand_and_model LIKE m.id " +
					"OR c.su_brand_and_model LIKE CONCAT(m.id, ',%') " +
					"OR c.su_brand_and_model LIKE CONCAT('%,', m.id, ',%') " +
					"OR c.su_brand_and_model LIKE CONCAT('%,', m.id)) and m.company_type=2 and m.mc_productclass_name like '%" + major_product +"%'";
    	}
		if (!major_product_type.isEmpty()) {
			sql = sql + " and (c.su_brand_and_model LIKE m.id " +
					"OR c.su_brand_and_model LIKE CONCAT(m.id, ',%') " +
					"OR c.su_brand_and_model LIKE CONCAT('%,', m.id, ',%') " +
					"OR c.su_brand_and_model LIKE CONCAT('%,', m.id)) and m.company_type=2 and m.mc_model like '%" + major_product_type +"%'";
		}
    	if (!su_productclass.isEmpty()) {
    	      sql = sql + " and c.su_productclass like '%" + su_productclass + "%'";
    	}
		if (!su_productclass_type.isEmpty()) {
			sql = sql + " and c.su_productclass_type like '%" + su_productclass_type + "%'";
		}
    	if(!su_industry_class.isEmpty()){
    		//sql=sql + " and c.ids in(select p.su_company from su_change_industry p where p.su_industry in(" +su_industry_class+ "))";
    		sql=sql + " and c.su_industry_class like '%"+su_industry_class+"%'";
    	}

		if(!subscribe_content.isEmpty()){
			sql=sql + " and c.subscribe_content like '%"+subscribe_content+"%'";
		}

    	if(admin.getId() !=1 && !admin.getName().equals("admin")){//根据登录用户id，对其能看到的数据进行过滤
    		sql=sql + " and c.blame=" +admin.getId();
    	}
    	if(create_date.equals("3")){//根据创建时间，对其能看到的数据进行过滤
    		sql=sql + " and c.create_date > date_format(date_sub(now(), INTERVAL 2 day),'%Y-%m-%d 00:00:00')";//三天内数据
    	}
    	if(create_date.equals("7")){//根据创建时间，对其能看到的数据进行过滤
    		sql=sql + " and YEARWEEK(date_format(c.create_date,'%Y-%m-%d')) = YEARWEEK(now())";//本周内数据
    	}
    	if(create_date.equals("30")){//根据创建时间，对其能看到的数据进行过滤
    		sql=sql + " and date_format(c.create_date,'%Y-%m')=date_format(now(),'%Y-%m')";//本月内数据
    	}
    		sql=sql + " order by "+sortValue+" "+sortOrder+" limit "+number+","+rows;
    	
    	List<Map<String, Object>> topNode =JdbcTemplate.queryForList(sql);//此查询获得数据
		return topNode;
	}

	@Override
	public List<Map<String, Object>> register(String name) {
		return JdbcTemplate.queryForList("select * from su_company where name='"+name+"'");
	}
}
