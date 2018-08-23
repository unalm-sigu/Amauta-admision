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

    public void agregarAlumno(Facultad facultad, CicloAcademico cicloAcademico, AlumnoBolsaInvestigacion alumno, DataSessionPivot ds);

    public List<String> checkearAlumno(Alumno alumno, CicloAcademico cicloAcademico);

    public AlumnoBolsaInvestigacion findAlumnoBolsaInvestigacion(Long id);

    public List<Alumno> searchAlumnosByFacultadNombre(Facultad facultad, String nombre);

    public List<Colaborador> searchColaboradoresByFacultadNombre(Facultad facultad, String nombre);
    
    public void updateAlumno(Facultad facultad, CicloAcademico cicloAcademico, AlumnoBolsaInvestigacion alumno, DataSessionPivot ds);

    public List<AlumnoBolsaInvestigacion> allByDynatableFacultadCicloAcademico(DynatableFilter filter, Facultad facultad, CicloAcademico cicloAcademico);

    public void eliminarAlumno(Long id, CicloAcademico cicloAcademico, Facultad facultad);

    public void enviarInvitaciones(Facultad facultad, CicloAcademico cicloAcademico, DataSessionPivot ds);

    Facultad findByDataSession(DataSessionPivot ds);

    public BolsaInvestigacion findByFacultadCicloAcademico(Facultad facultad, CicloAcademico cicloAcademico);

}
