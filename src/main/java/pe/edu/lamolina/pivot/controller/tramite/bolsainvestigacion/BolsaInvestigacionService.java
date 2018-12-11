package pe.edu.lamolina.pivot.controller.tramite.bolsainvestigacion;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.tramite.AlumnoBolsaInvestigacion;
import pe.edu.lamolina.model.tramite.BolsaInvestigacion;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface BolsaInvestigacionService {

    void agregarAlumno(Facultad facultad, CicloAcademico cicloAcademico, AlumnoBolsaInvestigacion alumno, DataSessionPivot ds);

    List<String> checkearAlumno(Alumno alumno, CicloAcademico cicloAcademico);

    AlumnoBolsaInvestigacion findAlumnoBolsaInvestigacion(Long id);

    List<Alumno> searchAlumnosByFacultadNombre(List<Facultad> facultad, String nombre, CicloAcademico ciclo);

    List<Colaborador> searchColaboradoresByFacultadNombre(Facultad facultad, String nombre);

    void updateAlumno(Facultad facultad, CicloAcademico cicloAcademico, AlumnoBolsaInvestigacion alumno, DataSessionPivot ds);

    List<AlumnoBolsaInvestigacion> allByDynatableFacultadCicloAcademico(DynatableFilter filter, Facultad facultad, CicloAcademico cicloAcademico);

    void eliminarAlumno(Long id, CicloAcademico cicloAcademico, Facultad facultad);

    void enviarInvitaciones(Facultad facultad, CicloAcademico cicloAcademico, DataSessionPivot ds);

    Facultad findByDataSession(DataSessionPivot ds);

    BolsaInvestigacion findByFacultadCicloAcademico(Facultad facultad, CicloAcademico cicloAcademico);

}
