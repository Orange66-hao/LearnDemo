package net.boocu.project.dao;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface ProductBrandDao extends BaseDao<ProductBrandEntity,Long> {

    List<Map<String,Object>> querybrandrows(HttpServletRequest request, HttpServletResponse response, Object o);

    Page<Map<String, Object>> findPage(Pageable pageable, HttpServletRequest request);
}
