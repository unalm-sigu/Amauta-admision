package pe.edu.lamolina.amauta.dao.encuesta.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.ResumenEncuestaDocente;
import pe.edu.lamolina.amauta.dao.encuesta.ResumenEncuestaDocenteDAO;
import pe.edu.lamolina.model.encuestaestudiantil.RespuestaEncuestaAlumno;
import pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum;
import pe.edu.lamolina.model.enums.encuesta.TipoPreguntaEncuestaEnum;

@Repository
public class ResumenEncuestaDocenteDAOH extends AbstractEasyDAO<ResumenEncuestaDocente> implements ResumenEncuestaDocenteDAO {

    public ResumenEncuestaDocenteDAOH() {
        super();
        setClazz(ResumenEncuestaDocente.class);
    }

    @Override
    public List<ResumenEncuestaDocente> allByEncuestaDocente(EncuestaDocente encuestaDocente) {
        Octavia sql = Octavia.query(ResumenEncuestaDocente.class, "red")
                .join("red.encuestaDocente ed", "pregunta p")
                .filter("ed.id", encuestaDocente)
                .filter("p.tipo", TipoPreguntaEncuestaEnum.LIKERT);

        return all(sql);
    }

    @Override
    public List<ResumenEncuestaDocente> allInfoByEncuestaDocente(EncuestaDocente encuestaDocente) {
        Octavia sql = Octavia.query()
                .from(RespuestaEncuestaAlumno.class, "rea")
                .select("avg(op.numero)", "stddev(op.numero)", "ed", "pr", "pr.tipoLikert")
                .into(ResumenEncuestaDocente.class)
                .join("rea.encuestaDocente ed")
                .join("rea.opcion op", "op.pregunta pr")
                .isNull("rea.encuestaCurso")
                .filter("ed.id", encuestaDocente)
                .filter("ed.estado",EncuestaEstudiantilEstadoEnum.ACT.name())
                .groupBy("pr.id");

        return all(sql);
    }

}
