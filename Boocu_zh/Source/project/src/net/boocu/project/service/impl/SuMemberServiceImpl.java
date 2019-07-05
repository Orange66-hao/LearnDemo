package net.boocu.project.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

import net.boocu.project.dao.SuMemberDao;
import net.boocu.project.entity.SuMemberEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.SuMemberService;
import net.boocu.web.Filter;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("SuMemberServiceImpl")
public class SuMemberServiceImpl extends BaseServiceImpl<SuMemberEntity, Long> implements SuMemberService {
	@Resource(name = "suMemberDaoImpl")
    private SuMemberDao suMemberDao;
	/*@Resource
	private JdbcTemplate jdbcTemplate;*/
    
    @Resource(name = "suMemberDaoImpl")
    public void setBaseDao(SuMemberDao SuMemberDao) {
    	super.setBaseDao(SuMemberDao);
    }

	@Override
	@Transactional
	public SuMemberEntity save(SuMemberEntity entity) {
		/*SuMemberEntity parentEntity = find(Long.parseLong(entity.getParentid())) ;
		parentEntity.setLeaf("0");
		update(parentEntity);*/
		return super.save(entity);
	}

	@Override
	@Transactional
	public void deleteList(Long... ids) {
		List<Long> childrenIds = new ArrayList<Long>();
		super.deleteList(ids);
	}

	@Override
	public List<SuMemberEntity> getChildren(Long[] ids) {
		List<SuMemberEntity> SuMemberEntities = new ArrayList<SuMemberEntity>();
		for(long id : ids){
			SuMemberEntity SuSuMemberEntity = find(id);
			if(SuSuMemberEntity != null){
				SuMemberEntities.add(SuSuMemberEntity);
				SuMemberEntities.addAll(getChildren(SuSuMemberEntity.getId()));
			}
		}
		return SuMemberEntities;
	}
	
	//递归取得子节点
	public List<SuMemberEntity> getChildren(long id){
		List<SuMemberEntity> SuMemberEntities = new ArrayList<SuMemberEntity>();
		List<SuMemberEntity> children = findList(Filter.eq("parentid", id));
		if(children.size() != 0){
			SuMemberEntities.addAll(children);
			for(SuMemberEntity item : children){
				SuMemberEntities.addAll(getChildren(item.getId()));
			}
		}
		return SuMemberEntities;
	}

	@Override
	public List<Map<String, Object>> getParentIds(String[] SuMember) {
		String param = "";
		for(int i = 0 ; i<SuMember.length;i++ ){
			param +="'"+SuMember[i]+"',";
		}
		// jdbcTemplate.queryForList(" select DISTINCT SUBSTR(ic.id,1,3) as parentId from jhj_industry_class ic where ic.name in ("+param+")");
		return suMemberDao.getParentIds(param.substring(0, param.length()-1));
	}

	@Override
	public Map getNodeData2(String rootId) {
		List<Map<String, Object>> topNode =suMemberDao.getNodeData2(rootId);
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
	public List<Map<String, Object>> register(String name) {
		return suMemberDao.register(name);
	}
	
    
    
}
