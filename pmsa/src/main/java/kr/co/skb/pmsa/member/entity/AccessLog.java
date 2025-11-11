package kr.co.skb.pmsa.member.entity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

// 접속 기록을 저장하는 테이블과 매핑되는 엔티티
@Entity
@Table(name = "access_logs")
public class AccessLog {

    // 1. NO (순번/ID)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "_id")
    private Long id;

    // 2. 사용자 ID (접속한 사용자)
    @Column(name = "user_id", nullable = false)
    private String userId;

    // 3. 접속 IP 주소
    @Column(name = "ip_address")
    private String ipAddress;

    // 4. 작업 구분 (예: LOGIN, LOGOUT, CHANGE_PASSWORD 등)
    @Column(name = "action_type", nullable = false)
    private String actionType;

    // 5. 접속 일시 (로그가 기록된 시간 - 자동으로 생성되도록 설정)
    @CreationTimestamp
    @Column(name = "log_time", nullable = false, updatable = false)
    private LocalDateTime logTime;


    // 기본 생성자
    public AccessLog() {}

    // 로그 기록 시 사용할 생성자 (ID와 시간은 자동으로 처리됨)
    public AccessLog(String userId, String ipAddress, String actionType) {
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.actionType = actionType;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public LocalDateTime getLogTime() { return logTime; }
    public void setLogTime(LocalDateTime logTime) { this.logTime = logTime; }

    @Override
    public String toString() {
        return "AccessLog{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", actionType='" + actionType + '\'' +
                ", logTime=" + logTime +
                '}';
    }
}