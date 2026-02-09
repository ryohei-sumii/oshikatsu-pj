package com.oshikatsu_pj.oshikatsu.oshimember.domain.model;

import com.oshikatsu_pj.oshikatsu.auth.domain.model.User;
import com.oshikatsu_pj.oshikatsu.oshigroup.domain.model.OshiGroup;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "oshi_member")
public class OshiMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_oshi_member_user",
                    value = ConstraintMode.CONSTRAINT
            )
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "group_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_oshi_member_group",
                    value = ConstraintMode.CONSTRAINT
            )
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private OshiGroup oshiGroup;

    @Column(nullable = false, name = "member_name")
    private String memberName;

    @Column(nullable = false, name = "member_name_kana")
    private String memberNameKana;

    @Column(nullable = false, name = "gender")
    private byte gender;

    @Column(nullable = false, name = "birth_day")
    private LocalDate birthDay;

    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false, name = "updated_at")
    @Version
    private LocalDateTime updatedAt;

    // JPA用のデフォルトコンストラクタ
    protected OshiMember() {}

    // ビジネスロジック用のコンストラクタ
    public OshiMember(User user,
                      OshiGroup oshiGroup,
                      String memberName,
                      String memberNameKana,
                      byte gender,
                      LocalDate birthDay) {
        this.user = user;
        this.oshiGroup = oshiGroup;
        this.memberName = memberName;
        this.memberNameKana = memberNameKana;
        this.gender = gender;
        this.birthDay = birthDay;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String memberName,
                       String memberNameKana,
                       byte gender,
                       LocalDate birthDay) {
        this.memberName = memberName;
        this.memberNameKana = memberNameKana;
        this.gender = gender;
        this.birthDay = birthDay;
        this.updatedAt = LocalDateTime.now();
    }
}
