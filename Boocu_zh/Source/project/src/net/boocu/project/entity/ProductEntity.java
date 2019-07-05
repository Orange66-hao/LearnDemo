package net.boocu.project.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import net.boocu.framework.entity.BaseEntity;
import net.boocu.web.entity.MemberEntity;

/**
 * 商品基表 fang in 20150807
 * 
 */

@Entity
@Table(name="jhj_product")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "jhj_product_sequence")
public class ProductEntity extends BaseEntity {

	/** serialVersionUID */
	private static final long serialVersionUID = 1857577852647157195L;

	public enum QualityStatusEnum {
		all, nine, eight, seven, six, five, belowfive
	}

	public enum StatusEnum {
		fine, bad, unknown
	}

	public enum ProuseAddressEnum {
		inChina, outofChina
	}

	public enum DateTypeEnum {
		day, week, month
	}

	public enum CurrencyEnum {
		rmb, dollar
	}

	public enum PriceUnitEnum {
		yuan, millionyuan
	}

	public enum RateEnum {
		// 3%普通发票
		invoice,
		// 3%增值税发票
		taxInvoice,
		// 17%增值税发票
		taxSpecialInvoice
	}

	/** 商品名称 */
	private String proName;
	/** 商品名称英文 */
	private String proNameEn;
	/** 品牌 */
	private ProductBrandEntity productBrandEntity;
	/** test */
	private String proType = "";
	/** 所属商品类型 add by fang 20150906 */
	private ProducttypeEntity productType;
	/** 商品型号 */
	private String proNo;
	/** 机身号码 */
	private String prodNumber;
	/** 产品状态 */
	private StatusEnum status;
	/** 商品分类实体 add by fang 20150908 */
	private ProductclassEntity productclass;
	/** 行业分类多选 */
	private String industryClass;
	//add by zhang 2018.12.13
	private Set<IndustryClassEntity> industryClassEntities=new HashSet<>();
	/** 选件 */
	private String poption;
	/** 是否上架 0:否 1:上架 */
	private Integer proownaudit = 0;
	/** 数量 */
	private Integer proStock = 0;
	/** 设备价值 */
	private String procostPrice;
	/** 成色 */
	private QualityStatusEnum qualityStatus;
	/** 全新参考价 */
	private BigDecimal referencePrice;
	/** 全新参考价单位 */
	private PriceUnitEnum referencepricetype;
	/** 全新参考价币别 */
	private CurrencyEnum referencePriceLimit;
	/** 是否含税 */
	private Integer isTax = 0;
	/** 税率 */
	private RateEnum taxRate;
	/** 是否可计量 */
	private Integer isUnit = 0;
	/** 计量单位 */
	private String proUnit;
	/** 交货周期 */
	private String cycle;
	/** 交货周期单位 */
	private DateTypeEnum cycleUnit;
	/** 所在地 中国,国外 */
	private ProuseAddressEnum prouseAddress;
	/** 省 */
	private String areaProvince;
	/** 市 */
	private String areaCity;
	/** 区 */
	private String areaCountry;
	/** 详细地址 */
	private String proAddressDetail;
	/** 保修时间 */
	private String repairPeriod;
	/** 保修时间单位 */
	private DateTypeEnum repairPeriodUnit;
	/** 退货时间 */
	private BigDecimal returnPeriod;
	/** 退货时间单位 */
	private DateTypeEnum returnPeriodUnit;
	/** 图片1 */
	private String proOriginal1;
	/** 图片2 */
	private String proOriginal2;
	/** 图片3 */
	private String proOriginal3;
	/** 图片4 */
	private String proOriginal4;
	/** 资料下载 */
	private String downData;
	/** 资料名称 */
	private String dataName;
	/** 产品描述 */
	private String proSynopsis;
	/** 产品描述英文 */
	private String proSynopsisEn;
	/** 详细信息 */
	private String proContent;
	/** 详细信息英文 */
	private String proContentEn;
	/** 行业应用 */
	private String apply;
	/** 行业应用英文 */
	private String applyEn;
	/** 优化标题 */
	private String prometaTitle;
	/** 优化描述 */
	private String prometaDescription;
	/** 优化关键字 */
	private String proMetaKeywords;
	/** 备注 */
	private String remain;
	/** 是否显示在中文网站 */
	private Integer webzh = 0;
	/** 是否显示在英文网站 */
	private Integer weben = 0;
	/*	*//** 信息有效期 */
	/*
	 * private BigDecimal inforValidity;
	 *//** 信息有效期单位 *//*
					 * private DateTypeEnum inforValidityUnit;
					 */
	/** 信息有效期 */
	private Date inforValidity;
	/** 有效天数 */
	private Integer inforNumber = 0;
	/** 长期 */
	private Integer longTerm = 0;

	/** 发货周期 */
	private BigDecimal sendCycle;
	/** 发货周期单位 */
	private DateTypeEnum sendCycleUnit;

	/** 客户价格 */
	private BigDecimal proMemberPrice;
	/** 客户价格币别 */
	private CurrencyEnum proMemberPriceType;
	/** 客户价格单位 */
	private PriceUnitEnum proMemberPriceLimit;

	/** VIP价格 */
	private BigDecimal proVipPrice;

	/** VIP价格币别 */
	private CurrencyEnum proVipPriceType;

	/** VIP价格单位 */
	private PriceUnitEnum proVipPriceLimit;

	/** 同行价格 */
	private BigDecimal proPeer;

	/** 同行价格币别 */
	private CurrencyEnum proPeerType;

	/** 同行价格单位 */
	private PriceUnitEnum proPeerLimit;

	/** 来宾价格 */
	private BigDecimal proCustomPrice;
	/** 来宾价格币别 */
	private CurrencyEnum proCustomPriceType;
	/** 来宾价格单位 */
	private PriceUnitEnum proCustomPriceLimit;

	/** 来宾价格英文 */
	private BigDecimal proCustomPriceEn;
	/** 来宾价格英文币别 */
	private CurrencyEnum proCustomPriceEnType;
	/** 来宾价格英文单位 */
	private PriceUnitEnum proCustomPriceEnLimit;

	/** 会员价格英文 */
	private BigDecimal proMemberPriceEn;
	/** 会员价格币别英文 */
	private CurrencyEnum proMemberPriceEnType;
	/** 会员价格单位英文 */
	private PriceUnitEnum proMemberPriceEnLimit;

	// 客户发布
	/** 会员 */
	private MemberEntity memberEntity;
	/** 状态 审核与否 0:未审核 1:已审核 2:驳回 */
	private Integer apprStatus = 0;
	// 自营
	/** 驳回原因 */
	private String remark;
	/** 商品发布形式 0代表自营商品，1代表客户商品，3代表快捷发布商品 */
	private Integer proClass = 0;

	/** 是否在首页推荐中 0否 1是 */
	private Integer isPromSale = 0;

	/** 0存在,1删除 */
	private Integer isDel = 0;

	/** 浏览次数,人气 */
	private Integer lookTime = 0;
	
	private Date lookDate;

	/** 后台管理人员审核是否在促销信息中显示 0否 1是 */
	private Integer display = 0;

	/** 排序 */
	private Integer sort = 0;

	/** 促销价格 */
	private BigDecimal salesPrice;
	/** 促销价格单位 */
	private PriceUnitEnum salesPriceUnit;
	/** 促销价格币别 */
	private CurrencyEnum salesPriceType;

	/** 促销真实价格 */
	private BigDecimal salesRealPrice;
	/** 收藏次数 */
	private Integer collectionCount;

	private ProductSaleEntity productSaleEntity;
	private ProductBuyEntity productBuyEntity;
	private ProductRentEntity productRentEntity;
	private ProductWantRentEntity productWantRentEntity;
	private ProductRepairEntity productRepairEntity;
	private ProducWantRepairEntity producWanRepairEntity;
	private AutoTestEntity autoTestEntity;
	private ProjectNeedEntity projectNeedEntity;
	private ProductTestEntity productTestEntity;
	private RequireTestEntity requireTestEntity;
	private CalibrationEntity calibrationEntity;

	// 冗余变量 价格,便于排序
	private BigDecimal price;
	//是否进口 默认为进口
	private  String isImport="0";
	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@ManyToOne
	@JoinColumn(name = "member_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public MemberEntity getMemberEntity() {
		return memberEntity;
	}

	public void setMemberEntity(MemberEntity memberEntity) {
		this.memberEntity = memberEntity;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getLookDate() {
		return lookDate;
	}

	public void setLookDate(Date lookDate) {
		this.lookDate = lookDate;
	}

	public Integer getProClass() {
		return proClass;
	}

	public void setProClass(Integer proClass) {
		this.proClass = proClass;
	}

	public String getPoption() {
		return poption;
	}

	public void setPoption(String poption) {
		this.poption = poption;
	}

	public Integer getProownaudit() {
		return proownaudit;
	}

	public void setProownaudit(Integer proownaudit) {
		this.proownaudit = proownaudit;
	}

	public Integer getProStock() {
		return proStock;
	}

	public void setProStock(Integer proStock) {
		this.proStock = proStock;
	}

	public String getProcostPrice() {
		return procostPrice;
	}

	public void setProcostPrice(String procostPrice) {
		this.procostPrice = procostPrice;
	}

	public BigDecimal getReferencePrice() {
		return referencePrice;
	}

	public void setReferencePrice(BigDecimal referencePrice) {
		this.referencePrice = referencePrice;
	}

	public RateEnum getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(RateEnum taxRate) {
		this.taxRate = taxRate;
	}

	public String getCycle() {
		return cycle;
	}

	public void setCycle(String cycle) {
		this.cycle = cycle;
	}

	public PriceUnitEnum getReferencepricetype() {
		return referencepricetype;
	}

	public void setReferencepricetype(PriceUnitEnum referencepricetype) {
		this.referencepricetype = referencepricetype;
	}

	public CurrencyEnum getReferencePriceLimit() {
		return referencePriceLimit;
	}

	public void setReferencePriceLimit(CurrencyEnum referencePriceLimit) {
		this.referencePriceLimit = referencePriceLimit;
	}

	public DateTypeEnum getCycleUnit() {
		return cycleUnit;
	}

	public void setCycleUnit(DateTypeEnum cycleUnit) {
		this.cycleUnit = cycleUnit;
	}

	public String getProAddressDetail() {
		return proAddressDetail;
	}

	public void setProAddressDetail(String proAddressDetail) {
		this.proAddressDetail = proAddressDetail;
	}

	public String getRepairPeriod() {
		return repairPeriod;
	}

	public void setRepairPeriod(String repairPeriod) {
		this.repairPeriod = repairPeriod;
	}

	public DateTypeEnum getRepairPeriodUnit() {
		return repairPeriodUnit;
	}

	public void setRepairPeriodUnit(DateTypeEnum repairPeriodUnit) {
		this.repairPeriodUnit = repairPeriodUnit;
	}

	public String getProOriginal1() {
		return proOriginal1;
	}

	public void setProOriginal1(String proOriginal1) {
		this.proOriginal1 = proOriginal1;
	}

	public String getProOriginal2() {
		return proOriginal2;
	}

	public void setProOriginal2(String proOriginal2) {
		this.proOriginal2 = proOriginal2;
	}

	public String getProOriginal3() {
		return proOriginal3;
	}

	public void setProOriginal3(String proOriginal3) {
		this.proOriginal3 = proOriginal3;
	}

	public String getProOriginal4() {
		return proOriginal4;
	}

	public void setProOriginal4(String proOriginal4) {
		this.proOriginal4 = proOriginal4;
	}

	public String getDownData() {
		return downData;
	}

	public void setDownData(String downData) {
		this.downData = downData;
	}

	@Lob
	public String getProSynopsis() {
		return proSynopsis;
	}

	public void setProSynopsis(String proSynopsis) {
		this.proSynopsis = proSynopsis;
	}

	@Lob
	public String getProSynopsisEn() {
		return proSynopsisEn;
	}

	public void setProSynopsisEn(String proSynopsisEn) {
		this.proSynopsisEn = proSynopsisEn;
	}

	@Lob
	public String getProContent() {
		return proContent;
	}

	public void setProContent(String proContent) {
		this.proContent = proContent;
	}

	@Lob
	public String getProContentEn() {
		return proContentEn;
	}

	public void setProContentEn(String proContentEn) {
		this.proContentEn = proContentEn;
	}

	public String getApply() {
		return apply;
	}

	public void setApply(String apply) {
		this.apply = apply;
	}

	public String getApplyEn() {
		return applyEn;
	}

	public void setApplyEn(String applyEn) {
		this.applyEn = applyEn;
	}

	public String getPrometaTitle() {
		return prometaTitle;
	}

	public void setPrometaTitle(String prometaTitle) {
		this.prometaTitle = prometaTitle;
	}

	public String getPrometaDescription() {
		return prometaDescription;
	}

	public void setPrometaDescription(String prometaDescription) {
		this.prometaDescription = prometaDescription;
	}

	public String getProMetaKeywords() {
		return proMetaKeywords;
	}

	public void setProMetaKeywords(String proMetaKeywords) {
		this.proMetaKeywords = proMetaKeywords;
	}

	public String getRemain() {
		return remain;
	}

	public void setRemain(String remain) {
		this.remain = remain;
	}

	public Integer getWebzh() {
		return webzh;
	}

	public void setWebzh(Integer webzh) {
		this.webzh = webzh;
	}

	public Integer getWeben() {
		return weben;
	}

	public void setWeben(Integer weben) {
		this.weben = weben;
	}

	public String getProName() {
		return proName;
	}

	public void setProName(String proName) {
		this.proName = proName;
	}

	public String getProNameEn() {
		return proNameEn;
	}

	public void setProNameEn(String proNameEn) {
		this.proNameEn = proNameEn;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "brand_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public ProductBrandEntity getProductBrandEntity() {
		return productBrandEntity;
	}

	public void setProductBrandEntity(ProductBrandEntity productBrandEntity) {
		this.productBrandEntity = productBrandEntity;
	}

	@ManyToOne
	@JoinColumn(name = "producttype_id")
	public ProducttypeEntity getProductType() {
		return productType;
	}

	public void setProductType(ProducttypeEntity productType) {
		this.productType = productType;
	}

	public String getProNo() {
		return proNo;
	}

	public void setProNo(String proNo) {
		this.proNo = proNo;
	}

	public String getProdNumber() {
		return prodNumber;
	}

	public void setProdNumber(String prodNumber) {
		this.prodNumber = prodNumber;
	}

	public StatusEnum getStatus() {
		return status;
	}

	public void setStatus(StatusEnum status) {
		this.status = status;
	}

	public ProuseAddressEnum getProuseAddress() {
		return prouseAddress;
	}

	public void setProuseAddress(ProuseAddressEnum prouseAddress) {
		this.prouseAddress = prouseAddress;
	}

	public Integer getIsDel() {
		return isDel;
	}

	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}

	public QualityStatusEnum getQualityStatus() {
		return qualityStatus;
	}

	public void setQualityStatus(QualityStatusEnum qualityStatus) {
		this.qualityStatus = qualityStatus;
	}

	public Integer getIsTax() {
		return isTax;
	}

	public void setIsTax(Integer isTax) {
		this.isTax = isTax;
	}

	public BigDecimal getSendCycle() {
		return sendCycle;
	}

	public void setSendCycle(BigDecimal sendCycle) {
		this.sendCycle = sendCycle;
	}

	public BigDecimal getProMemberPrice() {
		return proMemberPrice;
	}

	public void setProMemberPrice(BigDecimal proMemberPrice) {
		this.proMemberPrice = proMemberPrice;
	}

	public CurrencyEnum getProMemberPriceType() {
		return proMemberPriceType;
	}

	public void setProMemberPriceType(CurrencyEnum proMemberPriceType) {
		this.proMemberPriceType = proMemberPriceType;
	}

	public PriceUnitEnum getProMemberPriceLimit() {
		return proMemberPriceLimit;
	}

	public void setProMemberPriceLimit(PriceUnitEnum proMemberPriceLimit) {
		this.proMemberPriceLimit = proMemberPriceLimit;
	}

	public BigDecimal getProVipPrice() {
		return proVipPrice;
	}

	public void setProVipPrice(BigDecimal proVipPrice) {
		this.proVipPrice = proVipPrice;
	}

	public CurrencyEnum getProVipPriceType() {
		return proVipPriceType;
	}

	public void setProVipPriceType(CurrencyEnum proVipPriceType) {
		this.proVipPriceType = proVipPriceType;
	}

	public PriceUnitEnum getProVipPriceLimit() {
		return proVipPriceLimit;
	}

	public void setProVipPriceLimit(PriceUnitEnum proVipPriceLimit) {
		this.proVipPriceLimit = proVipPriceLimit;
	}

	public BigDecimal getProPeer() {
		return proPeer;
	}

	public void setProPeer(BigDecimal proPeer) {
		this.proPeer = proPeer;
	}

	public CurrencyEnum getProPeerType() {
		return proPeerType;
	}

	public void setProPeerType(CurrencyEnum proPeerType) {
		this.proPeerType = proPeerType;
	}

	public PriceUnitEnum getProPeerLimit() {
		return proPeerLimit;
	}

	public void setProPeerLimit(PriceUnitEnum proPeerLimit) {
		this.proPeerLimit = proPeerLimit;
	}

	public BigDecimal getProCustomPrice() {
		return proCustomPrice;
	}

	public void setProCustomPrice(BigDecimal proCustomPrice) {
		this.proCustomPrice = proCustomPrice;
	}

	public CurrencyEnum getProCustomPriceType() {
		return proCustomPriceType;
	}

	public void setProCustomPriceType(CurrencyEnum proCustomPriceType) {
		this.proCustomPriceType = proCustomPriceType;
	}

	public PriceUnitEnum getProCustomPriceLimit() {
		return proCustomPriceLimit;
	}

	public void setProCustomPriceLimit(PriceUnitEnum proCustomPriceLimit) {
		this.proCustomPriceLimit = proCustomPriceLimit;
	}

	public BigDecimal getProCustomPriceEn() {
		return proCustomPriceEn;
	}

	public void setProCustomPriceEn(BigDecimal proCustomPriceEn) {
		this.proCustomPriceEn = proCustomPriceEn;
	}

	public CurrencyEnum getProCustomPriceEnType() {
		return proCustomPriceEnType;
	}

	public void setProCustomPriceEnType(CurrencyEnum proCustomPriceEnType) {
		this.proCustomPriceEnType = proCustomPriceEnType;
	}

	public PriceUnitEnum getProCustomPriceEnLimit() {
		return proCustomPriceEnLimit;
	}

	public void setProCustomPriceEnLimit(PriceUnitEnum proCustomPriceEnLimit) {
		this.proCustomPriceEnLimit = proCustomPriceEnLimit;
	}

	public BigDecimal getProMemberPriceEn() {
		return proMemberPriceEn;
	}

	public void setProMemberPriceEn(BigDecimal proMemberPriceEn) {
		this.proMemberPriceEn = proMemberPriceEn;
	}

	public CurrencyEnum getProMemberPriceEnType() {
		return proMemberPriceEnType;
	}

	public void setProMemberPriceEnType(CurrencyEnum proMemberPriceEnType) {
		this.proMemberPriceEnType = proMemberPriceEnType;
	}

	public PriceUnitEnum getProMemberPriceEnLimit() {
		return proMemberPriceEnLimit;
	}

	public void setProMemberPriceEnLimit(PriceUnitEnum proMemberPriceEnLimit) {
		this.proMemberPriceEnLimit = proMemberPriceEnLimit;
	}

	public Integer getIsUnit() {
		return isUnit;
	}

	public void setIsUnit(Integer isUnit) {
		this.isUnit = isUnit;
	}

	public String getProUnit() {
		return proUnit;
	}

	public void setProUnit(String proUnit) {
		this.proUnit = proUnit;
	}

	public Integer getApprStatus() {
		return apprStatus;
	}

	public void setApprStatus(Integer apprStatus) {
		this.apprStatus = apprStatus;
	}

	public Integer getIsPromSale() {
		return isPromSale;
	}

	public void setIsPromSale(Integer isPromSale) {
		this.isPromSale = isPromSale;
	}

	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
	}

	@ManyToOne
	@JoinColumn(name = "pro_class_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public ProductclassEntity getProductclass() {
		return productclass;
	}

	public void setProductclass(ProductclassEntity productclass) {
		this.productclass = productclass;
	}

	public Integer getLookTime() {
		return lookTime;
	}

	public void setLookTime(Integer lookTime) {
		this.lookTime = lookTime;
	}

	@OneToOne(mappedBy = "productEntity")
	public ProductSaleEntity getProductSaleEntity() {
		return productSaleEntity;
	}

	public void setProductSaleEntity(ProductSaleEntity productSaleEntity) {
		this.productSaleEntity = productSaleEntity;
	}

	@OneToOne(mappedBy = "productEntity")
	public ProductBuyEntity getProductBuyEntity() {
		return productBuyEntity;
	}

	public void setProductBuyEntity(ProductBuyEntity productBuyEntity) {
		this.productBuyEntity = productBuyEntity;
	}

	@OneToOne(mappedBy = "productEntity")
	public ProductRentEntity getProductRentEntity() {
		return productRentEntity;
	}

	public void setProductRentEntity(ProductRentEntity productRentEntity) {
		this.productRentEntity = productRentEntity;
	}

	@OneToOne(mappedBy = "productEntity")
	public ProductWantRentEntity getProductWantRentEntity() {
		return productWantRentEntity;
	}

	public void setProductWantRentEntity(ProductWantRentEntity productWantRentEntity) {
		this.productWantRentEntity = productWantRentEntity;
	}

	@OneToOne(mappedBy = "productEntity")
	public ProductRepairEntity getProductRepairEntity() {
		return productRepairEntity;
	}

	public void setProductRepairEntity(ProductRepairEntity productRepairEntity) {
		this.productRepairEntity = productRepairEntity;
	}

	@OneToOne(mappedBy = "productEntity")
	public ProducWantRepairEntity getProducWanRepairEntity() {
		return producWanRepairEntity;
	}

	public void setProducWanRepairEntity(ProducWantRepairEntity producWanRepairEntity) {
		this.producWanRepairEntity = producWanRepairEntity;
	}

	public DateTypeEnum getSendCycleUnit() {
		return sendCycleUnit;
	}

	public void setSendCycleUnit(DateTypeEnum sendCycleUnit) {
		this.sendCycleUnit = sendCycleUnit;
	}

	public BigDecimal getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(BigDecimal returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public DateTypeEnum getReturnPeriodUnit() {
		return returnPeriodUnit;
	}

	public void setReturnPeriodUnit(DateTypeEnum returnPeriodUnit) {
		this.returnPeriodUnit = returnPeriodUnit;
	}

	public String getIndustryClass() {
		return industryClass;
	}

	public void setIndustryClass(String industryClass) {
		this.industryClass = industryClass;
	}

	public String getAreaProvince() {
		return areaProvince;
	}

	public void setAreaProvince(String areaProvince) {
		this.areaProvince = areaProvince;
	}

	public String getAreaCity() {
		return areaCity;
	}

	public void setAreaCity(String areaCity) {
		this.areaCity = areaCity;
	}

	public String getAreaCountry() {
		return areaCountry;
	}

	public void setAreaCountry(String areaCountry) {
		this.areaCountry = areaCountry;
	}

	public Integer getDisplay() {
		return display;
	}

	public void setDisplay(Integer display) {
		this.display = display;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public BigDecimal getSalesPrice() {
		return salesPrice;
	}

	public void setSalesPrice(BigDecimal salesPrice) {
		this.salesPrice = salesPrice;
	}

	public PriceUnitEnum getSalesPriceUnit() {
		return salesPriceUnit;
	}

	public void setSalesPriceUnit(PriceUnitEnum salesPriceUnit) {
		this.salesPriceUnit = salesPriceUnit;
	}

	public CurrencyEnum getSalesPriceType() {
		return salesPriceType;
	}

	public void setSalesPriceType(CurrencyEnum salesPriceType) {
		this.salesPriceType = salesPriceType;
	}

	public BigDecimal getSalesRealPrice() {
		return salesRealPrice;
	}

	public void setSalesRealPrice(BigDecimal salesRealPrice) {
		this.salesRealPrice = salesRealPrice;
	}

	public Date getInforValidity() {
		return inforValidity;
	}

	public void setInforValidity(Date inforValidity) {
		this.inforValidity = inforValidity;
	}

	public Integer getInforNumber() {
		return inforNumber;
	}

	public void setInforNumber(Integer inforNumber) {
		this.inforNumber = inforNumber;
	}

	public String getDataName() {
		return dataName;
	}

	public void setDataName(String dataName) {
		this.dataName = dataName;
	}

	@OneToOne(mappedBy = "productEntity")
	public AutoTestEntity getAutoTestEntity() {
		return autoTestEntity;
	}

	public void setAutoTestEntity(AutoTestEntity autoTestEntity) {
		this.autoTestEntity = autoTestEntity;
	}

	@OneToOne(mappedBy = "productEntity")
	public ProjectNeedEntity getProjectNeedEntity() {
		return projectNeedEntity;
	}

	public void setProjectNeedEntity(ProjectNeedEntity projectNeedEntity) {
		this.projectNeedEntity = projectNeedEntity;
	}

	@OneToOne(mappedBy = "productEntity")
	public ProductTestEntity getProductTestEntity() {
		return productTestEntity;
	}

	public void setProductTestEntity(ProductTestEntity productTestEntity) {
		this.productTestEntity = productTestEntity;
	}

	@OneToOne(mappedBy = "productEntity")
	public RequireTestEntity getRequireTestEntity() {
		return requireTestEntity;
	}

	public void setRequireTestEntity(RequireTestEntity requireTestEntity) {
		this.requireTestEntity = requireTestEntity;
	}

	@OneToOne(mappedBy = "productEntity")
	public CalibrationEntity getCalibrationEntity() {
		return calibrationEntity;
	}

	public void setCalibrationEntity(CalibrationEntity calibrationEntity) {
		this.calibrationEntity = calibrationEntity;
	}

	public Integer getLongTerm() {
		return longTerm;
	}

	public void setLongTerm(Integer longTerm) {
		this.longTerm = longTerm;
	}

	public Integer getCollectionCount() {
		return collectionCount;
	}

	public void setCollectionCount(Integer collectionCount) {
		this.collectionCount = collectionCount;
	}

	public String getIsImport() {
		return isImport;
	}

	public void setIsImport(String isImport) {
		this.isImport = isImport;
	}
}
