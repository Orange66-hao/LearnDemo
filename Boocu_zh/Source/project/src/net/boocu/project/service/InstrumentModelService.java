package net.boocu.project.service;

import net.boocu.project.entity.IndustryClassModelEntity;
import net.boocu.project.entity.InstrumentModelEntity;
import net.boocu.web.service.BaseService;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface InstrumentModelService extends BaseService<InstrumentModelEntity, Long> {

    Long queryrows(HttpServletRequest request, HttpServletResponse response, Model model);

    List<Map<String, Object>> query(HttpServletRequest request, HttpServletResponse response, Model model);

    List<Map<String, Object>> getManufacturersAndModelList(Long instrument_model_id, Long industry_id);

    void deleteManufacturersAndModelEntity(long l, Long id);

    void deleteIndustryClassModelEntity(Long id,Long id1);
}
