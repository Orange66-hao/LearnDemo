package net.boocu.project.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author 鲁小翔
 *
 * 2015年7月6日
 */
public class PageModel implements Serializable{
	private static final long serialVersionUID = -1144753432283916593L;
	
	/** 内容 */
    private List cont=new ArrayList();

    /** 总记录数 */
    private Long total=0L;

	public List getCont() {
		return cont;
	}

	public void setCont(List cont) {
		this.cont = cont;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}
    
}
