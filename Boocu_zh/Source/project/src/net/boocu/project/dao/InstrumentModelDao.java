package net.boocu.project.dao;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.InstrumentModelEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface InstrumentModelDao extends BaseDao<InstrumentModelEntity,Long> {

    Long queryrows(HttpServletRequest request, String page, String rows);

    List<Map<String, Object>> queryIndustryClassModelEntityList(long id);

    List<Map<String, Object>> queryManufacturersAndModelList(Object id, Object id1);

    void deleteManufacturersAndModelEntity(long l, Long id);

    void deleteIndustryClassModelEntity(Long id,Long id1);

    List<Map<String,Object>> query(HttpServletRequest request, String page, String rows);
}
