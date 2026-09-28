package com.meetingnecessity.score.repository;

import com.meetingnecessity.score.model.Score;
import com.meetingnecessity.score.model.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {
    Optional<Suggestion> findByScore(Score score);
    Optional<Suggestion> findByScoreId(Long scoreId);
}
