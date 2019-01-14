package pe.edu.lamolina.pivot.controller.programacionhorarios.tramiteaula;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.bienestar.ReservaAula;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Empresa;

public interface TramiteAulaService {

    List<Aula> allByDynatableFilterAula(DynatableFilter filter);

    Empresa saveInstitucion(Empresa institucion);

    List<Alumno> allAlumnoByName(String nombre);

    List<Docente> allDocenteByName(String nombre);

    void save(ReservaAula reservaAula);

}
