package id.ac.binus.mobileprog.mobileprogproject;

import android.content.Context;
import android.icu.number.NumberFormatter;
import android.view.ContentInfo;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

public class TransactionAdapter extends BaseAdapter {

    List<String> description, categoryName;
    List<Integer> nominal;
    List<Date> date;
    LayoutInflater inflater;

    public TransactionAdapter(Context context, List<String> description, List<String> categoryName, List<Integer> nominal, List<Date> date) {
        this.description = description;
        this.categoryName = categoryName;
        this.nominal = nominal;
        this.date = date;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return description.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.activity_transaction, null);
        TextView desc = view.findViewById(R.id.descriptionTextView);
        TextView cat = view.findViewById(R.id.categoryTextView);
        TextView dateTxt = view.findViewById(R.id.dateTextView);
        TextView nominal = view.findViewById(R.id.nominalTextView);

        desc.setText(description.get(i));
        cat.setText(categoryName.get(i));
        dateTxt.setText(date.get(i).toString());

        DecimalFormat formatter = new DecimalFormat("#.###");
        String nominalTxt = formatter.format(this.nominal.get(i));
        nominal.setText("Rp. " + nominalTxt);
        return view;
    }
}
