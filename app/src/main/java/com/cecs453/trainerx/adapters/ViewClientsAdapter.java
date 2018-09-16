package com.cecs453.trainerx.adapters;

import com.cecs453.trainerx.model.Client;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;

public class ViewClientsAdapter extends FlexibleAdapter<AbstractFlexibleItem> {

    private static final String TAG = ViewClientsAdapter.class.getSimpleName();

    private List<Client> clients;

    public ViewClientsAdapter(List<AbstractFlexibleItem> items, Object listeners, List<Client> clients) {
        super(items, listeners, false);
        this.clients = clients;
    }

    @Override
    public void updateDataSet(List<AbstractFlexibleItem> items, boolean animate) {
        super.updateDataSet(items, animate);
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public String onCreateBubbleText(int position) {
        return clients.get(position).getfName().substring(0, 1);
    }

}