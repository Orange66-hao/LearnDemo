package net.boocu.project.service.impl;

import net.boocu.project.dao.FlowDao;
import net.boocu.project.entity.FlowEntity;
import net.boocu.project.service.FlowService;
import net.boocu.web.Filter;
import net.boocu.web.service.impl.BaseServiceImpl;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("flowServiceImpl")
public class FlowServiceImpl extends BaseServiceImpl<FlowEntity, Long> implements FlowService {
	@Resource
    private FlowDao dao;
    
    @Resource 
    public void setBaseDao(FlowDao dao) {
        super.setBaseDao(dao);
    }

    @Override
    public Map<String, Object> findPage(Map<String, Object> params, int pageNumber, int pageSize) {
        List<Map<String, Object>> page = dao.findPage(params, pageNumber, pageSize);
        String stime=params.get("startTime").toString();
        String etime=params.get("endTime").toString();
        List<Filter> filters=new ArrayList<>();
        try {
            filters.add(Filter.ge("createDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(stime.equals("")?new DateFormatUtils().format(new Date(), "yyyy-MM-dd HH:mm:ss"):stime)));
            filters.add(Filter.le("createDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(etime.equals("")?new DateFormatUtils().format(new Date(), "yyyy-MM-dd HH:mm:ss"):etime)));
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        //long totalClickCount= dao.count(filters);
        Long total = dao.getTotal(params);
        Map<String, Object> resultMap=new HashMap<>();
        resultMap.put("total",total);
        resultMap.put("rows",page);
        return resultMap;
    }
    @Override
    public Long getTotal(Map<String, Object> params) {

        return dao.getTotal(params);
    }

}
