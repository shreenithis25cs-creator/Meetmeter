package com.meetingnecessity.score.controller;

import com.meetingnecessity.score.model.User;
import com.meetingnecessity.score.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/auth/google")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AuthController {

    private final UserService userService;
    private final WebClient webClient;

    @Value("${google.oauth.client-id:}")
    private String googleClientId;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${google.oauth.client-secret:}")
    private String googleClientSecret;

    @Value("${server.port:8080}")
    private int serverPort;

    public AuthController(UserService userService, WebClient.Builder webClientBuilder) {
        this.userService = userService;
        this.webClient = webClientBuilder.build();
    }

    /**
     * GET /auth/google/login → redirects to Google OAuth consent screen
     */
    @GetMapping("/login")
    public RedirectView loginWithGoogle(@RequestParam(value = "redirect_uri", required = false) String customRedirect) {
        if (googleClientId == null || googleClientId.isBlank() || googleClientId.contains("PLACEHOLDER")
                || googleClientSecret == null || googleClientSecret.isBlank()) {
            // For local development or when credentials are not yet set in properties:
            // Auto-authenticates demo user and redirects to frontend dashboard
            User demoUser = userService.getOrCreateDefaultUser();
            String target = isAllowedRedirect(customRedirect) ? customRedirect
                    : frontendUrl + "?token=" + demoUser.getId() + "&authenticated=true";
            return new RedirectView(target);
        }

        String redirectUri = callbackUri();
        String scope = "openid email profile https://www.googleapis.com/auth/calendar.readonly";

        String googleAuthUrl = String.format(
                "https://accounts.google.com/o/oauth2/v2/auth?client_id=%s&redirect_uri=%s&response_type=code&scope=%s&access_type=offline&prompt=consent",
                URLEncoder.encode(googleClientId, StandardCharsets.UTF_8),
                URLEncoder.encode(redirectUri, StandardCharsets.UTF_8),
                URLEncoder.encode(scope, StandardCharsets.UTF_8)
        );

        return new RedirectView(googleAuthUrl);
    }

    /**
     * GET /auth/google/callback → handles OAuth callback, saves tokens, creates/updates User
     */
    @GetMapping("/callback")
    public RedirectView handleGoogleCallback(@RequestParam(value = "code", required = false) String code,
                                             @RequestParam(value = "error", required = false) String error) {
        if (error != null || code == null) {
            return new RedirectView(frontendUrl + "?error=" + (error != null ? error : "auth_failed"));
        }

        if (googleClientId == null || googleClientId.isBlank() || googleClientSecret == null || googleClientSecret.isBlank()) {
            return new RedirectView(frontendUrl + "?error=oauth_not_configured");
        }

        try {
            Map<?, ?> tokens = webClient.post()
                    .uri("https://oauth2.googleapis.com/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(org.springframework.web.reactive.function.BodyInserters.fromFormData("code", code)
                            .with("client_id", googleClientId)
                            .with("client_secret", googleClientSecret)
                            .with("redirect_uri", callbackUri())
                            .with("grant_type", "authorization_code"))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            if (tokens == null || !(tokens.get("access_token") instanceof String accessToken)) {
                return new RedirectView(frontendUrl + "?error=oauth_token_exchange_failed");
            }

            Map<?, ?> profile = webClient.get()
                    .uri("https://www.googleapis.com/oauth2/v2/userinfo")
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            if (profile == null || profile.get("email") == null) {
                return new RedirectView(frontendUrl + "?error=oauth_profile_fetch_failed");
            }

            String refreshToken = tokens.get("refresh_token") instanceof String value ? value : null;
            Object email = profile.get("email");
            Object name = profile.containsKey("name") ? profile.get("name") : email;
            User user = userService.processOAuthUser(email.toString(), name.toString(), accessToken, refreshToken);
            return new RedirectView(frontendUrl + "?token=" + user.getId() + "&authenticated=true");
        } catch (Exception e) {
            return new RedirectView(frontendUrl + "?error=oauth_callback_failed");
        }
    }

    private String callbackUri() {
        String scheme = frontendUrl.startsWith("https://") ? "https" : "http";
        String authority = java.net.URI.create(frontendUrl).getAuthority();
        if (authority != null && !authority.startsWith("localhost") && !authority.startsWith("127.0.0.1")) {
            return scheme + "://" + authority + "/auth/google/callback";
        }
        return "http://localhost:" + serverPort + "/auth/google/callback";
    }

    private boolean isAllowedRedirect(String redirect) {
        if (redirect == null || redirect.isBlank()) return false;
        try {
            java.net.URI candidate = java.net.URI.create(redirect);
            java.net.URI frontend = java.net.URI.create(frontendUrl);
            return candidate.isAbsolute() && candidate.getHost() != null
                    && candidate.getHost().equalsIgnoreCase(frontend.getHost());
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Helper endpoint for checking current authentication status.
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        User user = userService.getOrCreateDefaultUser();
        Map<String, Object> res = new HashMap<>();
        res.put("id", user.getId());
        res.put("email", user.getEmail());
        res.put("name", user.getName());
        res.put("authenticated", true);
        return ResponseEntity.ok(res);
    }
}
