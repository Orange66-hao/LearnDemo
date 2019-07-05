package net.boocu.project.service.impl;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.project.dao.IndustryClassDao;
import net.boocu.project.dao.ProductclassDao;
import net.boocu.project.entity.BrandAndModelEntity;
import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.entity.ProducttypeEntity;
import net.boocu.project.service.BrandAndModelService;
import net.boocu.project.service.ProductclassService;
import net.boocu.web.Filter;
import net.boocu.web.Sequencer;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

@Service("productclassServiceImpl")
public class ProductclassServiceImpl extends BaseServiceImpl<ProductclassEntity, Long> implements ProductclassService {
	@Resource(name = "productclassDaoImpl")
    private ProductclassDao productclassDao;
    @Autowired
	private BrandAndModelService brandAndModelService;


	@Autowired
	private IndustryClassDao industryClassDao;

    @Resource(name = "productclassDaoImpl")
    public void setBaseDao(ProductclassDao productclassDao) {
        super.setBaseDao(productclassDao);
    }
    
	@Override
	@Transactional
	public void deleteList(Long... ids) {
		List<Long> childrenIds = new ArrayList<Long>();
		for(int i=0;i<ids.length;i++){
			ProductclassEntity entity = find(ids[i]);
			if(entity != null){
				 List<ProductclassEntity> children = findList(Filter.eq("parentid", entity.getMenuid()));
				 for(ProductclassEntity child : children){
					 childrenIds.add(child.getId());
				 }
				 if(children.size() > 0){
					 deleteList(childrenIds.toArray(new Long[childrenIds.size()]));
				 }
				//遍历到最后一个时,判断父节点是否仍有子节点
				if(i == ids.length-1){
					List<ProductclassEntity> leftChildren = findList(Filter.eq("parentid", entity.getMenuid()));
					if(leftChildren.size() == 0){
						ProductclassEntity parent = find(Filter.eq("menuid", entity.getParentid()));
						parent.setLeaf("1");
						update(parent);
					}
				}
			}
			
		}
		super.deleteList(ids);
	}

	@Override
	@Transactional
	public ProductclassEntity save(ProductclassEntity entity) {
/*		//更改menuid
		List<Filter> flist = new ArrayList<Filter>();
		flist.add(Filter.eq("parentid", entity.getParentid()));
		String menuid = entity.getParentid();
		System.out.println("parentid:"+menuid);
		List<ProductclassEntity> brotherNodes = findList(flist,Sequencer.desc("menuid"));
		String largestMenu = "";
		if(brotherNodes.size() != 0){
			largestMenu =brotherNodes.get(0).getMenuid();
			int no = Integer.parseInt(largestMenu.substring(largestMenu.length()-2))+1;
			if(no < 10){
				menuid = menuid +"0"+no;
			}else{
				menuid = menuid +no;
			}
		}else{
			menuid = menuid+"01";
		}
		 
		//将上级leaf设置为0
		System.out.println("menuid:"+entity.getParentid());
		ProductclassEntity parentEntity = find(Filter.eq("menuid", entity.getParentid()));
		
		if(parentEntity != null){
			parentEntity.setLeaf("0");
			update(parentEntity);
		}
		entity.setMenuid(menuid);
		return super.save(entity);*/
		
		//上级
		ProductclassEntity productclassEntity = find(Long.parseLong(entity.getParentid()));
		String menuid = productclassEntity.getMenuid();
		
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.eq("parentid", productclassEntity.getMenuid()));
		
		List<ProductclassEntity> productclassEntities = findList(filters, Sequencer.desc("menuid"));
		
		String menu = "";
		if(productclassEntities.size()>0){
			menu = productclassEntities.get(0).getMenuid();
			int n = Integer.parseInt(menu.substring(menu.length()-2))+1;
			if(n<10){
				menuid = menuid +"0"+n;
			}else{
				menuid = menuid +n;
			}
		}else{
			menuid = menuid + "01";
		}
		
		//设置leaf的值
		if(productclassEntity !=null){
			productclassEntity.setLeaf("0");
			update(productclassEntity);
		}
		entity.setMenuid(menuid);
		entity.setParentid(productclassEntity.getMenuid());
		
		return super.save(entity);
		
	}
    
	/**
	 * 用来刷新产品分类的全名 
	 */
	@Transactional
	public void testFullName(){
		List<ProductclassEntity> list = findAll();
		for(ProductclassEntity item : list){
			String fullName = "/"+item.getName();
			ProductclassEntity parent =  find(Filter.eq("menuid", item.getParentid()));
			if(parent != null && parent.getId() != 1l){
				//fullName = fullName.concat("/"+parent.getName());
				fullName = "/"+parent.getName().concat(fullName);
				ProductclassEntity gradepa =  find(Filter.eq("menuid", parent.getParentid()));
				if(gradepa != null && gradepa.getId() != 1l){
					fullName = "/"+gradepa.getName().concat(fullName);
				}
			}
			item.setFullName(fullName);
			update(item);
		}
		
	}

	@Override
	public List<Map<String, Object>> queryrows(HttpServletRequest request, HttpServletResponse response, Model model) {
		return productclassDao.queryrows(request,response,model);
	}

	@Override
	public List<Map> query(HttpServletRequest request, HttpServletResponse response, Model model) {
	    //产品分类数据 industryClass
		List<Map<String, Object>> queryrows = productclassDao.query(request, response, model);
		List<Map> resultList2 = new ArrayList<>();
		for (Map<String, Object> entity : queryrows) {

			Map<String, Object> item = new HashMap<String, Object>();
            
			if (entity.get("i_id") != null && entity.get("i_id") != "") {
				item.put("i_id", entity.get("i_id"));
            }
			
            if (entity.get("i_name") != null && entity.get("i_name") != "") {
                item.put("i_name", entity.get("i_name"));
            }
			if (entity.get("i_modify_date") != null && entity.get("i_modify_date") != "") {
				item.put("i_modify_date", entity.get("i_modify_date"));
			}

			if (entity.get("i_id") != null && entity.get("i_id") != "") {
				IndustryClassEntity industryClassEntity = industryClassDao.find(Long.parseLong(entity.get("i_id").toString()));
				Set<ProductclassEntity> productclassEntities = industryClassEntity.getProductclassEntities();
				Iterator<ProductclassEntity> iterator = productclassEntities.iterator();
				List<Map> prolist=new ArrayList<>();//仪器集合
				List<Map> bmlist=new ArrayList<>();//品牌型号集合
				while(iterator.hasNext()){
					//封装产品下面的仪器
					Map m=new HashMap();
					ProductclassEntity next = iterator.next();
					m.put("pid",next.getId());
					m.put("pname",next.getName());
					m.put("pmenuid",next.getMenuid());
					prolist.add(m);
					//封装仪器下面的品牌型号
					List<BrandAndModelEntity> list = brandAndModelService.findList(Filter.eq("industryClassEntity",industryClassEntity),Filter.eq("productclassEntity", next));
					if(list!=null){
						for (BrandAndModelEntity brandAndModelEntity : list) {
							Map bm=new HashMap();
							bm.put("b_id",brandAndModelEntity.getId());
							bm.put("b_model",brandAndModelEntity.getModel());
							bm.put("b_brand", StringUtils.isNotBlank(brandAndModelEntity.getProductBrandEntity().getNameEn())?brandAndModelEntity.getProductBrandEntity().getNameEn():brandAndModelEntity.getProductBrandEntity().getName());
							bmlist.add(bm);
						}
					}
				}
				item.put("bmList",bmlist);
				item.put("proList",prolist);
			}
			resultList2.add(item);
		}
		return resultList2;
	}

}
