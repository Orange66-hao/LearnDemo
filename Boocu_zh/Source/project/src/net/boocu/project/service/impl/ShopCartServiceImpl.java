/**
 * 
 */
package net.boocu.project.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.boocu.project.dao.ShopCartDao;
import net.boocu.project.entity.ShopCart;
import net.boocu.project.service.ShopCartService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.impl.BaseServiceImpl;

/**
 * @author Administrator
 *
 */
@Service
public class ShopCartServiceImpl extends BaseServiceImpl<ShopCart, Long>
	implements ShopCartService {
	
	@Autowired
	private ShopCartDao shopCartDao;

	/* (non-Javadoc)
	 * @see net.boocu.project.service.ShopCartService#insertSC(net.boocu.project.entity.ShopCart)
	 */
	@Override
	public int insertSC(ShopCart sc) {
		return shopCartDao.insert(sc);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.ShopCartService#updateSC(net.boocu.project.entity.ShopCart)
	 */
	@Override
	public int updateSC(ShopCart sc) {
		return shopCartDao.update(sc);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.ShopCartService#updateCountSC(java.lang.Long, int)
	 */
	@Override
	public int updateCountSC(Long id, int count) {
		return shopCartDao.updateCount(id, count);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.ShopCartService#deleteSC(java.lang.Long)
	 */
	@Override
	public int deleteSC(Long id) {
		return shopCartDao.delete(id);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.ShopCartService#batchDeleteSC(java.lang.String)
	 */
	@Override
	public int batchDeleteSC(String ids) {
		StringBuilder sb = new StringBuilder(ids);
		sb.deleteCharAt(ids.lastIndexOf(","));
		return shopCartDao.batchDelete(sb.toString());
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.ShopCartService#findShopCartByUserId(net.boocu.web.Pageable, java.util.Map)
	 */
	@Override
	public Page<ShopCart> findShopCartByUserId(Pageable pageable, Map<String, Object> params) {
		return shopCartDao.findShopCartByUserId(pageable, params);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.ShopCartService#getProductByUserIdAndProductId(java.lang.Long, java.lang.Long)
	 */
	@Override
	public int getProductByUserIdAndProductId(Long userId, Long proId) {
		return shopCartDao.getProductByUserIdAndProductId(userId, proId);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.ShopCartService#addCountOne(java.lang.Long, java.lang.Long)
	 */
	@Override
	public int addCountOne(Long userId, Long proId) {
		return shopCartDao.addCountOne(userId, proId);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.ShopCartService#findById(java.lang.Long)
	 */
	@Override
	public ShopCart findById(Long id) {
		return shopCartDao.findById(id);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.ShopCartService#shopCartList(java.util.Map)
	 */
	@Override
	public List<ShopCart> shopCartList(Map<String, Object> params) {
		return shopCartDao.shopCartList(params);
	}
}
