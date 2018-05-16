package cn.focus.dc.focussearch.common;

import java.io.Serializable;
import java.util.List;

public interface CommonIndex<T, ID extends Serializable> {

	/**
	 * index数据
	 * @param entity
	 * @return
	 */
	<S extends T> void save(S entity);
	
	/**
	 * 所以带有父子结构的数据
	 * @param entity
	 * @param parentId
	 */
	<S extends T> void save(S entity, String parentId);
	
	/**
	 * 批量index数据
	 * @param entities
	 * @return
	 */
	<S extends T> void batchSave(List<S> entities);
	
    /**
     * 批量索引带父子结构index数据
     * @param entities
     * @param parentId
     */
    <S extends T> void batchSave(List<S> entities, String parentId);	
	
	/**
	 * 根据ID删除
	 * @param id
	 */
	void delete(ID id,String index,String type);
	
	
	
	/**
	 * 根据ID批量删除
	 * @param ids
	 */
	void deleteIds(List<ID> ids,String index,String type);
	
	/**
	 * 根据字段进行删除
	 * @param index
	 * @param type
	 * @param filterName
	 * @param value
	 */
	void delete(String index, String type, String filterName, Object value);
	
	/**
	 * ID转为String
	 * @param id
	 * @return
	 */
	String stringIdRepresentation(ID id);
	
}
