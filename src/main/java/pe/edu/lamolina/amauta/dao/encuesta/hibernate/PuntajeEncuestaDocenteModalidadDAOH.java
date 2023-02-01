package pe.edu.lamolina.amauta.dao.encuesta.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocenteModalidad;
import pe.edu.lamolina.model.encuestaestudiantil.PuntajeEncuestaDocenteModalidad;
import pe.edu.lamolina.amauta.dao.encuesta.PuntajeEncuestaDocenteModalidadDAO;
import pe.edu.lamolina.model.encuestaestudiantil.RespuestaEncuestaAlumno;
import pe.edu.lamolina.model.enums.encuesta.TipoPreguntaEncuestaEnum;

@Repository
public class PuntajeEncuestaDocenteModalidadDAOH extends AbstractEasyDAO<PuntajeEncuestaDocenteModalidad> implements PuntajeEncuestaDocenteModalidadDAO {

    public PuntajeEncuestaDocenteModalidadDAOH() {
        super();
        setClazz(PuntajeEncuestaDocenteModalidad.class);
    }

    @Override
    public List<PuntajeEncuestaDocenteModalidad> allByEncuestaDocenteModalidad(EncuestaDocenteModalidad encuestaDocenteModalidad) {
        Octavia sql = Octavia.query()
                .from(PuntajeEncuestaDocenteModalidad.class, "pedm")
                .join("encuestaDocenteModalidad edm", "temaEncuesta te")
                .filter("edm.id", encuestaDocenteModalidad)
                .orderBy("te.nombre");

        return all(sql);
    }

    @Override
    public List<PuntajeEncuestaDocenteModalidad> allByEncuestasDocenteModalidad(List<EncuestaDocenteModalidad> encuestas) {
        Octavia sql = Octavia.query()
                .from(PuntajeEncuestaDocenteModalidad.class, "pedm")
                .join("encuestaDocenteModalidad edm", "temaEncuesta te")
                .in("edm.id", encuestas)
                .orderBy("te.nombre");

        return all(sql);
    }

    @Override
    public List<PuntajeEncuestaDocenteModalidad> allInfo(EncuestaDocenteModalidad encuestaDocenteModalidad) {
        Octavia sql = Octavia.query()
                .select("pe.tema", "avg(op.numero)", "stddev(op.numero)")
                .into(PuntajeEncuestaDocenteModalidad.class)
                .from(RespuestaEncuestaAlumno.class, "rea")
                .join("rea.opcion op", "op.pregunta pe", "rea.encuestaDocente ed", "pe.tema")
                .join("ed.encuestaEstudiantil ee", "ed.docenteSeccion ds", "ed.modalidadEstudio me")
                .join("ds.docente doc")
                .join("ee.cicloAcademico ca")
                .filter("pe.tipo", TipoPreguntaEncuestaEnum.LIKERT)
                .filter("doc.id", encuestaDocenteModalidad.getDocente())
                .filter("ca.id", encuestaDocenteModalidad.getCicloAcademico())
                .filter("me.id", encuestaDocenteModalidad.getModalidadEstudio())
                .groupBy("pe.tema");

        return all(sql);
    }
}
