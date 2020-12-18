package com.example.finalapp.Reclamation;

public class Reclamation {
    public String recId;
    public String userId;
    public String agentId;
    public String date;
    public String categorie;
    public String type;
    public String etat;
    public String details;

    public Reclamation(String recId, String userId, String agentId, String date, String categorie, String type, String etat, String details) {
        this.recId = recId;
        this.userId = userId;
        this.agentId = agentId;
        this.date = date;
        this.categorie = categorie;
        this.type = type;
        this.etat = etat;
        this.details = details;
    }

    public Reclamation() {}
}
