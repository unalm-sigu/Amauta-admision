package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.pivot.dao.academico.AlumnoVisitanteDAO;
import pe.edu.lamolina.pivot.model.academico.AlumnoVisitante;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.general.Persona;

@Repository
public class AlumnoVisitanteDAOH extends AbstractDAO<AlumnoVisitante> implements AlumnoVisitanteDAO {

    public AlumnoVisitanteDAOH() {
        super();
        setClazz(AlumnoVisitante.class);
    }

    @Override
    public AlumnoVisitante findByPersona(Persona persona) {
        Octavia sql = Octavia.query()
                .from(AlumnoVisitante.class, "av")
                .join("persona per","userRegistro us","cicloEstudia ci")
                .leftJoin("paisUniversidad pa")
                .filter("per.id", persona);
        return (AlumnoVisitante) sql.find(getCurrentSession());
    }

    @Override
    public AlumnoVisitante findByPersona(Persona persona, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AlumnoVisitante.class, "av")
                .join("persona per","userRegistro us","cicloEstudia ci")
                .leftJoin("paisUniversidad pa")
                .filter("per.id", persona)
                .filter("ci.id", cicloAcademico);
        return (AlumnoVisitante) sql.find(getCurrentSession());
    }

}
