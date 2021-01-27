package models;

import com.google.gson.annotations.Expose;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class Tarea extends Model{
    @Expose
    public String descripcion;
    @Expose
    public String fecha;
    @Expose
    public String lista;
    @Expose
    public String etiqueta;
    @ManyToOne
    public Usuario user;
    public Tarea( String descripcion, String fecha, String lista, String etiqueta){
        this.descripcion=descripcion;
        this.fecha=fecha;
        this.lista=lista;
        this.etiqueta=etiqueta;
    }


}
