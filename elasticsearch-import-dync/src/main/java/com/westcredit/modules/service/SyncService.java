package com.westcredit.modules.service;


import com.google.gson.Gson;
import com.westcredit.modules.config.ElasticSearchConfig;
import com.westcredit.modules.config.SyncEntity;
import com.westcredit.modules.thread.Customer;
import com.westcredit.modules.thread.Page;
import com.westcredit.modules.thread.Producer;
import com.westcredit.modules.thread.Storage;
import com.westcredit.modules.util.ElasticSearchUtil;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SyncService {
    /**
     * ES客户端
     */
    @Autowired
    private TransportClient client;

    @Autowired
    private ElasticSearchUtil util;

    @Autowired
    private ElasticSearchConfig elasticSearchConfig;

    @Autowired
    private JdbcTemplate jdbcTemplate;



    public void syncData(Map<String, String> sqls) {
        ExecutorService service = Executors.newCachedThreadPool();
        Storage storage= new Storage();
        for(String key: sqls.keySet()){
            Page page = new Page(); //一张表之间分页数据共享
            for(int i=0; i<elasticSearchConfig.getpThreadNum(); i++){
                service.submit(new Producer(key,key,storage,jdbcTemplate,sqls.get(key), elasticSearchConfig, page).getRunbale());

            }
            for(int i=0; i<elasticSearchConfig.getcThreadNum(); i++){
                 service.submit(new Customer(storage).getRunbale());
            }
        }

    }
    public void creatIndexForSortFiled(String indexName,String filedName ){
        this.client.admin().indices().prepareCreate(indexName).get();
        client.admin().indices().preparePutMapping(indexName)
                .setType(indexName)
                .setSource("{\n" +
                        "  \"properties\": {\n" +
                        "    \""+filedName+"\": {\n" +
                        "      \"type\": \"keyword\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}", XContentType.JSON).get();
    }

    public boolean indexExists(String index){
        IndicesExistsRequest request = new IndicesExistsRequest(index);
        IndicesExistsResponse response = this.client.admin().indices().exists(request).actionGet();
        if (response.isExists()) {
            return true;
        }
        return false;
    }



    public void wirtetime(){
        File file = new File( elasticSearchConfig.getTimepath()+"time.txt");
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            String date = df.format(new Date());// new Date()为获取当前系统时间
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file);
            fw.write(date);
            fw.flush();
            System.out.println("写数据成功！");
            fw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public  String readDate(){
        File file = new File( elasticSearchConfig.getTimepath()+"time.txt");
        StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(System.lineSeparator()+s);
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result.toString();

    }
    public void schedlude(){
        String lastAnsyTime =readDate();
        System.out.println("上一次同步时间 ： "+ lastAnsyTime);
        wirtetime();
        if(lastAnsyTime==null){
            lastAnsyTime = "1997-07-07";
        }else{
            lastAnsyTime = lastAnsyTime.trim();
        }
        Map<String,String> sqls = new HashMap<>();
        Map<String,String> temp = elasticSearchConfig.getSqls();
        Gson gson = new Gson();
       for(String key: temp.keySet()){
            String sqlEntity = temp.get(key).toString();
            SyncEntity entity = gson.fromJson(sqlEntity,SyncEntity.class);
            if("increase".equals(entity.getSyncType())){
                sqls.put(key,entity.getSql() +" where "+entity.getSyncFiledName() +">=to_date('"+lastAnsyTime+"','yyyy-mm-dd hh24:mi:ss')");
            }else {
                sqls.put(key,entity.getSql());
            }
           if(!indexExists(key)){
               for(String sortFile: entity.getSortFile()){
                   creatIndexForSortFiled(key,sortFile);
               }
           }
       }
       syncData(sqls);
    }
}


































