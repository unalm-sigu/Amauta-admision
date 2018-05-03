package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.EventoAcademico;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;

public interface EventoAcademicoDAO extends EasyDAO<EventoAcademico> {

    public List<EventoAcademico> allEventoAcademicoByName(String nombre);

    EventoAcademico findByCodigo(EventoAcademicoEnum eventoAcademicoEnum);

}
