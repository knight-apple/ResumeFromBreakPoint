package cn.knightapple.ReceivePart;

import cn.knightapple.MessagePart.Message;
import cn.knightapple.MessagePart.PreCheckMessageImp;
import cn.knightapple.MessagePart.RequestMessageImp;
import cn.knightapple.MessagePart.SliceMessageImp;
import cn.knightapple.tools.ConfigReader;
import cn.knightapple.tools.DESCryptography;
import cn.knightapple.tools.MessageResolve;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class Receiver {
    private ServerSocket serverSocket;
    private String fileNameTo;
    private boolean toggle;
    private ConfigReader configReader;
    private TempFile tempFile;
    private boolean encryption;

    public Receiver(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        configReader = new ConfigReader();
        encryption = false;
    }

    public Receiver(Integer port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        configReader = new ConfigReader();
        encryption = false;
    }

    public Receiver() throws IOException {
        this.serverSocket = new ServerSocket();
        configReader = new ConfigReader();
        encryption = false;
    }

    public long currentReceivedSize() {
        if (tempFile == null) {
            return 0;
        } else {
            return Long.parseLong(tempFile.getCurrentIndex()) * Long.parseLong(ConfigReader.getPropertie("sliceMaxSize"));
        }
    }

    public long totalFileSize() {
        if (tempFile != null && tempFile.getTotalSize() != null) {
            return Long.parseLong(tempFile.getTotalSize());
        } else {
            return 0;
        }
    }

    public void accept() throws IOException {
        if (fileNameTo == null || serverSocket == null) {
            throw new IOException("未设置serverSocket或fileNameTo");
        }
        Socket socket = serverSocket.accept();
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()), Integer.parseInt(ConfigReader.getPropertie("sliceMaxSize")) * 2);
        OutputStream os = socket.getOutputStream();
        FileWriter fileWriter = null;
        tempFile = new TempFile();
        toggle = true;
        while (toggle) {
            Message message = MessageResolve.getMessage(br);
            switch (message.getContentType()) {
                case "preCheck":
                    PreCheckMessageImp preMessage = ((PreCheckMessageImp) message);
                    if (preMessage.isEncryp()) {
                        encryption = true;
                    } else {
                        encryption = false;
                    }
                    if (tempFile.getFileTo() == null) {
                        tempFile.setFileTo(fileNameTo);
                    }
                    if (tempFile.checkDirExists()) {
                        tempFile.readFromTemp();
                        if (!tempFile.isLatestFile(preMessage)) {
                            tempFile.processPreMessage(preMessage);
                            tempFile.resetFile();
                        }
                    } else {
                        throw new FileNotFoundException("该目录不存在");
                    }
                    fileWriter = new FileWriter(new File(fileNameTo),
                            Long.parseLong(((PreCheckMessageImp) message).getFileTotalSize()),
                            Integer.parseInt(ConfigReader.getPropertie("sliceMaxSize")));
                    sendRequest(tempFile, os);
                    break;
                case "slice":
                    if (fileWriter == null) {
                        break;
                    }
                    SliceMessageImp sliceMessageImp = (SliceMessageImp) message;
                    long index = Long.parseLong(sliceMessageImp.getBlockIndex());
                    int size = Integer.parseInt(sliceMessageImp.getThisBlockSize());
                    char[] charData = new char[size];
                    byte[] data = new byte[size];
                    char[] temp = new char[1];
                    for (int i = 0; i < size; i++) {
                        br.read(temp);
                        data[i] = (byte) temp[0];
                    }
                    if (encryption) {
                        data = DESCryptography.DES_CBC_Decrypt(data);
                    }

                    fileWriter.push(index, data);
                    tempFile.increIndex();
                    sendRequest(tempFile, os);
                    break;
                default:
                    break;
            }

        }
    }

    private void sendRequest(TempFile tempFile, OutputStream outputStream) throws IOException {
        RequestMessageImp requestMessageImp = new RequestMessageImp();
        requestMessageImp.setNextBlockIndex(tempFile.getCurrentIndex());
        requestMessageImp.setMaxBlockSize(ConfigReader.getPropertie("sliceMaxSize"));
        if ((Integer.parseInt(tempFile.getCurrentIndex())) * Integer.parseInt(ConfigReader.getPropertie("sliceMaxSize")) > Integer.parseInt(tempFile.getTotalSize())) {
//            requestMessageImp.setMaxBlockSize(String.valueOf(Integer.parseInt(tempFile.getTotalSize()) - Integer.parseInt(ConfigReader.getPropertie("sliceMaxSize")) * Integer.parseInt(tempFile.getCurrentIndex())));
            requestMessageImp.setEnd();
            toggle = false;
            tempFile.deleteTempFile();
        } else {
            requestMessageImp.setNotEnd();
        }
        outputStream.write(requestMessageImp.toString().getBytes());
    }

    public void setFileNameTo(String fileNameTo) {
        this.fileNameTo = fileNameTo;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public static void main(String[] args) {
        try {
            Receiver receiver = new Receiver(12345);
            receiver.setFileNameTo("E:\\my-study\\JavaStudy\\ResumeFromBreakPoint\\src\\cn\\knightapple\\chm.chm");
            receiver.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
