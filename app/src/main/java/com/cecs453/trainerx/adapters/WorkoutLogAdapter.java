package com.cecs453.trainerx.adapters;

import android.support.annotation.Nullable;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;

public class WorkoutLogAdapter extends FlexibleAdapter<AbstractFlexibleItem> {

    public WorkoutLogAdapter(List<AbstractFlexibleItem> items, Object listeners) {
        super(items, listeners, false);
    }

    @Override
    public void updateDataSet(@Nullable List<AbstractFlexibleItem> items, boolean animate) {
        super.updateDataSet(items, animate);
    }
    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }
}
