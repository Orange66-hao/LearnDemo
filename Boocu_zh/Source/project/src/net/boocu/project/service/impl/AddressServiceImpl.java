/**
 * 
 */
package net.boocu.project.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.boocu.project.dao.AddressDao;
import net.boocu.project.entity.AddressEntity;
import net.boocu.project.service.AddressService;
import net.boocu.web.service.impl.BaseServiceImpl;

/**
 * @author Administrator
 *
 */
@Service
public class AddressServiceImpl extends BaseServiceImpl<AddressEntity, Long> 
	implements AddressService{
	
	@Autowired
	private AddressDao addressDao;

	/* (non-Javadoc)
	 * @see net.boocu.project.service.AddressService#addAddress(net.boocu.project.entity.AddressEntity)
	 */
	@Override
	public void addAddress(AddressEntity ae) {
		addressDao.addAddress(ae);
		//查询该用户的默认收货地址id
		AddressEntity addr = addressDao.findDefAddr(ae.getMemberEntity().getId());
		//更新默认收货地址
		addressDao.updateMemberAddr(ae.getMemberEntity().getId(), Long.parseLong(addr.getId()+""));
	}
	
	/* (non-Javadoc)
	 * @see net.boocu.project.service.AddressService#findDefAddr(java.lang.Long)
	 */
	@Override
	public AddressEntity findDefAddr(Long userId) {
		return addressDao.findDefAddr(userId);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.AddressService#delAddress(java.lang.Long)
	 */
	@Override
	public void delAddress(Long id) {
		addressDao.delAddress(id);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.AddressService#updateToDefault(java.lang.Long, java.lang.Long)
	 */
	@Override
	public void updateToDefault(Long userId, Long defId) {
		addressDao.updateToDefault(userId, defId);
		addressDao.updateMemberAddr(userId, defId);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.AddressService#getAddrLists(net.boocu.project.entity.AddressEntity)
	 */
	@Override
	public List<AddressEntity> getAddrLists(AddressEntity ae) {
		return addressDao.getAddrLists(ae);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.AddressService#findById(java.lang.Long)
	 */
	@Override
	public AddressEntity findById(Long id) {
		return addressDao.findById(id);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.AddressService#updateAddr(net.boocu.project.entity.AddressEntity)
	 */
	@Override
	public int updateAddr(AddressEntity ae) {
		return addressDao.updateAddr(ae);
	}
}
