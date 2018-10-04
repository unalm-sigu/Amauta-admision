package pe.edu.lamolina.pivot.dao.posgrado.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.posgrado.AlumnoResumenCuotas;
import pe.edu.lamolina.pivot.dao.posgrado.AlumnoResumenCuotasDAO;

@Repository
public class AlumnoResumenCuotasDAOH extends AbstractEasyDAO<AlumnoResumenCuotas> implements AlumnoResumenCuotasDAO {

    public AlumnoResumenCuotasDAOH() {
        super();
        setClazz(AlumnoResumenCuotas.class);
    }

}
