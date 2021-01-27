package models;

import com.google.gson.annotations.Expose;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Usuario extends Model {
    @Expose
    @Required
    @MaxSize(15)
    @MinSize(4)
    public String nombre;
    @Expose
    @Required
    @MaxSize(15)
    @MinSize(4)
    public String contrasena;

    @Expose
    @OneToMany(mappedBy="user")
    public List<Tarea> tareas= new ArrayList<Tarea>();


    public Usuario(String nombre, String contrasena){
        this.nombre=nombre;
        this.contrasena=contrasena;
    }

}
