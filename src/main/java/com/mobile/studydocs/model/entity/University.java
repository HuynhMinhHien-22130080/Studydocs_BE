package com.mobile.studydocs.model.entity;

import java.util.List;

public class University{
    private String id ;
    private String name;
    private List<String> subjects;

    // ===== hao lam phần này (university entity) =====
    // Thêm getter/setter để sử dụng với Firestore và REST API
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<String> getSubjects() { return subjects; }
    public void setSubjects(List<String> subjects) { this.subjects = subjects; }
    // ===== end hao lam phần này =====
}

