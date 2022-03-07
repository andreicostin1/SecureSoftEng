package service.vaxapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import service.vaxapp.model.ForumQuestion;

@Repository
public interface ForumQuestionRepository extends JpaRepository<ForumQuestion, Integer> {
}
