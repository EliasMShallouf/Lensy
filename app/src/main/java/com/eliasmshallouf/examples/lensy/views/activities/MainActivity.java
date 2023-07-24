package com.eliasmshallouf.examples.lensy.views.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.util.Consumer;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import com.eliasmshallouf.examples.lensy.utils.HorizontalMarginItemDecoration;
import com.eliasmshallouf.examples.lensy.R;
import com.eliasmshallouf.examples.lensy.utils.Utils;
import com.eliasmshallouf.examples.lensy.utils.VerticalMarginItemDecoration;
import com.eliasmshallouf.examples.lensy.databinding.ActivityMainBinding;
import com.eliasmshallouf.examples.lensy.models.Asset;
import com.eliasmshallouf.examples.lensy.view_models.AssetsViewModel;
import com.eliasmshallouf.examples.lensy.views.adapters.AssetsAdapter;
import com.eliasmshallouf.examples.lensy.views.adapters.CategoriesAdapter;
import com.google.android.material.appbar.AppBarLayout;
import java.util.Arrays;
import static com.eliasmshallouf.examples.lensy.utils.Utils.filter;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_BARCODE = 300;

    private static final String
            STATE_FILTER = "filter_state",
            STATE_CATEGORY = "category_state",
            STATE_CATEGORY_ITEM = "category_state_item",
            STATE_SCROLL = "scroll_state",
            STATE_APPBAR_EXPANDED = "scroll_appbar_expanded",
            STATE_FILTER_FOCUSED = "filter_focus_state";

    private ActivityMainBinding activityMainBinding;
    private AssetsViewModel assetsViewModel;
    private InputMethodManager inputMethodManager;

    private AppBarLayout appBarLayout;
    private View titleView, expandedTitleView;
    private AppCompatEditText filterET;

    private RecyclerView categoriesRV;
    private LinearLayoutManager categoriesLM;
    private CategoriesAdapter categoriesAdapter;

    private Observer<PagedList<Asset>> assetsObserver;
    private RecyclerView assetsRV;
    private AssetsAdapter assetsAdapter;
    private AppCompatTextView assetsCountTV;
    private Consumer<Void> filterFunction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        assetsViewModel = ViewModelProviders.of(this).get(AssetsViewModel.class);
        inputMethodManager = getSystemService(InputMethodManager.class);

        appBarLayout = activityMainBinding.appBar;
        titleView = activityMainBinding.title;
        expandedTitleView = activityMainBinding.expandedTitle;
        categoriesRV = activityMainBinding.categoriesLayout.categoriesList;
        assetsRV = activityMainBinding.assetsLayout.assetsList;
        assetsCountTV = activityMainBinding.assetsLayout.assetsCount;
        filterET = activityMainBinding.filter;

        Utils.registerCardViewTouchAnimation(activityMainBinding.menuBtn);
        Utils.registerCardViewTouchAnimation(activityMainBinding.userBtn);
        Utils.registerCardViewTouchAnimation(activityMainBinding.scanBarcode);
        Utils.registerCardViewTouchAnimation(activityMainBinding.goToTop);

        float transY = titleView.getTranslationY();

        appBarLayout.addOnOffsetChangedListener((AppBarLayout appBarLayout, int verticalOffset) -> {
            float percent = (verticalOffset * -1f) / (appBarLayout.getTotalScrollRange() * 1f);
            expandedTitleView.setAlpha(1f - percent);
            titleView.setAlpha(percent);
            titleView.setTranslationY(transY * (1f - percent) * 0.7f);
            appBarLayout.setElevation(10f * percent);
            activityMainBinding.toolbarDivider.setAlpha(percent);
        });

        assetsObserver = list -> {
            if(list.size() == 0) {
                assetsCountTV.animate().alpha(0f).setDuration(250).start();
                activityMainBinding.assetsLayout.nothingFoundCard.setVisibility(View.VISIBLE);
                activityMainBinding.assetsLayout.noAssetsFound.setText(String.format(getString(R.string.no_assets_desc), filterET.getText().toString()));
                TransitionManager.beginDelayedTransition((ViewGroup) activityMainBinding.assetsLayout.nothingFoundCard.getParent());
            } else {
                assetsCountTV.animate().alpha(1f).setDuration(250).start();
                if (activityMainBinding.assetsLayout.nothingFoundCard.getVisibility() == View.VISIBLE) {
                    activityMainBinding.assetsLayout.nothingFoundCard.setVisibility(View.GONE);
                    TransitionManager.beginDelayedTransition((ViewGroup) activityMainBinding.assetsLayout.nothingFoundCard.getParent());
                }
            }
            assetsCountTV.setText(String.format(getResources().getQuantityString(R.plurals.assets_count, list.size()), list.size()));
            TransitionManager.beginDelayedTransition((ViewGroup) assetsCountTV.getParent());
            assetsAdapter.submitList(list);
        };

        filterFunction = v -> {
            if(getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            filterET.clearFocus();
            assetsAdapter.setSearchText(filterET.getText().toString());
            assetsViewModel.search(categoriesAdapter.getSelectedCategory(), filterET.getText().toString()).observe(this, assetsObserver);
        };
        filterET.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        filterET.setOnEditorActionListener((v, action, event) -> {
            filterFunction.accept(null);
            return action == EditorInfo.IME_ACTION_SEARCH;
        });
        filterET.setOnFocusChangeListener((v, focused) -> {
            Utils.animateCardBorder(activityMainBinding.filterCard, getResources().getDimension(focused ? R.dimen.search_card_stroke_focused : R.dimen.search_card_stroke));
        });
        filterET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!filterET.getText().toString().isEmpty() && activityMainBinding.clearFilter.getVisibility() == View.INVISIBLE) {
                    activityMainBinding.clearFilter.setAlpha(0f);
                    activityMainBinding.clearFilter.setVisibility(View.VISIBLE);
                    activityMainBinding.clearFilter.animate().alpha(1f).setDuration(200).start();
                } else if(filterET.getText().toString().isEmpty() && activityMainBinding.clearFilter.getVisibility() == View.VISIBLE) {
                    activityMainBinding.clearFilter.animate().alpha(0f).setDuration(200).withEndAction(() -> activityMainBinding.clearFilter.setVisibility(View.INVISIBLE)).start();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        activityMainBinding.clearFilter.setOnClickListener(v -> {
            filterET.setText("");
            filterFunction.accept(null);
        });

        categoriesAdapter = new CategoriesAdapter();
        categoriesAdapter.setOnSelectCategory((cat, pos) -> {
            categoriesRV.scrollToPosition(pos);
            assetsViewModel.search(cat, filterET.getText().toString()).observe(this, assetsObserver);
        });
        categoriesAdapter.submitList(Arrays.asList("")); //All item

        categoriesLM = new LinearLayoutManager(this);
        categoriesLM.setOrientation(LinearLayoutManager.HORIZONTAL);
        categoriesLM.setSmoothScrollbarEnabled(true);
        categoriesLM.setStackFromEnd(false);
        categoriesLM.setReverseLayout(false);

        categoriesRV.setHasFixedSize(false);
        categoriesRV.setLayoutManager(categoriesLM);
        categoriesRV.setAdapter(categoriesAdapter);
        categoriesRV.addItemDecoration(new HorizontalMarginItemDecoration(
                (int) getResources().getDimension(R.dimen.margin_8),
                (int) getResources().getDimension(R.dimen.margin_24),
                (int) getResources().getDimension(R.dimen.margin_24)
        ));

        assetsViewModel.getCategories().observe(this, list -> {
            categoriesAdapter.submitList(filter(list));
        });

        assetsAdapter = new AssetsAdapter();

        assetsRV.setHasFixedSize(false);
        assetsRV.setLayoutManager(new LinearLayoutManager(this));
        assetsRV.setAdapter(assetsAdapter);
        assetsRV.addItemDecoration(new VerticalMarginItemDecoration(
                -1,
                -1,
                (int) (getResources().getDimension(R.dimen.margin_36) * 2.5)
        ));

        if(savedInstanceState == null) {
            assetsViewModel.getAssets().observe(this, assetsObserver);
        }

        activityMainBinding.scanBarcode.setOnClickListener(v -> {
            startActivityForResult(new Intent(this, BarcodeActivity.class), REQUEST_BARCODE);
        });

        activityMainBinding.assetsLayout.searchAgain.setOnClickListener(v -> {
            activityMainBinding.clearFilter.callOnClick();
        });

        activityMainBinding.scrollLayout.setOnScrollChangeListener((NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) -> {
            if(scrollY > 0) {
                showGoToTopButton();
            }
        });

        activityMainBinding.goToTop.setOnClickListener(v -> {
            activityMainBinding.scrollLayout.smoothScrollTo(0, 0);
            appBarLayout.setExpanded(true, true);
        });
    }

    private void hideGoToTopButton() {
        if(activityMainBinding.goToTop.getVisibility() == View.INVISIBLE)
            return;
        activityMainBinding.goToTop.animate()
                .scaleY(0f)
                .scaleX(0f)
                .translationY(500f)
                .alpha(0f)
                .withEndAction(() -> activityMainBinding.goToTop.setVisibility(View.INVISIBLE))
                .setDuration(250)
                .start();
    }

    private void showGoToTopButton() {
        if(activityMainBinding.goToTop.getVisibility() == View.VISIBLE)
            return;
        activityMainBinding.goToTop.animate()
                .scaleY(1f)
                .scaleX(1f)
                .translationY(0f)
                .alpha(1f)
                .withStartAction(() -> activityMainBinding.goToTop.setVisibility(View.VISIBLE))
                .withEndAction(() -> {
                    activityMainBinding.goToTop.postDelayed(this::hideGoToTopButton, 2500);
                })
                .setDuration(250)
                .start();
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_FILTER, filterET.getText().toString());
        outState.putInt(STATE_CATEGORY, categoriesAdapter.getSelectedCategoryIndex());
        outState.putString(STATE_CATEGORY_ITEM, categoriesAdapter.getSelectedCategory());
        outState.putBoolean(STATE_FILTER_FOCUSED, filterET.isFocused());
        outState.putInt(STATE_SCROLL, activityMainBinding.scrollLayout.computeVerticalScrollOffset());
        outState.getBoolean(STATE_APPBAR_EXPANDED, titleView.getAlpha() == 1f);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.getBoolean(STATE_FILTER_FOCUSED, false))
            filterET.requestFocus();
        appBarLayout.setExpanded(savedInstanceState.getBoolean(STATE_APPBAR_EXPANDED, false));
        filterET.setText(savedInstanceState.getString(STATE_FILTER, ""));
        categoriesAdapter.setSelectedCategory(savedInstanceState.getInt(STATE_CATEGORY, 0));
        assetsAdapter.setSearchText(filterET.getText().toString());
        assetsViewModel.search(savedInstanceState.getString(STATE_CATEGORY_ITEM, ""), filterET.getText().toString()).observe(this, list -> {
            assetsObserver.onChanged(list);
            //activityMainBinding.scrollLayout.scrollTo(0, savedInstanceState.getInt(STATE_SCROLL, 0));
            categoriesRV.scrollToPosition(categoriesAdapter.getSelectedCategoryIndex());
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BARCODE){
            if(resultCode == 1){
                String barcode = data.getStringExtra(BarcodeActivity.BARCODE_EXTRA);
                if(!barcode.isEmpty()) {
                    categoriesAdapter.setSelectedCategory(0);
                    categoriesRV.smoothScrollToPosition(0);
                    filterET.setText(barcode);
                    filterFunction.accept(null);
                }
            }
        }
    }
}
