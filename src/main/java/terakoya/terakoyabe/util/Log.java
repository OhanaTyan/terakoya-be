package terakoya.terakoyabe.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Log {
    private static final String LOG_FILE = "/home/ubuntu/terakoya-be-log.txt";

    private static PrintWriter writer;
    public static PrintWriter getPrintWriter(){
        try {
            if (writer == null) {
                writer = new PrintWriter(LOG_FILE);
            }
            return writer;
        } catch (FileNotFoundException e){
            throw new RuntimeException();
        }
    }
    public static String getCurrentTime(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.now();
        return dateTime.format(formatter);
    }

    public static void info(String s){
        PrintWriter writer = getPrintWriter();
        writer.println("[info]" + getCurrentTime());
        writer.println(s);
        writer.println('\n');
        writer.flush();
    }

    public static void error(String s){
        PrintWriter writer = getPrintWriter();
        writer.println("[Error]" + getCurrentTime());
        writer.println(s);
        writer.println('\n');
        writer.flush();
    }
}
