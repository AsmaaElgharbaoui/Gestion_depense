package com.example.gestion_depense.Data.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DepenseGroup {

    private Date date;
    private List<Depense> depenses;

    public DepenseGroup(Date date) {
        this.date = date;
        this.depenses = new ArrayList<>();
    }

    public DepenseGroup(Date date, List<Depense> depenses) {
        this.date = date;
        this.depenses = (depenses != null) ? depenses : new ArrayList<>();
    }

    public Date getDate() {
        return date;
    }

    public List<Depense> getDepenses() {
        return depenses;
    }

    public double getTotal() {
        double total = 0;
        for (Depense d : depenses) {
            total += d.getMontant();
        }
        return total;
    }


    public void addDepense(Depense depense) {
        depenses.add(depense);
    }


}