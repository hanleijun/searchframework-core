package cn.focus.dc.focussearch.template;

import java.util.LinkedList;

import org.elasticsearch.action.search.MultiSearchResponse;

public interface NativeMultiGetResultMapper {

	<T> LinkedList<T> mapResults(MultiSearchResponse responses, Class<T> clazz);
}
