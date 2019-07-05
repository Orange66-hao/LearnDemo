package net.boocu.web.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.NewsEnEntity;
import net.boocu.project.entity.NewsEntity;
import net.boocu.project.entity.NewsEntity.NewsTypeEnum;
import net.boocu.project.entity.NewsareaEntity;
import net.boocu.project.service.NewsEnService;
import net.boocu.project.service.NewsareaService;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 新闻管理
 * 
 * @author deng
 *
 *         2015年8月11日
 */
@Controller("newsEnController")
@RequestMapping("/admin/index/newsEn")
public class NewsEnController {

	private static final String TEM_PATH = "/template/admin/index/news";

	@Resource
	private NewsEnService newsEnService;

	@Resource
	private NewsareaService newsareaService;

	@RequestMapping(value = "toNewsList.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String toList(HttpServletRequest request, HttpServletResponse response, Model model) {
		return TEM_PATH + "/newslist";
	}

	@RequestMapping(value = "data.json", method = { RequestMethod.POST, RequestMethod.GET })
	public void dataJson(HttpServletRequest request, HttpServletResponse response, Model model) {
		String keyword = ReqUtil.getString(request, "keyword", "");
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		String sortValue = ReqUtil.getString(request, "sort", "");
		String sortOrder = ReqUtil.getString(request, "order", "desc");
		int state = ReqUtil.getInt(request, "state", 0);
		Pageable pageable = new Pageable(pagenumber, rows);
		if (!sortValue.isEmpty()) {
			List<Sequencer> ilSequencers = new ArrayList<Sequencer>();
			Sequencer sequencer = "desc".equals(sortOrder) ? Sequencer.desc(sortValue) : Sequencer.asc(sortValue);
			ilSequencers.add(sequencer);
			pageable.setSequencers(ilSequencers);
		}

		if (!keyword.isEmpty()) {
			List<Filter> flist = new ArrayList<Filter>();
			flist.add(Filter.like("title", "%" + keyword + "%"));
			pageable.getFilters().add(Filter.or(flist));
		}
		if (state == 2) {
			Page<NewsEnEntity> page = newsEnService.findPage(pageable);
			List<NewsEnEntity> newsEntities = page.getCont();
			List<Map> resultList = new ArrayList<Map>();
			for (NewsEnEntity item : newsEntities) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", item.getId());
				map.put("title", item.getTitle());
				map.put("newstype", item.getNewstype());
				map.put("content", item.getContent());
				map.put("newsareaId", item.getNewsareaEntity() == null ? "" : item.getNewsareaEntity().getName());
				map.put("creatuser", item.getCreatuser());
				map.put("updateuser", item.getUpdateuser());
				map.put("isrecommend", item.getIsrecommend() == 1 ? "推荐" : "不推荐");
				map.put("sort", item.getSort());
				resultList.add(map);
			}
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("total", page.getTotal());
			result.put("rows", resultList);
			RespUtil.renderJson(response, result);
		} else {
			Page<NewsEnEntity> page = newsEnService.findPage(pageable);
			List<NewsEnEntity> newsEntities = page.getCont();
			List<Map> resultList = new ArrayList<Map>();
			for (NewsEnEntity item : newsEntities) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", item.getId());
				map.put("title", item.getTitle());
				map.put("newstype", item.getNewstype());
				map.put("content", item.getContent());
				map.put("newsareaId", item.getNewsareaEntity() == null ? "" : item.getNewsareaEntity().getName());
				map.put("creatuser", item.getCreatuser());
				map.put("updateuser", item.getUpdateuser());
				map.put("isrecommend", item.getIsrecommend() == 1 ? "推荐" : "不推荐");
				map.put("sort", item.getSort());
				resultList.add(map);
			}
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("total", page.getTotal());
			result.put("rows", resultList);
			RespUtil.renderJson(response, result);
		}

	}

	// 跳转到村实体添加页面
	@RequestMapping(value = "/add_news.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String add_News(HttpServletRequest request, HttpServletResponse response, Model model) {
		return TEM_PATH + "/news_add";

	}

	/**
	 * 
	 * 方法:将新添加的ProductSaleEntity（本身实体 ）保存到数据库中 传入参数:ProductSaleEntity的字段
	 * 传出参数:result（方法结果信息） 逻辑：接收前台ProductSaleEntity的字段，对非空字段校验，新建实体赋值保存
	 */
	@ResponseBody
	@RequestMapping(value = "/save_news.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> saveProductSale(HttpServletRequest request, HttpServletResponse response, Model model) {

		// 获取到页面的基本信息
		String title = ReqUtil.getString(request, "title", "");
		String content = ReqUtil.getString(request, "content", "");
		String newstype = ReqUtil.getString(request, "newstype", "enterprise");
		long newsareaId = ReqUtil.getLong(request, "newsareaId", 0l);
		String creatuser = ReqUtil.getString(request, "creatuser", "");
		String updateuser = ReqUtil.getString(request, "updateuser", "");
		int isrecommend = ReqUtil.getInt(request, "isrecommend", 0);
		int sort = ReqUtil.getInt(request, "sort", 0);

		NewsEnEntity newsEntity = new NewsEnEntity();

		newsEntity.setTitle(title);
		newsEntity.setContent(content);
		newsEntity.setNewstype(NewsTypeEnum.valueOf(newstype));
		NewsareaEntity newsareaEntity = newsareaService.find(newsareaId);
		if (newsareaEntity != null) {
			newsEntity.setNewsareaEntity(newsareaEntity);
		}
		newsEntity.setCreatuser(creatuser);
		newsEntity.setUpdateuser(updateuser);
		newsEntity.setIsrecommend(isrecommend);
		newsEntity.setSort(sort);
		newsEnService.save(newsEntity);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "保存成功");
		return result;
	}

	/**
	 * 
	 * 方法:删除选中ProductSaleEntity（广告本身实体 ）并更新数据库 传入参数:id（选中实体的id数组）
	 * 传出参数:result（方法结果信息）
	 */
	@RequestMapping(value = "/delete_news.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void deleteProductSale(HttpServletRequest request, HttpServletResponse response, Model model, Long[] id) {
		if (id != null && id.length > 0) {
			this.newsEnService.deleteList(id);
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "操作成功");

		RespUtil.renderJson(response, result);
	}

	// 跳转到实体更新页面
	@RequestMapping(value = "/edit_news.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String editProductSale(HttpServletRequest request, HttpServletResponse response, Model model) {
		String id = ReqUtil.getString(request, "id", "");

		NewsEnEntity newsEntity =new NewsEnEntity();

		if (!id.equals("")) {
			Long lid = Long.parseLong(id);
			newsEntity = this.newsEnService.find(lid);
			model.addAttribute("item", newsEntity);
		}

		return TEM_PATH + "/news_edit";

	}

	/**
	 * 
	 * 方法:保存更新之后的ProductSaleEntity（村本身实体 ） 传入参数:ProductSaleEntity的字段 id（更新实体的id）
	 * 传出参数:result（方法结果信息） 逻辑：查询该id的实体，存在则读取前台ProductSaleEntity（村本身实体
	 * ）的字段并执行更新操作；否则提示更新错误
	 */
	@ResponseBody
	@RequestMapping(value = "/save_edit_news.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> saveEditNews(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		String title = ReqUtil.getString(request, "title", "");
		String content = ReqUtil.getString(request, "content", "");
		String newstype = ReqUtil.getString(request, "newstype", "enterprise");
		long newsareaId = ReqUtil.getLong(request, "newsareaId", 0l);
		String updateuser = ReqUtil.getString(request, "updateuser", "");
		int isrecommend = ReqUtil.getInt(request, "isrecommend", 0);
		int sort = ReqUtil.getInt(request, "sort", 0);

		NewsEnEntity newsEntityOle = newsEnService.find(id);
		NewsareaEntity newsareaEntity = newsareaService.find(newsareaId);
		if (newsEntityOle != null)
			if (newsareaEntity != null) {
				newsEntityOle.setNewsareaEntity(newsareaEntity);
			}
		newsEntityOle.setTitle(title);
		newsEntityOle.setContent(content);
		newsEntityOle.setNewstype(NewsTypeEnum.valueOf(newstype));
		newsEntityOle.setNewsareaEntity(newsareaEntity);
		newsEntityOle.setUpdateuser(updateuser);
		newsEntityOle.setIsrecommend(isrecommend);
		newsEntityOle.setSort(sort);
		newsEnService.update(newsEntityOle);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "保存成功");
		return result;
	}
}
