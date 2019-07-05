/**
 * 
 */
package net.boocu.project.quartz;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 统计最热门的型号
 * @author Administrator
 *
 */

@Service("lookTimeStatQuartz")
public class LookTimeStatQuartz {
	
	private static final Logger logger = Logger.getLogger(LookTimeStatQuartz.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	//@Scheduled(cron="0 59 23 * * ?")
	public void addLookTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		logger.info("日常最热门型号统计开始，时间戳："+sdf.format(new Date()));
		try {
			StringBuilder sql = new StringBuilder("INSERT INTO t_look_time(pro_no,look_time)");
			sql.append(" SELECT pro_no,look_time FROM jhj_product WHERE appr_status = 1 AND");
			sql.append(" proownaudit = 1 AND pro_no IS NOT NULL OR brand_id IS NOT NULL GROUP");
			sql.append(" BY pro_no ORDER BY look_time DESC LIMIT 150;");
			
			jdbcTemplate.update(sql.toString());
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
		logger.info("日常最热门型号统计结束，时间戳："+sdf.format(new Date()));
	}
}
