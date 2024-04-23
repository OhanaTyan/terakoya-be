package terakoya.terakoyabe.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import terakoya.terakoyabe.setting.*;

@RestController
@CrossOrigin(origins = Setting.SOURCE_SITE, maxAge = 3600, allowCredentials = "true")
@RequestMapping("/board")
public class BoardController {
    @Autowired
    BoardMapper boardMapper;

    @Autowired
    UserService userService;

    ResponseEntity<?> serverError(Exception e){
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.toString());
    }


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
            return serverError(e);
        }
    }

    @PostMapping("/edit")
    public ResponseEntity<?> edit(
        @RequestBody Board board,
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

            // 验证是否存在该板块
            Board oldBoard = boardMapper.findByID(board.getId());
            if (oldBoard == null){
                return ResponseEntity.status(400).body(new ErrorResponse("板块不存在"));
            }
            // 验证板块名字是否存在
            if (boardMapper.findByName(board.getName())!= null){
                // 虽然该板块名字存在，但是当前板块名字
                if (board.getName().equals(oldBoard.getName())){
                    // do nothing
                } else {
                    return ResponseEntity.status(400).body(new ErrorResponse("板块名字已存在"));
                }
            }
            
            // 更新板块信息
            boardMapper.update(
                board.getId(),
                board.getName(),
                board.getDescription()
            );
            
            return ResponseEntity.ok().body(null);
            
        } catch (Exception e) {
            return serverError(e);
        }
    }   

    @PostMapping("/delete")
    public ResponseEntity<?> delete(
        @RequestBody Board board,
        @CookieValue(name="uid", required=false) int uid,
        @CookieValue(name="token", required = false) String token
    )
    {
        try {
            // TODO: 实现删除板块功能
            return ResponseEntity.ok().body(null);
        } catch (Exception e) {
            return serverError(e);
        }
    }

    @AllArgsConstructor
    @Data
    public static class ListRequest{
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

            return ResponseEntity.ok().body(new ListRequest(boardCount, boards));

        } catch (Exception e){
            return serverError(e);
        }
    }

}
