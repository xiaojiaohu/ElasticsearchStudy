package com.test;

import com.utils.EsClient;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import java.io.IOException;

public class FilterSearch {
    RestHighLevelClient client =  EsClient.getClient();
    String index = "sms-logs-index";
    String type="sms-logs-type";


    @Test
    public void filter() throws IOException {

        // 1.searchRequest
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);

        // 2.指定查询条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        boolBuilder.filter(QueryBuilders.termQuery("corpName","海尔智家公司"));
        boolBuilder.filter(QueryBuilders.rangeQuery("fee").gt(20));
        boolBuilder.filter(QueryBuilders.boolQuery().
                should(QueryBuilders.termQuery("province","晋城")).should(QueryBuilders.termQuery("state",1)));
        sourceBuilder.query(boolBuilder);
        searchRequest.source(sourceBuilder);

        System.out.println(sourceBuilder.toString());

        //  3.执行
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        //  4. 输出结果
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
            System.out.println(hit.getId()+"的分数是："+hit.getScore());
        }
    }

    /**
     * {
     *     "query":{
     *         "bool":{
     *             "filter":[
     *                 {
     *                     "term":{
     *                         "corpName":{
     *                             "value":"海尔智家公司",
     *                             "boost":1
     *                         }
     *                     }
     *                 },
     *                 {
     *                     "range":{
     *                         "fee":{
     *                             "from":20,
     *                             "to":null,
     *                             "include_lower":false,
     *                             "include_upper":true,
     *                             "boost":1
     *                         }
     *                     }
     *                 },
     *                 {
     *                     "bool":{
     *                         "should":[
     *                             {
     *                                 "term":{
     *                                     "province":{
     *                                         "value":"晋城",
     *                                         "boost":1
     *                                     }
     *                                 }
     *                             },
     *                             {
     *                                 "term":{
     *                                     "state":{
     *                                         "value":1,
     *                                         "boost":1
     *                                     }
     *                                 }
     *                             }
     *                         ],
     *                         "adjust_pure_negative":true,
     *                         "boost":1
     *                     }
     *                 }
     *             ],
     *             "adjust_pure_negative":true,
     *             "boost":1
     *         }
     *     }
     * }
     */
}
