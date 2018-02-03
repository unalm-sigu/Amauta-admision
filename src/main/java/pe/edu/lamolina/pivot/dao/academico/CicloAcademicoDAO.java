package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;

public interface CicloAcademicoDAO extends EasyDAO<CicloAcademico> {

    CicloAcademico findActivo();

    List<CicloAcademico> allForChanges(Integer maxResultado);

    CicloAcademico findAnteriorRegular(CicloAcademico ciclo);

    List<CicloAcademico> allUltimos(Integer cantidadCiclos);

    List<CicloAcademico> allCicloAcademicoByRange(int yearinit, int yearend);

    CicloAcademico findCicloAcademico(CicloAcademico cicloAcademico);

    public List<CicloAcademico> allByDynatable(DynatableFilter filter);

}
