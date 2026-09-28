package com.meetingnecessity.score.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meetingnecessity.score.model.enums.SuggestionSource;
import jakarta.persistence.*;

@Entity
@Table(name = "suggestions")
public class Suggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "score_id", nullable = false, unique = true)
    @JsonIgnore
    private Score score;

    @Column(name = "suggestion_text", columnDefinition = "TEXT", nullable = false)
    private String suggestionText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SuggestionSource source;

    public Suggestion() {
    }

    public Suggestion(Score score, String suggestionText, SuggestionSource source) {
        this.score = score;
        this.suggestionText = suggestionText;
        this.source = source;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public String getSuggestionText() {
        return suggestionText;
    }

    public void setSuggestionText(String suggestionText) {
        this.suggestionText = suggestionText;
    }

    public SuggestionSource getSource() {
        return source;
    }

    public void setSource(SuggestionSource source) {
        this.source = source;
    }
}
