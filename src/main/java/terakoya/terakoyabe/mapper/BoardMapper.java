package terakoya.terakoyabe.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import terakoya.terakoyabe.entity.Board;

@Mapper
public interface BoardMapper {

    @Insert("INSERT INTO boards (id, name, description) SELECT COALESCE(MAX(id), 0) + 1, #{name}, #{description} FROM boards")
    void create(String name, String description);
    
    @Select("SELECT * FROM boards WHERE name = #{name}")
    Board findByName(String name);

    @Select("SELECT * FROM boards")
    List<Board> listAll();

    @Select("SELECT * FROM boards WHERE id = #{id}")
    Board findByID(int id);

    @Insert("UPDATE boards SET name = #{name}, description = #{description} WHERE id = #{id}")
    void update(int id, String name, String description);

    @Delete("DELETE FROM boards WHERE id = #{id}")
    void deleteBoard(int id);
}
