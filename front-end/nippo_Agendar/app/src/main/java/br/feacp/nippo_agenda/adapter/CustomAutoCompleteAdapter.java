package br.feacp.nippo_agenda.adapter;


import android.content.Context;
import android.widget.ArrayAdapter;
import java.util.List;

public class CustomAutoCompleteAdapter extends ArrayAdapter<String> {

    private final List<String> items;

    public CustomAutoCompleteAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public String getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
