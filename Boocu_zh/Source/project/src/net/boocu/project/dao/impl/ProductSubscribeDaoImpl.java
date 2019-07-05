package net.boocu.project.dao.impl;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.ProductSubscribeDao;
import net.boocu.project.entity.ProductSubscribeEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.entity.MemberGradeEntity;

@Repository("productSubscribeDaoImpl")
public class ProductSubscribeDaoImpl extends BaseDaoImpl<ProductSubscribeEntity, Long> implements ProductSubscribeDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Override
	public Page<ProductSubscribeEntity> findAllSubByMember(Pageable pageable, Map<String, Object> htMap) {
		// 获取条件构造器
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// 创建条件查询
		CriteriaQuery<ProductSubscribeEntity> criteriaQuery = criteriaBuilder.createQuery(ProductSubscribeEntity.class);

		// 设置查询ROOT
		Root<ProductSubscribeEntity> root = criteriaQuery.from(ProductSubscribeEntity.class);

		int appr = (int) htMap.get("appr");
		// 设置限制条件
		Predicate restrictions = criteriaBuilder.conjunction();
		criteriaQuery.distinct(false);
		if (appr == 1) { // 已分组
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder
					.isNotNull(root.<MemberEntity> get("memberEntity").<MemberGradeEntity> get("memberGradeEntity")));
		} else if (appr == 2) { // 未分组
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder
					.isNull(root.<MemberEntity> get("memberEntity").<MemberGradeEntity> get("memberGradeEntity")));
		}

		// 查找借款分页
		return findPage(criteriaBuilder, criteriaQuery, restrictions, root, pageable);
	}

	@Override
	public void deleteList(Long... ids) {
		for(int i=0;i<ids.length;i++) {
			ProductSubscribeEntity find = entityManager.find(ProductSubscribeEntity.class, ids[i]);
			find.setIsDelete(1);
		}
		
	}

	@Override
	public List<ProductSubscribeEntity> findSubScribeAll() {
		// 获取条件构造器
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// 创建条件查询
		CriteriaQuery<ProductSubscribeEntity> criteriaQuery = criteriaBuilder.createQuery(ProductSubscribeEntity.class);

		// 设置查询ROOT
		Root<ProductSubscribeEntity> root = criteriaQuery.from(ProductSubscribeEntity.class);
		Path<Integer> path = root.get("isDelete");
		Predicate predicate = criteriaBuilder.equal(path, 0);
		return entityManager.createQuery(criteriaQuery.where(predicate)).getResultList();
	}
	
	@Override
	public List<MemberEntity> findSubScribeAll1(String emailOrMobile) {
		String sql =" select distinct a.member_id as mid ,b.* from jhj_product_subscribe a,sys_member b where a.member_id=b.id and a.is_delete=0 ";
		if(emailOrMobile.equals("email")) {
			sql+=" and a.subscribe_email!=0 ";
		}else {
			sql+=" and b.member_grade_id!=1000342502 and a.subscribe_mobile!=0 ";
		}
		
		//如果不设置自定义的ResultTransformer转换器，则Hibernate将每行返回结果的数据按照结果列的顺序装入Object数组中。
		//关联表映射的时候，每个字段都需要addScalar
		Query query = entityManager.createNativeQuery(sql,MemberEntity.class);
		List<MemberEntity> resultList = query.getResultList();
		/*List<BookExpress> result = query.unwrap(SQLQuery.class)
				.addScalar("bookId", LongType.INSTANCE).addScalar("bookName").addScalar("expressId", LongType.INSTANCE).addScalar("expressName")
				.setResultTransformer(Transformers.aliasToBean(BookExpress.class)).list();*/

		return  resultList;
	}
	
	@Override
	public Page<MemberEntity> findSubScribeAllMember(String startdate,String endDate,Pageable pageable) {
		Query createNativeQuery=null;
		long count=0;
		if(startdate!=null&&endDate!=null) {
			String sql="select * from sys_member where id in (select  member_id from jhj_product_subscribe where create_date BETWEEN '"+startdate+"' and '"+endDate+"' GROUP by member_id ) ORDER BY  create_date desc";
			count=((BigInteger)entityManager.createNativeQuery("select count(*) from sys_member where id in (select  member_id from jhj_product_subscribe where create_date BETWEEN '"+startdate+"' and '"+endDate+"' GROUP by member_id ) ").getResultList().get(0)).intValue();
			if(pageable!=null) {
				sql+=" limit "+(pageable.getPageNumber()-1)*pageable.getPageSize()+","+pageable.getPageSize();
			}
			createNativeQuery = entityManager.createNativeQuery(sql,MemberEntity.class);
		}else {
			String sql="select * from sys_member where id in (select member_id from jhj_product_subscribe GROUP BY member_id) ORDER BY  create_date desc";
			count=((BigInteger)entityManager.createNativeQuery("select count(*) from sys_member where id in (select member_id from jhj_product_subscribe GROUP BY member_id)").getResultList().get(0)).intValue();
			if(pageable!=null) {
				sql+=" limit "+(pageable.getPageNumber()-1)*pageable.getPageSize()+","+pageable.getPageSize();
			}
			createNativeQuery= entityManager.createNativeQuery(sql,MemberEntity.class);
		}
		List<MemberEntity> resultList = createNativeQuery.getResultList();
		
		return new Page<MemberEntity>(pageable, resultList, count);
	}

	@Override
	public long getCount(String stime, String etime) {
		Query query
		= entityManager.createNativeQuery("select count(DISTINCT member_id) from jhj_product_subscribe where modify_date>='"+stime+"' and modify_date<='"+etime+"'");
		
		return ((BigInteger )query.getResultList().get(0)).intValue();
	}

	@Override
	public  Page<MemberEntity> getCloseSubscribe(String stime, String etime,Pageable pageable) {
		Query createNativeQuery=null;
		long count=0;
		if(StringUtils.isNotBlank(stime)&&StringUtils.isNotBlank(etime)) {
			String sql="select * from sys_member where id in (select member_id from jhj_product_subscribe where subscribe_email=0 and modify_date BETWEEN '"+stime+"' and '"+etime+"'  GROUP BY member_id) ORDER BY  create_date desc";
			count=((BigInteger)entityManager.createNativeQuery("select count(*) from sys_member where id in (select member_id from jhj_product_subscribe where subscribe_email=0 and modify_date BETWEEN '"+stime+"' and '"+etime+"'  GROUP BY member_id)").getResultList().get(0)).intValue();
			if(pageable!=null) {
				sql+=" limit "+(pageable.getPageNumber()-1)*pageable.getPageSize()+","+pageable.getPageSize();
			}
			createNativeQuery = entityManager.createNativeQuery(sql,MemberEntity.class);
		}else {
			String sql="select * from sys_member where id in (select member_id from jhj_product_subscribe where subscribe_email=0 GROUP BY member_id) ORDER BY  create_date desc";
			count=((BigInteger)entityManager.createNativeQuery("select count(*) from sys_member where id in (select member_id from jhj_product_subscribe where subscribe_email=0 GROUP BY member_id)").getResultList().get(0)).intValue();
			if(pageable!=null) {
				sql+=" limit "+(pageable.getPageNumber()-1)*pageable.getPageSize()+","+pageable.getPageSize();
			}
			createNativeQuery= entityManager.createNativeQuery(sql,MemberEntity.class);
		}
		List<MemberEntity> resultList = createNativeQuery.getResultList();
		
		return new Page<MemberEntity>(pageable, resultList, count);
	}

	@Override
	public long getMobileSubscribe(String stime, String etime) {
		Query query
		= entityManager.createNativeQuery("select count(DISTINCT member_id) from jhj_product_subscribe where modify_date>='"+stime+"' and modify_date<='"+etime+"' and subscribe_mobile!=0");
		
		return ((BigInteger )query.getResultList().get(0)).intValue();
	}

	@Override
	public Page<MemberEntity> getCloseMobileSubscribe(String stime, String etime,Pageable pageable) {
		Query createNativeQuery=null;
		long count=0;
		if(StringUtils.isNotBlank(stime)&&StringUtils.isNotBlank(etime)) {
			String sql="select * from sys_member where id in (select member_id from jhj_product_subscribe where subscribe_mobile=0 and modify_date BETWEEN '"+stime+"' and '"+etime+"'  GROUP BY member_id) ORDER BY  create_date desc";
			count=((BigInteger)entityManager.createNativeQuery("select count(*) from sys_member where id in (select member_id from jhj_product_subscribe where subscribe_mobile=0 and modify_date BETWEEN '"+stime+"' and '"+etime+"'  GROUP BY member_id)").getResultList().get(0)).intValue();
			if(pageable!=null) {
				sql+=" limit "+(pageable.getPageNumber()-1)*pageable.getPageSize()+","+pageable.getPageSize();
			}
			createNativeQuery = entityManager.createNativeQuery(sql,MemberEntity.class);
		}else {
			String sql="select * from sys_member where id in (select member_id from jhj_product_subscribe where subscribe_mobile=0 GROUP BY member_id) ORDER BY  create_date desc";
			count=((BigInteger)entityManager.createNativeQuery("select count(*) from sys_member where id in (select member_id from jhj_product_subscribe where subscribe_mobile=0 GROUP BY member_id)").getResultList().get(0)).intValue();
			if(pageable!=null) {
				sql+=" limit "+(pageable.getPageNumber()-1)*pageable.getPageSize()+","+pageable.getPageSize();
			}
			createNativeQuery= entityManager.createNativeQuery(sql,MemberEntity.class);
		}
		List<MemberEntity> resultList = createNativeQuery.getResultList();
		
		return new Page<MemberEntity>(pageable, resultList, count);
	}

	@Override
	public Map<String, Object> query(String stime, String etime) {
		long totalCompany = commonQuery("select count(DISTINCT id) from sys_member where member_ship=1");
		long addCompany = commonQuery("select count(DISTINCT id) from sys_member where create_date>='"+stime+"' and create_date<='"+etime+"' and member_ship=1");
		long LoginCompany = commonQuery("select count(DISTINCT id) from sys_member where login_date>='"+stime+"' and login_date<='"+etime+"' and member_ship=1");
		//累计产品数
		long totalProduct = commonQuery("select count(DISTINCT id) from jhj_product");
		long totalSale = commonQuery("select count(DISTINCT id) from jhj_product where producttype_id=1");
		long totalPurchase = commonQuery("select count(DISTINCT id) from jhj_product where producttype_id=2");
		long totalRentOut = commonQuery("select count(DISTINCT id) from jhj_product where producttype_id=3");
		long totalWantRent = commonQuery("select count(DISTINCT id) from jhj_product where producttype_id=4");
		long totalRepair = commonQuery("select count(DISTINCT id) from jhj_product where producttype_id=5");
		long totalSeekingRepair = commonQuery("select count(DISTINCT id) from jhj_product where producttype_id=6");
		long totalAutoTest = commonQuery("select count(DISTINCT id) from jhj_product where producttype_id=7");
		long totalProjectNeed = commonQuery("select count(DISTINCT id) from jhj_product where producttype_id=8");
		long totalInvite = commonQuery("select count(DISTINCT id) from jhj_product where producttype_id=10");
		long totalProductTest = commonQuery("select count(DISTINCT id) from jhj_product where producttype_id=11");
		long totalRequireTest = commonQuery("select count(DISTINCT id) from jhj_product where producttype_id=12");
		long totalCalibration = commonQuery("select count(DISTINCT id) from jhj_product where producttype_id=13");
		//新增产品数
		long addProduct = commonQuery("select count(DISTINCT id) from jhj_product where create_date>='"+stime+"' and create_date<='"+etime+"'");
		long addSale = commonQuery("select count(DISTINCT id) from jhj_product where create_date>='"+stime+"' and create_date<='"+etime+"' and producttype_id=1");
		long addPurchase = commonQuery("select count(DISTINCT id) from jhj_product where create_date>='"+stime+"' and create_date<='"+etime+"' and producttype_id=2");
		long addRentOut = commonQuery("select count(DISTINCT id) from jhj_product where create_date>='"+stime+"' and create_date<='"+etime+"' and producttype_id=3");
		long addWantRent = commonQuery("select count(DISTINCT id) from jhj_product where create_date>='"+stime+"' and create_date<='"+etime+"' and producttype_id=4");
		long addRepair = commonQuery("select count(DISTINCT id) from jhj_product where create_date>='"+stime+"' and create_date<='"+etime+"' and producttype_id=5");
		long addSeekingRepair = commonQuery("select count(DISTINCT id) from jhj_product where create_date>='"+stime+"' and create_date<='"+etime+"' and producttype_id=6");
		long addAutoTest = commonQuery("select count(DISTINCT id) from jhj_product where create_date>='"+stime+"' and create_date<='"+etime+"' and producttype_id=7");
		long addProjectNeed = commonQuery("select count(DISTINCT id) from jhj_product where create_date>='"+stime+"' and create_date<='"+etime+"' and producttype_id=8");
		long addInvite = commonQuery("select count(DISTINCT id) from jhj_product where create_date>='"+stime+"' and create_date<='"+etime+"' and producttype_id=10");
		long addProductTest = commonQuery("select count(DISTINCT id) from jhj_product where create_date>='"+stime+"' and create_date<='"+etime+"' and producttype_id=11");
		long addRequireTest = commonQuery("select count(DISTINCT id) from jhj_product where create_date>='"+stime+"' and create_date<='"+etime+"' and producttype_id=12");
		long addCalibration = commonQuery("select count(DISTINCT id) from jhj_product where create_date>='"+stime+"' and create_date<='"+etime+"' and producttype_id=13");
		
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("totalCompany", totalCompany);
		data.put("addCompany", addCompany);
		data.put("LoginCompany", LoginCompany);
		
		data.put("totalProduct", totalProduct);
		data.put("totalSale", totalSale);
		data.put("totalPurchase", totalPurchase);
		data.put("totalRentOut", totalRentOut);
		data.put("totalWantRent", totalWantRent);
		data.put("totalRepair", totalRepair);
		data.put("totalSeekingRepair", totalSeekingRepair);
		data.put("totalAutoTest", totalAutoTest);
		data.put("totalProjectNeed", totalProjectNeed);
		data.put("totalInvite", totalInvite);
		data.put("totalProductTest", totalProductTest);
		data.put("totalRequireTest", totalRequireTest);
		data.put("totalCalibration", totalCalibration);
		
		
		data.put("addProduct", addProduct);
		data.put("addSale", addSale);
		data.put("addPurchase", addPurchase);
		data.put("addRentOut", addRentOut);
		data.put("addWantRent", addWantRent);
		data.put("addRepair", addRepair);
		data.put("addSeekingRepair", addSeekingRepair);
		data.put("addAutoTest", addAutoTest);
		data.put("addProjectNeed", addProjectNeed);
		data.put("addInvite", addInvite);
		data.put("addProductTest", addProductTest);
		data.put("addRequireTest", addRequireTest);
		data.put("addCalibration", addCalibration);
		return data;
	}

	@Override
	public List<Map<String, Object>> querySubscribeTypeAndMemberList(int week, int dayOfMonth) {
		if(week!=-1){
			String s="select pro_type,member_id from \n" +
					"(SELECT * FROM jhj_product_subscribe WHERE find_in_set('"+dayOfMonth+"',right(subscribe_email, LENGTH(subscribe_email)-2)) \n" +
					"and subscribe_email like '3-%' \n" +
					"UNION select *   from jhj_product_subscribe  where subscribe_email like '2-%"+week+"%' UNION select * from jhj_product_subscribe where subscribe_email=1) p GROUP BY p.pro_type,p.member_id";
            return jdbcTemplate.queryForList(s);
		}
		//String sql="select pro_type,member_id from jhj_product_subscribe a where  a.is_delete=0 and a.subscribe_email!=0 and a.subscribe_email<="+1+" GROUP BY pro_type,member_id";
		return null;
	}

    @Override
    public List<ProductSubscribeEntity> queryConditionList(String pro_type, String member_id) {
        String sql="select * from jhj_product_subscribe  where pro_type="+pro_type+" and member_id="+member_id+"";
        return entityManager.createNativeQuery(sql, ProductSubscribeEntity.class).getResultList();
    }

	@Override
	public List<Long> findAllMemberList() {
		String sql="select DISTINCT member_id from jhj_product_subscribe";

		return jdbcTemplate.queryForList(sql,Long.class);
	}

	public long commonQuery(String queryString) {
		Query createNativeQuery = entityManager.createNativeQuery(queryString);
		long longData = ((BigInteger )createNativeQuery.getResultList().get(0)).intValue();
		return longData;
	}

}
