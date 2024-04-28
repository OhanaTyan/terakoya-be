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
        PrintWriter pw = getPrintWriter();
        pw.println("[Error]" + getCurrentTime());
        e.printStackTrace(pw);
        pw.println("\n");
        pw.flush();
        message = e.toString();
    }
}
