package net.boocu.project.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.framework.util.ReqUtil;
import net.boocu.project.dao.InstrumentModelDao;
import net.boocu.project.entity.InstrumentModelEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Repository
public class InstrumentModelDaoImpl extends BaseDaoImpl<InstrumentModelEntity,Long> implements InstrumentModelDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Long queryrows(HttpServletRequest request, String page, String rows) {
        Long brand = ReqUtil.getLong(request, "brand", 0L);
        String instrument_model = ReqUtil.getString(request, "instrument_model", "");
        Long productName = ReqUtil.getLong(request, "productName", 0L);
        Long industryClassName = ReqUtil.getLong(request, "industryClassName", 0L);
        String model_name = ReqUtil.getString(request,"model_name","");
        String sql="select * from jhj_instrument_model a LEFT JOIN jhj_manufacturers_and_model b on a.id=b.instrument_model_id where 1=1 ";
        if(brand!=0){
            sql+=" and a.brand_id="+brand;
        }
        if(StringUtils.isNotBlank(instrument_model)){
            sql+=" and a.model like"+"\"%"+instrument_model+"%\"";
        }
        if(productName!=0){
            sql+=" and a.pro_id="+productName;
        }

        return Long.valueOf(jdbcTemplate.queryForList(sql).size());
    }

    @Override
    public List<Map<String, Object>> query(HttpServletRequest request,String page, String rows) {
        Long brand = ReqUtil.getLong(request, "brand", 0L);
        String instrument_model = ReqUtil.getString(request, "instrument_model", "");
        Long productName = ReqUtil.getLong(request, "productName", 0L);

        String sql="select im.id,pb.`name` pb_name, im.model,im.poption,p.`name` pname,im.remark  from jhj_instrument_model im \n" +
                "LEFT JOIN jhj_product_brand pb on pb.id=im.brand_id \n" +
                "LEFT JOIN jhj_productclass p on p.id=im.pro_id  where 1=1 " ;

        if(brand!=0){
            sql+=" and im.brand_id="+brand;
        }
        if(StringUtils.isNotBlank(instrument_model)){
            sql+=" and im.model like"+"\"%"+instrument_model+"%\"";
        }
        if(productName!=0){
            sql+=" and im.pro_id="+productName;
        }
        sql+=" limit "+(Long.parseLong(page)-1)*Long.parseLong(rows)+","+rows;
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> queryIndustryClassModelEntityList(long id) {
        String sql="select im.id,i.`name`,i.id as indu_id from jhj_industry_class_model  im LEFT JOIN jhj_industry_class i on i.id=im.industry_id where instrument_model_id="+id;
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        return list;
    }

    @Override
    public List<Map<String, Object>> queryManufacturersAndModelList(Object id, Object id1) {
        String sql="select * from jhj_manufacturers_and_model where instrument_model_id="+id+" and industry_id="+id1+"";
        return  jdbcTemplate.queryForList(sql);
    }

    @Override
    public void deleteManufacturersAndModelEntity(long l, Long id) {

        String sql="delete from jhj_manufacturers_and_model where instrument_model_id="+l+" and industry_id="+id;
        jdbcTemplate.update(sql);
    }

    @Override
    public void deleteIndustryClassModelEntity(Long instrument_mode_id ,Long indu_id) {
        String sql="delete from jhj_manufacturers_and_model where industry_id="+indu_id+" and instrument_model_id="+instrument_mode_id;
        jdbcTemplate.update(sql);
        //需要两个条件才可以删除
        String sql2="delete from jhj_industry_class_model where instrument_model_id="+instrument_mode_id +" and industry_id="+indu_id;
        jdbcTemplate.update(sql2);
    }
}
