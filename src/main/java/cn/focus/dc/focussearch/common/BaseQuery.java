package cn.focus.dc.focussearch.common;


import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryparser.classic.QueryParser;



/**
 * @author huiyang205628
 */
public class BaseQuery {
	//排序
	public Integer sort = 0;
	//排序类型
	public Integer sortType = 0;
	//搜索词
	public String q;
	//返回的字段
	private String[] fields;
	//高亮的字段
	private String[] hlFields;

	public boolean isQExist;


	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getSortType() {
		return sortType;
	}

	public void setSortType(Integer sortType) {
		this.sortType = sortType;
	}

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		if(StringUtils.isNotBlank(q)){
			isQExist = true;
			if(q.length() > SearchConstants.QUERY_LENGTH){
				q = q.substring(0, SearchConstants.QUERY_LENGTH);
			}
//			this.q = escape(q);
			this.q = q;
		}else{
			this.q = null;
            this.isQExist=false;
		}
	}

	@Deprecated
	public String escape(String s) {
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < s.length(); i++) {
	      char c = s.charAt(i);
	      // These characters are part of the query syntax and must be escaped
	      if (c == '\\' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':'
	        || c == '^' || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~'
	        || c == '*' || c == '?' || c == '|' || c == '&' || c == '/') {
	        sb.append('\\');
	      }
	      sb.append(c);
	    }
	    return sb.toString();
	  }
	
	public boolean isQExist() {
		return isQExist;
	}

	public String[] getFields() {
		return fields;
	}

	public void setFields(String[] fields) {
		this.fields = fields;
	}

	public String[] getHlFields() {
		return hlFields;
	}

	public void setHlFields(String[] hlFields) {
		this.hlFields = hlFields;
	}
	
}
