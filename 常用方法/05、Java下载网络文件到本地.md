

# Java下载网络文件到本地

## 上代码

```java
    public static void download(String urlString, String filename) throws Exception {
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
        // 瘟都死的用户这里注意一下
        OutputStream os = new FileOutputStream("/Users/Downloads/" + filename);
        // 开始读取
        while ((len = is.read(bs)) != -1) {
            os.write(bs, 0, len);
        }

        System.out.println(" 下载图片成功");
        // 完毕，关闭所有链接
        os.close();
        is.close();
    }

``` 
    