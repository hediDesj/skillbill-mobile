package com.jde.skillbill.presentation.vue;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.jde.skillbill.R;
import com.jde.skillbill.presentation.IContratVuePresenteurVoirUnGroupe;
import com.jde.skillbill.presentation.presenteur.PresenteurVoirUnGroupe;
import com.jde.skillbill.presentation.vue.recyclerview_adapters.RvVoirFactureAdapter;

import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class VueVoirUnGroupe extends Fragment implements IContratVuePresenteurVoirUnGroupe.IVueVoirUnGroupe {


   private PresenteurVoirUnGroupe _presenteur;
   private TextView tvNomGroupe;
   private TabLayout tabLayout;
   private TextView detailMembres;
   private Button ajouterMembre;

    RecyclerView rvFactures;
    RvVoirFactureAdapter rvFacturesAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View racine = inflater.inflate(R.layout.frag_voir_un_groupe, container, false);
        ajouterMembre = racine.findViewById(R.id.btnAjouter);
        detailMembres = racine.findViewById(R.id.detailMembres);
        detailMembres.setVisibility(View.INVISIBLE);
        ajouterMembre.setVisibility(View.INVISIBLE);
        tvNomGroupe=racine.findViewById(R.id.tvNomGroupe);
        tabLayout=racine.findViewById(R.id.tabLayoutFactureMembres);

        rvFactures=racine.findViewById(R.id.rvFactures);
        rvFacturesAdapter=new RvVoirFactureAdapter(_presenteur);
        rvFactures.setAdapter(rvFacturesAdapter);
        rvFactures.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFactures.addItemDecoration(new DividerItemDecoration(rvFactures.getContext(), DividerItemDecoration.VERTICAL));

        //pour le swipe to delete
        ItemTouchHelper itemTouchHelper= new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvFactures);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==0){
                    rvFactures.setVisibility(View.VISIBLE);
                }
                else if(tab.getPosition()==1){
                    detailMembres.setVisibility(View.VISIBLE);
                    ajouterMembre.setVisibility(View.VISIBLE);
                    if(_presenteur.isGroupeSolo()){
                        detailMembres.setText("Vous n'avez aucun autre membres dans ce groupe, cliquez sur Ajouter pour en ajouter");
                    }else{
                        detailMembres.setText("Vous partagez vos factures avec "+_presenteur.getMembresGroupe());
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if(tab.getPosition()==0) {
                  rvFactures.setVisibility(View.INVISIBLE);
                }
                else if(tab.getPosition()==1){
                    detailMembres.setVisibility(View.INVISIBLE);
                    ajouterMembre.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        ajouterMembre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(racine.getContext());
                builder.setTitle("Entrez le courriel du membre à ajouter");
                final View customLayout = getLayoutInflater().inflate(R.layout.alert_dialog_ajouter_membre, null);
                builder.setView(customLayout);
                builder.setPositiveButton("Ajouter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        EditText champsCourriel = customLayout.findViewById(R.id.editTextTextEmailAddressAjouterMembre);
                        int code = _presenteur.ajouterUtilisateurAuGroupe(champsCourriel.getText().toString());
                        if(_presenteur.AJOUT_OK==code){
                            detailMembres.setText("Vous partagez vos factures avec "+_presenteur.getMembresGroupe());
                        }else if(_presenteur.EMAIL_INCONNU==code){
                            Toast.makeText(racine.getContext(), "Email inconnu, vous pouvez l'inviter ", Toast.LENGTH_LONG).show();
                        }else if(_presenteur.ERREUR_ACCES==code){
                            Toast.makeText(racine.getContext(), "Vous avez dejà ajouté cet utilisateur ", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                builder.setNeutralButton("Inviter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText champsCourriel = customLayout.findViewById(R.id.editTextTextEmailAddressAjouterMembre);
                        _presenteur.envoyerCourriel(champsCourriel.getText().toString());

                    }
                });

                builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }

        });
        return racine;
    }

    @Override

    public void setPresenteur(IContratVuePresenteurVoirUnGroupe.IPresenteurVoirUnGroupe presenteur) {
        _presenteur = (PresenteurVoirUnGroupe) presenteur;
    }

   // @Override
    public void setNomGroupe(String nom) {
        tvNomGroupe.setText(nom);
    }


    /**
     * Rafraichit la vue
     */

    public void rafraichir() {
        if(rvFacturesAdapter!=null) rvFacturesAdapter.notifyDataSetChanged();
    }

    /**
     * crée le swipe gauche pour le rv
     */
    final ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

        /**
         * Cette méthode n'Est pa utilisée pour l'intsant
         * @param recyclerView le rv des factures
         * @param viewHolder
         * @param target
         * @return null
         */
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        /**
         * Ce qu'il se passe quand on swipe un element du rv selon une direction
         * @param viewHolder le viewHolder
         * @param direction la dr
         */
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();
            Snackbar.make(rvFactures, _presenteur.getFacturesGroupe().get(position).getLibelle(), Snackbar.LENGTH_LONG)
                    .setAction("Supprimer la facture", new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            _presenteur.requeteSupprimerFacture(position);
                        }
                    }).setActionTextColor(Color.RED).setBackgroundTint(Color.LTGRAY).show();
                    rvFacturesAdapter.notifyDataSetChanged();
        }

        /**
         * Décore le swipe vers la droite, y met la couleure ansi que l'icone
         * J'ai pris cette méthode d'une librairie sur git, voir le fichier gradle.build de l'app
         * @param c
         * @param recyclerView
         * @param viewHolder
         * @param dX
         * @param dY
         * @param actionState
         * @param isCurrentlyActive
         */
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.error))
                    .addActionIcon(R.drawable.ic_remove_circle_black_24dp)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

}