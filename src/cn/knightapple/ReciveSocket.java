package cn.knightapple;

import cn.knightapple.ReceivePart.Receiver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.text.DecimalFormat;

public class ReciveSocket {
    private Receiver receiver;

    public ReciveSocket(ServerSocket serverSocket) {
        receiver = new Receiver(serverSocket);
    }

    public ReciveSocket(Integer port) {
        try {
            receiver = new Receiver(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ReciveSocket() {
        try {
            receiver = new Receiver();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bind(SocketAddress endPoint) throws IOException {
        receiver.bind(endPoint);
    }

    public void bind(SocketAddress endPoint, int backLog) throws IOException {
        receiver.bind(endPoint, backLog);
    }

    public long currentReceivedSize() {
        return receiver.currentReceivedSize();
    }

    public void accept() {
        try {
            receiver.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setFileNameTo(String fileNameTo) {
        receiver.setFileNameTo(fileNameTo);
    }

    public void setServerSocket(ServerSocket serverSocket) {
        receiver.setServerSocket(serverSocket);
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
