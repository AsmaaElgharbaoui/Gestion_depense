package com.example.gestion_depense.Data.Model;

import java.util.List;

public class Depense {
    private String id;
    private double montant;
    private String description;
    private long date; // timestamp
    private List<String> categoryIds;

    public Depense() {} // constructeur vide requis pour Firestore

    public Depense(double montant, String description, long date, List<String> categoryIds) {
        this.montant = montant;
        this.description = description;
        this.date = date;
        this.categoryIds = categoryIds;
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }
    public List<String> getCategoryIds() { return categoryIds; }
    public void setCategoryIds(List<String> categoryIds) { this.categoryIds = categoryIds; }
}
