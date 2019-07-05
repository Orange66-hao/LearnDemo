package net.boocu.project.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

import net.boocu.project.dao.McContactsDao;
import net.boocu.project.entity.McContactsEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.McContactsService;
import net.boocu.web.Filter;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("McContactsServiceImpl")
public class McContactsServiceImpl extends BaseServiceImpl<McContactsEntity, Long> implements McContactsService {
	@Resource(name = "mcContactsDaoImpl")
    private McContactsDao McContactsDao;
	/*@Resource
	private JdbcTemplate jdbcTemplate;*/
    
    @Resource(name = "mcContactsDaoImpl")
    public void setBaseDao(McContactsDao McContactsDao) {
    	super.setBaseDao(McContactsDao);
    }

	@Override
	@Transactional
	public McContactsEntity save(McContactsEntity entity) {
		/*McContactsEntity parentEntity = find(Long.parseLong(entity.getParentid())) ;
		parentEntity.setLeaf("0");
		update(parentEntity);*/
        McContactsDao.persist(entity);
		return entity;
	}

	@Override
	@Transactional
	public void deleteList(Long... ids) {
		List<Long> childrenIds = new ArrayList<Long>();
		for(int i=0;i<ids.length;i++){
			McContactsEntity entity = find(ids[i]);
			if(entity != null){
				 List<McContactsEntity> children = findList(Filter.eq("parentid", entity.getId()));
				 for(McContactsEntity child : children){
					 childrenIds.add(child.getId());
				 }
				 if(children.size() > 0){
					 deleteList(childrenIds.toArray(new Long[childrenIds.size()]));
				 }
			}
			//遍历到最后一个时,判断父节点是否仍有子节点
			/*if(i == ids.length-1){
				List<McContactsEntity> leftChildren = findList(Filter.eq("parentid", entity.getMenuid()));
				if(leftChildren.size() == 0){
					McContactsEntity parent = find(Filter.eq("id", entity.getParentid()));
					parent.setLeaf("1");
					update(parent);
				}
			}*/
		}
		super.deleteList(ids);
	}

	@Override
	public List<McContactsEntity> getChildren(Long[] ids) {
		List<McContactsEntity> McContactsEntities = new ArrayList<McContactsEntity>();
		for(long id : ids){
			McContactsEntity McMcContactsEntity = find(id);
			if(McMcContactsEntity != null){
				McContactsEntities.add(McMcContactsEntity);
				McContactsEntities.addAll(getChildren(McMcContactsEntity.getId()));
			}
		}
		return McContactsEntities;
	}
	
	//递归取得子节点
	public List<McContactsEntity> getChildren(long id){
		List<McContactsEntity> McContactsEntities = new ArrayList<McContactsEntity>();
		List<McContactsEntity> children = findList(Filter.eq("parentid", id));
		if(children.size() != 0){
			McContactsEntities.addAll(children);
			for(McContactsEntity item : children){
				McContactsEntities.addAll(getChildren(item.getId()));
			}
		}
		return McContactsEntities;
	}

	@Override
	public List<Map<String, Object>> getParentIds(String[] McContacts) {
		String param = "";
		for(int i = 0 ; i<McContacts.length;i++ ){
			param +="'"+McContacts[i]+"',";
		}
		// jdbcTemplate.queryForList(" select DISTINCT SUBSTR(ic.id,1,3) as parentId from jhj_industry_class ic where ic.name in ("+param+")");
		return McContactsDao.getParentIds(param.substring(0, param.length()-1));
	}

	@Override
	public List<Map<String, Object>> quarycontacts(String id) {
		return McContactsDao.quarycontacts(id);
	}
	
    
    
}
