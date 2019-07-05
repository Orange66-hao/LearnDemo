package net.boocu.project.bean;

import java.util.Arrays;

import javax.persistence.Entity;


/**
 * bean --前台搜索条件bean
 * fang 20150911
 * 
 * */
public class SearchConditionBean {
	
	private int pageNum;//页码
	private int pageSize ;//页记录数
	private String popuOrder;//人气排序
	private String priceOrder;//价格排序
	private String keyword;//搜索关键字
	private Long[] serTypeIds;//服务类型
	private Long[] indClass;//行业分类
	private Long[] proClass;//产品分类
	private Long[] brands;//品牌
	private String isSelf;//是否自营
	private String isNew;//是否全新
	private Long[] outputPlace;//发货地
	
	
	public int getPageNum() {
		return pageNum;
	}
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public String getPopuOrder() {
		return popuOrder;
	}
	public void setPopuOrder(String popuOrder) {
		this.popuOrder = popuOrder;
	}
	public String getPriceOrder() {
		return priceOrder;
	}
	public void setPriceOrder(String priceOrder) {
		this.priceOrder = priceOrder;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public Long[] getSerTypeIds() {
		return serTypeIds;
	}
	public void setSerTypeIds(Long[] serTypeIds) {
		this.serTypeIds = serTypeIds;
	}
	public Long[] getIndClass() {
		return indClass;
	}
	public void setIndClass(Long[] indClass) {
		this.indClass = indClass;
	}
	public Long[] getProClass() {
		return proClass;
	}
	public void setProClass(Long[] proClass) {
		this.proClass = proClass;
	}
	public Long[] getBrands() {
		return brands;
	}
	public void setBrands(Long[] brands) {
		this.brands = brands;
	}
	public String getIsSelf() {
		return isSelf;
	}
	public void setIsSelf(String isSelf) {
		this.isSelf = isSelf;
	}
	public String getIsNew() {
		return isNew;
	}
	public void setIsNew(String isNew) {
		this.isNew = isNew;
	}
	public Long[] getOutputPlace() {
		return outputPlace;
	}
	public void setOutputPlace(Long[] outputPlace) {
		this.outputPlace = outputPlace;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SearchConditionBean [pageNum=" + pageNum + ", pageSize="
				+ pageSize + ", popuOrder=" + popuOrder + ", priceOrder="
				+ priceOrder + ", keyword=" + keyword + ", serTypeIds="
				+ Arrays.toString(serTypeIds) + ", indClass="
				+ Arrays.toString(indClass) + ", proClass="
				+ Arrays.toString(proClass) + ", brands="
				+ Arrays.toString(brands) + ", isSelf=" + isSelf + ", isNew="
				+ isNew + ", outputPlace=" + Arrays.toString(outputPlace) + "]";
	}
	
	
}
