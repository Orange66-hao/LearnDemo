package net.boocu.project.service;

import java.util.List;
import java.util.Map;

import net.boocu.project.entity.SubscribeInfoEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.BaseService;

public interface SubscribeInfoService  extends BaseService<SubscribeInfoEntity, Long>{
	public List<Object> getModel(Long id);
	public Page<SubscribeInfoEntity> findFrontSubscribePage(Pageable pageable,
			Map map);
	public List<Long> findDistinctMemberList();
	public List<Map<String, Object>> getInfoList(Long id);
	public void deleteById(Long valueOf);
}
