package cn.knightapple.SendPart;

import cn.knightapple.MessagePart.PreCheckMessageImp;
import cn.knightapple.MessagePart.RequestMessageImp;
import cn.knightapple.MessagePart.SliceMessageImp;
import cn.knightapple.tools.ConfigReader;
import cn.knightapple.tools.DESCryptography;
import cn.knightapple.tools.MessageResolve;

import java.io.*;
import java.net.Socket;

/**
 * 用来发送文件的发送端，必须指定的是文件的来源
 * 还有发生的目标的socket或其替代物
 * 可以指定是否加密传输
 *
 * @author kngihtapple
 * @version 1.1
 */
public class Sender {
    private Socket socket;
    private String fileName;
    private boolean encryption;

    /**
     * Sender的构造器，需要填入Socket实例和文件的来源
     *
     * @param socket       socket已连接对象
     * @param fileNameFrom 待传输的文件的来源
     */
    public Sender(Socket socket, String fileNameFrom) {
        this.encryption = false;
        this.socket = socket;
        this.fileName = fileNameFrom;
    }

    /**
     * Sender的构造器，只需填入Socket实例
     *
     * @param socket 连接所创建的socket
     */
    public Sender(Socket socket) {
        this.encryption = false;
        this.socket = socket;
    }

    /**
     * 设置欲发送文件的来源
     *
     * @param fileName 文件url
     * @exception FileNotFoundException 找不到所设置的文件
     */
    public void setFileName(String fileName) throws FileNotFoundException {
        if (!new File(fileName).exists()) {
            throw new FileNotFoundException("该文件不存在");
        } else {
            this.fileName = fileName;
        }
    }

    /**
     * 设置为加密模式
     */
    public void setEncrypt() {
        encryption = true;
    }

    /**
     * 设置为非加密模式
     */
    public void setUnEncrypt() {
        encryption = false;
    }

    /**
     * 开始发送文件，不需要保证接收端预先开启
     * 但是需要保证socket和文件来源均已设置
     *
     * @throws IOException 找不到需要发送的文件
     */
    public void send() throws IOException {
        //保证已经设置所需要的对象
        if (socket == null || fileName == null) {
            throw new IOException("未设置连接或文件名");
        }

        //初始化文件打开时所需要的读取器
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        OutputStream os = socket.getOutputStream();

        //设置缓冲写时可以有足够的buffer来一次性写入
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os), Integer.parseInt(ConfigReader.getPropertie("sliceMaxSize")) * 2);

        //初始化并设置设置第一个将发送的预检信息
        PreCheckMessageImp checkMessageImp = new PreCheckMessageImp();
        checkMessageImp.setFileNameFrom(fileName);
        checkMessageImp.setFileTotalSize(String.valueOf(new File(fileName).length()));
        checkMessageImp.setUpdateTime(String.valueOf(new File(fileName).lastModified()));

        //设置是否加密，用于通知接收端
        if (encryption) {
            checkMessageImp.encryp();
        }

        os.write(checkMessageImp.toString().getBytes());

        //初始化文件读取器
        FileReader fileReader = new FileReader(fileName);

        //接收回复，保证回复一定是ReqMessage类型的
        RequestMessageImp resvMessage = null;
        resvMessage = (RequestMessageImp) MessageResolve.getMessage(br);

        //当收到正确的回复，且未传输完毕前不断循环
        while (resvMessage != null && !resvMessage.isEnd()) {

            //从文件中读取所请求的文件片
            byte[] data = fileReader.read(Integer.parseInt(resvMessage.getNextBlockIndex()), Integer.parseInt(resvMessage.getMaxBlockSize()));
            //当加密时，设置加密
            if (encryption) {
                data = DESCryptography.DES_CBC_Encrypt(data);
            }

            //转换数据至char类型，以便接收端读取，因接收端读取存在缓冲，byte和char不能直接转换，故采用char更合适。
            char[] charData = new char[data.length];
            for (int i = 0; i < data.length; i++) {
                charData[i] = (char) data[i];
            }

            //准备文件片信息
            SliceMessageImp sliceMessageImp = new SliceMessageImp();
            sliceMessageImp.setBlockIndex(resvMessage.getNextBlockIndex());
            sliceMessageImp.setThisBlockSize(String.valueOf(data.length));

            //发送文件片信息
            os.write(sliceMessageImp.toString().getBytes());
            os.flush();

            //发送文件片
            bufferedWriter.write(charData);
            bufferedWriter.flush();

            //等待接收下一个的请求
            resvMessage = (RequestMessageImp) MessageResolve.getMessage(br);
        }
    }
//    public static void main(String[] args) {
//        try {
//            Socket socket = new Socket("localhost", 12345);
//            Sender sender = new Sender(socket, "E:\\学习资料\\txt专业类图书\\IT书籍\\1400多篇各类破解文章全中文.chm");
//            sender.setEncrypt();
//            sender.send();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}
