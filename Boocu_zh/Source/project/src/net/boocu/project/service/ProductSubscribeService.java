package net.boocu.project.service;

import java.util.List;
import java.util.Map;
import net.boocu.project.entity.ProductSubscribeEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.BaseService;

public interface ProductSubscribeService  extends BaseService<ProductSubscribeEntity, Long>{

	Page<ProductSubscribeEntity> findAllSubByMember(Pageable pageable, Map<String, Object> htMap);

	List<ProductSubscribeEntity> findSubScribeAll();

	List<MemberEntity> findSubScribeAll1(String emailOrMobile);

	Page<MemberEntity> findSubScribeAllMember(String startdate,String endDate,Pageable pageable);

	long getCount(String stime, String etime);

	Page<MemberEntity> getCloseSubscribe(String stime, String etime,Pageable pageable);

	long getMobileSubscribe(String stime, String etime);


	Map<String, Object> query(String stime, String etime);

	Page<MemberEntity> getCloseMobileSubscribe(String startTimeStr, String endTimeStr, Pageable pageable);


    List<Map<String,Object>> querySubscribeTypeAndMemberList(int week, int rate);

	List<ProductSubscribeEntity> queryConditionList(String pro_type, String member_id);

    List<Long> findAllMemberList();
}
