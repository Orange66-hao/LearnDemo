/**
 * 
 */
package net.boocu.project.util;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Administrator
 *
 */
@SuppressWarnings("unchecked")
public class PageUtils implements Serializable{
	private static final long serialVersionUID = 2387515373707451333L;
	  private JdbcTemplate jdbcTemplate;
	  private int start = 1;
	  private String countSelect = "select count(*) ";
	  private String select = "";
	  private String from = "";
	  private String where = "";
	  private String countFrom = "";
	  private String countWhere = "";
	  private String groupBy = "";
	  private String orderBy = "";
	  private List<Object> parametersName;
	  private List<Object> parameters;
	  private Map<String, Object> otherParameters;
	  private Object result;
	  private int pageSize = 10;
	  private int pi = 1;
	  private int countPage;
	  private int count = 0;
	  private List<Map<String, Object>> resultList;
	  private List<Object> midList = new ArrayList();
	  private List<String> ListOrder;
	  private String firstHref;
	  private String preHref;
	  private String nextHref;
	  private String lastHref;
	  private String hideStr = "";
	  private int len = 3;
	  private String originUrl = "";
	  private int step;
	  private Map<String, Object> resultMap;
	  
	  public Map<String, Object> getResultMap()
	  {
	    return this.resultMap;
	  }
	  
	  public void setResultMap(Map<String, Object> resultMap)
	  {
	    this.resultMap = resultMap;
	  }
	  
	  public JdbcTemplate getJdbcTemplate()
	  {
	    return this.jdbcTemplate;
	  }
	  
	  public void setJdbcTemplate(JdbcTemplate jdbcTemplate)
	  {
	    this.jdbcTemplate = jdbcTemplate;
	  }
	  
	  public int getStart()
	  {
	    return this.start;
	  }
	  
	  public void setStart(int start)
	  {
	    this.start = start;
	  }
	  
	  public String getCountSelect()
	  {
	    return this.countSelect;
	  }
	  
	  public void setCountSelect(String countSelect)
	  {
	    this.countSelect = countSelect;
	  }
	  
	  public String getSelect()
	  {
	    return this.select;
	  }
	  
	  public void setSelect(String select)
	  {
	    this.select = select;
	  }
	  
	  public String getFrom()
	  {
	    return this.from;
	  }
	  
	  public void setFrom(String from)
	  {
	    this.from = from;
	  }
	  
	  public String getWhere()
	  {
	    return this.where;
	  }
	  
	  public void setWhere(String where)
	  {
	    this.where = where;
	  }
	  
	  public String getCountFrom()
	  {
	    return this.countFrom;
	  }
	  
	  public void setCountFrom(String countFrom)
	  {
	    this.countFrom = countFrom;
	  }
	  
	  public String getCountWhere()
	  {
	    return this.countWhere;
	  }
	  
	  public void setCountWhere(String countWhere)
	  {
	    this.countWhere = countWhere;
	  }
	  
	  public String getGroupBy()
	  {
	    return this.groupBy;
	  }
	  
	  public void setGroupBy(String groupBy)
	  {
	    this.groupBy = groupBy;
	  }
	  
	  public String getOrderBy()
	  {
	    return this.orderBy;
	  }
	  
	  public void setOrderBy(String orderBy)
	  {
	    this.orderBy = orderBy;
	  }
	  
	  public List<Object> getParametersName()
	  {
	    return this.parametersName;
	  }
	  
	  public void setParametersName(List<Object> parametersName)
	  {
	    this.parametersName = parametersName;
	  }
	  
	  public List<Object> getParameters()
	  {
	    return this.parameters;
	  }
	  
	  public void setParameters(List<Object> parameters)
	  {
	    this.parameters = parameters;
	  }
	  
	  public Map<String, Object> getOtherParameters()
	  {
	    return this.otherParameters;
	  }
	  
	  public void setOtherParameters(Map<String, Object> otherParameters)
	  {
	    this.otherParameters = otherParameters;
	  }
	  
	  public int getPageSize()
	  {
	    return this.pageSize;
	  }
	  
	  public void setPageSize(int pageSize)
	  {
	    this.pageSize = pageSize;
	  }
	  
	  public int getPi()
	  {
	    return this.pi;
	  }
	  
	  public void setPi(int pi)
	  {
	    this.pi = pi;
	  }
	  
	  public int getCountPage()
	  {
	    return this.countPage;
	  }
	  
	  public void setCountPage(int countPage)
	  {
	    this.countPage = countPage;
	  }
	  
	  public int getCount()
	  {
	    return this.count;
	  }
	  
	  public void setCount(int count)
	  {
	    this.count = count;
	  }
	  
	  public List<Map<String, Object>> getResultList()
	  {
	    return this.resultList;
	  }
	  
	  public void setResultList(List<Map<String, Object>> resultList)
	  {
	    this.resultList = resultList;
	  }
	  
	  public List<Object> getMidList()
	  {
	    return this.midList;
	  }
	  
	  public void setMidList(List<Object> midList)
	  {
	    this.midList = midList;
	  }
	  
	  public String getFirstHref()
	  {
	    return this.firstHref;
	  }
	  
	  public void setFirstHref(String firstHref)
	  {
	    this.firstHref = firstHref;
	  }
	  
	  public String getPreHref()
	  {
	    return this.preHref;
	  }
	  
	  public void setPreHref(String preHref)
	  {
	    this.preHref = preHref;
	  }
	  
	  public String getNextHref()
	  {
	    return this.nextHref;
	  }
	  
	  public void setNextHref(String nextHref)
	  {
	    this.nextHref = nextHref;
	  }
	  
	  public String getLastHref()
	  {
	    return this.lastHref;
	  }
	  
	  public void setLastHref(String lastHref)
	  {
	    this.lastHref = lastHref;
	  }
	  
	  public String getHideStr()
	  {
	    return this.hideStr;
	  }
	  
	  public void setHideStr(String hideStr)
	  {
	    this.hideStr = hideStr;
	  }
	  
	  public int getLen()
	  {
	    return this.len;
	  }
	  
	  public void setLen(int len)
	  {
	    this.len = len;
	  }
	  
	  public String getOriginUrl()
	  {
	    return this.originUrl;
	  }
	  
	  public void setOriginUrl(String originUrl)
	  {
	    this.originUrl = originUrl;
	  }
	  
	  public Object getResult()
	  {
	    return this.result;
	  }
	  
	  public void setResult(Object result)
	  {
	    this.result = result;
	  }
	  
	  public int getStep()
	  {
	    return this.step;
	  }
	  
	  public void setStep(int step)
	  {
	    this.step = step;
	  }
	  
	  public PageUtils() {}
	  
	  public PageUtils(int pi, int pageSize, JdbcTemplate jdbcTemplate)
	  {
	    this.pi = pi;
	    this.pageSize = pageSize;
	    this.jdbcTemplate = jdbcTemplate;
	  }
	  
	  public List<Map<String, Object>> getPageList()
	  {
	    if (this.resultList != null) {
	      return this.resultList;
	    }
	    int parlen = this.parameters == null ? 0 : this.parameters.size();
	    





	    String countSql = this.countSelect + this.from + this.where;
	    if (parlen > 0) {
	      this.count = this.jdbcTemplate.queryForInt(countSql, 
	        list2object(this.parameters));
	    } else {
	      this.count = this.jdbcTemplate.queryForInt(countSql);
	    }
	    this.count += this.step;
	    this.countPage = (this.count / this.pageSize);
	    if (this.count % this.pageSize != 0) {
	      this.countPage += 1;
	    }
	    if (this.pi < 1) {
	      this.pi = 1;
	    }
	    this.start = ((this.pi - 1) * this.pageSize);
	    if (this.start < 0) {
	      this.start = 0;
	    }
	    if (this.step > 0) {
	      if (this.pi == 1) {
	        this.pageSize -= this.step;
	      } else {
	        this.start -= this.step;
	      }
	    }
	    try
	    {
	      String limit = " limit " + this.start + " , " + this.pageSize;
	      if (parlen > 0) {
	        this.resultList = this.jdbcTemplate.queryForList(this.select + this.from + this.where + 
	          this.groupBy + this.orderBy + limit, list2object(this.parameters));
	      } else {
	        this.resultList = this.jdbcTemplate.queryForList(this.select + this.from + this.where + 
	          this.groupBy + this.orderBy + limit);
	      }
	      int len = this.resultList == null ? 0 : this.resultList.size();
	      if ((this.count < 1) && (len == this.pageSize))
	      {
	        if ((this.countFrom == null) || ("".equals(this.countFrom))) {
	          this.countFrom = this.from;
	        }
	        if ((this.countWhere == null) || ("".equals(this.countWhere))) {
	          this.countWhere = this.where;
	        }
	        if (parlen > 0) {
	          this.count = this.jdbcTemplate.queryForInt(countSql, 
	            list2object(this.parameters));
	        } else {
	          this.count = this.jdbcTemplate.queryForInt(countSql);
	        }
	        this.countPage = (this.count / this.pageSize);
	        if (this.count % this.pageSize != 0) {
	          this.countPage += 1;
	        }
	      }
	    }
	    catch (Exception ex)
	    {
	      ex.printStackTrace();
	    }
	    createUrl();
	    return this.resultList;
	  }
	  
	  public List<Map<String, Object>> getPageList(boolean flag)
	  {
	    if (this.resultList != null) {
	      return this.resultList;
	    }
	    int parlen = this.parameters == null ? 0 : this.parameters.size();
	    





	    String countSql = this.countSelect + " FROM ( " + this.select + this.from + this.where + " ) a";
	    if (parlen > 0) {
	      this.count = this.jdbcTemplate.queryForInt(countSql, 
	        list2object(this.parameters));
	    } else {
	      this.count = this.jdbcTemplate.queryForInt(countSql);
	    }
	    this.count += this.step;
	    this.countPage = (this.count / this.pageSize);
	    if (this.count % this.pageSize != 0) {
	      this.countPage += 1;
	    }
	    if (this.pi < 1) {
	      this.pi = 1;
	    }
	    this.start = ((this.pi - 1) * this.pageSize);
	    if (this.start < 0) {
	      this.start = 0;
	    }
	    if (this.step > 0) {
	      if (this.pi == 1) {
	        this.pageSize -= this.step;
	      } else {
	        this.start -= this.step;
	      }
	    }
	    try
	    {
	      String limit = " limit " + this.start + " , " + this.pageSize;
	      if (parlen > 0) {
	        this.resultList = this.jdbcTemplate.queryForList(this.select + this.from + this.where + 
	          this.groupBy + this.orderBy + limit, list2object(this.parameters));
	      } else {
	        this.resultList = this.jdbcTemplate.queryForList(this.select + this.from + this.where + 
	          this.groupBy + this.orderBy + limit);
	      }
	      int len = this.resultList == null ? 0 : this.resultList.size();
	      if ((this.count < 1) && (len == this.pageSize))
	      {
	        if ((this.countFrom == null) || ("".equals(this.countFrom))) {
	          this.countFrom = this.from;
	        }
	        if ((this.countWhere == null) || ("".equals(this.countWhere))) {
	          this.countWhere = this.where;
	        }
	        if (parlen > 0) {
	          this.count = this.jdbcTemplate.queryForInt(countSql, 
	            list2object(this.parameters));
	        } else {
	          this.count = this.jdbcTemplate.queryForInt(countSql);
	        }
	        this.countPage = (this.count / this.pageSize);
	        if (this.count % this.pageSize != 0) {
	          this.countPage += 1;
	        }
	      }
	    }
	    catch (Exception ex)
	    {
	      ex.printStackTrace();
	    }
	    createUrl();
	    return this.resultList;
	  }
	  
	  private void createUrl()
	  {
	    String pUrl = createPUrl();
	    this.firstHref = 
	      (this.originUrl + "pageUtils.pi=1" + pUrl + "&pageUtils.count=" + this.count);
	    this.preHref = 
	      (this.originUrl + "pageUtils.pi=" + (this.pi - 1 <= 0 ? 1 : this.pi - 1) + pUrl + "&pageUtils.count=" + this.count);
	    this.nextHref = 
	    
	      (this.originUrl + "pageUtils.pi=" + (this.pi + 1 > this.countPage ? this.countPage : this.pi + 1) + pUrl + "&pageUtils.count=" + this.count);
	    this.lastHref = 
	      (this.originUrl + "pageUtils.pi=" + this.countPage + pUrl + "&pageUtils.count=" + this.count);
	    createMidNum(pUrl);
	  }
	  
	  private void createMidNum(String pUrl)
	  {
	    int s = 1;
	    int e = 1;
	    s = this.pi - this.len <= 0 ? 1 : this.pi - this.len;
	    e = this.pi + this.len > this.countPage ? this.countPage : this.pi + this.len;
	    this.midList.clear();
	    for (int i = s; i <= e; i++)
	    {
	      Map<Object, Object> midMap = new HashMap();
	      midMap.put("pi", Integer.valueOf(i));
	      midMap.put("url", this.originUrl + "pageUtils.pi=" + i + pUrl + 
	        "&pageUtils.count=" + this.count);
	      this.midList.add(midMap);
	    }
	  }
	  
	  private String createPUrl()
	  {
	    StringBuffer pUrl = new StringBuffer("");
	    int parlen = this.parametersName == null ? 0 : this.parametersName.size();
	    String key;
	    if (parlen > 0) {
	      for (int i = 0; i < parlen; i++)
	      {
	        key = (String)this.parametersName.get(i);
	        String value = (String) this.parameters.get(i);
	        if (key.endsWith("_"))
	        {
	          key = key.substring(0, key.lastIndexOf("_"));
	          if (value.startsWith("%")) {
	            value = value.substring(1);
	          }
	          if (value.endsWith("%")) {
	            value = value.substring(0, value.lastIndexOf("%"));
	          }
	          pUrl.append("&" + key + "=" + getEncoder(value));
	        }
	        else
	        {
	          pUrl.append("&" + key + "=" + getEncoder(value));
	        }
	        this.hideStr = (this.hideStr + "<input type='hidden' name='" + key + "' id='" + key + "' value='" + value + "'/>");
	      }
	    }
	    if ((this.otherParameters != null) && (this.otherParameters.size() > 0)) {
	      for (Map.Entry<String, Object> entry : this.otherParameters.entrySet())
	      {
	        pUrl.append("&" + (String)entry.getKey() + "=" + entry.getValue());
	        

	        this.hideStr = (this.hideStr + "<input type='hidden' name='" + (String)entry.getKey() + "' id='" + (String)entry.getKey() + "' value='" + entry.getValue() + "'/>");
	      }
	    }
	    return pUrl.toString();
	  }
	  
	  private Object[] list2object(List<Object> list)
	  {
	    if ((list != null) && (list.size() > 0))
	    {
	      Object[] obj = new Object[list.size()];
	      for (int i = 0; i < list.size(); i++) {
	        obj[i] = list.get(i);
	      }
	      return obj;
	    }
	    return null;
	  }
	  
	  private String getEncoder(String str)
	  {
	    try
	    {
	      return URLEncoder.encode(str, "utf-8");
	    }
	    catch (Exception ex)
	    {
	      ex.printStackTrace();
	    }
	    return "";
	  }
	  
	  public List<String> getListOrder()
	  {
	    return this.ListOrder;
	  }
	  
	  public void setListOrder(List<String> listOrder)
	  {
	    this.ListOrder = listOrder;
	  }
}
