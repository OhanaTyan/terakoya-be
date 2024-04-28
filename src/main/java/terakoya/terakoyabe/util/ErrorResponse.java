package terakoya.terakoyabe.util;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ErrorResponse {
    String message;

    public ErrorResponse(String msg){
        message = msg;
    }

    // TODO: 添加适配于 linux 系统的日志


}
