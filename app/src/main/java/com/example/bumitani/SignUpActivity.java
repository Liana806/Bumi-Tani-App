package com.example.bumitani;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextMasukanNamaLengkap, editTextMasukanEmail, editTextMasukanTelepon, editTextPassword, editTextRePassword;
    private Button buttonDaftar;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextMasukanNamaLengkap =findViewById(R.id.editTextMasukanNamaLengkap);
        editTextMasukanEmail =findViewById(R.id.editTextMasukanEmail);
        editTextMasukanTelepon =findViewById(R.id.editTextMasukanTelepon);
        editTextPassword =findViewById(R.id.editTextPassword);
        editTextRePassword =findViewById(R.id.editTextRePassword);
        buttonDaftar =findViewById(R.id.buttonDaftar);

        auth = FirebaseAuth.getInstance();

        buttonDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String namaLengkap = editTextMasukanNamaLengkap.getText().toString();
                String email = editTextMasukanEmail.getText().toString();
                String telepon = editTextMasukanNamaLengkap.getText().toString();
                String password = editTextPassword.getText().toString();
                String repassword = editTextRePassword.getText().toString();
                String btndaftar = buttonDaftar.getText().toString();

                if (!(namaLengkap.isEmpty() || email.isEmpty() || telepon.isEmpty() || password.isEmpty() || repassword.isEmpty())) {

                    if (password.length() >6 ){
                        if(repassword.equals(password)){

                            auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {

                                            if (task.isSuccessful()){
                                                auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            Toast.makeText(getApplicationContext(),"Daftar Berhasil. Silahkan Cek Email Anda!", Toast.LENGTH_SHORT).show();

                                                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));

                                                        }else{
                                                            Toast.makeText(getApplicationContext(),"Verifikasi Gagal Di Kirim", Toast.LENGTH_SHORT).show();
                                                        }


                                                    }
                                                });
                                            }else {
                                                Toast.makeText(getApplicationContext(),"Daftar Gagal", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                        }else {
                            editTextRePassword.setError("Password Tidak Sama");
                        }
                    }else {
                        editTextPassword.setError("Password Harus Lebih Dari 6 Karakter");
                    }


                } else {
                    Toast.makeText(getApplicationContext(),"Ada Data Yang Masih Kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}