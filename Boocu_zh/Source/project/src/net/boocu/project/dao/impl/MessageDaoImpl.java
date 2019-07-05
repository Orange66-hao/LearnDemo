package net.boocu.project.dao.impl;

import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.MessageDao;
import net.boocu.project.entity.MessageEntity;
import net.boocu.project.entity.MessageTextEntity;
import net.boocu.project.entity.SysMessageEntity;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.entity.AdminEntity;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.AdminService;
import net.boocu.web.service.MemberService;

@Repository("MessageDaoImpl")
public class MessageDaoImpl extends BaseDaoImpl<MessageEntity, Long> implements MessageDao {
	@Resource
	private AdminService adminService;

	@Resource
	MemberService memberService;

	@Override
	public Page<MessageEntity> findPageOrSendList(Pageable pageable, Map map) {
		// TODO Auto-generated method stub
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		CriteriaQuery<MessageEntity> query = builder.createQuery(MessageEntity.class);
		Root<MessageEntity> root = query.from(MessageEntity.class);
		Predicate restrictions = builder.conjunction();
		MemberEntity member = (MemberEntity) map.get("member");
		restrictions = builder.and(restrictions, builder.equal(root.<MemberEntity> get("recEntity"), member));
		restrictions = builder.or(restrictions, builder.equal(root.<MemberEntity> get("recEntity"), 0));
		if (map != null) {
			if (map.get("keyword") != null && map.get("keyword") != "") {
				restrictions = builder.and(restrictions,
						builder.equal(root.<MessageTextEntity> get("messageEntity").<String> get("message_title"),
								map.get("keyword")));
				restrictions = builder.or(restrictions,
						builder.equal(root.<MessageTextEntity> get("messageEntity").<String> get("message_context"),
								map.get("keyword")));
			}
			if (map.get("state") != null && (Integer) map.get("state") == 0) {
				restrictions = builder.and(restrictions,
						builder.equal(root.<Integer> get("state"), (Integer) map.get("state")));
			}
		}
		return findPage(builder, query, restrictions, root, pageable);
	}

	@Override
	public Page<MessageEntity> findPageOrRecByAdminList(Pageable pageable, Map map) {
		// TODO Auto-generated method stub
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<MessageEntity> query = builder.createQuery(MessageEntity.class);
		Root<MessageEntity> root = query.from(MessageEntity.class);
		Predicate restrictions = builder.conjunction();
		restrictions = builder.and(restrictions,
				builder.equal(root.<AdminEntity> get("sendEntity"), adminService.find(Filter.eq("name", "admin"))));
		if (map != null) {
			if (map.get("keyword") != null && map.get("keyword") != "") {
				restrictions = builder.and(restrictions,
						builder.like(root.<MessageTextEntity> get("messageEntity").<String> get("message_title"),
								"%" + map.get("keyword") + "%"));
				restrictions = builder.or(restrictions,
						builder.like(root.<MessageTextEntity> get("messageEntity").<String> get("message_context"),
								"%" + map.get("keyword") + "%"));
			}
			if (map.get("username") != null && map.get("username") != "") {
				restrictions = builder.and(restrictions, builder.equal(root.<MemberEntity> get("sendEntity"),
						memberService.find(Filter.eq("username", map.get("username").toString()))));
			}
		}

		return findPage(builder, query, restrictions, root, pageable);
	}

}
