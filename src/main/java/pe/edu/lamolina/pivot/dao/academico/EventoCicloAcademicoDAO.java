package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;

public interface EventoCicloAcademicoDAO extends EasyDAO<EventoCicloAcademico> {

    public List<EventoCicloAcademico> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

    public EventoCicloAcademico findEventoCicloAcademico(EventoCicloAcademico eventoCicloAcademico);

}
