package com.eliasmshallouf.examples.lensy;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.eliasmshallouf.examples.lensy.models.Asset;
import com.eliasmshallouf.examples.lensy.models.daos.AssetsDAO;
import com.eliasmshallouf.examples.lensy.utils.TaskRunner;
import com.eliasmshallouf.examples.lensy.utils.Utils;

import java.util.List;

@androidx.room.Database(entities = {Asset.class}, version = 1, exportSchema = false)
public abstract class Database extends RoomDatabase {
    private static Database instance;

    public abstract AssetsDAO assetsDAO();

    public static Database getInstance(Context context) {
        if(instance == null) {
            instance =
                    Room.databaseBuilder(context.getApplicationContext(),
                            Database.class, "assets_database")
                            .addCallback(createCallback(context))
                            .build();
        }
        return instance;
    }

    //use callback function to listen on db creation and then insert the required data
    private static Callback createCallback(Context context) {
        return new Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
                TaskRunner.getInstance().executeAsync(() -> {
                    load(Database.getInstance(context).assetsDAO(), Utils.loadAssetsFromJson(Utils.loadRawAsText(context, R.raw.data)));
                    return null;
                });
            }
        };
    }

    public static void load(AssetsDAO dao, List<Asset> assets) {
        for(Asset asset : assets) {
            Log.d("inserting", asset.barcode);
            dao.insert(asset);
        }
    }

}
