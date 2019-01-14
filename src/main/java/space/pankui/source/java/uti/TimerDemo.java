package space.pankui.source.java.uti;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * @author pankui
 * @date 2019-01-13
 * <pre>
 *
 * </pre>
 */
public class TimerDemo {

    public static void main(String[] args) {
        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                System.out.println("task1 run ...  execute time:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                try {
                    TimeUnit.SECONDS.sleep(2);
                    // 异常会导致所有的定时任务都会停
                   // throw new RuntimeException("task1 run ...  execute time RuntimeException");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                System.out.println("task2 run ...  execute time:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Timer t = new Timer();
        // 这里在往队列里面添加 定时任务，然后定时任务里会去唤醒线程
        /**
         * private void sched(TimerTask task, long time, long period) {
         *  // 其他代码省略
         *   queue.add(task);
         *             if (queue.getMin() == task){
         *                 queue.notify();
         *              }
         *  }
         * */
        t.schedule(task1, 0,1000);
        t.schedule(task2, 0,1000);
    }
}
