package space.pankui.multi_thread_concurrent.disruptor;

import com.lmax.disruptor.EventHandler;

/**
 * @author pankui
 * @date 2018/10/31
 * <pre>
 * 需要有一个监听事件类，用于处理数据（Event类）
 *
 * 消费者监听，也就是一个事件处理器。该事件用于获取disruptor存储的数据。
 * </pre>
 */
public class LongEventHandler implements EventHandler<LongEvent> {

    @Override
    public void onEvent(LongEvent longEvent, long l, boolean b) throws Exception {
        System.out.println(longEvent.getValue());
    }

}
