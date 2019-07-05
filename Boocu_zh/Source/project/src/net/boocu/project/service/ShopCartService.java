/**
 * 
 */
package net.boocu.project.service;

import java.util.List;
import java.util.Map;

import net.boocu.project.entity.ShopCart;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.BaseService;

/**
 * @author Administrator
 *
 */
public interface ShopCartService extends BaseService<ShopCart, Long>{
	
	public int insertSC(ShopCart sc);
	
	public int updateSC(ShopCart sc);
	
	public int updateCountSC(Long id,int count);
	
	public int deleteSC(Long id);
	
	public int batchDeleteSC(String ids);
	
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
