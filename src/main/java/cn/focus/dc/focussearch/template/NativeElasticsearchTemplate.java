package cn.focus.dc.focussearch.template;

import java.util.LinkedList;
import java.util.List;

import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.DefaultResultMapper;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

/**
 * MultiSearch patched to SpringData-ES
 */
public class NativeElasticsearchTemplate {
	
	private Client client;
	
	private NativeMultiGetResultMapper nativeResultMapperAdapter;
	
	public NativeElasticsearchTemplate(Client client) {
		this.client = client;
		this.nativeResultMapperAdapter = new NativeResultMapperAdapter(new DefaultResultMapper());
	}
	
	/**
	 * get the multiple response as List<T>, T is the unit of query object
	 * NOTE: the responses order is determined by the searchQuery order in the request List
	 * @param multiSearchQuery
	 * @param clazz
	 * @return
	 */
	public <T> LinkedList<T> multiGet(List<SearchQuery> multiSearchQuery, Class<T> clazz) {
		return nativeResultMapperAdapter.mapResults(getMultiResponse(multiSearchQuery, clazz), clazz);
	}
	
	/**
	 * multisearch action to a kind of class(with @Documnet annotation)
	 * @param multiSearchQuery
	 * @param clazz
	 * @return
	 */
	private <T> MultiSearchResponse getMultiResponse(List<SearchQuery> multiSearchQuery, Class<T> clazz) {
		Document document = clazz.getAnnotation(Document.class);
		MultiSearchRequestBuilder builder = client.prepareMultiSearch();
		for (SearchQuery searchQuery : multiSearchQuery) {
			SearchRequestBuilder request = client
					.prepareSearch(document.indexName())
					.setTypes(document.type())
					.setQuery(searchQuery.getQuery())
					.setPostFilter(searchQuery.getFilter())
					.setFrom(searchQuery.getPageable().getPageNumber())
					.setSize(searchQuery.getPageable().getPageSize());
			for (String field : searchQuery.getFields()) {
				request.addField(field);
			}
			for (SortBuilder sort : searchQuery.getElasticsearchSorts()) {
				request.addSort(sort);
			}
			builder.add(request);
		}
		return builder.execute().actionGet();
	}
}
