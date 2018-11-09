package space.pankui.multi_thread_concurrent.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * @author pankui
 * @date 2018/10/31
 * <pre>
 * 2.建立一个工厂Event类，用于创建Event类实例对象
 * 需要让disruptor为我们创建事件，声明了一个EventFactory来实例化Event对象。
 * </pre>
 */
public class LongEventFactory implements EventFactory {

    @Override
    public Object newInstance() {
        return new LongEvent();
    }
}
