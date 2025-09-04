package kr.co.skb.pmsa.member.entity;

import jakarta.persistence.*;
@Entity
@Table(name = "admin_users")
public class AdminUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "_aid") // DB 컬럼명 매핑
    private int aid;
    @Column(name = "admin_id")
    private String adminId;
    @Column(name = "access_level")
    private int accessLevel;
    @Column(name = "name")
    private String name;
    @Column(name = "phone")
    private String phone;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;


    // Getters & Setters
    public int getAid() { return aid; }
    public void setAid(int aid) { this.aid = aid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getAccessLevel() { return accessLevel; }
    public void setAccessLevel(int accessLevel) { this.accessLevel = accessLevel; }

}
