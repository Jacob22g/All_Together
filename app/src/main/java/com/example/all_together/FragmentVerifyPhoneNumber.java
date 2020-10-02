package com.example.all_together;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;


public class FragmentVerifyPhoneNumber extends Fragment {

    private static final String ARGS_PHONE_NUMBER = "args_phone_number";

    Button confirmBtn;
    EditText verifyCodeEt;
    ProgressBar progressBar;

    private String phoneNumber;
    String verificationCodeBySystem;
    PhoneAuthProvider.ForceResendingToken mResendToken;


    public static FragmentVerifyPhoneNumber newInstance(String phoneNumber){
        FragmentVerifyPhoneNumber verifyFragment = new FragmentVerifyPhoneNumber();
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_PHONE_NUMBER, phoneNumber);
        verifyFragment.setArguments(bundle);
        return verifyFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_verify_phone_number, container, false);

        if (getArguments()!= null) {
            phoneNumber = getArguments().getString(ARGS_PHONE_NUMBER);
            Toast.makeText(getContext(), phoneNumber, Toast.LENGTH_SHORT).show();
        }

        confirmBtn = rootView.findViewById(R.id.confirm_verify_phone);
        progressBar = rootView.findViewById(R.id.progress_bar_verify);
        verifyCodeEt = rootView.findViewById(R.id.verify_code);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String code = verifyCodeEt.getText().toString();
                verifyCode(code);
            }
        });

        //Toast.makeText(getContext(), "Your Phone Number: " + phoneNumber, Toast.LENGTH_SHORT).show();

        sandVerificationCodeToUser(phoneNumber);

        return rootView;
    }

    private void sandVerificationCodeToUser(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+972" + phoneNumber,        // Phone number to verify
                120,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallbacks);// OnVerificationStateChangedCallbacks
    }

    private void verifyCode(String codeByUser) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem,codeByUser);
        signInByCredential(credential);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mResendToken = forceResendingToken;
            verificationCodeBySystem = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            String code = credential.getSmsCode();
            if(code!= null){
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(getContext(), "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("phone","Error " + e.getMessage());
        }
    };

    private void signInByCredential(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(getActivity(),
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
//                                    FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(getContext(), "Goooood", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(),OldUserActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        }else {

                            Toast.makeText(getContext(), "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}