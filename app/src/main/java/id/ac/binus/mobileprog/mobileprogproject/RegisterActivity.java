package id.ac.binus.mobileprog.mobileprogproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText editEmail, editPassword;
    Button btnReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView gotoLogin;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        editEmail = findViewById(R.id.email);
        editPassword = findViewById(R.id.password);
        btnReg = findViewById(R.id.registerBtn);
        progressBar = findViewById(R.id.progressBar);
        gotoLogin = findViewById(R.id.gotoLogin);

        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                String email, password;
                email = String.valueOf(editEmail.getText());
                password = String.valueOf(editPassword.getText());

                progressBar.setVisibility(View.VISIBLE);

                if (TextUtils.isEmpty(email))
                {
                    Toast.makeText(RegisterActivity.this, "Email is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email))
                {
                    Toast.makeText(RegisterActivity.this, "Password is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful())
                                {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), ViewTransactionsActivity.class);
                                    startActivity(intent);
                                }
                                else
                                {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}