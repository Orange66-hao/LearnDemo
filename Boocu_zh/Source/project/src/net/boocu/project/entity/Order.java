/**
 * 
 */
package net.boocu.project.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import net.boocu.framework.entity.BaseEntity;
import net.boocu.web.entity.MemberEntity;

/**
 * @author Administrator
 *
 */

@Entity
@Table(name = "t_order")
public class Order extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1615446786282291799L;
	
	private String orderNumber;
	
	private float freight;
	
	private int status;
	
	private String deliveryAddress;
	
	//订单总价格 包含运费
	private float sumPrice;
	
	private float discountPrice;
	
	private MemberEntity buyMember;
	
	private MemberEntity sellMember;
	
	private String logisticsCompany;
	
	private String logisticsNumber;
	
	private String payCompleteTime;
	
	private int payType;
	
	private float payAmount;
	
	private String tradeCompleteTime;
	
	private String orderProductIds;
	
	private String wxPayNonceStr;
	
	private String body;
	
	private List<OrderProduct> orderProduct;
	
	private AddressEntity addressEntity;
	
	@OneToOne
	@JoinColumn(name = "recv_addr_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public AddressEntity getAddressEntity() {
		return addressEntity;
	}

	public void setAddressEntity(AddressEntity addressEntity) {
		this.addressEntity = addressEntity;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getWxPayNonceStr() {
		return wxPayNonceStr;
	}

	public void setWxPayNonceStr(String wxPayNonceStr) {
		this.wxPayNonceStr = wxPayNonceStr;
	}

	public float getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(float discountPrice) {
		this.discountPrice = discountPrice;
	}

	public String getOrderProductIds() {
		return orderProductIds;
	}

	public void setOrderProductIds(String orderProductIds) {
		this.orderProductIds = orderProductIds;
	}

	@OneToMany(mappedBy="order",cascade=CascadeType.REFRESH)
	public List<OrderProduct> getOrderProduct() {
		return orderProduct;
	}

	public void setOrderProduct(List<OrderProduct> orderProduct) {
		this.orderProduct = orderProduct;
	}

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public float getSumPrice() {
		return sumPrice;
	}

	public void setSumPrice(float sumPrice) {
		this.sumPrice = sumPrice;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public float getFreight() {
		return freight;
	}

	public void setFreight(float freight) {
		this.freight = freight;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getLogisticsCompany() {
		return logisticsCompany;
	}

	public void setLogisticsCompany(String logisticsCompany) {
		this.logisticsCompany = logisticsCompany;
	}

	public String getLogisticsNumber() {
		return logisticsNumber;
	}

	public void setLogisticsNumber(String logisticsNumber) {
		this.logisticsNumber = logisticsNumber;
	}

	public String getPayCompleteTime() {
		return payCompleteTime;
	}

	public void setPayCompleteTime(String payCompleteTime) {
		this.payCompleteTime = payCompleteTime;
	}

	public int getPayType() {
		return payType;
	}

	public void setPayType(int payType) {
		this.payType = payType;
	}

	public float getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(float payAmount) {
		this.payAmount = payAmount;
	}

	public String getTradeCompleteTime() {
		return tradeCompleteTime;
	}

	public void setTradeCompleteTime(String tradeCompleteTime) {
		this.tradeCompleteTime = tradeCompleteTime;
	}

	@OneToOne
	@JoinColumn(name = "buyer_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public MemberEntity getBuyMember() {
		return buyMember;
	}

	public void setBuyMember(MemberEntity buyMember) {
		this.buyMember = buyMember;
	}

	@OneToOne
	@JoinColumn(name = "seller_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public MemberEntity getSellMember() {
		return sellMember;
	}

	public void setSellMember(MemberEntity sellMember) {
		this.sellMember = sellMember;
	}

}
