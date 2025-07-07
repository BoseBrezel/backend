package com.fokuswissen.exponat;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fokuswissen.quiz.Quiz;

@Document(collection = "exponate")
public class Exponat
{
    @Id
    private String id;

    private String titel;
    private String beschreibung;
    private String scanBild;
    private String videoUrl;

    private ArrayList<String> ausstellungsIds = new ArrayList<>();
    private ArrayList<String> bildUrls = new ArrayList<>();
    private ArrayList<Quiz> quiz = new ArrayList<>();


    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setScanBild(String scanBild)
    {
        this.scanBild = scanBild;
    }
    public String getScanBild()
    {
        return scanBild;
    }

    public String getTitel()
    {
        return titel;
    }

    public void setTitel(String titel)
    {
        this.titel = titel;
    }

    public String getBeschreibung()
    {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung)
    {
        this.beschreibung = beschreibung;
    }


    public ArrayList<String> getAusstellungsIds() {
        return ausstellungsIds;
    }

    public void setAusstellungsIds(ArrayList<String> ausstellungsIds) {
        this.ausstellungsIds = ausstellungsIds;
    }

    public String getVideoUrl()
    {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl)
    {
        this.videoUrl = videoUrl;
    }

    public ArrayList<String> getBildUrls()
    {
        return bildUrls;
    }

    public void setBildUrls(ArrayList<String> bildUrls)
    {
        this.bildUrls = bildUrls;
    }

    public void setQuiz(ArrayList<Quiz> quiz)
    {
        this.quiz = quiz;
    }

    public ArrayList<Quiz> getQuiz()
    {
        return quiz;
    }
}