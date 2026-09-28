package com.meetingnecessity.score.config;

import com.meetingnecessity.score.model.User;
import com.meetingnecessity.score.service.DashboardService;
import com.meetingnecessity.score.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserService userService;
    private final DashboardService dashboardService;

    public DataInitializer(UserService userService, DashboardService dashboardService) {
        this.userService = userService;
        this.dashboardService = dashboardService;
    }

    @Override
    public void run(String... args) {
        log.info("Initializing Meeting Necessity Score backend default dataset...");
        User user = userService.getOrCreateDefaultUser();
        dashboardService.getDashboardData(user);
        log.info("Meeting Necessity Score backend ready on port 8080. Demo user: {}", user.getEmail());
    }
}
