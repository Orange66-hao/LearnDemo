package net.boocu.project.service;

import java.util.List;
import java.util.Map;

import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.IndustryClassModelEntity;
import net.boocu.web.service.BaseService;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IndustryClassService extends BaseService<IndustryClassEntity, Long> {
	//取得节点下的所有子节点,包括本身
	public List<IndustryClassEntity> getChildren(Long[] id);
	//取得节点下的所有子节点,包括本身
	public List<Map<String, Object>> getParentIds(String[] industryClass);

    List<Map<String,Object>> queryrows(HttpServletRequest request, HttpServletResponse response, Model model);

	List<Map> query(HttpServletRequest request, HttpServletResponse response, Model model);
}
