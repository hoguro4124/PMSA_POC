package kr.co.skb.pmsa.member.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_logs")
public class WorkLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "_id")
    private Long id;

    @Column(name = "operator_id", nullable = false)
    private String operatorId; // 작업자

    @Column(name = "target_id")
    private String targetId;   // 대상자

    // ▼▼▼ [추가된 컬럼] ▼▼▼
    @Column(name = "menu_name")
    private String menuName;   // 접근 메뉴명
    // ▲▲▲

    @Column(name = "action_type", nullable = false)
    private String actionType; // 작업유형

    @Column(name = "details")
    private String details;    // 상세내용

    @Column(name = "ip_address")
    private String ipAddress;  // IP

    @CreationTimestamp
    @Column(name = "log_time", nullable = false, updatable = false)
    private LocalDateTime logTime;

    // 1. 기본 생성자 (JPA 필수)
    public WorkLog() {
    }

    // 2. 데이터 저장용 생성자 (menuName 포함)
    public WorkLog(String operatorId, String targetId, String menuName, String actionType, String details, String ipAddress) {
        this.operatorId = operatorId;
        this.targetId = targetId;
        this.menuName = menuName; // 추가됨
        this.actionType = actionType;
        this.details = details;
        this.ipAddress = ipAddress;
    }

    // 3. Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getMenuName() { // Getter 추가
        return menuName;
    }

    public void setMenuName(String menuName) { // Setter 추가
        this.menuName = menuName;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getLogTime() {
        return logTime;
    }

    public void setLogTime(LocalDateTime logTime) {
        this.logTime = logTime;
    }
}