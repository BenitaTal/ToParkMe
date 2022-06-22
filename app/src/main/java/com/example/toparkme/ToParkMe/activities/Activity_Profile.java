package com.example.toparkme.ToParkMe.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.toparkme.R;
import com.example.toparkme.ToParkMe.classes.AppManager;
import com.example.toparkme.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Activity_Profile extends AppCompatActivity {

    private MaterialButton profile_back_btn;
    private MaterialButton profile_nickName_btn;
    private MaterialButton profile_name_btn;
    private MaterialButton profile_photo_btn;
    private ShapeableImageView profile_photo_iv;

    private Intent intent;
    private AppManager manager;
    private FirebaseUser fUser;
    private DatabaseReference reference;
    private String userID;

    private ActivityProfileBinding binding;
    private Uri imageUri;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        getCurrentUsersData();

        setContentView(binding.parkIMGProfilePhoto.getRootView());

        findViews();

        profile_back_btn.setOnClickListener(profileBackToHomeBtn);
        profile_nickName_btn.setOnClickListener(profileChangeNickNameBtn);
        profile_name_btn.setOnClickListener(profileChangeUserNameBtn);
        profile_photo_btn.setOnClickListener(profileChangePhotoBtn);
        profile_photo_iv.setOnClickListener(profileChangePhotoIv);


    }

    public void getCurrentUsersData(){
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = fUser.getUid();



        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                manager = snapshot.getValue(AppManager.class);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Activity_Profile.this, "Something wrong happened!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private View.OnClickListener profileChangeNickNameBtn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
            alert.setTitle("Change Your NickName:");
            final EditText input = new EditText(view.getContext());
            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    manager.getChat().setNickName(input.getText().toString());
                    saveData();
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });
            alert.show();

        }
    };

    private View.OnClickListener profileChangeUserNameBtn = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                alert.setTitle("Change Your Name:");
                final EditText input = new EditText(view.getContext());
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        manager.getPerson().setName(input.getText().toString());
                        saveData();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
                alert.show();

            }
        };

    public void saveData(){
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(manager);
    }

    private View.OnClickListener profileBackToHomeBtn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            returnToHomeActivity();
        }
    };

    private View.OnClickListener profileChangePhotoBtn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            uploadImage();

        }
    };



    private View.OnClickListener profileChangePhotoIv = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            binding.parkIMGProfilePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage();
                }
            });
        }
    };
    private void setImageToProfile(){
        //
        profile_photo_btn.setVisibility(View.INVISIBLE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Waiting for Image... ");
        progressDialog.show();

        profile_photo_iv = (ShapeableImageView)findViewById(R.id.park_IMG_profile_photo);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("images/").child(userID);


        final long ONE_MEGABYTE = 1024 * 1024;
        //download file as a byte array

        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profile_photo_iv.setImageBitmap(bitmap);

                if (progressDialog.isShowing())
                    progressDialog.dismiss();



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

            }
        });

    }


    private void uploadImage() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image... ");
        progressDialog.show();

        String fileName = userID;
        storageReference = FirebaseStorage.getInstance().getReference("images/" + fileName);

        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        binding.parkIMGProfilePhoto.setImageURI(null);
                        Toast.makeText(Activity_Profile.this, "Successfully Uploaded! ", Toast.LENGTH_LONG).show();


                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        setImageToProfile();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                Toast.makeText(Activity_Profile.this, "Failed To Upload! ", Toast.LENGTH_LONG).show();

            }
        });

    }

    private void selectImage() {
        intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == 100) && (data != null) && (data.getData() != null)){
            imageUri = data.getData();
            binding.parkIMGProfilePhoto.setImageURI(imageUri);
            //
            profile_photo_btn.setVisibility(View.VISIBLE);
        }
    }

    private void returnToHomeActivity() {
        intent = new Intent(this, Activity_Home.class);
        startActivity(intent);
        finish();
    }


    private void findViews() {
        profile_back_btn= findViewById(R.id.park_Btn_profile_back);
        profile_nickName_btn = findViewById(R.id.park_Btn_profile_change_nickName);
        profile_photo_btn = findViewById(R.id.park_Btn_profile_import_photo);
        profile_name_btn = findViewById(R.id.park_Btn_profile_change_name);
        setImageToProfile();
    }

}
