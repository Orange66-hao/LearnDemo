/**
 * 
 */
package net.boocu.project.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import net.boocu.project.dao.LookValDao;
import net.boocu.project.entity.LookValEntity;

/**
 * @author Administrator
 *
 */
@Repository
public class LookValDaoImpl implements LookValDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public void addLookVal(String sql) {
		jdbcTemplate.update(sql);
	}
	
	@Override
	public Map<String,Object> findSubScript(Long proId,int group){
		String sql = "SELECT DISTINCT COUNT(s.member_id) AS userCount , SUM(IF(s.model=0||2,1,0)) AS system,"
		+ "SUM(IF(s.model=1||2,1,0)) AS email FROM jhj_product_subscribe s WHERE";
		if(group == 1){
			sql += " s.product_id="+proId;
		}else if(group == 2){
			sql += " s.productclass_id = "+proId;
		}else if(group == 3){
			sql +=" s.industryclass_id ="+proId;
		}
		Map<String,Object> map = new HashMap<String,Object>();
		jdbcTemplate.query(sql, new RowCallbackHandler(){
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				map.put("userCount", rs.getString("userCount"));
				map.put("systemRecv", rs.getString("system"));
				map.put("emailRecv", rs.getString("email"));
			}
		});
		return map;
	}
	
	@Override
	public List<LookValEntity> lvSubscript(int type,int group){
		
		if(type == 0 || group == 0)
			return new ArrayList<LookValEntity>();
		
		//type 1 ：产品 2：行业 3：产品分类
		String sql = "SELECT COUNT(s.member_id) AS lookVal,s.pro_type AS proType,";
		if(type == 1){
			sql += "s.product_id AS proId,(SELECT p.pro_name FROM jhj_product p WHERE p.id = s.product_id)";
		}else if(type == 2){
			sql += "s.productclass_id AS proId,(SELECT p.name FROM jhj_productclass p WHERE p.id = s.productclass_id)";
		}else if(type == 3){
			sql += "s.industryclass_id AS proId,(SELECT p.name FROM jhj_industry_class p WHERE p.id = s.industryclass_id)";
		}
	    sql += " AS name FROM jhj_product_subscribe s WHERE s.pro_type LIKE '%"+group+"%'";
		if(type == 1){
			sql += " AND s.product_id IS NOT NULL GROUP BY s.product_id,s.member_id";
		}else if(type == 2){
			sql += " AND s.productclass_id IS NOT NULL GROUP BY s.productclass_id,s.member_id";
		}else if(type == 3){
			sql += " AND s.industryclass_id IS NOT NULL GROUP BY s.industryclass_id,s.member_id";
		}
		sql += " ORDER BY COUNT(s.id) DESC LIMIT 50;";
		
		List<LookValEntity> list = new ArrayList<LookValEntity>();
		jdbcTemplate.query(sql, new RowCallbackHandler(){
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				LookValEntity lv = new LookValEntity();
				lv.setLookVal(rs.getInt("lookVal"));
				lv.setProNo(rs.getString("proType"));
				lv.setProId(rs.getLong("proId"));
				lv.setBrandName(rs.getString("name"));
				list.add(lv);
			}
		});
		return list;
	}
	
	/**
	 * 新增最多的表格
	 */
	@Override
	public List<LookValEntity> lvMostAdd(Map<String,Object> params){
		String startTime = params.get("startTime").toString();
		String endTime = params.get("endTime").toString();
		int type = Integer.parseInt(params.get("type").toString());
		String dynamic = null;
		switch (type) {
		case 1:
			dynamic = "jhj_product_sale";
			break;
		case 2:
			dynamic = "jhj_product_buy";
			break;
		case 3:
			dynamic = "jhj_product_rent";
			break;
			
		case 4:
			dynamic = "jhj_product_want_rent";
			break;
		case 5:
			dynamic = "jhj_product_repair";
			break;
		case 6:
			dynamic = "jhj_product_want_repair";
			break;

		default:
			return null;
		}
		
		String sql = "SELECT p.brand_id AS brandId,b.name AS brandName,p.pro_no AS proNo,COUNT(p.id) AS lookVal,p.id AS ";
		sql += " proId FROM "+dynamic+" s LEFT JOIN jhj_product p ON s.product_id = p.id LEFT JOIN ";
		sql += " jhj_product_brand b ON b.id = p.brand_id WHERE pro_no IS NOT NULL AND brand_id IS ";
		sql += " NOT NULL AND p.appr_status = 1 AND p.proownaudit = 1 AND p.is_del=0";
		if(startTime != null && !"".equals(startTime) && endTime != null && !"".equals(endTime)){
			sql += " AND p.create_date >= '"+startTime+"' AND p.create_date <= '" + endTime +"'";
		}
		sql += " GROUP BY p.pro_no,p.brand_id ORDER BY COUNT(p.id) DESC LIMIT 50;";
		
		List<LookValEntity> list = new ArrayList<LookValEntity>();
		jdbcTemplate.query(sql,new  RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				LookValEntity lv = new LookValEntity();
				lv.setProId(rs.getLong("proId"));
				lv.setProNo(rs.getString("proNo"));
				lv.setBrandId(rs.getLong("brandId"));
				lv.setBrandName(rs.getString("brandName"));
				lv.setLookVal(rs.getInt("lookVal"));
				list.add(lv);
			}
		});

		return list;
	}
	
	//商品总数
	@Override
	public int findMostProCount(String proNo,Long brandId,int type){
		String dynamic = null;
		switch (type) {
		case 1:
			dynamic = "jhj_product_sale";
			break;
		case 2:
			dynamic = "jhj_product_buy";
			break;
		case 3:
			dynamic = "jhj_product_rent";
			break;
			
		case 4:
			dynamic = "jhj_product_want_rent";
			break;
		case 5:
			dynamic = "jhj_product_repair";
			break;
		case 6:
			dynamic = "jhj_product_want_repair";
			break;

		default:
			return 0;
		}
		String sql = "SELECT COUNT(p.id) FROM "+dynamic+" s LEFT JOIN jhj_product p ON ";
		sql +=  "s.product_id = p.id WHERE p.brand_id= "+brandId+" AND p.pro_no = '"+proNo+"' AND p.is_del=0;";
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}
	
	//发布用户数
	@Override
	public int findMostUserCount(String proNo,Long brandId,int type){
		
		String dynamic = null;
		switch (type) {
		case 1:
			dynamic = "jhj_product_sale";
			break;
		case 2:
			dynamic = "jhj_product_buy";
			break;
		case 3:
			dynamic = "jhj_product_rent";
			break;
			
		case 4:
			dynamic = "jhj_product_want_rent";
			break;
		case 5:
			dynamic = "jhj_product_repair";
			break;
		case 6:
			dynamic = "jhj_product_want_repair";
			break;

		default:
			return 0;
		}
		
		String sql = "SELECT COUNT(*) FROM (SELECT COUNT(p.id) FROM "+dynamic+" s LEFT JOIN jhj_product p ON s.product_id =";
		sql += " p.id WHERE p.brand_id="+brandId+" AND p.is_del=0 AND p.pro_no = '"+proNo+"' GROUP BY p.member_id) t;";
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}
	
	/**
	 * 热门类型
	 */
	@Override
	public List<LookValEntity> lvTypeList(Map<String,Object> params) {
		String startTime = params.get("startTime").toString();
		String endTime = params.get("endTime").toString();
		int type = Integer.parseInt(params.get("type").toString());
		String dynamic = null;
		switch (type) {
		case 1:
			dynamic = "jhj_product_sale";
			break;
		case 2:
			dynamic = "jhj_product_buy";
			break;
		case 3:
			dynamic = "jhj_product_rent";
			break;
			
		case 4:
			dynamic = "jhj_product_want_rent";
			break;
		case 5:
			dynamic = "jhj_product_repair";
			break;
		case 6:
			dynamic = "jhj_product_want_repair";
			break;

		default:
			return null;
		}
		
		String sql = "SELECT c.id AS classId,b.name AS brandName,c.name AS proNo,COUNT(l.id) AS lookVal FROM t_look_val"; 
		sql += " l LEFT JOIN " + dynamic + " s ON l.pro_id = s.product_id LEFT JOIN jhj_product p ON";
		sql += " s.product_id = p.id LEFT JOIN jhj_product_brand b ON b.id = p.brand_id LEFT JOIN jhj_productclass";
		sql += " c ON p.pro_class_id = c.id  WHERE p.pro_no IS NOT NULL AND p.appr_status = 1 AND p.proownaudit = 1 AND p.is_del=0";
		if(startTime != null && !"".equals(startTime) &&
		endTime != null && !"".equals(endTime)){
			sql += " AND l.create_time >= '"+startTime+"' AND l.create_time <= '" + endTime +"'";
		}
		sql += " AND p.brand_id IS NOT NULL AND c.name IS NOT NULL AND c.leaf = 1 GROUP BY";
		sql += " c.id ORDER BY COUNT(l.id) DESC LIMIT 50;";
		
		List<LookValEntity> list = new ArrayList<LookValEntity>();
		jdbcTemplate.query(sql,new  RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				LookValEntity lv = new LookValEntity();
				lv.setProId(rs.getLong("classId"));
				lv.setProNo(rs.getString("proNo"));
				lv.setBrandName(rs.getString("brandName"));
				lv.setLookVal(rs.getInt("lookVal"));
				list.add(lv);
			}
		});

		return list;
	}
	
	@Override
	public LookValEntity findTypeInfo(Long classId,int type){
		String dynamic = null;
		switch (type) {
		case 1:
			dynamic = "jhj_product_sale";
			break;
		case 2:
			dynamic = "jhj_product_buy";
			break;
		case 3:
			dynamic = "jhj_product_rent";
			break;
			
		case 4:
			dynamic = "jhj_product_want_rent";
			break;
		case 5:
			dynamic = "jhj_product_repair";
			break;
		case 6:
			dynamic = "jhj_product_want_repair";
			break;

		default:
			return null;
		}
		
		String sql = "SELECT (SELECT username FROM sys_member m WHERE m.id = p.member_id";
		sql += " ) AS username,p.look_date AS lookDate,p.pro_no AS proNo,(SELECT NAME ";
		sql += " FROM jhj_product_brand b WHERE b.id = p.brand_id) AS brandName FROM "+dynamic;
		sql += " s LEFT JOIN jhj_product p ON p.id = s.product_id LEFT JOIN t_look_val";
		sql += " l ON l.pro_id = p.id WHERE p.pro_no IS NOT NULL AND p.appr_status = 1";
		sql += " AND p.proownaudit = 1 AND p.is_del=0 AND p.brand_id IS NOT NULL AND p.pro_class_id = "+classId;
		sql += " ORDER BY look_time DESC LIMIT 1;";
		
		LookValEntity lv = new LookValEntity();
		jdbcTemplate.query(sql,new  RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				lv.setUserName(rs.getString("username"));
				lv.setProNo(rs.getString("proNo"));
				lv.setBrandName(rs.getString("brandName"));
				lv.setCreateTime(rs.getString("lookDate"));
			}
		});

		return lv;
	}
	
	
	
	/**
	 *浏览最多的列表 
	 */
	@Override
	public Map<String,Object> findLookInfo(String proNo,Long brandId,int type){
		String sql = "SELECT COUNT(id) AS Count FROM t_look_val WHERE brand_id = "+brandId+""
				+ " AND pro_no= '"+proNo+"' AND type="+type+" GROUP BY pro_id;";
		
		List<LookValEntity> list = new ArrayList<LookValEntity>();
		jdbcTemplate.query(sql,new  RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				LookValEntity lv = new LookValEntity();
				lv.setLookVal(rs.getInt("Count"));
				list.add(lv);
			}
		});
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("proCount", list.size());
		int maxLook = 0;
		for(LookValEntity l : list){
			if(l.getLookVal() > maxLook){
				maxLook = l.getLookVal();
			}
		}
		map.put("maxLook", maxLook);
		return map;
	}

	/**
	 * 浏览最多的型号
	 */
	@Override
	public List<LookValEntity> lvList(Map<String, Object> params) {
		String startTime = params.get("startTime").toString();
		String endTime = params.get("endTime").toString();
		int type = Integer.parseInt(params.get("type").toString());
		String sql = "SELECT COUNT(*) AS lookVal,pro_no AS proNo,brand_name"
		+" AS brandName,brand_id AS brandId FROM t_look_val WHERE 1 = 1 ";
		if(type > 0){
			sql += " AND type = " + type;
		}
		if(startTime != null && !"".equals(startTime) &&
		endTime != null && !"".equals(endTime)){
			sql += " AND create_time >= '"+startTime+"' AND create_time <= '" + endTime +"'";
		}
		sql += " GROUP BY pro_no,brand_id ORDER BY COUNT(*) DESC LIMIT 50";
		
		List<LookValEntity> list = new ArrayList<LookValEntity>();
		jdbcTemplate.query(sql,new  RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				LookValEntity lv = new LookValEntity();
				lv.setLookVal(rs.getInt("lookVal"));
				lv.setProNo(rs.getString("proNo"));
				lv.setBrandName(rs.getString("brandName"));
				lv.setBrandId(rs.getLong("brandId"));
				list.add(lv);
			}
		});

		return list;
	}
}