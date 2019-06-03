package com.gmail.yair.gmyaguitarra;

import android.widget.RadioButton;

public class guitarras {
    private String Guitarrasid;
    private String Marca;
    private String Fecha;
    private String Tipo;
    private String Color;
    private String Cuerdas;


    public String getGuitarrasid() {
        return Guitarrasid;
    }

    public void setGuitarrasid(String guitarraid) {
        this.Guitarrasid = guitarraid;
    }

    public String getMarca(){
        return Marca;
    }

    public void setMarca (String marca){
        this.Marca=marca;
    }

    public String getFecha(){
        return Fecha;
    }

    public void setFecha (String fecha){
        Fecha=fecha;
    }

    public String getTipo(){
        return Tipo;
    }

    public void setTipo (String tipo){
        Tipo=tipo;
    }

    public String getColor(){
        return Color;
    }

    public void setColor (String color){
        Color=color;
    }

    public String getCuerdas(){
        return Cuerdas;
    }
    public void setCuerdas (String cuerdas){
        Cuerdas=cuerdas;
    }

   /* public guitarras (String marca, String fecha, String color){
        this.Marca=marca;
        this.Fecha=fecha;
        this.Color=color;
    }
*/

    @Override
    public String toString() {
        return this.Fecha + ", Marca " + Marca + ", Tipo " + Tipo + ", Color " + Color ;
    }
}
