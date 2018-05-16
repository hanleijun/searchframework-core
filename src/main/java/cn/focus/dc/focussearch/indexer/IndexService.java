package cn.focus.dc.focussearch.indexer;




public interface IndexService {
	
	/**
	 * 初始化全量数据
	 * 只会调用一次
	 */
	public void initAllData() throws Exception ;

	/**
	 * 删除数据
	 * 
	 */
	public void delData();
	
	
	/**
	 * 获取增量数据数据
	 * 
	 */
	public void addData();
	
	
	/**
	 * 更新数据
	 */
	public void updateData();
	
}
