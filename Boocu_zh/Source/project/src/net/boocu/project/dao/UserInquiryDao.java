/**
 * 
 */
package net.boocu.project.dao;

import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.UserInquiryEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

/**
 * @author Administrator
 *
 */
public interface UserInquiryDao extends BaseDao<UserInquiryEntity, Long> {

	public Page<UserInquiryEntity> findInquiryByPage(Pageable pageable,
			Map<String,Object> params);

	/**
	 * @param uie
	 */
	public int insertInquiry(UserInquiryEntity uie);

	/**
	 * 催促报价
	 * @param id
	 * @return
	 */
	int inquiry(Long id);

	/**
	 * 后台管理获取询价单
	 * @param pageable
	 * @param params
	 * @return
	 */
	Page<UserInquiryEntity> inquiryData(Pageable pageable, Map<String, Object> params);

	/**
	 * 报价
	 * @param id
	 * @param price
	 * @return
	 */
	int give(int id, float price);
}
