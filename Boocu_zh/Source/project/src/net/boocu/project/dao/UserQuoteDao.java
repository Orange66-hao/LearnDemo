/**
 * 
 */
package net.boocu.project.dao;

import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.UserQuoteEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

/**
 * @author Administrator
 *
 */
public interface UserQuoteDao extends BaseDao<UserQuoteEntity, Long> {

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
	int quote(Long id);

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
	int give(Long id, float price);

	/**
	 * @param id
	 * @return
	 */
	int findById(Long id);
}
