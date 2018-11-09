package space.pankui.multi_thread_concurrent.disruptor;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * 介绍
 *
 * https://www.cnblogs.com/haiq/p/4112689.html
 *
 *
 *
 * @author pankui
 * @date 2018/10/31
 * <pre>
 *     https://my.oschina.net/2286252881/blog/865148
 *
 *  实例化Disruptor实例，参数配置。对Disruptor实例绑定监听事件类，接受并处理数据。
 *
 * </pre>
 *
 *
 * 这个disruptor JAR 目前只支持JDK8 ，到JDK9 由于jdk 包的重构，现在执行有问题
 */
public class LongEventMain {

    public static void main(String[] args) throws Exception {
        // 创建线程池
       // ExecutorService executor = Executors.newCachedThreadPool();
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        // 创建工厂
        LongEventFactory factory = new LongEventFactory();

        // 创建bufferSize ,也就是RingBuffer大小，必须是2的N次方
        int ringBufferSize = 1024 * 1024; //

        // 创建disruptor
        /**
         * 1.factory:工厂对象，创建数据对象
         * 2.ringBufferSize:指定缓存区
         * 3.executor:线程池，用于在disruptor内部数据接收处理
         * 4.SINGLE或MULTI:SINGLE一个生产者，MULTI多个生产者
         * 5.new YieldingWaitStrategy():disruptor策略
         */
        /**
         * //BlockingWaitStrategy 是最低效的策略，但其对CPU的消耗最小并且在各种不同部署环境中能提供更加一致的性能表现
         * WaitStrategy BLOCKING_WAIT = new BlockingWaitStrategy();
         * //SleepingWaitStrategy
         * 的性能表现跟BlockingWaitStrategy差不多，对CPU的消耗也类似，但其对生产者线程的影响最小，适合用于异步日志类似的场景
         * WaitStrategy SLEEPING_WAIT = new SleepingWaitStrategy();
         * //YieldingWaitStrategy
         * 的性能是最好的，适合用于低延迟的系统。在要求极高性能且事件处理线数小于CPU逻辑核心数的场景中，推荐使用此策略；例如，
         * CPU开启超线程的特性 WaitStrategy YIELDING_WAIT = new YieldingWaitStrategy();
         *
         */
        Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(factory, ringBufferSize, threadFactory,
                ProducerType.SINGLE, new YieldingWaitStrategy());

        // 连接消费事件方法
        disruptor.handleEventsWith(new LongEventHandler());

        // 启动
        disruptor.start();

        // Disruptor 的事件发布过程是一个两阶段提交的过程：
        // 存放数据
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
        //通过生产端发布事件
        LongEventProducer producer = new LongEventProducer(ringBuffer);
        // LongEventProducerWithTranslator producer = new
        // LongEventProducerWithTranslator(ringBuffer);
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        for (long l = 0; l < 100; l++) {
            byteBuffer.putLong(0, l);
            producer.onData(byteBuffer);
            // Thread.sleep(1000);
        }

        disruptor.shutdown();// 关闭 disruptor，方法会堵塞，直至所有的事件都得到处理；
        //executor.shutdown();// 关闭 disruptor 使用的线程池；如果需要的话，必须手动关闭， disruptor 在
        // shutdown 时不会自动关闭；

    }
}
