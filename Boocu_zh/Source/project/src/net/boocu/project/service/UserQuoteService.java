/**
 * 
 */
package net.boocu.project.service;

import java.util.Map;

import net.boocu.project.entity.UserQuoteEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.BaseService;

/**
 * @author Administrator
 *
 */
public interface UserQuoteService extends BaseService<UserQuoteEntity,Long>{
	public Page<UserQuoteEntity> findQuoteByPage(Pageable pageable,
			Map<String,Object> params);

	/**
	 * @param uie
	 */
	public int insertQuote(UserQuoteEntity uqe);

	/**
	 * 催促报价
	 * @param id
	 * @return
	 */
	int updateQuote(Long id);

	/**
	 * 后台管理获取询价单
	 * @param pageable
	 * @param params
	 * @return
	 */
	Page<UserQuoteEntity> quoteData(Pageable pageable, Map<String, Object> params);

	/**
	 * 报价
	 * @param id
	 * @param price
	 * @return
	 */
	int updateGive(Long id, float price);
	
	/**
	 * @param id
	 * @return
	 */
	int findById(Long id);
}
