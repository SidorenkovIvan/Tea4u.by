package ru.SidorenkovIvan.MyApplication.ui.Search;

import java.io.Serializable;

class Product implements Serializable {
    private String id;
    private String name;
    private String code;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String toString() {
        return this.name + "\n" + this.code;
    }
}