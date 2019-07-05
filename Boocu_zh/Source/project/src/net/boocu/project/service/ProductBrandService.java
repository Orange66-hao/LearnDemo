package net.boocu.project.service;

import com.sun.xml.internal.ws.resources.HttpserverMessages;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.BaseService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 使用方法:如UserEntity
 * 1.将 ProductBrand replaceAll 成 User (注意大小写敏感,将Case sensitive勾选)
 * */
public interface ProductBrandService extends BaseService<ProductBrandEntity, Long> {

    void orderExport(HttpServletRequest request, HttpSession session, HttpServletResponse response);


    Page<Map<String, Object>> findPage(Pageable pageable, HttpServletRequest request);
}
