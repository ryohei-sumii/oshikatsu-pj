package com.oshikatsu_pj.oshikatsu.auth.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;
    private String email;
    private String password;

    public void changePassword(String newPassword) {
        if (newPassword.length() < 8)
        this.password = password;
    }
}
