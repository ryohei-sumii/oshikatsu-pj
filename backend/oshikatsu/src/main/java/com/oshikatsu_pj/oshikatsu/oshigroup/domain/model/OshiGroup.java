package com.oshikatsu_pj.oshikatsu.oshigroup.domain.model;

import com.oshikatsu_pj.oshikatsu.auth.domain.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "oshi_group")
public class OshiGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_oshi_group_user",
                    value = ConstraintMode.CONSTRAINT
            )
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(nullable = false, name = "group_name")
    private String groupName;

    @Column(nullable = false, name = "company")
    private String company;

    @Column(name = "description")
    private String description;

    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false, name = "updated_at")
    @Version
    private LocalDateTime updatedAt;

    // JPA用のデフォルトコンストラクタ
    protected OshiGroup() {}

    // ビジネスロジック用のコンストラクタ
    public OshiGroup(String groupName,
                     String company,
                     String description) {
        this.groupName = groupName;
        this.company = company;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String groupName,
                       String company,
                       String description) {
        this.groupName = groupName;
        this.company = company;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
}
