/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.controller.admin;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.controller.BaseController;
import net.boocu.project.bean.InstrumentsBean;
import net.boocu.project.entity.CalibrationEntity;
import net.boocu.project.entity.InstrumentEntity;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.entity.RequireTestEntity;
import net.boocu.project.service.CalibrationService;
import net.boocu.project.service.HelpService;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.InstrumentService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProductclassService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.web.Filter;
import net.boocu.web.Message;
import net.boocu.web.controller.common.CommonUtil;
import net.boocu.web.service.MemberService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

 
  

/**
 * Controller - 修改计量校准
 * 
 * @author deng 20160130
 * @version 1.0
 */
@Controller("editCalibrationController")
@RequestMapping("/admin")
public class EditCalibrationController extends BaseController {
    
    /**成功信息*/
    private static final Message SUCCESS_MESSAGE = Message.success("操作成功!");
    
    /**错误信息*/
    private static final Message ERROR_MESSAGE = Message.error("错误信息!");
    
    @Resource
    CalibrationService calibrationService;
    
    @Resource
    ProductService productService;
    
    @Resource
    MemberService memberService;
    
    @Resource
    HelpService helpService;
    
    @Resource
    ProductBrandService productBrandService;
    
    @Resource
    ProducttypeService productTypeService;
    
    @Resource
    ProductclassService productClassService;
    
    @Resource
    IndustryClassService industryClassService;
    
    @Resource
    InstrumentService instrumentService;
    
    @ModelAttribute
    public void getCalibration(@RequestParam(required=false,defaultValue="0") Long id,Model model){
    	CalibrationEntity calibrationEntity = calibrationService.find(id);
    	if(calibrationEntity!=null){
    		model.addAttribute("editCalibration", calibrationEntity);
    	}
    }
    
    //修改产品测试
    @ResponseBody
    @RequestMapping(value="/update_updateEdit_user_pub_calibration", method={RequestMethod.POST, RequestMethod.GET})
    public Message update_updateEdit_user_pub_calibration(HttpServletRequest request, HttpServletResponse response,String[] brandName,String brand,@ModelAttribute("editCalibration") CalibrationEntity calibrationEntity, InstrumentsBean instrumentsBean){
    	
    	if(calibrationEntity.getProductEntity().getProductclass().getId()==null){
    		return ERROR_MESSAGE;
    	}
    	if(calibrationEntity.getProductEntity().getInforValidity()==null && calibrationEntity.getProductEntity().getLongTerm()==0){
    		return ERROR_MESSAGE;
    	}
    	
		List<InstrumentEntity> list =  calibrationEntity.getProductItems();
		if(brand!=null){
			if(!brand.isEmpty()){
				ProductBrandEntity productBrand = productBrandService.find(Filter.eq("name", brand));
				if(CommonUtil.isNumeric(brand)==true){
					ProductBrandEntity productBrandEntity = productBrandService.find(Long.parseLong(brand));
					if(productBrandEntity !=null){
						calibrationEntity.getProductEntity().setProductBrandEntity(productBrandEntity);
					}else if(productBrand !=null){
						calibrationEntity.getProductEntity().setProductBrandEntity(productBrand);
					}else{
						ProductBrandEntity productBrandEntity1 = new ProductBrandEntity();
			    		productBrandEntity1.setName(brand);
			    		productBrandEntity1.setApprStatus(0);
			    		productBrandService.save(productBrandEntity1);
			    		calibrationEntity.getProductEntity().setProductBrandEntity(productBrandEntity1);
					}
				}else if(productBrand !=null){
					calibrationEntity.getProductEntity().setProductBrandEntity(productBrand);
				}else{
					ProductBrandEntity productBrandEntity = new ProductBrandEntity();
		    		productBrandEntity.setName(brand);
		    		productBrandEntity.setApprStatus(0);
		    		productBrandService.save(productBrandEntity);
		    		calibrationEntity.getProductEntity().setProductBrandEntity(productBrandEntity);
				}
			}
		}else{
			calibrationEntity.getProductEntity().setProductBrandEntity(null);
		}
    	calibrationService.update(calibrationEntity);
    	if(list.size()!=0){
        	instrumentService.deleteList(list);
		}
    		int i=0;
    		for(InstrumentEntity instrument : instrumentsBean.getInstruments()){
    			if(instrument!=null && (instrument.getProductBrandEntity()!=null || instrument.getProNo()!=null || instrument.getProName()!=null)){
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
    					    		productBrandEntity1.setApprStatus(0);
    					    		productBrandService.save(productBrandEntity1);
    					    		instrument.setProductBrandEntity(productBrandEntity1);
    							}
    						}else if(productBrand !=null){
    							instrument.setProductBrandEntity(productBrand);
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
    				instrument.setCalibration(calibrationEntity);
    				instrumentService.save(instrument);
    				i++;
    			}	
    	}


    	
    	return SUCCESS_MESSAGE;
    	
    }			
}