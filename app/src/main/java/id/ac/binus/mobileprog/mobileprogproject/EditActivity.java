package id.ac.binus.mobileprog.mobileprogproject;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();

        //firebase
        firestore = FirebaseFirestore.getInstance();

        addCategory = findViewById(R.id.editCategory);

        addExpenses = findViewById(R.id.editExpenses);
        addDescription = findViewById(R.id.editDescription);

        btnSubmit = findViewById(R.id.editBtnSubmit);

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
        addCategory2 = findViewById(R.id.editcategorySpnr);
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
                transaction.put("expenses", addExpenses.getText().toString());
                transaction.put("date", eText.getText().toString());
                transaction.put("description", addDescription.getText().toString());

                firestore.collection("transaction")
                        .document(intent.getStringExtra("transId"))
                        .update(transaction)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent intent2 = new Intent(getApplicationContext(), ViewTransactionsActivity.class);
                        startActivity(intent2);
                    }
                });
            }
        });

        eText=(EditText) findViewById(R.id.editDate);
        eText.setInputType(InputType.TYPE_NULL);
        eText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(EditActivity.this,
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