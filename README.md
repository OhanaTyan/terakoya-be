预计加入接口
- 点赞/点踩
- - URL: 
- - - `/api/post/like`
- - - `/api/post/dislike`
- - - `/api/reply/like`
- - - `/api/reply/dislike`
- - 类型: `POST`
- - 载荷: `application/json`: `{pid:number}`
- - 响应: `application/json`: `{success:boolean, message:string}`
- - - `success`为`true`表示点赞/点踩成功，`false`表示点赞/点踩失败
- - - `message`为提示信息，一般为`点赞成功`或`点踩成功`或`操作失败`
- - 权限: 普通用户、管理用户
- - 描述: 给一个帖子点赞或点踩。如果某帖子/回复被赞过，则再次点赞会取消赞，点踩同理。如果某帖子回复被赞过，然后点踩，则会撤销之前的点赞，同时点踩该帖子/回复。点踩同理

```cpp
// 前端实现点赞或点踩功能时可以采用以下方法：
// 如果用户点击点赞或点踩按钮，
// 则无论请求是否发送成功，
// 请求返回值为多少，
// 均按照以下方法改变点赞值或点踩值
int like, dislike; //用户操作前的点赞数或点踩数
int like_pressed = false,
    dislike_pressed = false; //用户是否点赞或点踩

void press_like() {
    if (dislike_pressed){
        dislike--;
        dislike_pressed = false;
    }
    // 如果 like 按钮没有被点击过
    if (!like_pressed){
        like++;
    } else {
        like--;
    }
    like_pressed = true;
}

void press_dislike() {
    if (like_pressed){
        like--;
        like_pressed = false;
    }
    // 如果 dislike 按钮没有被点击过
    if (!dislike_pressed){
        dislike++;
    } else {
        dislike--;
    }
    dislike_pressed = true;
}


```
TODO: 封装接口，将token都放到结构体中

用户接口 全部完成

板块接口 全部完成
- 创建板块
- 修改板块
- 删除板块
- 获取板块列表

帖子接口 
- 帖子创建
- 帖子修改
- 帖子删除
- - 更新了删帖的逻辑
- 最新帖子
- 管理帖子列表
- 用户浏览帖子

回复接口
- 创建回复
- 修改回复
- 删除回复

- 最新回复

改了一些接口
- 帖子创建只需要传入`title` `content` `board`即可，其余字段会自动生成
- 帖子修改只需要传入`post`即可，无需传入`pid`。可以把`id`作为`post`的字段传入