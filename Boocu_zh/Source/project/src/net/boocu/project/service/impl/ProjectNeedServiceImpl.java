package net.boocu.project.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import net.boocu.project.dao.ProjectNeedDao;
import net.boocu.project.entity.AutoTestEntity;
import net.boocu.project.entity.InstrumentEntity;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;
import net.boocu.project.entity.ProjectNeedEntity;
import net.boocu.project.service.InstrumentService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProductclassService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.project.service.ProjectNeedService;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.controller.common.CommonUtil;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.MemberService;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;


@Service("projectNeedServiceImpl")
public class ProjectNeedServiceImpl extends BaseServiceImpl<ProjectNeedEntity, Long> implements ProjectNeedService {
	@Resource(name = "projectNeedDaoImpl")
    private ProjectNeedDao projectNeedDao;
    
    @Resource(name = "projectNeedDaoImpl")
    public void setBaseDao(ProjectNeedDao projectNeedDao) {
        super.setBaseDao(projectNeedDao);
    }

    @Resource
    private ProductService productService;
    @Resource
    private ProducttypeService producttypeService;
    @Resource
    private InstrumentService instrumentService;
    @Resource
    private ProductBrandService productBrandService;
    @Resource
    private MemberService memberService;
    @Resource
    private ProductclassService productClassService;
    
	@Override
	public Page<ProjectNeedEntity> findProjectNeedPage(Pageable pageable,
			HashMap<String, Object> htMap) {
		return projectNeedDao.findProjectNeedPage(pageable, htMap);
	}

	@Override
	@Transactional
	public ProjectNeedEntity save(ProjectNeedEntity projectNeedEntity,
			ProductEntity productEntity) {
		BigDecimal price = projectNeedEntity.getBudgetPrice();
		if(projectNeedEntity.getBudgetPriceUnit() == PriceUnitEnum.millionyuan){
			price = price.multiply(new BigDecimal("10000"));
		}
		productEntity.setPrice(price);
		ProductEntity productEntityNew = productService.save(productEntity);
		projectNeedEntity.setProductEntity(productEntityNew);
		return save(projectNeedEntity);
	}
	
	@Override
	@Transactional
	public void deleteList(Long... ids) {
		for(long id : ids){
			ProjectNeedEntity projectNeedEntity = find(id);
			projectNeedEntity.getProductEntity().setIsDel(1);
			update(projectNeedEntity);
		}
		
	}
	
	@Override
	public void saveWithNeed(ProjectNeedEntity projectNeedEntity, String[] brandName) {
		ProductEntity productEntity = projectNeedEntity.getProductEntity();
		productEntity.setProductType(producttypeService.find(8l));
		if(productEntity.getMemberEntity()!=null){
			MemberEntity memberEntity = memberService.find(productEntity.getMemberEntity().getId());
		    if(memberEntity.getMemberGradeEntity().getName().equals("内部员工")){
		    	productEntity.setProClass(0);
		    }else{
		    	productEntity.setProClass(1);
		    }
		}
    	if(productEntity.getInforValidity() !=null){
    		Date date = new Date();
    		Integer date1 = (int)((productEntity.getInforValidity().getTime() - date.getTime())/1000/60/60/24);
    		productEntity.setInforNumber(date1);
    	}
		productService.save(productEntity);
		List<InstrumentEntity> list =  projectNeedEntity.getInstrumentEntities();
		save(projectNeedEntity);
		int i=0;
		for(InstrumentEntity instrument :list){
			if(instrument!=null && (instrument.getProductBrandEntity()!=null || instrument.getProNo()!=null || instrument.getProName()!=null)){
				if(brandName !=null && brandName.length>0){
					String string = brandName[i];
					if(!string.isEmpty()){
						List<ProductBrandEntity> productBrand = productBrandService.findList(Filter.eq("name", string));
						if(CommonUtil.isNumeric(string)==true){
							ProductBrandEntity productBrandEntity = productBrandService.find(Long.parseLong(string));
							if(productBrandEntity !=null){
								instrument.setProductBrandEntity(productBrandEntity);
							}else if(productBrand.size()>0){
								instrument.setProductBrandEntity(productBrand.get(0));
							}else{
								ProductBrandEntity productBrandEntity1 = new ProductBrandEntity();
					    		productBrandEntity1.setName(string);
					    		productBrandEntity1.setApprStatus(0);
					    		productBrandService.save(productBrandEntity1);
					    		instrument.setProductBrandEntity(productBrandEntity1);
							}
						}else if(productBrand.size()>0){
							instrument.setProductBrandEntity(productBrand.get(0));
						}else{
							ProductBrandEntity productBrandEntity = new ProductBrandEntity();
				    		productBrandEntity.setName(string);
				    		productBrandEntity.setApprStatus(0);
				    		productBrandService.save(productBrandEntity);
				    		instrument.setProductBrandEntity(productBrandEntity);
						}
					}	
				}
				if(instrument.getProductclass()==null){
					instrument.setProductclass(null);
				}else{
					ProductclassEntity productclassEntity = productClassService.find(instrument.getProductclass().getId());
					instrument.setProductclass(productclassEntity);
				}
				instrument.setProjectNeed(projectNeedEntity);;
				i++;
				instrumentService.save(instrument);
			}
		}
	}
}
