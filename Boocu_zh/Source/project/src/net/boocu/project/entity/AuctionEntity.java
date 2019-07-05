package net.boocu.project.entity;

import java.math.BigDecimal;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;
import net.boocu.project.entity.ProductEntity.CurrencyEnum;
import net.boocu.project.entity.ProductEntity.DateTypeEnum;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;


/**
 * 拍卖
 * v1
 * deng in 20151027
 * 
 * */

@Entity
@Table(name="jhj_Auction")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_auction_sequence")
public class AuctionEntity extends BaseEntity {


	/**serialVersionUID*/
	private static final long serialVersionUID = -6646088500385263980L;

	/**商品基表*/
	private ProductEntity productEntity;
	
	/**出价记录表*/
	private BidRecordEntity bidRecordEntity;
	
	/**起拍价*/
	private BigDecimal startPrice;
	
	/**起拍价单位*/
	private PriceUnitEnum startPriceUnit;
	
	/**起拍价币别*/
	private CurrencyEnum startPriceType;
	
	/**加价幅度*/
	private BigDecimal increaseRange;
	
	/**起拍价单位*/
	private PriceUnitEnum increaseRangeUnit;
	
	/**起拍价币别*/
	private CurrencyEnum increaseRangeType;
	
	/**开始时间*/
	private Date startTime;
	
	/**结束时间*/
	private Date terminalTime;
	
	/**评估价*/
	private BigDecimal evaluationPrice;
	
	/**评估价单位*/
	private PriceUnitEnum evaluationPriceUnit;
	
	/**评估价币别*/
	private CurrencyEnum evaluationPriceType;
	
	/**保证金*/
	private BigDecimal cashDeposit;
	
	/**保证金单位*/
	private PriceUnitEnum cashDepositUnit;
	
	/**保证金币别*/
	private CurrencyEnum cashDepositType;
	
	/**延时周期*/
	private String delayPeriod;
	
	/**保留价*/
	private BigDecimal reservePrice ;
	
	/**保留价单位*/
	private PriceUnitEnum reservePriceUnit;
	
	/**保留价币别*/
	private CurrencyEnum reservePriceType;
	
	@OneToMany
	@JoinColumn(name="bidRecord_id")
	public void setBidRecordEntity(BidRecordEntity bidRecordEntity) {
		this.bidRecordEntity = bidRecordEntity;
	}

	public void setProductEntity(ProductEntity productEntity) {
		this.productEntity = productEntity;
	}
	
	@OneToOne
	@JoinColumn(name="product_id")
	public ProductEntity getProductEntity() {
		return productEntity;
	}

	public BidRecordEntity getBidRecordEntity() {
		return bidRecordEntity;
	}

	public BigDecimal getStartPrice() {
		return startPrice;
	}

	public void setStartPrice(BigDecimal startPrice) {
		this.startPrice = startPrice;
	}

	public PriceUnitEnum getStartPriceUnit() {
		return startPriceUnit;
	}

	public void setStartPriceUnit(PriceUnitEnum startPriceUnit) {
		this.startPriceUnit = startPriceUnit;
	}

	public CurrencyEnum getStartPriceType() {
		return startPriceType;
	}

	public void setStartPriceType(CurrencyEnum startPriceType) {
		this.startPriceType = startPriceType;
	}

	public BigDecimal getIncreaseRange() {
		return increaseRange;
	}

	public void setIncreaseRange(BigDecimal increaseRange) {
		this.increaseRange = increaseRange;
	}

	public PriceUnitEnum getIncreaseRangeUnit() {
		return increaseRangeUnit;
	}

	public void setIncreaseRangeUnit(PriceUnitEnum increaseRangeUnit) {
		this.increaseRangeUnit = increaseRangeUnit;
	}

	public CurrencyEnum getIncreaseRangeType() {
		return increaseRangeType;
	}

	public void setIncreaseRangeType(CurrencyEnum increaseRangeType) {
		this.increaseRangeType = increaseRangeType;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getTerminalTime() {
		return terminalTime;
	}

	public void setTerminalTime(Date terminalTime) {
		this.terminalTime = terminalTime;
	}

	public BigDecimal getEvaluationPrice() {
		return evaluationPrice;
	}

	public void setEvaluationPrice(BigDecimal evaluationPrice) {
		this.evaluationPrice = evaluationPrice;
	}

	public PriceUnitEnum getEvaluationPriceUnit() {
		return evaluationPriceUnit;
	}

	public void setEvaluationPriceUnit(PriceUnitEnum evaluationPriceUnit) {
		this.evaluationPriceUnit = evaluationPriceUnit;
	}

	public CurrencyEnum getEvaluationPriceType() {
		return evaluationPriceType;
	}

	public void setEvaluationPriceType(CurrencyEnum evaluationPriceType) {
		this.evaluationPriceType = evaluationPriceType;
	}

	public BigDecimal getCashDeposit() {
		return cashDeposit;
	}

	public void setCashDeposit(BigDecimal cashDeposit) {
		this.cashDeposit = cashDeposit;
	}

	public PriceUnitEnum getCashDepositUnit() {
		return cashDepositUnit;
	}

	public void setCashDepositUnit(PriceUnitEnum cashDepositUnit) {
		this.cashDepositUnit = cashDepositUnit;
	}

	public CurrencyEnum getCashDepositType() {
		return cashDepositType;
	}

	public void setCashDepositType(CurrencyEnum cashDepositType) {
		this.cashDepositType = cashDepositType;
	}

	public String getDelayPeriod() {
		return delayPeriod;
	}

	public void setDelayPeriod(String delayPeriod) {
		this.delayPeriod = delayPeriod;
	}

	public BigDecimal getReservePrice() {
		return reservePrice;
	}

	public void setReservePrice(BigDecimal reservePrice) {
		this.reservePrice = reservePrice;
	}

	public PriceUnitEnum getReservePriceUnit() {
		return reservePriceUnit;
	}

	public void setReservePriceUnit(PriceUnitEnum reservePriceUnit) {
		this.reservePriceUnit = reservePriceUnit;
	}

	public CurrencyEnum getReservePriceType() {
		return reservePriceType;
	}

	public void setReservePriceType(CurrencyEnum reservePriceType) {
		this.reservePriceType = reservePriceType;
	}	
}
