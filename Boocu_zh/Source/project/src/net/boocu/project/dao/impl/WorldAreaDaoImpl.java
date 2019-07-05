package net.boocu.project.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.WorldAreaDao;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.WorldAreaEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * 使用方法:如UserEntity
 * 1.将 module replaceAll 成 user (注意大小写敏感,将Case sensitive勾选)
 * 2.将 Module replaceAll 成 User (注意大小写敏感,将Case sensitive勾选)
 * */
@Repository("worldAreaDaoImpl")
public class WorldAreaDaoImpl extends BaseDaoImpl<WorldAreaEntity, Long> implements WorldAreaDao {	

	
}
