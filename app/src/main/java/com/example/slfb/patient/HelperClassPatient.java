package com.example.slfb.patient;

import com.example.slfb.HelperClass;

import java.util.Date;

public class HelperClassPatient extends HelperClass {
    private String age,height,weight,sleepDuration;
    private String activityLevel,sex,image;
    private String diabetesSince;
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HelperClassPatient() {
    }

    public HelperClassPatient(String age, String height, String weight, String sex,String sleepDuration, String activityLevel, String diabetesSince) {
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.sleepDuration = sleepDuration;
        this.activityLevel = activityLevel;
        this.diabetesSince = diabetesSince;
        this.sex=sex;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public HelperClassPatient(String name, String email, String phone, String password, String age, String height, String weight, String sex, String sleepDuration, String activityLevel, String diabetesSince, String image) {
        super(name, email, phone, password);
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.sleepDuration = sleepDuration;
        this.activityLevel = activityLevel;
        this.diabetesSince = diabetesSince;
        this.sex=sex;
        this.image=image;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getSleepDuration() {
        return sleepDuration;
    }

    public void setSleepDuration(String sleepDuration) {
        this.sleepDuration = sleepDuration;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public String getDiabetesSince() {
        return diabetesSince;
    }

    public void setDiabetesSince(String diabetesSince) {
        this.diabetesSince = diabetesSince;
    }
}
