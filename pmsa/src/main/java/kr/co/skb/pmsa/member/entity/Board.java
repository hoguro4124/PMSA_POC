package kr.co.skb.pmsa.member.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "board")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "_id")
    private int id;

    @Column(name = "user_id", nullable = false)
    @JsonProperty("userId")
    private String userId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    // [중요] DB가 NOT NULL이므로, 값이 안 들어오면 빈 문자열이라도 넣어야 함
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment = "";

    // [중요] String 대신 LocalDateTime 사용 (DB의 datetime 타입과 매핑)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 저장되기 직전에 시간 자동 설정
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // --- Getters & Setters ---
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    // 타입이 LocalDateTime으로 변경됨
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Board{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", comment='" + comment + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}