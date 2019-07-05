package net.boocu.project.controller.front;

import com.alibaba.druid.support.json.JSONUtils;
import com.sun.xml.internal.ws.spi.db.PropertySetterBase;
import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.*;
import net.boocu.project.service.*;
import net.boocu.web.*;
import net.boocu.web.enums.MessageTypeEnum;
import net.boocu.web.service.AdminService;
import net.boocu.web.service.MemberService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * 
 * 
 * @author zhang
 *         2015年8月27日
 */
@Controller("instrumentModelController")
@RequestMapping("/instrumentModel")
public class InstrumentModelController {
    private Logger logger = Logger.getLogger(InstrumentModelController.class);
    private static final String TEM_PATH = "/template/front/instrumentModel";
    @Autowired
    private InstrumentModelService instrumentModelService;
    @Autowired
    private ProductclassService productclassService;

    @Resource
    private ProductBrandService productBrandService;
    
    
    @Autowired
    private IndustryClassService industryClassService;

    @Resource
    private MemberService memberService;

    @Resource
    private ProductService productService;

    @Resource
    private JdbcTemplate JdbcTemplate;

    @ResponseBody
    @RequestMapping(value="/doAdd.jspx",method={RequestMethod.POST,RequestMethod.GET})
    public Message doAdd(HttpServletRequest request, HttpServletResponse response){
        String brandId = ReqUtil.getString(request, "brandId", "");
        String model = ReqUtil.getString(request, "model", "");
        String poption = ReqUtil.getString(request, "poption", "");
        String remark = ReqUtil.getString(request, "remark", "");
        String instrument = ReqUtil.getString(request, "instrument", "");
        String[] chanpinIdLists = ReqUtil.getParams(request, "chanpinIdList[]");
        System.out.println(brandId);
        ProductBrandEntity productBrandEntity=null;
        try {
             productBrandEntity= productBrandService.find(Long.parseLong(brandId));
        } catch (NumberFormatException e) {
            e.printStackTrace();
           return  new Message(MessageTypeEnum.error,"请选择下拉品牌");
        }
        ProductclassEntity productclassEntity=null;
        try {
             productclassEntity= productclassService.find(Long.parseLong(instrument));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return  new Message(MessageTypeEnum.error,"此仪器分类不存在");
        }
        InstrumentModelEntity entity=new InstrumentModelEntity();
        entity.setProductBrandEntity(productBrandEntity);
        entity.setModel(model);
        entity.setRemark(remark);
        entity.setPoption(poption);
        entity.setProductclassEntity(productclassEntity);
        if(chanpinIdLists!=null&&chanpinIdLists.length>0){
            for (String id : chanpinIdLists) {
                IndustryClassEntity industryClassEntity = industryClassService.find(Long.parseLong(id));
                IndustryClassModelEntity industryClassModelEntity=new IndustryClassModelEntity();
                industryClassModelEntity.setIndustryClassEntity(industryClassEntity);
                industryClassModelEntity.setInstrumentModelEntity(entity);
                entity.getIndustryClassModelEntities().add(industryClassModelEntity);
            }
        }
        instrumentModelService.save(entity);
        return  new Message(MessageTypeEnum.success,"成功添加");
    }
    @RequestMapping(value = "doModifyInstrumentModel.jspx", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Message doModifyInstrumentModel(HttpServletRequest request, HttpServletResponse response) {
        try {
            Long instrument_model_id_add = ReqUtil.getLong(request, "instrument_model_id_add", 0L);
            Long brand = ReqUtil.getLong(request, "brand", 0L);
            String model = ReqUtil.getString(request, "model", "");
            String poptipn = ReqUtil.getString(request, "poption", "");
            String remark = ReqUtil.getString(request, "remark", "");
            Long instrument = ReqUtil.getLong(request, "instrument", 0L);
            String[] add_list = ReqUtil.getParams(request, "add_list[]");
            String[] del_list = ReqUtil.getParams(request, "del_list[]");
            InstrumentModelEntity entity = instrumentModelService.find(instrument_model_id_add);
            ProductBrandEntity brandEntity=new ProductBrandEntity();
            brandEntity.setId(brand);
            entity.setProductBrandEntity(brandEntity);
            entity.setModel(model);
            entity.setPoption(poptipn);
            entity.setRemark(remark);
            ProductclassEntity productclassEntity = productclassService.find(instrument);
            if(productclassEntity!=null){
                entity.setProductclassEntity(productclassEntity);
            }
            if(del_list!=null&&del_list.length>0){
               for (String s : del_list) {
                   instrumentModelService.deleteIndustryClassModelEntity(instrument_model_id_add, Long.parseLong(s));
               }
           }

           if(add_list!=null&&add_list.length>0){
               for (String id : add_list) {
                   IndustryClassModelEntity modelEntity=new IndustryClassModelEntity();
                   IndustryClassEntity industryClassEntity = industryClassService.find(Long.parseLong(id));
                   modelEntity.setInstrumentModelEntity(entity);
                   modelEntity.setIndustryClassEntity(industryClassEntity);
                   entity.getIndustryClassModelEntities().add(modelEntity);
               }
           }
            instrumentModelService.update(entity);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return new Message(MessageTypeEnum.error,"修改失败，服务器出错了");
        }
        return new Message(MessageTypeEnum.success,"修改成功");
    }
    @RequestMapping(value = "toList.jspx", method = {RequestMethod.POST, RequestMethod.GET})
    public String toList(HttpServletRequest request, HttpServletResponse response) {

        return TEM_PATH + "/InstrumentModel_List";
    }
    @RequestMapping(value = "deleteInstrumentModel.jspx", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Message deleteInstrumentModel(HttpServletRequest request, HttpServletResponse response, Model model) {
        try {
            String instrumentModelIds = ReqUtil.getString(request, "instrumentModelIds", "");
            if(instrumentModelIds!=""){
                String[] ids = instrumentModelIds.split(",");
                for (String id : ids) {
                    InstrumentModelEntity instrumentModelEntity = instrumentModelService.find(Long.parseLong(id));
                    if(instrumentModelEntity!=null){
                        Set<IndustryClassModelEntity> industryClassModelEntities = instrumentModelEntity.getIndustryClassModelEntities();
                        for (IndustryClassModelEntity industryClassModelEntity : industryClassModelEntities) {
                            instrumentModelService.deleteManufacturersAndModelEntity(Long.parseLong(id),  industryClassModelEntity.getIndustryClassEntity().getId());
                        }
                    }
                    instrumentModelService.delete(Long.parseLong(id));
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return new Message(MessageTypeEnum.error,"删除失败，服务器出错了");
        }
        return new Message(MessageTypeEnum.success,"删除成功");
    }
    @RequestMapping(value = "findById.json", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Map<String,Object> find(HttpServletRequest request, HttpServletResponse response, Model model) {
        Long instrument_model_id = ReqUtil.getLong(request, "instrument_model_id", 0L);
        InstrumentModelEntity entity=null;
        if(instrument_model_id!=0L){
             entity = instrumentModelService.find(instrument_model_id);
        }
        Map<String,Object> map=new HashMap<>();
        map.put("id",entity.getId());
        map.put("brand_id",entity.getProductBrandEntity().getId());
        map.put("model",entity.getModel());
        map.put("poption",entity.getPoption());
        map.put("remark",entity.getRemark());
        map.put("productClass_id",entity.getProductclassEntity().getId());
        List list=new ArrayList();
        Iterator<IndustryClassModelEntity> iterator = entity.getIndustryClassModelEntities().iterator();
        while (iterator.hasNext()){
            list.add(iterator.next().getIndustryClassEntity().getId());
        }
        map.put("indu_id_List",list);
       return map;
    }
    @RequestMapping(value = "list.json", method = {RequestMethod.POST, RequestMethod.GET})
    public void list(HttpServletRequest request, HttpServletResponse response, Model model) {
        List<Map<String, Object>> query = instrumentModelService.query(request, response, model);
        Long count=instrumentModelService.queryrows(request, response, model);//查询条数
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("total", count);//获取行总数
        result.put("rows", query);
       RespUtil.renderJson( response, result);
    }
    //添加厂家_型号
    @RequestMapping(value = "doAddManufacturersModel.jspx", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Message doAddManufacturersModel(HttpServletRequest request, HttpServletResponse response, Model model) {
        try {
            String[] manufacturers = ReqUtil.getParams(request, "manufacturers");
            String[] models = ReqUtil.getParams(request, "model");
            Long industryClass = ReqUtil.getLong(request, "industryClass", 0L);
            Long instrument_model_id = ReqUtil.getLong(request, "instrument_model_id", 0l);
            if(industryClass!=0L){
               IndustryClassEntity industryClassEntity = industryClassService.find(industryClass);
               if(industryClassEntity!=null){
                   for (int i=0;i<manufacturers.length; i++) {
                       ManufacturersAndModelEntity manufacturersAndModelEntity=new ManufacturersAndModelEntity();
                       manufacturersAndModelEntity.setManufacturers(manufacturers[i]);
                       manufacturersAndModelEntity.setModel(models[i]);
                       manufacturersAndModelEntity.setIndustryClassEntity(industryClassEntity);
                       manufacturersAndModelEntity.setInstrument_model_id(instrument_model_id);
                       industryClassEntity.getManufacturersAndModelEntityList().add(manufacturersAndModelEntity);
                   }
               }
               industryClassService.save(industryClassEntity);
           }
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(MessageTypeEnum.error,"服务器错误");
        }
        return new Message(MessageTypeEnum.success,"添加成功");
    }
    //跳转仪器关联产品界面
    @RequestMapping("association.jspx")
    public String guanlian(HttpServletRequest request, HttpServletResponse response) {
        return TEM_PATH+"/association";
    }
    // 获取仪器下拉树形结构信息
    @ResponseBody
    @RequestMapping(value = "/productclass/combotreeData.json", method = { RequestMethod.POST, RequestMethod.GET })
    public List<Map> getComboTreeDataJson(HttpServletRequest request, HttpServletResponse response, Model model) {
        boolean getSanji = ReqUtil.getBoolean(request, "getSanji", false);
        if(getSanji){
            List<Map> resultList = new ArrayList<Map>();
            ProductclassEntity topNode = productclassService.find(1L);
            return getNodeData2(topNode,resultList);
        }else{
            ProductclassEntity topNode = productclassService.find(1L);
            return getNodeData(topNode);
        }
    }
    // 根据仪器型号表id和industry表的id获取厂家型号信息
    @ResponseBody
    @RequestMapping(value = "/getManufacturersAndModelList.json", method = { RequestMethod.POST, RequestMethod.GET })
    public List<Map<String, Object>> getManufacturersAndModelList(HttpServletRequest request, HttpServletResponse response, Model model) {
        Long instrument_model_id = ReqUtil.getLong(request, "instrument_model_id", 0L);
        Long industry_id = ReqUtil.getLong(request, "industry_id", 0L);
        List<Map<String, Object>> list=new ArrayList<>();
        if(instrument_model_id!=0L&&industry_id!=0L){
            list=instrumentModelService.getManufacturersAndModelList(instrument_model_id,industry_id);
        }
        return  list;
    }
    //递归查找树形结构信息
    public List<Map> getNodeData(ProductclassEntity topNode) {
        List<Map> resultList = new ArrayList<Map>();
        List<Filter> flist = new ArrayList<Filter>();
        flist.add(Filter.eq("parentid", topNode.getMenuid()));
        List<ProductclassEntity> items = productclassService.findList(flist, Sequencer.asc("sort"));
        for (ProductclassEntity item : items) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", item.getId());
            map.put("text", item.getName());
            List<ProductclassEntity> children = productclassService.findList(Filter.eq("parentid", item.getMenuid()));
            if (children.size() != 0) {
                map.put("children", getNodeData(item));
            }
            resultList.add(map);
        }
        return resultList;
    }
    //递归查找三级目录
    public List<Map> getNodeData2(ProductclassEntity topNode, List<Map> resultList) {
        Filter filter = Filter.eq("parentid", topNode.getMenuid());
        List filters=new ArrayList();
        filters.add(filter);
        Sequencer sequencer=Sequencer.asc("sort");
        List<ProductclassEntity> items = productclassService.findList(filters,sequencer);
        for (ProductclassEntity item : items) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", item.getId());
            map.put("text", item.getName());
            ArrayList<Filter> filters1 = new ArrayList<>();
            filters1.add(Filter.eq("parentid",item.getMenuid()));
            List<ProductclassEntity> children = productclassService.findList(filters1,sequencer);
            if (children.size() != 0) {
                getNodeData2(item,resultList);
            }else{
                resultList.add(map);
            }
        }
        return resultList;
    }
    //获取下拉树形结构信息
    @ResponseBody
    @RequestMapping(value="/industyClass/combotreeData.json",method={RequestMethod.POST,RequestMethod.GET})
    public List<Map> getComboTreeDataJson2(HttpServletRequest request, HttpServletResponse response, Model model){
        IndustryClassEntity topNode =  industryClassService.find(1l);
        return getNodeData(topNode);
    }

    //递归查找树形结构信息
    public List<Map> getNodeData(IndustryClassEntity topNode){
        List<Map> resultList = new ArrayList<Map>();
        List<Filter> flist = new ArrayList<Filter>();
        flist.add(Filter.eq("parentid", topNode.getId()));
        List<IndustryClassEntity> items = industryClassService.findList(flist, Sequencer.asc("sort"));
        for(IndustryClassEntity item : items){
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("id", item.getId());
            map.put("text", item.getName());
            List<IndustryClassEntity> children = industryClassService.findList(Filter.eq("parentid", item.getId()));
            if(children.size() != 0){
                map.put("children", getNodeData(item));
            }
            resultList.add(map);
        }
        return resultList;
    }
}
