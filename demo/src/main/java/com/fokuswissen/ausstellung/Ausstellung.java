package com.fokuswissen.ausstellung;

import java.util.ArrayList;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ausstellungen")
public class Ausstellung {
    @Id
    private String id;

    private String titel;
    private String beschreibung;
    private String logo;
    private ArrayList<String> exponatIds;

    public Ausstellung() {
        this.exponatIds = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public ArrayList<String> getExponatIds() {
        return exponatIds;
    }

    public void setExponatIds(ArrayList<String> exponatIds) {
        this.exponatIds = exponatIds;
    }

    // Exponat hinzufügen
    public void addExponat(String exponatId) {
        if (!exponatIds.contains(exponatId)) {
            exponatIds.add(exponatId);
        }
    }

    // Exponat entfernen
    public void removeExponat(String exponatId)
    {
        exponatIds.remove(exponatId);
    }

    public String getLogo()
    {
        return logo;
    }

    public void setLogo(String logo)
    {
        this.logo = logo;
    }
}
