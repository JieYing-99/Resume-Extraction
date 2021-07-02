package com.app.smarthire;

import com.google.firebase.database.Exclude;

public class Employee {

    private String mName;
    private String mPosition;
    private String mImageUrl;
    private String resumeImageUrl;
    private String key;
    private String mAddress;
    private String mPhoneNumber;
    private String mEmail;
    private String recruitedDate;
    private String mSkills;
    private String mLanguages;
    private String mEducation;
    private String mQualification;
    private String verify;
    private int mAge;
    private String bookMark;

    public Employee() {
    }

    public int getmAge() {
        return mAge;
    }

    public void setmAge(int mAge) {
        this.mAge = mAge;
    }

    public String getmSkills() {
        return mSkills;
    }

    public void setmSkills(String mSkills) {
        this.mSkills = mSkills;
    }

    public String getmLanguages() {
        return mLanguages;
    }

    public void setmLanguages(String mLanguages) {
        this.mLanguages = mLanguages;
    }

    public String getmEducation() {
        return mEducation;
    }

    public void setmEducation(String mEducation) {
        this.mEducation = mEducation;
    }

    public String getmQualification() {
        return mQualification;
    }

    public void setmQualification(String mQualification) {
        this.mQualification = mQualification;
    }

    public Employee(String mName, String mImageUrl, String resumeImageUrl, String mAddress, String mPhoneNumber, String mEmail, String recruitedDate, String mSkills, String mLanguages, String mEducation, String mQualification, int mAge) {
        this.mName = mName;
        this.mImageUrl = mImageUrl;
        this.resumeImageUrl = resumeImageUrl;
        this.mAddress = mAddress;
        this.mPhoneNumber = mPhoneNumber;
        this.mEmail = mEmail;
        this.recruitedDate = recruitedDate;
        this.mSkills = mSkills;
        this.mLanguages = mLanguages;
        this.mEducation = mEducation;
        this.mQualification = mQualification;
        this.mPosition = "Employee";
        this.mAge = mAge;
        this.verify = "unverified";
        this.bookMark = "unbookmarked";
    }

    public Employee(String mName, String mImageUrl, String resumeImageUrl, String mAddress, String mPhoneNumber, String mEmail, String recruitedDate, String mSkills, String mLanguages, String mEducation, String mQualification, int mAge, String mPosition) {
        this.mName = mName;
        this.mImageUrl = mImageUrl;
        this.resumeImageUrl = resumeImageUrl;
        this.mAddress = mAddress;
        this.mPhoneNumber = mPhoneNumber;
        this.mEmail = mEmail;
        this.recruitedDate = recruitedDate;
        this.mSkills = mSkills;
        this.mLanguages = mLanguages;
        this.mEducation = mEducation;
        this.mQualification = mQualification;
        this.mPosition = mPosition;
        this.mAge = mAge;
        this.verify = "unverified";
        this.bookMark = "unbookmarked";
    }

    public String getBookMark(){
        return bookMark;
    }

    public void setBookMark(String bookMark){
        this.bookMark = bookMark;
    }

    public String getVerify(){
        return verify;
    }

    public void setVerify(String verify){
        this.verify = verify;
    }

    public String getRecruitedDate() {
        return recruitedDate;
    }

    public void setRecruitedDate(String recruitedDate) {
        this.recruitedDate = recruitedDate;
    }

    public String getResumeImageUrl() {
        return resumeImageUrl;
    }

    public void setResumeImageUrl(String resumeImageUrl) {
        this.resumeImageUrl = resumeImageUrl;
    }


    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public String getmPhoneNumber() {
        return mPhoneNumber;
    }

    public void setmPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }


    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmPosition() {
        return mPosition;
    }

    public void setmPosition(String mPosition) {
        this.mPosition = mPosition;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    //exclude from firebase database
    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}
