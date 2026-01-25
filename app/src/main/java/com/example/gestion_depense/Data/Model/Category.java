package com.example.gestion_depense.Data.Model;
public class Category {

    private String id;
    private String name;
    private boolean isDefault;

    public Category() {}

    public Category(String name, boolean isDefault) {
        this.name = name;
        this.isDefault = isDefault;
    }

    public String getName() { return name; }
    public boolean isDefault() { return isDefault; }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}