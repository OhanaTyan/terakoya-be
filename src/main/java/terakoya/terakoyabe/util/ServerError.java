package terakoya.terakoyabe.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerError {
    String message;

    public ServerError(Exception e){
        e.printStackTrace();
        message = e.toString();
    }
}
