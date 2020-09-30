package com.jde.skillbill.presentation.vue.recyclerview_adapters;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;
import com.jde.skillbill.R;
import com.jde.skillbill.presentation.presenteur.PresenteurVoirGroupes;

public class RVVoirGroupesAdapter extends RecyclerView.Adapter {
    PresenteurVoirGroupes presenteurVoirGroupes;

    public RVVoirGroupesAdapter(PresenteurVoirGroupes presenteurVoirGroupes) {
        this.presenteurVoirGroupes = presenteurVoirGroupes;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        LinearLayout linearLayout= (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.vue_rv_groupes_item, parent, false);
        return new  RecyclerView.ViewHolder(linearLayout){};
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        (holder.itemView.findViewById(R.id.rv_groupe_item_button)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View bouton){
                presenteurVoirGroupes.commencerVoirGroupeActivite(position);
            }
        });
        ((Button)holder.itemView.findViewById(R.id.rv_groupe_item_button)).setText(presenteurVoirGroupes.getNomGroupe(position));
    }


    @Override
    public int getItemCount() {
        if (presenteurVoirGroupes==null) return 0;
        return presenteurVoirGroupes.getGroupeAbonnes().size();
    }
}