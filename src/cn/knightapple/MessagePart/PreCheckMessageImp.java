package cn.knightapple.MessagePart;
/**
 * @author kngihtapple
 * @version 1.1
 */
public class PreCheckMessageImp extends Message {
    private String fileNameFrom;
    //    private String fileNameTo;
    private String fileTotalSize;
    private String updateTime;
    private String encryption;

    public PreCheckMessageImp() {
        lines = "4";
        contentType = "preCheck";
        encryption = "unencryp";
    }

    @Override
    public String toString() {
        return "lines::" + getLines()
                + "\r\ncontentType::" + getContentType()
                + "\r\nfileNameFrom::" + fileNameFrom
//                + "\r\nfileNameTo::" + fileNameTo
                + "\r\nfileTotalSize::" + fileTotalSize
                + "\r\nupdateTime::" + updateTime
                + "\r\nencryption::" + encryption
                + "\r\n";
    }

    @Override
    public void processString(String str) {
        String[] strLine = str.split("\r\n");
        for (String one : strLine) {
            String[] temp = one.split("::");
            switch (temp[0]) {
                case "fileNameFrom":
                    setFileNameFrom(temp[1]);
                    break;
//                case "fileNameTo":
//                    setFileNameTo(temp[1]);
//                    break;
                case "fileTotalSize":
                    setFileTotalSize(temp[1]);
                    break;
                case "updateTime":
                    setUpdateTime(temp[1]);
                    break;
                case "encryption":
                    setEncryption(temp[1]);

                default:
                    break;
            }
        }
    }

    public void encryp() {
        encryption = "encryp";
    }
    public void unEncryp()
    {
        encryption = "unencryp";
    }
    public boolean isEncryp() {
        if (encryption.equals("encryp")) {
            return true;
        } else {
            return false;
        }
    }

    public void setEncryption(String encryption) {
        this.encryption = encryption;
    }

    public String getEncryption() {
        return encryption;
    }

    public void setFileNameFrom(String fileNameFrom) {
        this.fileNameFrom = fileNameFrom;
    }

//    public void setFileNameTo(String fileNameTo) {
//        this.fileNameTo = fileNameTo;
//    }

    public void setFileTotalSize(String fileTotalSize) {
        this.fileTotalSize = fileTotalSize;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getFileNameFrom() {
        return fileNameFrom;
    }

    public String getFileTotalSize() {
        return fileTotalSize;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public static void main(String[] args) {
        PreCheckMessageImp message = new PreCheckMessageImp();
        message.setFileNameFrom("c://aaa");
//        message.setFileNameTo("d://aaa");
        message.setUpdateTime("now");
        message.setFileTotalSize("500kb");
        String messageInfo = message.toString();
        Message message2 = new PreCheckMessageImp();
        message2.processString(messageInfo);
        System.out.println(message2);
    }
}
