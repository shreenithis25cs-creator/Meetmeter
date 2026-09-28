package com.meetingnecessity.score.repository;

import com.meetingnecessity.score.model.Meeting;
import com.meetingnecessity.score.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    // User ownership check queries
    List<Meeting> findByUser(User user);
    
    List<Meeting> findByUserId(Long userId);

    List<Meeting> findByUserAndStartTimeBetweenOrderByStartTimeAsc(User user, LocalDateTime start, LocalDateTime end);

    List<Meeting> findByUserIdAndStartTimeBetweenOrderByStartTimeAsc(Long userId, LocalDateTime start, LocalDateTime end);

    Optional<Meeting> findByIdAndUser(Long id, User user);

    Optional<Meeting> findByIdAndUserId(Long id, Long userId);

    Optional<Meeting> findByUserAndGoogleEventId(User user, String googleEventId);
}
