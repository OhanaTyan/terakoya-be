package terakoya.terakoyabe.Service;

import terakoya.terakoyabe.entity.Board;

public interface BoardService {

    boolean isBoardExists(int id);

    Board getBoardById(int id);
}
