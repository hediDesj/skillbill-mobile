package com.jde.skillbill.presentation.presenteur;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.jde.skillbill.domaine.entites.Utilisateur;
import com.jde.skillbill.domaine.interacteurs.DataSourceUsers;
import com.jde.skillbill.domaine.interacteurs.GestionUtilisateur;
import com.jde.skillbill.presentation.IContratVPConnexion;
import com.jde.skillbill.presentation.modele.Modele;
import com.jde.skillbill.presentation.vue.VueConnexion;
import com.jde.skillbill.ui.activity.ActivityCreerCompte;

public class PresenteurConnexion implements IContratVPConnexion.IPresenteurConnexion {

    private VueConnexion _vueConnexion;
    private Modele _modele;
    private DataSourceUsers _dataSource;
    private Activity _activite;

    public PresenteurConnexion(Activity activite,Modele modele, VueConnexion vueConnexion) {
        _activite=activite;
        _modele = modele;
        _vueConnexion= vueConnexion;
    }

    public void setDataSource(DataSourceUsers dataSource) {
        _dataSource = dataSource;
    }

    @Override
    public void tenterConnexion(String email, String mdp){
    GestionUtilisateur gestionUtilisateur= new GestionUtilisateur(_dataSource);
    gestionUtilisateur.setSource(_dataSource);
    Utilisateur utilisateurConnecter= gestionUtilisateur.tenterConnexion(email, mdp);
    if (utilisateurConnecter!=null){
        //TODO rediriger vers le menu principal a partir de ses informations
        //Pour l'instant on dis qu'il a ete connecter avec succes.
        _modele.setUtilisateurConnecte(utilisateurConnecter);
        _vueConnexion.afficherMsgConnecter(_modele.getUtilisateurConnecte().getCourriel(), _modele.getUtilisateurConnecte().getNom());
        }

    else {
        _vueConnexion.afficherMsgErreur();
    }


    }

    @Override
    public void allerInscription() {
        Intent intentInscription = new Intent(_activite, ActivityCreerCompte.class);
        _activite.startActivity(intentInscription);
    }
}