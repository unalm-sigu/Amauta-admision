package pe.edu.lamolina.amauta.dao.consejeria;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.consejeria.AgendaConsejero;
import pe.edu.lamolina.model.consejeria.Consejero;

public interface AgendaConsejeroDAO extends EasyDAO<AgendaConsejero> {

    public List<AgendaConsejero> allByConsejero(Consejero consejero);

    public List<AgendaConsejero> allDynatableByCicloAcademico(DynatableFilter filter);

    public List<AgendaConsejero> allDynatableByCicloAcademicoCarrera(DynatableFilter filter, Long idCarreraSupervisor);

}
