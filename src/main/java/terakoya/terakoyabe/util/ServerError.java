package terakoya.terakoyabe.util;

import lombok.Getter;
import lombok.Setter;

import java.io.*;

import static terakoya.terakoyabe.MyUtil.getCurrentTime;
import static terakoya.terakoyabe.util.Log.getPrintWriter;

@Getter
@Setter
public class ServerError {
    String message;

    public ServerError(Exception e){
        // TODO: 增加适用于linux的log
        e.printStackTrace();
        message = e.toString();
    }
}
