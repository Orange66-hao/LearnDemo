package net.boocu.project.service.impl;

import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import net.boocu.project.dao.ProductSubscribeDao;
import net.boocu.project.entity.ProductSubscribeEntity;
import net.boocu.project.service.ProductSubscribeService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.impl.BaseServiceImpl;
@Service("productSubscribeServiceImpl")
public class ProductSubscribeServiceImpl  extends BaseServiceImpl<ProductSubscribeEntity, Long> implements ProductSubscribeService{
	
	@Resource(name = "productSubscribeDaoImpl")
    private ProductSubscribeDao productSubscribeDao;
    
    @Resource(name = "productSubscribeDaoImpl")
    public void setBaseDao(ProductSubscribeDao productSubscribeDao) {
    	super.setBaseDao(productSubscribeDao);
    }
    public Page<ProductSubscribeEntity> findAllSubByMember(Pageable pageable, Map<String, Object> htMap){
    	return productSubscribeDao.findAllSubByMember(pageable, htMap);
    }
    @Override
    public void deleteList(Long... ids) {
    	productSubscribeDao.deleteList(ids);
    }
	@Override
	public List<ProductSubscribeEntity> findSubScribeAll() {
		// TODO Auto-generated method stub
		return productSubscribeDao.findSubScribeAll();
	}
	@Override
	public List<MemberEntity> findSubScribeAll1(String emailOrMobile) {
		// TODO Auto-generated method stub
		return productSubscribeDao.findSubScribeAll1(emailOrMobile);
	}
	@Override
	public Page<MemberEntity> findSubScribeAllMember(String startdate,String endDate,Pageable pageable) {
		// TODO Auto-generated method stub
		return productSubscribeDao.findSubScribeAllMember(startdate,endDate,pageable);
	}
	@Override
	public long getCount(String stime, String etime) {
		// TODO Auto-generated method stub
		return productSubscribeDao.getCount(stime,etime);
	}
	@Override
	public Page<MemberEntity> getCloseSubscribe(String stime, String etime,Pageable pageable) {
		// TODO Auto-generated method stub
		return productSubscribeDao.getCloseSubscribe(stime,etime,pageable);
	}
	@Override
	public long getMobileSubscribe(String stime, String etime) {
		// TODO Auto-generated method stub
		return productSubscribeDao.getMobileSubscribe(stime,etime);
	}
	@Override
	public Page<MemberEntity> getCloseMobileSubscribe(String stime, String etime,Pageable pageable) {
		// TODO Auto-generated method stub
		return productSubscribeDao.getCloseMobileSubscribe(stime,etime,pageable);
	}

	@Override
	public List<Map<String, Object>> querySubscribeTypeAndMemberList(int week, int dayOfMonth) {
		return productSubscribeDao.querySubscribeTypeAndMemberList(week,dayOfMonth);
	}

	@Override
	public List<ProductSubscribeEntity> queryConditionList(String pro_type, String member_id) {
		return productSubscribeDao.queryConditionList(pro_type,member_id);
	}

	@Override
	public List<Long> findAllMemberList() {
		return productSubscribeDao.findAllMemberList();
	}

	@Override
	public Map<String, Object> query(String stime, String etime) {
		// TODO Auto-generated method stub
		return productSubscribeDao.query(stime,etime);
	}
	
    
}
	