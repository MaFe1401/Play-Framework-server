package controllers;

import org.h2.engine.User;
import play.*;
import play.db.jpa.GenericModel;
import play.db.jpa.JPA;
import play.mvc.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.*;
import play.data.validation.*;
import models.*;
import play.mvc.results.RenderText;

import javax.persistence.Query;

public class Application extends Controller {
    @Before
    //S'executa en connectar-se l'usuari
    static void addUser(){
        Usuario user=connected();
        if (user!=null){
            renderArgs.put("user",user);
        }
    }
    static Usuario connected(){
        if(renderArgs.get("user")!=null) {
            return renderArgs.get("user", Usuario.class);
        }
        String username=session.get("user");
        if(username!=null){
            return Usuario.find("byNombre",username).first();
        }
        return null;
    }
    //Carrega la pàgina inicial
    public static void index() {
       if(connected()!=null){
          Tareas.index();
       }
       render();
    }
    //Carrega la vista de registre
    public static void register(){
        render();
    }
    //Registre de l'usuari
    public static void saveUser(@Valid Usuario user, String verifypassword){
        validation.required(verifypassword).message("Vuelve a introducir la contraseña");
        validation.equals(verifypassword, user.contrasena).message("La contraseña no coincide");
        if(validation.hasErrors()){
            render("@register", user,verifypassword);
        }
        user.create();
        session.put("user",user.nombre);
        flash.success("Bienvenido, " +user.nombre);
        Tareas.index();
    }
    //Inicialitza la base de dades (no s'utilitza)
    public void inicializarBDD(){
        Usuario f = new Usuario("marc1", "12345");
        f.save();
        Tarea tar = new Tarea( "deberes", "2021-3-15","Pendiente","urgente");
        tar.save();
        f.tareas.add(tar);
        f.save();
        tar.user=f;
        tar.save();
        Usuario us = new Usuario("marc2", "12345");
        us.save();
        Tarea tarea = new Tarea( "pasear", "2021-3-15","Pendiente","urgente");
        tarea.save();
        us.tareas.add(tar);
        us.save();
        tarea.user=us;
        tarea.save();
        }
        //Registre per a Android
    public static void signUp(String nombre, String contra){
        Usuario u=Usuario.find("byNombre", nombre).first();
        if (u==null)
        {
            Usuario f = new Usuario (nombre, contra);
            f.save();
            renderText("Correcto");
            session.put("user", u.nombre);
            flash.success("Registrado correctamente");
        }
        else {
            renderText("No se ha podido registrar");
            flash.error("Error, el usuario ya existe");
            index();
        }
    }
    //Iniciar sessió depenent de si és admin o usuari normal
    public static void logIn(String nombre, String contra) {
        Usuario u = Usuario.find("byNombreAndContrasena", nombre, contra).first();

        if (u!=null) {
            if ((u.nombre.equals("admin"))&&(u.contrasena.equals("admin")))
            {
                session.put("user", u.nombre);
                Tareas.adminIndex();
            }
            session.put("user", u.nombre);
            flash.success("Bienvenido, "+u.nombre);
            Tareas.index();
        }
        flash.put("nombre",nombre);
        flash.error("Error al iniciar sesión");
        index();
    }
    //Iniciar sessió en Android
    public static void logInAndroid(String nombre, String contra){
        Usuario u = Usuario.find("byNombreAndContrasena", nombre, contra).first();

        if (u!=null) {
            if ((u.nombre.equals("admin"))&&(u.contrasena.equals("admin")))
            {
                session.put("user", u.nombre);
                renderText("Correcto");
            }
            session.put("user", u.nombre);
            flash.success("Bienvenido, "+u.nombre);
            renderText("Correcto");
        }
        flash.put("nombre",nombre);
        flash.error("Error al iniciar sesión");
        renderText("error");
    }
    //Borrar usuario
    public static void borrarUser(long id){
        Usuario u = Usuario.findById(id);
        deleteUser(u.nombre);
    }
    //Borrar usuario
    public static void deleteUser(String user){
        Usuario u = Usuario.find("byNombre",user).first();
        if(u!=null){
            if(!u.tareas.isEmpty()){
                for(Tarea t: u.tareas){
                    t.delete();
                }
            }
            u.delete();
            Tareas.adminIndex();
        }
        else renderText("Usuario "+user+" no encontrado");
    }
    //Cerrar sesión
    public static void logout(){
        session.clear();
        index();
    }



}