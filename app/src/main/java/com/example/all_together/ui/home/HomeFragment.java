package com.example.all_together.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.all_together.R;
import com.example.all_together.model.Volunteering;
import com.example.all_together.adapter.HomeVolunteeringAdapter;

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
                nextVolCardBtn.setBackground(getResources().getDrawable(R.drawable.background_button_dark));
                oldVolCardBtn.setBackground(getResources().getDrawable(R.drawable.background_button_light));
                oldVolCardView.setVisibility(View.GONE);
                nextVolCardView.setVisibility(View.VISIBLE);
            }
        });
        oldVolCardBtn = rootView.findViewById(R.id.old_vol_card_btn);
        oldVolCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextVolCardBtn.setBackground(getResources().getDrawable(R.drawable.background_button_light));
                oldVolCardBtn.setBackground(getResources().getDrawable(R.drawable.background_button_dark));
                nextVolCardView.setVisibility(View.GONE);
                oldVolCardView.setVisibility(View.VISIBLE);

            }
        });

        RecyclerView recyclerViewNew = rootView.findViewById(R.id.recyclerNew);
        recyclerViewNew.setHasFixedSize(true);
        recyclerViewNew.setLayoutManager(new LinearLayoutManager(rootView.getContext(),LinearLayoutManager.VERTICAL, false));

        final List<Volunteering>volunteeringListNew = new ArrayList<>();
        volunteeringListNew.add(new Volunteering("Lidan", "Haifa","st", "12/05/20", "12:15", "Shopping","bla bli blopy","1"));
        volunteeringListNew.add(new Volunteering("Lida", "Haifa","st", "12/05/20", "12:15", "Shopping","bla bli blopy","1"));
        volunteeringListNew.add(new Volunteering("Lid", "Haifa","st", "12/05/20", "12:15", "Shopping","bla bli blopy","1"));
        volunteeringListNew.add(new Volunteering("Li", "Haifa","st", "12/05/20", "12:15", "Shopping","bla bli blopy","1"));

        HomeVolunteeringAdapter volunteeringAdapterNew = new HomeVolunteeringAdapter(volunteeringListNew);
        volunteeringAdapterNew.setListener(new HomeVolunteeringAdapter.MyVolunteeringInfoListener() {
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
        volunteeringListOld.add(new Volunteering("Avi", "Tel-Aviv","st", "10/05/20", "12:15", "Shopping","bla bli blopy","1"));
        volunteeringListOld.add(new Volunteering("Avi", "Tel-Aviv","st", "10/05/20", "12:15", "Shopping","bla bli blopy","1"));
        volunteeringListOld.add(new Volunteering("Avi", "Haifa","st", "12/05/20", "12:15", "Shopping","bla bli blopy","1"));
        volunteeringListOld.add(new Volunteering("Avi", "Haifa","st", "12/05/20", "12:15", "Shopping","bla bli blopy","1"));

        HomeVolunteeringAdapter volunteeringAdapterOld = new HomeVolunteeringAdapter(volunteeringListOld);
        recyclerViewOld.setAdapter(volunteeringAdapterOld);

        return rootView;

    }
}
