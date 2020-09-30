package com.jde.skillbill.presentation.vue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jde.skillbill.R;
import com.jde.skillbill.presentation.IContratVuePresenteurVoirGroupes;
import com.jde.skillbill.presentation.presenteur.PresenteurVoirGroupes;
import com.jde.skillbill.presentation.vue.recyclerview_adapters.RVVoirGroupesAdapter;

public class VueVoirGroupes extends Fragment implements IContratVuePresenteurVoirGroupes.IVueVoirGroupes {

    private PresenteurVoirGroupes presenteurVoirGroupes;

    FloatingActionButton buttonCommencerActiviteCreerGroupe;
    RecyclerView rvMesGroupes;

    @Override
    public View onCreateView (LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState) {
        View racine = inflater.inflate(R.layout.frag_voir_groupes, container, false);
        buttonCommencerActiviteCreerGroupe=racine.findViewById(R.id.floatingActionButtonAjouterGroupe);
        rvMesGroupes=racine.findViewById(R.id.rvMesGroupes);
        rvMesGroupes.setAdapter(new RVVoirGroupesAdapter(presenteurVoirGroupes));
        rvMesGroupes.setLayoutManager(new LinearLayoutManager(getContext()));


        buttonCommencerActiviteCreerGroupe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               presenteurVoirGroupes.commencerCreerGroupeActivite();
            }
        });

        return racine;
    }

    @Override
    public void setPresenteur(PresenteurVoirGroupes presenteurVoirGroupes){
        this.presenteurVoirGroupes=presenteurVoirGroupes;
    }

    @Override
    public int getPosition() {
        return 0;
    }
}