package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.amauta.dao.tramite.ConstanciaPlantillaDAO;

@Repository
public class ConstanciaPlantillaDAOH extends AbstractEasyDAO<SerieDocumento> implements ConstanciaPlantillaDAO {

    public ConstanciaPlantillaDAOH() {
        super();
        setClazz(SerieDocumento.class);
    }

    @Override
    public String alumnoName(Alumno alumno) {

        Octavia sql = Octavia.query()
                .select("per.nombreCompleto")
                .from(Alumno.class, "alu")
                .join("persona per")
                .filter("alu.id",alumno);

        return (String) sql.find(getCurrentSession());
    }

}
