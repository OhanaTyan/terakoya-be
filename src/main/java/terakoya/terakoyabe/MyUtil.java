package terakoya.terakoyabe;


import terakoya.terakoyabe.util.Log;

public class MyUtil {
    public static int getCurrentTime(){
        return  (int) (System.currentTimeMillis() / 1000);
    }

    // 获取随机数字
    public static int getRandomValue(){
        return (int) (Math.random() * 1000000000);
    }

    // 通过页码和页面大小获取下标
    public static int getOffset(int page, int size){
        Log.info("MyUtil::getOffset\npage:"+page+"\nsize:"+size);
        return (page-1) * size;
    }

}
