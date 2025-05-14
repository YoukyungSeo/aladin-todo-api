package com.aladin.todo_api.todos;

import com.aladin.todo_api.todos.dto.TodoDto;
import com.aladin.todo_api.todos.dto.TodoModifyDto;
import com.aladin.todo_api.todos.dto.TodoSearchDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final Logger log = (Logger) LoggerFactory.getLogger(TodoService.class);
    private final TodoRepository todoRepository;

    /*
     * AccessToken 검증 후 userId 가져오기
     * */
    private String getUserId(Authentication authentication) {
        return authentication.getPrincipal().toString();
    }

    /*
     * id, userId로 등록된 할일 데이터 불러오기
     * */
    private Todo getTodoByIdAndUser(Long id, String userId){
        Todo todo = todoRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 정보입니다."));
        return todo;
    }

    /*
     * 할일 등록
     * */
    @Transactional
    public Todo createTodo(TodoDto dto, Authentication authentication) {
        String userId = getUserId(authentication);
        Status statusEnum = Status.fromString(dto.getStatus());

        Todo todo = Todo.builder()
                .userId(userId)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(statusEnum)
                .build();
        return todoRepository.save(todo);
    }

    /*
     * 할일 목록 조회
     * */
    @Transactional
    public Page<Todo> getTodos(Authentication authentication) {
        String userId = getUserId(authentication);
        Pageable pageable = PageRequest.of(0, 10);

        return todoRepository.findAllByUserId(userId, pageable);
    }

    /*
     * 할일 단건 조회
     * */
    @Transactional
    public Todo getTodo(Long id, Authentication authentication) {
        String userId = getUserId(authentication);
        return getTodoByIdAndUser(id, userId);
    }

    /*
     * 할일 수정
     * */
    @Transactional
    public Todo modifyTodo(Long id, TodoModifyDto dto, Authentication authentication) {
        String userId = getUserId(authentication);
        Todo todo = getTodoByIdAndUser(id, userId);

        if (dto.getTitle() != null && !dto.getTitle().trim().isEmpty()) {
            todo.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null && !dto.getDescription().trim().isEmpty()) {
            todo.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null) {
            Status statusEnum = Status.fromString(dto.getStatus());
            todo.setStatus(statusEnum);
        }
        return todoRepository.save(todo);
    }

    /*
     * 할일 삭제
     * */
    @Transactional
    public Todo deleteTodo(Long id, Authentication authentication) {
        String userId = getUserId(authentication);

        Todo todo = getTodoByIdAndUser(id, userId);
        todoRepository.deleteByIdAndUserId(id, userId);

        return todo;
    }

    /*
     * 할일 검색
     * */
    @Transactional
    public Page<Todo> SearchTodo(TodoSearchDto dto, Authentication authentication) {
        String userId = getUserId(authentication);

        Pageable pageable = PageRequest.of(0, 10);

        SearchType searchTypeEnum = SearchType.fromString(dto.getSearchType());
        String searchType = searchTypeEnum.name();

        return todoRepository.searchTodos(userId, searchType, dto.getSearchWord(), pageable);
    }

}
