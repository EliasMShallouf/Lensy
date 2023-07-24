package com.eliasmshallouf.examples.lensy.view_models;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;
import com.eliasmshallouf.examples.lensy.models.Asset;
import com.eliasmshallouf.examples.lensy.models.repositories.AssetsRepository;

public class AssetsViewModel extends AndroidViewModel {
    private AssetsRepository assetsRepository;
    private LiveData<PagedList<Asset>> assets;
    private LiveData<PagedList<String>> categories;

    public AssetsViewModel(@NonNull Application application) {
        super(application);
        assetsRepository = new AssetsRepository(application);
        assets = assetsRepository.getAssets();
        categories = assetsRepository.getCategories();
    }

    public LiveData<PagedList<Asset>> search(String category, String filter) {
        return assetsRepository.search(category.isEmpty() ? "%%" : category, "%" + filter + "%");
    }

    public void find(String barcode) {
        assetsRepository.find(barcode);
    }

    public LiveData<PagedList<Asset>> getAssets() {
        return assets;
    }

    public LiveData<PagedList<String>> getCategories() {
        return categories;
    }
}
