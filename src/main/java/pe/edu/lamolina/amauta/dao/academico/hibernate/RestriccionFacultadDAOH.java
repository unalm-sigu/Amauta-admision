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
import pe.edu.lamolina.model.academico.RestriccionFacultad;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.amauta.dao.academico.RestriccionFacultadDAO;

@Repository
public class RestriccionFacultadDAOH extends AbstractEasyDAO<RestriccionFacultad> implements RestriccionFacultadDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public RestriccionFacultadDAOH() {
        super();
        setClazz(RestriccionFacultad.class);
    }

    @Override
    public List<RestriccionFacultad> allBySeccion(Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(RestriccionFacultad.class, "rf")
                .join("facultad fac", "seccion sec")
                .filter("sec.id", seccion);
        return all(sql);
    }

    @Override
    public List<RestriccionFacultad> allActivasBySeccion(Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(RestriccionFacultad.class, "rf")
                .join("facultad fac", "seccion sec")
                .filter("rf.estado", EstadoEnum.ACT)
                .filter("sec.id", seccion);
        return all(sql);
    }

    @Override
    public List<RestriccionFacultad> allActivasBySecciones(List<Seccion> secciones) {
        Octavia sql = Octavia.query()
                .from(RestriccionFacultad.class, "rf")
                .join("facultad fac", "seccion sec")
                .filter("rf.estado", EstadoEnum.ACT)
                .in("sec.id", secciones);
        return all(sql);
    }

    @Override
    public void updateEstadoFechaUsuario(RestriccionFacultad restriccionFacultad) {
        Octavia octavia = Octavia.update(RestriccionFacultad.class);
        octavia.set(restriccionFacultad, "estado");
        octavia.set(restriccionFacultad, "usuarioModificacion");
        octavia.set(restriccionFacultad, "fechaModificacion");
        this.update(restriccionFacultad);
    }

    @Override
    public void deleteAllByCiclo(CicloAcademico ciclo) {

        StringBuilder sql = new StringBuilder();

        sql.append(" DELETE ").append(RestriccionFacultad.class.getName()).append(" rfa ")
                .append(" WHERE EXISTS ")
                .append(" ( ")
                .append(" SELECT 1 FROM ").append(Seccion.class.getName()).append(" sec ")
                .append(" JOIN sec.grupoSeccion gs ")
                .append(" JOIN gs.cicloAcademico ci ")
                .append(" WHERE ci.id=:CICLO ")
                .append("   AND rfa.seccion.id=sec.id ")
                .append(" ) ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("CICLO", ciclo.getId());
        query.executeUpdate();
    }

    @Override
    public int saveList(List<RestriccionFacultad> restricciones) {
        if (restricciones.isEmpty()) {
            return 0;
        }

        long t1 = System.currentTimeMillis();
        Insecto sql = Insecto.createInsert()
                .into(RestriccionFacultad.class)
                .columns("estado", "fechaRegistro", "fechaModificacion",
                        "seccion", "facultad", "usuarioRegistro", "usuarioModificacion")
                .values(restricciones);

        Query query = getCurrentSession().createSQLQuery(sql.toString());
        int rows = query.executeUpdate();

        long t2 = System.currentTimeMillis();
        logger.info("{} RestriccionFacultad's insertados en {} mseg....", rows, (t2 - t1));
        return rows;
    }

}
