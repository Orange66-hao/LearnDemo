package net.boocu.project.dao.impl;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.criterion.Expression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.framework.util.DateTimeUtil;
import net.boocu.project.dao.SubscribeInfoDao;
import net.boocu.project.entity.ProductSaleEntity;
import net.boocu.project.entity.SubscribeInfoEntity;
import net.boocu.project.service.ProductSubscribeService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.entity.MemberEntity;

@Repository("subscribeInfoDaoImpl")
public class SubscribeInfoDaoImpl extends BaseDaoImpl<SubscribeInfoEntity, Long> implements SubscribeInfoDao {
	@Autowired
	private JdbcTemplate template;

	@Override
	public List<Object> getModel(Long id) {
		// TODO Auto-generated method stub
		return template.queryForList(
				"select DATE_FORMAT(pro_info.create_date,'%Y-%m-%d') from (select * from jhj_product_subscribe_info where member_id=?) AS pro_info GROUP BY DATE_FORMAT(pro_info.create_date,'%Y-%m-%d')",
				new Object[] { id }, Object.class);
	}

	@Override
	public Page<SubscribeInfoEntity> findFrontSubscribePage(Pageable pageable, Map map) {
		// TODO Auto-generated method stub

		// 获取条件构造器
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		CriteriaQuery<SubscribeInfoEntity> query = builder.createQuery(SubscribeInfoEntity.class);
		Root<SubscribeInfoEntity> root = query.from(SubscribeInfoEntity.class);
		Predicate restrictions = builder.conjunction();
		
		MemberEntity member = (MemberEntity) map.get("member");
		restrictions = builder.and(restrictions,builder.equal(root.<Long> get("model"),0));
		restrictions=builder.or(restrictions,builder.equal(root.<Long> get("model"), 1));
		if (member != null && !"".equals(member)) {
			// 带有参数
			restrictions = builder.and(restrictions, builder.equal(root.<MemberEntity> get("memberEntity"), member));
		}
		// 筛选日期
		if (map.get("createdate") != null&&!map.get("createdate").equals("")) {
			Date date= Date.valueOf(map.get("createdate").toString());
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.DATE, 1);
			restrictions = builder.and(restrictions,
					builder.lessThanOrEqualTo(root.<Date> get("createDate"), calendar.getTime()));
			restrictions = builder.and(restrictions, builder.greaterThanOrEqualTo(root.<Date> get("createDate"), date));
		}
		return findPage(builder, query, restrictions, root, pageable);
	}

	@Override
	public List<Long> findDistinctMemberList() {
		return template.queryForList(
				"select distinct member_id from jhj_product_subscribe_info",
				Long.class);
	}

	@Override
	public List<Map<String, Object>> getInfoList(Long id) {
		return template.queryForList("select id, date from (select id, date_format(create_date,'%Y-%m-%d') date from jhj_product_subscribe_info where member_id="+id+") a " + 
				" where a.date not in (select * from (select date_format(create_date,'%Y-%m-%d') date from jhj_product_subscribe_info where member_id="+id+" group by date order by date desc limit 0,10 ) b)");
	}

	@Override
	public void deleteById(Long valueOf) {
		template.update("delete  from jhj_product_subscribe_info where id="+valueOf);
	}

}
