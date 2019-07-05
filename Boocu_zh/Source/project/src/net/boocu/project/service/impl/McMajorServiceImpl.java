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
import net.boocu.project.dao.McMajorDao;
import net.boocu.project.entity.McMajorEntity;
import net.boocu.project.entity.McModelEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.McMajorService;
import net.boocu.web.Filter;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;


@Service("McMajorServiceImpl")
public class McMajorServiceImpl extends BaseServiceImpl<McMajorEntity, Long> implements McMajorService {
	@Resource(name = "mcMajorDaoImpl")
    private McMajorDao McMajorDao;
	/*@Resource
	private JdbcTemplate jdbcTemplate;*/
    
    @Resource(name = "mcMajorDaoImpl")
    public void setBaseDao(McMajorDao McMajorDao) {
    	super.setBaseDao(McMajorDao);
    }

	@Override
	@Transactional
	public McMajorEntity save(McMajorEntity entity) {
		/*McMajorEntity parentEntity = find(Long.parseLong(entity.getParentid())) ;
		parentEntity.setLeaf("0");
		update(parentEntity);*/
		return super.save(entity);
	}

	@Override
	@Transactional
	public void deleteList(Long... ids) {
		List<Long> childrenIds = new ArrayList<Long>();
		for(int i=0;i<ids.length;i++){
			McMajorEntity entity = find(ids[i]);
			if(entity != null){
				 List<McMajorEntity> children = findList(Filter.eq("parentid", entity.getId()));
				 for(McMajorEntity child : children){
					 childrenIds.add(child.getId());
				 }
				 if(children.size() > 0){
					 deleteList(childrenIds.toArray(new Long[childrenIds.size()]));
				 }
			}
			//遍历到最后一个时,判断父节点是否仍有子节点
			/*if(i == ids.length-1){
				List<McMajorEntity> leftChildren = findList(Filter.eq("parentid", entity.getMenuid()));
				if(leftChildren.size() == 0){
					McMajorEntity parent = find(Filter.eq("id", entity.getParentid()));
					parent.setLeaf("1");
					update(parent);
				}
			}*/
		}
		super.deleteList(ids);
	}

	@Override
	public List<McMajorEntity> getChildren(Long[] ids) {
		List<McMajorEntity> McMajorEntities = new ArrayList<McMajorEntity>();
		for(long id : ids){
			McMajorEntity McMcMajorEntity = find(id);
			if(McMcMajorEntity != null){
				McMajorEntities.add(McMcMajorEntity);
				McMajorEntities.addAll(getChildren(McMcMajorEntity.getId()));
			}
		}
		return McMajorEntities;
	}
	
	//递归取得子节点
	public List<McMajorEntity> getChildren(long id){
		List<McMajorEntity> McMajorEntities = new ArrayList<McMajorEntity>();
		List<McMajorEntity> children = findList(Filter.eq("parentid", id));
		if(children.size() != 0){
			McMajorEntities.addAll(children);
			for(McMajorEntity item : children){
				McMajorEntities.addAll(getChildren(item.getId()));
			}
		}
		return McMajorEntities;
	}

	@Override
	public List<Map<String, Object>> getParentIds(String[] McMajor) {
		String param = "";
		for(int i = 0 ; i<McMajor.length;i++ ){
			param +="'"+McMajor[i]+"',";
		}
		// jdbcTemplate.queryForList(" select DISTINCT SUBSTR(ic.id,1,3) as parentId from jhj_industry_class ic where ic.name in ("+param+")");
		return McMajorDao.getParentIds(param.substring(0, param.length()-1));
	}

	@Override
	public Map getNodeData2(String rootId) {
		List<Map<String, Object>> topNode =McMajorDao.getNodeData2(rootId);
			if (topNode == null) {
				return null;
			}
		String  id[] = new String[topNode.size()];
		String  name[] = new String[topNode.size()];
		int i=0;
		for (Map<String, Object> map : topNode) {
				id[i]=(map.get("id")).toString();
				name[i]=(String) map.get("name");
				i++;
		}
		Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", id);
			map.put("name", name);
		return map;
	}

	@Override
	public Map queryall(String id) {
		List<Map<String, Object>> topNode =McMajorDao.queryall(id);
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
	public List<Map> getMcModelGradeNames(HttpServletRequest reqeust, HttpServletResponse response, Model model,
			McModelEntity memberGradeEntity) {
		String mc_industry_id = ReqUtil.getString(reqeust, "mc_industry_id", "");
	    	List<Map<String, Object>> topNode =McMajorDao.getMcModelGradeNames(mc_industry_id);
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
				/*map.put("id", id);
				map.put("text", name);*/
		return resultList;
	}

	@Override
	public List<Map> getMcMajorGradeNames(HttpServletRequest reqeust, HttpServletResponse response, Model model,
			McMajorEntity memberGradeEntity) {
		String mc_industry_id = ReqUtil.getString(reqeust, "mc_industry_id", "");
    	List<Map<String, Object>> topNode ;
    	if (mc_industry_id != null && mc_industry_id != "") {
    		topNode =McMajorDao.queryallinid(mc_industry_id);
		}else{
			topNode =McMajorDao.queryallid();
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
