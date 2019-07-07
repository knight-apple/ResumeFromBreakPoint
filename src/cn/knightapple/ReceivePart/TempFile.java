package cn.knightapple.ReceivePart;

import cn.knightapple.MessagePart.Message;
import cn.knightapple.MessagePart.PreCheckMessageImp;

import java.io.*;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Logger;

public class TempFile {
    //文件来源
    private String fileFrom;
    //文件目标
    private String fileTo;
    //文件总大小
    private String totalSize;
    //本地文件最新的修改时间，防本地修改
    private String dateModified;
    //远程文件的更新时间，防文件源修改
    private String updateTime;
    //最开始的文件更新时间
//    private String updateTimeOnOpen;
    //当前下标
    private String currentIndex;
    private static final Logger LOG = Logger.getLogger(TempFile.class.getName());

    public TempFile()
    {
        currentIndex = "0";
    }
    @Override
    public String toString() {
        return "fileFrom::" + fileFrom
                + "\r\nfileTo::" + fileTo
                + "\r\ntotalSize::" + totalSize
                + "\r\ndateModified::" + dateModified
                + "\r\ncurrentIndex::" + currentIndex
                + "\r\nupdateTime::" + updateTime;
    }

    //判断文件是否被更新过,目前尚有问题,可能是打开文件即被认为是新修改的
    public boolean isLatestFile(PreCheckMessageImp preCheckMessageImp) {
        if (preCheckMessageImp.getFileNameFrom().equals(fileFrom)
                && preCheckMessageImp.getFileTotalSize().equals(totalSize)
                && preCheckMessageImp.getUpdateTime().equals(updateTime)
//                && dateModified != "null"
//                && new File(fileTo).lastModified() < Long.parseLong(dateModified)
        ){
            return true;
        } else{
            return false;
        }
    }

    //检查该文件所在目录是否存在
    public boolean checkDirExists() {
        File file = new File(fileTo).getParentFile();
        return file.exists();
    }

    //获取临时目录
    private File getTempFile() {
        File temp = new File(fileTo);

        String fileName = temp.getName();
        return new File(new File(fileTo).getParent() + File.separator + fileName + ".temp");
    }

    //重置文件下载进度
    public void resetFile() throws IOException {
        File tempFile = getTempFile();
        OutputStream os = new FileOutputStream(tempFile);
        this.currentIndex = "0";
        this.dateModified = String.valueOf(System.currentTimeMillis());
        os.write(this.toString().getBytes());
        os.close();
    }

    //更新至下一个索引
    public void increIndex() throws IOException {
        File tempFile = getTempFile();
        OutputStream os = new FileOutputStream(tempFile);
        currentIndex = String.valueOf(Integer.valueOf(currentIndex) + 1);
        dateModified = String.valueOf(System.currentTimeMillis());
        os.write(this.toString().getBytes());
        os.close();
    }

    //固化内存至temp文件
    private void store() throws IOException {
        dateModified = String.valueOf(System.currentTimeMillis());
        File tempFile = getTempFile();
        OutputStream os = new FileOutputStream(tempFile);
        os.write(this.toString().getBytes());
        os.close();
    }

    //将PreMessage的内容写入tempFile中
    public void processPreMessage(PreCheckMessageImp preCheckMessageImp) throws IOException {
        fileFrom = preCheckMessageImp.getFileNameFrom();
        totalSize = preCheckMessageImp.getFileTotalSize();
        updateTime = preCheckMessageImp.getUpdateTime();
        currentIndex = "0";
//        this.store();
    }

    //从临时文件中读取至内存
    public void readFromTemp() {
        File tempFile = getTempFile();
        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileInputStream(tempFile));
        } catch (FileNotFoundException e) {
            LOG.warning("未创建临时文件");
        }

        while (scanner != null && scanner.hasNext()) {
            String[] tempStrPair = scanner.nextLine().split("::");
            switch (tempStrPair[0]) {
                case "fileTo":
                    this.fileTo = tempStrPair[1];
                    break;
                case "fileFrom":
                    this.fileFrom = tempStrPair[1];
                    break;
                case "totalSize":
                    this.totalSize = tempStrPair[1];
                    break;
                case "dateModified":
                    this.dateModified = tempStrPair[1];
                    break;
                case "currentIndex":
                    this.currentIndex = tempStrPair[1];
                    break;
                case "updateTime":
                    this.updateTime = tempStrPair[1];
                    break;
                default:
                    break;
            }
        }
        if (scanner != null) {
            scanner.close();
        }
    }

    public void deleteTempFile() {
        File temp = getTempFile();
        if (!temp.delete()) {
            LOG.warning("delete temp file fail");
        }

    }

    //setter
    public void setCurrentIndex(String currentIndex) {
        this.currentIndex = currentIndex;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public void setFileFrom(String fileFrom) {
        this.fileFrom = fileFrom;
    }

    public void setFileTo(String fileTo) {
        this.fileTo = fileTo;
    }

    public void setTotalSize(String totalSize) {
        this.totalSize = totalSize;
    }

    //getter
    public String getCurrentIndex() {
        return currentIndex;
    }

    public String getDateModified() {
        return dateModified;
    }

    public String getFileFrom() {
        return fileFrom;
    }

    public String getFileTo() {
        return fileTo;
    }

    public String getTotalSize() {
        return totalSize;
    }

    public static void main(String[] args) throws IOException {
        TempFile tempFile = new TempFile();
        PreCheckMessageImp preCheckMessageImp = new PreCheckMessageImp();
        preCheckMessageImp.setFileNameFrom("D:\\my-study\\JavaStudy\\ResumeFromBreakPoint\\src\\cn\\knightapple\\MessagePart\\Message.java");
        preCheckMessageImp.setFileTotalSize(
                String.valueOf(
                        new File(
                                "D:\\my-study\\JavaStudy\\ResumeFromBreakPoint\\src\\cn\\knightapple\\MessagePart\\Message.java")
                                .length()));
        preCheckMessageImp.setUpdateTime(
                String.valueOf(new File(
                        "D:\\my-study\\JavaStudy\\ResumeFromBreakPoint\\src\\cn\\knightapple\\MessagePart\\Message.java")
                        .lastModified()));

        tempFile.setCurrentIndex("0");
        tempFile.setDateModified(String.valueOf(new File(
                "D:\\my-study\\JavaStudy\\ResumeFromBreakPoint\\src\\cn\\knightapple\\MessagePart\\Message.java")
                .lastModified()));
        tempFile.setFileFrom("D:\\my-study\\JavaStudy\\ResumeFromBreakPoint\\src\\cn\\knightapple\\MessagePart\\Message.java");
        tempFile.setFileTo("D:\\my-study\\JavaStudy\\ResumeFromBreakPoint\\src\\cn\\knightapple\\mmmmm.properties");
        tempFile.setTotalSize(String.valueOf(
                new File(
                        "D:\\my-study\\JavaStudy\\ResumeFromBreakPoint\\src\\cn\\knightapple\\MessagePart\\Message.java")
                        .length()));
        tempFile.checkDirExists();
        tempFile.readFromTemp();
        System.out.println(tempFile.isLatestFile(preCheckMessageImp));
        tempFile.resetFile();
        tempFile.store();
        tempFile.increIndex();
        tempFile.deleteTempFile();

    }
}
