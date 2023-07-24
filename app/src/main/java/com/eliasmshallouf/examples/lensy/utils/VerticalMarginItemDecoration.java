package com.eliasmshallouf.examples.lensy.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VerticalMarginItemDecoration extends RecyclerView.ItemDecoration {
    private int margin = -1, firstMargin = -1, lastMargin = -1;

    public VerticalMarginItemDecoration(int margin, int firstMargin, int lastMargin) {
        this.margin = margin;
        this.firstMargin = firstMargin;
        this.lastMargin = lastMargin;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (isFirstItem(parent, view) && firstMargin != -1) {
            outRect.top = firstMargin;
        } else {
            outRect.top = margin;
        }

        if(lastMargin == -1) {
            outRect.bottom = margin;
        } else {
            if(isLastItem(parent, view, state)) {
                outRect.bottom = lastMargin;
            } else {
                outRect.bottom = margin;
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
