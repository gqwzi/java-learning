package space.pankui.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystem;

/**
 * @author pankui
 * @date 2018/9/12
 * <pre>
 *   下载网络文件
 * </pre>
 */
public class DownloadNetWorkFile {


    public static void download(String urlString,String dirPath, String filename) throws Exception {
        // 构造URL
        URL url = new URL(urlString);
        // 打开连接
        URLConnection con = url.openConnection();


        // 输入流
        InputStream is = con.getInputStream();
        // 1K的数据缓冲
        byte[] bs = new byte[1024];
        // 读取到的数据长度
        int len;
        // 输出的文件流
        OutputStream os = new FileOutputStream( dirPath+ File.pathSeparator +filename);
        // 开始读取
        while ((len = is.read(bs)) != -1) {
            os.write(bs, 0, len);
        }

        System.out.println(" 下载图片成功");
        // 完毕，关闭所有链接
        os.close();
        is.close();
    }
}
