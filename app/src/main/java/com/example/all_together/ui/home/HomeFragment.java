package com.example.all_together.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.all_together.R;
import com.example.all_together.Volunteering;
import com.example.all_together.VolunteeringAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    Button nextVolCardBtn,oldVolCardBtn;
    CardView nextVolCardView,oldVolCardView;

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

        RecyclerView recyclerViewNew = rootView.findViewById(R.id.recyclerNew);
        recyclerViewNew.setHasFixedSize(true);
        recyclerViewNew.setLayoutManager(new LinearLayoutManager(rootView.getContext(),LinearLayoutManager.VERTICAL, false));

        final List<Volunteering>volunteeringListNew = new ArrayList<>();
        volunteeringListNew.add(new Volunteering("Lidan", "Haifa", "12/05/20", "12:15", "Shopping"));
        volunteeringListNew.add(new Volunteering("Lida", "Haifa", "12/05/20", "12:15", "Shopping"));
        volunteeringListNew.add(new Volunteering("Lid", "Haifa", "12/05/20", "12:15", "Shopping"));
        volunteeringListNew.add(new Volunteering("Li", "Haifa", "12/05/20", "12:15", "Shopping"));

        VolunteeringAdapter volunteeringAdapterNew = new VolunteeringAdapter(volunteeringListNew);
        volunteeringAdapterNew.setListener(new VolunteeringAdapter.MyVolunteeringInfoListener() {
            @Override
            public void onVolunteeringClicked(int position, View view) {
                Toast.makeText(view.getContext(), "My name is:" + volunteeringListNew.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerViewNew.setAdapter(volunteeringAdapterNew);


        RecyclerView recyclerViewOld = rootView.findViewById(R.id.recyclerOld);
        recyclerViewOld.setHasFixedSize(true);
        recyclerViewOld.setLayoutManager(new LinearLayoutManager(rootView.getContext(),LinearLayoutManager.VERTICAL, false));

        List<Volunteering>volunteeringListOld = new ArrayList<>();
        volunteeringListOld.add(new Volunteering("Avi", "Tel-Aviv", "10/05/20", "12:15", "Shopping"));
        volunteeringListOld.add(new Volunteering("Avi", "Tel-Aviv", "10/05/20", "12:15", "Shopping"));
        volunteeringListOld.add(new Volunteering("Avi", "Haifa", "12/05/20", "12:15", "Shopping"));
        volunteeringListOld.add(new Volunteering("Avi", "Haifa", "12/05/20", "12:15", "Shopping"));

        VolunteeringAdapter volunteeringAdapterOld = new VolunteeringAdapter(volunteeringListOld);
        recyclerViewOld.setAdapter(volunteeringAdapterOld);

        return rootView;

    }
}
