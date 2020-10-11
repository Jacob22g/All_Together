package com.example.all_together;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class PhoneLogin extends Fragment {

    public static int PHONE_NUMBER_CHARACTERS = 10;

    interface OnRegisterFragmentListener{
        void onPhoneLogin(String phoneNumber);
    }

    OnRegisterFragmentListener callback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback = (OnRegisterFragmentListener)context;
        } catch (ClassCastException ex){
            throw new ClassCastException("The activity must implement OnRegisterFragmentListener interface");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_phone_login, container, false);

        final EditText phoneEditText = rootView.findViewById(R.id.phone_register);

        Button submitOldBtn = rootView.findViewById(R.id.submit_register_help);

        submitOldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phoneNum = phoneEditText.getText().toString();
                String phoneNumber;
                if (phoneNum.equals(""))
                    phoneNumber = "";
                else
                    phoneNumber = phoneNum.substring(1);

                if(TextUtils.isEmpty(phoneNumber)){
                    phoneEditText.setError(getResources().getString(R.string.phone_required));
                    return;
                }

                if (phoneNumber.length() < PHONE_NUMBER_CHARACTERS - 1 ) {
                    phoneEditText.setError(getResources().getString(R.string.phone_number_must_be_at_least) + " " + + PHONE_NUMBER_CHARACTERS + " " + getResources().getString(R.string.numbers));
                    return;
                }

                callback.onPhoneLogin(phoneNumber);

            }
        });

        return rootView;
    }
}