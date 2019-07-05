package net.boocu.project.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.util.ReqUtil;
import net.boocu.project.dao.McBrandDao;
import net.boocu.project.entity.McBrandEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.McBrandService;
import net.boocu.web.Filter;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;


@Service("McBrandServiceImpl")
public class McBrandServiceImpl extends BaseServiceImpl<McBrandEntity, Long> implements McBrandService {
	@Resource(name = "mcBrandDaoImpl")
    private McBrandDao McBrandDao;
	/*@Resource
	private JdbcTemplate jdbcTemplate;*/
    
    @Resource(name = "mcBrandDaoImpl")
    public void setBaseDao(McBrandDao McBrandDao) {
    	super.setBaseDao(McBrandDao);
    }

	@Override
	@Transactional
	public McBrandEntity save(McBrandEntity entity) {
		/*McBrandEntity parentEntity = find(Long.parseLong(entity.getParentid())) ;
		parentEntity.setLeaf("0");
		update(parentEntity);*/
		return super.save(entity);
	}

	@Override
	@Transactional
	public void deleteList(Long... ids) {
		List<Long> childrenIds = new ArrayList<Long>();
		for(int i=0;i<ids.length;i++){
			McBrandEntity entity = find(ids[i]);
			if(entity != null){
				 List<McBrandEntity> children = findList(Filter.eq("parentid", entity.getId()));
				 for(McBrandEntity child : children){
					 childrenIds.add(child.getId());
				 }
				 if(children.size() > 0){
					 deleteList(childrenIds.toArray(new Long[childrenIds.size()]));
				 }
			}
			//遍历到最后一个时,判断父节点是否仍有子节点
			/*if(i == ids.length-1){
				List<McBrandEntity> leftChildren = findList(Filter.eq("parentid", entity.getMenuid()));
				if(leftChildren.size() == 0){
					McBrandEntity parent = find(Filter.eq("id", entity.getParentid()));
					parent.setLeaf("1");
					update(parent);
				}
			}*/
		}
		super.deleteList(ids);
	}

	@Override
	public List<McBrandEntity> getChildren(Long[] ids) {
		List<McBrandEntity> McBrandEntities = new ArrayList<McBrandEntity>();
		for(long id : ids){
			McBrandEntity McMcBrandEntity = find(id);
			if(McMcBrandEntity != null){
				McBrandEntities.add(McMcBrandEntity);
				McBrandEntities.addAll(getChildren(McMcBrandEntity.getId()));
			}
		}
		return McBrandEntities;
	}
	
	//递归取得子节点
	public List<McBrandEntity> getChildren(long id){
		List<McBrandEntity> McBrandEntities = new ArrayList<McBrandEntity>();
		List<McBrandEntity> children = findList(Filter.eq("parentid", id));
		if(children.size() != 0){
			McBrandEntities.addAll(children);
			for(McBrandEntity item : children){
				McBrandEntities.addAll(getChildren(item.getId()));
			}
		}
		return McBrandEntities;
	}

	@Override
	public List<Map<String, Object>> getParentIds(String[] McBrand) {
		String param = "";
		for(int i = 0 ; i<McBrand.length;i++ ){
			param +="'"+McBrand[i]+"',";
		}
		// jdbcTemplate.queryForList(" select DISTINCT SUBSTR(ic.id,1,3) as parentId from jhj_industry_class ic where ic.name in ("+param+")");
		return McBrandDao.getParentIds(param.substring(0, param.length()-1));
	}

	@Override
	public Map queryall(String id) {
		List<Map<String, Object>> topNode=null;
		if (id != null && !id.isEmpty()) {
			topNode =McBrandDao.queryall(id);
		}
			if (topNode == null) {
				return null;
			}
			String  ids[] = new String[topNode.size()];
			String  name[] = new String[topNode.size()];
			int i=0;
			for (Map<String, Object> map : topNode) {
				ids[i]=(map.get("id")).toString();
				name[i]=(String) map.get("name");
				i++;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", ids);
			map.put("name", name);
		return map;
	}

	@Override
	public Map getNodeData2(String id) {
		List<Map<String, Object>> topNode =McBrandDao.getNodeData2(id);
			if (topNode == null) {
				return null;
			}
		String  id2[] = new String[topNode.size()];
		String  name[] = new String[topNode.size()];
		int i=0;
		for (Map<String, Object> map : topNode) {
				id2[i]=(map.get("id")).toString();
				name[i]=(String) map.get("name");
				i++;
		}
		Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", id2);
			map.put("name", name);
		return map;
	}

	@Override
	public List<Map> getMcBrandGradeNames(HttpServletRequest reqeust, HttpServletResponse response, Model model,
			McBrandEntity memberGradeEntity) {
		String mc_product_id = ReqUtil.getString(reqeust, "mc_product_id", "");
    	String mc_brand_coll_type = ReqUtil.getString(reqeust, "mc_brand_coll_type", "");
    	if (mc_product_id == "" || mc_product_id.length() == 0) {
    		mc_product_id=null;
		}
    	List<Map<String, Object>> topNode;
    		if (mc_product_id != null && mc_product_id != "") {
        		topNode =McBrandDao.queryall_brainid(mc_product_id);
    		}
    		else if(mc_brand_coll_type.equals("onunselect")){
    			mc_product_id=null;
    			topNode =McBrandDao.queryall_brainid(mc_product_id);
    		}
    		else{
    			topNode =McBrandDao.queryall_braid();
    		}
    		
    		
		String  id[] = new String[topNode.size()];
		String  name[] = new String[topNode.size()];
		int i=0;
		List<Map> resultList = new ArrayList<Map>();
		for (Map<String, Object> map2 : topNode) {
			Map<String, Object> map = new HashMap<String, Object>();
				//id[i]=(map2.get("id")).toString();
				map.put("id", (map2.get("id")).toString());
				//name[i]=(String) map2.get("name");
				map.put("text", (String) map2.get("name"));
				resultList.add(map);
				i++;
		}
		return resultList;
	}
	
    
    
}
