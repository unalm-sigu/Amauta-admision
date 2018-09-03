package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.PuntajeEncuestaDocente;
import pe.edu.lamolina.pivot.dao.encuesta.PuntajeEncuestaDocenteDAO;

@Repository
public class PuntajeEncuestaDocenteDAOH extends AbstractEasyDAO<PuntajeEncuestaDocente> implements PuntajeEncuestaDocenteDAO {

    public PuntajeEncuestaDocenteDAOH() {
        super();
        setClazz(PuntajeEncuestaDocente.class);
    }

    //@Override
    public PuntajeEncuestaDocente findEncuestaDocente(EncuestaDocente encuestaForm) {
        Octavia sql = Octavia.query()
                .from(EncuestaDocente.class, "ed")
                .join("encuestaEstudiantil ee", "ee.encuesta en", "ee.cicloAcademico ciclo")
                .join("docenteSeccion ds", "ds.docente doc", "doc.persona per")
                .join("ds.seccion sec", "sec.grupoSeccion gs", "gs.curso cur")
                .join("cur.departamentoAcademico da", "da.facultad")
                .leftJoin("per.tipoDocumento tdoc")
                .filter("ed.id", encuestaForm);
        return find(sql);
    }

}
