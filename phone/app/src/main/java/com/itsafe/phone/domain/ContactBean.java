package com.itsafe.phone.domain;

/**
 * Created by Hello World on 2016/3/13.
 */
public class ContactBean {
    private String name;//名字
    private String phone;//电话

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ContactBean{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
