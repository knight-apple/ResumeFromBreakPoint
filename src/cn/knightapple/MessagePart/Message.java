package cn.knightapple.MessagePart;

public abstract class Message {
    /**
     * lines:除lines和contentType的数量
     * contentType:Message的类型
     * */
    protected String lines;
    protected String contentType;

    public Message(){
        contentType="noSet";
        lines="2";
    }
    @Override
    public abstract String toString();
    public abstract void processString(String str);

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
