package com.example.slfb.doctor;

import com.example.slfb.HelperClass;

import java.io.Serializable;

public class HelperClassD extends HelperClass implements Serializable {
    private String about, address, education, experience;




    public HelperClassD(String name, String email, String phone, String password, String about, String address, String education, String experience) {
        super(name, email, phone, password);
        this.about = about;
        this.address = address;
        this.education = education;
        this.experience = experience;

    }

    public HelperClassD(String about, String address, String education, String experience) {
        this.about = about;
        this.address = address;
        this.education = education;
        this.experience = experience;
    }

    public HelperClassD() {
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

}
