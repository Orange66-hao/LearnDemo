package net.boocu.project.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BookOrderBean {
	
	private Long addressId;

	private int payTypeVal = 1;
	
	private BigDecimal totalCash;
	
	public Long getAddressId() {
		return addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}

	public BigDecimal getTotalCash() {
		return totalCash;
	}

	public void setTotalCash(BigDecimal totalCash) {
		this.totalCash = totalCash;
	}

	private List<BookOrderDetailBean> detailBeans=new ArrayList<BookOrderDetailBean>();

	public List<BookOrderDetailBean> getDetailBeans() {
		return detailBeans;
	}

	public void setDetailBeans(List<BookOrderDetailBean> detailBeans) {
		this.detailBeans = detailBeans;
	}

	public int getPayTypeVal() {
		return payTypeVal;
	}

	public void setPayTypeVal(int payTypeVal) {
		this.payTypeVal = payTypeVal;
	}
	
	
	
}
