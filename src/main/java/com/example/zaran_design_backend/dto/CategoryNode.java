package com.example.zaran_design_backend.dto;

import com.example.zaran_design_backend.entity.SketchCategory;

import java.util.ArrayList;
import java.util.List;

/** 草图分类树节点 */
public class CategoryNode {

    private Integer categoryId;
    private String name;
    private String description;
    private List<CategoryNode> children = new ArrayList<>();

    public static CategoryNode of(SketchCategory category) {
        CategoryNode node = new CategoryNode();
        node.categoryId = category.getCategoryId();
        node.name = category.getName();
        node.description = category.getDescription();
        return node;
    }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<CategoryNode> getChildren() { return children; }
    public void setChildren(List<CategoryNode> children) { this.children = children; }
}
