package net.boocu.project.dao;

import java.util.List;
import java.util.Map;
import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.ProductSubscribeEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.entity.MemberEntity;

public interface ProductSubscribeDao extends BaseDao<ProductSubscribeEntity,Long> {
	
	public Page<ProductSubscribeEntity> findAllSubByMember(Pageable pageable, Map<String, Object> htMap);
	public void deleteList(Long... ids);
	public List<ProductSubscribeEntity> findSubScribeAll();
	List<MemberEntity> findSubScribeAll1(String emailOrMobile);
	public Page<MemberEntity> findSubScribeAllMember(String startdate,String endDate,Pageable pageable);
	public long getCount(String stime, String etime);
	public Page<MemberEntity> getCloseSubscribe(String stime, String etime,Pageable pageable);
	public long getMobileSubscribe(String stime, String etime);
	public Page<MemberEntity> getCloseMobileSubscribe(String stime, String etime, Pageable pageable);
	public Map<String, Object> query(String stime, String etime);

    List<Map<String,Object>> querySubscribeTypeAndMemberList(int week, int rate);

    List<ProductSubscribeEntity> queryConditionList(String pro_type, String member_id);

    List<Long> findAllMemberList();
}
