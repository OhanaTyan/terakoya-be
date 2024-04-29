package terakoya.terakoyabe.util;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Log {
    private static final String LOG_FILE = "/home/ubuntu/terakoya-be-log.txt";

    private static FileWriter writer;
    private static boolean inited = false;

    // 获取进程号
    private static long getPid(){
        // https://blog.csdn.net/mingtianhaiyouwo/article/details/75132106
        return ManagementFactory.getRuntimeMXBean().getPid();
    }

    public static void  init(){
        if (!inited) {
            inited = true;
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
            // TODO:写入进程号和当前时间
            info("运行成功");
            info("当前进程号为pid=" + getPid());
            info("当前时间为" + getCurrentTimeString());
        }

    }

    public static FileWriter getFileWriter() {
        try {
            if (!inited){
                inited = true;
                init();
            }
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
            writer.write("[info]" + getCurrentTimeString() + "\n");
            writer.write(s + "\n");
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
