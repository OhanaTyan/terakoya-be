package terakoya.terakoyabe.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import terakoya.terakoyabe.Service.UserService;
import terakoya.terakoyabe.entity.Board;
import terakoya.terakoyabe.mapper.BoardMapper;
import terakoya.terakoyabe.mapper.PostMapper;
import terakoya.terakoyabe.setting.*;
import terakoya.terakoyabe.util.ServerError;

@RestController
@CrossOrigin(origins = Setting.SOURCE_SITE, maxAge = 3600, allowCredentials = "true")
@RequestMapping("/board")
public class BoardController {
    @Autowired
    BoardMapper boardMapper;

    @Autowired
    PostMapper postMapper;

    @Autowired
    UserService userService;


    @AllArgsConstructor
    @Data
    public static class ErrorResponse{
        String message;
    }

    @AllArgsConstructor
    @Data
    public static class CreateResponse{
        int id;
    }

    public boolean isBoardNameExists(String name){
        Board board = boardMapper.findByName(name);
        return board!= null;
    }
    
    @PostMapping("/create")
    public ResponseEntity<?> create(
        @RequestBody Board board,
        @CookieValue(name="uid", required=false) int uid,
        @CookieValue(name="token", required = false) String token
    )
    {
        try {
            // 验证用户身份
            if (!TokenController.verifyToken(uid, token)){
                return ResponseEntity.status(401).body(new ErrorResponse("token 验证失败，请重新登录"));
            }
            // 验证是否是管理用户
            if (!userService.isAdmin(uid)){
                return ResponseEntity.status(403).body(new ErrorResponse("权限不足"));
            }
            // 检查是否已有板块名字与传入板块相同
            if (isBoardNameExists(board.getName())){
                return ResponseEntity.status(400).body(new ErrorResponse("板块名字已存在"));
            }

            boardMapper.create(
                board.getName(),
                board.getDescription()
            );
            
            int id = boardMapper.findByName(board.getName()).getId();

            return ResponseEntity.ok().body(new CreateResponse(id));
        } catch (Exception e){
            return ResponseEntity.status(500).body(new ServerError(e));
        }
    }


    @AllArgsConstructor
    @Data
    public static class MyBoard{
        Integer id;
        String  name;
        String  description;
    }

    @PostMapping("/edit")
    public ResponseEntity<?> edit(
        @RequestBody MyBoard data,
        @CookieValue(name="uid") int uid,
        @CookieValue(name="token") String token
    )
    {
        try {
            // 验证 token
            if (!TokenController.verifyToken(uid, token)){
                return ResponseEntity.status(401).body(new ErrorResponse("token 验证失败，请重新登录"));
            }

            // 验证是否是管理用户
            if (!userService.isAdmin(uid)){
                return ResponseEntity.status(403).body(new ErrorResponse("权限不足"));
            }

            int boardId;
            if (data.getId() == null){
                return ResponseEntity.status(400).body(new ErrorResponse("板块 id 不能为空"));
            } else {
                boardId = data.getId();
            }

            // 验证是否存在该板块
            Board oldBoard = boardMapper.findByID(boardId);
            if (oldBoard == null){
                return ResponseEntity.status(400).body(new ErrorResponse("板块不存在或已被删除"));
            }
            String name;
            if (data.getName() == null){
                return ResponseEntity.status(400).body(new ErrorResponse("板块名字不能为空"));
            } else {
                name = data.getName();
            }
            // 验证板块名字是否存在
            if (boardMapper.findByName(name)!= null){
                // 虽然该板块名字存在，但是当前板块名字
                if (data.getName().equals(oldBoard.getName())){
                    // do nothing
                } else {
                    return ResponseEntity.status(400).body(new ErrorResponse("板块名字已存在"));
                }
            }
            
            // 更新板块信息
            boardMapper.update(
                boardId,
                name,
                data.getDescription()
            );
            
            return ResponseEntity.ok().body(null);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ServerError(e));
        }
    }   

    @PostMapping("/delete")
    public ResponseEntity<?> delete(
        @RequestBody MyBoard data,
        @CookieValue(name="uid", required=false) int uid,
        @CookieValue(name="token", required = false) String token
    )
    {
        try {
            // 验证 token
            if (!TokenController.verifyToken(uid, token)){
                return ResponseEntity.status(401).body(new ErrorResponse("token 验证失败，请重新登录"));
            }

            // 验证是否是管理用户
            if (!userService.isAdmin(uid)){
                return ResponseEntity.status(403).body(new ErrorResponse("权限不足"));
            }

            // int boardId = data.getId();
            int boardId;
            if (data.getId() == null){
                return ResponseEntity.status(400).body(new ErrorResponse("板块 id 不能为空"));
            } else {
                boardId = data.getId();
            }

            // 验证是否存在该板块
            Board oldBoard = boardMapper.findByID(boardId);
            if (oldBoard == null){
                return ResponseEntity.status(400).body(new ErrorResponse("板块不存在或已被删除"));
            }
            // 清除板块
            boardMapper.deleteBoard(data.getId());
            // 将该板块下的所有帖子的 postid 改为 0
            postMapper.updateBoardToZero(boardId);

            return ResponseEntity.ok().body("删除成功");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ServerError(e));
        }
    }

    @AllArgsConstructor
    @Data
    public static class ListResponse{
        int boardCount;
        List<Board> boards;
    }

    @GetMapping("/list")
    public ResponseEntity<?> list(
        @CookieValue(name="uid", required=false) int uid,
        @CookieValue(name="token", required = false) String token
    )
    {
        try {
            // 验证用户身份
            if (!TokenController.verifyToken(uid, token)){
                return ResponseEntity.status(401).body(new ErrorResponse("token 验证失败，请重新登录"));
            }

            // 返回所有板块的列表
            List<Board> boards = boardMapper.listAll();
            int boardCount = boards.size();

            return ResponseEntity.ok().body(new ListResponse(boardCount, boards));

        } catch (Exception e){
            return ResponseEntity.status(500).body(new ServerError(e));
        }
    }

}
