package net.boocu.project.controller.front;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.BasedataEntity;
import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.BasedataService;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductclassService;
import net.boocu.project.service.SysMessageService;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 资料库管理同步
 * 
 * @author deng
 *
 *         2015年8月15日
 */
@Controller("basedataFrontController")
@RequestMapping("/tongBasedata")
public class BasedataController {

	@Resource
	private SysMessageService messageService;
	@Resource
	private BasedataService basedataService;

	@Resource
	private ProductBrandService productBrandService;

	@Resource
	private ProductclassService productclassService;

	@Resource
	private IndustryClassService industryClassService;

	// 同步数据库
	@RequestMapping(value = "/Tong", method = { RequestMethod.POST, RequestMethod.GET })
	public void saleTong(HttpServletRequest request, HttpServletResponse response, Model model) {
		long brandId = ReqUtil.getLong(request, "brandId", 0l);
		String proNo = ReqUtil.getString(request, "proNo", "");
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.eq("isDel", 0));
		filters.add(Filter.eq("productBrandEntity", productBrandService.find(brandId)));
		filters.add(Filter.eq("proNo", proNo));
		filters.add(Filter.eq("apprStatus", 1));
		List<BasedataEntity> basedataEntities = basedataService.findList(filters);
		Map<String, Object> result = new HashMap<String, Object>();
		if (basedataEntities.size() > 0) {
			for (BasedataEntity item : basedataEntities) {
				result.put("result", 1);
				result.put("proName", item.getProName());
				result.put("proNameEn", item.getProNameEn());
				result.put("image", item.getImage());
				result.put("proSynopsis", item.getProSynopsis());
				result.put("proSynopsisEn", item.getProSynopsisEn());
				result.put("proContent", item.getProContent());
				result.put("proContentEn", item.getProContentEn());
				if (item.getProductclass() != null) {
					result.put("proClassId", item.getProductclass().getId());
				}
				result.put("indClassId", item.getIndustryClass());
			}
		}
		RespUtil.renderJson(response, result);
	}
}
