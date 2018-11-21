package space.pankui.source;

import java.io.Closeable;
import java.io.IOException;

import static java.nio.channels.AsynchronousServerSocketChannel.open;

/**
 * @author pankui
 * @date 2018/11/21
 * <pre>
 *    stack=1, locals=3, args_size=1
 *          0: aconst_null
 *          1: astore_1
 *          2: invokestatic  #2                  // Method java/nio/channels/AsynchronousServerSocketChannel.open:()Ljava/nio/channels/AsynchronousServerSocketChannel;
 *          5: astore_1
 *          6: aload_1
 *          7: ifnull        32
 *         10: aload_1
 *         11: invokeinterface #3,  1            // InterfaceMethod java/io/Closeable.close:()V
 *         16: goto          32
 *         19: astore_2
 *         20: aload_1
 *         21: ifnull        30
 *         24: aload_1
 *         25: invokeinterface #3,  1            // InterfaceMethod java/io/Closeable.close:()V
 *         30: aload_2
 *         31: athrow
 *         32: return
 *       Exception table:
 *          from    to  target type
 *              2     6    19   any
 *
 *
 *
 *              字节码finally 生成了两份!!!!
 * </pre>
 */
public class FinallyByteCode {

    void example() throws IOException {
        Closeable resource = null;
        try {
            resource = open();
            // do something with resource
        } finally { // this block is emitted twice:
            if (resource != null) {
                resource.close(); // not executed on exception path from method `open`
            }
        }
    }
}
