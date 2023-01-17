package id.ac.binus.mobileprog.mobileprogproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

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

    RecyclerView recyclerView;
    FirebaseFirestore db;

    List<String> descriptions;
    List<String> categoryNames;
    List<Date> dates;
    List<Integer> nominals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_transactions);

        recyclerView = findViewById(R.id.recyclerView);


        db = FirebaseFirestore.getInstance();

        db.collection("transactions")
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
                                db.collection("categories")
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
    }
}