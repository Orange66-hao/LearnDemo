package net.boocu.project.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.FlowDao;
import net.boocu.project.entity.FlowEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.persistence.Query;
import java.util.List;
import java.util.Map;

@Service("flowDaoImpl")
public class FlowDaoImpl extends BaseDaoImpl<FlowEntity, Long> implements FlowDao{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Override
    public List<Map<String, Object>> findPage(Map<String, Object> params, int pageNumber, int pageSize) {
        String sql="select f.id,f.open_type,m.username,m.co_name,p.pro_name,MAX(f.create_date) createDate,COUNT(f.product_id) clickCount from sys_flow f,sys_member m,jhj_product p where 1=1  and f.member_id=m.id and f.product_id=p.id  ";
        if(!"".equals(params.get("startTime").toString())){
            sql+=" and unix_timestamp(f.create_date) >=unix_timestamp(\""+params.get("startTime").toString()+"\")";
        }
        if(!"".equals(params.get("endTime").toString())){
            sql+=" and unix_timestamp(f.create_date) <=unix_timestamp(\""+params.get("endTime").toString()+"\")";
        }
        sql+="group by f.member_id,f.product_id ORDER BY createDate DESC limit "+(pageNumber-1)*pageSize+","+pageSize;

        return  jdbcTemplate.queryForList(sql);
    }

    @Override
    public Long getTotal(Map<String, Object> params) {
        String sql="select count(1) c from (select f.id from sys_flow f,sys_member m ,jhj_product p where f.member_id=m.id and f.product_id=p.id ";

        if(!"".equals(params.get("startTime").toString())){
            sql+=" and unix_timestamp(f.create_date) >=unix_timestamp(\""+params.get("startTime").toString()+"\")";
        }
        if(!"".equals(params.get("endTime").toString())){
            sql+=" and unix_timestamp(f.create_date) <=unix_timestamp(\""+params.get("endTime").toString()+"\")";
        }
        sql+=" group by f.member_id,f.product_id) tb";
        return Long.parseLong(jdbcTemplate.queryForList(sql).get(0).get("c").toString());
    }

}
