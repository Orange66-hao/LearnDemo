/**
 * 
 */
package net.boocu.project.service.impl;

import net.boocu.project.dao.DeleteLogDao;
import net.boocu.project.entity.DeleteLogEntity;
import net.boocu.project.service.DeleteLogService;
import net.boocu.web.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Administrator
 *
 */
@Service
public class DeleteLogServiceImpl extends BaseServiceImpl<DeleteLogEntity, Long>
	implements DeleteLogService{

	@Resource
	private DeleteLogDao deleteLogDao;
	@Resource(name = "deleteLogDaoImpl")
	public void setBaseDao(DeleteLogDao deleteLogDao) {
		super.setBaseDao(deleteLogDao);
	}

}
