package net.boocu.project.dao;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.McBrandAndModelEntity;
import net.boocu.project.entity.McModelEntity;

import java.util.List;
import java.util.Map;

public interface McBrandAndModelDao extends BaseDao<McBrandAndModelEntity,Long> {

	List<Map<String, Object>> getParentIds(String param);
	//获取仪器品牌型号名称
	public List<Map<String, Object>> addBrandAndModel (String brand, String model, String mc_productclass_name,String mc_company);
	//获取仪器相关信息
    public List<Map<String,Object>> quaryMcBrandAndModel(String id);
}
