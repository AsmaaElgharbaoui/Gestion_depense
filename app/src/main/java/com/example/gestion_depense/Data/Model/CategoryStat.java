package com.example.gestion_depense.Data.Model;
public class CategoryStat {

    private String name;
    private float total;
    private float percentage;

    public CategoryStat(String name, float total, float percentage) {
        this.name = name;
        this.total = total;
        this.percentage = percentage;
    }

    public String getName() { return name; }
    public float getTotal() { return total; }
    public float getPercentage() { return percentage; }
}
