package net.boocu.project.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.framework.util.ReqUtil;
import net.boocu.project.dao.ProductBrandDao;
import net.boocu.project.entity.ProductBrandEntity;

import net.boocu.web.Page;
import net.boocu.web.Pageable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Repository("productBrandDaoImpl")
public class ProductBrandDaoImpl extends BaseDaoImpl<ProductBrandEntity, Long> implements ProductBrandDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Override
    public List<Map<String, Object>> querybrandrows(HttpServletRequest request, HttpServletResponse response, Object o) {
        String sql="select * from jhj_product_brand where appr_status=1 and is_del=0";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        return maps;
    }

    @Override
    public Page<Map<String, Object>> findPage(Pageable pageable, HttpServletRequest request) {
        String keyword = ReqUtil.getString(request, "keyword", "");
        String sql="select * from jhj_product_brand where is_del=0 and appr_status=1 ";
        if(StringUtils.isNotBlank(keyword)){
            sql+=" and ( name like '%"+keyword+"%' or name_en like '%"+keyword+"%')";
        }
        sql+=" limit "+(pageable.getPageNumber()-1)*pageable.getPageSize()+","+pageable.getPageSize();
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select count(1) c from jhj_product_brand");
        Long count=Long.parseLong(list.get(0).get("c").toString());
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        Page page = new Page(pageable,maps,count);
        return page;
    }
}
