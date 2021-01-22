package ru.sidorenkovivan.tea4uby.util;

import org.jetbrains.annotations.NotNull;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {

    private final LinearLayoutManager mLayoutManager;

    public PaginationScrollListener(final LinearLayoutManager pLayoutManager) {
        mLayoutManager = pLayoutManager;
    }

    @Override
    public void onScrolled(@NotNull final RecyclerView pRecyclerView, final int pDx, final int pDy) {
        super.onScrolled(pRecyclerView, pDx, pDy);

        final int visibleItemCount = mLayoutManager.getChildCount();
        final int totalItemCount = mLayoutManager.getItemCount();
        final int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();

        if (!isLoading() && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= getTotalPageCount()) {
                loadMoreItems();
            }
        }

    }

    protected abstract void loadMoreItems();

    public abstract int getTotalPageCount();

    public abstract boolean isLastPage();

    public abstract boolean isLoading();

}
