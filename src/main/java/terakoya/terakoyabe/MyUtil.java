package terakoya.terakoyabe;


public class MyUtil {
    public static int getCurrentTime(){
        return  (int) (System.currentTimeMillis() / 1000);
    }

    // 获取随机数字
    public static int getRandomValue(){
        return (int) (Math.random() * 1000000000);
    }


}
