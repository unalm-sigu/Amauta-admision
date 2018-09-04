package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocenteModalidad;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaDocenteModalidadDAO;

@Repository
public class EncuestaDocenteModalidadDAOH extends AbstractEasyDAO<EncuestaDocenteModalidad> implements EncuestaDocenteModalidadDAO {

    public EncuestaDocenteModalidadDAOH() {
        super();
        setClazz(EncuestaDocenteModalidad.class);
    }

    @Override
    public List<EncuestaDocenteModalidad> allByCiclo(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(EncuestaDocenteModalidad.class, "edm")
                .join("docente d", "modalidadEstudio me", "cicloAcademico ca")
                .filter("ca.id", cicloAcademico);
        return all(sql);
    }

}
