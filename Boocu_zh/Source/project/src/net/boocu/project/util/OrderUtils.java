/**
 * 
 */
package net.boocu.project.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import net.boocu.project.entity.ProductEntity;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.SysConfigService;
import net.boocu.web.util.Constants;

/**
 * @author Administrator
 *
 */
public class OrderUtils {
	
	/**
	 * 生成订单id
	 * @return
	 */
	public synchronized String genOrderNumber(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		StringBuilder sb = new StringBuilder();
		Random rd = new Random();
		for(int i = 0;i < 8;i++){
			sb.append(rd.nextInt(10));
		}
		return sdf.format(new Date()).concat(sb.toString()).toString();
	}
	
	/**
	 * 把美元转成成人民币
	 * @param member
	 * @param product
	 * @param configService
	 * @return
	 */
	public ProductEntity DollarToRmb(MemberEntity member,ProductEntity product,SysConfigService configService){
		if(product ==null){
			return null;
		}else{
			if(member == null){
				//来宾价格
				if(product.getProCustomPriceType()!=null && product.getProCustomPriceType().ordinal() == 1){
					//美元转人民币
					String rate = configService.getValByKey(Constants.DOLLERCHRMBKEY);
					if(rate !=null && !"".equals(rate)){
						if(product.getProCustomPrice()!=null&&product.getProCustomPrice().compareTo(BigDecimal.ZERO) > 0){
							product.setProCustomPrice(product.getProCustomPrice().multiply(new BigDecimal(rate)));
							return product;
						}
					}else{
						return null;
					}
				}else{
					//货币单位是人民币
					return product;
				}
			}else{
				switch (member.getMemberGradeEntity().getPriceType()) {
				case "客户":
					if(product.getProMemberPriceType() != null && product.getProMemberPriceType().ordinal() == 1){
						//美元转人民币
						String rate = configService.getValByKey(Constants.DOLLERCHRMBKEY);
						if(rate !=null && !"".equals(rate)){
							if(product.getProMemberPrice()!=null&&product.getProMemberPrice().compareTo(BigDecimal.ZERO) > 0){
								product.setProMemberPrice(product.getProMemberPrice().multiply(new BigDecimal(rate)));
								return product;
							}
						}else{
							return null;
						}
					}else{
						//货币单位是人民币
						return product;
					}
					break;
				case "同行":
					if(product.getProPeerType()!=null && product.getProPeerType().ordinal() == 1){
						//美元转人民币
						String rate = configService.getValByKey(Constants.DOLLERCHRMBKEY);
						if(rate !=null && !"".equals(rate)){
							if(product.getProPeer()!=null&&product.getProPeer().compareTo(BigDecimal.ZERO) > 0){
								product.setProPeer(product.getProPeer().multiply(new BigDecimal(rate)));
								return product;
							}
						}else{
							return null;
						}
					}else{
						//货币单位是人民币
						return product;
					}
					break;
					
				case "VIP":
					if(product.getProVipPriceType() != null && product.getProVipPriceType().ordinal() == 1){
						//美元转人民币
						String rate = configService.getValByKey(Constants.DOLLERCHRMBKEY);
						if(rate !=null && !"".equals(rate)){
							if(product.getProVipPrice()!=null&&product.getProVipPrice().compareTo(BigDecimal.ZERO) > 0){
								product.setProVipPrice(product.getProVipPrice().multiply(new BigDecimal(rate)));
								return product;
							}
						}else{
							return null;
						}
					}else{
						//货币单位是人民币
						return product;
					}
					break;
					
				case "来宾":
					if(product.getProCustomPriceType()!=null && product.getProCustomPriceType().ordinal() == 1){
						//美元转人民币
						String rate = configService.getValByKey(Constants.DOLLERCHRMBKEY);
						if(rate !=null && !"".equals(rate)){
							if(product.getProCustomPrice()!=null&&product.getProCustomPrice().compareTo(BigDecimal.ZERO) > 0){
								product.setProCustomPrice(product.getProCustomPrice().multiply(new BigDecimal(rate)));
								return product;
							}
						}else{
							return null;
						}
					}else{
						//货币单位是人民币
						return product;
					}
					break;

				default:
					return null;
				}
			}
		}
		return null;
	}
}
