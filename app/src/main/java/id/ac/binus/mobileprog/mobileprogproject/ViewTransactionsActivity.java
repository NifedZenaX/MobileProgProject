package id.ac.binus.mobileprog.mobileprogproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.Date;
import java.util.List;

public class ViewTransactionsActivity extends AppCompatActivity {

    ListView listView;
    FirebaseFirestore db;

    List<String> descriptions;
    List<String> categoryNames;
    List<Date> dates;
    List<Integer> nominals;


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
                intent = new Intent(this, TransactionActivity.class);
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

        db.collection("transaction")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        for (QueryDocumentSnapshot doc: task.getResult()) {
                            if(doc.get("user_id").toString() == user.getUid()){
                                descriptions.add(doc.get("description").toString());
                                nominals.add(Integer.parseInt(doc.get("nominal").toString()));
                                try {
                                    dates.add(new SimpleDateFormat("dd/MM/yyyy").parse(doc.get("date").toString()));
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                                db.collection("category")
                                        .document(doc.get("category_id").toString())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    categoryNames.add(task.getResult().get("name").toString());
                                                }
                                            }
                                        });
                            }
                        }
                    }
                }
        });
        TransactionAdapter adapter = new TransactionAdapter(getApplicationContext(), descriptions, categoryNames, nominals, dates);
        listView.setAdapter(adapter);
    }
}