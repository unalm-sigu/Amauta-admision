package pe.edu.lamolina.pivot.dao.academico.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.pivot.dao.academico.EgresadoDAO;

@Repository
public class EgresadoDAOH extends AbstractEasyDAO<Egresado> implements EgresadoDAO {

    public EgresadoDAOH() {
        super();
        setClazz(Egresado.class);
    }

    @Override
    public Egresado findByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(Egresado.class, "e")
                .join("alumno alu")
                .filter("alu.id", alumno);

        return find(sql);
    }

}
