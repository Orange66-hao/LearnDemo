package net.boocu.project.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.util.ReqUtil;
import net.boocu.project.dao.McProductClassDao;
import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.McProductClassEntity;
import net.boocu.project.entity.ProducttypeEntity;
import net.boocu.project.service.McProductClassService;
import net.boocu.web.Filter;
import net.boocu.web.Sequencer;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

@Service("mcProductClassServiceImpl")
public class McProductClassServiceImpl extends BaseServiceImpl<McProductClassEntity, Long> implements McProductClassService {
	@Resource(name = "mcproductclassDaoImpl")
    private McProductClassDao mcproductclassDao;
    
    @Resource(name = "mcproductclassDaoImpl")
    public void setBaseDao(McProductClassDao productclassDao) {
        super.setBaseDao(productclassDao);
    }
    
	@Override
	@Transactional
	public void deleteList(Long... ids) {
		List<Long> childrenIds = new ArrayList<Long>();
		for(int i=0;i<ids.length;i++){
			McProductClassEntity entity = find(ids[i]);
			if(entity != null){
				 List<McProductClassEntity> children = findList(Filter.eq("parentid", entity.getMenuid()));
				 for(McProductClassEntity child : children){
					 childrenIds.add(child.getId());
				 }
				 if(children.size() > 0){
					 deleteList(childrenIds.toArray(new Long[childrenIds.size()]));
				 }
				//遍历到最后一个时,判断父节点是否仍有子节点
				if(i == ids.length-1){
					List<McProductClassEntity> leftChildren = findList(Filter.eq("parentid", entity.getMenuid()));
					if(leftChildren.size() == 0){
						McProductClassEntity parent = find(Filter.eq("menuid", entity.getParentid()));
						parent.setLeaf("1");
						update(parent);
					}
				}
			}
			
		}
		super.deleteList(ids);
	}

	@Override
	@Transactional
	public McProductClassEntity save(McProductClassEntity entity) {
		
		return super.save(entity);
		
	}
    
	/**
	 * 用来刷新产品分类的全名 
	 */
	@Transactional
	public void testFullName(){
		List<McProductClassEntity> list = findAll();
		for(McProductClassEntity item : list){
			String fullName = "/"+item.getName();
			McProductClassEntity parent =  find(Filter.eq("menuid", item.getParentid()));
			if(parent != null && parent.getId() != 1l){
				//fullName = fullName.concat("/"+parent.getName());
				fullName = "/"+parent.getName().concat(fullName);
				McProductClassEntity gradepa =  find(Filter.eq("menuid", parent.getParentid()));
				if(gradepa != null && gradepa.getId() != 1l){
					fullName = "/"+gradepa.getName().concat(fullName);
				}
			}
			item.setFullName(fullName);
			update(item);
		}
		
	}

	@Override
	public Map getNodeData2(String productclass_id) {
		List<Map<String, Object>> topNode =mcproductclassDao.getNodeData2(productclass_id);
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
	public Map queryall_pro(String id) {
		List<Map<String, Object>> topNode=null;
		if (id != null && !id.isEmpty()) {
			topNode =mcproductclassDao.queryall_pro(id);
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
	public List<Map> getMcProductClass(HttpServletRequest reqeust, HttpServletResponse response, Model model,
			McProductClassEntity memberGradeEntity) {
		String mc_major_id = ReqUtil.getString(reqeust, "mc_major_id", "");
    	String major_product_coll_type = ReqUtil.getString(reqeust, "major_product_coll_type", "");
    	List<Map<String, Object>> topNode;
    	if (mc_major_id != null && mc_major_id != "") {
    		topNode =mcproductclassDao.queryall_inid(mc_major_id);
		}
    	else if (major_product_coll_type.equals("onunselect")){
    		mc_major_id=null;
    		topNode =mcproductclassDao.queryall_inid(mc_major_id);
    	}
    	else{
			topNode =mcproductclassDao.queryall_id();
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
				/*map.put("id", id);
				map.put("text", name);*/
		return resultList;
	}
    
}
