package com.meetingnecessity.score.controller;

import com.meetingnecessity.score.dto.SettingsDTO;
import com.meetingnecessity.score.model.Settings;
import com.meetingnecessity.score.model.User;
import com.meetingnecessity.score.repository.SettingsRepository;
import com.meetingnecessity.score.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class SettingsController {

    private final SettingsRepository settingsRepository;
    private final UserService userService;

    public SettingsController(SettingsRepository settingsRepository, UserService userService) {
        this.settingsRepository = settingsRepository;
        this.userService = userService;
    }

    /**
     * GET /api/settings → get user settings
     */
    @GetMapping
    public ResponseEntity<SettingsDTO> getSettings(@RequestParam(value = "userId", required = false) Long userId) {
        User user = (userId != null) ? userService.findById(userId).orElseGet(userService::getOrCreateDefaultUser) : userService.getOrCreateDefaultUser();

        Settings settings = settingsRepository.findByUser(user)
                .orElseGet(() -> {
                    Settings s = new Settings(user);
                    return settingsRepository.save(s);
                });

        return ResponseEntity.ok(new SettingsDTO(
                settings.getCostPerPersonPerHour(),
                settings.getLowScoreThreshold(),
                settings.getHighScoreThreshold()
        ));
    }

    /**
     * PUT /api/settings → update user's cost-per-hour and thresholds
     */
    @PutMapping
    public ResponseEntity<SettingsDTO> updateSettings(@RequestBody SettingsDTO dto,
                                                      @RequestParam(value = "userId", required = false) Long userId) {
        if (dto == null || !Double.isFinite(dto.getCostPerPersonPerHour())
                || dto.getCostPerPersonPerHour() < 0
                || dto.getLowScoreThreshold() < 0 || dto.getLowScoreThreshold() > 100
                || dto.getHighScoreThreshold() < 1 || dto.getHighScoreThreshold() > 100
                || dto.getLowScoreThreshold() >= dto.getHighScoreThreshold()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        User user = (userId != null) ? userService.findById(userId).orElseGet(userService::getOrCreateDefaultUser) : userService.getOrCreateDefaultUser();

        Settings settings = settingsRepository.findByUser(user)
                .orElseGet(() -> new Settings(user));

        settings.setCostPerPersonPerHour(dto.getCostPerPersonPerHour());
        settings.setLowScoreThreshold(dto.getLowScoreThreshold());
        settings.setHighScoreThreshold(dto.getHighScoreThreshold());

        Settings updated = settingsRepository.save(settings);

        return ResponseEntity.ok(new SettingsDTO(
                updated.getCostPerPersonPerHour(),
                updated.getLowScoreThreshold(),
                updated.getHighScoreThreshold()
        ));
    }
}
