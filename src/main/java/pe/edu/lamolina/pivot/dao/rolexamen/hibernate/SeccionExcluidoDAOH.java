package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.rolexamen.CursoExcluido;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionExcluido;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionExcluidoDAO;

@Repository
public class SeccionExcluidoDAOH extends AbstractEasyDAO<SeccionExcluido> implements SeccionExcluidoDAO {

    public SeccionExcluidoDAOH() {
        super();
        setClazz(SeccionExcluido.class);
    }

    @Override
    public SeccionExcluido find(long id) {
        Octavia sql = Octavia.query()
                .from(SeccionExcluido.class, "se")
                .join("rolExamenes re", "seccion sec")
                .join("cursoExcluido ce", "ce.curso cur", "ce.rolExamenes cre")
                .filter("se.id", id);
        return find(sql);
    }

    @Override
    public List<SeccionExcluido> allByRolExamenes(RolExamenes rolExamenes) {
        Octavia sql = Octavia.query()
                .from(SeccionExcluido.class, "se")
                .join("rolExamenes re", "seccion sec")
                .filter("re.id", rolExamenes);
        return all(sql);
    }

    @Override
    public SeccionExcluido findByRolExamenesAndSeccion(RolExamenes rolExamenes, Seccion seccion, EstadoEnum... estadoEnum) {
        Octavia sql = Octavia.query()
                .from(SeccionExcluido.class, "se")
                .join("rolExamenes re", "seccion sec")
                .filter("re.id", rolExamenes)
                .filter("sec.id", seccion)
                .in("se.estado", estadoEnum);
        return find(sql);
    }

    @Override
    public List<SeccionExcluido> allByCursoExcluido(CursoExcluido cursoExcluido, EstadoEnum... estadoEnum) {
        Octavia sql = Octavia.query()
                .from(SeccionExcluido.class, "se")
                .join("cursoExcluido ce", "seccion sec", "ce.curso cur")
                .join("sec.grupoHoras gh", "sec.aula au")
                .filter("ce.id", cursoExcluido);
        if (estadoEnum != null && estadoEnum.length != 0) {
            sql.in("se.estado", estadoEnum);
        }
        sql.orderBy("sec.codigo2");
        return all(sql);
    }

    @Override
    public void updateColumns(SeccionExcluido seccionExcluido, String... columns) {
        Octavia octavia = Octavia.update(SeccionExcluido.class);
        for (String column : columns) {
            octavia.set(seccionExcluido, column);
        }
        this.update(octavia);
    }

    @Override
    public void deleteBySecciones(List<Seccion> secciones) {
        List<Long> seccionesIds = secciones.stream().map(x -> x.getId()).collect(Collectors.toList());
        StringBuilder sql = new StringBuilder();
        sql.append(" DELETE ").append(SeccionExcluido.class.getName()).append(" sex ")
                .append(" WHERE sex.seccion.id in :SECCIONES ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameterList("SECCIONES", seccionesIds);
        query.executeUpdate();
    }

    @Override
    public void deleteByRolExamenes(RolExamenes rolExamenes) {
        StringBuilder sql = new StringBuilder();
        sql.append(" DELETE ").append(SeccionExcluido.class.getName()).append(" sex ")
                .append(" WHERE sex.rolExamenes.id in :ROL_EXAMENES ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("ROL_EXAMENES", rolExamenes.getId());
        query.executeUpdate();
    }

    @Override
    public Integer countByCursoExcluido(CursoExcluido cursoExcluido, EstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .select("count(ce)")
                .from(SeccionExcluido.class, "se")
                .join("cursoExcluido ce")
                .filter("ce.id", cursoExcluido)
                .in("se.estado", estados);
        return TypesUtil.getInt(find(sql));
    }

    @Override
    public void deleteByCursoExcluido(CursoExcluido cursoExcluido) {
        StringBuilder sql = new StringBuilder();
        sql.append(" DELETE ").append(SeccionExcluido.class.getName()).append(" sex ")
                .append(" WHERE sex.cursoExcluido.id = :CURSO_EXCLUIDO ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("CURSO_EXCLUIDO", cursoExcluido.getId());
        query.executeUpdate();
    }

}
