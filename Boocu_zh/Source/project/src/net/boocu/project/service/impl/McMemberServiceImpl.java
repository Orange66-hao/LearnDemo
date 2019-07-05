package net.boocu.project.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.boocu.project.dao.McMemberDao;
import net.boocu.project.entity.McMemberEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.McMemberService;
import net.boocu.web.Filter;
import net.boocu.web.Message;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("McMemberServiceImpl")
public class McMemberServiceImpl extends BaseServiceImpl<McMemberEntity, Long> implements McMemberService {
	@Resource(name = "mcMemberDaoImpl")
    private McMemberDao McMemberDao;
	
	@Resource
	private JdbcTemplate JdbcTemplate;
	/*@Resource
	private JdbcTemplate jdbcTemplate;*/
    
    @Resource(name = "mcMemberDaoImpl")
    public void setBaseDao(McMemberDao McMemberDao) {
    	super.setBaseDao(McMemberDao);
    }

	@Override
	@Transactional
	public McMemberEntity save(McMemberEntity entity) {
		/*McMemberEntity parentEntity = find(Long.parseLong(entity.getParentid())) ;
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
	public List<McMemberEntity> getChildren(Long[] ids) {
		List<McMemberEntity> McMemberEntities = new ArrayList<McMemberEntity>();
		for(long id : ids){
			McMemberEntity McMcMemberEntity = find(id);
			if(McMcMemberEntity != null){
				McMemberEntities.add(McMcMemberEntity);
				McMemberEntities.addAll(getChildren(McMcMemberEntity.getId()));
			}
		}
		return McMemberEntities;
	}
	
	//递归取得子节点
	public List<McMemberEntity> getChildren(long id){
		List<McMemberEntity> McMemberEntities = new ArrayList<McMemberEntity>();
		List<McMemberEntity> children = findList(Filter.eq("parentid", id));
		if(children.size() != 0){
			McMemberEntities.addAll(children);
			for(McMemberEntity item : children){
				McMemberEntities.addAll(getChildren(item.getId()));
			}
		}
		return McMemberEntities;
	}

	@Override
	public List<Map<String, Object>> getParentIds(String[] McMember) {
		String param = "";
		for(int i = 0 ; i<McMember.length;i++ ){
			param +="'"+McMember[i]+"',";
		}
		// jdbcTemplate.queryForList(" select DISTINCT SUBSTR(ic.id,1,3) as parentId from jhj_industry_class ic where ic.name in ("+param+")");
		return McMemberDao.getParentIds(param.substring(0, param.length()-1));
	}

	@Override
	public List<Map<String, Object>> register(String edit_username, String username, HttpServletRequest request, HttpSession session) {
		return McMemberDao.register(edit_username, username, request, session);
	}

	@Override
	public List<Map<String, Object>> getNodeData2(String rootId) {
		return McMemberDao.getNodeData2(rootId);
	}

	@Override
	public List<Map<String, Object>> quaryblame(String blame_id) {
		return McMemberDao.quaryblame(blame_id);
	}
	
    
    
}
