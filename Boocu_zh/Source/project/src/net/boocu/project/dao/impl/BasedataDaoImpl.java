package net.boocu.project.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.BasedataDao;
import net.boocu.project.entity.BasedataEntity;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.deser.Deserializers.Base;

@Repository("basedataDaoImpl")
public class BasedataDaoImpl extends BaseDaoImpl<BasedataEntity, Long> implements BasedataDao {
}
