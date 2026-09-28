package com.meetingnecessity.score.service;

import com.meetingnecessity.score.model.Settings;
import com.meetingnecessity.score.model.User;
import com.meetingnecessity.score.repository.SettingsRepository;
import com.meetingnecessity.score.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SettingsRepository settingsRepository;

    public UserService(UserRepository userRepository, SettingsRepository settingsRepository) {
        this.userRepository = userRepository;
        this.settingsRepository = settingsRepository;
    }

    /**
     * Saves or updates a user from Google OAuth2 credentials.
     */
    @Transactional
    public User processOAuthUser(String email, String name, String accessToken, String refreshToken) {
        Optional<User> existingOpt = userRepository.findByEmail(email);
        User user;

        if (existingOpt.isPresent()) {
            user = existingOpt.get();
            user.setName(name);
            if (accessToken != null) user.setGoogleAccessToken(accessToken);
            if (refreshToken != null) user.setGoogleRefreshToken(refreshToken);
        } else {
            user = new User(email, name, accessToken, refreshToken);
            user = userRepository.save(user);

            // Create default settings for new user
            Settings settings = new Settings(user);
            settingsRepository.save(settings);
            user.setSettings(settings);
        }

        return userRepository.save(user);
    }

    /**
     * Retrieves the default/demo user or the current user.
     */
    @Transactional
    public User getOrCreateDefaultUser() {
        return userRepository.findByEmail("shanmugam@example.com")
                .orElseGet(() -> processOAuthUser("shanmugam@example.com", "Shanmugam", "mock_google_access_token", "mock_google_refresh_token"));
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
