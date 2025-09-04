// users 테이블 1:1 매핑

package kr.co.skb.pmsa.member.entity;

import jakarta.persistence.*;
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "_id") // DB 컬럼명 매핑
    private int id;
    @Column(name = "user_id")
    private String userId;;
    @Column(name = "phone")
    private String phone;
    @Column(name = "password")
    private String password;
    @Column(name = "marketing_agree")
    private boolean maAgree;
    @Column(name = "age_agree")
    private boolean ageAgree;
    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String email;
    @Column(name = "access_level")
    private int accessLevel;




    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isMaAgree() { return maAgree; }
    public void setMaAgree(boolean maAgree) { this.maAgree = maAgree; }

    public boolean isAgeAgree() { return ageAgree; }
    public void setAgeAgree(boolean ageAgree) { this.ageAgree = ageAgree; }

    public int getAccessLevel() { return accessLevel; }
    public void setAccessLevel(int accessLevel) { this.accessLevel = accessLevel; }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", maAgree=" + maAgree +
                ", ageAgree=" + ageAgree +
                ", accessLevel=" + accessLevel +
                '}';
    }

}

