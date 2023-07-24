package com.eliasmshallouf.examples.lensy.models.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import com.eliasmshallouf.examples.lensy.Database;
import com.eliasmshallouf.examples.lensy.models.Asset;
import com.eliasmshallouf.examples.lensy.models.daos.AssetsDAO;
import com.eliasmshallouf.examples.lensy.Database;
import com.eliasmshallouf.examples.lensy.models.Asset;
import com.eliasmshallouf.examples.lensy.models.daos.AssetsDAO;

public class AssetsRepository {
    private AssetsDAO assetsDAO;
    private LiveData<PagedList<Asset>> assets;
    private LiveData<PagedList<String>> categories;

    public AssetsRepository(Application application) {
        Database database = Database.getInstance(application);
        assetsDAO = database.assetsDAO();
        categories = getLiveData(assetsDAO.listCategories(), 5);
        assets = search("%%", "%%");
    }

    private <E> LiveData<PagedList<E>> getLiveData(DataSource.Factory<Integer, E> source, int pageSize) {
        return new LivePagedListBuilder<>(
                source,
                new PagedList.Config.Builder()
                        .setEnablePlaceholders(false)
                        .setPageSize(pageSize)
                        .build()
        ).build();
    }

    public LiveData<PagedList<Asset>> search(String category, String filter) {
        return getLiveData(assetsDAO.search(category, filter), 10);
    }

    public void find(String barcode) {
        assets = getLiveData(assetsDAO.find(barcode), 10);
    }

    public LiveData<PagedList<Asset>> getAssets() {
        return assets;
    }

    public LiveData<PagedList<String>> getCategories() {
        return categories;
    }
}
