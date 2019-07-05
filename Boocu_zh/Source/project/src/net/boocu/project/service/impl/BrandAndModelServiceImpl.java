package net.boocu.project.service.impl;

import net.boocu.project.dao.BrandAndModelDao;
import net.boocu.project.dao.IndustryClassDao;
import net.boocu.project.dao.McBrandDao;
import net.boocu.project.entity.BrandAndModelEntity;
import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.BrandAndModelService;
import net.boocu.project.service.ProductclassService;
import net.boocu.web.Filter;
import net.boocu.web.service.impl.BaseServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Service
public class BrandAndModelServiceImpl extends BaseServiceImpl<BrandAndModelEntity,Long> implements BrandAndModelService{
    @Autowired
    private BrandAndModelDao brandAndModelDao;
    @Autowired
    private IndustryClassDao industryClassDao;
    @Autowired
    private ProductclassService productclassService;

    @Autowired
    public void setBaseDao(BrandAndModelDao brandAndModelDao) {
        super.setBaseDao(brandAndModelDao);
    }

    @Override
    public List<Map<String, Object>> queryrows(HttpServletRequest request, HttpServletResponse response, Model model) {
        return brandAndModelDao.queryrows(request,response,model);
    }

    @Override
    public List<Map> query(HttpServletRequest request, HttpServletResponse response, Model model) {
        //产品分类数据 industryClass
        List<Map<String, Object>> queryrows = brandAndModelDao.query(request, response, model);
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
            if (entity.get("p_id") != null && entity.get("p_id") != "") {
                item.put("p_id", entity.get("p_id"));
            }

            if (entity.get("p_name") != null && entity.get("p_name") != "") {
                item.put("p_name", entity.get("p_name"));
            }

            if (entity.get("i_id") != null && entity.get("p_id") != null) {
                List<Map<String, Object>> list = brandAndModelDao.findListByIndustryClassAndProductclass(item.get("i_id").toString(), item.get("p_id").toString());
                List<Map> bmlist=new ArrayList<>();//品牌型号集合
                if(list!=null){
                    for (Map<String, Object> map : list) {
                        Map bm=new HashMap();
                        bm.put("b_id",map.get("id"));
                        bm.put("b_model",map.get("model"));
                        bm.put("b_brand", StringUtils.isNotBlank(map.get("name_en").toString())?map.get("name_en"):map.get("brand_name"));
                        bmlist.add(bm);
                    }
                }
                item.put("bmList",bmlist);
            }
            resultList2.add(item);
        }
        return resultList2;
    }
}
