package pe.edu.lamolina.pivot.dao.mensajeria;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoMensajeIntranet;
import pe.edu.lamolina.model.academico.MensajeIntranet;

public interface AlumnoMensajeIntranetDAO extends EasyDAO<AlumnoMensajeIntranet> {

    void createMessage(MensajeIntranet mensajeria, List<Alumno> alumnos);

}
