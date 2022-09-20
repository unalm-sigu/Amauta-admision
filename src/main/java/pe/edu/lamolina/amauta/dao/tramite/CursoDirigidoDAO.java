package pe.edu.lamolina.amauta.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.tramite.CursoDirigido;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.Tramite;

public interface CursoDirigidoDAO extends EasyDAO<CursoDirigido> {

    CursoDirigido findByTramite(Tramite tramite);

    List<CursoDirigido> allByfacultades(DynatableFilter filters, CicloAcademico ciclo);

    void updateEstado(CursoDirigido cursoDirigido);

    List<CursoDirigido> allByTramites(List<Tramite> tramites);

    List<CursoDirigido> allByResolucion(DynatableFilter filter, Resolucion resolucion);

    List<CursoDirigido> allByResolucion(Resolucion resolucion);

    List<CursoDirigido> allByCicloAcademicoAlumno(MatriculaResumen matriculaResumen);

    List<CursoDirigido> allByCicloAcademicoSol(CicloAcademico cicloAcademico);

    List<CursoDirigido> allByfacultades(Facultad facultad, CicloAcademico cicloAcademico);
}
