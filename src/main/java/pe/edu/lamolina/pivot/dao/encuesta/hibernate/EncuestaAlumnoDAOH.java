package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
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
                .join("encuestaCurso ec", "ed.encuestaEstudiantil ee", "ed.docenteSeccion ds")
                .filter("ec.id", encuesta);
        return all(sql);
    }

    @Override
    public void deleteByEncuestasDocente(List<Long> encDcos) {
        String strQuery = "delete from EncuestaAlumno ea where ea.encuestaDocente.id in :enc";
        Query query = getCurrentSession().createQuery(strQuery);
        query.setParameterList("enc", encDcos);
        query.executeUpdate();
    }

    @Override
    public void deleteByEncuestasCurso(List<Long> encCursos) {
        String strQuery = "delete from EncuestaAlumno ea where ea.encuestaCurso.id in :enc";
        Query query = getCurrentSession().createQuery(strQuery);
        query.setParameterList("enc", encCursos);
        query.executeUpdate();
    }

}
