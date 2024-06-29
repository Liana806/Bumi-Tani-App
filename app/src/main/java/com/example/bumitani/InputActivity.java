package com.example.bumitani;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class InputActivity extends AppCompatActivity {

    private ImageView productImageView;
    private Button chooseImageButton;
    private EditText productNameEditText;
    private EditText productDescEditText;
    private EditText productPriceEditText;
    private Spinner productCategorySpinner;
    private Button createProductButton;
    private Uri selectedImageUri;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        selectedImageUri = data.getData();
                        productImageView.setImageURI(selectedImageUri);
                    }
                }});

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        productImageView = findViewById(R.id.productImageView);
        chooseImageButton = findViewById(R.id.chooseImageButton);
        productNameEditText = findViewById(R.id.productNameEditText);
        productDescEditText = findViewById(R.id.productDescEditText);
        productPriceEditText = findViewById(R.id.productPriceEditText);
        productCategorySpinner = findViewById(R.id.productCategorySpinner);
        createProductButton = findViewById(R.id.createProductButton);

        //inisialisaasi fb. realtime
        databaseReference = FirebaseDatabase.getInstance().getReference("products");

        //inisialisasi fb. storage
        storageReference = FirebaseStorage.getInstance().getReference("product_images");

        //spinner
        String[] kategori = {"Cabai", "Tomat", "Bawang", "Sayur", "Buah"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, kategori);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productCategorySpinner.setAdapter(adapter);

        //event listner tombol buat pilih gbr
        chooseImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
        });

        //event listner buat produk
        createProductButton.setOnClickListener(view -> {// 1. Extract input retrieval for better readability
            String productName = productNameEditText.getText().toString();
            String productDesc = productDescEditText.getText().toString();
            String productPriceString = productPriceEditText.getText().toString();
            String productCategory = productCategorySpinner.getSelectedItem().toString();

// 2. Handle price parsing with better error handling and a default value
            double productPrice = 0.0;
            try {
                productPrice = Double.parseDouble(productPriceString);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Harga produk tidak valid", Toast.LENGTH_SHORT).show();
                return;
            }

// 3. Consolidate validation checks for conciseness
            if (productName.isEmpty() || productDesc.isEmpty() || productPrice <= 0 || productCategory.isEmpty()) {
                StringBuilder errorMessage = new StringBuilder();
                if (productName.isEmpty()) errorMessage.append("Nama produk tidak boleh kosong.\n");
                if (productDesc.isEmpty()) errorMessage.append("Deskripsi produk tidak boleh kosong.\n");
                if (productPrice <= 0) errorMessage.append("Harga produk harus lebih besar dari 0.\n");
                if (productCategory.isEmpty()) errorMessage.append("Kategori produk tidak boleh kosong.\n");

                Toast.makeText(this, errorMessage.toString().trim(), Toast.LENGTH_SHORT).show();
                return;
            }

// 4.  Check for image before uploading
            if (selectedImageUri == null) {
                Toast.makeText(this, "Pilih gambar produk", Toast.LENGTH_SHORT).show();
                return;
            }

// 5.  Upload image and save data to Firebase
            StorageReference imageRef = storageReference.child(System.currentTimeMillis() + ".jpg");
            UploadTask uploadTask = imageRef.putFile(selectedImageUri);

            double finalProductPrice = productPrice;
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();

                    String productId = databaseReference.push().getKey();
                    Product product = new Product(productId, productName, productDesc, finalProductPrice, productCategory, imageUrl);

                    databaseReference.child(productId).setValue(product)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(InputActivity.this, "Produk berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(InputActivity.this, "Gagal menyimpan produk", Toast.LENGTH_SHORT).show();
                            });
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Gambar berhasil diupload", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Gagal mengupload gambar", Toast.LENGTH_SHORT).show();
            });
        });


    }
}