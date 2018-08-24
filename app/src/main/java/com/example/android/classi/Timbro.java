package com.example.android.classi;

public class Timbro {
    public long timbroId;
    public String userEmail;
    public int numberTimbri;
    public int numberOmaggi;
    //public long updateAtOmaggi;

    public Timbro(long timbroId,String userEmail,int numberTimbri,int numberOmaggi) {
        this.timbroId = timbroId;
        this.userEmail = userEmail;
        this.numberTimbri = numberTimbri;
        this.numberOmaggi = numberOmaggi;
        //this.updateAtOmaggi = updateAtOmaggi;
    }

    @Override
    public String toString() {
        return timbroId + " - " + userEmail + " - " + Integer.toString(numberTimbri) + " - "
                + Integer.toString(numberOmaggi);
    }
}
