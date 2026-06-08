package com.example.zaran_design_backend.entity;

import jakarta.persistence.*;

/**
 * 草图分类，对应数据库 sketch_categories 表。
 * 通过 parent_id 构建多级层级，按 sort_order 排序。
 */
@Entity
@Table(name = "sketch_categories")
public class SketchCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 255)
    private String description;

    /** 父分类ID，顶级分类为 null */
    @Column(name = "parent_id")
    private Integer parentId;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
