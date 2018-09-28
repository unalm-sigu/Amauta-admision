package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ControlMeritoEgresado;

public interface ControlMeritoEgresadoDAO extends EasyDAO<ControlMeritoEgresado> {

    List<ControlMeritoEgresado> allByDynatableCicloAcademico(DynatableFilter filter, CicloAcademico cicloAcademico);

    List<ControlMeritoEgresado> allByCicloAcademico(CicloAcademico cicloAcademico);

    void deleteByCicloAcademico(CicloAcademico cicloAcademico);

}
