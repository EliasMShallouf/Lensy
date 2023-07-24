package com.eliasmshallouf.examples.lensy.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "assets")
public class Asset {
    @PrimaryKey @NonNull public String barcode;
    public String description;
    public String category;

    public Asset(String barcode, String description, String category) {
        this.barcode = barcode;
        this.description = description;
        this.category = category;
    }
}
