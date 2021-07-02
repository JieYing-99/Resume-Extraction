package com.app.smarthire.recyclerviewLanguages;

public class Languages {

    private String language;
    private String level;

    public Languages() {
    }

    public Languages(String language, String level) {
        this.language = language;
        this.level = level;
    }

    public String getLanguage() {
        return language;
    }

    public void setSkill(String skill) {
        this.language = language;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return language + " - "+ level +"\n";
    }
}
