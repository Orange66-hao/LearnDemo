package net.boocu.project.service;

import net.boocu.project.entity.BrandAndModelEntity;
import net.boocu.project.entity.McBrandEntity;
import net.boocu.web.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface BrandAndModelService extends BaseService<BrandAndModelEntity, Long> {

    List<Map<String,Object>> queryrows(HttpServletRequest request, HttpServletResponse response, Model model);

    List<Map> query(HttpServletRequest request, HttpServletResponse response, Model model);
}
