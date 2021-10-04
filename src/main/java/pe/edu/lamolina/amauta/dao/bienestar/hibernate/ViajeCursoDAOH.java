package pe.edu.lamolina.amauta.dao.bienestar.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.bienestar.ViajeCursoDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.bienestar.ViajeCurso;

@Repository
public class ViajeCursoDAOH extends AbstractEasyDAO<ViajeCurso> implements ViajeCursoDAO {

    public ViajeCursoDAOH() {
        super();
        setClazz(ViajeCurso.class);
    }

    @Override
    public ViajeCurso find(long id) {
        Octavia sql = Octavia.query()
                .from(ViajeCurso.class, "vc")
                .join("curso", "seccion", "cicloAcademico", "alumnoDelegado", "docenteCreador")
                .filter("id", id);

        return find(sql);
    }

    @Override
    public List<ViajeCurso> allByDocenteCiclo(Docente docente, CicloAcademico ciclo, DynatableFilter filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
