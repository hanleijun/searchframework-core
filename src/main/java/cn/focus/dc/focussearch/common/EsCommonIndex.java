/**
 *
 */
package cn.focus.dc.focussearch.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.repository.core.support.PersistentEntityInformation;

import com.rometools.utils.Lists;

/**
 * abstract class for indexing action.
 * @author Legend Han(leijunhan@sohu-inc.com)
 */
public abstract class EsCommonIndex<T, ID extends Serializable> implements CommonIndex<T, ID> {

    @Resource(name="elasticsearchTemplate")
    public ElasticsearchTemplate template;

    /**
     * 方便子类与父类操作不同ES集群时候对于父类方法的调用
     * @param template
     */
    protected void setTemplate(ElasticsearchTemplate template){
    	this.template = template;
    }
    
    @Override
    public <S extends T> void save(S entity) {
    	IndexQuery indexQuery = buildIndexQuery(entity);
    	index(entity, indexQuery);
    	refreshIndex(entity);
    }
    
    @Override
    public <S extends T> void save(S entity, String parentId) {
    	IndexQuery indexQuery = buildIndexQuery(entity, parentId);
    	index(entity, indexQuery);
    	refreshIndex(entity);
    }
    
    @Override
    public <S extends T> void batchSave(List<S> entities, String parentId) {
    	if (Lists.isNotEmpty(entities)) {
    		List<IndexQuery> queryList = new ArrayList<IndexQuery>();
            for (S s : entities) {
                IndexQuery indexQuery = buildIndexQuery(s, parentId);
                buildIdIndexQuery(s, indexQuery);
                queryList.add(indexQuery);
            }
            template.bulkIndex(queryList);
            refreshIndex(entities.get(0));
    	}
    }
    
    private void refreshIndex(Object entity) {
    	Document annotation = entity.getClass().getAnnotation(Document.class);
        String indexName = annotation.indexName();
        template.refresh(indexName);
    }
    
	private void index(Object entity, IndexQuery indexQuery) {
    	buildIdIndexQuery(entity, indexQuery);
        template.index(indexQuery);
    }
    
    @Override
    public <S extends T> void batchSave(List<S> entities) {
    	if (Lists.isNotEmpty(entities)) {
    		List<IndexQuery> queryList = new ArrayList<IndexQuery>();
            for (S s : entities) {
                IndexQuery indexQuery = buildIndexQuery(s);
                buildIdIndexQuery(s, indexQuery);
                queryList.add(indexQuery);
            }
            template.bulkIndex(queryList);
            refreshIndex(entities.get(0));
    	}
    }
    
    @Override
    public void delete(ID id, String index, String type) {
        // TODO Auto-generated method stub
        DeleteQuery dQuery = new DeleteQuery();
        dQuery.setIndex(index);
        dQuery.setType(type);
        dQuery.setQuery(QueryBuilders.idsQuery().addIds(stringIdRepresentation(id)));
        template.delete(dQuery);
        template.refresh(index);
    }

    @Override
    public void deleteIds(List<ID> ids, String index, String type) {
        // TODO Auto-generated method stub
        if (Lists.isNotEmpty(ids)) {
            String[] idArray = new String[ids.size()];
            for (int i = 0, j = ids.size(); i < j; i++) {
                idArray[i] = stringIdRepresentation(ids.get(i));
            }
            DeleteQuery dQuery = new DeleteQuery();
            dQuery.setIndex(index);
            dQuery.setType(type);
            dQuery.setQuery(QueryBuilders.idsQuery().addIds(idArray));
            template.delete(dQuery);
            template.refresh(index);
        }
    }
    
    public void delete(String index, String type, String filterName, Object value) {
        DeleteQuery dQuery = new DeleteQuery();
        dQuery.setIndex(index);
        dQuery.setType(type);
        dQuery.setQuery(QueryBuilders.termQuery(filterName, value));
        template.delete(dQuery);
        template.refresh(index);
    }
    
    private IndexQuery buildIndexQuery(Object entity) {
    	IndexQuery indexQuery = new IndexQuery();
        indexQuery.setObject(entity);
        return indexQuery;
    }
    
    private IndexQuery buildIndexQuery(Object entity, String parentId) {
    	IndexQuery indexQuery = buildIndexQuery(entity);
    	indexQuery.setParentId(parentId);
    	return indexQuery;
    }
    
    @SuppressWarnings("unchecked")
	private IndexQuery buildIdIndexQuery(Object entity, IndexQuery indexQuery) {
    	@SuppressWarnings("rawtypes")
		PersistentEntity persistEntity = template.getElasticsearchConverter().getMappingContext().getPersistentEntity(entity.getClass());
        indexQuery.setId(stringIdRepresentation((ID) new PersistentEntityInformation<Object, ID>(persistEntity).getId(entity)));
        return indexQuery;
    }
    
    /**
	 * ID转为String
	 * @param id
	 * @return
	 */
    @Override
	public String stringIdRepresentation(ID id){
		return id.toString();
	}
}
