package pe.edu.lamolina.pivot.dao.academico.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.AlumnoVisitante;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.dao.academico.AlumnoVisitanteDAO;

@Repository
public class AlumnoVisitanteDAOH extends AbstractEasyDAO<AlumnoVisitante> implements AlumnoVisitanteDAO {

    public AlumnoVisitanteDAOH() {
        super();
        setClazz(AlumnoVisitante.class);
    }

    @Override
    public AlumnoVisitante findByPersona(Persona persona) {
        Octavia sql = Octavia.query()
                .from(AlumnoVisitante.class, "av")
                .join("persona per", "userRegistro us", "cicloEstudia ci")
                .leftJoin("paisUniversidad pa")
                .filter("per.id", persona);

        return find(sql);
    }

    @Override
    public AlumnoVisitante findByPersona(Persona persona, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AlumnoVisitante.class, "av")
                .join("persona per", "userRegistro us", "cicloEstudia ci")
                .leftJoin("paisUniversidad pa")
                .filter("per.id", persona)
                .filter("ci.id", cicloAcademico);

        return find(sql);
    }

}
