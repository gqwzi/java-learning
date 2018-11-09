package space.pankui.io;

import java.io.*;

/**
 * @author pankui
 * @date 2018/11/5
 * <pre>
 *  原文:https://www.jianshu.com/p/61b23e92d03f
 *
 *  java 复制指定路径下的所有
 *
 * </pre>
 */
public class CopyDirectioryOrFile {

    /**
     * 源文件夹 "D:/min_res/video/1565"
     */
    static String url1 = System.getProperty("user.home") + "/Desktop/video";
    /**
     * 目标文件夹 "D:/min_res/video/551/"
     */
    static String url2 = System.getProperty("user.home") + "/Desktop/video2";

    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        // 创建目标文件夹
        copyDirectiory(url1, url2);
    }

    private static void copyFile(File sourcefile, File targetFile) throws IOException {

        // 新建文件输入流并对它进行缓冲
        FileInputStream input = new FileInputStream(sourcefile);

        // 新建文件输出流并对它进行缓冲
        FileOutputStream out = new FileOutputStream(targetFile);
        BufferedOutputStream outbuff = new BufferedOutputStream(out);

        // 缓冲数组
        byte[] b = new byte[1024];
        int len = 0;
        while ((len = input.read(b)) != -1) {
            outbuff.write(b, 0, len);
        }

        //关闭文件
        outbuff.close();
        input.close();

    }

    private static void copyDirectiory(String sourceDir, String targetDir) throws IOException {

        // 新建目标目录
        (new File(targetDir)).mkdirs();

        // 获取源文件夹当下的文件或目录
        File[] file = (new File(sourceDir)).listFiles();

        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                // 源文件
                File sourceFile = file[i];
                // 目标文件
                File targetFile = new File(targetDir + File.separator + sourceFile.getName());
                copyFile(sourceFile, targetFile);

            }

            if (file[i].isDirectory()) {
                // 准备复制的源文件夹
                String dir1 = sourceDir + File.separator + file[i].getName();
                // 准备复制的目标文件夹
                String dir2 = targetDir + File.separator + file[i].getName();

                copyDirectiory(dir1, dir2);
            }
        }

    }
}
