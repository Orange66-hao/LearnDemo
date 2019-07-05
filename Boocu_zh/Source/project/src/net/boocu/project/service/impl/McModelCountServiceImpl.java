package net.boocu.project.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

import net.boocu.project.dao.McModelCountDao;
import net.boocu.project.entity.McModelCountEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.McModelCountService;
import net.boocu.web.Filter;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("McModelCountServiceImpl")
public class McModelCountServiceImpl extends BaseServiceImpl<McModelCountEntity, Long> implements McModelCountService {
	@Resource(name = "mcModelCountDaoImpl")
    private McModelCountDao McModelCountDao;
	/*@Resource
	private JdbcTemplate jdbcTemplate;*/
    
    @Resource(name = "mcModelCountDaoImpl")
    public void setBaseDao(McModelCountDao McModelCountDao) {
    	//super.setBaseDao(McModelCountDao);
    }

	@Override
	@Transactional
	public McModelCountEntity save(McModelCountEntity entity) {
		/*McModelCountEntity parentEntity = find(Long.parseLong(entity.getParentid())) ;
		parentEntity.setLeaf("0");
		update(parentEntity);*/
		return super.save(entity);
	}

	@Override
	@Transactional
	public void deleteList(Long... ids) {
		List<Long> childrenIds = new ArrayList<Long>();
		for(int i=0;i<ids.length;i++){
			McModelCountEntity entity = find(ids[i]);
			if(entity != null){
				 List<McModelCountEntity> children = findList(Filter.eq("parentid", entity.getId()));
				 for(McModelCountEntity child : children){
					 childrenIds.add(child.getId());
				 }
				 if(children.size() > 0){
					 deleteList(childrenIds.toArray(new Long[childrenIds.size()]));
				 }
			}
			//遍历到最后一个时,判断父节点是否仍有子节点
			/*if(i == ids.length-1){
				List<McModelCountEntity> leftChildren = findList(Filter.eq("parentid", entity.getMenuid()));
				if(leftChildren.size() == 0){
					McModelCountEntity parent = find(Filter.eq("id", entity.getParentid()));
					parent.setLeaf("1");
					update(parent);
				}
			}*/
		}
		super.deleteList(ids);
	}

	@Override
	public List<McModelCountEntity> getChildren(Long[] ids) {
		List<McModelCountEntity> McModelCountEntities = new ArrayList<McModelCountEntity>();
		for(long id : ids){
			McModelCountEntity McMcModelCountEntity = find(id);
			if(McMcModelCountEntity != null){
				McModelCountEntities.add(McMcModelCountEntity);
				McModelCountEntities.addAll(getChildren(McMcModelCountEntity.getId()));
			}
		}
		return McModelCountEntities;
	}
	
	//递归取得子节点
	public List<McModelCountEntity> getChildren(long id){
		List<McModelCountEntity> McModelCountEntities = new ArrayList<McModelCountEntity>();
		List<McModelCountEntity> children = findList(Filter.eq("parentid", id));
		if(children.size() != 0){
			McModelCountEntities.addAll(children);
			for(McModelCountEntity item : children){
				McModelCountEntities.addAll(getChildren(item.getId()));
			}
		}
		return McModelCountEntities;
	}

	@Override
	public List<Map<String, Object>> getParentIds(String[] McModelCount) {
		String param = "";
		for(int i = 0 ; i<McModelCount.length;i++ ){
			param +="'"+McModelCount[i]+"',";
		}
		// jdbcTemplate.queryForList(" select DISTINCT SUBSTR(ic.id,1,3) as parentId from jhj_industry_class ic where ic.name in ("+param+")");
		return McModelCountDao.getParentIds(param.substring(0, param.length()-1));
	}
	
    
    
}
