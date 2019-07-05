package net.boocu.project.controller.front;

import javax.annotation.Resource;

import net.boocu.project.entity.BrandAndModelEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.BrandAndModelService;
import net.boocu.project.service.ProductclassService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import net.boocu.project.service.MobileRecordService;
import net.boocu.web.Message;

@Controller
public class MyController {
    /**
     * 模板路径
     */
    private static final String TEMPLATE_PATH = "front/userCenter/productInforManage/autoTest";

    /**
     * 成功信息
     */
    private static final Message SUCCESS_MESSAGE = Message.success("操作成功!");

    /**
     * 错误信息
     */
    private static final Message ERROR_MESSAGE = Message.error("错误信息!");

    @Resource
    private MobileRecordService mobileRecordService;
    @Resource
    private ProductclassService productclassService;
    @Resource
    private BrandAndModelService brandAndModelService;
    @RequestMapping("/in.jspx")
    public String toIndex() {
        return "login";
    }

    @RequestMapping("/add.jspx")
    public void addBrandModel() {
        ProductclassEntity productclassEntity = productclassService.find(2L);
        BrandAndModelEntity brandAndModelEntity=new BrandAndModelEntity();
        //brandAndModelEntity.setBrand("1p");
        brandAndModelEntity.setModel("1p");
        brandAndModelEntity.setRemark("第一个型号");
        brandAndModelEntity.setProductclassEntity(productclassEntity);
        brandAndModelService.save(brandAndModelEntity);
    }
    @RequestMapping("/get.jspx")
    public void getproductclassService() {
        ProductclassEntity productclassEntity = productclassService.find(2L);
        BrandAndModelEntity brandAndModelEntity=new BrandAndModelEntity();
        //brandAndModelEntity.setBrand("2p");
        brandAndModelEntity.setModel("2p");
        brandAndModelEntity.setProductclassEntity(productclassEntity);
        brandAndModelEntity.setRemark("第2个型号");
        brandAndModelService.save(brandAndModelEntity);
    }


}
