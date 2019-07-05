package net.boocu.project.service.impl;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.project.dao.*;
import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.IndustryClassModelEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.PbrandAndModelService;
import net.boocu.project.service.ProductclassService;
import net.boocu.web.Filter;
import net.boocu.web.service.impl.BaseServiceImpl;

import nl.siegmann.epublib.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;


@Service("industryClassServiceImpl")
public class IndustryClassServiceImpl extends BaseServiceImpl<IndustryClassEntity, Long> implements IndustryClassService {
	@Resource(name = "industryClassDaoImpl")
    private IndustryClassDao industryClassDao;
	@Autowired
	private ProductclassDao productclassDao;
	@Autowired
	private PbrandAndModelDao pbrandAndModelDao;

	@Autowired
	private PbrandAndModelService pbrandAndModelService;
	/*@Resource
	private JdbcTemplate jdbcTemplate;*/
    @Autowired
	private ProductclassService productclassService;
    @Resource(name = "industryClassDaoImpl")
    public void setBaseDao(IndustryClassDao industryClassDao) {
    	super.setBaseDao(industryClassDao);
    }

	@Override
	@Transactional
	public IndustryClassEntity save(IndustryClassEntity entity) {
		IndustryClassEntity parentEntity = find(Long.parseLong(entity.getParentid())) ;
		parentEntity.setLeaf("0");
		update(parentEntity);
		return super.save(entity);
	}

	@Override
	@Transactional
	public void deleteList(Long... ids) {
		List<Long> childrenIds = new ArrayList<Long>();
		for(int i=0;i<ids.length;i++){
			IndustryClassEntity entity = find(ids[i]);
			if(entity != null){
				 List<IndustryClassEntity> children = findList(Filter.eq("parentid", entity.getId()));
				 for(IndustryClassEntity child : children){
					 childrenIds.add(child.getId());
				 }
				 if(children.size() > 0){
					deleteList(childrenIds.toArray(new Long[childrenIds.size()]));
				}
			}
			//遍历到最后一个时,判断父节点是否仍有子节点
			if(i == ids.length-1){
				List<IndustryClassEntity> leftChildren = findList(Filter.eq("parentid", entity.getMenuid()));
				if(leftChildren.size() == 0){
					IndustryClassEntity parent = find(Filter.eq("id", entity.getParentid()));
					parent.setLeaf("1");
					update(parent);
				}
			}
		}
		super.deleteList(ids);
	}

	@Override
	public List<IndustryClassEntity> getChildren(Long[] ids) {
		List<IndustryClassEntity> industryClassEntities = new ArrayList<IndustryClassEntity>();
		for(long id : ids){
			IndustryClassEntity industryClassEntity = find(id);
			if(industryClassEntity != null){
				industryClassEntities.add(industryClassEntity);
				industryClassEntities.addAll(getChildren(industryClassEntity.getId()));
			}
		}
		return industryClassEntities;
	}
	
	//递归取得子节点
	public List<IndustryClassEntity> getChildren(long id){
		List<IndustryClassEntity> industryClassEntities = new ArrayList<IndustryClassEntity>();
		List<IndustryClassEntity> children = findList(Filter.eq("parentid", id));
		if(children.size() != 0){
			industryClassEntities.addAll(children);
			for(IndustryClassEntity item : children){
				industryClassEntities.addAll(getChildren(item.getId()));
			}
		}
		return industryClassEntities;
	}

	@Override
	public List<Map<String, Object>> getParentIds(String[] industryClass) {
		String param = "";
		for(int i = 0 ; i<industryClass.length;i++ ){
			param +="'"+industryClass[i]+"',";
		}
		// jdbcTemplate.queryForList(" select DISTINCT SUBSTR(ic.id,1,3) as parentId from jhj_industry_class ic where ic.name in ("+param+")");
		return industryClassDao.getParentIds(param.substring(0, param.length()-1));
	}

	@Override
	public List<Map<String, Object>> queryrows(HttpServletRequest request, HttpServletResponse response, Model model) {
		return industryClassDao.queryrows( request,  response,  model);
	}

	@Override
	public List<Map> query(HttpServletRequest request, HttpServletResponse response, Model model) {
		//仪器分类数据 productClass
		List<Map<String, Object>> queryrows = industryClassDao.query(request, response, model);
		List<Map> resultList2 = new ArrayList<>();
		for (Map<String, Object> entity : queryrows) {

			Map<String, Object> item = new HashMap<String, Object>();
			ProductclassEntity productclassEntity1=new ProductclassEntity();
			if (entity.get("p_id") != null && entity.get("p_id") != "") {
				item.put("p_id", entity.get("p_id"));
			}

			if (entity.get("p_name") != null && entity.get("p_name") != "") {
				item.put("p_name", entity.get("p_name"));
			}
			if (entity.get("p_modify_date") != null && entity.get("p_modify_date") != "") {
				item.put("p_modify_date", entity.get("p_modify_date"));
			}

			if (entity.get("p_id") != null && entity.get("p_id") != "") {
				ProductclassEntity productclassEntity = productclassDao.find(Long.parseLong(entity.get("p_id").toString()));
				Set<IndustryClassEntity> industryClassEntities = productclassEntity.getIndustryClassEntities();
				Iterator<IndustryClassEntity> iterator = industryClassEntities.iterator();
				List<Map> indulist=new ArrayList<>();//仪器集合
				List<Map> pbmlist=new ArrayList<>();//品牌型号集合
				while(iterator.hasNext()){
					//封装仪器下的产品
					Map m=new HashMap();
					IndustryClassEntity next = iterator.next();
					m.put("i_id",next.getId());
					m.put("i_name",next.getName());
					indulist.add(m);
					//封装仪器下面的品牌型号
					List<Map<String, Object>> list = pbrandAndModelDao.findListByIndustryClassAndProductclass(entity.get("p_id").toString(),m.get("i_id").toString());
					if(list!=null){
						for (Map<String, Object> map : list) {
							Map bm=new HashMap();
							bm.put("b_id",map.get("id"));
							bm.put("b_model",map.get("model"));
							pbmlist.add(bm);
						}
					}
				}
				item.put("pbmlist",pbmlist);
				item.put("indulist",indulist);
			}
			resultList2.add(item);
		}
		return resultList2;
	}




}
