package pe.edu.lamolina.amauta.dao.nivelacioneegg.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.AlumnoNivelacionDAO;
import pe.edu.lamolina.model.nivelacioneegg.AlumnoNivelacion;

@Repository
public class AlumnoNivelacionDAOH extends AbstractEasyDAO<AlumnoNivelacion> implements AlumnoNivelacionDAO {

    public AlumnoNivelacionDAOH() {
        super();
        setClazz(AlumnoNivelacion.class);
    }

}
