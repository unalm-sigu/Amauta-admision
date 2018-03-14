package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.ingresante;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface HorarioCachimboIngresanteService {

    void addAlumno(Alumno alumno, CicloAcademico cicloAcademico);

    void buscarHorario(Alumno alumno, CicloAcademico cicloAcademico);

    void asignarHorario(AlumnoHorario alumnoHorario, DataSessionPivot ds);

    List<AlumnoHorario> allAlumnoHorario(DynatableFilter filter, CicloAcademico cicloAcademico);

    void retirarHorario(AlumnoHorario alumnoHorario, Usuario user);

    void activarMatricula(AlumnoHorario alumnoHorario);

    void suspenderMatricula(AlumnoHorario alumnoHorario);

    List<Alumno> allAlumnoByName(String nombre);

    void cargarIngresantes(CicloAcademico cicloAcademico, Usuario user);

    List<Alumno> allAlumnoIngresantePregradoByNameCiclo(String nombre, CicloAcademico cicloAcademico);

    void eliminarHorarios(CicloAcademico cicloAcademico, Usuario user);

    List<IngresanteCantidad> allIngresanteCantidad(CicloAcademico cicloAcademico);

    void deleteIngresante(AlumnoHorario alumnoHorario, CicloAcademico cicloAcademico);

}
