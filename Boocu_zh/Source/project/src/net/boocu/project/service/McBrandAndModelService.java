package net.boocu.project.service;

import net.boocu.project.entity.McBrandAndModelEntity;
import net.boocu.project.entity.McModelEntity;
import net.boocu.web.Message;
import net.boocu.web.service.BaseService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

public interface McBrandAndModelService extends BaseService<McBrandAndModelEntity, Long> {
	//取得节点下的所有子节点,包括本身
	public List<McBrandAndModelEntity> getChildren(Long[] id);
	//取得节点下的所有子节点,包括本身
	public List<Map<String, Object>> getParentIds(String[] industryClass);
	//获取仪器品牌型号名称
	public Message addBrandAndModel (String brand, String model, String mc_productclass_name, String mc_company);
	//获取仪器品牌型号名称
    public List<Map<String,Object>> quaryMcBrandAndModel(String id);
}
