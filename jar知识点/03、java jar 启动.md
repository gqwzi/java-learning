

# java jar的启动命令


指定垃圾收集器 G1 

```jshelllanguage
 java -Dfile.encoding=UTF-8 -Xms1G -Xmx2G -server -XX:SurvivorRatio=8 -XX:MaxMetaspaceSize=512M -XX:+UseG1GC -XX:CompressedClassSpaceSize=512M -XX:MaxTenuringThreshold=5  
 -XX:InitiatingHeapOccupancyPercent=70 -Dlogs=logs -verbose:gc -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:logs/gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=1 -XX:GCLogFileSize=512M -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9999 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -jar demo.jar --server.port=8080 &
```


查看成活率前 100

> jmp -histo:live [pid] | head -n 100