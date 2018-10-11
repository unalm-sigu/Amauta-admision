package pe.edu.lamolina.pivot.dao.posgrado.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.posgrado.AlumnoTarifa;
import pe.edu.lamolina.pivot.dao.posgrado.AlumnoTarifaDAO;

@Repository
public class AlumnoTarifaDAOH extends AbstractEasyDAO<AlumnoTarifa> implements AlumnoTarifaDAO {

    public AlumnoTarifaDAOH() {
        super();
        setClazz(AlumnoTarifa.class);
    }

}
