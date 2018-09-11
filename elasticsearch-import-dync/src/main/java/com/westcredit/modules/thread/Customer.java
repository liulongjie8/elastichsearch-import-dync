package com.westcredit.modules.thread;
import com.westcredit.modules.util.ElasticSearchUtil;
import com.westcredit.modules.util.SpringUtil;

/**
 * 消费者
 */
public class Customer {


    private Storage storage;

    private   ElasticSearchUtil util = SpringUtil.getBean(ElasticSearchUtil.class);


    public  Runnable getRunbale() {
        return new Runnable() {
            @Override
            public void run()  {
                while(true){
                    try {
                        Resource resource = storage.pop();
                        System.out.println(Thread.currentThread().getName()+"---"+resource.getIndex());
                        if("end".equals(resource.getIndex())){
                            System.out.println("同步完毕  ---" + resource.getType());
                            break;
                        }
                        util.BulkProcessAdd(resource.getIndex(), resource.getType(), resource.getDocument());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    public Customer( Storage storage) {
        this.storage = storage;
    }
}
