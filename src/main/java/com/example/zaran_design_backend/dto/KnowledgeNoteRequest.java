package com.example.zaran_design_backend.dto;

public class KnowledgeNoteRequest {
    private Integer userId;
    private Integer entryId;
    private String personalNote;

    // ========== Getter 和 Setter ==========
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getEntryId() { return entryId; }
    public void setEntryId(Integer entryId) { this.entryId = entryId; }

    public String getPersonalNote() { return personalNote; }
    public void setPersonalNote(String personalNote) { this.personalNote = personalNote; }
}