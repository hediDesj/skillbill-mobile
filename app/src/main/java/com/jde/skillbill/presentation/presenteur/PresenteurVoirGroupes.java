package com.jde.skillbill.presentation.presenteur;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import com.jde.skillbill.BuildConfig;
import com.jde.skillbill.R;
import com.jde.skillbill.domaine.entites.Groupe;
import com.jde.skillbill.domaine.entites.Monnaie;
import com.jde.skillbill.domaine.entites.Utilisateur;
import com.jde.skillbill.domaine.interacteurs.GestionGroupes;
import com.jde.skillbill.domaine.interacteurs.GestionUtilisateur;
import com.jde.skillbill.domaine.interacteurs.ISourceDonnee;
import com.jde.skillbill.domaine.interacteurs.interfaces.IGestionGroupes;
import com.jde.skillbill.domaine.interacteurs.interfaces.IGestionUtilisateur;
import com.jde.skillbill.domaine.interacteurs.interfaces.SourceDonneeException;
import com.jde.skillbill.presentation.IContratVuePresenteurVoirGroupes;
import com.jde.skillbill.presentation.modele.Modele;
import com.jde.skillbill.presentation.vue.VueVoirGroupes;
import com.jde.skillbill.ui.activity.ActivityAjouterFacture;
import com.jde.skillbill.ui.activity.ActivityConnexion;
import com.jde.skillbill.ui.activity.ActivityCreerGroupe;
import com.jde.skillbill.ui.activity.ActivityModifProfil;
import com.jde.skillbill.ui.activity.ActivityVoirUnGroupe;

import java.util.List;
import java.util.Locale;


public class PresenteurVoirGroupes implements IContratVuePresenteurVoirGroupes.IPresenteurVoirGroupe {
    private Modele _modele;
    private VueVoirGroupes vueVoirGroupes;
    private Activity activity;

    private String EXTRA_ID_UTILISATEUR="com.jde.skillbill.utlisateur_identifiant";
    private String EXTRA_GROUPE_POSITION= "com.jde.skillbill.groupe_identifiant";
    private Handler handler;
    private Thread filEsclave;
    private ISourceDonnee sourceDonnee;
    private static final int MSG_GET_GROUPES=1;
    private static final int MSG_GET_FACTURE=3;
    private static final int MSG_ERREUR_CONNECTION=4;
    private static final int REQUETE_PRENDRE_PHOTO= 2;




    @SuppressLint("HandlerLeak")
    public PresenteurVoirGroupes(Modele modele, VueVoirGroupes vueVoirGroupes, Activity activity,  ISourceDonnee sourceDonnee) {
        this._modele = modele;
        this.vueVoirGroupes = vueVoirGroupes;
        this.activity=activity;
        modele.setUtilisateurConnecte((Utilisateur) activity.getIntent().getSerializableExtra(EXTRA_ID_UTILISATEUR));
        this.sourceDonnee = sourceDonnee;

        handler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                filEsclave = null;
                vueVoirGroupes.fermerProgressBar();
                if(msg.what == MSG_GET_GROUPES){
                    _modele.setSoldeParPosition(((Modele) msg.obj).getSoldeParPosition());

                    vueVoirGroupes.rafraichir();

                }
                if(msg.what == MSG_GET_FACTURE){

                }
                if(msg.what == MSG_ERREUR_CONNECTION){
                    Toast.makeText(activity, R.string.pas_de_connection_internet, Toast.LENGTH_LONG);
                }

            }
        };

        chargerGroupes();
    }


    @Override
    public void chargerGroupes(){
        IGestionUtilisateur gestionUtilisateur = new GestionUtilisateur(sourceDonnee);
        IGestionGroupes gestionGroupe = new GestionGroupes(sourceDonnee);
        filEsclave = new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg;
                try{
                    List<Groupe> groupes= gestionUtilisateur.trouverGroupesAbonne(_modele.getUtilisateurConnecte());
                    _modele.setGroupesAbonnesUtilisateurConnecte(groupes);

                    if(groupes!=null){
                        int position= 0;
                        int taille = groupes.size();
                        String[] textes = new String[taille];
                        while (position<taille){

                            try {
                                String soldeMonnaie = gestionGroupe.getSoldeParUtilisateurEtGroupe(_modele.getUtilisateurConnecte(), _modele.getListGroupeAbonneUtilisateurConnecte().get(position));
                                double solde = Double.parseDouble(soldeMonnaie.split("--")[0]);

                                String monnaieGroupe = soldeMonnaie.split("--")[1];
                                if (solde == 0) {
                                    textes[position] = activity.getResources().getString(R.string.solde_utilisateur_nul);

                                } else if (solde < 0) {
                                    textes[position] = activity.getResources().getString(R.string.solde_utilisateur_debiteur) + " " + String.format( "%.02f",Math.abs(solde) ) +" "+monnaieGroupe;

                                } else {
                                    textes[position] = activity.getResources().getString(R.string.solde_utilisateur_crediteur) + " " + String.format( "%.02f",Math.abs(solde) )+" "+monnaieGroupe;

                                    if(!_modele.getUtilisateurConnecte().getMonnaieUsuelle().name().equals(monnaieGroupe)){
                                        double montantConvertiCAD = solde*Monnaie.valueOf(monnaieGroupe).getTauxDevise();
                                        double montantConverti = montantConvertiCAD*_modele.getUtilisateurConnecte().getMonnaieUsuelle().getTauxCad();
                                        String  montantStr = String.format(Locale.CANADA,"%.2f",montantConverti)+ _modele.getUtilisateurConnecte().getMonnaieUsuelle().name();
                                        textes[position]+="\n"+montantStr+" "+activity.getString(R.string.dans_votre_monnaie);
                                    }
                                }


                            } catch (NullPointerException e) { //TODO vraie exception
                                textes[position] =  activity.getResources().getString(R.string.pas_de_facture_dans_le_groupe);

                            }
                            position++;

                        }

                        _modele.setSoldeParPosition(textes);
                    }

                    msg = handler.obtainMessage(MSG_GET_GROUPES, _modele);
                }catch (SourceDonneeException e ){
                    msg = handler.obtainMessage(MSG_ERREUR_CONNECTION);
                }

                handler.sendMessage(msg);

            }
        });
        filEsclave.start();
    }

    @Override
    public List<Groupe> getGroupeAbonnes() {

        return _modele.getListGroupeAbonneUtilisateurConnecte();
    }

    @Override
    public void commencerVoirUnGroupeActivite(int position) {
        Intent intent = new Intent(activity, ActivityVoirUnGroupe.class);
        intent.putExtra(EXTRA_GROUPE_POSITION, _modele.getListGroupeAbonneUtilisateurConnecte().get(position));
        intent.putExtra(EXTRA_ID_UTILISATEUR, _modele.getUtilisateurConnecte());
        activity.startActivity(intent);
    }

    @Override
    public String getMessageSoldeParPosition(int position){
        if(_modele ==null || _modele.getSoldeParPosition()==null || _modele.getSoldeParPosition().length ==0 || position>= _modele.getSoldeParPosition().length) return "";
        return _modele.getSoldeParPosition()[position];
    }

    @Override
    public String getNomGroupe(int position) {
        if (BuildConfig.DEBUG && position < 0) {
            throw new AssertionError("Assertion failed");
        }
        return _modele.getListGroupeAbonneUtilisateurConnecte().get(position).getNomGroupe();
    }

    @Override
    public void commencerAjouterFactureActivite(int position) {
        Intent intent = new Intent(activity, ActivityAjouterFacture.class);
        intent.putExtra(EXTRA_GROUPE_POSITION, _modele.getListGroupeAbonneUtilisateurConnecte().get(position));
        intent.putExtra(EXTRA_ID_UTILISATEUR, _modele.getUtilisateurConnecte());
        activity.startActivity(intent);
    }

    @Override
    public void redirigerModifCompte() {
        Intent intent = new Intent(activity, ActivityModifProfil.class);
        intent.putExtra(EXTRA_ID_UTILISATEUR, _modele.getUtilisateurConnecte());
        activity.startActivityForResult(intent, 1);
    }


    @Override
    public void commencerCreerGroupeActivite() {
        Intent intent = new Intent(activity, ActivityCreerGroupe.class);
        intent.putExtra(EXTRA_ID_UTILISATEUR, _modele.getUtilisateurConnecte());
        activity.startActivity(intent);
    }

    @Override
    public void commencerPrendrePhotoFacture(int position){
        //TODO remove
    }

    @Override
    public String getNomUserConnecte(){
        return _modele.getUtilisateurConnecte().getNom();
    }

    @Override
    public void deconnexion(){
        Intent intent = new Intent(activity, ActivityConnexion.class);
        activity.startActivity(intent);
        this.activity.finish();
    }


}
