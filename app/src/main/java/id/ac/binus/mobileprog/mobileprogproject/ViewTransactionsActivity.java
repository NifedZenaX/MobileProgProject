package id.ac.binus.mobileprog.mobileprogproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class ViewTransactionsActivity extends AppCompatActivity {

    List<String> descriptions = new ArrayList<>();
    List<String> categoryName = new ArrayList<>();
    Map<String, String> categories = Collections.synchronizedMap(new HashMap<String, String>());
    List<Date> dates = new ArrayList<>();
    List<Integer> nominals = new ArrayList<>();
    List<String> transId = new ArrayList<>();
    ListView listView;
    Button editBtn, delBtn;
    FirebaseFirestore db;




    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId())
        {
            case R.id.options1:
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.options2:
                intent = new Intent(this, CreateActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_transactions);

        listView = findViewById(R.id.listView);


        db = FirebaseFirestore.getInstance();

        db.collection("categories")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot category : queryDocumentSnapshots){
                            categories.put(category.getId(), category.get("name").toString());
                        }
                    }
                });

        db.collection("transaction")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        for (QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                            if(doc.get("user_id").toString().equals(user.getUid())){
                                transId.add(doc.getId());
                                descriptions.add(doc.get("description").toString());
                                nominals.add(Integer.parseInt(doc.get("nominal").toString()));
                                categoryName.add(categories.get(doc.get("category_id").toString()));
                                try {
                                    dates.add(new SimpleDateFormat("dd/MMM/yyyy").parse(doc.get("date").toString()));
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        TransactionAdapter adapter = new TransactionAdapter(getApplicationContext(), descriptions, categoryName, nominals, dates, transId);
                        System.out.println("Adapter: " + adapter.getCount());
                        listView.setAdapter(adapter);
                    }
                });

    }
}