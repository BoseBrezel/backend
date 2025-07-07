package com.fokuswissen.quiz;

import java.util.List;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "quiz")
public class Quiz
{
    private String frage;
    private String antwort;
    private List<String> options;

    public Quiz()
    {
    }

    public Quiz(String frage, String antwort, List<String> options)
    {
        this.frage = frage;
        this.antwort = antwort;
        this.options = options;
    }

    public String getFrage()
    {
        return frage;
    }

    public void setFrage(String frage)
    {
        this.frage = frage;
    }

    public String getAntwort()
    {
        return antwort;
    }

    public void setAntwort(String antwort)
    {
        this.antwort = antwort;
    }

    public List<String> getOptions()
    {
        return options;
    }

    public void setOptions(List<String> options)
    {
        this.options = options;
    }
}