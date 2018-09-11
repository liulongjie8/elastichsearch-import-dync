package com.westcredit.modules.thread;
import javax.annotation.Resource;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Resource
public class Storage {

    public  BlockingQueue<com.westcredit.modules.thread.Resource > queues = new LinkedBlockingQueue<com.westcredit.modules.thread.Resource >(10);


    /**
     * 生产
     *
     * @param resource
     *            产品
     * @throws InterruptedException
     */
    public void push(com.westcredit.modules.thread.Resource resource) throws InterruptedException {
        System.out.println("add---------------"+queues.size());
        queues.put(resource);
    }

    /**
     * 消费
     *
     * @return 产品
     * @throws InterruptedException
     */
    public com.westcredit.modules.thread.Resource  pop() throws InterruptedException {
        System.out.println("pop---------------"+queues.size());
        return queues.take();
    }

}
