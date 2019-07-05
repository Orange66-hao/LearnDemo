/**
 * 
 */
package net.boocu.pay.quartz;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import net.boocu.project.entity.Order;
import net.boocu.project.service.OrderService;

/**
 * @author Administrator
 *
 */

@Service("overTimeOrderQuartz")
public class OverTimeOrderQuartz {
	
	private static final Logger logger = Logger.getLogger(OverTimeOrderQuartz.class); 
	
	@Autowired
	private OrderService orderService;
	
	/** 每分钟执行一次 */
	//@Scheduled(cron="0 5/1 * * * ?")
	public void updateOvertimeOrder(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		//logger.info("当前线程名:"+Thread.currentThread().getName());
		logger.info("订单超时结算进程启动,当前时间戳："+sdf.format(new Date()));
		try {
			List<Order> list = orderService.getWaitPayList();
			for(Order order : list){
				//修改为交易关闭状态
				orderService.updateStatus(order.getId(), 13);
			}
		} catch (Exception e) {
			logger.info(e.getMessage(),e);
		}
		logger.info("订单超时结算进程结束,当前时间戳："+sdf.format(new Date()));
	}
}
