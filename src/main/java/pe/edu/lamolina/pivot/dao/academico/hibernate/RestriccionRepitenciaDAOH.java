package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RestriccionRepitencia;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.pivot.dao.academico.RestriccionRepitenciaDAO;

@Repository
public class RestriccionRepitenciaDAOH extends AbstractEasyDAO<RestriccionRepitencia> implements RestriccionRepitenciaDAO {

    public RestriccionRepitenciaDAOH() {
        super();
        setClazz(RestriccionRepitencia.class);
    }

    @Override
    public List<RestriccionRepitencia> allBySeccion(Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(RestriccionRepitencia.class, "rp")
                .join("tipoRepitencia tr", "seccion sec")
                .filter("sec.id", seccion);
        return all(sql);
    }

    @Override
    public List<RestriccionRepitencia> allActivasBySeccion(Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(RestriccionRepitencia.class, "rp")
                .join("tipoRepitencia tr", "seccion sec")
                .filter("rp.estado", EstadoEnum.ACT)
                .filter("sec.id", seccion);
        return all(sql);
    }

    @Override
    public List<RestriccionRepitencia> allActivasBySecciones(List<Seccion> secciones) {
        Octavia sql = Octavia.query()
                .from(RestriccionRepitencia.class, "rp")
                .join("tipoRepitencia tr", "seccion sec")
                .filter("rp.estado", EstadoEnum.ACT)
                .in("sec.id", secciones);
        return all(sql);
    }

    @Override
    public void updateEstadoFechaUsuario(RestriccionRepitencia restriccionRepitencia) {
        Octavia octavia = Octavia.update(RestriccionRepitencia.class);
        octavia.set(restriccionRepitencia, "estado");
        octavia.set(restriccionRepitencia, "usuarioModificacion");
        octavia.set(restriccionRepitencia, "fechaModificacion");
        this.update(restriccionRepitencia);
    }

    @Override
    public void deleteAllByCiclo(CicloAcademico ciclo) {

        StringBuilder sql = new StringBuilder();

        sql.append(" DELETE ")
                .append(RestriccionRepitencia.class.getName()).append(" rr ")
                .append(" WHERE EXISTS ")
                .append(" ( ")
                .append(" SELECT 1 FROM ").append(Seccion.class.getName()).append(" sec ")
                .append(" JOIN sec.grupoSeccion gs ")
                .append(" JOIN gs.cicloAcademico ci ")
                .append(" WHERE ci.id=:CICLO ")
                .append("   AND rr.seccion.id=sec.id ")
                .append(" ) ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("CICLO", ciclo.getId());
        query.executeUpdate();
    }

}
