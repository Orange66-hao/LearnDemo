package net.boocu.project.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

import net.boocu.project.dao.SuModelCountDao;
import net.boocu.project.entity.SuModelCountEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.SuModelCountService;
import net.boocu.web.Filter;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("SuModelCountServiceImpl")
public class SuModelCountServiceImpl extends BaseServiceImpl<SuModelCountEntity, Long> implements SuModelCountService {
	@Resource(name = "suModelCountDaoImpl")
    private SuModelCountDao SuModelCountDao;
	/*@Resource
	private JdbcTemplate jdbcTemplate;*/
    
    @Resource(name = "suModelCountDaoImpl")
    public void setBaseDao(SuModelCountDao SuModelCountDao) {
    	//super.setBaseDao(SuModelCountDao);
    }

	@Override
	@Transactional
	public SuModelCountEntity save(SuModelCountEntity entity) {
		/*SuModelCountEntity parentEntity = find(Long.parseLong(entity.getParentid())) ;
		parentEntity.setLeaf("0");
		update(parentEntity);*/
		return super.save(entity);
	}

	@Override
	@Transactional
	public void deleteList(Long... ids) {
		List<Long> childrenIds = new ArrayList<Long>();
		for(int i=0;i<ids.length;i++){
			SuModelCountEntity entity = find(ids[i]);
			if(entity != null){
				 List<SuModelCountEntity> children = findList(Filter.eq("parentid", entity.getId()));
				 for(SuModelCountEntity child : children){
					 childrenIds.add(child.getId());
				 }
				 if(children.size() > 0){
					 deleteList(childrenIds.toArray(new Long[childrenIds.size()]));
				 }
			}
			//遍历到最后一个时,判断父节点是否仍有子节点
			/*if(i == ids.length-1){
				List<SuModelCountEntity> leftChildren = findList(Filter.eq("parentid", entity.getMenuid()));
				if(leftChildren.size() == 0){
					SuModelCountEntity parent = find(Filter.eq("id", entity.getParentid()));
					parent.setLeaf("1");
					update(parent);
				}
			}*/
		}
		super.deleteList(ids);
	}

	@Override
	public List<SuModelCountEntity> getChildren(Long[] ids) {
		List<SuModelCountEntity> SuModelCountEntities = new ArrayList<SuModelCountEntity>();
		for(long id : ids){
			SuModelCountEntity SuSuModelCountEntity = find(id);
			if(SuSuModelCountEntity != null){
				SuModelCountEntities.add(SuSuModelCountEntity);
				SuModelCountEntities.addAll(getChildren(SuSuModelCountEntity.getId()));
			}
		}
		return SuModelCountEntities;
	}
	
	//递归取得子节点
	public List<SuModelCountEntity> getChildren(long id){
		List<SuModelCountEntity> SuModelCountEntities = new ArrayList<SuModelCountEntity>();
		List<SuModelCountEntity> children = findList(Filter.eq("parentid", id));
		if(children.size() != 0){
			SuModelCountEntities.addAll(children);
			for(SuModelCountEntity item : children){
				SuModelCountEntities.addAll(getChildren(item.getId()));
			}
		}
		return SuModelCountEntities;
	}

	@Override
	public List<Map<String, Object>> getParentIds(String[] SuModelCount) {
		String param = "";
		for(int i = 0 ; i<SuModelCount.length;i++ ){
			param +="'"+SuModelCount[i]+"',";
		}
		// jdbcTemplate.queryForList(" select DISTINCT SUBSTR(ic.id,1,3) as parentId from jhj_industry_class ic where ic.name in ("+param+")");
		return SuModelCountDao.getParentIds(param.substring(0, param.length()-1));
	}
	
    
    
}
