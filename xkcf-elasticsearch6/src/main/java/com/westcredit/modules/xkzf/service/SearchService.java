package com.westcredit.modules.xkzf.service;

import com.westcredit.modules.util.ElasticSearchUtil;
import com.westcredit.modules.xkzf.entity.QueryResult;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.collapse.CollapseBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    /**
     * ES客户端
     */
    @Autowired
    private TransportClient client;

    @Autowired
    private ElasticSearchUtil searchUtil;

    /**
     * 根据条件查询许可信息
     *
     * @param baseCreditCorpusCode
     * @param xk_xdr_mc
     * @param xk_wsh
     * @param xk_xkmc
     * @param city_code
     * @param keywords
     * @param pagesize
     * @param pagenum
     * @return
     * @throws IOException
     */
    public QueryResult getXK(String baseCreditCorpusCode, String xk_xdr_mc,
                             String xk_wsh, String xk_xkmc, String city_code, String areaType,
                             String xk_xzjg, String id, Integer pagesize, Integer pagenum,
                             String code) throws Exception {
        String areaName = null;
        if (city_code != null && areaType != null) { // 填充区域
            areaName = getAreaName(areaType);
        }
        QueryResult result = new QueryResult();
        QueryBuilder builder = null;
        SearchRequestBuilder searchBuilder= null;
        if(baseCreditCorpusCode==null){  // 根据baseCreditCorpusCode出事查询client
            searchBuilder = this.client.prepareSearch("gs_l_xzxk", "gs_p_xzxk")
                    .setTypes("gs_l_xzxk", "gs_p_xzxk");
        }else{
            String index = getXkNameByBase(baseCreditCorpusCode);
            searchBuilder = this.client.prepareSearch(index)
                    .setTypes(index);
        }
        //如果没有任何条件查询
        if (xk_xdr_mc == null && xk_wsh == null && xk_xkmc == null
                && city_code == null && areaType == null && code == null
                && id == null) {
           builder = QueryBuilders.matchAllQuery();

        } else { //如果有查询条件
            builder=createQueryByilder_XK(xk_xdr_mc, xk_wsh, xk_xkmc, city_code,
                    xk_xzjg, areaName, id, code);
        }
        searchBuilder.setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(builder)
                .setFrom((pagenum - 1) * pagesize)
                .setSize(pagesize)
                .addSort("_score",SortOrder.DESC)
                .addSort("update_date", SortOrder.DESC)
                .highlighter(getXKHighlightBuilder());
        searchBuilder = filter(searchBuilder,areaName,city_code);
        SearchResponse response = searchBuilder.get();
        result = formatResponse(response);
        return result;
    }


    /**
     * 根据关键字查询许可信息
     * @param keywords
     * @param pagesize
     * @param pagenum
     * @param areacode
     * @param areatype
     * @return
     */
    public QueryResult searchXKWithKeyWords(String keywords, Integer pagesize,
                                Integer pagenum, String areaCode, String areaType) throws Exception {
        QueryResult result = new QueryResult();
        QueryBuilder builder = null;
        String areaName = null;
        if (areaCode != null && areaType != null) { // 填充区域
            areaName = getAreaName(areaType);
        }
        SearchRequestBuilder searchBuilder = this.client.prepareSearch( "gs_p_xzxk","gs_l_xzxk")
                .setTypes( "gs_p_xzxk","gs_l_xzxk");
        if(keywords==null || "".equals(keywords.trim())){
            builder = QueryBuilders.matchAllQuery();
        }else{
            builder = QueryBuilders.boolQuery()
                    .should(QueryBuilders.matchPhraseQuery("xk_xdr_mc", keywords).slop(100))
                    .should(QueryBuilders.matchPhraseQuery("xk_wsh", keywords).slop(100))
                    .should(QueryBuilders.matchPhraseQuery("xk_xdr_shxym", keywords).slop(100))
                    .should(QueryBuilders.matchPhraseQuery("xk_xdr_gsdj",keywords).slop(100))
                    .should(QueryBuilders.matchPhraseQuery("xk_xzjg",keywords).slop(100)).minimumShouldMatch(1);
        }
        searchBuilder.setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(builder)
                .setFrom((pagenum - 1) * pagesize)
                .setSize(pagesize)
                .addSort("_score",SortOrder.DESC)
                .addSort("update_date", SortOrder.DESC)
                .highlighter(getXKHighlightBuilder());
        searchBuilder = filter(searchBuilder,areaName,areaCode);
        SearchResponse response = searchBuilder.get();
        result = formatResponse(response);
        return  result;
    }
    /**
     * 根据关键字查询处罚信息
     * @param keywords
     * @param pagesize
     * @param pagenum
     * @param areacode
     * @param areatype
     * @return
     */
    public QueryResult searchCFWithKeyWords(String keywords, Integer pagesize,
                                            Integer pagenum, String areaCode, String areaType) throws Exception {
        CollapseBuilder collapseBuilder = new CollapseBuilder("cf_xdr_mc");
        QueryResult result = new QueryResult();
        QueryBuilder builder = null;
        String areaName = null;
        if (areaCode != null && areaType != null) { // 填充区域
            areaName = getAreaName(areaType);
        }
        SearchRequestBuilder searchBuilder = this.client.prepareSearch("gs_l_xzcf", "gs_p_xzcf")
                .setTypes("gs_l_xzcf", "gs_p_xzcf");
        if(keywords==null || "".equals(keywords.trim())){
            builder = QueryBuilders.matchAllQuery();
        }else{
            builder = QueryBuilders.boolQuery()
                    .should(QueryBuilders.matchPhraseQuery("cf_xdr_mc", keywords).slop(100))
                    .should(QueryBuilders.matchPhraseQuery("cf_wsh", keywords).slop(100))
                    .should(QueryBuilders.matchPhraseQuery("cf_xdr_gsdj", keywords).slop(100))
                    .should(QueryBuilders.matchPhraseQuery("cf_xdr_shxym",keywords).slop(100))
                    .should(QueryBuilders.matchPhraseQuery("cf_xzjg",keywords).slop(100)).minimumShouldMatch(1);
        }
        searchBuilder.setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(builder)
                .setFrom((pagenum - 1) * pagesize)
                .setSize(pagesize)
                .addSort("_score",SortOrder.DESC)
                .addSort("update_date", SortOrder.DESC)
              //  .setCollapse(collapseBuilder)
                .highlighter(getXKHighlightBuilder());
        searchBuilder = filter(searchBuilder,areaName,areaCode);
        SearchResponse response = searchBuilder.get();
        result = formatResponse(response);
        return  result;
    }

    /**
     * 获取行政处罚查询
     * @param baseCreditCorpusCode
     * @param cf_xdr_mc
     * @param cf_wsh
     * @param cf_xdr_shxym
     * @param area_code
     * @param areaType
     * @param cf_xzjg
     * @param id
     * @param pagesize
     * @param pagenum
     * @param code
     * @return
     * @throws IOException
     */
    public QueryResult getCF(String baseCreditCorpusCode, String cf_xdr_mc,
                             String cf_wsh, String cf_xdr_shxym, String area_code,
                             String areaType, String cf_xzjg, String id, Integer pagesize,
                             Integer pagenum, String code) throws Exception {
        QueryResult result = new QueryResult();
        String areaName = null;
        if (area_code != null && areaType != null) { // 填充区域
            areaName = getAreaName(areaType);
        }

        QueryBuilder builder = null;
        SearchRequestBuilder searchBuilder= null;
        if(baseCreditCorpusCode==null){  // 根据baseCreditCorpusCode出事查询client
            searchBuilder = this.client.prepareSearch("gs_l_xzcf", "gs_p_xzcf")
                    .setTypes("gs_l_xzcf", "gs_p_xzcf");
        }else{
            String index = getCFNameByBase(baseCreditCorpusCode);
            searchBuilder = this.client.prepareSearch(index)
                    .setTypes(index);
        }
        if (cf_xdr_mc == null && cf_wsh == null && cf_xdr_shxym == null
                && area_code == null && areaType == null && code == null
                && id == null) {
            builder = QueryBuilders.matchAllQuery();
        } else {
            builder=createQueryByilder_CF(cf_xdr_mc, cf_wsh, cf_xdr_shxym, cf_xzjg,
                    area_code, id, areaName, code);
        }
        searchBuilder.setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(builder)
                .setFrom((pagenum - 1) * pagesize)
                .setSize(pagesize)
                .addSort("_score",SortOrder.DESC)
                .addSort("update_date", SortOrder.DESC)
                .highlighter(getCFHighlightBuilder());
        searchBuilder = filter(searchBuilder,areaName,area_code);
        SearchResponse response = searchBuilder.get();
        result = formatResponse(response);
        return result;
    }

    /**
     * 将Response转化成 QueryResult 对象
     * @param response
     * @return
     * @throws Exception
     */
    private  QueryResult formatResponse(SearchResponse response) throws Exception {
        QueryResult result = new QueryResult();
        if(response.status()!= RestStatus.OK){
            throw new Exception("elasticsearch查询异常， 检查其是否启动");
        }
        List<Map<String, Object>> resources= new ArrayList<Map<String, Object>>();
        SearchHits hits=response.getHits();
        result.setCount(Double.valueOf(hits.getTotalHits()+""));
        for(SearchHit hit : hits){
            Map<String, Object> map =  hit.getSourceAsMap();
            Map<String, HighlightField> highs=hit.getHighlightFields();
            if(highs!=null){
                for(String key : highs.keySet()){
                    for(String _key : map.keySet()){
                        if(key.equals(_key)){
                            map.put(key,highs.get(key).getFragments()[0].toString());
                        }
                    }
                }
            }
            map.put("type",hit.getType());
            resources.add(map);
        }
        result.setResult(resources);
        result.setTime(response.getTook().getMinutesFrac());
        return result;
    }

    /**
     * 获取许可高量数据
     * @return
     */
    private HighlightBuilder getXKHighlightBuilder(){
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field(new HighlightBuilder.Field("xk_xdr_mc"));
        highlightBuilder.field(new HighlightBuilder.Field("xk_wsh"));
        highlightBuilder.field(new HighlightBuilder.Field("xk_xkmc"));
        highlightBuilder.field(new HighlightBuilder.Field("xk_xzjg"));
        return  highlightBuilder;
    }


    /**
     * 获取处罚高量显示
     * @return
     */
    private HighlightBuilder getCFHighlightBuilder() {
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field(new HighlightBuilder.Field("cf_xdr_mc"));
        highlightBuilder.field(new HighlightBuilder.Field("cf_wsh"));
        highlightBuilder.field(new HighlightBuilder.Field("cf_xdr_shxym"));
        highlightBuilder.field(new HighlightBuilder.Field("cf_xzjg"));
        return  highlightBuilder;
    }



    /**
     * 获取区域查询字段
     * @param areatType
     * @return
     */
    private String getAreaName(String areatType){
        String areaName="";
        AreaLevel level = AreaLevel.levelOf(areatType);
        switch (level) {
            case COUNTRY:
                break;
            case PROVINCE:
                areaName = "province_code";
                break;
            case CITY:
                areaName = "city_code";
                break;
            case COUNTY:
                areaName = "region_code";
                break;
            default:
                break;
        }
        return  areaName;
    }


    /**
     * 初始化许可查询
     * @param xk_xdr_mc  许可相对人名称
     * @param xk_wsh  许可文书号
     * @param xk_xkmc  许可名称
     * @param city_code 区域Code
     * @param xk_xzjg  许可机构
     * @param areaName  区域查询字段
     * @param id
     * @param code  主体M010101
     * @return
     */
    private QueryBuilder createQueryByilder_XK(String xk_xdr_mc, String xk_wsh, String xk_xkmc, String city_code,
                                             String   xk_xzjg, String areaName, String  id, String code){
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        if(xk_xdr_mc!=null){
            builder.must(QueryBuilders.matchPhraseQuery("xk_xdr_mc",xk_xdr_mc).slop(100));
        }
        if(xk_wsh!=null){
            builder.must(QueryBuilders.matchPhraseQuery("xk_wsh",xk_wsh).slop(100));
        }
        if(xk_xkmc!=null){
            builder.must(QueryBuilders.matchPhraseQuery("xk_xkmc",xk_xkmc).slop(100));
        }
        if(city_code!=null){
            builder.must(QueryBuilders.matchQuery(areaName,city_code));
        }
        if(xk_xzjg!=null){
            builder.must(QueryBuilders.matchPhraseQuery("xk_xzjg",xk_xzjg).slop(100));
        }
        if(id!=null){
            builder.must(QueryBuilders.matchQuery("id",id));
        }
        if(code!=null){
            builder.must(QueryBuilders.matchQuery("m_010101",code));
        }
        return  builder;
    }

    /**
     * 创建处罚查询
     * @param cf_xdr_mc 处罚相对人名称
     * @param cf_wsh  处罚文书号
     * @param cf_xdr_shxym
     * @param cf_xzjg  处罚机构
     * @param area_code  区域Code
     * @param id
     * @param areaName 区域名称
     * @param code  M010101
     * @return
     */
    private QueryBuilder createQueryByilder_CF(String cf_xdr_mc, String cf_wsh, String cf_xdr_shxym, String cf_xzjg, String area_code, String id, String areaName, String code) {
        BoolQueryBuilder builder = QueryBuilders.boolQuery();

        if(cf_xdr_mc!=null){
            builder.must(QueryBuilders.matchPhraseQuery("cf_xdr_mc",cf_xdr_mc).slop(100));
        }
        if(cf_wsh!=null){
            builder.must(QueryBuilders.matchPhraseQuery("cf_wsh",cf_wsh).slop(100));
        }
        if(cf_xdr_shxym!=null){
            builder.must(QueryBuilders.matchPhraseQuery("cf_xdr_shxym",cf_xdr_shxym).slop(100));
        }
        if(cf_xzjg!=null){
            builder.must(QueryBuilders.matchPhraseQuery("cf_xzjg",cf_xzjg).slop(100));
        }
        if(area_code!=null){
            builder.must(QueryBuilders.matchQuery(areaName,area_code));
        }
        if(id!=null){
            builder.must(QueryBuilders.matchPhraseQuery("id",id).slop(100));
        }
        if(code!=null){
            builder.must(QueryBuilders.matchQuery("m_010101",code));
        }
        return  builder;
    }


    /**
     * 根据主体信息获取查询表名
     *
     * @param baseCreditCorpusCode
     * @return
     */
    private String getXkNameByBase(String baseCreditCorpusCode) {
        String tableName = null;
        if ("P".equals(baseCreditCorpusCode)) {
            tableName = "gs_p_xzxk";
        } else if ("L".equals(baseCreditCorpusCode)) {
            tableName = "gs_l_xzxk";
        } else if (baseCreditCorpusCode == null) {
            tableName = "gs_l_xzxk,gs_p_xzxk";
        }
        return tableName;
    }

    /**
     * 根据主体信息获取查询表名
     * @param baseCreditCorpusCode
     * @return
     */
    private String getCFNameByBase(String baseCreditCorpusCode) {
        String tableName = null;
        if ("P".equals(baseCreditCorpusCode)) {
            tableName = "gs_p_xzcf";
        } else if ("L".equals(baseCreditCorpusCode)) {
            tableName = "gs_l_xzcf";
        } else if (baseCreditCorpusCode == null) {
            tableName = "gs_l_xzcf,gs_p_xzcf";
        }
        return tableName;
    }

    /**
     * 添加filter
     * @param builder 查询
     * @param areaName 区域名称
     * @param areaCode 区域Code
     * @return
     */
    private SearchRequestBuilder filter(SearchRequestBuilder builder,String areaName,String areaCode){

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if(areaName!=null){
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery(areaName,areaCode));
        }
        boolQueryBuilder.must(QueryBuilders.rangeQuery("m_010101").gt(0));
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("is_peat","1"));
        boolQueryBuilder.must(QueryBuilders.boolQuery().mustNot(QueryBuilders.matchQuery("publish_state","0")));
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("del_flag","0"));
        return  builder.setPostFilter(boolQueryBuilder);
    }




}
