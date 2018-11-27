package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.List;
import org.hibernate.Query;
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
    public SeccionCursoMasivo find(long id) {
        Octavia sql = Octavia.query()
                .from(SeccionCursoMasivo.class, "scm")
                .join("cursoMasivoExamen cme", "cme.rolExamenes rexa", "userRegistro ur", "seccion se")
                .filter("scm.id", id);
        return find(sql);
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
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(" update SeccionCursoMasivo scm set scm.estado=:ESTADO, scm.usuarioExclusion.id=:USUARIO, scm.fechaExclusion=:FECHA_EXC ");
        strBuilder.append(" where scm.id=:PRM_ID ");
        Query query = getCurrentSession().createQuery(strBuilder.toString());
        query.setParameter("PRM_ID", seccionCursoMasivo.getId());
        query.setParameter("ESTADO", SeccionRolExamenEstadoEnum.EXC.name());
        query.setParameter("USUARIO", seccionCursoMasivo.getUsuarioExclusion().getId());
        query.setParameter("FECHA_EXC", seccionCursoMasivo.getFechaExclusion());
        query.executeUpdate();

    }
}
