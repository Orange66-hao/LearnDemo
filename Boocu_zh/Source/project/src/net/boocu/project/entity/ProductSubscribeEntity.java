package net.boocu.project.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;
import net.boocu.web.entity.MemberEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import sun.applet.Main;

/**
 * 订阅 v1
 * 
 */

@Entity
@Table(name = "jhj_product_subscribe")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "jhj_product_subscribe_sequence")
public class ProductSubscribeEntity extends BaseEntity {
	/** serialVersionUID */
	private static final long serialVersionUID = 3170588356426004030L;
	/** 用户实体对象*/
	private MemberEntity memberEntity;
	/** 产品实体对象*/
	private ProductEntity productEntity;
	//品牌对象
	private ProductBrandEntity productBrandEntity;
	/** 订阅的产品类型   11项服务类型*/
	private String proType;
	/** 型号*/
	private String model;
	/** 费了频率 */
	private Long rate;
	/** 按产品类型订阅 */
	private ProductclassEntity proClassEntity;
	/** 按行业分类订阅*/
	private  IndustryClassEntity indClassEntity;
	/** 邮箱订阅频率  1  2  3  每天 每周每月*/
	private  String subscribeEmail;
	/** 手机订阅频率*/
	private  String subscribeMobile;
	/** 平台订阅频率*/
	private  Integer subscribePlatform;
	/**是否删除* 0默认不删除  1 为删除*/
	private  Integer isDelete=0;

	/**
	 * 订阅条件  1全新 2 二手3 进口 4国产  使用字符串拼接的方式  例如 订阅全新二手 保存到数据库为 1 2
	 * 默认为 1234  全部勾选订阅
	 */
	private String subscribeTerm="1234";

	public String getSubscribeTerm() {
		return subscribeTerm;
	}

	public void setSubscribeTerm(String subscribeTerm) {
		this.subscribeTerm = subscribeTerm;
	}

	@ManyToOne
	@JoinColumn(name = "member_id")
	public MemberEntity getMemberEntity() {
		return memberEntity;
	}
	public void setMemberEntity(MemberEntity memberEntity) {
		this.memberEntity = memberEntity;
	}
	@ManyToOne
	@JoinColumn(name = "product_id")
	public ProductEntity getProductEntity() {
		return productEntity;
	}
	public void setProductEntity(ProductEntity productEntity) {
		this.productEntity = productEntity;
	}
	public String getProType() {
		return proType;
	}
	public void setProType(String proType) {
		this.proType = proType;
	}

	public Long getRate() {
		return rate;
	}
	public void setRate(Long rate) {
		this.rate = rate;
	}
	@ManyToOne
	@JoinColumn(name = "productclass_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public ProductclassEntity getProClassEntity() {
		return proClassEntity;
	}
	public void setProClassEntity(ProductclassEntity proClassEntity) {
		this.proClassEntity = proClassEntity;
	}
	@ManyToOne
	@JoinColumn(name = "industryclass_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public IndustryClassEntity getIndClassEntity() {
		return indClassEntity;
	}
	public void setIndClassEntity(IndustryClassEntity indClassEntity) {
		this.indClassEntity = indClassEntity;
	}

	public String getSubscribeEmail() {
		return subscribeEmail;
	}

	public void setSubscribeEmail(String subscribeEmail) {
		this.subscribeEmail = subscribeEmail;
	}

	public String getSubscribeMobile() {
		return subscribeMobile;
	}

	public void setSubscribeMobile(String subscribeMobile) {
		this.subscribeMobile = subscribeMobile;
	}

	public Integer getSubscribePlatform() {
		return subscribePlatform;
	}
	public void setSubscribePlatform(Integer subscribePlatform) {
		this.subscribePlatform = subscribePlatform;
	}
    @ManyToOne
    @JoinColumn(name = "brand_id")
    public ProductBrandEntity getProductBrandEntity() {
        return productBrandEntity;
    }

    public void setProductBrandEntity(ProductBrandEntity productBrandEntity) {
        this.productBrandEntity = productBrandEntity;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((indClassEntity == null) ? 0 : indClassEntity.hashCode());
		result = prime * result + ((memberEntity == null) ? 0 : memberEntity.hashCode());
		result = prime * result + ((proClassEntity == null) ? 0 : proClassEntity.hashCode());
		result = prime * result + ((proType == null) ? 0 : proType.hashCode());
		result = prime * result + ((productEntity == null) ? 0 : productEntity.hashCode());
		result = prime * result + ((subscribeEmail == null) ? 0 : subscribeEmail.hashCode());
		result = prime * result + ((subscribeMobile == null) ? 0 : subscribeMobile.hashCode());
		result = prime * result + ((subscribePlatform == null) ? 0 : subscribePlatform.hashCode());
		return result;
	}

	
}
