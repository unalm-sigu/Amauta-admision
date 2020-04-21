package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Insecto;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RestriccionCarrera;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.amauta.dao.academico.RestriccionCarreraDAO;

@Repository
public class RestriccionCarreraDAOH extends AbstractEasyDAO<RestriccionCarrera> implements RestriccionCarreraDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public RestriccionCarreraDAOH() {
        super();
        setClazz(RestriccionCarrera.class);
    }

    @Override
    public List<RestriccionCarrera> allBySeccion(Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(RestriccionCarrera.class, "rc")
                .join("carrera car", "seccion sec")
                .filter("sec.id", seccion);
        return all(sql);
    }

    @Override
    public List<RestriccionCarrera> allActivasBySeccion(Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(RestriccionCarrera.class, "rc")
                .join("carrera car", "seccion sec")
                .filter("rc.estado", EstadoEnum.ACT)
                .filter("sec.id", seccion);
        return all(sql);
    }

    @Override
    public List<RestriccionCarrera> allActivasBySecciones(List<Seccion> secciones) {
        Octavia sql = Octavia.query()
                .from(RestriccionCarrera.class, "rc")
                .join("carrera car", "seccion sec")
                .filter("rc.estado", EstadoEnum.ACT)
                .in("sec.id", secciones);
        return all(sql);
    }

    @Override
    public void updateEstadoFechaUsuario(RestriccionCarrera restriccionCarrera) {
        Octavia octavia = Octavia.update(RestriccionCarrera.class);
        octavia.set(restriccionCarrera, "estado");
        this.update(restriccionCarrera);
    }

    @Override
    public void deleteAllByCiclo(CicloAcademico ciclo) {
        StringBuilder sql = new StringBuilder();
        sql.append(" DELETE ").append(RestriccionCarrera.class.getName()).append(" rca ")
                .append(" WHERE EXISTS ")
                .append(" ( ")
                .append(" SELECT 1 FROM ").append(Seccion.class.getName()).append(" sec ")
                .append(" JOIN sec.grupoSeccion gs ")
                .append(" JOIN gs.cicloAcademico ci ")
                .append(" WHERE ci.id=:CICLO ")
                .append("   AND rca.seccion.id=sec.id ")
                .append(" ) ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("CICLO", ciclo.getId());
        query.executeUpdate();
    }

    @Override
    public int saveList(List<RestriccionCarrera> restricciones) {
        if (restricciones.isEmpty()) {
            return 0;
        }

        long t1 = System.currentTimeMillis();
        Insecto sql = Insecto.createInsert()
                .into(RestriccionCarrera.class)
                .columns("estado", "fechaRegistro", "fechaModificacion",
                        "seccion", "carrera", "usuarioRegistro", "usuarioModificacion")
                .values(restricciones);

        Query query = getCurrentSession().createSQLQuery(sql.toString());
        int rows = query.executeUpdate();

        long t2 = System.currentTimeMillis();
        logger.info("{} RestriccionCarrera's insertados en {} mseg....", rows, (t2 - t1));
        return rows;
    }

}
