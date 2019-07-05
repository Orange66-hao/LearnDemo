package net.boocu.project.service;

import net.boocu.project.entity.FlowEntity;
import net.boocu.web.service.BaseService;

import java.util.Map;

public interface FlowService extends BaseService<FlowEntity, Long>{

    Map<String, Object> findPage(Map<String, Object> params, int pageNumber, int pageSize);
    Long getTotal(Map<String, Object> params);
}
