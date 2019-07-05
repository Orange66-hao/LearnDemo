/**
 * 
 */
package net.boocu.project.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.boocu.project.dao.UserQuoteDao;
import net.boocu.project.entity.UserQuoteEntity;
import net.boocu.project.service.UserQuoteService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.impl.BaseServiceImpl;

/**
 * @author Administrator
 *
 */
@Service
public class UserQuoteServiceImpl extends BaseServiceImpl<UserQuoteEntity, Long> implements UserQuoteService{

	@Autowired
	private UserQuoteDao userQuoteDao;
	
	@Override
	public Page<UserQuoteEntity> findQuoteByPage(Pageable pageable, Map<String, Object> params) {
		return userQuoteDao.findQuoteByPage(pageable, params);
	}

	@Override
	public int insertQuote(UserQuoteEntity uqe) {
		return userQuoteDao.insertQuote(uqe);
	}

	@Override
	public int updateQuote(Long id) {
		return userQuoteDao.quote(id);
	}

	@Override
	public Page<UserQuoteEntity> quoteData(Pageable pageable, Map<String, Object> params) {
		return userQuoteDao.quoteData(pageable, params);
	}

	@Override
	public int updateGive(Long id, float price) {
		return userQuoteDao.give(id, price);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.UserQuoteService#findById(java.lang.Long)
	 */
	@Override
	public int findById(Long id) {
		return userQuoteDao.findById(id);
	}

}
