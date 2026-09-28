package com.meetingnecessity.score.controller;

import com.meetingnecessity.score.dto.DashboardResponseDTO;
import com.meetingnecessity.score.model.User;
import com.meetingnecessity.score.service.DashboardService;
import com.meetingnecessity.score.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserService userService;

    public DashboardController(DashboardService dashboardService, UserService userService) {
        this.dashboardService = dashboardService;
        this.userService = userService;
    }

    /**
     * GET /api/dashboard → returns all this week's meetings with their scores, plus aggregate stats
     */
    @GetMapping
    public ResponseEntity<DashboardResponseDTO> getDashboard(@RequestParam(value = "userId", required = false) Long userId) {
        User user = (userId != null) ? userService.findById(userId).orElseGet(userService::getOrCreateDefaultUser) : userService.getOrCreateDefaultUser();

        DashboardResponseDTO dashboardData = dashboardService.getDashboardData(user);
        return ResponseEntity.ok(dashboardData);
    }
}
