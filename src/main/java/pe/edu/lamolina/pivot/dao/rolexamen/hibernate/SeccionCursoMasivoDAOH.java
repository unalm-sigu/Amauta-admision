package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.SeccionCursoMasivo;
import pe.edu.lamolina.pivot.dao.rolexamen.*;

@Repository
public class SeccionCursoMasivoDAOH extends AbstractEasyDAO<SeccionCursoMasivo> implements SeccionCursoMasivoDAO {

    public SeccionCursoMasivoDAOH() {
        super();
        setClazz(SeccionCursoMasivo.class);
    }

    @Override
    public List<SeccionCursoMasivo> allByCursosMasivos(List<CursoMasivoExamen> cursosMasivosExamenes) {
        Octavia sql = Octavia.query()
                .from(SeccionCursoMasivo.class, "scm")
                .join("cursoMasivoExamen cme", "userRegistro ur", "seccion se")
                .in("cme.id", cursosMasivosExamenes);
        return all(sql);
    }

    @Override
    public List<SeccionCursoMasivo> allSeccionByCursoMasivo(CursoMasivoExamen cursoMasivo) {
        Octavia sql = Octavia.query()
                .from(SeccionCursoMasivo.class, "scm")
                .join("cursoMasivoExamen cme", "userRegistro ur")
                .join("userRegistro ureg", "ureg.persona pureg")
                .left("usuarioExclusion uexl", "uexl.persona puexl")
                .filter("cme.id", cursoMasivo);
        return all(sql);
    }

    @Override
    public void updateEstadoExcluido(SeccionCursoMasivo seccionCursoMasivo) {
        seccionCursoMasivo.setEstadoEnum(SeccionRolExamenEstadoEnum.EXC);

        Octavia octavia = Octavia.update(SeccionCursoMasivo.class);
        octavia.set(seccionCursoMasivo, "estado");
        octavia.set(seccionCursoMasivo, "usuarioExclusion");
        octavia.set(seccionCursoMasivo, "fechaExclusion");
        this.update(octavia);
    }
}
