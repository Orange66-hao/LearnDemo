package net.boocu.project.service;

import net.boocu.project.entity.ProductclassEntity;
import net.boocu.web.service.BaseService;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface ProductclassService extends BaseService<ProductclassEntity, Long> {
	public void testFullName();

    List<Map<String,Object>> queryrows(HttpServletRequest request, HttpServletResponse response, Model model);

    List<Map> query(HttpServletRequest request, HttpServletResponse response, Model model);
}
