package cn.knightapple.tools;

import cn.knightapple.MessagePart.Message;
import cn.knightapple.MessagePart.PreCheckMessageImp;
import cn.knightapple.MessagePart.RequestMessageImp;
import cn.knightapple.MessagePart.SliceMessageImp;

import java.io.*;

public class MessageResolve {
    public static Message getMessage(BufferedReader br) throws IOException {
        String data = "";
        String line = br.readLine();
        String contentType = br.readLine();
        data += line;
        data += contentType;
        String[] linePair = line.split("::");
        String[] typePair = contentType.split("::");
        if (linePair[0].equals("lines")) {
            int times = Integer.valueOf(linePair[1]);
            for (int i = 0; i < times; i++) {
                data += "\r\n"+br.readLine();
            }
        }
        Message message = null;
        if (typePair[0].equals("contentType")) {
            switch (typePair[1]) {
                case "preCheck":
                    message = new PreCheckMessageImp();
                    break;
                case "request":
                    message = new RequestMessageImp();
                    break;
                case "slice":
                    message = new SliceMessageImp();
                    break;
                default:
                    break;
            }
            message.processString(data);
            return message;
        }
        return null;
    }
}
