/**
 * 
 */
package net.boocu.project.dao;

import java.util.List;
import java.util.Map;

import net.boocu.project.entity.LookValEntity;

/**
 * @author Administrator
 *
 */
public interface LookValDao {

	public void addLookVal(String sql);
	
	public List<LookValEntity> lvList(Map<String,Object> params);

	/**
	 * @param params
	 * @return
	 */
	public List<LookValEntity> lvTypeList(Map<String, Object> params);

	/**
	 * @param params
	 * @return
	 */
	public List<LookValEntity> lvMostAdd(Map<String, Object> params);

	/**
	 * @param proNo
	 * @param brandId
	 * @param type
	 * @return
	 */
	public Map<String, Object> findLookInfo(String proNo, Long brandId, int type);

	/**
	 * @param proNo
	 * @param brandId
	 * @param type
	 * @return
	 */
	public int findMostUserCount(String proNo, Long brandId, int type);

	/**
	 * @param proNo
	 * @param brandId
	 * @param type
	 * @return
	 */
	public int findMostProCount(String proNo, Long brandId, int type);

	/**
	 * @param params
	 * @return
	 */
	public LookValEntity findTypeInfo(Long classId,int type);

	/**
	 * @param type
	 * @param group
	 * @return
	 */
	public List<LookValEntity> lvSubscript(int type, int group);

	/**
	 * @param proId
	 * @param group
	 * @return
	 */
	public Map<String, Object> findSubScript(Long proId, int group);
	
}
