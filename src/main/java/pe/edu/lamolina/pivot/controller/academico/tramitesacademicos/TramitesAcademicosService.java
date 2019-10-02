package pe.edu.lamolina.pivot.controller.academico.tramitesacademicos;

import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.List;
import org.joda.time.DateTime;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.AccionTramiteAcademico;
import pe.edu.lamolina.model.tramite.AccionTramiteDocumento;
import pe.edu.lamolina.model.tramite.ReunionConsejo;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface TramitesAcademicosService {

    List<Tramite> allTramitesByFilter(DynatableFilter filter, DataSessionPivot dsp);

    void aceptarSolReincorporacion(Tramite tramite, AccionTramiteAcademico accionTramiteAcademico, DataSessionPivot ds);

    void agendarSolicitud(Tramite tramite, ReunionConsejo reunionConsejo, DateTime today, Usuario usuario);

    List<ReunionConsejo> allReunionConsejoByDyna(DynatableFilter filter, List<Oficina> oficina);

    void revertTramiteAcademico(Tramite tramite, DataSessionPivot ds);

    Tramite findTramite(Long tramiteId);

    void procesarTramite(Tramite tramite, AccionTramiteAcademico accionTramiteAcademico, AccionTramiteDocumento accionTramiteDocumento, DataSessionPivot ds);

    String cursoDirigidoReporte(Tramite tramite, DataSessionPivot ds);

    List<Curso> allCursos();

    List<Curso> allCursosByName(String nombre, Integer limit);

    List<CicloAcademico> allCiclosAcademicosByName(String nombre, Alumno alumno);

    ArrayNode allAlumnoCicloJson(Alumno alumno, AlumnoCiclo ciclo);

    AlumnoCiclo findAlumnoCiclo(AlumnoCiclo alumnoCiclo, Tramite tramite);

    List<AlumnoCiclo> allAlumnoCicloByAlumno(Alumno alumno, Tramite tramite);

    void saveAlumnoCicloFromRevision(AlumnoCiclo alumnoCiclo, Long tramiteId, DataSessionPivot ds);

    AccionTramiteAcademico findAccionTramiteAcademico(AccionTramiteAcademico accionTramiteAcademico);

    public List<Docente> allByNombre(String nombre);

    public List<Tramite> allTramitesByFac(Facultad facultad, DataSessionPivot ds);

    public String allcursoDirigidoFac(Facultad tram, DataSessionPivot ds);

    public String alllistCursoDirigidoFac(Facultad facultad, DataSessionPivot ds);

    public AccionTramiteDocumento findAccionTramiteDocumento(AccionTramiteDocumento accionTramiteDoc);

    public void revertirCambioHistorial(AlumnoCiclo alumnoCiclo, DataSessionPivot ds);

    public void deleteCicloCurso(AlumnoCicloCurso alumnoCicloCurso, Long idTramite, DataSessionPivot ds);

    public TipoTramite findTipoTramite(Long id);

}
