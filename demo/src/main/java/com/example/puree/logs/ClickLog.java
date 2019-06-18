package com.example.puree.logs;

import com.google.gson.annotations.SerializedName;

import com.mercari.puree.PureeLog;

public class ClickLog implements PureeLog {
    @SerializedName("page")
    private String page;
    @SerializedName("label")
    private String label;

    public ClickLog(String page, String label) {
        this.page = page;
        this.label = label;
    }
}
