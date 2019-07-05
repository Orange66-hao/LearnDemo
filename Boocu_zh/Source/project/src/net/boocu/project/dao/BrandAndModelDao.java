package net.boocu.project.dao;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.BrandAndModelEntity;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface BrandAndModelDao extends BaseDao<BrandAndModelEntity,Long> {


    List<Map<String,Object>> queryrows(HttpServletRequest request, HttpServletResponse response, Model model);

    List<Map<String, Object>> query(HttpServletRequest request, HttpServletResponse response, Model model);

    List<Map<String, Object>> findListByIndustryClassAndProductclass(String id, String pid);
}
