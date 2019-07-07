# ResumeFromBreakPointForJava

> #### 基于Java的断点续传中间件

## 概览

* 使用Java原生的Socket实现文件的断点续传功能。
* 支持源文件与临时下载文件防篡改。
* 支持文件传输加密。
* 临时文件将自动删除。
* 轻松调用，配备丰富文档。
* 单文件块默认为64KB，机械硬盘传输最高可达8MB/S

## 下载



## 使用

本中间件的断点续传需要调用两个类，发别用来发送和接收。*



**第一步**

#### 创建接收端实例

接收时需要使用Receiver类

操作同上

```
import cn.knightapple.ReceivePart.Receiver;
```

然后创建Receiver实例

```
Receiver receiver = new Receiver(12345);
receiver.setFileNameTo("请输入接收的文件所在的地址");
```



#### 创建发送端实例

发送功能请使用Sander类

首先导入该类

```
import cn.knightapple.SendPart.Sender;
```

然后创建Sender实例

```java
Socket socket = new Socket("localhost", 12345);
Sender sender = new Sender(socket, "请输入要发送的文件所在地址"); 
```

**注意：所使用的socket请勿同时用与其他传输功能。**



#### 发送

将上述两者创建完毕之后，先后启动两实例（接收端要先于发送端启动）

接收端代码如下：

````
receiver.accept();
````

发送端代码如下：

````
sender.send();
````

至此，即可等待文件传输完毕，即使中间断开，下次仍可继续传输。

#### 查看传输进度

在接收端的Receiver实例中，可通过currentReceivedSize()方法查看已接受文件的大小，通过totalFileSize()方法查看文件总大小。



测试代码如下：

````java
import cn.knightapple.ReceivePart.Receiver;
import cn.knightapple.SendPart.Sender;

import java.io.IOException;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Receiver receiver = new Receiver(12345);
        receiver.setFileNameTo("D:\\Source.test");
        Thread revd = new Thread(() -> {
            try {
                receiver.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Socket socket = new Socket("localhost", 12345);
        Sender sender = new Sender(socket, "D:\\Target.test");

        Thread send = new Thread(() -> {
            try {
                sender.send();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println(
            System.currentTimeMillis() 
            + "  " 
            + receiver.currentReceivedSize());
        Long start = System.currentTimeMillis();
        revd.start();
        send.start();
        while (revd.isAlive()) {
       	System.out.println(
            new Date(System.currentTimeMillis()) + "  " 
            + readableFileSize(receiver.currentReceivedSize())+" /"
            +readableFileSize(receiver.totalFileSize()));
            TimeUnit.MILLISECONDS.sleep(1000);
        }
        Long end = System.currentTimeMillis();
        System.out.println("DownLoad Rate:" 
        + readableFileSize(
        receiver.currentReceivedSize() 
        / new Date(end - start).getSeconds())
        + "/S");
    }

    public static String readableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#")
            .format(size / Math.pow(1024, digitGroups)) 
            + " " + units[digitGroups];
    }
}

````

#### 运行效果



## 许可