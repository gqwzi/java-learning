

# java 执行shell 命令

Runtime

其实这个在java1.0 出来的时候就有了,

> 用同事一个句话说 '清朝早亡了'


## [java 执行shell cd 命令](https://stackoverflow.com/questions/4884681/how-to-use-cd-command-using-java-runtime)

```java
String[] cmd = { "/bin/sh", "-c", "cd /var; ls -l" };
Process p = Runtime.getRuntime().exec(cmd);
```

## [Running system commands in Java applications](https://alvinalexander.com/java/edu/pj/pj010016)

```java
 public static void main(String args[]) {

        String s = null;

        try {
            
	    // run the Unix "ps -ef" command
            // using the Runtime exec method:
            Process p = Runtime.getRuntime().exec("ps -ef");
            
            BufferedReader stdInput = new BufferedReader(new 
                 InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new 
                 InputStreamReader(p.getErrorStream()));

            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }
            
            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
            
            System.exit(0);
        }
        catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
        }
    }
```