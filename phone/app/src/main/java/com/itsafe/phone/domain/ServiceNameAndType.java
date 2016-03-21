package com.itsafe.phone.domain;

/**
 * Created by Hello World on 2016/3/21.
 */
public class ServiceNameAndType {
    private String name;
    private int out_id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOut_id() {
        return out_id;
    }

    public void setOut_id(int out_id) {
        this.out_id = out_id;
    }

    @Override
    public String toString() {
        return "ServiceNameAndType{" +
                "name='" + name + '\'' +
                ", out_id=" + out_id +
                '}';
    }
}
