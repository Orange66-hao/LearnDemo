package net.boocu.project.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import net.boocu.project.dao.AutoTestDao;
import net.boocu.project.entity.AutoTestEntity;
import net.boocu.project.entity.InstrumentEntity;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;
import net.boocu.project.service.AutoTestService;
import net.boocu.project.service.InstrumentService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProductclassService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.controller.common.CommonUtil;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.MemberService;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;


@Service("autoTestServiceImpl")
public class AutoTestServiceImpl extends BaseServiceImpl<AutoTestEntity, Long> implements AutoTestService {
	@Resource(name = "autoTestDaoImpl")
    private AutoTestDao autoTestDao;
    
    @Resource(name = "autoTestDaoImpl")
    public void setBaseDao(AutoTestDao autoTestDao) {
        super.setBaseDao(autoTestDao);
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
	public Page<AutoTestEntity> findAutoTestPage(Pageable pageable,
			HashMap<String, Object> htMap) {
		return autoTestDao.findAutoTestPage(pageable, htMap);
	}

	@Override
	@Transactional
	public AutoTestEntity save(AutoTestEntity autoTestEntity,
			ProductEntity productEntity) {
		BigDecimal price = autoTestEntity.getBudgetPrice();
		if(autoTestEntity.getBudgetPriceUnit() == PriceUnitEnum.millionyuan){
			price = price.multiply(new BigDecimal("10000"));
		}
		productEntity.setPrice(price);
		ProductEntity productEntityNew = productService.save(productEntity);
		autoTestEntity.setProductEntity(productEntityNew);
		return save(autoTestEntity);
	}
	
	@Override
	@Transactional
	public void deleteList(Long... ids) {
		for(long id : ids){
			AutoTestEntity autoTestEntity = find(id);
			autoTestEntity.getProductEntity().setIsDel(1);
			update(autoTestEntity);
		}
	}

	@Override
	public void saveWithPro(AutoTestEntity autoTestEntity, String[] brandName) {
		ProductEntity productEntity = autoTestEntity.getProductEntity();
		productEntity.setProductType(producttypeService.find(7l));
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
		List<InstrumentEntity> list =  autoTestEntity.getProductItems();
		save(autoTestEntity);
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
				if(instrument.getProductclass().getId()==null){
					instrument.setProductclass(null);
				}else{
					ProductclassEntity productclassEntity = productClassService.find(instrument.getProductclass().getId());
					instrument.setProductclass(productclassEntity);
				}
				instrument.setAutoTest(autoTestEntity);
				i++;
				instrumentService.save(instrument);	
			}
		}
	}
	
	@Override
	public void updateWithPro(AutoTestEntity autoTestEntity, String[] brandName, Long id) {
		//更新前的自动化测试方案表
		AutoTestEntity autoTestEntity2 = find(id);
		
		long idss = autoTestEntity2.getId();
		String auto = autoTestEntity2.getProjectName();
		
		ProductEntity productEntity = productService.find(autoTestEntity2.getProductEntity().getId());
		ProductEntity productEntity1 = autoTestEntity.getProductEntity();
		//修改商品表
		if(productEntity!=null && productEntity1!=null){
			productEntity.setProownaudit(productEntity1.getProownaudit());
			productEntity.setInforValidity(productEntity1.getInforValidity());
			productEntity.setWeben(productEntity1.getWeben());
			productEntity.setWebzh(productEntity1.getWebzh());
			productEntity.setIsTax(productEntity1.getIsTax());
			productEntity.setTaxRate(productEntity1.getTaxRate());
			productEntity.setProuseAddress(productEntity1.getProuseAddress());
			productEntity.setAreaProvince(productEntity1.getAreaProvince());
			productEntity.setAreaCity(productEntity1.getAreaCity());
			productEntity.setApply(productEntity1.getApply());
			productEntity.setIndustryClass(productEntity1.getIndustryClass());
			productEntity.setPrometaTitle(productEntity1.getPrometaTitle());
			productEntity.setProMetaKeywords(productEntity1.getProMetaKeywords());
			productEntity.setPrometaDescription(productEntity1.getPrometaDescription());
	    	if(productEntity1.getInforValidity() !=null){
	    		Date date = new Date();
	    		Integer date1 = (int)((productEntity.getInforValidity().getTime() - date.getTime())/1000/60/60/24);
	    		productEntity.setInforNumber(date1);
	    	}
			//productService.update(productEntity);
		}
		
		//更新自动化测试方案表
		autoTestEntity2.setProjectName(autoTestEntity.getProjectName());
		autoTestEntity2.setProjectNameEn(autoTestEntity.getProjectNameEn());
		autoTestEntity2.setDevelopPeriod(autoTestEntity.getDevelopPeriod());
		autoTestEntity2.setDevelopPeriodUnit(autoTestEntity.getDevelopPeriodUnit());
		autoTestEntity2.setBudgetPrice(autoTestEntity.getBudgetPrice());
		autoTestEntity2.setBudgetPriceType(autoTestEntity.getBudgetPriceType());
		autoTestEntity2.setBudgetPriceUnit(autoTestEntity.getBudgetPriceUnit());
		autoTestEntity2.setProjectIntroduction(autoTestEntity.getProjectIntroduction());
		autoTestEntity2.setProjectIntroductionEn(autoTestEntity.getProjectIntroductionEn());
		autoTestEntity2.setProductEntity(autoTestEntity.getProductEntity());
		List<InstrumentEntity> instrumentEntities = autoTestEntity2.getProductItems();
		if(instrumentEntities!=null && instrumentEntities.size()>0){
			for(InstrumentEntity itemEntity : instrumentEntities){
				Long ids = itemEntity.getId();
				instrumentService.delete(itemEntity.getId());
			}
		}
		List<InstrumentEntity> list =  autoTestEntity.getProductItems();
		int i=0;
		for(InstrumentEntity instrument :list){
			if(brandName !=null && brandName.length>0){
				String string = brandName[i];
				if(!string.isEmpty()){
					ProductBrandEntity productBrand = productBrandService.find(Filter.eq("name", string));
					if(CommonUtil.isNumeric(string)==true){
						ProductBrandEntity productBrandEntity = productBrandService.find(Long.parseLong(string));
						if(productBrandEntity !=null){
							instrument.setProductBrandEntity(productBrandEntity);
						}else if(productBrand !=null){
							instrument.setProductBrandEntity(productBrand);
						}else{
							ProductBrandEntity productBrandEntity1 = new ProductBrandEntity();
				    		productBrandEntity1.setName(string);
				    		productBrandService.save(productBrandEntity1);
				    		instrument.setProductBrandEntity(productBrandEntity1);
						}
					}else if(productBrand !=null){
						instrument.setProductBrandEntity(productBrand);
					}else{
						ProductBrandEntity productBrandEntity = new ProductBrandEntity();
			    		productBrandEntity.setName(string);
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
			instrument.setAutoTest(autoTestEntity2);
			i++;
			instrumentService.save(instrument);
		}
		update(autoTestEntity2);
	}
}
