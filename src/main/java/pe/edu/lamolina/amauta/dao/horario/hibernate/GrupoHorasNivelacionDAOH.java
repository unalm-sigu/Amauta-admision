package pe.edu.lamolina.amauta.dao.horario.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.horario.GrupoHorasNivelacionDAO;
import pe.edu.lamolina.model.horario.GrupoHorasNivelacion;

@Repository
public class GrupoHorasNivelacionDAOH extends AbstractEasyDAO<GrupoHorasNivelacion> implements GrupoHorasNivelacionDAO {

    public GrupoHorasNivelacionDAOH() {
        super();
        setClazz(GrupoHorasNivelacion.class);
    }

}
