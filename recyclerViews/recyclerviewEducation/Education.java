package com.app.smarthire.recyclerviewEducation;

public class Education {

    private String education;

    public Education(String education) {
        this.education = education;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    @Override
    public String toString() {
        return education + "\n";
    }
}
