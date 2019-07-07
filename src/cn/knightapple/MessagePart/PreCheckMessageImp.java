package cn.knightapple.MessagePart;

public class PreCheckMessageImp extends Message {
    private String fileNameFrom;
    //    private String fileNameTo;
    private String fileTotalSize;
    private String updateTime;

    public PreCheckMessageImp() {
        lines = "3";
        contentType = "preCheck";
    }

    @Override
    public String toString() {
        return "lines::" + getLines()
                + "\r\ncontentType::" + getContentType()
                + "\r\nfileNameFrom::" + fileNameFrom
//                + "\r\nfileNameTo::" + fileNameTo
                + "\r\nfileTotalSize::" + fileTotalSize
                + "\r\nupdateTime::" + updateTime
                +"\r\n";
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
                default:
                    break;
            }
        }
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
