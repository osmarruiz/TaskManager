package com.dcmc.apps.taskmanager.repository;

import com.dcmc.apps.taskmanager.domain.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Comment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
    @Query("select comment from Comment comment where comment.author.login = ?#{authentication.name}")
    List<Comment> findByAuthorIsCurrentUser();
}
