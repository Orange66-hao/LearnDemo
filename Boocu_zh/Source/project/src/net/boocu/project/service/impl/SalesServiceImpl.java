package net.boocu.project.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import net.boocu.project.dao.SalesDao;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.service.SalesService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;


@Service("salesServiceImpl")
public class SalesServiceImpl extends BaseServiceImpl<ProductEntity, Long> implements SalesService {
	
	@Resource(name = "salesDaoImpl")
    private SalesDao salesDao;
    
    @Resource(name = "salesDaoImpl")
    public void setBaseDao(SalesDao salesDao) {
        super.setBaseDao(salesDao);
    }


	@Override
	public Page<ProductEntity> findFrontSalesPage(Pageable pageable, Map map) {
		// TODO Auto-generated method stub
		Page<ProductEntity> pages = salesDao.findFrontSalesPage(pageable, map);
		System.out.println("页数:--:"+pages.getTotalPages());
		return pages;
	}
	
	@Override
	public Page<ProductEntity> findSalesPage(Pageable pageable,
			HashMap<String, Object> map){
		Page<ProductEntity> pages = salesDao.findSalesPage(pageable, map);
		System.out.println("页数:--:"+pages.getTotalPages());
		return pages;
	}

}
