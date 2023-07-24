package com.eliasmshallouf.examples.lensy.utils;

import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HorizontalMarginItemDecoration extends RecyclerView.ItemDecoration {
    private int margin = -1, firstMargin = -1, lastMargin = -1;

    public HorizontalMarginItemDecoration(int margin, int firstMargin, int lastMargin) {
        this.margin = margin;
        this.firstMargin = firstMargin;
        this.lastMargin = lastMargin;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (isFirstItem(parent, view) && firstMargin != -1) {
            outRect.left = firstMargin;
        } else {
            outRect.left = margin;
        }

        if(lastMargin == -1) {
            outRect.right = margin;
        } else {
            if(isLastItem(parent, view, state)) {
                outRect.right = lastMargin;
            } else {
                outRect.right = margin;
            }
        }
    }

    private boolean isFirstItem(RecyclerView parent, View item) {
        return parent.getChildAdapterPosition(item) == 0;
    }

    private boolean isLastItem(RecyclerView parent, View item, RecyclerView.State state) {
        return parent.getChildAdapterPosition(item) == state.getItemCount() - 1;
    }
}
