package cn.knightapple.MessagePart;

/**
 * @author kngihtapple
 * @version 1.1
 */
public abstract class Message {
    /**
     * @value line 除了lines和contentType的数量
     * @value contentType Message的类型
     * */
    protected String lines;
    protected String contentType;

    /**
     * Message的构造函数
     */
    public Message(){
        contentType="noSet";
        lines="2";
    }
    @Override
    public abstract String toString();

    /**
     * @param str 待处理的Message数据，将会格式化为对象
     */
    public abstract void processString(String str);

    /**
     * @return Message的类型
     */
    public String getContentType() {
        return contentType;
    }

    public String getLines() {
        return lines;
    }

//    public void setContentType(String contentType) {
//        this.contentType = contentType;
//    }

//    public void setLines(String lines) {
//        this.lines = lines;
//    }
//    public void setLines(Integer lines)
//    {
//        this.lines = String.valueOf(lines);
//    }
}
