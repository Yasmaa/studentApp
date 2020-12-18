package com.example.finalapp.user.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finalapp.R;
import com.example.finalapp.Reclamation.Reclamation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AjouterReclamationFragment extends Fragment {

    private final int REQUEST_IMAGE_CAPTURE = 22;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    private RadioGroup radioGroup1;
    private RadioGroup radioGroup2;
    private Button button1;
    private ImageView imageView;
    private EditText editText;
    private Button button2;
    private String image = new String();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_ajouter_reclamation, container, false);

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        radioGroup1 =  view.findViewById(R.id.radioGroup1);
        radioGroup2 =  view.findViewById(R.id.radioGroup2);
        button1 = view.findViewById(R.id.button1);
        imageView = view.findViewById(R.id.imageView);
        editText = view.findViewById(R.id.editText);
        button2 = view.findViewById(R.id.button2);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image = new String();
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(image.isEmpty()) {
                    button1.setError("Image required.");
                    return;
                }
                if(editText.getText().toString().isEmpty()) {
                    editText.setError("Details required.");
                    editText.requestFocus();
                    return;
                }
                String recId = myRef.child("claim").push().getKey();
                String uid = mAuth.getCurrentUser().getUid();

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy 'à' HH:mm");
                String date = sdf.format(new Date());

                RadioButton selectedRadio1 = view.findViewById(radioGroup1.getCheckedRadioButtonId());
                String categorie = selectedRadio1.getText().toString();
                RadioButton selectedRadio2 = view.findViewById(radioGroup2.getCheckedRadioButtonId());
                String type = selectedRadio2.getText().toString();
                String details = editText.getText().toString();
                Reclamation rec = new Reclamation(recId, uid, null, date, categorie, type,"Non traitée", details);
                myRef.child("claim").child(recId).setValue(rec);

                DatabaseReference ref = myRef.child("image").child(recId);
                ref.setValue(image);
                Toast.makeText(getActivity(), "Claim sent", Toast.LENGTH_LONG).show();
                returnToUserFragment();
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle extras = data.getExtras();
        if(extras != null) {
            button1.setError(null);
            Bitmap srcBmp = (Bitmap) extras.get("data");
            Bitmap dstBmp;

            if (srcBmp.getWidth() >= srcBmp.getHeight()){
                dstBmp = Bitmap.createBitmap(
                        srcBmp,
                        srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
                        0,
                        srcBmp.getHeight(),
                        srcBmp.getHeight()
                );

            }else{
                dstBmp = Bitmap.createBitmap(
                        srcBmp,
                        0,
                        srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                        srcBmp.getWidth(),
                        srcBmp.getWidth()
                );
            }

            encodeBitmapAndSaveToFirebase(dstBmp);
        }
    }

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        image = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    private  void returnToUserFragment() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserFragment(), "MY_FRAGMENT").commit();
    }
}
