package pe.edu.lamolina.amauta.dao.consejeria.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.consejeria.TutorSolicitudDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.TutorSolicitud;

@Repository
public class TutorSolicitudDAOH extends AbstractEasyDAO<TutorSolicitud> implements TutorSolicitudDAO {

    public TutorSolicitudDAOH() {
        super();
        setClazz(TutorSolicitud.class);
    }

    @Override
    public List<TutorSolicitud> allTutorSolicitudByFilter(DynatableFilter filter, CicloAcademico ciclo) {
        DynatableSql sql = new DynatableSql(filter)
                .from(TutorSolicitud.class, "ts")
                .join("alumnoConsejero ac")
                .join("ac.alumno alu", "ac.cicloAcademico ca", "ac.consejero co")
                .leftJoin("co.colaborador col", "col.persona")
                .join("alu.persona per", "per.tipoDocumento", "alu.carrera car", "car.facultad")
                .leftJoin("usuarioRegistra", "usuarioVerifica")
                .searchFields("alu.codigo", "per.numeroDocIdentidad")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .filter("ca.id", ciclo)
                .orderBy("ts.id");
        return all(sql);
    }

    @Override
    public void deleteByCiclo(CicloAcademico cicloAcademico) {

        StringBuilder sql = new StringBuilder();
        sql.append(" delete from ").append(TutorSolicitud.class.getSimpleName()).append(" as ts ");
        sql.append(" where ts.alumnoConsejero.id in  (");
        sql.append("     select  ac.id ");
        sql.append("      from ").append(AlumnoConsejero.class.getSimpleName()).append(" as ac ");
        sql.append("      where ac.cicloAcademico.id = :CICLO_ACADEMICO ");
        sql.append("  )");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("CICLO_ACADEMICO", cicloAcademico.getId());
        query.executeUpdate();

    }

}
