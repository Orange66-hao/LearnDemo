package net.boocu.project.service.impl;

import net.boocu.framework.util.ReqUtil;
import net.boocu.project.dao.InstrumentModelDao;
import net.boocu.project.entity.IndustryClassModelEntity;
import net.boocu.project.entity.InstrumentModelEntity;
import net.boocu.project.service.InstrumentModelService;
import net.boocu.web.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InstrumentModelServiceImpl extends BaseServiceImpl<InstrumentModelEntity,Long> implements InstrumentModelService{
    @Autowired
    private InstrumentModelDao instrumentModelDao;

    @Autowired
    public void setBaseDao(InstrumentModelDao instrumentModelDao) {
        super.setBaseDao(instrumentModelDao);
    }


    @Override
    public Long queryrows(HttpServletRequest request, HttpServletResponse response, Model model) {
        String page = ReqUtil.getString(request, "page", "");
        String rows = ReqUtil.getString(request, "rows", "");
        return  instrumentModelDao.queryrows(request, page, rows);
    }

    @Override
    public List<Map<String, Object>> query(HttpServletRequest request, HttpServletResponse response, Model model) {
        String page = ReqUtil.getString(request, "page", "");
        String rows = ReqUtil.getString(request, "rows", "");
        List<Map<String, Object>> list=instrumentModelDao.query(request,page,rows);
        List<Map<String, Object>> arr=new ArrayList<>();
        for (Map<String, Object> map : list) {
           Map m=new HashMap();
           m.put("brandAndName",map.get("pb_name").toString()+" "+map.get("model"));
           m.put("poption",map.get("poption"));
           m.put("pname",map.get("pname"));
           m.put("model",map.get("model"));
            m.put("remark",map.get("remark"));
            m.put("id",map.get("id"));
           if(map.get("id")!=null){
               List<Map<String, Object>> list1 =instrumentModelDao.queryIndustryClassModelEntityList(Long.parseLong(map.get("id").toString()));
               if(list1!=null&&list1.size()>0){
                   List arr1=new ArrayList();
                   List arr2=new ArrayList();
                   for (Map<String, Object> objectMap : list1) {
                       Map m1=new HashMap();
                       m1.put("id",objectMap.get("id"));
                       m1.put("name",objectMap.get("name"));
                       m1.put("indu_id",objectMap.get("indu_id"));
                       arr1.add(m1);
                       List<Map<String, Object>> list2=instrumentModelDao.queryManufacturersAndModelList(map.get("id"),objectMap.get("indu_id"));
                       if(list2!=null&&list2.size()>0){
                           for (Map<String, Object> stringObjectMap : list2) {
                               Map m2=new HashMap();
                               m2.put("id",stringObjectMap.get("id"));
                               m2.put("manufacturers",stringObjectMap.get("manufacturers"));
                               m2.put("model",stringObjectMap.get("model"));
                               arr2.add(m2);
                           }
                       }
                   }
                    m.put("industryList",arr1);
                   m.put("manufacturersList",arr2);
                }
           }
            arr.add(m);
        }
        return arr;
    }

    @Override
    public List<Map<String, Object>> getManufacturersAndModelList(Long instrument_model_id, Long industry_id) {
        return instrumentModelDao.queryManufacturersAndModelList(instrument_model_id,industry_id);
    }

    @Override
    public void deleteManufacturersAndModelEntity(long l, Long id) {
        instrumentModelDao.deleteManufacturersAndModelEntity(l,id);
    }

    @Override
    public void deleteIndustryClassModelEntity(Long i_id,Long indu_id) {
        instrumentModelDao.deleteIndustryClassModelEntity(i_id,indu_id);
    }
}
