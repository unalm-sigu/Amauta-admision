package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.pivot.dao.encuesta.BloquePreguntasDAO;
import pe.edu.lamolina.model.examen.BloquePreguntas;
import pe.edu.lamolina.model.examen.SubTituloExamen;

@Repository
public class BloquePreguntasDAOH extends AbstractEasyDAO<BloquePreguntas> implements BloquePreguntasDAO {

    public BloquePreguntasDAOH() {
        super();
        setClazz(BloquePreguntas.class);
    }

    @Override
    public List<BloquePreguntas> allBysubtitulos(List<SubTituloExamen> subTitulos) {
        Octavia sql = Octavia.query()
                .from(BloquePreguntas.class, "blo")
                .join("subTituloExamen subti")
                .in("subti.id", subTitulos);

        return all(sql);
    }

    @Override
    public List<BloquePreguntas> allBySubtitulo(SubTituloExamen subtitulo) {
        Octavia sql = Octavia.query()
                .from(BloquePreguntas.class, "blo")
                .join("subTituloExamen subti")
                .filter("subti.id", subtitulo);

        return all(sql);
    }

    @Override
    public BloquePreguntas findBloqueEvaluacionVirtual(Long instancia) {
        Octavia sql = Octavia.query()
                .from(BloquePreguntas.class, "blo")
                .join("subTituloExamen subti")
                .filter("blo.id", instancia);

        return find(sql);
    }

}
