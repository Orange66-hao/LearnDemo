package net.boocu.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.boocu.framework.enums.SequencerDirectionEnum;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Pageable
  implements Serializable
{
  private static final long serialVersionUID = 7644417473278664774L;
  private static final Integer DEFAULT_PAGE_NUMBER = Integer.valueOf(1);
  private static final Integer DEFAULT_PAGE_SIZE = Integer.valueOf(20);
  private static final Integer MAX_PAGE_SIZE = Integer.valueOf(1000);
  private Integer pageNumber = DEFAULT_PAGE_NUMBER;
  private Integer pageSize = DEFAULT_PAGE_SIZE;
  private String searchProperty;
  private String searchValue;
  private List<Filter> filters = new ArrayList();
  private String showtype;
  private String sortProperty;
  private SequencerDirectionEnum sortDirection;
  private List<Sequencer> sequencers = new ArrayList();
  
  public Pageable() {}
  
  public Pageable(Integer pageSize)
  {
    if ((pageSize != null) && (pageSize.intValue() > 0) && (pageSize.intValue() <= MAX_PAGE_SIZE.intValue())) {
      this.pageSize = pageSize;
    }
  }
  
  public Pageable(Integer pageNumber, Integer pageSize)
  {
    if ((pageNumber != null) && (pageNumber.intValue() > 0)) {
      this.pageNumber = pageNumber;
    }
    if ((pageSize != null) && (pageSize.intValue() > 0) && (pageSize.intValue() <= MAX_PAGE_SIZE.intValue())) {
      this.pageSize = pageSize;
    }
  }
  
  public Integer getPageNumber()
  {
    return this.pageNumber;
  }
  
  public void setPageNumber(Integer pageNumber)
  {
    if ((pageNumber == null) || (pageNumber.intValue() < 1)) {
      pageNumber = DEFAULT_PAGE_NUMBER;
    }
    this.pageNumber = pageNumber;
  }
  
  public Integer getPageSize()
  {
    return this.pageSize;
  }
  
  public void setPageSize(Integer pageSize)
  {
    if ((pageSize == null) || (pageSize.intValue() < 1) || (pageSize.intValue() > MAX_PAGE_SIZE.intValue())) {
      pageSize = DEFAULT_PAGE_SIZE;
    }
    this.pageSize = pageSize;
  }
  
  public String getSearchProperty()
  {
    return this.searchProperty;
  }
  
  public void setSearchProperty(String searchProperty)
  {
    this.searchProperty = searchProperty;
  }
  
  public String getSearchValue()
  {
    return this.searchValue;
  }
  
  public void setSearchValue(String searchValue)
  {
    this.searchValue = searchValue;
  }
  
  public List<Filter> getFilters()
  {
    return this.filters;
  }
  
  public void setFilters(List<Filter> filters)
  {
    this.filters = filters;
  }
  
  public String getSortProperty()
  {
    return this.sortProperty;
  }
  
  public void setSortProperty(String sortProperty)
  {
    this.sortProperty = sortProperty;
  }
  
  public SequencerDirectionEnum getSortDirection()
  {
    return this.sortDirection;
  }
  
  public void setSortDirection(SequencerDirectionEnum sortDirection)
  {
    this.sortDirection = sortDirection;
  }
  
  public List<Sequencer> getSequencers()
  {
    return this.sequencers;
  }
  
  public void setSequencers(List<Sequencer> sequencers)
  {
    this.sequencers = sequencers;
  }
  

public String getShowtype() {
	return showtype;
}

public void setShowtype(String showtype) {
	this.showtype = showtype;
}

public int hashCode()
  {
    return 
    
      new HashCodeBuilder(17, 37).append(getPageNumber()).append(getPageSize()).append(getSearchProperty()).append(getSearchValue()).append(getFilters()).append(getSortProperty()).append(getSortDirection()).append(getSequencers()).toHashCode();
  }
  
  public boolean equals(Object obj)
  {
    if (obj == this) {
      return true;
    }
    if ((isEmpty()) || (obj == null) || (getClass() != obj.getClass())) {
      return false;
    }
    Pageable pageable = (Pageable)obj;
    if (pageable.isEmpty()) {
      return false;
    }
    return 
    




      new EqualsBuilder().append(getPageNumber(), pageable.getPageNumber()).append(getPageSize(), pageable.getPageSize()).append(getSearchProperty(), pageable.getSearchProperty()).append(getSearchValue(), pageable.getSearchValue()).append(getFilters(), pageable.getFilters()).append(getSortProperty(), pageable.getSortProperty()).append(getSortDirection(), pageable.getSortDirection()).append(getSequencers(), pageable.getSequencers()).isEquals();
  }
  
  public boolean isEmpty()
  {
    return (getPageNumber() == null) || (getPageSize() == null);
  }
}
