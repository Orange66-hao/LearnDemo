/**
 * 
 */
package net.boocu.project.dao;

import java.util.List;
import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.ShopCart;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

/**
 * @author Administrator
 *
 */
public interface ShopCartDao extends BaseDao<ShopCart, Long> {
	
	public int insert(ShopCart sc);
	
	public int update(ShopCart sc);
	
	public int updateCount(Long id,int count);
	
	public int delete(Long id);
	
	public int batchDelete(String ids);
	
	public int getProductByUserIdAndProductId(Long userId,Long proId);
	
	public Page<ShopCart> findShopCartByUserId(Pageable pageable, Map<String, Object> params);

	/**
	 * @param userId
	 * @param proId
	 * @return
	 */
	public int addCountOne(Long userId, Long proId);
	
	public ShopCart findById(Long id);

	/**
	 * @param params
	 * @return
	 */
	public List<ShopCart> shopCartList(Map<String, Object> params);

}
