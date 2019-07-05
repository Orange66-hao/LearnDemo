package net.boocu.project.service.impl;

import net.boocu.project.dao.McBrandAndModelDao;
import net.boocu.project.dao.McModelDao;
import net.boocu.project.entity.McBrandAndModelEntity;
import net.boocu.project.entity.McModelEntity;
import net.boocu.project.service.McBrandAndModelService;
import net.boocu.project.service.McModelService;
import net.boocu.web.Filter;
import net.boocu.web.Message;
import net.boocu.web.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service("McBrandAndModelServiceImpl")
public class McBrandAndModelServiceImpl extends BaseServiceImpl<McBrandAndModelEntity, Long> implements McBrandAndModelService {
	@Resource(name = "mcBrandAndModelDaoImpl")
    private McBrandAndModelDao McBrandAndModelDao;
	/*@Resource
	private JdbcTemplate jdbcTemplate;*/
    
    @Resource(name = "mcBrandAndModelDaoImpl")
    public void setBaseDao(McBrandAndModelDao McBrandAndModelDao) {
    	super.setBaseDao(McBrandAndModelDao);
    }

	@Override
	@Transactional
	public McBrandAndModelEntity save(McBrandAndModelEntity entity) {
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
			McBrandAndModelEntity entity = find(ids[i]);
			if(entity != null){
				 List<McBrandAndModelEntity> children = findList(Filter.eq("parentid", entity.getId()));
				 for(McBrandAndModelEntity child : children){
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
	public List<McBrandAndModelEntity> getChildren(Long[] ids) {
		List<McBrandAndModelEntity> McBrandAndModelEntities = new ArrayList<McBrandAndModelEntity>();
		for(long id : ids){
			McBrandAndModelEntity McBrandAndModelEntity = find(id);
			if(McBrandAndModelEntity != null){
				McBrandAndModelEntities.add(McBrandAndModelEntity);
				McBrandAndModelEntities.addAll(getChildren(McBrandAndModelEntity.getId()));
			}
		}
		return McBrandAndModelEntities;
	}
	
	//递归取得子节点
	public List<McBrandAndModelEntity> getChildren(long id){
		List<McBrandAndModelEntity> McBrandAndModelEntities = new ArrayList<McBrandAndModelEntity>();
		List<McBrandAndModelEntity> children = findList(Filter.eq("parentid", id));
		if(children.size() != 0){
			McBrandAndModelEntities.addAll(children);
			for(McBrandAndModelEntity item : children){
				McBrandAndModelEntities.addAll(getChildren(item.getId()));
			}
		}
		return McBrandAndModelEntities;
	}

	@Override
	public List<Map<String, Object>> getParentIds(String[] McModel) {
		String param = "";
		for(int i = 0 ; i<McModel.length;i++ ){
			param +="'"+McModel[i]+"',";
		}
		// jdbcTemplate.queryForList(" select DISTINCT SUBSTR(ic.id,1,3) as parentId from jhj_industry_class ic where ic.name in ("+param+")");
		return McBrandAndModelDao.getParentIds(param.substring(0, param.length()-1));
	}

	@Override
	public Message addBrandAndModel(String brand, String model, String mc_productclass_name, String mc_company) {
		List<Map<String, Object>> row = McBrandAndModelDao.addBrandAndModel(brand,model,mc_productclass_name,mc_company);
		if (row.size() > 0) {
			return Message.success("false");
		}
		return Message.success("true");
	}

	@Override
	public List<Map<String, Object>> quaryMcBrandAndModel(String id) {
		return McBrandAndModelDao.quaryMcBrandAndModel(id);
	}
}
