package com.test;

import com.utils.EsClient;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.Test;

import java.io.IOException;

public class HigLightDemo {

    RestHighLevelClient client =  EsClient.getClient();
    String index = "sms-logs-index";
    String type="sms-logs-type";

    @Test
    public void highLightQuery() throws IOException {
      // 1.创建request
        SearchRequest request = new SearchRequest(index);
        request.types(type);

      // 2.指定查询条件，指定高亮
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //2.1 指定查询条件
        builder.query(QueryBuilders.matchQuery("smsContent","团队"));
        //2.2 指定高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("smsContent",10)
                .preTags("<font colr='red'>")
                .postTags("</font>");
        builder.highlighter(highlightBuilder);
        System.out.println(builder.toString());
        request.source(builder);

      // 3.执行
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        //4. 输出结果
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getHighlightFields().get("smsContent"));
        }
    }
}
