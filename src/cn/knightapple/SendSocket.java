package cn.knightapple;

import cn.knightapple.SendPart.Sender;

import java.io.IOException;
import java.net.Socket;

public class SendSocket {
private Sender sender;
    public SendSocket(Socket socket, String fileNameFrom) {
        sender = new Sender(socket,fileNameFrom);
    }
    public SendSocket(String host, Integer point,String fileNameFrom) {
        try {
            sender = new Sender(host,point,fileNameFrom);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void send()
    {
        try {
            sender.send();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}