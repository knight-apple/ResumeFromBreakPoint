package cn.knightapple.ReceivePart;

import java.io.*;

public class FileWriter {
    private RandomAccessFile target;
    private Long fileSize;
    private Integer sliceSize;
    public FileWriter(File file, Long fileSize, Integer sliceSize) throws IOException {
        this.target = new RandomAccessFile(file,"rw");
        this.fileSize = fileSize;
        this.sliceSize = sliceSize;
        target.setLength(fileSize);
    }
    public void push(long index,byte[] data) throws IOException {
        target.seek(index*sliceSize);
        target.write(data);
    }
    public void close() throws IOException {
        target.close();
    }

    public static void main(String[] args) throws IOException {
        File file = new File("D:\\my-study\\JavaStudy\\ResumeFromBreakPoint\\src\\cn\\knightapple\\test.properties");
        FileWriter fileWriter = new FileWriter(file, (long) 20,2);
        for(int i=10;i<20;i++)
        {
            fileWriter.push(i,(""+i).getBytes());
        }
        fileWriter.close();
    }
}
