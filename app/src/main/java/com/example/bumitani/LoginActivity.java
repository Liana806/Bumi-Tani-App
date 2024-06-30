package com.example.bumitani;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.facebook.CallbackManager;
import com.facebook.FacebookActivity;
import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;
import com.facebook.FacebookException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private TextView textViewBuatAkun;
    private Button buttonLogin;
    private ImageView facebookLoginButton;
    private static final String EMAIL = "email";
    private EditText editTextEmail, editTextPassword;

    private FirebaseAuth auth;
    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        facebookLoginButton = findViewById(R.id.facebookLoginButton);
        callbackManager = CallbackManager.Factory.create();

        auth = FirebaseAuth.getInstance();
        textViewBuatAkun = findViewById(R.id.textViewBuatAkun);

        textViewBuatAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivities(new Intent[]{intent});
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            if (auth.getCurrentUser().isEmailVerified()){
                                Toast.makeText(getApplicationContext(),"Login Berhasil!", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                            }else {
                                editTextEmail.setError("Verifikasi Email Anda Terlebih Dahulu!");
                            }
                        }else {
                            Toast.makeText(getApplicationContext(),"Login Gagal!!!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,FacebookLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });




    }
}