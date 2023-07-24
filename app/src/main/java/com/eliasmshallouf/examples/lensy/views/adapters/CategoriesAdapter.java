package com.eliasmshallouf.examples.lensy.views.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.eliasmshallouf.examples.lensy.App;
import com.eliasmshallouf.examples.lensy.R;
import com.eliasmshallouf.examples.lensy.utils.Utils;
import com.google.android.material.card.MaterialCardView;

public class CategoriesAdapter extends ListAdapter<String, CategoriesAdapter.CategoryViewHolder> {
    @FunctionalInterface public interface OnSelectCategory {
        void onSelect(String category, int position);
    }

    private static final DiffUtil.ItemCallback<String> DIFF_CALLBACK = new DiffUtil.ItemCallback<String>() {
        @Override
        public boolean areItemsTheSame(String oldItem, String newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(String oldItem, String newItem) {
            return oldItem.equals(newItem);
        }
    };

    private int selectedCategory;
    private OnSelectCategory onSelectCategory;

    public CategoriesAdapter() {
        super(DIFF_CALLBACK);
        selectedCategory = 0;
    }

    public CategoriesAdapter setOnSelectCategory(OnSelectCategory onSelectCategory) {
        this.onSelectCategory = onSelectCategory;
        return this;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(position == 0) {
            holder.bind(App.get().getString(R.string.all));
        } else {
            holder.bind(getItem(position));
        }
        if (position == selectedCategory) {
            holder.select();
        } else {
            holder.deselect();
        }
        holder.itemView.setOnClickListener(v -> {
            if(position != selectedCategory) {
                final int tmp = selectedCategory;
                selectedCategory = position;
                notifyItemChanged(tmp);
                notifyItemChanged(position);
                if(this.onSelectCategory != null)
                    this.onSelectCategory.onSelect(position == 0 ? "" : getItem(position), position);
            }
        });
    }

    public void setSelectedCategory(int category) {
        if(this.selectedCategory == category)
            return;
        int tmp = this.selectedCategory;
        this.selectedCategory = category;
        notifyItemChanged(tmp);
        notifyItemChanged(this.selectedCategory);
    }

    public int getSelectedCategoryIndex() {
        return selectedCategory;
    }

    public String getSelectedCategory() {
        return selectedCategory == 0 ? "" : getItem(selectedCategory);
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView container;
        private AppCompatTextView category;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            container = (MaterialCardView) itemView;
            category = itemView.findViewById(R.id.category);
        }

        public void bind(String cat) {
            category.setText(Utils.capitalize(cat));
        }

        public void select() {
            container.setCardBackgroundColor(App.get().getAttrColor(R.attr.category_background_selected));
            category.setTextColor(App.get().getAttrColor(R.attr.category_text_color_selected));
            category.setTypeface(ResourcesCompat.getFont(App.get(), R.font.bold));
        }

        public void deselect() {
            container.setCardBackgroundColor(App.get().getAttrColor(R.attr.category_background));
            category.setTextColor(App.get().getAttrColor(R.attr.category_text_color));
            category.setTypeface(ResourcesCompat.getFont(App.get(), R.font.semi_bold));
        }
    }
}
