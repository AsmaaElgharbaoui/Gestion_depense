package com.example.gestion_depense.Data.Model;

import java.util.Date;
import java.util.List;

public class DepenseGroup {

    private Date date;
    private List<Depense> depenses;

    public DepenseGroup(Date date, List<Depense> depenses) {
        this.date = date;
        this.depenses = depenses;
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
}