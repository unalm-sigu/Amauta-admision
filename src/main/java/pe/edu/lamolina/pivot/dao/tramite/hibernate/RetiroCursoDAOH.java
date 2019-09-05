package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.tramite.RetiroCursoDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.ACEP;
import pe.edu.lamolina.model.tramite.RetiroCurso;

@Repository
public class RetiroCursoDAOH extends AbstractEasyDAO<RetiroCurso> implements RetiroCursoDAO {

    public RetiroCursoDAOH() {
        super();
        setClazz(RetiroCurso.class);
    }

    @Override
    public List<RetiroCurso> allByAlumno(Alumno alumno) {
        Octavia sql = new Octavia()
                .from(RetiroCurso.class, "rc")
                .join("alumno al", "cicloAcademico ca", "curso cur")
                .filter("al.id", alumno);

        return all(sql);

    }

    @Override
    public List<RetiroCurso> allInfo() {
        Octavia sql = new Octavia()
                .from(RetiroCurso.class, "rc")
                .join("alumno al", "cicloAcademico ca", "curso cur")
                .filter("rc.estado", ACEP.name());

        return all(sql);
    }

    @Override
    public List<RetiroCurso> allRetiroCursoByAlumno(Alumno alumno) {
        
        Octavia sql = new Octavia()
                .from(RetiroCurso.class, "rc")
                .join("alumno al", "cicloAcademico ca", "curso cur")
                .leftJoin("tramite tra")
                .filter("al.id", alumno);
        return all(sql);
    }
}
