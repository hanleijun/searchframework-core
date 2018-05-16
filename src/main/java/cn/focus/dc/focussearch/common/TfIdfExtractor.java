package cn.focus.dc.focussearch.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.termvectors.TermVectorsResponse;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Copyright (C) 1998 - 2016 SOHU Inc., All Rights Reserved.
 * <p>
 *
 * an abstract class for extract tf-idf factors from documents in elasticsearch
 * @Author: hanleijun (leijunhan@sohu-inc.com)
 * @Date: 2018/4/25
 */
public abstract class TfIdfExtractor {

    @Resource(name = "elasticsearchTemplate")
    public ElasticsearchTemplate template;

    private final static Logger logger = Logger.getLogger(TfIdfExtractor.class);

    /**
     * extract term vectors from specific index/type
     * @param indexName
     * @param typeName
     * @param docId the unique document id of es
     * @param fields string array of target fields
     * @return
     */
    public JSONObject extractVectors(String indexName, String typeName, String docId, String[] fields){
        ListenableActionFuture<TermVectorsResponse> responses =  template.getClient().prepareTermVectors().setFieldStatistics(true)
                .setTermStatistics(true).setSelectedFields(fields).setIndex(indexName)
                .setType(typeName).setId(docId).execute();
        try {
            TermVectorsResponse response = responses.get(10, TimeUnit.SECONDS);

            Fields fieldsEnum = response.getFields();
            JSONObject jo = new JSONObject();
            Iterator<String> iterator = fieldsEnum.iterator();
            List<String> termStrings = new ArrayList<>();
            while (iterator.hasNext()) {
                String field = iterator.next();
                Terms terms = fieldsEnum.terms(field);
                TermsEnum termsEnum = terms.iterator();
                JSONArray cells = new JSONArray();
                while(termsEnum.next() != null){
                    int df = termsEnum.docFreq();
                    long totalTermFreq = termsEnum.totalTermFreq();
                    BytesRef term = termsEnum.term();
                    if (term != null) {
                        termStrings.add(term.utf8ToString());
                    }
                    JSONObject cell = new JSONObject();
                    cell.put("termName", term.utf8ToString());
                    cell.put("docFreq", df);
                    cell.put("totalTermFreq", totalTermFreq);
                    cells.add(cell);
                }
                jo.put(field, cells);
            }
            return jo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
