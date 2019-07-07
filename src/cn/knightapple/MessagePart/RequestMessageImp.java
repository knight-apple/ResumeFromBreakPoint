package cn.knightapple.MessagePart;
/**
 * @author kngihtapple
 * @version 1.1
 */
public class RequestMessageImp extends Message {
    private String nextBlockIndex;
    private String maxBlockSize;
    private String isEnd;

    public RequestMessageImp() {
        lines = "3";
        contentType = "request";
        isEnd="noEnd";
    }

    @Override
    public String toString() {
        return "lines::" + lines
                + "\r\ncontentType::" + contentType
                + "\r\nnextBlockIndex::" + nextBlockIndex
                + "\r\nmaxBlockSize::" + maxBlockSize
                + "\r\nisEnd::" + isEnd
                +"\r\n";
    }

    @Override
    public void processString(String str) {
        String[] strLine = str.split("\r\n");
        for (String one : strLine) {
            String[] temp = one.split("::");
            switch (temp[0]) {
                case "nextBlockIndex":
                    nextBlockIndex = temp[1];
                    break;
                case "maxBlockSize":
                    maxBlockSize = temp[1];
                    break;
                case "isEnd":
                    isEnd = temp[1];
                    break;
                default:
                    break;
            }
        }
    }

    public void setMaxBlockSize(String maxBlockSize) {
        this.maxBlockSize = maxBlockSize;
    }

    public void setNextBlockIndex(String nextBlockIndex) {
        this.nextBlockIndex = nextBlockIndex;
    }

    public void setEnd() {
        this.isEnd = "end";
    }
    public void setNotEnd()
    {
        this.isEnd="noEnd";
    }
    public boolean isEnd()
    {
        if(isEnd.equals("end")) {
            return true;
        } else
        {
            return false;
        }
    }
    public String getMaxBlockSize() {
        return maxBlockSize;
    }

    public String getNextBlockIndex() {
        return nextBlockIndex;
    }

    public static void main(String[] args) {
        RequestMessageImp message = new RequestMessageImp();
        message.setMaxBlockSize("512");
        message.setNextBlockIndex("500");
        String messageInfo = message.toString();
        Message message2 = new RequestMessageImp();
        message2.processString(messageInfo);
        System.out.println(message2);
    }
}
