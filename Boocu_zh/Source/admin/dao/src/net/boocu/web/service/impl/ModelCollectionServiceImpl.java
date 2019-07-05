package net.boocu.web.service.impl;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.boocu.framework.util.ReqUtil;
import net.boocu.project.entity.*;
import net.boocu.project.service.*;
import net.boocu.web.Message;
import net.boocu.web.controller.admin.McBrandAndModelController;
import net.boocu.web.controller.admin.McCompanyNameController;
import net.boocu.web.controller.admin.McContactsController;
import net.boocu.web.controller.admin.McMemberController;
import net.boocu.web.dao.ModelCollectionDao;
import net.boocu.web.entity.AdminEntity;
import net.boocu.web.service.AdminService;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;


@Service("modelCollectionServiceImpl")
public class ModelCollectionServiceImpl extends BaseServiceImpl<ModelCollectionEntity, Long> implements ModelCollectionService {

    @Resource
    private JdbcTemplate JdbcTemplate;

    @Resource(name = "adminServiceImpl")
    AdminService adminService;

    @Resource
    private IndustryClassService industryClassService;//注入IndustryClassService对象

    @Resource
    private McBrandAndModelService mcBrandAndModelService;//注入McBrandAndModelService对象

    @Resource
    private ProductBrandService productBrandService;//注入ProductBrandService对象

    @Resource
    private ProductclassService productclassService;//注入ProductclassService对象

    @Resource
    private McMemberService mcMemberService;

    @Resource
    private McMemberController mcmemberController;

    @Resource
    private McContactsController mccontactsController;

    @Resource
    private McBrandAndModelController mcBrandAndModelController;//注入McBrandAndModelController对象

    @Resource
    private McCompanyNameController mccompanynameController;

    @Resource(name = "modelCollectionDaoImpl")
    private ModelCollectionDao modelCollectionDao;

    @Resource(name = "modelCollectionDaoImpl")
    public void setBaseDao(ModelCollectionDao memberGradeDao) {
        super.setBaseDao(memberGradeDao);
    }

    public List<Map> querycompany(HttpServletRequest request, HttpServletResponse response, Model model) {
        List<Map<String, Object>> topNode = modelCollectionDao.querycompany(request, response, model);//查询数据

        List<Map> resultList2 = new ArrayList<>();
        for (Map<String, Object> entity : topNode) {

            Map<String, Object> item = new HashMap<String, Object>();

            Map mcmember1 = null;
            if ( entity.get("blame") != null && !(String.valueOf(entity.get("blame")).isEmpty())) {
                mcmember1 = mcmemberController.getNodeData2(String.valueOf(entity.get("blame")));//根据责任人的id组，查询对应name（名称）
            }

            Map contacts = null;
            if ( entity.get("contact") != null && !(String.valueOf(entity.get("contact")).isEmpty())) {
                String contact = String.valueOf(entity.get("contact"));
                contacts = mccontactsController.getNodeData2(contact);//根据常用联系人的id组，查询对应name（名称）
            }

            Map mc_brand_and_models = null;
            if (entity.get("mc_brand_and_model") != null && !(String.valueOf(entity.get("mc_brand_and_model")).isEmpty())) {
                String mc_brand_and_model = String.valueOf(entity.get("mc_brand_and_model"));
                mc_brand_and_models = mcBrandAndModelController.getNodeData2(mc_brand_and_model);//根据仪器品牌和型号表的id组，查询对应数据
            }

            Map companyname = null;
            if (entity.get("name") != null && !(String.valueOf(entity.get("name")).isEmpty())) {
                companyname = mccompanynameController.getNodeData2(String.valueOf(entity.get("name")));//根据公司名称表的id组，查询对应name（名称）
            }
            if (entity.get("id") != null && entity.get("id") != "") {
                item.put("id", entity.get("id"));
            }
            if (companyname != null && companyname.get("id") != null && companyname.get("id") != "") {
                item.put("name_id", companyname.get("id"));
            }
            if (companyname != null && companyname.get("name") != null && companyname.get("name") != "") {
                item.put("name", companyname.get("name"));
            }
            if (entity.get("laboratory") != null && entity.get("laboratory") != "") { //实验室
                item.put("laboratory", entity.get("laboratory"));
            }

            if (entity.get("mc_industry_class") != null && entity.get("mc_industry_class") != "") {
                item.put("mc_industry_class_name", entity.get("mc_industry_class"));
            }

            String major_product = (String)entity.get("major_product");
            String name = "";
            if ((major_product != null) && (major_product != "")) {
                //item.put("major_product_name", entity.get("major_product"));
                //查询主营产品列的多个id的字符串并根据逗号切割
                String[] split = major_product.toString().split(",");
                for (int i = 0;i<split.length;i++) {
                    String s = split[i];
                    if (isNumeric(s)) {
                        //通过id查询并返回对应的 IndustryClassEntity 对象
                        IndustryClassEntity industryClassEntity = industryClassService.find(Long.parseLong(s));
                        if(industryClassEntity!=null){
                            name += industryClassEntity.getName()+",";
                        }
                    }
                }
                item.put("major_product_name", name);
            }

            if ((entity.get("research_direction") != null) && (entity.get("research_direction") != "")) {
                item.put("research_direction_name", entity.get("research_direction"));
            }

//            String mc_productclass = (String)entity.get("mc_productclass");
//            String mc_productclass_name = "";
//            if ((mc_productclass != null) && (mc_productclass != "")) {
//                //查询主营产品列的多个id的字符串并根据逗号切割
//                String[] split = mc_productclass.toString().split(",");
//                for (int i = 0;i<split.length;i++) {
//                    String s = split[i];
//                    if (isNumeric(s)) {
//                        //通过id查询并返回对应的 ProductclassEntity 对象
//                        ProductclassEntity productclassEntity = productclassService.find(Long.parseLong(s));
//                        mc_productclass_name += productclassEntity.getName()+",";
//                    }
//                }
//                item.put("mc_productclass_name", mc_productclass_name);
//            }
            if ((entity.get("mc_productclass") != null) && (entity.get("mc_productclass") != "")) {//之前的常用仪器名称
                item.put("mc_productclass_name", entity.get("mc_productclass"));
            }

            if ((entity.get("resume") != null) && (entity.get("resume") != "")) {
                item.put("resume", entity.get("resume"));
            }

            String mc_brand_and_model = (String) entity.get("mc_brand_and_model");//这里需要强制转换成String类型
            if (mc_brand_and_model != null && !mc_brand_and_model.equals("")) {//字符串与字符串之间用.equals()判断,不能使用 ==
                String mcBrandAndModel = "";
                String mcproductclass_name="";
                String mcproductclass_nameId="";
                String mc_companyId = "";
                ProductclassEntity productclassEntity =null;

                String[] split = mc_brand_and_model.toString().split(",");
                HashMap<String, String> productclassMap = new HashMap<>();
                for (int i = 0;i<split.length;i++) {

                    String s = split[i];
                    if (isNumeric(s)) {
                        McBrandAndModelEntity mcBrandAndModelEntity = mcBrandAndModelService.find(Long.parseLong(s));
                        String mc_brand = mcBrandAndModelEntity.getMc_brand();

                        mcproductclass_nameId = mcBrandAndModelEntity.getMc_productclass_name();
                        mc_companyId = mcBrandAndModelEntity.getMc_company();
                        productclassMap.put(mcproductclass_nameId,mc_companyId);

                        ProductBrandEntity productBrandEntity = productBrandService.find(Long.parseLong(mc_brand));
                        String mc_productclass_number = mcBrandAndModelEntity.getMc_productclass_number();
                        if (!mc_productclass_number.equals("1")) {
                            mcBrandAndModel += productBrandEntity.getName() +"，"+ mcBrandAndModelEntity.getMc_model() +"，"+ mc_productclass_number +";";
                        } else {
                            mcBrandAndModel += productBrandEntity.getName() +"，"+ mcBrandAndModelEntity.getMc_model() +";";

                        }
                    }
                }
                if (mcproductclass_nameId != null && !mcproductclass_nameId.equals("")) {
                    Set<Map.Entry<String, String>> entries = productclassMap.entrySet();
                    Iterator<Map.Entry<String, String>> iterator = entries.iterator();
                    String key = "";
                    while (iterator.hasNext()) {
                        key = iterator.next().getKey();
                        productclassEntity = productclassService.find(Long.parseLong(key));
                        mcproductclass_name += productclassEntity.getName()+"; ";
                    }
                }

                item.put("mc_brand_and_models", mcBrandAndModel);
                item.put("mcproductclass_name",mcproductclass_name);
            }

            /*if (entity.get("mc_brand") != null && entity.get("mc_brand") != "") {
                item.put("brand_name", entity.get("mc_brand"));
            }*/
            if (entity.get("mc_productclass") != null && entity.get("mc_productclass") != "") {
                item.put("mc_productclass", entity.get("mc_productclass"));
            }
            if (entity.get("subscribe_content") != null && entity.get("subscribe_content") != "") {
                item.put("subscribe_content", entity.get("subscribe_content"));
            }
            /*if (entity.get("mc_model") != null && entity.get("mc_model") != "") {
                item.put("mc_model", entity.get("mc_model"));
            }*/
            if (contacts != null && contacts.get("id") != null && contacts.get("id") != "") {
                item.put("contact", contacts.get("id"));
            }
            if (contacts != null && contacts.get("name") != null && contacts.get("name") != "") {
                item.put("contact_name", contacts.get("name"));
            }
            if (contacts != null && contacts.get("teacher") != null && contacts.get("teacher") != "") {//老师姓名
                item.put("teacher", contacts.get("teacher"));
            }
            if (contacts != null && contacts.get("major") != null && contacts.get("major") != "") {//专业
                item.put("major", contacts.get("major"));
            }
            if (contacts != null && contacts.get("resume") != null && contacts.get("resume") != "") {//简历
                item.put("resume", contacts.get("resume"));
            }

            if (mc_brand_and_models != null && !mc_brand_and_models.get("id").equals("")) {
                item.put("mc_brand_and_models",mc_brand_and_models.get("id"));
            }
            if (mc_brand_and_models != null && !mc_brand_and_models.get("mc_model").equals("")){
                String[] mc_brands = (String[]) mc_brand_and_models.get("mc_brand");
                ArrayList<String> mc_brand_name = new ArrayList<>();
                for (int i=0;i<mc_brands.length;i++) {
                    ProductBrandEntity productBrandEntity = productBrandService.find(Long.parseLong(mc_brands[i]));
                    mc_brand_name.add(productBrandEntity.getName());
                }
                item.put("mc_brand_name", mc_brand_name);
                item.put("mc_model", mc_brand_and_models.get("mc_model"));
                item.put("mc_productclass_number", mc_brand_and_models.get("mc_productclass_number"));

            }

            if (entity.get("blame") != null && entity.get("blame") != "") {
                item.put("blame", entity.get("blame")); //通过id去找name
            }
            if (mcmember1 != null && mcmember1.get("name") != null && mcmember1.get("name") != "") {
                item.put("blame_name", mcmember1.get("name"));
            }
            if (entity.get("introducer") != null && entity.get("introducer") != "") { //介绍人
                item.put("introducer", entity.get("introducer"));
            }

            if (entity.get("remark") != null && entity.get("remark") != "") { //备注
                item.put("remark", entity.get("remark"));
            }

            if (entity.get("createuser") != null && entity.get("createuser") != "") {
                item.put("createuser", entity.get("createuser"));
            }
            if (entity.get("create_date") != null && entity.get("create_date") != "") {//列表顶部的更新时间用到
                item.put("create_date", entity.get("create_date"));
            }
            if (entity.get("modify_date") != null && entity.get("modify_date") != "") {//把更新时间返回
                item.put("modify_date", entity.get("modify_date"));
            }
            if (entity.get("grade") != null && entity.get("grade") != "") {
                item.put("grade", entity.get("grade"));
            }
            if (entity.get("measurement_time") != null && entity.get("measurement_time") != "") {
                item.put("measurement_time", entity.get("measurement_time"));
            }
            if (entity.get("ck_zt") != null && entity.get("ck_zt") != "") {
                item.put("ckZt", entity.get("ck_zt"));
            }
            if (entity.get("rate") != null && entity.get("rate") != "") {
                item.put("rate", entity.get("rate"));
            }
            if (entity.get("subscribe_content") != null && entity.get("subscribe_content") != "") {
                item.put("subscribe_content", entity.get("subscribe_content"));
            }
            if (entity.get("website") != null && entity.get("website") != "") {
                item.put("mc_website", entity.get("website"));
            }
            resultList2.add(item);
        }
        return resultList2;
    }

    public static boolean isNumeric(String str) {//判断是否为数字的字符串的方法,用于区分主营产品是修改后的id字符串还是修改前的文本
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public void orderExport(HttpServletRequest request, HttpSession session, HttpServletResponse response) {
        List<Map<String, Object>> orderList = modelCollectionDao.querycompanyrows(request, response, null);//查询数据
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

        if (orderList != null && orderList.size() > 0) {//查询的数据不为空就对数据进行导出
            //导出文件的标题
            String title = "客户管理数据统计" + df.format(new Date()) + ".xls";
            //设置表格标题行
            String[] headers = new String[]{"序号", "行业分类", "公司名称", "主营产品", "常用仪器", "品牌型号", "联系人", "责任人", "等级", "计量时间", "创建时间"};
            List<Object[]> dataList = new ArrayList<Object[]>();
            Object[] objs = null;

            for (Map<String, Object> entity : orderList) {

                objs = new Object[headers.length];

                List<Map<String, Object>> mcmember1 = null;
                if (!(String.valueOf(entity.get("blame")).isEmpty()) && entity.get("blame") != null) {
                    mcmember1 = mcmemberController.quaryblame(String.valueOf(entity.get("blame")));
                }


                List<Map<String, Object>> contacts = null;
                if (!(String.valueOf(entity.get("contact")).isEmpty()) && entity.get("contact") != null) {
                    contacts = mccontactsController.quarycontacts(String.valueOf(entity.get("contact")));//根据常用联系人的id组，查询对应name（名称）
                }
                List<Map<String, Object>> companyname = null;
                if (!(String.valueOf(entity.get("name")).isEmpty()) && entity.get("name") != null) {
                    companyname = mccompanynameController.quarymccompanyname(String.valueOf(entity.get("name")));//根据常用联系人的id组，查询对应name（名称）
                }

    			/*if (entity.get("id") != null && entity.get("id") != "") {
    				objs entity.get("id"));
    			}*/
    			/*if (companyname != null && companyname.get("id") != null && companyname.get("id") != "") {
    				item.put("name_id", companyname.get("id"));
    			}*/
                if (companyname != null && companyname.size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    int i = 0;
                    for (Map<String, Object> map : companyname) {//当有多条数据，就进行拼接
                        if (i == companyname.size() - 1) { //当循环到最后一个的时候 就不添加逗号
                            sb.append((String) map.get("name"));
                            i++;
                        } else {
                            sb.append((String) map.get("name"));
                            sb.append(",");
                            i++;
                        }
                    }
                    objs[2] = sb;
                }

                objs[0] = entity.get("rowno");
                if (entity.get("mc_industry_class") != null && entity.get("mc_industry_class") != "") {
                    objs[1] = entity.get("mc_industry_class");
                }
                if ((entity.get("major_product") != null) && (entity.get("major_product") != "")) {

                    String[] major_product=entity.get("major_product").toString().split(",");
                    String str="";
                    if(major_product!=null&&major_product.length>0){
                        for (String s : major_product) {
                            IndustryClassEntity industryClassEntity = industryClassService.find(Long.parseLong(s));
                            if(industryClassEntity!=null){
                                str+=industryClassEntity.getName()+",";
                            }
                        }
                    }
                    objs[3] =str;
                }
                if ((entity.get("mc_productclass") != null) && (entity.get("mc_productclass") != "")) {
                    objs[4] = entity.get("mc_productclass");
                }

                if (entity.get("mc_brand") != null && entity.get("mc_brand") != "") {
                    objs[5] = entity.get("mc_brand");
                }
    		/*	if (entity.get("mc_productclass") != null && entity.get("mc_productclass") != "") {
    				item.put("mc_productclass", entity.get("mc_productclass"));
    			}
    			if (entity.get("mc_model") != null && entity.get("mc_model") != "") {
    				item.put("mc_model", entity.get("mc_model"));
    			}
    			if (contacts != null && contacts.get("id") != null && contacts.get("id") != "") {
    				item.put("contact", contacts.get("id"));
    			}*/
                if (contacts != null && contacts.size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    int i = 0;
                    for (Map<String, Object> map : contacts) {//当有多条数据，就进行拼接
                        if (i == contacts.size() - 1) { //当循环到最后一个的时候 就不添加逗号
                            sb.append((String) map.get("name"));
                            i++;
                        } else {
                            sb.append((String) map.get("name"));
                            sb.append(",");
                            i++;
                        }
                    }
                    objs[6] = sb;
                }
    			/*if (entity.get("blame") != null && entity.get("blame") != "") {
    				item.put("blame", entity.get("blame"));
    			}*/
                if (mcmember1 != null && mcmember1.size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    int i = 0;
                    for (Map<String, Object> map : mcmember1) {//当有多条数据，就进行拼接
                        if (i == mcmember1.size() - 1) { //当循环到最后一个的时候 就不添加逗号
                            sb.append((String) map.get("name"));
                            i++;
                        } else {
                            sb.append((String) map.get("name"));
                            sb.append(",");
                            i++;
                        }
                    }
                    objs[7] = sb;
                }
    			/*if (entity.get("createuser") != null && entity.get("createuser") != "") {
    				item.put("createuser", entity.get("createuser"));
    			}*/
                if (entity.get("create_date") != null && entity.get("create_date") != "") {
                    objs[10] = entity.get("create_date");
                }
                if (entity.get("grade") != null && entity.get("grade") != "") {
                    if (entity.get("grade").equals("1")) {
                        objs[8] = "等级一";
                    }
                    if (entity.get("grade").equals("2")) {
                        objs[8] = "等级二";
                    }
                    if (entity.get("grade").equals("3")) {
                        objs[8] = "等级三";
                    }
                    if (entity.get("grade").equals("4")) {
                        objs[8] = "等级四";
                    }
                    if (entity.get("grade").equals("5")) {
                        objs[8] = "等级五";
                    }
                    if (entity.get("grade").equals("6")) {
                        objs[8] = "等级六";
                    }
                    if (entity.get("grade").equals("7")) {
                        objs[8] = "等级七";
                    }
                    if (entity.get("grade").equals("8")) {
                        objs[8] = "等级八";
                    }
                    if (entity.get("grade").equals("9")) {
                        objs[8] = "等级九";
                    }
                }
                if (entity.get("measurement_time") != null && entity.get("measurement_time") != "") {
                    if (entity.get("measurement_time").equals("1")) {
                        objs[9] = "一月";
                    }
                    if (entity.get("measurement_time").equals("2")) {
                        objs[9] = "二月";
                    }
                    if (entity.get("measurement_time").equals("3")) {
                        objs[9] = "三月";
                    }
                    if (entity.get("measurement_time").equals("4")) {
                        objs[9] = "四月";
                    }
                    if (entity.get("measurement_time").equals("5")) {
                        objs[9] = "五月";
                    }
                    if (entity.get("measurement_time").equals("6")) {
                        objs[9] = "六月";
                    }
                    if (entity.get("measurement_time").equals("7")) {
                        objs[9] = "七月";
                    }
                    if (entity.get("measurement_time").equals("8")) {
                        objs[9] = "八月";
                    }
                    if (entity.get("measurement_time").equals("9")) {
                        objs[9] = "九月";
                    }
                    if (entity.get("measurement_time").equals("10")) {
                        objs[9] = "十月";
                    }
                    if (entity.get("measurement_time").equals("11")) {
                        objs[9] = "十一月";
                    }
                    if (entity.get("measurement_time").equals("12")) {
                        objs[9] = "十二月";
                    }
                }
                dataList.add(objs);
            }

            //使用流将数据导出
            OutputStream out = null;
            try {
                //防止中文乱码
                String headStr = "attachment; filename=\"" + new String(title.getBytes("gb2312"), "ISO8859-1") + "\"";
                response.setContentType("octets/stream");
                response.setContentType("APPLICATION/OCTET-STREAM");
                response.setHeader("Content-Disposition", headStr);
                out = response.getOutputStream();
                //ExportExcel ex = new ExportExcel(title, headers, dataList);//有标题
                ExportExcelSeedBack ex = new ExportExcelSeedBack(title, headers, dataList);//没有标题
                ex.export(out);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Message register(String edit_name, String name, HttpServletRequest request, HttpSession session) {
        if (edit_name != null && edit_name != "" && edit_name.equals(name)) {
            return Message.success("true");
        } else {
            List<Map<String, Object>> row = modelCollectionDao.register(edit_name, name, request, session);
            if (row.size() > 0) {
                return Message.success("false");
            }
            return Message.success("true");
        }
    }

    @Override
    public List<Map<String, Object>> querycompanyrows(HttpServletRequest request, HttpServletResponse response, Model model) {
        return modelCollectionDao.querycompanyrows(request, response, model);
    }
}
