package net.boocu.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import net.boocu.framework.enums.SequencerDirectionEnum;

public class Page<T>
  implements Serializable
{
  private static final long serialVersionUID = 5384212692354556792L;
  private Pageable pageable;
  private List<T> cont;
  private Long total;
  private Integer totalPages;
  
  public Page()
  {
    this.pageable = new Pageable();
    this.cont = new ArrayList();
    this.total = Long.valueOf(0L);
    this.totalPages = Integer.valueOf(0);
  }
  
  public Page(Pageable pageable, List<T> cont, Long total)
  {
    this.pageable = pageable;
    this.cont = cont;
    this.total = total;
    Integer da=Integer.valueOf((int)Math.ceil(((double)total.longValue()) / getPageSize().intValue()));
   /* if (pageable.getShowtype() != null && pageable.getShowtype() == "dingyue") {
    	if (da.intValue() > 5) {
    		this.totalPages = Integer.valueOf(5);;
    	}else{
    		this.totalPages = da;
    	}
	}else{
		this.totalPages=da;
	}*/
    this.totalPages=da;
    
    if (getTotalPages().intValue() < getPageable().getPageNumber().intValue()) {
      getPageable().setPageNumber(getTotalPages());
    }
  }
  
  public Pageable getPageable()
  {
    return this.pageable;
  }
  
  public Integer getPageNumber()
  {
    return getPageable().getPageNumber();
  }
  
  public Integer getPageSize()
  {
    return getPageable().getPageSize();
  }
  
  public String getSearchProperty()
  {
    return getPageable().getSearchProperty();
  }
  
  public String getSearchValue()
  {
    return getPageable().getSearchValue();
  }
  
  public List<Filter> getFilters()
  {
    return getPageable().getFilters();
  }
  
  public String getSortProperty()
  {
    return getPageable().getSortProperty();
  }
  
  public SequencerDirectionEnum getSortDirection()
  {
    return getPageable().getSortDirection();
  }
  
  public List<Sequencer> getSequencers()
  {
    return getPageable().getSequencers();
  }
  
  public List<T> getCont()
  {
    return this.cont;
  }
  
  public Long getTotal()
  {
    return this.total;
  }
  
  public Integer getTotalPages()
  {
    return this.totalPages;
  }
}
