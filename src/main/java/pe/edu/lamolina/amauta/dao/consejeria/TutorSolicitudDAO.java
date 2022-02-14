package pe.edu.lamolina.amauta.dao.consejeria;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.TutorSolicitud;

public interface TutorSolicitudDAO extends EasyDAO<TutorSolicitud> {

    List<TutorSolicitud> allTutorSolicitudByFilter(DynatableFilter filter,CicloAcademico ciclo);

    public void deleteByCiclo(CicloAcademico cicloAcademico);
}
