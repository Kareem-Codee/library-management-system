package com.kareemmushtaha.library.model;

import java.time.LocalDate;

public class Member {

    private int id;
    private String fullName;
    private String email;
    private LocalDate joinedAt;

    // new member, joined today
    public Member(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
        this.joinedAt = LocalDate.now();
    }

    // member from the database
    public Member(int id, String fullName, String email, LocalDate joinedAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.joinedAt = joinedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getJoinedAt() {
        return joinedAt;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + fullName + " <" + email + "> joined: " + joinedAt;
    }
}