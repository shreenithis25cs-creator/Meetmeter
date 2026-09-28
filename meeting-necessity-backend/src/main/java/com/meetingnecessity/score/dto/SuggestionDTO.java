package com.meetingnecessity.score.dto;

import com.meetingnecessity.score.model.enums.SuggestionSource;

public class SuggestionDTO {
    private Long id;
    private String suggestionText;
    private SuggestionSource source;

    public SuggestionDTO() {
    }

    public SuggestionDTO(Long id, String suggestionText, SuggestionSource source) {
        this.id = id;
        this.suggestionText = suggestionText;
        this.source = source;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
