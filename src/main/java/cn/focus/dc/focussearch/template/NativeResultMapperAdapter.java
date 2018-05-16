package cn.focus.dc.focussearch.template;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedList;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.springframework.data.elasticsearch.core.AbstractResultMapper;

public class NativeResultMapperAdapter implements NativeMultiGetResultMapper {
	
	private AbstractResultMapper resultsMapper;
	
	public NativeResultMapperAdapter(AbstractResultMapper resultsMapper) {
		super();
		this.resultsMapper = resultsMapper;
	}

	@Override
	public <T> LinkedList<T> mapResults(MultiSearchResponse responses, Class<T> clazz) {
		LinkedList<T> results = new LinkedList<T>();
		for (MultiSearchResponse.Item item : responses.getResponses()) {
			if (item.isFailure()) {
				continue;
			}
			for (SearchHit hit : item.getResponse().getHits()) {
				if (hit != null) {
					T result = null;
					if (hit.sourceAsString() == null || hit.sourceAsString().length() == 0) {
						result = resultsMapper.mapEntity(hit.sourceAsString(), clazz);
					} else {
						result = mapEntity(hit.getFields().values(), clazz);
					}
					results.add(result);
				}
			}
		}
		return results;
	}

	private <T> T mapEntity(Collection<SearchHitField> values, Class<T> clazz) {
		return resultsMapper.mapEntity(buildJSONFromFields(values), clazz);
	}

	private String buildJSONFromFields(Collection<SearchHitField> values) {
		JsonFactory nodeFactory = new JsonFactory();
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			JsonGenerator generator = nodeFactory.createGenerator(stream, JsonEncoding.UTF8);
			generator.writeStartObject();
			for (SearchHitField value : values) {
				if (value.getValues().size() > 1) {
					generator.writeArrayFieldStart(value.getName());
					for (Object val : value.getValues()) {
						generator.writeObject(val);
					}
					generator.writeEndArray();
				} else {
					generator.writeObjectField(value.getName(), value.getValue());
				}
			}
			generator.writeEndObject();
			generator.flush();
			return new String(stream.toByteArray(), Charset.forName("UTF-8"));
		} catch (IOException e) {
			return null;
		}
	}
}
