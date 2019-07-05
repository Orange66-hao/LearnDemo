package net.boocu.project.dao.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.framework.util.ReqUtil;
import net.boocu.project.dao.IndustryClassDao;
import net.boocu.project.entity.IndustryClassEntity;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;

@Repository("industryClassDaoImpl")
public class IndustryClassDaoImpl extends BaseDaoImpl<IndustryClassEntity, Long> implements IndustryClassDao {

	@Resource
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public List<Map<String, Object>> getParentIds(String param) {
		System.err.println("-----------"+param);
		return jdbcTemplate.queryForList(" select DISTINCT SUBSTR(ic.id,1,3) as parentId from jhj_industry_class ic where ic.name in ("+param+")");
	}

	@Override
	public List<Map<String, Object>> queryrows(HttpServletRequest request, HttpServletResponse response, Model model) {
		String sql="select p.id p_id,p.name p_name,p.modify_date p_modify_date from jhj_productclass p\n" +
				"LEFT JOIN product_class_industry_class pi on pi.pro_id=p.id \n" +
				"LEFT JOIN jhj_industry_class i  on i.id=pi.indu_id \n" +
				"LEFT JOIN jhj_brand_and_model bm on bm.industry_id=i.id \n" +
				"where p.leaf=1  ";
		String productName = ReqUtil.getString(request, "productName", "");
		String brand = ReqUtil.getString(request, "brand", "");
		String model1 = ReqUtil.getString(request, "model", "");
		String industryClassName = ReqUtil.getString(request, "industryClassName", "");
		if(StringUtils.isNotBlank(productName)){
			sql+=" and p.id="+productName;
		}
		if(StringUtils.isNotBlank(model1)){
			sql+=" and bm.model like"+"\"%"+model1+"%\"";
		}
		if(StringUtils.isNotBlank(industryClassName)){
			sql+=" and i.id="+industryClassName;
		}
		if(StringUtils.isNotBlank(brand)){
			sql+=" and bm.brand_id="+brand;
		}
		sql+=" GROUP BY p.id ";
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> query(HttpServletRequest request, HttpServletResponse response, Model model) {
		String productName = ReqUtil.getString(request, "productName", "");
		String industryClassName = ReqUtil.getString(request, "industryClassName", "");
		String brand = ReqUtil.getString(request, "brand", "");
		String model1 = ReqUtil.getString(request, "model", "");
		int pagenumber = ReqUtil.getInt(request, "page", 1);
		int rows = ReqUtil.getInt(request, "rows", 10);
		int number=(pagenumber-1)*rows;

		String sortValue = ReqUtil.getString(request, "sort", "");
		String sortOrder = ReqUtil.getString(request, "order", "desc");
		String sql="select p.id p_id,p.name p_name,p.modify_date p_modify_date from jhj_productclass p\n" +
				"LEFT JOIN product_class_industry_class pi on pi.pro_id=p.id \n" +
				"LEFT JOIN jhj_industry_class i  on i.id=pi.indu_id \n" +
				"LEFT JOIN jhj_p_brand_and_model pbm on pbm.pro_id=p.id \n" +
				"where p.leaf=1  ";
		if(StringUtils.isNotBlank(productName)){
			sql+=" and p.id="+productName;
		}
		if(StringUtils.isNotBlank(industryClassName)){
			sql+=" and i.id="+industryClassName;
		}
		if(StringUtils.isNotBlank(brand)){
			sql+=" and pbm.brand_id="+brand;
		}
		if(StringUtils.isNotBlank(model1)){
			sql+=" and pbm.model like"+"\"%"+model1+"%\"";
		}
		sql+=" GROUP BY p.id";
		sql = sql + " order by " + sortValue + " " + sortOrder + " limit " + number + "," + rows;
		List<Map<String, Object>> topNode =jdbcTemplate.queryForList(sql);//此查询获得数据
		return topNode;
	}
}
