package com.example.all_together.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.all_together.R;

public class HomeFragment extends Fragment {

    Button nextVolCardBtn,oldVolCardBtn;
    CardView nextVolCardView,oldVolCardView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_home,container,false);

        nextVolCardView = rootView.findViewById(R.id.cardNextVol);
        oldVolCardView =  rootView.findViewById(R.id.cardOldVol);

        nextVolCardBtn = rootView.findViewById(R.id.next_vol_card_btn);
        nextVolCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextVolCardBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                oldVolCardBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
                oldVolCardView.setVisibility(View.GONE);
                nextVolCardView.setVisibility(View.VISIBLE);
            }
        });
        oldVolCardBtn = rootView.findViewById(R.id.old_vol_card_btn);
        oldVolCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextVolCardBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
                oldVolCardBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                nextVolCardView.setVisibility(View.GONE);
                oldVolCardView.setVisibility(View.VISIBLE);

            }
        });

        return rootView;

    }
}
