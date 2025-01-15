package pe.edu.lamolina.amauta.dao.nivelacioneegg.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.ExamenAlumnoNivelacionDAO;
import pe.edu.lamolina.model.nivelacioneegg.ExamenAlumnoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.ExamenCursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;

@Repository
public class ExamenAlumnoNivelacionDAOH extends AbstractEasyDAO<ExamenAlumnoNivelacion> implements ExamenAlumnoNivelacionDAO {

    public ExamenAlumnoNivelacionDAOH() {
        super();
        setClazz(ExamenAlumnoNivelacion.class);
    }

    @Override
    public ExamenAlumnoNivelacion find(long id) {
        Octavia sql = Octavia.query()
                .from(ExamenAlumnoNivelacion.class, "ean")
                .join("notaAlumnoNivelacion nan", "examenCursoNivelacion ecn")
                .join("ecn.tipoExamenNivelacion", "ecn.cursoNivelacion")
                .filter("ean.id", id);

        return find(sql);
    }

    @Override
    public List<ExamenAlumnoNivelacion> allByNotaAlumno(NotaAlumnoNivelacion notaAlumno) {
        Octavia sql = Octavia.query()
                .from(ExamenAlumnoNivelacion.class, "ean")
                .join("notaAlumnoNivelacion nan", "examenCursoNivelacion ecn")
                .join("ecn.tipoExamenNivelacion", "ecn.cursoNivelacion")
                .filter("nan.id", notaAlumno);

        return all(sql);
    }

    @Override
    public List<ExamenAlumnoNivelacion> allByNotasAlumnos(List<NotaAlumnoNivelacion> notasAlumnos) {
        Octavia sql = Octavia.query()
                .from(ExamenAlumnoNivelacion.class, "ean")
                .join("notaAlumnoNivelacion nan", "examenCursoNivelacion ecn")
                .join("ecn.tipoExamenNivelacion", "ecn.cursoNivelacion")
                .in("nan.id", notasAlumnos);

        return all(sql);
    }

    @Override
    public List<ExamenAlumnoNivelacion> allByExamen(ExamenCursoNivelacion examen) {
        Octavia sql = Octavia.query()
                .from(ExamenAlumnoNivelacion.class, "ean")
                .join("notaAlumnoNivelacion nan", "examenCursoNivelacion ecn")
                .join("ecn.tipoExamenNivelacion", "ecn.cursoNivelacion")
                .filter("ecn.id", examen);

        return all(sql);
    }

}
