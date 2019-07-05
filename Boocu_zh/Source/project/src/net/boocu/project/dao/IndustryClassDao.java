package net.boocu.project.dao;

import java.util.List;
import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.IndustryClassEntity;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IndustryClassDao extends BaseDao<IndustryClassEntity,Long> {

	List<Map<String, Object>> getParentIds(String param);

    List<Map<String,Object>> queryrows(HttpServletRequest request, HttpServletResponse response, Model model);

    List<Map<String, Object>> query(HttpServletRequest request, HttpServletResponse response, Model model);
}
