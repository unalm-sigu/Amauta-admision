package pe.edu.lamolina.pivot.dao.rrhh;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.rrhh.ContratoDocente;

public interface ContratoDocenteDAO extends EasyDAO<ContratoDocente> {

    List<ContratoDocente> allByDynatableProfesor(DynatableFilter filter, Docente docente);

    List<ContratoDocente> allByPeriodoDocente(CicloAcademico cicloInicio, CicloAcademico cicloFin, Docente docente);

}
