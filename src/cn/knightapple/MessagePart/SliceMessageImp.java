package cn.knightapple.MessagePart;
/**
 * @author kngihtapple
 * @version 1.1
 */
public class SliceMessageImp extends Message {
    private String thisBlockSize;
    private String blockIndex;

    public SliceMessageImp() {
        lines = "2";
        contentType = "slice";
    }

    @Override
    public String toString() {
        return "lines::" + getLines()
                + "\r\ncontentType::" + getContentType()
                + "\r\nthisBlockSize::" + thisBlockSize
                + "\r\nblockIndex::" + blockIndex
                +"\r\n";
    }

    @Override
    public void processString(String str) {
        String[] strLine = str.split("\r\n");
        for (String one : strLine) {
            String[] temp = one.split("::");
            switch (temp[0]) {
                case "thisBlockSize":
                    setThisBlockSize(temp[1]);
                    break;
                case "blockIndex":
                    setBlockIndex(temp[1]);
                    break;
                default:
                    break;
            }
        }
    }

    public void setBlockIndex(String blockIndex) {
        this.blockIndex = blockIndex;
    }

    public void setThisBlockSize(String thisBlockSize) {
        this.thisBlockSize = thisBlockSize;
    }

    public String getBlockIndex() {
        while (blockIndex==null){};
        return blockIndex;
    }

    public String getThisBlockSize() {
        return thisBlockSize;
    }

    public static void main(String[] args) {
        SliceMessageImp message = new SliceMessageImp();
        message.setThisBlockSize("500");
        message.setBlockIndex("500");
        String messageInfo = message.toString();
        Message message2 = new SliceMessageImp();
        message2.processString(messageInfo);
        System.out.println(message2);
    }
}
