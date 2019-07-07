package cn.knightapple;

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
        receiver.setFileNameTo("E:\\奖学金资料\\2017-2018学年综测加分-团学.rar");
        Thread revd = new Thread(() -> {
            try {
                receiver.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Socket socket = new Socket("localhost", 12345);
        Sender sender = new Sender(socket, "E:\\奖学金资料\\2017-2018学年综测加分-团学20180918.rar");
        sender.setEncrypt();
        Thread send = new Thread(() -> {
            try {
                sender.send();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println(System.currentTimeMillis() + "  " + receiver.currentReceivedSize());
        Long start = System.currentTimeMillis();
        revd.start();
        send.start();
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
