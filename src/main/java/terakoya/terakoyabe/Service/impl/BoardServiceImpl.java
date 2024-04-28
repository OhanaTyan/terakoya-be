package terakoya.terakoyabe.Service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import terakoya.terakoyabe.Service.BoardService;
import terakoya.terakoyabe.entity.Board;
import terakoya.terakoyabe.mapper.BoardMapper;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    private BoardMapper boardMapper;

    @Override
    public boolean isBoardExists(int id) {
        Board board = boardMapper.findByID(id);

        return board != null;
    }

    @Override
    public Board getBoardById(int id) {
        Board board = boardMapper.findByID(id);

        return board;
    }

    
   
}
