package terakoya.terakoyabe.util;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
public class ServerError {
    String message;

    // log 文件地址
    private static String LOG_FILE = "/home/ubuntu/terakoya-bg-log.txt";

    private static PrintWriter getPrintWriter(){
        try {
            return new PrintWriter(LOG_FILE);
        } catch (FileNotFoundException e){
            throw new RuntimeException();
        }
    }

    private static String getCurrentTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }

    public ServerError(Exception e){
        // TODO: 增加适用于linux的log
//        e.printStackTrace();
        PrintWriter pw = getPrintWriter();
        pw.println(getCurrentTime());
        e.printStackTrace(pw);
        pw.println('\n');
        pw.flush();
        pw.close();
        message = e.toString();
    }
}
