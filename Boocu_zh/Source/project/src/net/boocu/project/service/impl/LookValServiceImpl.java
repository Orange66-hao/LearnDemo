/**
 * 
 */
package net.boocu.project.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.boocu.project.dao.LookValDao;
import net.boocu.project.entity.LookValEntity;
import net.boocu.project.service.LookValService;

/**
 * @author Administrator
 *
 */
@Service
public class LookValServiceImpl implements LookValService {
	
	@Autowired
	private LookValDao lookValDao;

	@Override
	public void addLookVal(String sql) {
		lookValDao.addLookVal(sql);
	}

	@Override
	public List<LookValEntity> lvList(Map<String, Object> params) {
		return lookValDao.lvList(params);
	}

	@Override
	public List<LookValEntity> lvTypeList(Map<String, Object> params) {
		return lookValDao.lvTypeList(params);
	}

	@Override
	public List<LookValEntity> lvMostAdd(Map<String, Object> params) {
		return lookValDao.lvMostAdd(params);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.LookValService#findLookInfo(java.lang.String, java.lang.Long, int)
	 */
	@Override
	public Map<String, Object> findLookInfo(String proNo, Long brandId, int type) {
		return lookValDao.findLookInfo(proNo, brandId, type);
	}

	@Override
	public int findMostUserCount(String proNo, Long brandId, int type) {
		return lookValDao.findMostUserCount(proNo, brandId, type);
	}

	@Override
	public int findMostProCount(String proNo, Long brandId, int type) {
		return lookValDao.findMostProCount(proNo, brandId, type);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.LookValService#findTypeInfo(java.util.Map)
	 */
	@Override
	public LookValEntity findTypeInfo(Long classId,int type) {
		return lookValDao.findTypeInfo(classId,type);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.LookValService#lvSubscript(int, int)
	 */
	@Override
	public List<LookValEntity> lvSubscript(int type, int group) {
		return lookValDao.lvSubscript(type, group);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.LookValService#findSubScript(java.lang.Long, int)
	 */
	@Override
	public Map<String, Object> findSubScript(Long proId, int group) {
		return lookValDao.findSubScript(proId, group);
	}
	
}
