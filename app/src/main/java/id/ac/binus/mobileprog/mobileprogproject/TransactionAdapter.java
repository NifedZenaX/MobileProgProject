package id.ac.binus.mobileprog.mobileprogproject;

import android.content.Context;
import android.content.Intent;
import android.icu.number.NumberFormatter;
import android.view.ContentInfo;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

public class TransactionAdapter extends BaseAdapter {

    List<String> description, categoryName;
    List<Integer> nominal;
    List<String> date;

    List<String> transId;
    LayoutInflater inflater;

    FirebaseFirestore db;

    public TransactionAdapter(Context context, List<String> description, List<String> categoryName, List<Integer> nominal, List<String> date, List<String> transId) {
        this.description = description;
        this.categoryName = categoryName;
        this.nominal = nominal;
        this.date = date;
        this.transId = transId;
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
        db = FirebaseFirestore.getInstance();
        view = inflater.inflate(R.layout.activity_transaction, null);
        TextView desc = view.findViewById(R.id.descriptionTextView);
        TextView cat = view.findViewById(R.id.categoryTextView);
        TextView dateTxt = view.findViewById(R.id.dateTextView);
        TextView nominal = view.findViewById(R.id.nominalTextView);
        Button editBtn = view.findViewById(R.id.editBtn);
        Button delBtn = view.findViewById(R.id.delbtn);
        desc.setText(description.get(i));
        cat.setText(categoryName.get(i));
        dateTxt.setText(date.get(i));

        DecimalFormat formatter = new DecimalFormat("#.###");
        String nominalTxt = formatter.format(this.nominal.get(i));
        nominal.setText("Rp. " + nominalTxt);

        final int idx = i;
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditActivity.class);
                intent.putExtra("transId", transId.get(idx));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
            }
        });
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("transaction")
                        .document(transId.get(idx))
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //Refresh activity
                                Intent intent = new Intent(view.getContext(), ViewTransactionsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                view.getContext().startActivity(intent);
//                                notifyDataSetChanged();
                            }
                        });
            }
        });
        return view;
    }
}
