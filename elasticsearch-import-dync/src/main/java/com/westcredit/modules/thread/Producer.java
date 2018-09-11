package com.westcredit.modules.thread;

import com.westcredit.modules.config.ElasticSearchConfig;
import org.springframework.jdbc.core.JdbcTemplate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 生产者
 */
public class Producer   {

    private String index;

    private String type;

    private Storage storage;

    private String sql;

    private JdbcTemplate jdbcTemplate;

    private ElasticSearchConfig config;

    private Page page;


    public   Runnable getRunbale()  {
      return  new Runnable() {
           @Override
           public void run() {
               Integer size = config.getPageSize();
               Integer count = 0;
               try {

                   System.out.println("select count(1) from ("+sql+") t");

                    count = jdbcTemplate.queryForObject("select count(1) from ("+sql+") t" , Integer.class);
               }catch (Exception e){
                   e.printStackTrace();
               }
               System.out.println("数据库记录数: " +count);
               System.out.println(" 总页数： "+  count/size);
               while(page.getPageNo()<=((count/size)+1)){
                   long time = System.currentTimeMillis();
                   System.out.println("数据库获取-------start-----"+type+"----"+page.getPageNo()*size);
                   String _sql="SELECT  * FROM  " +
                           "(  " +
                           "SELECT A.*, ROWNUM RN  " +
                           "FROM ("+sql+" ) A  " +
                           "WHERE ROWNUM <= ? " +
                           ")  " +
                           "WHERE RN >= ? ";
                   List<Map<String,Object>> resources = null ;
                   try {
                       resources = jdbcTemplate.queryForList(_sql, size*page.getPageNo(), (page.getPageNo()-1)*size);
                   }catch (Exception e){
                       e.printStackTrace();
                   }
                   System.out.println("数据库获取-------end---"+resources.size()+" , 耗时：" + (System.currentTimeMillis()-time)/1000);
                   Resource r = new Resource(index, type, resources);
                   try {
                       storage.push(r);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                   page.add();
               }
               Resource r = new Resource("end", type, null);
               try {
                   storage.push(r);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
       };
    }

    public Producer(String index, String type, Storage storage,JdbcTemplate template ,String sql,  ElasticSearchConfig config , Page page) {
        this.index = index;
        this.type = type;
        this.storage = storage;
        this.jdbcTemplate = template;
        this.sql = sql;
        this.config = config;
        this.page = page;
    }

}
