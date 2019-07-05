package net.boocu.project.dao;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.ProductclassEntity;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 使用方法:如UserEntity
 * 1.将 Productclass replaceAll 成 User (注意大小写敏感,将Case sensitive勾选)
 * */
public interface ProductclassDao extends BaseDao<ProductclassEntity,Long> {

    List<Map<String,Object>> queryrows(HttpServletRequest request, HttpServletResponse response, Model model);

    List<Map<String,Object>> query(HttpServletRequest request, HttpServletResponse response, Model model);
}
