package com.jde.skillbill.presentation;

import java.time.DateTimeException;
import java.time.LocalDate;

public interface IContratVPAjouterFacture {

    interface IPresenteurAjouterFacture{

        void montrerListeUtilisateurMontant();

        void ajouterFacture();
    }

    interface IVueAjouterFacture{

        void setPresenteur(IPresenteurAjouterFacture presenteurAjouterFacture);

        LocalDate getDateFactureInput() throws NullPointerException, DateTimeException;

        double getMontantFactureInput();


        void afficherMessageErreurAlertDialog(String message, String titre);

        String getTitreInput();
    }
}
