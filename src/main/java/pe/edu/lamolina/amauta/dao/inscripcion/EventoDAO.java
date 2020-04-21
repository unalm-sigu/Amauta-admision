package pe.edu.lamolina.amauta.dao.inscripcion;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.inscripcion.Evento;

public interface EventoDAO extends EasyDAO<Evento> {

    Evento findByCode(String exam);

}
