package com.aladin.todo_api.todos;

import com.aladin.todo_api.common.response.ResponseUtil;
import com.aladin.todo_api.todos.dto.TodoDto;
import com.aladin.todo_api.todos.dto.TodoModifyDto;
import com.aladin.todo_api.todos.dto.TodoSearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    /*
     * 할일 등록
     * */
    @PostMapping()
    public ResponseEntity<?> createTodo(@RequestBody TodoDto dto, Authentication authentication) {
        Todo todo = todoService.createTodo(dto, authentication);
        return ResponseUtil.response("등록에 성공하였습니다.", todo);
    }

    /*
     * 할일 목록 조회
     * */
    @GetMapping()
    public ResponseEntity<?> getTodos(Authentication authentication) {
        Page<Todo> todos = todoService.getTodos(authentication);
        String message = todos.isEmpty()? "등록된 할일이 없습니다." : "목록 조회에 성공하였습니다.";
        return ResponseUtil.response(message, todos);
    }

    /*
     * 할일 단건 조회
     * */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTodo(@PathVariable Long id, Authentication authentication) {
        Todo todo = todoService.getTodo(id, authentication);
        return ResponseUtil.response("단건 조회에 성공하였습니다.", todo);
    }

    /*
     * 할일 수정
     * */
    @PutMapping("/{id}")
    public ResponseEntity<?> modifyTodo(@PathVariable Long id, @RequestBody TodoModifyDto dto, Authentication authentication) {
        Todo todo = todoService.modifyTodo(id, dto, authentication);
        return ResponseUtil.response("수정에 성공하였습니다.", todo);
    }

    /*
     * 할일 삭제
     * */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable Long id, Authentication authentication) {
        Todo todo = todoService.deleteTodo(id, authentication);
        return ResponseUtil.response("삭제에 성공하였습니다.", todo);

    }

    /*
     * 할일 검색
     * */
    @GetMapping("/search")
    public ResponseEntity<?> searchTodo(@RequestParam(defaultValue = "ALL") String searchType,
                                        @RequestParam String searchWord, Authentication authentication) {
        TodoSearchDto dto = new TodoSearchDto(searchType, searchWord);
        Page<Todo> todos = todoService.searchTodo(dto, authentication);
        return ResponseUtil.response("검색에 성공하였습니다.", todos);
    }
}
