package net.boocu.web.service.impl;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import net.boocu.project.entity.ProductEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.dao.MessageAuditDao;
import net.boocu.web.service.MessageAuditService;

import org.springframework.stereotype.Service;


@Service("messageAuditServiceImpl")
public class MessageAuditServiceImpl extends BaseServiceImpl<ProductEntity, Long> implements MessageAuditService {
	@Resource(name = "messageAuditDaoImpl")
    private MessageAuditDao messageAuditDao;
    	
    @Resource(name = "messageAuditDaoImpl")
    public void setBaseDao(MessageAuditDao messageAuditDao) {
        super.setBaseDao(messageAuditDao);
    }
	@Override
	public Page<ProductEntity> findMessageAuditPage(Pageable pageable,HashMap<String, Object> map){
		return messageAuditDao.findMessageAuditPage(pageable,map);
	}

	@Override
	@Transactional
	public void deleteList(Long... ids) {
		//super.deleteList(ids);
		for(long id : ids){
			ProductEntity productEntity = find(id);
			productEntity.setIsDel(1);
			update(productEntity);
		}
	}
}
