/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package net.boocu.project.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.framework.util.ReqUtil;
import net.boocu.project.dao.PbrandAndModelDao;
import net.boocu.project.entity.PbrandAndModelEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Repository
public class PbrandAndModelDaoImpl extends BaseDaoImpl<PbrandAndModelEntity,Long> implements PbrandAndModelDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Override
    public List<Map<String, Object>> queryrows(HttpServletRequest request, HttpServletResponse response, Model model) {
        String sql="select p.id p_id,p.name p_name,p.modify_date p_modify_date,i.id i_id,i.name i_name from jhj_productclass p \n" +
                "LEFT JOIN product_class_industry_class pi on pi.pro_id=p.id\n" +
                "LEFT JOIN jhj_industry_class i on i.id=pi.indu_id\n" +
                "LEFT JOIN jhj_p_brand_and_model pbm on pbm.pro_id=p.id\n" +
                "where p.leaf=1  ";
        String productName = ReqUtil.getString(request, "productName", "");
        String brand = ReqUtil.getString(request, "brand", "");
        String model1 = ReqUtil.getString(request, "model", "");
        String industryClassName = ReqUtil.getString(request, "industryClassName", "");
        if(StringUtils.isNotBlank(productName)){
            sql+=" and p.id="+productName;
        }
        if(StringUtils.isNotBlank(model1)){
            sql+=" and pbm.model like"+"\"%"+model1+"%\"";
        }
        if(StringUtils.isNotBlank(industryClassName)){
            sql+=" and i.id="+industryClassName;
        }
        if(StringUtils.isNotBlank(brand)){
            sql+=" and pbm.brand_id="+brand;
        }
        sql+=" GROUP BY p.id,i.id ";
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
        String sql="select p.id p_id,p.name p_name,p.modify_date p_modify_date,i.id i_id,i.name i_name from jhj_productclass p \n" +
                "LEFT JOIN product_class_industry_class pi on pi.pro_id=p.id\n" +
                "LEFT JOIN jhj_industry_class i on i.id=pi.indu_id\n" +
                "LEFT JOIN jhj_p_brand_and_model pbm on pbm.pro_id=p.id\n" +
                "where p.leaf=1 ";
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
        sql+=" GROUP BY p.id,i.id";
        sql = sql + " order by " + sortValue + " " + sortOrder + " limit " + number + "," + rows;
        List<Map<String, Object>> topNode =jdbcTemplate.queryForList(sql);//此查询获得数据
        return topNode;
    }

    @Override
    public List<Map<String, Object>> findListByIndustryClassAndProductclass(String p_id, String i_id) {
        String sql="select id,model from jhj_p_brand_and_model pbm where pbm.industry_id="+i_id+" and pro_id="+p_id+"";
        return jdbcTemplate.queryForList(sql);
    }
}
