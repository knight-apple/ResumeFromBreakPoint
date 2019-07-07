package cn.knightapple.ReceivePart;

import cn.knightapple.MessagePart.Message;
import cn.knightapple.MessagePart.PreCheckMessageImp;
import cn.knightapple.MessagePart.RequestMessageImp;
import cn.knightapple.MessagePart.SliceMessageImp;
import cn.knightapple.tools.ConfigReader;
import cn.knightapple.tools.DESCryptography;
import cn.knightapple.tools.MessageResolve;

import java.io.*;
import java.net.Socket;

/**
 * 用来接收文件的接收端，必须指定的是文件的存储地址
 * 还有发生的目标的socket或其替代物
 * 可以指定是否加密传输
 * @author kngihtapple
 * @version 1.1
 */
public class Receiver {
    private Socket socket;
    private String fileNameTo;
    private boolean toggle;
    private TempFile tempFile;
    private boolean encryption;

    /**
     * 接收端构造器，仅接收一个socket
     * 需要使用setFileTo方法指定文件存放位置
     * @param socket 已连接的Socket
     */
    public Receiver(Socket socket) {
        this.socket = socket;
        encryption = false;
    }

    /**
     *接收端构造器，可以同时接收socket和文件名
     * 当socket连接后，可直接使用接收方法
     * @param socket 已连接的Socket
     * @param fileTo 目标文件的路径
     */
    public Receiver(Socket socket, String fileTo) {
        this.socket = socket;
        this.fileNameTo = fileTo;
        encryption = false;
    }

    /**
     * 设置存放文件的位置
     * @param fileTo 文件位置的路径
     */
    public void setFileTo(String fileTo) {
        this.fileNameTo = fileTo;
    }

    /**
     * 当前已接收的文件的大小
     * @return 返回long类型的已接收文件的大小
     */
    public long currentReceivedSize() {
        if (tempFile == null) {
            return 0;
        } else {
            return Long.parseLong(tempFile.getCurrentIndex()) * Long.parseLong(ConfigReader.getPropertie("sliceMaxSize"));
        }
    }

    /**
     * 获取总文件大小
     * @return 返回long类型的待接收文件总大小
     */
    public long totalFileSize() {
        if (tempFile != null && tempFile.getTotalSize() != null) {
            return Long.parseLong(tempFile.getTotalSize());
        } else {
            return 0;
        }
    }

    /**
     * 向发送端发送文件片请求，请求下一个文件片的相关信息
     * @param tempFile 临时文件类型，存放了临时文件的对象内容和相关方法
     * @param outputStream 输出流，已连接发送端的socket所产生的输出流
     * @throws IOException IO错误，当发送请求失败时报错，一般为socket未连接或断开
     */
    private void sendRequest(TempFile tempFile, OutputStream outputStream) throws IOException {

        //设置请求Message相关信息
        RequestMessageImp requestMessageImp = new RequestMessageImp();
        requestMessageImp.setNextBlockIndex(tempFile.getCurrentIndex());
        requestMessageImp.setMaxBlockSize(ConfigReader.getPropertie("sliceMaxSize"));

        //当文件未完成传输时继续传输
        //当文件完成传输时，发送结束信息，并关闭循环
        if ((Integer.parseInt(tempFile.getCurrentIndex())) * Integer.parseInt(ConfigReader.getPropertie("sliceMaxSize")) > Integer.parseInt(tempFile.getTotalSize())) {
            requestMessageImp.setEnd();
            toggle = false;
            tempFile.deleteTempFile();
        } else {
            requestMessageImp.setNotEnd();
        }

        //发送请求信息
        outputStream.write(requestMessageImp.toString().getBytes());
    }

    /**
     * 接收端，接收行为，执行该方法将会等待发送端发送预检Message
     * 然后不断发送文件片请求，直至读取完成
     * @throws IOException 文件读取错误，信息发送错误等
     */
    public void accept() throws IOException {

        //判断是否已设置Socket和文件存放位置
        if (fileNameTo == null || socket == null) {
            throw new IOException("未设置Socket或fileNameTo");
        }

        //初始化传输对象，设置足够大的读取Buffer
        //以保证一次性能读取完一个文件片，减少读取和等待次数
        //*2是为了为加密产生的空间提供一定的冗余
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()), Integer.parseInt(ConfigReader.getPropertie("sliceMaxSize")) * 2);
        OutputStream os = socket.getOutputStream();
        FileWriter fileWriter = null;

        //初始化临时文件
        tempFile = new TempFile();

        //开启预设循环
        toggle = true;
        while (toggle) {

            //接收Message
            Message message = MessageResolve.getMessage(br);

            //根据Message的类型来分别处理
            //分别可能是预检Message或文件片Message
            switch (message.getContentType()) {

                //当Message是预检请求时
                case "preCheck":
                    PreCheckMessageImp preMessage = ((PreCheckMessageImp) message);
                    //判断是否要求加密
                    if (preMessage.isEncryp()) {
                        encryption = true;
                    } else {
                        encryption = false;
                    }

                    //写文件对象初始化
                    fileWriter = new FileWriter(new File(fileNameTo),
                            Long.parseLong(((PreCheckMessageImp) message).getFileTotalSize()),
                            Integer.parseInt(ConfigReader.getPropertie("sliceMaxSize")));

                    //判断临时文件是否已设置目的，要放在检查上面
                    //因为检查依赖于此处设置的文件目的
                    if (tempFile.getFileTo() == null) {
                        tempFile.setFileTo(fileNameTo);
                    }

                    //临时文件检查机制，当已存在时，直接读取即可
                    // 如果目录不存在，则报错，提示用户创建存储目录
                    if (tempFile.checkDirExists()) {
                        tempFile.readFromTemp();

                        //如果不是最新的文件，说明被篡改过，根据收到的信息重置临时文件
                        if (!tempFile.isLatestFile(preMessage)) {
                            tempFile.processPreMessage(preMessage);
                            tempFile.resetFile();
                        }
                    } else {
                        throw new FileNotFoundException("该目录不存在");
                    }

                    //预检完成，发送文件请求
                    sendRequest(tempFile, os);
                    break;

                //当请求是文件片Message时
                case "slice":

                    //检查是否已设置文件写入器
                    if (fileWriter == null) {
                        break;
                    }

                    //读取文件片信息
                    SliceMessageImp sliceMessageImp = (SliceMessageImp) message;
                    long index = Long.parseLong(sliceMessageImp.getBlockIndex());
                    int size = Integer.parseInt(sliceMessageImp.getThisBlockSize());

                    //从char[]中转码至byte[]
                    //需要一个个读取，否则在网速过慢时
                    // 可能会导致只读取部分信息，剩余信息被当作Message而报错
                    byte[] data = new byte[size];
                    char[] temp = new char[1];
                    for (int i = 0; i < size; i++) {
                        br.read(temp);
                        data[i] = (byte) temp[0];
                    }

                    //是否为加密模式，若加密则解密
                    if (encryption) {
                        data = DESCryptography.DES_CBC_Decrypt(data);
                    }

                    //使用文件写入器来写入文件片
                    fileWriter.push(index, data);

                    //文件片索引+1
                    tempFile.increIndex();

                    //发送下一个文件片的请求
                    sendRequest(tempFile, os);
                    break;
                default:
                    break;
            }
        }
    }



    public static void main(String[] args) {
        try {
            Receiver receiver = new Receiver(new Socket());
            receiver.setFileTo("E:\\my-study\\JavaStudy\\ResumeFromBreakPoint\\src\\cn\\knightapple\\chm.chm");
            receiver.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
