package space.pankui.multi_thread_concurrent.disruptor;

/**
 * @author pankui
 * @date 2018/10/31
 * <pre>
 *
 *  原文 https://my.oschina.net/2286252881/blog/865148
 *
 * 1.建立一个Event类(数据对象)
 *
 * 第一：建立一个Event类
 *
 * 第二：建立一个工厂Event类，用于创建Event类实例对象
 *
 * 第三：需要有一个监听事件类，用于处理数据（Event类）
 *
 * 第四：我们需要进行测试代码编写。实例化Disruptor实例，配置一系列参数。然后我们对Disruptor实例绑定监听事件类，接受并处理数据。
 *
 * 第五：在Disruptor中，真正存储数据的核心叫做RingBuffer，我们通过Disruptor实例拿到它，然后把数据生产出来，把数据加入到RingBuffer的实例对象中即可。
 *
 * 备注：RingBuffer表示环状的缓存
 *
 * </pre>
 */
public class LongEvent {

    private long value;


    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
