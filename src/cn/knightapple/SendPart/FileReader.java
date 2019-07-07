package cn.knightapple.SendPart;

import java.io.*;

public class FileReader {
    private RandomAccessFile randomAccessFile;

    FileReader(String fileName) throws FileNotFoundException {
        randomAccessFile = new RandomAccessFile(fileName, "rw");
    }

    public byte[] read(long index, long size) throws IOException {
        randomAccessFile.seek(index * size);
        if ((index + 1) * size >= randomAccessFile.length()) {
            size = randomAccessFile.length() - index * size;
        }
        byte[] data = new byte[(int) size];
        randomAccessFile.read(data, 0, (int) size);
        return data;
    }

    public static void main(String[] args) {

    }
}
