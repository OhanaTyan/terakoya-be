package terakoya.terakoyabe.Service;

import terakoya.terakoyabe.entity.Board;

import java.util.List;

public interface BoardService {

    boolean isBoardExists(int boardid);

    Board findBoardById(int boardid);

    void createBoard(String name, String description);

    Board findBoardByName(String name);

    void updateBoard(int boardid, String name, String description);

    void deleteBoard(int boardid);

    List<Board> listAllBoards();
}
