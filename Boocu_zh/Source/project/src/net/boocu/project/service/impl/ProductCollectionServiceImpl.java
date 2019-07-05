package net.boocu.project.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.boocu.project.dao.ProductCollectionDao;
import net.boocu.project.dao.ProducttypeDao;
import net.boocu.project.entity.ProductCollectionEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.service.ProductCollectionService;
import net.boocu.project.service.ProductService;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.entity.MenuEntity;
import net.boocu.web.service.impl.BaseServiceImpl;

@Service("collectionServiceImpl")
public class ProductCollectionServiceImpl extends BaseServiceImpl<ProductCollectionEntity, Long>
		implements ProductCollectionService {

	@Resource(name = "collectionDaoImpl")
	private ProductCollectionDao productCollDao;

	@Resource(name = "collectionDaoImpl")
	public void setBaseDao(ProductCollectionDao productCollectionDao) {
		super.setBaseDao(productCollectionDao);
	}

	@Resource
	private ProductService proService;

	@Override
	public boolean reverseProCollection(Long[] id, String msg) {
		// TODO Auto-generated method stub
		if (id.length > 0) {
			for (Long l : id) {
				delete(l);
			}
			msg = "删除收藏成功!";
			return true;
		} else {
			msg = "请选择要删除的收藏商品信息......";
		}
		return false;
	}

	@Override
	public void saveCollection(ProductCollectionEntity entity) {
		// TODO Auto-generated method stub
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.eq("memberEntity", entity.getMemberEntity()));
		filters.add(Filter.eq("productEntity", entity.getProductEntity()));
		ProductCollectionEntity codition = find(filters);
		if (codition == null) {
			save(entity);
			ProductEntity proEntity = entity.getProductEntity();
			int count = 0;
			if (proEntity.getCollectionCount() != null) {
				count = proEntity.getCollectionCount();
			}
			proEntity.setCollectionCount(count + 1);
			proService.update(proEntity);
		}

	}

	@Override
	public Page<ProductCollectionEntity> findListCollectionPage(Pageable pageable, Map map) {
		// TODO Auto-generated method stub
		return productCollDao.findListProductPage(pageable, map);
	}

}
