package com.example.gestion_depense.Data.Model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

public class Category {
    @Exclude
    private String id;
    private String name;
    @PropertyName("isDefault")
    private boolean isDefault;

    public Category() {}

    public Category(String name, boolean isDefault) {
        this.name = name;
        this.isDefault = isDefault;
    }
    @Exclude
    public String getId() { return id; }
    @Exclude
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    @PropertyName("isDefault")
    public boolean isDefault() { return isDefault; }
    @PropertyName("isDefault")
    public void setDefault(boolean aDefault) { isDefault = aDefault; }
}