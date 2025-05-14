package com.aladin.todo_api.todos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    @Query(" SELECT t FROM Todo t WHERE t.userId = :userId " +
            "ORDER BY t.id DESC ")
    Page<Todo> findAllByUserId(String userId, Pageable pageable);

    Optional<Todo> findByIdAndUserId(Long id, String userId);

    void deleteByIdAndUserId(Long id, String userId);

    @Query(" SELECT t FROM Todo t WHERE t.userId = :userId " +
            "AND ((:searchType = 'ALL' AND (t.title LIKE %:searchWord% OR t.description LIKE %:searchWord%)) " +
            "OR (:searchType = 'TITLE' AND t.title LIKE %:searchWord%) " +
            "OR (:searchType = 'DESCRIPTION' AND t.description LIKE %:searchWord%))" +
            "ORDER BY t.id DESC ")
    Page<Todo> searchTodos(@Param("userId") String userId, @Param("searchType") String searchType, @Param("searchWord") String searchWord, Pageable pageable);
}
