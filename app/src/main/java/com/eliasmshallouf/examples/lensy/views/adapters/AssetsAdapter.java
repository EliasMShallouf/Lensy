package com.eliasmshallouf.examples.lensy.views.adapters;

import android.graphics.Color;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.eliasmshallouf.examples.lensy.App;
import com.eliasmshallouf.examples.lensy.R;
import com.eliasmshallouf.examples.lensy.utils.TaskRunner;
import com.eliasmshallouf.examples.lensy.utils.Utils;
import com.eliasmshallouf.examples.lensy.models.Asset;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.zxing.BarcodeFormat;

public class AssetsAdapter extends ListAdapter<Asset, AssetsAdapter.AssetViewHolder> {
    private static final DiffUtil.ItemCallback<Asset> DIFF_CALLBACK = new DiffUtil.ItemCallback<Asset>() {
        @Override
        public boolean areItemsTheSame(Asset oldItem, Asset newItem) {
            return oldItem.barcode.equals(newItem.barcode);
        }

        @Override
        public boolean areContentsTheSame(Asset oldItem, Asset newItem) {
            return oldItem.category.equals(newItem.category) &&
                    oldItem.description.equals(newItem.description);
        }
    };

    private TaskRunner taskRunner;
    private String searchText;

    public AssetsAdapter() {
        super(DIFF_CALLBACK);
        taskRunner = TaskRunner.getInstance();
        searchText = "";
    }

    public AssetsAdapter setSearchText(String searchText) {
        this.searchText = searchText;
        return this;
    }

    @NonNull
    @Override
    public AssetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AssetViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.asset_item_barcode, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AssetViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public class AssetViewHolder extends RecyclerView.ViewHolder {
        private ShapeableImageView barcode;
        private AppCompatTextView description, category, barcodeTV;
        private BackgroundColorSpan fcs = new BackgroundColorSpan(App.get().getAttrColor(R.attr.span_color));

        public AssetViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.description);
            category = itemView.findViewById(R.id.category);
            barcodeTV = itemView.findViewById(R.id.barcode);
            barcode = itemView.findViewById(R.id.barcode_img);
        }

        public void bind(Asset asset) {
            asset.description = asset.description.toLowerCase();
            if(searchText.length() > 0) {
                description.setText(Utils.filterText(Utils.capitalize(asset.description), searchText, fcs));
                barcodeTV.setText(Utils.filterText(asset.barcode, searchText, fcs));
            } else {
                description.setText(Utils.capitalize(asset.description));
                barcodeTV.setText(asset.barcode);
            }

            category.setText(Utils.capitalize(asset.category));
            taskRunner.executeAsync(() ->
                Utils.createBitmap(
                    Utils.encode(
                                asset.barcode,
                                BarcodeFormat.CODE_39,
                                40,
                                60
                    ), App.get().getAttrColor(R.attr.asset_card_barcode), Color.TRANSPARENT
                ), drawable -> {
                    barcode.setImageDrawable(drawable);
                }, error -> Log.e("error", "barcode error : " + error));
        }
    }
}
