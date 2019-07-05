package net.boocu.project.dao;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.FlowEntity;

import java.util.List;
import java.util.Map;

public interface FlowDao extends BaseDao<FlowEntity,Long>{

    List<Map<String, Object>> findPage(Map<String, Object> params, int pageNumber, int pageSize);
    Long getTotal(Map<String, Object> params);
}
