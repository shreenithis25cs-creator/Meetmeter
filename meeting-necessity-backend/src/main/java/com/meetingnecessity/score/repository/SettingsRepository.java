package com.meetingnecessity.score.repository;

import com.meetingnecessity.score.model.Settings;
import com.meetingnecessity.score.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingsRepository extends JpaRepository<Settings, Long> {
    Optional<Settings> findByUser(User user);
    Optional<Settings> findByUserId(Long userId);
}
