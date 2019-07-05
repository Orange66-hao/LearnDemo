/**
 * 
 */
package net.boocu.project.service;

import java.util.Map;

import net.boocu.project.entity.UserInquiryEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.BaseService;

/**
 * @author Administrator
 *
 */
public interface UserInquiryService extends BaseService<UserInquiryEntity, Long>{
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
	public int updateInquiry(Long id);
	
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
