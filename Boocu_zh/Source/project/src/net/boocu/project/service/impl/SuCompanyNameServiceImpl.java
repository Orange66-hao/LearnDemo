package net.boocu.project.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

import net.boocu.project.dao.SuCompanyNameDao;
import net.boocu.project.entity.SuCompanyNameEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.SuCompanyNameService;
import net.boocu.web.Filter;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("SuCompanyNameServiceImpl")
public class SuCompanyNameServiceImpl extends BaseServiceImpl<SuCompanyNameEntity, Long> implements SuCompanyNameService {
	@Resource(name = "suCompanyNameDaoImpl")
    private SuCompanyNameDao SuCompanyNameDao;
	/*@Resource
	private JdbcTemplate jdbcTemplate;*/
    
    @Resource(name = "suCompanyNameDaoImpl")
    public void setBaseDao(SuCompanyNameDao SuCompanyNameDao) {
    	super.setBaseDao(SuCompanyNameDao);
    }

	@Override
	@Transactional
	public SuCompanyNameEntity save(SuCompanyNameEntity entity) {
		/*SuCompanyNameEntity parentEntity = find(Long.parseLong(entity.getParentid())) ;
		parentEntity.setLeaf("0");
		update(parentEntity);*/
		return super.save(entity);
	}

	@Override
	@Transactional
	public void deleteList(Long... ids) {
		List<Long> childrenIds = new ArrayList<Long>();
		for(int i=0;i<ids.length;i++){
			SuCompanyNameEntity entity = find(ids[i]);
			if(entity != null){
				 List<SuCompanyNameEntity> children = findList(Filter.eq("parentid", entity.getId()));
				 for(SuCompanyNameEntity child : children){
					 childrenIds.add(child.getId());
				 }
				 if(children.size() > 0){
					 deleteList(childrenIds.toArray(new Long[childrenIds.size()]));
				 }
			}
			//遍历到最后一个时,判断父节点是否仍有子节点
			/*if(i == ids.length-1){
				List<SuCompanyNameEntity> leftChildren = findList(Filter.eq("parentid", entity.getMenuid()));
				if(leftChildren.size() == 0){
					SuCompanyNameEntity parent = find(Filter.eq("id", entity.getParentid()));
					parent.setLeaf("1");
					update(parent);
				}
			}*/
		}
		super.deleteList(ids);
	}

	@Override
	public List<SuCompanyNameEntity> getChildren(Long[] ids) {
		List<SuCompanyNameEntity> SuCompanyNameEntities = new ArrayList<SuCompanyNameEntity>();
		for(long id : ids){
			SuCompanyNameEntity SuSuCompanyNameEntity = find(id);
			if(SuSuCompanyNameEntity != null){
				SuCompanyNameEntities.add(SuSuCompanyNameEntity);
				SuCompanyNameEntities.addAll(getChildren(SuSuCompanyNameEntity.getId()));
			}
		}
		return SuCompanyNameEntities;
	}
	
	//递归取得子节点
	public List<SuCompanyNameEntity> getChildren(long id){
		List<SuCompanyNameEntity> SuCompanyNameEntities = new ArrayList<SuCompanyNameEntity>();
		List<SuCompanyNameEntity> children = findList(Filter.eq("parentid", id));
		if(children.size() != 0){
			SuCompanyNameEntities.addAll(children);
			for(SuCompanyNameEntity item : children){
				SuCompanyNameEntities.addAll(getChildren(item.getId()));
			}
		}
		return SuCompanyNameEntities;
	}

	@Override
	public List<Map<String, Object>> getParentIds(String[] SuCompanyName) {
		String param = "";
		for(int i = 0 ; i<SuCompanyName.length;i++ ){
			param +="'"+SuCompanyName[i]+"',";
		}
		// jdbcTemplate.queryForList(" select DISTINCT SUBSTR(ic.id,1,3) as parentId from jhj_industry_class ic where ic.name in ("+param+")");
		return SuCompanyNameDao.getParentIds(param.substring(0, param.length()-1));
	}

	@Override
	public List<Map<String, Object>> getNodeData2(String nameid) {
		return SuCompanyNameDao.getNodeData2(nameid);
	}

	@Override
	public List<Map<String, Object>> queryall(String id) {
		return SuCompanyNameDao.queryall(id);
	}

	@Override
	public List<Map<String, Object>> register(String name) {
		return SuCompanyNameDao.register(name);
	}
	
    
    
}
