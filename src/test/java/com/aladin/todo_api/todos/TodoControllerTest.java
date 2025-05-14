package com.aladin.todo_api.todos;

import com.aladin.todo_api.common.security.TokenProvider;
import com.aladin.todo_api.todos.dto.TodoDto;
import com.aladin.todo_api.todos.dto.TodoSearchDto;
import com.aladin.todo_api.users.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TodoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TokenProvider tokenProvider;
    @Autowired private TodoRepository todoRepository;
    @Autowired private UserRepository userRepository;

    private static final String USER_ID = "aladinUser";

    @BeforeEach
    void setUp() {
        todoRepository.deleteAll();
        userRepository.deleteAll();
    }

    private String generateToken() {
        return tokenProvider.generateToken(USER_ID);
    }

    private Todo createTodo(String title, String description, Status status) {
        Todo todo = Todo.builder()
                .userId(USER_ID)
                .title(title)
                .description(description)
                .status(status)
                .build();
        return todoRepository.save(todo);
    }

    @Test
    void 할일_생성_성공() throws Exception {
        // Given
        String token = generateToken();
        TodoDto dto = TodoDto.builder()
                .title("제목")
                .description("내용")
                .status("IN_PROGRESS")
                .build();

        // When & Then
        mockMvc.perform(post("/todos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("등록에 성공하였습니다."))
                .andExpect(jsonPath("$.data.userId").value(USER_ID))
                .andExpect(jsonPath("$.data.title").value("제목"))
                .andExpect(jsonPath("$.data.description").value("내용"))
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));
    }

    @Test
    void 할일_목록조회_성공() throws Exception {
        // Given
        String token = generateToken();
        createTodo("제목", "내용", Status.IN_PROGRESS);

        // When & Then
        mockMvc.perform(get("/todos")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("목록 조회에 성공하였습니다."))
                .andExpect(jsonPath("$.data.content[0].title").value("제목"))
                .andExpect(jsonPath("$.data.content[0].description").value("내용"))
                .andExpect(jsonPath("$.data.content[0].status").value("IN_PROGRESS"));
    }

    @Test
    void 할일_단건조회_성공() throws Exception {
        // Given
        String token = generateToken();
        Todo todo = createTodo("제목", "내용", Status.IN_PROGRESS);

        // When & Then
        mockMvc.perform(get("/todos/" + todo.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("단건 조회에 성공하였습니다."))
                .andExpect(jsonPath("$.data.title").value("제목"))
                .andExpect(jsonPath("$.data.description").value("내용"))
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));
    }

    @Test
    void 할일_수정_성공() throws Exception {
        // Given
        String token = generateToken();
        Todo todo = createTodo("제목", "내용", Status.IN_PROGRESS);

        TodoDto dto = TodoDto.builder()
                .title("수정된 제목")
                .description("수정된 내용")
                .status("COMPLETED")
                .build();

        // When & Then
        mockMvc.perform(put("/todos/" + todo.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("수정에 성공하였습니다."))
                .andExpect(jsonPath("$.data.title").value("수정된 제목"))
                .andExpect(jsonPath("$.data.description").value("수정된 내용"))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }

    @Test
    void 할일_삭제_성공() throws Exception {
        // Given
        String token = generateToken();
        Todo todo = createTodo("제목", "내용", Status.IN_PROGRESS);

        // When & Then
        mockMvc.perform(delete("/todos/" + todo.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("삭제에 성공하였습니다."))
                .andExpect(jsonPath("$.data.title").value("제목"))
                .andExpect(jsonPath("$.data.description").value("내용"))
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));
    }

    @Test
    void 할일_검색_성공() throws Exception {
        // Given
        String token = generateToken();
        createTodo("제목1", "내용1", Status.PENDING);
        createTodo("제목2", "내용2", Status.IN_PROGRESS);

        TodoSearchDto dto = TodoSearchDto.builder()
                .searchType("ALL")
                .searchWord("제목")
                .build();

        // When & Then
        mockMvc.perform(get("/todos/search")
                        .header("Authorization", "Bearer " + token)
                        .param("searchType", dto.getSearchType())
                        .param("searchWord", dto.getSearchWord()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("검색에 성공하였습니다."))
                .andExpect(jsonPath("$.data.content[1].title").value("제목1"))
                .andExpect(jsonPath("$.data.content[0].title").value("제목2"));
    }

    @Test
    void 할일_검색_실패_잘못된_검색타입() throws Exception {
        // Given
        String token = generateToken();

        TodoSearchDto dto = TodoSearchDto.builder()
                .searchType(null)
                .searchWord("검색어")
                .build();

        // When & Then
        mockMvc.perform(get("/todos/search")
                        .header("Authorization", "Bearer " + token)
                        .param("searchWord", "검색어"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("검색에 성공하였습니다."));
    }

    @Test
    void 할일_단건조회_실패_없는ID_404() throws Exception {
        // Given
        String token = generateToken();

        // When & Then
        mockMvc.perform(get("/todos/9999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 정보입니다."));
    }

    @Test
    void 할일_삭제_실패_없는ID_404() throws Exception {
        // Given
        String token = generateToken();

        // When & Then
        mockMvc.perform(delete("/todos/99999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 정보입니다."));
    }

    @Test
    void 할일_목록조회_실패_JWT_없음_401() throws Exception {
        // When & Then
        mockMvc.perform(get("/todos"))
                .andExpect(status().isUnauthorized());
    }
}
