package controllers;

import com.google.gson.Gson;
import org.h2.engine.User;
import play.*;
import play.db.jpa.JPA;
import play.mvc.*;
import java.util.Date;
import java.util.*;
import play.data.validation.*;
import models.*;
import play.mvc.results.RenderText;

import javax.persistence.Query;



public class Tareas extends Application{
    /*@Before
    //Comprueba si se ha iniciado sesión
    static void checkUser(){
        if (connected()==null){
            flash.error("Por favor inicia sesión");
            Application.index();
        }
    }*/
    //Página principal
    public static void index() {
        Usuario u= connected();
        if((u.nombre.equals("admin"))&&(u.contrasena.equals("admin"))){
            Tareas.adminIndex();
        }
        else{
            try {
                List<Tarea> tareas = u.tareas;
                render(tareas, u);
            }
            catch (ArithmeticException ex) {
                // This prints the stacktrace to standard output
                ex.printStackTrace();
            }
        }

    }
    //Dirige a la vista de nueva tarea
    public static void nuevaTarea(){
        Usuario u=connected();
        render();
    }
    //Carga la vista del admin
    public static void adminIndex(){
        Usuario u= connected();
        List<Usuario> users= Usuario.findAll();
        render(users, u);
    }
    //Carga la lista de tareas en android
    public static void ListaTareasAndroid(String nombre){
        Usuario u =Usuario.find("byNombre",nombre).first();
        if(u!=null){
            List<Tarea> tareas=u.tareas;
            Gson g = new Gson();
            g = g.newBuilder().excludeFieldsWithoutExposeAnnotation().create();
            renderJSON(g.toJson(tareas));
        }
        else{
            renderText("error");
        }

    }
    //Editar tarea
    public static void editarTarea(Long id, String descripcion, String fecha, String lista, String etiqueta){
        Tarea vieja=Tarea.findById(id);
        if(vieja!=null){
            vieja.descripcion=descripcion;
            vieja.fecha=fecha;
            if(lista.equals("1")){
                vieja.lista="Pendiente";
            }
            if(lista.equals("2")){
                vieja.lista="En curso";
            }
            if(lista.equals("3")){
                vieja.lista="Realizada";
            }
            if(etiqueta.equals("1")){
                vieja.etiqueta="Urgente";
            }
            if(etiqueta.equals("2")){
                vieja.etiqueta="No urgente";
            }
            if(etiqueta.equals("3")){
                vieja.etiqueta="Dificil";
            }
            if(etiqueta.equals("4")){
                vieja.etiqueta="Facil";
            }
            vieja.save();
        }
        Usuario u=connected();
        List<Tarea> tareas=u.tareas;
        List<Usuario> users = Usuario.findAll();
        if(u.nombre.equals("admin")){
            renderTemplate("Tareas/adminIndex.html",users);
        }
        renderTemplate("Tareas/index.html", tareas);

    }
    //Borrar tarea
    public static void deleteTarea(Long id, String u){
        Usuario user =Usuario.find("byNombre", u).first();
        Tarea t= Tarea.find("byId",id).first();
        if(t!=null){
            user.tareas.remove(t);
            t.delete();
            Tareas.index();
        }
        else Tareas.index();
    }
    //Añadir nueva tarea
    public static void anadirTarea(String descripcion, String fecha, String lista, String etiqueta){
        String etiq="etiqueta";
        String list="lista";
        if(lista.equals("1")){
            list="Pendiente";
        }
        if(lista.equals("2")){
            list="En curso";
        }
        if(lista.equals("3")){
            list="Realizada";
        }
        if(etiqueta.equals("1")){
            etiq="Urgente";
        }
        if(etiqueta.equals("2")){
            etiq="No urgente";
        }
        if(etiqueta.equals("3")){
            etiq="Dificil";
        }
        if(etiqueta.equals("4")){
            etiq="Facil";
        }
        Tarea t= new Tarea(descripcion, fecha, list, etiq);
        Usuario u =connected();
        if (u!=null){
            u.tareas.add(t);
            t.user=u;
            t.save();
            List<Tarea> tareas=u.tareas;
            renderTemplate("Tareas/index.html", tareas);
        }
        else renderText("Usuario no encontrado");
    }
    //Modificar una tarea ya existente
    public static void actualizarTarea(Long id){
        Tarea t=Tarea.findById(id);
        Usuario u =connected();
        String nombre =u.nombre;
        renderTemplate("Tareas/tarea.html",t, nombre);

    }
    //Devuelve la lista de tareas de un usuario
    public static void returnTareas(String nombre){
        Usuario u = Usuario.find("byNombre", nombre).first();
        List<Usuario> users= Usuario.findAll();
        if(u!=null){
            List<Tarea> tareas = u.tareas;
            render("Tareas/adminIndex.html",tareas, users);
        }
        else{
            render("Tareas/adminIndex.html",users);
        }
    }
    //Devuelve el numero de tareas de un usuario clasificadas por lista
    public static void returnContador(String nombre){
        Usuario u=Usuario.find("byNombre", nombre).first();
        int pendientes=0, realizadas=0, encurso=0;
        List<Usuario> users=Usuario.findAll();
        if(u!=null){
            for(int i=0; i<u.tareas.size();i++){
                if(u.tareas.get(i).lista.equals("Pendiente"))
                   pendientes++;
                if(u.tareas.get(i).lista.equals("Realizada"))
                    realizadas++;
                if(u.tareas.get(i).lista.equals("En curso"))
                    encurso++;
            }
            render("Tareas/adminIndex.html",pendientes, realizadas, encurso, users);
        }
        else{
            render("Tareas/adminIndex.html",users);
        }
    }
    //Devuelve los usuarios con mas tareas pendientes
    public static void maxTareas(){
        List<Usuario> users = Usuario.findAll();
        String usuarios=null;
        int cont = 0;
        int max=0;
        for (int i=0;i<users.size();i++)
        {
            for(int y = 0; y<users.get(i).tareas.size();y++)
            {
                if (users.get(i).tareas.get(y).lista.equals("Pendiente")){
                    cont++;
                }
            }
            if(cont==max){
                usuarios=usuarios+", "+users.get(i).nombre;
                cont=0;
            }
            if(cont>max){
                usuarios=users.get(i).nombre;
                max=cont;
                cont=0;
            }

        }
        render("Tareas/adminIndex.html", usuarios, max, users);
    }
    //Editar la descripción de una tarea (no se usa)
    public void editarDescripcion(Tarea t, String user, String nuevaDescripcion){
        Usuario u = Usuario.find("byNombre", user).first();
        if(u!=null){
            for(Tarea tar:u.tareas){
                if (tar==t){
                    tar.descripcion=nuevaDescripcion;
                    tar.save();
                }
                renderText("Tarea editada");
            }

        }
        else renderText("Tarea no encontrada");
    }
    //Editar etiqueta de una tarea (no se usa)
    public void editarEtiqueta(Tarea t, String user, String nuevaEtiqueta){
        Usuario u = Usuario.find("byNombre", user).first();
        if(u!=null){
            for(Tarea tar:u.tareas){
                if (tar==t){
                    tar.etiqueta=nuevaEtiqueta;
                    tar.save();
                }
                renderText("Etiqueta editada");
            }

        }
        else renderText("Tarea no encontrada");
    }
    //Editar contraseña de un usuario (no se usa)
    public void editarContrasena(String nombre,String nuevaContrasena){
        Usuario u= Usuario.find("byNombre",nombre).first();
        if(u!=null){
            u.contrasena=nuevaContrasena;
            u.save();
            renderText("Contraseña editada");

        }
        else renderText("No se encontró el usuario");
    }
}
