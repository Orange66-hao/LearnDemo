package net.boocu.web.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.framework.util.ReqUtil;
import net.boocu.project.entity.MailSignatureEntity;
import net.boocu.project.entity.ModelCollectionEntity;
import net.boocu.project.entity.ModelCollectionTwoEntity;
import net.boocu.web.dao.MailSignatureDao;
import net.boocu.web.dao.MemberGradeDao;
import net.boocu.web.dao.ModelCollectionDao;
import net.boocu.web.dao.ModelCollectionTwoDao;
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
@Repository("modelCollectionTwoDaoImpl")
public class ModelCollectionTwoDaoImpl extends BaseDaoImpl<ModelCollectionTwoEntity, Long> implements ModelCollectionTwoDao {
	@Resource
	private JdbcTemplate JdbcTemplate;
	
	@Resource(name = "adminServiceImpl")
	AdminService adminService;
	
	
	@Override
	public List<Map<String, Object>> querycompanytworows(HttpServletRequest request, HttpServletResponse response,
			Model model) {
		String keyword = ReqUtil.getString(request, "keyword", "");
		String mc_industry_class = ReqUtil.getString(request, "mc_industry_class", "");
		String major_product = ReqUtil.getString(request, "major_product", "");
		String mc_productclass = ReqUtil.getString(request, "mc_productclass", "");
		String mode_blame = ReqUtil.getString(request, "mode_blame", "");
		String mc_brand = ReqUtil.getString(request, "mc_brand", "");
		String create_date = ReqUtil.getString(request, "create_date", "");
		
		int pagenumber = ReqUtil.getInt(request, "page", 1);
		int rows = ReqUtil.getInt(request, "rows", 10);
		int number=(pagenumber-1)*rows;
		
		String sortValue = ReqUtil.getString(request, "sort", "");
		String sortOrder = ReqUtil.getString(request, "order", "desc");
		
		
		AdminEntity admin=adminService.getCurrent();
    	
		//当进行条件过滤查询的时候的sql语句
		String sql=" select * from mc_company_two c where 1=1 ";
    	if(!keyword.isEmpty()){
    		sql=sql + " and c.name in (" +keyword+ ")";
    	}
    	if(!mode_blame.isEmpty()){
    		sql=sql + " and c.blame in (" +mode_blame+ ")";
    	}
    	/*if(!mc_industry_class.isEmpty()){
    		sql=sql + " and c.mc_industry_class in(" +mc_industry_class+ ")";
    	}*/
    	if(!major_product.isEmpty()){
    		sql=sql + " and c.ids in(select m.mc_company from mc_change_major m where m.mc_major in(" +major_product+ "))";
    	}
    	if(!mc_productclass.isEmpty()){
    		sql=sql + " and c.ids in(select p.mc_company from mc_change_productclass p where p.mc_productclass in(" +mc_productclass+ "))";
    	}
    	if(!mc_brand.isEmpty()){
    		sql=sql + " and c.ids in(select p.mc_company from mc_change_brand p where p.mc_brand in(" +mc_brand+ "))";
    	}
    	/*if (!major_product.isEmpty()) {
    	      sql = sql + " and c.major_product like '%" + major_product + "%'";
    	    }
    	if (!mc_productclass.isEmpty()) {
    	      sql = sql + " and c.mc_productclass like '%" + mc_productclass + "%'";
    	    }
    	if(!mc_industry_class.isEmpty()){
    		//sql=sql + " and c.ids in(select p.mc_company from mc_change_industry p where p.mc_industry in(" +mc_industry_class+ "))";
    		sql=sql + " and c.mc_industry_class like '%"+mc_industry_class+"%'";
    	}*/
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
    	
    	List<Map<String, Object>> topNoderows =JdbcTemplate.queryForList(sql);//此查询获得条数
    	return topNoderows;
	}

	@Override
	public List<Map<String, Object>> querycompanytwo(HttpServletRequest request, HttpServletResponse response, Model model) {
		String keyword = ReqUtil.getString(request, "keyword", "");
		String mc_industry_class = ReqUtil.getString(request, "mc_industry_class", "");
		String major_product = ReqUtil.getString(request, "major_product", "");
		String mc_productclass = ReqUtil.getString(request, "mc_productclass", "");
		String mode_blame = ReqUtil.getString(request, "mode_blame", "");
		String create_date = ReqUtil.getString(request, "create_date", "");
		String grade = ReqUtil.getString(request, "grade", "");
		String measurement_time = ReqUtil.getString(request, "measurement_time", "");
		
		int pagenumber = ReqUtil.getInt(request, "page", 1);
		int rows = ReqUtil.getInt(request, "rows", 10);
		int number=(pagenumber-1)*rows;
		
		String sortValue = ReqUtil.getString(request, "sort", "");
		String sortOrder = ReqUtil.getString(request, "order", "desc");
    	AdminEntity admin=adminService.getCurrent();
    	
		//当进行条件过滤查询的时候的sql语句
		String sql=" select * from mc_company_two c where 1=1 ";
    	if(!keyword.isEmpty()){
    		sql=sql + " and c.name in (" +keyword+ ")";
    	}
    	if(!mode_blame.isEmpty()){
    		sql=sql + " and c.blame in (" +mode_blame+ ")";
    	}
    	/*if(!mc_industry_class.isEmpty()){
    		sql=sql + " and c.mc_industry_class in(" +mc_industry_class+ ")";
    	}*/
    	/*if(!major_product.isEmpty()){
    		sql=sql + " and c.ids in(select m.mc_company from mc_change_major m where m.mc_major in(" +major_product+ "))";
    	}
    	if(!mc_productclass.isEmpty()){
    		sql=sql + " and c.ids in(select p.mc_company from mc_change_productclass p where p.mc_productclass in(" +mc_productclass+ "))";
    	}*/
    	if (!major_product.isEmpty()) {
    	      sql = sql + " and c.major_product like '%" + major_product + "%'";
    	    }
    	if (!mc_productclass.isEmpty()) {
    	      sql = sql + " and c.mc_productclass like '%" + mc_productclass + "%'";
    	    }
    	if (!grade.isEmpty()) {
    		sql = sql + " and c.grade="+grade;
    	}
    	if (!measurement_time.isEmpty()) {
    		sql = sql + " and c.measurement_time="+measurement_time;
    	}
    	if(!mc_industry_class.isEmpty()){
    		//sql=sql + " and c.ids in(select p.mc_company from mc_change_industry p where p.mc_industry in(" +mc_industry_class+ "))";
    		sql=sql + " and c.mc_industry_class like '%"+mc_industry_class+"%'";
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
		return JdbcTemplate.queryForList("select * from mc_company where name='"+name+"'");
	}

	@Override
	public int delete_mc_change_major(String mc_major) {
		return JdbcTemplate.update("delete from mc_change_major where mc_major in("+mc_major+")");
	}

	@Override
	public int insert_mc_change_major(String ids, String mc_major) {
		return JdbcTemplate.update("insert into mc_change_major(mc_company,mc_major) values("+ids+","+mc_major+")");
	}

	@Override
	public int delete_mc_change_productclass(String mc_productclass) {
		return JdbcTemplate.update("delete from mc_change_productclass where mc_productclass in("+mc_productclass+")");
	}

	@Override
	public int insert_mc_change_productclass(String ids, String mc_productclass) {
		return JdbcTemplate.update("insert into mc_change_productclass(mc_company,mc_productclass) values("+ids+","+mc_productclass+")");
	}

	@Override
	public int delete_mc_change_brand(String mc_brand) {
		return JdbcTemplate.update("delete from mc_change_brand where mc_brand in("+mc_brand+")");
	}

	@Override
	public int insert_mc_change_brand(String ids, String mc_brand) {
		return JdbcTemplate.update("insert into mc_change_brand(mc_company,mc_brand) values("+ids+","+mc_brand+")");
	}
}
