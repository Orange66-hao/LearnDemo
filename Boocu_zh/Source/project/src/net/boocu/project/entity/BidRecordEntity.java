package net.boocu.project.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;
import net.boocu.project.entity.ProductEntity.CurrencyEnum;
import net.boocu.project.entity.ProductEntity.DateTypeEnum;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;


/**
 * 出价记录
 * v1
 * deng in 20151027
 * 
 * */

@Entity
@Table(name="jhj_bidRecord")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_bidRecord_sequence")
public class BidRecordEntity extends BaseEntity {

	/**serialVersionUID*/
	private static final long serialVersionUID = 5283196240486840619L;
	
	public enum AuctionStatus{
		done,out;
	}
	
	/**拍卖状态*/
	private AuctionStatus AuctionStatus;
	
	/**竞买号*/
	private String bidNo;
	
	/**出价*/
	private BigDecimal bidPrice ;
	
	/**出价单位*/
	private PriceUnitEnum bidPriceUnit;
	
	/**出价币别*/
	private CurrencyEnum bidPriceType;
	
	
	public AuctionStatus getAuctionStatus() {
		return AuctionStatus;
	}

	public void setAuctionStatus(AuctionStatus auctionStatus) {
		AuctionStatus = auctionStatus;
	}

	public BigDecimal getBidPrice() {
		return bidPrice;
	}

	public void setBidPrice(BigDecimal bidPrice) {
		this.bidPrice = bidPrice;
	}

	public PriceUnitEnum getBidPriceUnit() {
		return bidPriceUnit;
	}

	public void setBidPriceUnit(PriceUnitEnum bidPriceUnit) {
		this.bidPriceUnit = bidPriceUnit;
	}

	public CurrencyEnum getBidPriceType() {
		return bidPriceType;
	}

	public void setBidPriceType(CurrencyEnum bidPriceType) {
		this.bidPriceType = bidPriceType;
	}

	public String getBidNo() {
		return bidNo;
	}

	public void setBidNo(String bidNo) {
		this.bidNo = bidNo;
	}

}
