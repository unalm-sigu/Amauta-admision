package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.academico.SituacionAcademicaDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.enums.SituacionAcademicaEnum;

@Repository
public class SituacionAcademicaDAOH extends AbstractEasyDAO<SituacionAcademica> implements SituacionAcademicaDAO {

    public SituacionAcademicaDAOH() {
        super();
        setClazz(SituacionAcademica.class);
    }

    @Override
    public SituacionAcademica findByCodigo(String codigo) {
        Octavia sql = Octavia.query()
                .from(SituacionAcademica.class, "sa")
                .filter("sa.codigo", codigo);

        return find(sql);
    }

    @Override
    public List<SituacionAcademica> allByCodes(List<SituacionAcademicaEnum> situaciones) {
        Octavia sql = Octavia.query()
                .from(SituacionAcademica.class, "sa")
                .complexFilter("concat('S_',sa.codigo)", "in", situaciones);

        return all(sql);
    }
}
