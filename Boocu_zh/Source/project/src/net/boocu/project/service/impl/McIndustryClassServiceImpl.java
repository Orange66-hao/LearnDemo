package net.boocu.project.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

import net.boocu.project.dao.McIndustryClassDao;
import net.boocu.project.entity.McIndustryClassEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.McIndustryClassService;
import net.boocu.web.Filter;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("McIndustryClassServiceImpl")
public class McIndustryClassServiceImpl extends BaseServiceImpl<McIndustryClassEntity, Long> implements McIndustryClassService {
	@Resource(name = "mcIndustryClassDaoImpl")
    private McIndustryClassDao McIndustryClassDao;
	/*@Resource
	private JdbcTemplate jdbcTemplate;*/
    
    @Resource(name = "mcIndustryClassDaoImpl")
    public void setBaseDao(McIndustryClassDao McIndustryClassDao) {
    	super.setBaseDao(McIndustryClassDao);
    }

	@Override
	@Transactional
	public McIndustryClassEntity save(McIndustryClassEntity entity) {
		/*McIndustryClassEntity parentEntity = find(Long.parseLong(entity.getParentid())) ;
		parentEntity.setLeaf("0");
		update(parentEntity);*/
		return super.save(entity);
	}

	@Override
	@Transactional
	public void deleteList(Long... ids) {
		List<Long> childrenIds = new ArrayList<Long>();
		for(int i=0;i<ids.length;i++){
			McIndustryClassEntity entity = find(ids[i]);
			if(entity != null){
				 List<McIndustryClassEntity> children = findList(Filter.eq("parentid", entity.getId()));
				 for(McIndustryClassEntity child : children){
					 childrenIds.add(child.getId());
				 }
				 if(children.size() > 0){
					 deleteList(childrenIds.toArray(new Long[childrenIds.size()]));
				 }
			}
			//遍历到最后一个时,判断父节点是否仍有子节点
			if(i == ids.length-1){
				List<McIndustryClassEntity> leftChildren = findList(Filter.eq("parentid", entity.getMenuid()));
				if(leftChildren.size() == 0){
					McIndustryClassEntity parent = find(Filter.eq("id", entity.getParentid()));
					parent.setLeaf("1");
					update(parent);
				}
			}
		}
		super.deleteList(ids);
	}

	@Override
	public List<McIndustryClassEntity> getChildren(Long[] ids) {
		List<McIndustryClassEntity> McIndustryClassEntities = new ArrayList<McIndustryClassEntity>();
		for(long id : ids){
			McIndustryClassEntity McMcIndustryClassEntity = find(id);
			if(McMcIndustryClassEntity != null){
				McIndustryClassEntities.add(McMcIndustryClassEntity);
				McIndustryClassEntities.addAll(getChildren(McMcIndustryClassEntity.getId()));
			}
		}
		return McIndustryClassEntities;
	}
	
	//递归取得子节点
	public List<McIndustryClassEntity> getChildren(long id){
		List<McIndustryClassEntity> McIndustryClassEntities = new ArrayList<McIndustryClassEntity>();
		List<McIndustryClassEntity> children = findList(Filter.eq("parentid", id));
		if(children.size() != 0){
			McIndustryClassEntities.addAll(children);
			for(McIndustryClassEntity item : children){
				McIndustryClassEntities.addAll(getChildren(item.getId()));
			}
		}
		return McIndustryClassEntities;
	}

	@Override
	public List<Map<String, Object>> getParentIds(String[] McIndustryClass) {
		String param = "";
		for(int i = 0 ; i<McIndustryClass.length;i++ ){
			param +="'"+McIndustryClass[i]+"',";
		}
		// jdbcTemplate.queryForList(" select DISTINCT SUBSTR(ic.id,1,3) as parentId from jhj_industry_class ic where ic.name in ("+param+")");
		return McIndustryClassDao.getParentIds(param.substring(0, param.length()-1));
	}
	
    
    
}
