import models.Tarea;
import models.Usuario;
import play.jobs.*;
@OnApplicationStart
public class Bootstrap extends Job{
    public void doJob() {
        if(Usuario.count() == 0) {
            Usuario f = new Usuario("marc", "1234");
            f.save();
            Tarea tar = new Tarea( "deberes","13-3-2020", "pendientes","urgente");
            tar.save();
            f.tareas.add(tar);
            f.save();
            tar.user=f;
            tar.save();
        }
    }
}
