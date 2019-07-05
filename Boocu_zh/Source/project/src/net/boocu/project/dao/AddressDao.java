/**
 * 
 */
package net.boocu.project.dao;

import java.util.List;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.AddressEntity;

/**
 * @author Administrator
 *
 */
public interface AddressDao extends BaseDao<AddressEntity, Long> {
	
	public long addAddress(AddressEntity ae);
	
	public void delAddress(Long id);
	
	public void updateToDefault(Long userId,Long defId);

	/**
	 * @param ae
	 * @return
	 */
	public List<AddressEntity> getAddrLists(AddressEntity ae);

	/**
	 * @param userId
	 */
	public void cancelAddr(Long userId);

	/**
	 * @param userId
	 * @param addrId
	 */
	public void updateMemberAddr(Long userId, Long addrId);

	/**
	 * @param userId
	 * @return
	 */
	public AddressEntity findDefAddr(Long userId);
	
	public AddressEntity findById(Long id);

	/**
	 * @param ae
	 * @return
	 */
	public int updateAddr(AddressEntity ae);
}
