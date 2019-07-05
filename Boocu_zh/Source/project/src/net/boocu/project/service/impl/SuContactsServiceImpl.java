package net.boocu.project.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

import net.boocu.project.dao.SuContactsDao;
import net.boocu.project.entity.SuContactsEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.SuContactsService;
import net.boocu.web.Filter;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("SuContactsServiceImpl")
public class SuContactsServiceImpl extends BaseServiceImpl<SuContactsEntity, Long> implements SuContactsService {
	@Resource(name = "suContactsDaoImpl")
    private SuContactsDao SuContactsDao;
	/*@Resource
	private JdbcTemplate jdbcTemplate;*/
    
    @Resource(name = "suContactsDaoImpl")
    public void setBaseDao(SuContactsDao SuContactsDao) {
    	super.setBaseDao(SuContactsDao);
    }

	@Override
	@Transactional
	public SuContactsEntity save(SuContactsEntity entity) {
		/*SuContactsEntity parentEntity = find(Long.parseLong(entity.getParentid())) ;
		parentEntity.setLeaf("0");
		update(parentEntity);*/
		return super.save(entity);
	}

	@Override
	@Transactional
	public void deleteList(Long... ids) {
		List<Long> childrenIds = new ArrayList<Long>();
		for(int i=0;i<ids.length;i++){
			SuContactsEntity entity = find(ids[i]);
			if(entity != null){
				 List<SuContactsEntity> children = findList(Filter.eq("parentid", entity.getId()));
				 for(SuContactsEntity child : children){
					 childrenIds.add(child.getId());
				 }
				 if(children.size() > 0){
					 deleteList(childrenIds.toArray(new Long[childrenIds.size()]));
				 }
			}
			//遍历到最后一个时,判断父节点是否仍有子节点
			/*if(i == ids.length-1){
				List<SuContactsEntity> leftChildren = findList(Filter.eq("parentid", entity.getMenuid()));
				if(leftChildren.size() == 0){
					SuContactsEntity parent = find(Filter.eq("id", entity.getParentid()));
					parent.setLeaf("1");
					update(parent);
				}
			}*/
		}
		super.deleteList(ids);
	}

	@Override
	public List<SuContactsEntity> getChildren(Long[] ids) {
		List<SuContactsEntity> SuContactsEntities = new ArrayList<SuContactsEntity>();
		for(long id : ids){
			SuContactsEntity SuSuContactsEntity = find(id);
			if(SuSuContactsEntity != null){
				SuContactsEntities.add(SuSuContactsEntity);
				SuContactsEntities.addAll(getChildren(SuSuContactsEntity.getId()));
			}
		}
		return SuContactsEntities;
	}
	
	//递归取得子节点
	public List<SuContactsEntity> getChildren(long id){
		List<SuContactsEntity> SuContactsEntities = new ArrayList<SuContactsEntity>();
		List<SuContactsEntity> children = findList(Filter.eq("parentid", id));
		if(children.size() != 0){
			SuContactsEntities.addAll(children);
			for(SuContactsEntity item : children){
				SuContactsEntities.addAll(getChildren(item.getId()));
			}
		}
		return SuContactsEntities;
	}

	@Override
	public List<Map<String, Object>> getParentIds(String[] SuContacts) {
		String param = "";
		for(int i = 0 ; i<SuContacts.length;i++ ){
			param +="'"+SuContacts[i]+"',";
		}
		// jdbcTemplate.queryForList(" select DISTINCT SUBSTR(ic.id,1,3) as parentId from jhj_industry_class ic where ic.name in ("+param+")");
		return SuContactsDao.getParentIds(param.substring(0, param.length()-1));
	}

	@Override
	public List<Map<String, Object>> getNodeData2(String rootId) {
		return SuContactsDao.getNodeData2(rootId);
	}

	@Override
	public List<Map<String, Object>> queryall(String id) {
		return SuContactsDao.queryall(id);
	}
	
    
    
}
