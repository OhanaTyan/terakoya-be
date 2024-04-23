package terakoya.terakoyabe.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import terakoya.terakoyabe.entity.Board;
import terakoya.terakoyabe.mapper.BoardMapper;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    private BoardMapper boardMapper;

    @Override
    public boolean isBoardExists(int id) {
        Board board = boardMapper.findByID(id);

        if (board == null) {
            return false;
        }

        return true;
    }

   
}
