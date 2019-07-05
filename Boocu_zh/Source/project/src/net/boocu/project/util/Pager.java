package net.boocu.project.util;

import java.util.ArrayList;
import java.util.List;

import net.boocu.web.Page;

/**
 * 根据参数生成分页按钮
 * 
 * @author xyf
 *
 */
public class Pager {
	public static final int DEFAULT_EDGE_COUNT = 1;
	public static final int DEFAULT_SHOW_COUNT = 3;
	private int showCount;
	private int edgeCount;

	/**
	 * 分页按钮数据
	 * 
	 * @author xyf
	 *
	 */
	public static class PagerData {
		/**
		 * 分隔符
		 */
		public static final PagerData SEPERATOR = new PagerData();

		public PagerData() {
			type = DataType.SEPERATOR;
		}

		/**
		 * 
		 * @param page
		 *            页码
		 */
		public PagerData(int page) {
			this(page, true);
		}

		PagerData(int page, boolean isEnabled) {
			pageNumber = page;
			type = DataType.NUMBER;
			this.isEnabled = isEnabled;
		}

		/**
		 * 分页按钮数据类型
		 */
		public enum DataType {
			/**
			 * 分隔符
			 */
			SEPERATOR,
			/**
			 * 页码
			 */
			NUMBER
		};

		private DataType type;
		private boolean isEnabled;
		private int pageNumber;

		public DataType getType() {
			return type;
		}

		public void setType(DataType type) {
			this.type = type;
		}

		public boolean isEnabled() {
			return isEnabled;
		}

		public void setEnabled(boolean isEnabled) {
			this.isEnabled = isEnabled;
		}

		public int getPageNumber() {
			return pageNumber;
		}

		public void setPageNumber(int pageNumber) {
			this.pageNumber = pageNumber;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			switch (type) {
			case NUMBER:
				sb.append(pageNumber);
				if (!isEnabled)
					sb.append(" d");
				break;
			default:
				sb.append(type);
				break;
			}
			return sb.toString();
		}
	}

	/**
	 * 
	 * @param showCount
	 *            当前页码附近显示的分页按钮数量，应该是奇数
	 * @param edgeCount
	 *            头尾显示的分页按钮数量
	 */
	Pager(int showCount, int edgeCount) {
		this.showCount = showCount;
		this.edgeCount = edgeCount;
	}

	public Pager() {
		this(DEFAULT_SHOW_COUNT, DEFAULT_EDGE_COUNT);
	}

	public int getShowCount() {
		return showCount;
	}

	public void setShowCount(int showCount) {
		this.showCount = showCount;
	}

	public int getEdgeCount() {
		return edgeCount;
	}

	public void setEdgeCount(int edgeCount) {
		this.edgeCount = edgeCount;
	}

	/**
	 * 获取分页按钮数据列表
	 * 
	 * @param page
	 *            分页信息
	 * @return
	 */
	public <K> List<PagerData> getPagerDataList(Page<K> page) {
		return getPagerDataList(page.getPageNumber(), page.getTotalPages());
	}

	/**
	 * 获取分页按钮数据列表
	 * 
	 * @param page
	 *            当前页码
	 * @param pageCount
	 *            分页的总页数
	 * @return
	 */
	public List<PagerData> getPagerDataList(int page, int pageCount) {
		int halfCount = (showCount - 1) / 2;
		List<PagerData> ret = new ArrayList<PagerData>();
		if (pageCount > 0) {
			if (page < edgeCount + 1 + halfCount) {
				for (int i = 1; i < page; i++)
					ret.add(new PagerData(i));
				ret.add(new PagerData(page, false));
			} else {
				for (int i = 1; i <= edgeCount; i++)
					ret.add(new PagerData(i));
				ret.add(PagerData.SEPERATOR);
				for (int i = page - halfCount; i <= page; i++)
					ret.add(new PagerData(i));
			}
			if (pageCount <= page + edgeCount + halfCount) {
				for (int i = page + 1; i <= pageCount; i++)
					ret.add(new PagerData(i));
			} else {
				for (int i = page + 1; i <= page + halfCount; i++)
					ret.add(new PagerData(i));
				ret.add(PagerData.SEPERATOR);
				for (int i = pageCount - edgeCount + 1; i <= pageCount; i++)
					ret.add(new PagerData(i));
			}
		}
		return ret;
	}

}
