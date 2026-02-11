package pe.edu.lamolina.amauta.controller.academico.registroborradoalumno;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RegistroBorradoAlumno;
import pe.edu.lamolina.model.seguridad.Usuario;

import javax.sound.sampled.Line;
import java.util.List;

public interface RegistroBorradoService {

    List<Alumno> allActivoPregradoByNombre(String nombre);

    List<RegistroBorradoAlumno> allByDynatable(DynatableFilter filter);

    ObjectNode allHistorialByInfoAlumno(InfoAlumno infoAlumno);

    ArrayNode allCiclosEstudiadosByAlumno(Alumno alumno);

    void save(RegistroBorradoAlumno registro, DataSessionPivot ds);

    ObjectNode allHistorialEliminadoByInfoAlumno(InfoAlumno infoAlumno);
}
