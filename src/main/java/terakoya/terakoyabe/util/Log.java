package terakoya.terakoyabe.util;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Log {
    private static final String LOG_FILE = "/home/ubuntu/terakoya-be-log.txt";


    // 获取进程号
    private static long getPid(){
        // https://blog.csdn.net/mingtianhaiyouwo/article/details/75132106
        return ManagementFactory.getRuntimeMXBean().getPid();
    }

    static{
            // terakoya 的 ascii 画
/*
  _______             _
 |__   __|           | |
    | | ___ _ __ __ _| | _____  _   _  __ _
    | |/ _ \ '__/ _` | |/ / _ \| | | |/ _` |
    | |  __/ | | (_| |   < (_) | |_| | (_| |
    |_|\___|_|  \__,_|_|\_\___/ \__, |\__,_|
                                 __/ |
                                |___/
 */
        info("\n  _______             _                      \n |__   __|           | |                     \n    | | ___ _ __ __ _| | _____  _   _  __ _  \n    | |/ _ \\ '__/ _` | |/ / _ \\| | | |/ _` | \n    | |  __/ | | (_| |   < (_) | |_| | (_| | \n    |_|\\___|_|  \\__,_|_|\\_\\___/ \\__, |\\__,_| \n                                 __/ |       \n                                |___/        \n");
        info("运行成功");
        info("当前进程号为pid=" + getPid());
    }

    public static FileWriter getFileWriter() {
        try {
            return new FileWriter(LOG_FILE, true);
        } catch (FileNotFoundException e) {
            throw new RuntimeException();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getCurrentTimeString(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.now();
        return dateTime.format(formatter);
    }

    public static void info(String s){
        try {
            FileWriter writer = getFileWriter();
            writer.write("[Info]" + getCurrentTimeString() + "\n");
            writer.write(s + "\n");
            writer.flush();
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void error(String s){
        try {
            FileWriter writer = getFileWriter();
            writer.write("[Error]" + getCurrentTimeString() + "\n");
            writer.write(s + "\n");
            writer.flush();
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void error(Exception e){
            // https://blog.csdn.net/godha/article/details/13066095
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String msg = sw.toString();
            error(msg);
    }
}
