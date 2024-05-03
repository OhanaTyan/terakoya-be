package terakoya.terakoyabe.Service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import terakoya.terakoyabe.Service.BoardService;
import terakoya.terakoyabe.entity.Board;
import terakoya.terakoyabe.mapper.BoardMapper;

import java.util.List;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    private BoardMapper boardMapper;

    @Override
    public boolean isBoardExists(int boardid) {
        Board board = boardMapper.findBoardById(boardid);

        return board != null;
    }


    @Override
    public Board findBoardById(int boardid) {
        return boardMapper.findBoardById(boardid);
    }

    @Override
    public void createBoard(String name, String description) {
        boardMapper.create(name, description);
    }

    @Override
    public Board findBoardByName(String name) {
        return boardMapper.findBoardByName(name);
    }

    @Override
    public void updateBoard(int boardid, String name, String description) {
        boardMapper.updateBoard(boardid, name, description);
    }

    @Override
    public void deleteBoard(int boardid) {
        boardMapper.deleteBoard(boardid);
    }

    @Override
    public List<Board> listAllBoards() {
        return boardMapper.listAllBoards();
    }


}
