package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RecorridoIngresante;

public interface RecorridoIngresanteDAO extends EasyDAO<RecorridoIngresante> {

    List<RecorridoIngresante> allByDynatableCiclo(DynatableFilter filter, CicloAcademico ciclo);

}
