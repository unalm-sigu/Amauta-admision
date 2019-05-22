package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.rolexamen.CursoExcluido;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.pivot.dao.rolexamen.CursoExcluidoDAO;

@Repository
public class CursoExcluidoDAOH extends AbstractEasyDAO<CursoExcluido> implements CursoExcluidoDAO {

    public CursoExcluidoDAOH() {
        super();
        setClazz(CursoExcluido.class);
    }

    @Override
    public CursoExcluido find(long id) {
        Octavia sql = Octavia.query()
                .from(CursoExcluido.class, "ce")
                .join("rolExamenes re", "curso cu")
                .filter("ce.id", id);
        return find(sql);
    }

    @Override
    public List<CursoExcluido> allByRolExamenes(RolExamenes rolExamenes) {
        Octavia sql = Octavia.query()
                .from(CursoExcluido.class, "cme")
                .join("rolExamenes re", "curso cu")
                .filter("re.id", rolExamenes)
                .orderBy("cu.nombre");
        return all(sql);
    }

    @Override
    public List<CursoExcluido> allByRolExamenes(RolExamenes rolExamenes, EstadoEnum estadoEnum) {
        Octavia sql = Octavia.query()
                .from(CursoExcluido.class, "cme")
                .join("rolExamenes re", "curso cu")
                .filter("re.id", rolExamenes)
                .filter("re.estado", estadoEnum)
                .orderBy("cu.nombre");
        return all(sql);
    }

    @Override
    public CursoExcluido findActiveByCursoAndRolExamenes(Curso curso, RolExamenes rolExamenes) {
        Octavia sql = Octavia.query()
                .from(CursoExcluido.class, "ce")
                .join("rolExamenes re", "curso cu")
                .filter("re.id", rolExamenes)
                .filter("cu.id", curso)
                .filter("ce.estado", EstadoEnum.ACT.name())
                .orderBy("cu.nombre");
        return find(sql);
    }

    @Override
    public void updateAnulacion(CursoExcluido cursoExcluidoUpd) {
        Octavia octavia = Octavia.update(CursoExcluido.class);
        octavia.set(cursoExcluidoUpd, "estado");
        this.update(octavia);
    }

    @Override
    public void updateColumns(CursoExcluido sursoExcluido, String... columns) {
        Octavia octavia = Octavia.update(CursoExcluido.class);
        for (String column : columns) {
            octavia.set(sursoExcluido, column);
        }
        this.update(octavia);
    }

    @Override
    public void deleteByRolExamenes(RolExamenes rolExamenes) {
        StringBuilder strb = new StringBuilder();
        strb.append(" delete from  CursoExcluido ce where ce.rolExamenes.id=:ROL_EXAMENES ");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("ROL_EXAMENES", rolExamenes.getId());
        query.executeUpdate();
    }
}
