package net.boocu.project.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import net.boocu.project.dao.ProducWantRepairDao;
import net.boocu.project.entity.ProducWantRepairEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductSaleEntity;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;
import net.boocu.project.service.ProducWantRepairService;
import net.boocu.project.service.ProductService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;


@Service("producWantRepairServiceImpl")
public class ProducWantRepairServiceImpl extends BaseServiceImpl<ProducWantRepairEntity, Long> implements ProducWantRepairService {
	@Resource(name = "producWantRepairDaoImpl")
    private ProducWantRepairDao producWanttRepairDao;
    
    @Resource(name = "producWantRepairDaoImpl")
    public void setBaseDao(ProducWantRepairDao producWanttRepairDao) {
        super.setBaseDao(producWanttRepairDao);
    }
    
    @Resource
    private ProductService productService;
    
	@Override
	public Page<ProducWantRepairEntity> getProductWantRepairPage(Pageable pageable, Map<String, Object> map) {
		return producWanttRepairDao.getProductWantRepairPage(pageable, map);
	}
    
	@Override
	public Page<ProducWantRepairEntity> findProductWantRepairPage(
			Pageable pageable, HashMap<String, Object> htMap) {
		return producWanttRepairDao.findProductWantRepairPage(pageable, htMap);
	}

	@Override
	@Transactional
	public ProducWantRepairEntity save(
			ProducWantRepairEntity producWantRepairEntity,
			ProductEntity productEntity) {
		BigDecimal price = producWantRepairEntity.getRepairPrice();
		if(producWantRepairEntity.getRepairPriceUnit() == PriceUnitEnum.millionyuan){
			price = price.multiply(new BigDecimal("10000"));
		}
		productEntity.setPrice(price);
		ProductEntity productEntityNew = productService.save(productEntity);
		producWantRepairEntity.setProductEntity(productEntityNew);
		return save(producWantRepairEntity);
	}
	
	@Override
	@Transactional
	public void deleteList(Long... ids) {
		for(long id : ids){
			ProducWantRepairEntity producWantRepairEntity = find(id);
			producWantRepairEntity.getProductEntity().setIsDel(1);
			update(producWantRepairEntity);
		}
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.ProducWantRepairService#getWantRepair(java.util.Map)
	 */
	@Override
	public List<ProducWantRepairEntity> getWantRepair(Map<String, Object> map) {
		return producWanttRepairDao.getWantRepair(map);
	}
}
