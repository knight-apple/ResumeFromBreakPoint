package cn.knightapple;

import cn.knightapple.ReceivePart.Receiver;
import cn.knightapple.SendPart.Sender;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(12345);
        Socket sendSocket = new Socket("localhost", 12345);

        Receiver receiver = new Receiver(serverSocket.accept());


        Thread revd = new Thread(() -> {
            try {
                receiver.setFileTo("E:\\奖学金资料\\2017-2018学年综测加分-团学.rar");
                receiver.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Sender sender = new Sender(sendSocket, "E:\\奖学金资料\\2017-2018学年综测加分-团学20180918.rar");
        sender.setEncrypt();
        Thread send = new Thread(() -> {
            try {
                sender.send();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        System.out.println(System.currentTimeMillis() + "  " + receiver.currentReceivedSize());
        Long start = System.currentTimeMillis();
        send.start();
        revd.start();
        while (revd.isAlive()) {
            System.out.println(
                    new Date(System.currentTimeMillis()-start).getSeconds()
                    + "  " + readableFileSize(receiver.currentReceivedSize())
                    +" / "+readableFileSize(receiver.totalFileSize()));
            TimeUnit.MILLISECONDS.sleep(1000);
        }
        Long end = System.currentTimeMillis();
        System.out.println("DownLoad Rate:"
                + readableFileSize(receiver.currentReceivedSize() / new Date(end - start).getSeconds()) + "/S");
    }

    public static String readableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
