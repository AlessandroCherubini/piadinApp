package com.example.ale.piadinapp.classi;

import android.os.Parcel;
import android.os.Parcelable;

public class FasciaOraria implements Parcelable {

    private int idFascia;
    private String inizioFascia;
    private String fineFascia;
    private boolean isOccupata;
    private int coloreBadge;

    public FasciaOraria(int idFascia, String inizioFascia, String fineFascia, boolean isOccupata, int coloreBadge){
        this.idFascia = idFascia;
        this.inizioFascia = inizioFascia;
        this.fineFascia = fineFascia;
        this.isOccupata = isOccupata;
        this.coloreBadge = coloreBadge;
    }

    public int getIdFascia() {
        return idFascia;
    }

    public void setIdFascia(int idFascia) {
        this.idFascia = idFascia;
    }

    public String getInizioFascia() {
        return inizioFascia;
    }

    public void setInizioFascia(String inizioFascia) {
        this.inizioFascia = inizioFascia;
    }

    public String getFineFascia() {
        return fineFascia;
    }

    public void setFineFascia(String fineFascia) {
        this.fineFascia = fineFascia;
    }

    public boolean isOccupata() {
        return isOccupata;
    }

    public void setOccupata(boolean occupata) {
        isOccupata = occupata;
    }

    public int getColoreBadge() {
        return coloreBadge;
    }

    public void setColoreBadge(int coloreBadge) {
        this.coloreBadge = coloreBadge;
    }

    public String toString(){
        return idFascia + " " + inizioFascia + " " + fineFascia + " " + isOccupata + " " + coloreBadge;
    }

    /* Per il Parcelable */
    public FasciaOraria(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<FasciaOraria> CREATOR = new Parcelable.Creator<FasciaOraria>() {
        public FasciaOraria createFromParcel(Parcel in) {
            return new FasciaOraria(in);
        }

        public FasciaOraria[] newArray(int size) {
            return new FasciaOraria[size];
        }

    };

    public void readFromParcel(Parcel in) {
        idFascia = in.readInt();
        inizioFascia = in.readString();
        fineFascia = in.readString();
        isOccupata = (Boolean) in.readValue(null);
        coloreBadge = in.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idFascia);
        dest.writeString(inizioFascia);
        dest.writeString(fineFascia);
        dest.writeValue(isOccupata);
        dest.writeInt(coloreBadge);
    }
}
