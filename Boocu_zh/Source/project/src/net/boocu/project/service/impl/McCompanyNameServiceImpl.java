package net.boocu.project.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

import net.boocu.project.dao.McCompanyNameDao;
import net.boocu.project.entity.McCompanyNameEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.McCompanyNameService;
import net.boocu.web.Filter;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("McCompanyNameServiceImpl")
public class McCompanyNameServiceImpl extends BaseServiceImpl<McCompanyNameEntity, Long> implements McCompanyNameService {
	@Resource(name = "mcCompanyNameDaoImpl")
    private McCompanyNameDao McCompanyNameDao;
	/*@Resource
	private JdbcTemplate jdbcTemplate;*/
    
    @Resource(name = "mcCompanyNameDaoImpl")
    public void setBaseDao(McCompanyNameDao McCompanyNameDao) {
    	super.setBaseDao(McCompanyNameDao);
    }

	@Override
	@Transactional
	public McCompanyNameEntity save(McCompanyNameEntity entity) {
		/*McCompanyNameEntity parentEntity = find(Long.parseLong(entity.getParentid())) ;
		parentEntity.setLeaf("0");
		update(parentEntity);*/
		return super.save(entity);
	}

	@Override
	@Transactional
	public void deleteList(Long... ids) {
		List<Long> childrenIds = new ArrayList<Long>();
		for(int i=0;i<ids.length;i++){
			McCompanyNameEntity entity = find(ids[i]);
			if(entity != null){
				 List<McCompanyNameEntity> children = findList(Filter.eq("parentid", entity.getId()));
				 for(McCompanyNameEntity child : children){
					 childrenIds.add(child.getId());
				 }
				 if(children.size() > 0){
					 deleteList(childrenIds.toArray(new Long[childrenIds.size()]));
				 }
			}
			//遍历到最后一个时,判断父节点是否仍有子节点
			/*if(i == ids.length-1){
				List<McCompanyNameEntity> leftChildren = findList(Filter.eq("parentid", entity.getMenuid()));
				if(leftChildren.size() == 0){
					McCompanyNameEntity parent = find(Filter.eq("id", entity.getParentid()));
					parent.setLeaf("1");
					update(parent);
				}
			}*/
		}
		super.deleteList(ids);
	}

	@Override
	public List<McCompanyNameEntity> getChildren(Long[] ids) {
		List<McCompanyNameEntity> McCompanyNameEntities = new ArrayList<McCompanyNameEntity>();
		for(long id : ids){
			McCompanyNameEntity McMcCompanyNameEntity = find(id);
			if(McMcCompanyNameEntity != null){
				McCompanyNameEntities.add(McMcCompanyNameEntity);
				McCompanyNameEntities.addAll(getChildren(McMcCompanyNameEntity.getId()));
			}
		}
		return McCompanyNameEntities;
	}
	
	//递归取得子节点
	public List<McCompanyNameEntity> getChildren(long id){
		List<McCompanyNameEntity> McCompanyNameEntities = new ArrayList<McCompanyNameEntity>();
		List<McCompanyNameEntity> children = findList(Filter.eq("parentid", id));
		if(children.size() != 0){
			McCompanyNameEntities.addAll(children);
			for(McCompanyNameEntity item : children){
				McCompanyNameEntities.addAll(getChildren(item.getId()));
			}
		}
		return McCompanyNameEntities;
	}

	@Override
	public List<Map<String, Object>> getParentIds(String[] McCompanyName) {
		String param = "";
		for(int i = 0 ; i<McCompanyName.length;i++ ){
			param +="'"+McCompanyName[i]+"',";
		}
		// jdbcTemplate.queryForList(" select DISTINCT SUBSTR(ic.id,1,3) as parentId from jhj_industry_class ic where ic.name in ("+param+")");
		return McCompanyNameDao.getParentIds(param.substring(0, param.length()-1));
	}

	@Override
	public List<Map<String, Object>> quarymccompanyname(String id) {
		return McCompanyNameDao.quarymccompanyname(id);
	}

	@Override
	public List<Map<String, Object>> registermccompanyname(String name) {
		return McCompanyNameDao.registermccompanyname(name);
	}

	@Override
	public List<Map<String, Object>> queryallids(String id) {
		return McCompanyNameDao.queryallids(id);
	}
	
    
    
}
