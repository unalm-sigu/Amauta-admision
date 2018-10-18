package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ControlOrdenMerito;
import pe.edu.lamolina.model.academico.Facultad;

public interface ControlOrdenMeritoDAO extends EasyDAO<ControlOrdenMerito> {

    List<ControlOrdenMerito> allByDynatableCicloAcademico(DynatableFilter filter, CicloAcademico cicloAcademico);

    List<ControlOrdenMerito> allByCicloAcademico(CicloAcademico cicloAcademico);

    void deleteByCicloAcademico(CicloAcademico cicloAcademico);

    public ControlOrdenMerito findByFac(Facultad facultad, CicloAcademico cicloAcademico);

}
