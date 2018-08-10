package com.example.ale.piadinapp.classi;

public class Timbro {
    public long timbroId;
    public String userEmail;
    public int numberTimbri;
    public int numberOmaggi;

    public Timbro(long timbroId,String userEmail,int numberTimbri,int numberOmaggi)
    {
        this.timbroId = timbroId;
        this.userEmail = userEmail;
        this.numberTimbri = numberTimbri;
        this.numberOmaggi = numberOmaggi;
    }

    @Override
    public String toString() {
        return timbroId + " - " + userEmail + " - " + Integer.toString(numberTimbri) + " - "
                + Integer.toString(numberTimbri);
    }
}
