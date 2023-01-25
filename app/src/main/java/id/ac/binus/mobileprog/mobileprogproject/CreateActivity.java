package id.ac.binus.mobileprog.mobileprogproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class CreateActivity extends AppCompatActivity {

    DatePickerDialog picker;
    EditText eText;

    Spinner addCategory2;
    EditText addCategory;
    EditText addExpenses;
    EditText addDescription;
    Button btnSubmit;

    FirebaseFirestore firestore;
    Map<String, String> categories = Collections.synchronizedMap(new HashMap<String, String>());
    List<String> currCategories = Collections.synchronizedList(new ArrayList<String>());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        //firebase
        firestore = FirebaseFirestore.getInstance();

        addCategory = findViewById(R.id.addCategory);

        addExpenses = findViewById(R.id.addExpenses);
        addDescription = findViewById(R.id.addDescription);

        btnSubmit = findViewById(R.id.btnSubmit);

        firestore.collection("categories")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot category : queryDocumentSnapshots){
                            categories.put(category.get("name").toString(), category.getId());
                            currCategories.add(category.get("name").toString());
                        }
                    }
                });

        // Buat Dropdown category
        addCategory2 = findViewById(R.id.categorySpnr);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, currCategories);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        addCategory2.setAdapter(adapter);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> transaction = new HashMap<>();
                transaction.put("category", categories.get(addCategory.getText().toString()));
                //Nanti tolong dites ini bisa ato engga yg bawah ini, nanti kl mo tes yg bawah ini, atasnya dicomment aja
//                person.put("category", categories.get(addCategory2.getSelectedItem().toString()));
                transaction.put("nominal", addExpenses.getText().toString());
                transaction.put("date", eText.getText().toString());
                transaction.put("description", addDescription.getText().toString());

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                transaction.put("user_id", user.getUid());

                firestore.collection("transaction").add(transaction).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), "Sukses!", Toast.LENGTH_LONG).show();
                        Intent intent =  new Intent(getApplicationContext(), ViewTransactionsActivity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        eText=(EditText) findViewById(R.id.addDate);
        eText.setInputType(InputType.TYPE_NULL);
        eText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(CreateActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                eText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });
    }
}