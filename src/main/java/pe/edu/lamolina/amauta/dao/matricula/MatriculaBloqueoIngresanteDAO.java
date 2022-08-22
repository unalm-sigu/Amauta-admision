package pe.edu.lamolina.amauta.dao.matricula;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaBloqueoIngresante;

public interface MatriculaBloqueoIngresanteDAO extends EasyDAO<MatriculaBloqueoIngresante> {

    List<MatriculaBloqueoIngresante> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

    List<MatriculaBloqueoIngresante> allByCicloAcademico(CicloAcademico cicloAcademico);

}
