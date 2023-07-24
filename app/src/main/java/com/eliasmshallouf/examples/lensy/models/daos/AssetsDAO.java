package com.eliasmshallouf.examples.lensy.models.daos;

import androidx.paging.DataSource;
import androidx.room.*;

import com.eliasmshallouf.examples.lensy.models.Asset;

@Dao
public interface AssetsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE) void insert(Asset asset);
    @Update void update(Asset asset);
    @Delete void delete(Asset asset);
    @Query("DELETE FROM assets") void clear();
    @Query("SELECT * FROM assets where category LIKE :category and (description LIKE :filter or barcode LIKE :filter) ORDER BY description ASC") DataSource.Factory<Integer, Asset> search(String category, String filter);
    @Query("SELECT * FROM assets where barcode = :barcode") DataSource.Factory<Integer, Asset> find(String barcode);
    @Query("SELECT DISTINCT(category) FROM assets ORDER BY category ASC") DataSource.Factory<Integer, String> listCategories();
}
