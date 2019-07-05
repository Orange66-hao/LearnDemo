package net.boocu.project.bean;

import java.math.BigDecimal;

public class BookOrderDetailBean {
	private Long bookId;
	/** 类型 */
	private int type;

	/** 单价 */
	private BigDecimal itemPrice;

	/** 购买数量 */
	private int quantity;

	public BookOrderDetailBean(Long bookId, int type, BigDecimal itemPrice,
			int quantity) {
		setBookId(bookId);
		setType(type);
		setItemPrice(itemPrice);
		setQuantity(quantity);
	}

	public BookOrderDetailBean() {
		// TODO Auto-generated constructor stub
	}

	public Long getBookId() {
		return bookId;
	}

	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public BigDecimal getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(BigDecimal itemPrice) {
		this.itemPrice = itemPrice;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
