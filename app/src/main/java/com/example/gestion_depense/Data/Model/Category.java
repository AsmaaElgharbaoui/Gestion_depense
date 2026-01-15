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
}