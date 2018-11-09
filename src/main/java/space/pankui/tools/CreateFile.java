package space.pankui.tools;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @author pankui
 * @date 2018/9/12
 * <pre>
 *   生成文件
 * </pre>
 */
public class CreateFile {


    public void file(String file, String str) {
        FileWriter fw = null;
        try {
            //  传递一个true，代表不覆盖原有内容。
            fw = new FileWriter("/文件夹/" + file, true);
            fw.write(str);
        } catch (IOException ex) {
            System.out.println("创建文件流错误");
        } finally {
            try {
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException ex) {
                System.out.println("关闭流错误。");
            }
        }

        System.out.println("##### 生成文件成功");

    }
}
