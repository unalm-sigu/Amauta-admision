package pe.edu.lamolina.pivot.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.EstadoTramiteEnum;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.Tramite;

public interface ReincorporacionDAO extends EasyDAO<Reincorporacion> {

    List<Reincorporacion> allByTramite(Tramite tramite);

    List<Reincorporacion> allByDyna(DynatableFilter filter);

    List<Reincorporacion> allByResolucion(Resolucion resolucion);

    Reincorporacion findByTramiteEstadoTram(Tramite tramite, EstadoTramiteEnum estadoTramiteEnum);

    void updateEstado(Reincorporacion reincorporacion);

    void updateAceptado(Reincorporacion reincorporacion);

    List<Reincorporacion> allByEstadoTramiteAndAlumnoRei(Alumno alumno, EstadoTramite estadoTramite);

    Reincorporacion findByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);

    public List<Reincorporacion> allByCicloReincorporacion(CicloAcademico cicloAcademico);

    public List<Reincorporacion> allByTramitesCondicional(List<Tramite> tramites);
    
    List<Reincorporacion> allByTramite(List<Tramite> tramite);

    public List<Reincorporacion> allByEstadoTramiteAndAlumnos(List<Alumno> alumnos, EstadoTramite estadoTramite);
}
