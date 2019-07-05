/**
 * 
 */
package net.boocu.project.service;

import java.util.List;

import net.boocu.project.entity.AddressEntity;
import net.boocu.web.service.BaseService;

/**
 * @author Administrator
 *
 */
public interface AddressService extends BaseService<AddressEntity, Long> {
	
	public void addAddress(AddressEntity ae);
	
	public void delAddress(Long id);
	
	public void updateToDefault(Long userId,Long defId);
	
	/**
	 * @param ae
	 * @return
	 */
	public List<AddressEntity> getAddrLists(AddressEntity ae);
	
	/**
	 * @param userId
	 * @return
	 */
	AddressEntity findDefAddr(Long userId);
	
	public AddressEntity findById(Long id);
	
	/**
	 * @param ae
	 * @return
	 */
	public int updateAddr(AddressEntity ae);
}
