package cn.focus.dc.focussearch.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rometools.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.suggest.SuggestRequestBuilder;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.DefaultResultMapper;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @param <T>
 * @param <Q>
 * @author Legend Han(leijunhan@sohu-inc.com)
 */
public abstract class EsCommonSearch<T, Q extends PageBaseQuery> implements CommonSearch<T, Q> {

    @Resource(name = "elasticsearchTemplate")
    public ElasticsearchTemplate template;

    private final static Logger logger = Logger.getLogger(EsCommonSearch.class);
    /**
     * 方便子类与父类操作不同ES集群时候对于父类方法的调用
     *
     * @param template
     */
    protected void setTemplate(ElasticsearchTemplate template) {
        this.template = template;
    }

    @Override
    public AggregatedPage<T> search(Q q, String[] indices, String[] types, Class<T> tClass) {
        QueryBuilder queryBuilder = getQuery(q);
        QueryBuilder filterBuilder = getFilter(q);
        List<SortBuilder> sorts = getSorts(q);
        if (q.getAggFieldName() != null) {
            return searchWithAgg(q, tClass, filterBuilder, queryBuilder, sorts, q.getAggFieldName());
        }
        return search(q, indices, types, tClass, filterBuilder, queryBuilder, sorts);
    }

    @Override
    public QueryBuilder getQuery(Q q) {
        // TODO Auto-generated method stub
        return QueryBuilders.matchAllQuery();
    }

    public AggregatedPage<T> search(Q q, Class<T> tClass) {
        return search(q, null, null, tClass);
    }


    /**
     * get searching result with aggregation information
     *
     * @param q
     * @param tClass
     * @param filterBuilder
     * @param queryBuilder
     * @param sorts
     * @param aggField
     * @return
     */
    public AggregatedPage<T> searchWithAgg(Q q, Class<T> tClass, QueryBuilder filterBuilder, QueryBuilder queryBuilder, List<SortBuilder> sorts, String aggField) {
        BoolQueryBuilder bqb = QueryBuilders.boolQuery().must(queryBuilder).filter(filterBuilder);
        NativeSearchQueryBuilder searchQuery = new NativeSearchQueryBuilder().withQuery(bqb);
        searchQuery.withPageable(new PageRequest(q.getPage(), q.getPageSize()));
        searchQuery.withSearchType(q.getSearchType());
        if (Lists.isNotEmpty(sorts)) {
            for (SortBuilder sort : sorts) {
                searchQuery.withSort(sort);
            }
        }
        if (null != q.getFields() && q.getFields().length > 0) {
            searchQuery.withFields(q.getFields());
        }
        if (!StringUtils.isEmpty(aggField)) {
            searchQuery.addAggregation(AggregationBuilders.terms(aggField).field(aggField).size(SearchConstants.BUCKET_SIZE));
        }
        AggregatedPage<T> queryForPage;
        queryForPage = template.queryForPage(searchQuery.build(), tClass, setAggResultMapper());
        return queryForPage;
    }

    public AggregatedPage<T> search(Q q, String[] indices, String[] types, Class<T> tClass, QueryBuilder filterBuilder, QueryBuilder queryBuilder, List<SortBuilder> sorts) {
        BoolQueryBuilder bqb = QueryBuilders.boolQuery().must(queryBuilder).filter(filterBuilder);
        NativeSearchQueryBuilder searchQuery = new NativeSearchQueryBuilder().withQuery(bqb);

        if (null != indices && indices.length > 0) {
            searchQuery.withIndices(indices);
        }
        if (null != types && types.length > 0) {
            searchQuery.withTypes(types);
        }
        searchQuery.withPageable(new PageRequest(q.getPage(), q.getPageSize()));
        searchQuery.withSearchType(q.getSearchType());
        if (Lists.isNotEmpty(sorts)) {
            for (SortBuilder sort : sorts) {
                searchQuery.withSort(sort);
            }
        }
        if (null != q.getFields() && q.getFields().length > 0) {
            searchQuery.withFields(q.getFields());
        }

        AggregatedPage<T> queryForPage;
        if (null != q.getHlFields() && q.getHlFields().length > 0) {
            int length = q.getHlFields().length;
            HighlightBuilder.Field[] hlFields = new HighlightBuilder.Field[length];
            for (int i = 0; i < length; i++) {
                String field = q.getHlFields()[i];
                HighlightBuilder.Field hlField = new HighlightBuilder.Field(field)
                        .fragmentSize(90).numOfFragments(1).highlighterType("fvh")
                        .boundaryMaxScan(30).preTags("<em>").postTags("</em>");
                hlFields[i] = hlField;
            }
            searchQuery.withHighlightFields(hlFields);
            queryForPage = template.queryForPage(searchQuery.build(), tClass, setHighLightResultMapper(q.getHlFields()));
        } else {
            long start = System.currentTimeMillis();
            queryForPage = template.queryForPage(searchQuery.build(), tClass);
            long end = System.currentTimeMillis();
            logger.info("[search-core]: query:[" + searchQuery.build().getQuery() + "] cost: [" + (end - start) + "]");
        }
        return queryForPage;
    }

    @Override
    public SearchResultMapper setHighLightResultMapper(final String... hlFields) {
        // TODO Auto-generated method stub
        return new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response,
                                                    Class<T> clazz, Pageable pageable) {
                AggregatedPage<T> mapResults = new DefaultResultMapper().mapResults(response, clazz, pageable);
                List<T> chunk = mapResults.getContent();
                int i = 0;
                for (SearchHit searchHit : response.getHits()) {
                    if (response.getHits().getHits().length <= 0) {
                        return null;
                    }
                    T t = chunk.get(i);
                    Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
                    for (String hlField : hlFields) {
                        if (highlightFields.containsKey(hlField)) {
                            String hfFieldValue = highlightFields.get(hlField).fragments()[0].toString();
                            String field = hlField;
                            if (hlField.contains(".")) {
                                field = hlField.substring(0, hlField.indexOf("."));
                            }
                            try {
                                Field declaredField = t.getClass().getDeclaredField(field);
                                declaredField.setAccessible(true);
                                declaredField.set(t, hfFieldValue);
                            } catch (NoSuchFieldException e) {
                                e.printStackTrace();
                            } catch (SecurityException e) {
                                e.printStackTrace();
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    i++;
                }
                return mapResults;
            }
        };
    }

    /**
     * address the searching response partly to FacetedPage
     *
     * @return
     */
    public SearchResultMapper setAggResultMapper() {
        return new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                AggregatedPage<T> mapResults = new AggResultMapper().mapResults(response, clazz, pageable);
                return mapResults;
            }
        };
    }

    @Override
    public Map<String, Long> termsAgg(String[] indices, String[] types, String field) {
        String name = "termsAgg";
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .withIndices(indices).withTypes(types)
                .addAggregation(AggregationBuilders.terms(name).field(field))
                .build();
        Aggregations aggregations = template.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });

        List<Aggregation> list = aggregations.asList();
        Map<String, Long> resultMap = new HashMap<String, Long>();
        for (int i = 0; i < list.size(); i++) {
            LongTerms lt = (LongTerms) list.get(i);
            Collection<Bucket> bucketss = lt.getBuckets();
            Iterator<Bucket> iterators = bucketss.iterator();
            while (iterators.hasNext()) {
                Bucket bucketsss = iterators.next();
                resultMap.put(String.valueOf(bucketsss.getKey()), bucketsss.getDocCount());
            }
        }
        return resultMap;
    }

    public Map<String, Long> termsAgg(String field) {
        return termsAgg(null, null, field);
    }

    /**
     * 范围性过滤
     *
     * @param boolQuery
     * @param filterName
     * @param min
     * @param max
     */
    protected void rangeMustFilter(BoolQueryBuilder boolQuery, String filterName, Object min, Object max) {
        if (min != null && max != null) {
            boolQuery.must(new RangeQueryBuilder(filterName).gte(min).lte(max));
        }
    }

    /**
     * prefix filter method.
     *
     * @param boolQuery
     * @param filterName
     */
    protected void prefixMustFilter(BoolQueryBuilder boolQuery, String filterName, Object value) {
        if (value != null) {
            boolQuery.must(new PrefixQueryBuilder(filterName, value.toString()));
        }
    }

    /**
     * 词条过滤
     *
     * @param boolQuery
     * @param filterName
     */
    protected void termMustFilter(BoolQueryBuilder boolQuery, String filterName, Object value) {
        if (value != null) {
            boolQuery.must(new TermQueryBuilder(filterName, value));
        }
    }

    /**
     * 禁止词条过滤
     *
     * @param boolQuery
     * @param filterName
     * @param value
     */
    protected void termMustNotFilter(BoolQueryBuilder boolQuery, String filterName, Object value) {
        if (value != null) {
            boolQuery.mustNot(new TermQueryBuilder(filterName, value));
        }
    }

    /**
     * 词条多选过滤
     *
     * @param boolQuery
     * @param filterName
     * @param values
     */
    protected void termsMustFilter(BoolQueryBuilder boolQuery, String filterName, Object[] values) {
        if (values != null && values.length > 0) {
            boolQuery.must(new TermsQueryBuilder(filterName, values));
        }
    }

    /**
     * 小于等于取反
     *
     * @param boolQuery
     * @param filterName
     * @param value
     */
    protected void lteMustNotFilter(BoolQueryBuilder boolQuery, String filterName, Object value) {
        if (value != null) {
            boolQuery.mustNot(new RangeQueryBuilder(filterName).lt(value));
        }
    }

    /**
     * 大于等于去反
     *
     * @param boolQuery
     * @param filterName
     * @param value
     */
    protected void gteMustNotFilter(BoolQueryBuilder boolQuery, String filterName, Object value) {
        if (value != null) {
            boolQuery.mustNot(new RangeQueryBuilder(filterName).gt(value));
        }
    }

    /**
     * 对子查询条件进行过滤
     *
     * @param boolQuery
     * @param type
     * @param filterName
     * @param value
     */
    protected void termMustChildFilter(BoolQueryBuilder boolQuery, String type, String filterName, Object value) {
        if (value != null) {
            boolQuery.must(new HasParentQueryBuilder(type, new TermQueryBuilder(filterName, value)));
        }
    }

    /**
     * 对父查询进行过滤
     *
     * @param boolQuery
     * @param type
     * @param filterName
     * @param value
     */
    protected void termMustParentFilter(BoolQueryBuilder boolQuery, String type, String filterName, Object value) {
        if (value != null) {
            boolQuery.must(new HasParentQueryBuilder(type, new TermQueryBuilder(filterName, value)));
        }
    }

    /**
     * 对子查询进行范围过滤
     *
     * @param boolQuery
     * @param type
     * @param filterName
     * @param min
     * @param max
     */
    protected void rangeMustChildFilter(BoolQueryBuilder boolQuery, String type, String filterName, Object min, Object max) {
        if (min != null && max != null) {
            boolQuery.must(new HasChildQueryBuilder(type, new RangeQueryBuilder(filterName).gte(min).lte(max)));
        }
    }

    /**
     * 对字段进行排序
     *
     * @param field
     * @param sort
     * @param missing
     * @return
     */
    protected FieldSortBuilder fieldSort(String field, SortOrder sort, String missing) {
        return SortBuilders.fieldSort(field).order(sort).missing(missing);
    }

    /**
     * 获得查询条件
     *
     * @param q
     * @return
     */
    public SearchQuery getSearchQuery(Q q) {
        QueryBuilder queryBuilder = getQuery(q);
        QueryBuilder filterBuilder = getFilter(q);
        List<SortBuilder> sorts = getSorts(q);

        NativeSearchQueryBuilder searchQuery = new NativeSearchQueryBuilder().withQuery(queryBuilder).withFilter(filterBuilder);
        if (Lists.isNotEmpty(sorts)) {
            for (SortBuilder sort : sorts) {
                searchQuery.withSort(sort);
            }
        }
        if (null != q.getFields() && q.getFields().length > 0) {
            searchQuery.withFields(q.getFields());
        }

        return searchQuery.build();
    }

    /**
     * Scroll 方式返回结果
     */
    public List<SampleEntity> getScrollResult(SearchQuery searchQuery, long scrollTimeInMillis, boolean noFields, int count) {
        String scrollId = template.scan(searchQuery, scrollTimeInMillis, noFields);
        List<SampleEntity> sampleEntities = new ArrayList<>();
        boolean hasRecords = true;
        int cnt = 0;
        while (hasRecords && cnt++ < count) {
            Page<SampleEntity> page = template.scroll(scrollId, scrollTimeInMillis, new SearchResultMapper() {
                @Override
                public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                    List<SampleEntity> chunk = new ArrayList<>();
                    for (SearchHit searchHit : response.getHits()) {
                        if (response.getHits().getHits().length <= 0) {
                            return null;
                        }
                        SampleEntity user = new SampleEntity();
                        user.setId(searchHit.getId());
                        user.setMessage(JSONObject.parse(JSONObject.toJSONString(searchHit.getSource())));
                        chunk.add(user);
                    }
                    if (chunk.size() > 0) {
                        return new AggregatedPageImpl<T>((List<T>) chunk);
                    }
                    return null;
                }
            });
            if (page != null) {
                sampleEntities.addAll(page.getContent());
            } else {
                hasRecords = false;
            }
        }
        template.clearScroll(scrollId);
        return sampleEntities;
    }

    /**
     * @param text  输入需要自动补全的内容
     * @param field 自动补全功能对应的字段
     * @param size  返回补全的个数
     * @param index 需要查询的 elasticsearch 的索引
     * @return 返回一个补全信息的 jsonarray
     */
    public JSONArray completionSuggester(String text, String field, Integer size, String index) {
        CompletionSuggestionBuilder completionSuggestionBuilder = new CompletionSuggestionBuilder("suggester");
        completionSuggestionBuilder.text(text);
        completionSuggestionBuilder.field(field);
        completionSuggestionBuilder.size(size);
        SuggestRequestBuilder suggestRequest = template.getClient().prepareSuggest(index).addSuggestion(completionSuggestionBuilder);
        long start = System.currentTimeMillis();
        SuggestResponse suggestResponse = suggestRequest.execute().actionGet();
        long end = System.currentTimeMillis();
        logger.info("[completionSuggester]: cost: [" + (end - start) + "]");
        String newsSuggest = JSONArray.parseArray(JSONObject.parseObject(suggestResponse.toString()).get("suggester").toString()).get(0).toString();
        JSONArray options = JSONArray.parseArray(JSONObject.parseObject(newsSuggest).get("options").toString());
        JSONArray result = new JSONArray();
        for (Object option : options) {
            result.add(JSONObject.parseObject(option.toString()).get("payload"));
        }
        return result;
    }

    /**
     * 批量从es中获取文档，index是索引名，type是类型，docIds是文档唯一标识的列表
     * @param index
     * @param type
     * @param docIds
     * @return
     */
    public JSONArray multiGet(String index, String type, List<String> docIds) {
        MultiGetRequestBuilder multiGetRequest = template.getClient().prepareMultiGet();
        for (String docId : docIds) {
            multiGetRequest.add(index, type, docId);
        }
        MultiGetResponse multiGetItemResponses = multiGetRequest.get();
        JSONArray jsonArray = new JSONArray();
        for (MultiGetItemResponse itemResponse : multiGetItemResponses) {
            GetResponse response = itemResponse.getResponse();
            if (response.isExists()) {
                JSONObject jsonObject = JSONObject.parseObject(response.getSourceAsString());
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }
}




