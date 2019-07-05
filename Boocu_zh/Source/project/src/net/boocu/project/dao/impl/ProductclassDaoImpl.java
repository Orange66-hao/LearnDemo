package net.boocu.project.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.framework.util.ReqUtil;
import net.boocu.project.dao.ProductclassDao;
import net.boocu.project.entity.ProductclassEntity;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


@Repository("productclassDaoImpl")
public class ProductclassDaoImpl extends BaseDaoImpl<ProductclassEntity, Long> implements ProductclassDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Override
    public List<Map<String, Object>> queryrows(HttpServletRequest request, HttpServletResponse response, Model model) {
        String sql="select i.id i_id,i.name i_name,i.modify_date i_modify_date from jhj_industry_class i \n" +
                "LEFT JOIN product_class_industry_class pi on pi.indu_id=i.id \n" +
                "LEFT JOIN jhj_productclass p on p.id=pi.pro_id   \n" +
                "LEFT JOIN jhj_brand_and_model bm on bm.industry_id=i.id\n" +
                "where i.leaf=1 ";
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
        sql+=" GROUP BY i.id ";
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
        String sql="select i.id i_id,i.name i_name,i.modify_date i_modify_date from jhj_industry_class i \n" +
                "LEFT JOIN product_class_industry_class pi on pi.indu_id=i.id \n" +
                "LEFT JOIN jhj_productclass p on p.id=pi.pro_id   \n" +
                "LEFT JOIN jhj_brand_and_model bm on bm.industry_id=i.id\n" +
                "where i.leaf=1 ";
        if(StringUtils.isNotBlank(productName)){
            sql+=" and p.id="+productName;
        }
        if(StringUtils.isNotBlank(industryClassName)){
            sql+=" and i.id="+industryClassName;
        }
        if(StringUtils.isNotBlank(brand)){
            sql+=" and bm.brand_id="+brand;
        }
        if(StringUtils.isNotBlank(model1)){
            sql+=" and bm.model like"+"\"%"+model1+"%\"";
        }
        sql+=" GROUP BY i.id";
        sql = sql + " order by " + sortValue + " " + sortOrder + " limit " + number + "," + rows;
        List<Map<String, Object>> topNode =jdbcTemplate.queryForList(sql);//此查询获得数据
        return topNode;
    }
}
