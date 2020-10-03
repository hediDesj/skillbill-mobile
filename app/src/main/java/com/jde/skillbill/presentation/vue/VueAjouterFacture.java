package com.jde.skillbill.presentation.vue;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jde.skillbill.R;
import com.jde.skillbill.presentation.IContratVPAjouterFacture;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class VueAjouterFacture extends Fragment implements IContratVPAjouterFacture.IVueAjouterFacture {
    IContratVPAjouterFacture.IPresenteurAjouterFacture presenteurAjouterFacture;
    Button boutonAjouter, boutonAnnuler;
    EditText editTextMontant;
    EditText editTextTitre;
    Spinner spinnerChoix;
    Spinner spinnerChoixUtilisateursRedevables;
    CalendarView calendarView;
    EditText date;

    @Override
    public View onCreateView (LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState) {
        View racine = inflater.inflate(R.layout.frag_ajouter_facture, container, false);
        editTextTitre=racine.findViewById(R.id.edit_t_nom_activie);
         boutonAjouter= racine.findViewById(R.id.btnAjouter);
         boutonAjouter.setOnClickListener(view -> {
             presenteurAjouterFacture.ajouterFacture();
         });

         boutonAnnuler= racine.findViewById(R.id.btnAnuller);
         boutonAnnuler.setOnClickListener(view -> {
             Objects.requireNonNull(this.getActivity()).onBackPressed();
         });
         calendarView=racine.findViewById(R.id.calendarView);
         calendarView.setVisibility(View.GONE);
         date = racine.findViewById(R.id.editTextDate);

         date.setOnFocusChangeListener((view, b) -> {
             if(b) {
                 calendarView.setVisibility(View.FOCUSABLE);
             }else {
                 calendarView.setVisibility(View.GONE);
             }
         });


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                date.setText(LocalDate.of(year,month+1/*Ok google?*/,dayOfMonth).toString());
            }
        });
         editTextMontant= racine.findViewById(R.id.txt_input_montant);
         spinnerChoixUtilisateursRedevables=racine.findViewById(R.id.spinner_choix_remboursement);
         spinnerChoixUtilisateursRedevables.setAdapter( ArrayAdapter.createFromResource(this.getContext(), R.array.spinner_ajouter_facture_choix_utilisateur_pour_remboursement, R.layout.support_simple_spinner_dropdown_item));

         spinnerChoixUtilisateursRedevables.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                 date.clearFocus();
                 editTextMontant.clearFocus();
                 if(i==1){

                     presenteurAjouterFacture.montrerListeUtilisateurMontant();
                 }
             }

             @Override
             public void onNothingSelected(AdapterView<?> adapterView) {

             }
         });
         spinnerChoix =racine.findViewById(R.id.spinner_choix_payeur);
         spinnerChoix.setAdapter(ArrayAdapter.createFromResource(this.getContext(),R.array.spinner_ajouter_facture_choix, R.layout.support_simple_spinner_dropdown_item));
         spinnerChoix.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                 if(i==1){
                     presenteurAjouterFacture.montrerListeUtilisateurMontant();
                 }
             }

             @Override
             public void onNothingSelected(AdapterView<?> adapterView) {

             }
         });

        return racine;
    }

    @Override
    public void setPresenteur(IContratVPAjouterFacture.IPresenteurAjouterFacture presenteurAjouterFacture) {
        this.presenteurAjouterFacture=presenteurAjouterFacture;
    }

    @Override
    public LocalDate getDateFactureInput() throws NullPointerException, DateTimeParseException{
        return  LocalDate.parse( date.getText());
    }



    @Override
    public double getMontantFactureInput() throws NullPointerException, NumberFormatException {
        if(Double.parseDouble( editTextMontant.getText().toString())<=0){
            throw new NumberFormatException("Le montant doit etre strictement superieur a zero");// TODO string.xml
        }
        return Double.parseDouble( editTextMontant.getText().toString());
    }
    @Override
    public void afficherMessageErreurAlertDialog(String message, String titre){
        MaterialAlertDialogBuilder alertBuilder=new MaterialAlertDialogBuilder(Objects.requireNonNull(this.getContext()));
        alertBuilder.setTitle(titre);
        alertBuilder.setMessage(message);
        alertBuilder.show();
    }
    @Override
    public String getTitreInput(){
        Log.e("alll", editTextTitre.getText().toString()+"j");//TODO bub
        Log.e("alll", editTextTitre.getText().toString().trim()+"j");
        if(editTextTitre.getText().toString().trim()==""){
            Log.e("If ", editTextTitre.getText().toString());
            return null;
        }
        return editTextTitre.getText().toString();
    }


}
