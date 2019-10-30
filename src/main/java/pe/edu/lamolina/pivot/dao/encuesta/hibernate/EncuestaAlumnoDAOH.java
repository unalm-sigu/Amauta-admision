package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Insecto;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaAlumno;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaCurso;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaAlumnoDAO;

@Repository
public class EncuestaAlumnoDAOH extends AbstractEasyDAO<EncuestaAlumno> implements EncuestaAlumnoDAO {

    public EncuestaAlumnoDAOH() {
        super();
        setClazz(EncuestaAlumno.class);
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<EncuestaAlumno> allByEncuestaDocente(EncuestaDocente encuesta) {
        Octavia sql = Octavia.query()
                .from(EncuestaAlumno.class, "ea")
                .join("encuestaDocente ed", "ed.encuestaEstudiantil ee", "ed.docenteSeccion ds")
                .filter("ed.id", encuesta);
        return all(sql);
    }

    @Override
    public List<EncuestaAlumno> allByEncuestaCurso(EncuestaCurso encuesta) {
        Octavia sql = Octavia.query()
                .from(EncuestaAlumno.class, "ea")
                .join("encuestaCurso ec", "ec.encuestaEstudiantil ee")
                .filter("ec.id", encuesta);
        return all(sql);
    }

    @Override
    public void deleteByEncuestasDocentes(List<EncuestaDocente> encuestasDocentes) {
        List<Long> ids = encuestasDocentes.stream().map(enDoc -> enDoc.getId()).collect(Collectors.toList());
        if (ids.isEmpty()) {
            return;
        }

        String strQuery = "delete EncuestaAlumno ea where ea.encuestaDocente.id in (:ENCUESTAS) ";
        Query query = getCurrentSession().createQuery(strQuery);
        query.setParameterList("ENCUESTAS", ids);
        query.executeUpdate();
    }

    @Override
    public void deleteByEncuestasCursos(List<EncuestaCurso> encuestasCursos) {
        List<Long> ids = encuestasCursos.stream().map(enDoc -> enDoc.getId()).collect(Collectors.toList());
        if (ids.isEmpty()) {
            return;
        }

        String strQuery = "delete EncuestaAlumno ea where ea.encuestaCurso.id in (:ENCUESTAS) ";
        Query query = getCurrentSession().createQuery(strQuery);
        query.setParameterList("ENCUESTAS", ids);
        query.executeUpdate();
    }

    @Override
    public List<EncuestaAlumno> allByListEncuestaDocente(List<EncuestaDocente> listEncuestaDocente) {
        Octavia sql = Octavia.query()
                .from(EncuestaAlumno.class, "ea")
                .join("encuestaDocente ed", "ed.encuestaEstudiantil ee", "ed.docenteSeccion ds")
                .in("ed.id", listEncuestaDocente);
        return all(sql);
    }

    @Override
    public int saveList(List<EncuestaAlumno> encuestasAlumnos) {
        if (encuestasAlumnos.isEmpty()) {
            return 0;
        }

        long t1 = System.currentTimeMillis();
        Insecto sql = Insecto.createInsert()
                .into(EncuestaAlumno.class)
                .columns("estado", "fechaEncuesta", "fechaRegistro", "encuestaDocente",
                        "encuestaCurso", "alumno", "userEncuesta", "userRegistro")
                .values(encuestasAlumnos);

        Query query = getCurrentSession().createSQLQuery(sql.toString());
        int rows = query.executeUpdate();

        long t2 = System.currentTimeMillis();
        logger.info("{} EncuestaAlumno's insertados en {} mseg....", rows, (t2 - t1));
        return rows;
    }

}
