package cn.knightapple.SendPart;

import cn.knightapple.MessagePart.PreCheckMessageImp;
import cn.knightapple.MessagePart.RequestMessageImp;
import cn.knightapple.MessagePart.SliceMessageImp;
import cn.knightapple.tools.ConfigReader;
import cn.knightapple.tools.DESCryptography;
import cn.knightapple.tools.MessageResolve;

import java.io.*;
import java.net.Socket;

public class Sender {
    private Socket socket;
    private String fileName;
    private boolean encryption;
    //    private String totalSize;
//    private String updateTime;
    public Sender(Socket socket, String fileNameFrom) {
//        toggle = true;
        this.encryption = false;
        this.socket = socket;
        this.fileName = fileNameFrom;
    }

    public Sender(String host, int port, String fileNameFrom) throws IOException {
        this.encryption =false;
        socket = new Socket(host, port);
        this.fileName = fileNameFrom;
    }
    public void setEncrypt()
    {
        encryption =true;
    }
    public void setUnEncrypt()
    {
        encryption = false;
    }
    public void send() throws IOException, InterruptedException {
        if (socket == null || fileName == null) {
            throw new IOException("未设置连接或文件名");
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        OutputStream os = socket.getOutputStream();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os),Integer.parseInt(ConfigReader.getPropertie("sliceMaxSize"))*2);
        PreCheckMessageImp checkMessageImp = new PreCheckMessageImp();
        checkMessageImp.encryp();
        cn.knightapple.SendPart.FileReader fileReader = new FileReader(fileName);
        checkMessageImp.setFileNameFrom(fileName);
        checkMessageImp.setFileTotalSize(String.valueOf(new File(fileName).length()));
        checkMessageImp.setUpdateTime(String.valueOf(new File(fileName).lastModified()));
        RequestMessageImp resvMessage = null;
        os.write(checkMessageImp.toString().getBytes());
        resvMessage = (RequestMessageImp) MessageResolve.getMessage(br);
        while (resvMessage != null && !resvMessage.isEnd()) {
            SliceMessageImp sliceMessageImp = new SliceMessageImp();
            byte[] data = fileReader.read(Integer.parseInt(resvMessage.getNextBlockIndex()), Integer.parseInt(resvMessage.getMaxBlockSize()));
            if(encryption)
            {
                data = DESCryptography.DES_CBC_Encrypt(data);
            }

            char[] charData = new char[data.length];
            for(int i=0;i<data.length;i++)
            {
                charData[i] = (char)data[i];
            }
            sliceMessageImp.setBlockIndex(resvMessage.getNextBlockIndex());
            sliceMessageImp.setThisBlockSize(String.valueOf(data.length));
            os.write(sliceMessageImp.toString().getBytes());
            os.flush();
            bufferedWriter.write(charData);
            bufferedWriter.flush();
            resvMessage = (RequestMessageImp) MessageResolve.getMessage(br);
        }
        socket.close();
    }

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 12345);
            Sender sender = new Sender(socket, "E:\\学习资料\\txt专业类图书\\IT书籍\\1400多篇各类破解文章全中文.chm");
            sender.setEncrypt();
            sender.send();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
