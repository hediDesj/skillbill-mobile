package com.jde.skillbill.presentation;

import com.jde.skillbill.domaine.entites.Monnaie;

public interface IContratVPModifProfil {
    interface VueModifProfil {
        void setPresenteur(PresenteurModifProfil presenuteur);

        String getNouveauNom();
        String getNouveauEmail();
        String getNouveauMdp();

        String getMdpCourrant();

        Monnaie getNouvelleMonnaie();

        void setNomUser(String nom);
        void setMonnaieUser(String monnaieStr);
        void setEmailUser(String nom);

        void setMdpUser(String mdp);

        boolean tousLesChampsValides();

        void activerDescativerBtn();
    }

    interface PresenteurModifProfil {

        void modifierProfil();

        String getEmailUserConnecte();
        String getNomUserConnecte();
        Monnaie  getMonnaieConnecte();
        String getMdpUserConnecte();
    }
}
