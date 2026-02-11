package pe.edu.lamolina.amauta.dao.tramite;

import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.tramite.DocenteResolucion;
import pe.edu.lamolina.model.tramite.Resolucion;

import java.util.List;

public interface DocenteResolucionDAO extends EasyDAO<DocenteResolucion> {

    List<DocenteResolucion> allTramiteByFilter(DynatableFilter filter);
    DocenteResolucion findByCicloAndTipo(CicloAcademico cicloAcademico, Docente docente);
    List<DocenteResolucion> allDocenteResolucionConsejoByResolucion(Resolucion resolucion);
    List<DocenteResolucion> allDocenteResolucionFacultadByResolucion(Resolucion resolucion);
    List<DocenteResolucion> allDocenteResolucionConsejo();
    List<DocenteResolucion> allDocenteResolucionFacultad();
    DocenteResolucion findByDocenteConsejoAct(Docente docente);
    DocenteResolucion findByDocenteFacultadAct(Docente docente);
}
