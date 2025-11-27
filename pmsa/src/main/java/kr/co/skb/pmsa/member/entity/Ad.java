package kr.co.skb.pmsa.member.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ad")
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "_id")
    private Long id;

    @Column(name = "ad_title", nullable = false, length = 200)
    private String adTitle;

    @Column(name = "ad_start_at")
    private LocalDateTime adStartDate;

    @Column(name = "ad_end_at")
    private LocalDateTime adEndDate;

    @Column(name = "ad_attach", length = 255)
    private String adAttach;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "ad_reg_at")
    private LocalDateTime adRegDate;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdTitle() {
        return adTitle;
    }

    public void setAdTitle(String adTitle) {
        this.adTitle = adTitle;
    }

    public LocalDateTime getAdStartDate() {
        return adStartDate;
    }

    public void setAdStartDate(LocalDateTime adStartDate) {
        this.adStartDate = adStartDate;
    }

    public LocalDateTime getAdEndDate() {
        return adEndDate;
    }

    public void setAdEndDate(LocalDateTime adEndDate) {
        this.adEndDate = adEndDate;
    }

    public String getAdAttach() {
        return adAttach;
    }

    public void setAdAttach(String adAttach) {
        this.adAttach = adAttach;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getAdRegDate() {
        return adRegDate;
    }

    public void setAdRegDate(LocalDateTime adRegDate) {
        this.adRegDate = adRegDate;
    }

    @Override
    public String toString() {
        return "Ad{" +
                "id=" + id +
                ", adTitle='" + adTitle + '\'' +
                ", adStartDate=" + adStartDate +
                ", adEndDate=" + adEndDate +
                ", adAttach='" + adAttach + '\'' +
                ", userId='" + userId + '\'' +
                ", adRegDate=" + adRegDate +
                '}';
    }
}
