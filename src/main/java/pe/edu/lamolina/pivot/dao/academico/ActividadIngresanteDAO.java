package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.ActividadIngresante;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RecorridoIngresante;

public interface ActividadIngresanteDAO extends EasyDAO<ActividadIngresante> {

    List<ActividadIngresante> allByRecorridoIngresantes(List<RecorridoIngresante> recorridoIngresantes);

    List<ActividadIngresante> allByCicloAcademico(CicloAcademico cicloAcademico);

}
