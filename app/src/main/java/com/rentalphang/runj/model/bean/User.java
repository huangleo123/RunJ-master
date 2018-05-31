package com.rentalphang.runj.model.bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;

import static com.baidu.location.h.j.P;

/**
 * Created by rentalphang on 2016/11/14.
 */

public class User extends BmobUser {

    private String nickName;//昵称

    private Boolean sex;//性别

    private Integer age;//年龄

    private float weight;//体重

    private BmobDate birthday;//生日

    private String signature;//个性签名

    private String headImgPath;//头像本地文件路径

    private String headImgUrl;//头像网络url

    private float Height;//身高
    private String student_ID;
    private String facetoken;
    private String truename;
    private Boolean issign;

    public Boolean getIssign() {
        return issign;
    }

    public void setIssign(Boolean issign) {
        this.issign = issign;
    }

    public String getTruename() {
        return truename;
    }

    public void setTruename(String truename) {
        this.truename = truename;
    }

    public float getHeight() {
        return Height;
    }

    public String getStudent_ID() {
        return student_ID;
    }

    public void setStudent_ID(String student_ID) {
        this.student_ID = student_ID;
    }

    public String getFacetoken() {
        return facetoken;
    }

    public void setFacetoken(String facetoken) {
        this.facetoken = facetoken;
    }

    public void setHeight(float height) {
        Height = height;
    }




    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public BmobDate getBirthday() {
        return birthday;
    }

    public void setBirthday(BmobDate birthday) {
        this.birthday = birthday;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getHeadImgPath() {
        return headImgPath;
    }

    public void setHeadImgPath(String headImgPath) {
        this.headImgPath = headImgPath;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }
}
