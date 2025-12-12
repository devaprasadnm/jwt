package com.ievolve.jwt.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Category {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int categoryId;
    @Column(unique = true)
    private String categoryName;
    public Category() {
        super();
    }
    public Category(String categoryName) {
        this.categoryName = categoryName;
    }
    public int getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    @Override
    public String toString() {  
        return "Category [categoryId=" + categoryId + ", categoryName=" + categoryName + "]";
    }
}
