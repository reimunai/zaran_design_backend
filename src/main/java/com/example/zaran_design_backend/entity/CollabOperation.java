package com.example.zaran_design_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 协同编辑操作记录，对应数据库 collab_operations 表。
 * 存储协同过程中的每次编辑操作，用于版本历史和回滚。
 */
@Entity
@Table(name = "collab_operations",
       indexes = @Index(columnList = "session_id, version_number"))
public class CollabOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "op_id")
    private Integer opId;

    @Column(name = "session_id", nullable = false)
    private Integer sessionId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /** 操作类型（draw / layer_add / layer_delete / layer_update / move / delete 等） */
    @Column(name = "op_type", nullable = false, length = 20)
    private String opType;

    /** 操作数据（JSON 字符串，存储路径、坐标等） */
    @Lob
    @Column(name = "op_data", nullable = false)
    private String opData;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Integer getOpId() { return opId; }
    public void setOpId(Integer opId) { this.opId = opId; }

    public Integer getSessionId() { return sessionId; }
    public void setSessionId(Integer sessionId) { this.sessionId = sessionId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getOpType() { return opType; }
    public void setOpType(String opType) { this.opType = opType; }

    public String getOpData() { return opData; }
    public void setOpData(String opData) { this.opData = opData; }

    public Integer getVersionNumber() { return versionNumber; }
    public void setVersionNumber(Integer versionNumber) { this.versionNumber = versionNumber; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
