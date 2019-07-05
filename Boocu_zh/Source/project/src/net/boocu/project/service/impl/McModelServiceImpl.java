package net.boocu.project.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

import net.boocu.project.dao.McModelDao;
import net.boocu.project.entity.McModelEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.McModelService;
import net.boocu.web.Filter;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("McModelServiceImpl")
public class McModelServiceImpl extends BaseServiceImpl<McModelEntity, Long> implements McModelService {
	@Resource(name = "mcModelDaoImpl")
    private McModelDao McModelDao;
	/*@Resource
	private JdbcTemplate jdbcTemplate;*/
    
    @Resource(name = "mcModelDaoImpl")
    public void setBaseDao(McModelDao McModelDao) {
    	super.setBaseDao(McModelDao);
    }

	@Override
	@Transactional
	public McModelEntity save(McModelEntity entity) {
		/*McModelEntity parentEntity = find(Long.parseLong(entity.getParentid())) ;
		parentEntity.setLeaf("0");
		update(parentEntity);*/
		return super.save(entity);
	}

	@Override
	@Transactional
	public void deleteList(Long... ids) {
		List<Long> childrenIds = new ArrayList<Long>();
		for(int i=0;i<ids.length;i++){
			McModelEntity entity = find(ids[i]);
			if(entity != null){
				 List<McModelEntity> children = findList(Filter.eq("parentid", entity.getId()));
				 for(McModelEntity child : children){
					 childrenIds.add(child.getId());
				 }
				 if(children.size() > 0){
					 deleteList(childrenIds.toArray(new Long[childrenIds.size()]));
				 }
			}
			//遍历到最后一个时,判断父节点是否仍有子节点
			/*if(i == ids.length-1){
				List<McModelEntity> leftChildren = findList(Filter.eq("parentid", entity.getMenuid()));
				if(leftChildren.size() == 0){
					McModelEntity parent = find(Filter.eq("id", entity.getParentid()));
					parent.setLeaf("1");
					update(parent);
				}
			}*/
		}
		super.deleteList(ids);
	}

	@Override
	public List<McModelEntity> getChildren(Long[] ids) {
		List<McModelEntity> McModelEntities = new ArrayList<McModelEntity>();
		for(long id : ids){
			McModelEntity McMcModelEntity = find(id);
			if(McMcModelEntity != null){
				McModelEntities.add(McMcModelEntity);
				McModelEntities.addAll(getChildren(McMcModelEntity.getId()));
			}
		}
		return McModelEntities;
	}
	
	//递归取得子节点
	public List<McModelEntity> getChildren(long id){
		List<McModelEntity> McModelEntities = new ArrayList<McModelEntity>();
		List<McModelEntity> children = findList(Filter.eq("parentid", id));
		if(children.size() != 0){
			McModelEntities.addAll(children);
			for(McModelEntity item : children){
				McModelEntities.addAll(getChildren(item.getId()));
			}
		}
		return McModelEntities;
	}

	@Override
	public List<Map<String, Object>> getParentIds(String[] McModel) {
		String param = "";
		for(int i = 0 ; i<McModel.length;i++ ){
			param +="'"+McModel[i]+"',";
		}
		// jdbcTemplate.queryForList(" select DISTINCT SUBSTR(ic.id,1,3) as parentId from jhj_industry_class ic where ic.name in ("+param+")");
		return McModelDao.getParentIds(param.substring(0, param.length()-1));
	}
	
    
    
}
