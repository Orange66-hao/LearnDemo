package net.boocu.project.util;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;

import net.boocu.project.entity.SubscribeInfoEntity;
import net.boocu.project.service.SubscribeInfoService;
import net.boocu.web.Filter;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.MemberService;

public class DeleteSubscirbeRecord {
	
	@Resource(name = "subscribeInfoServiceImpl")
	private SubscribeInfoService subscribeInfoService;
	
	@Resource(name = "memberServiceImpl")
	private MemberService memberService;
	//@Scheduled(cron = "0 * * * * ?")
	public void deleteRecord() {
		List<Long> memberIdList=subscribeInfoService.findDistinctMemberList();
		Iterator<Long> iterator = memberIdList.iterator();
		
		while(iterator.hasNext()) {
			List<Map<String, Object>> infoList = subscribeInfoService.getInfoList(iterator.next());
			Iterator<Map<String, Object>> iterator2 = infoList.iterator();
			while(iterator2.hasNext()) {
				subscribeInfoService.deleteById(Long.valueOf(iterator2.next().get("id").toString()));
			}
		}
	}
}
