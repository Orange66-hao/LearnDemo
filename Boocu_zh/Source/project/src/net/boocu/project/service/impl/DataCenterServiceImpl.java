package net.boocu.project.service.impl;

import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import net.boocu.project.dao.DataCenterDao;
import net.boocu.project.entity.DataCenterEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.service.DataCenterService;
import net.boocu.project.service.ProductService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;



@Service("dataCenterServiceImpl")
public class DataCenterServiceImpl extends BaseServiceImpl<DataCenterEntity, Long> implements DataCenterService {
	
	@Resource
	ProductService productService; 
	
	@Resource(name = "dataCenterDaoImpl")
    private DataCenterDao dataCenterDao;
    
    @Resource(name = "dataCenterDaoImpl")
    public void setBaseDao(DataCenterDao dataCenterDao) {
    	super.setBaseDao(dataCenterDao);
    }
    
	@Override
	@Transactional
	public DataCenterEntity save(DataCenterEntity dataCenterEntity,
			ProductEntity productEntity) {
		ProductEntity productEntityNew = productService.save(productEntity);
		dataCenterEntity.setProductEntity(productEntityNew);
		return save(dataCenterEntity);
	}
	
	@Override
	public Page<DataCenterEntity> findFrontDataCenterPage(Pageable pageable, Map map) {
		// TODO Auto-generated method stub
		Page<DataCenterEntity> pages = dataCenterDao.findFrontDataCenterPage(pageable, map);
		System.out.println("页数:--:"+pages.getTotalPages());
		return pages;
	}
}
