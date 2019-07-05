/**
 * 
 */
package net.boocu.project.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.boocu.project.dao.UserInquiryDao;
import net.boocu.project.entity.UserInquiryEntity;
import net.boocu.project.service.UserInquiryService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.impl.BaseServiceImpl;

/**
 * @author Administrator
 *
 */

@Service("userInquiryServiceImpl")
public class UserInquiryServiceImpl extends BaseServiceImpl<UserInquiryEntity, Long> implements UserInquiryService {
	
	@Resource
	private UserInquiryDao userInquiryDao;

	@Override
	public Page<UserInquiryEntity> findInquiryByPage(Pageable pageable, Map<String, Object> params) {
		return userInquiryDao.findInquiryByPage(pageable, params);
	}

	@Override
	public int insertInquiry(UserInquiryEntity uie) {
		return userInquiryDao.insertInquiry(uie);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.UserInquiryService#updateInquiry(java.lang.Long)
	 */
	@Override
	public int updateInquiry(Long id) {
		return userInquiryDao.inquiry(id);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.UserInquiryService#inquiryData(net.boocu.web.Pageable, java.util.Map)
	 */
	@Override
	public Page<UserInquiryEntity> inquiryData(Pageable pageable, Map<String, Object> params) {
		return userInquiryDao.inquiryData(pageable, params);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.UserInquiryService#give(int, float)
	 */
	@Override
	public int give(int id, float price) {
		return userInquiryDao.give(id, price);
	}


}
