package space.pankui.demo.tess4j;

import java.io.File;

import net.sourceforge.tess4j.*;
/**
 * @author pankui
 * @date 2018/11/16
 * <pre>
 *
 * </pre>
 */
public class Tess4jDemo {

    public static void main(String[] args) throws TesseractException {


        ITesseract instance = new Tesseract();
        //如果未将tessdata放在根目录下需要指定绝对路径
        //instance.setDatapath("the absolute path of tessdata");
        // 我们需要指定识别语种
        instance.setLanguage("chi_sim");
        // 指定识别图片
        File imgDir = new File("image_test4j.jpeg");
        long startTime = System.currentTimeMillis();
        String ocrResult = instance.doOCR(imgDir);
        // 输出识别结果
        System.out.println("OCR Result: \n" + ocrResult + "\n 耗时：" + (System.currentTimeMillis() - startTime) + "ms");


    }
}
