package com.jde.skillbill.presentation;

import com.jde.skillbill.presentation.presenteur.PresenteurVoirUnGroupe;

public interface IContratVPVoirUnGroupe {
    interface IPresenteurVoirUnGroupe{
        public double getMontantFacturePayerParUser(int posFacture);

        void requeteSupprimerFacture(int position);
    }

    //interface pour la vue du groupe
    interface IVueVoirUnGroupe{
        public void setNomGroupe(String nom);
        public void setPresenteur(PresenteurVoirUnGroupe presenteur);
    }

    //pour l'adapter d'une facture au recyclerView.
    interface IAdapterVoirUneFacture{
        public void setNomFacture(String nomActivite);
        public void setMontantFacture(double montant);
        public void setPresenteur(PresenteurVoirUnGroupe presenteur);
    }
}
