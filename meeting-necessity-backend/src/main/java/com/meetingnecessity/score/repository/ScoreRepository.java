package com.meetingnecessity.score.repository;

import com.meetingnecessity.score.model.Meeting;
import com.meetingnecessity.score.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {
    Optional<Score> findByMeeting(Meeting meeting);
    Optional<Score> findByMeetingId(Long meetingId);
}
