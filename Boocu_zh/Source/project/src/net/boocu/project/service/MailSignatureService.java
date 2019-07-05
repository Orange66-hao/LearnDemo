package net.boocu.project.service;

import java.util.HashMap;
import java.util.Map;

import net.boocu.project.entity.MailSignatureEntity;
import net.boocu.project.entity.ProductSubscribeEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.BaseService;

public interface MailSignatureService  extends BaseService<MailSignatureEntity, Long>{

	//Page<MailSignatureEntity> findAllSubByMember(Pageable pageable, Map<String, Object> htMap);
	
}
