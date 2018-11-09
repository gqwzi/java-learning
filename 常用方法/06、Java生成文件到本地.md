
# Java生成文件到本地

## 上代码

```java
 public void file(String file, String str) {
        FileWriter fw = null;
        try {
            //  传递一个true，代表不覆盖原有内容。
            fw = new FileWriter("/Users/Downloads/" + file, true);
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
```