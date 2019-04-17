package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.matricula.MatriculaSimultaneo;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSimultaneoDAO;

@Repository
public class MatriculaSimultaneoDAOH extends AbstractEasyDAO<MatriculaSimultaneo> implements MatriculaSimultaneoDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public MatriculaSimultaneoDAOH() {
        super();
        setClazz(MatriculaSimultaneo.class);
    }

    @Override
    public List<MatriculaSimultaneo> allByMatriculaCurso(List<MatriculaCurso> matriculaCursos) {
        Octavia sql = Octavia.query()
                .from(MatriculaSimultaneo.class, "ms")
                .join("matriculaCurso mc", "matriculaCursoSimultaneo mcs")
                .in("mc.id", matriculaCursos);
        return all(sql);
    }

    @Override
    public List<MatriculaSimultaneo> allByMatriculaCurso(MatriculaCurso matriculaCurso) {
        Octavia sql = Octavia.query()
                .from(MatriculaSimultaneo.class, "ms")
                .join("matriculaCurso mc", "matriculaCursoSimultaneo mcs")
                .join("mc.curso cur1", "mcs.curso cur2")
                .filter("mc.id", matriculaCurso);
        return all(sql);
    }

    @Override
    public List<MatriculaSimultaneo> allByMatriculaResumen(List<MatriculaResumen> resumenes) {
        Octavia sql = Octavia.query()
                .from(MatriculaSimultaneo.class, "ms")
                .join("matriculaCurso mc", "matriculaCursoSimultaneo mcs", "mc.matriculaResumen mr")
                .join("mc.curso cur1", "mcs.curso cur2")
                .in("mr.id", resumenes);
        return all(sql);
    }

}
