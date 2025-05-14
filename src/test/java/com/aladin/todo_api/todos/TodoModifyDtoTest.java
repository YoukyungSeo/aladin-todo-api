package com.aladin.todo_api.todos;

import com.aladin.todo_api.todos.dto.TodoModifyDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TodoModifyDtoTest {

    @Test
    void getter_검증() {
        // Given
        String title = "제목";
        String description = "내용";
        String status = "IN_PROGRESS";

        // When
        TodoModifyDto dto = new TodoModifyDto(title, description, status);

        // Then
        assertEquals(title, dto.getTitle());
        assertEquals(description, dto.getDescription());
        assertEquals(status, dto.getStatus());
    }

    @Test
    void 빌더_패턴_검증() {
        // Given & When
        TodoModifyDto dto = TodoModifyDto.builder()
                .title("제목")
                .description("내용")
                .status("COMPLETED")
                .build();

        // Then
        assertEquals("제목", dto.getTitle());
        assertEquals("내용", dto.getDescription());
        assertEquals("COMPLETED", dto.getStatus());
    }
}
