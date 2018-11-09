package space.pankui.jdk5;

/**
 * @author pankui
 * @date 2018/7/1
 * <pre>
 *
 * </pre>
 */
public class PBDemo {

    public static void main(String[] args) {
        try {
            //
            ProcessBuilder proc = new ProcessBuilder("vim", "testfile.txt");
            proc.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error executing notepad.");
        }
    }

}
