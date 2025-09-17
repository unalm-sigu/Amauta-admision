package pe.edu.lamolina.amauta.dao.tramite;

import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.SancionDisciplina;
import pe.edu.lamolina.model.tramite.SancionDisciplinaCiclo;

import java.util.List;

public interface SancionDisciplinaDAO extends EasyDAO<SancionDisciplina> {
    List<SancionDisciplina> allByCicloDynatable(CicloAcademico cicloAcademico, DynatableFilter filter);
    List<SancionDisciplina> allSancionDisciplina();
    List<SancionDisciplina> allSancionDisciplinaByResolucion(Resolucion resolucion);
    SancionDisciplina findBySancion(Alumno alumno);
    SancionDisciplina findByAlumnoAct(Alumno alumno);
    List<SancionDisciplina> findAlumnosSancionadosPorCiclo(CicloAcademico cicloAcademico);
    List<SancionDisciplina> findByResolucionAndEstadoACEP(Resolucion resolucion);

}
