package pe.edu.lamolina.amauta.dao.consejeria.hibernate;

import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import org.springframework.stereotype.Service;
import pe.albatross.octavia.Insecto;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.controller.consejeria.administracion.view.FiltroReporteAgendaDTO;
import pe.edu.lamolina.amauta.dao.consejeria.ReunionAlumnoConsejeroDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.AgendaConsejero;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.consejeria.ReunionAlumnoConsejero;
import static pe.edu.lamolina.model.constantines.GlobalConstantine.ID_CONSEJERO_NN;
import pe.edu.lamolina.model.enums.AgendaConsejeroEstadoEnum;
import pe.edu.lamolina.model.enums.ReunionAlumnoConsejeroEstadoEnum;
import static pe.edu.lamolina.model.enums.ReunionAlumnoConsejeroEstadoEnum.AGEN;

@Service
public class ReunionAlumnoConsejeroDAOH extends AbstractEasyDAO<ReunionAlumnoConsejero> implements ReunionAlumnoConsejeroDAO {

    public ReunionAlumnoConsejeroDAOH() {
        super();
        setClazz(ReunionAlumnoConsejero.class);
    }

    @Override
    public int saveList(List<ReunionAlumnoConsejero> reunionAlumnoConsejeros) {
        if (reunionAlumnoConsejeros.isEmpty()) {
            return 0;
        }

        long t1 = System.currentTimeMillis();
        Insecto sql = Insecto.createInsert()
                .into(ReunionAlumnoConsejero.class)
                .columns("estado", "alumnoConsejero", "agendaConsejero", "fechaRegistro", "userRegistro")
                .values(reunionAlumnoConsejeros);

        Query query = getCurrentSession().createSQLQuery(sql.toString());
        int rows = query.executeUpdate();

        long t2 = System.currentTimeMillis();
        return rows;
    }

    @Override
    public List<ReunionAlumnoConsejero> allByAgendaConsejero(AgendaConsejero agendaConsejeroForm) {
        Octavia sql = new Octavia()
                .from(ReunionAlumnoConsejero.class, "rac")
                .join("agendaConsejero ac", "ac.consejero")
                .filter("rac.estado", AGEN)
                .filter("ac.id", agendaConsejeroForm);

        return all(sql);
    }

    @Override
    public List<ReunionAlumnoConsejero> allDynatableByConsejero(DynatableFilter filter, Consejero consejero, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(ReunionAlumnoConsejero.class, "rac")
                .join("alumnoConsejero ac", "agendaConsejero acon", "ac.cicloAcademico ca")
                .join("ac.alumno al", "al.persona per", "per.tipoDocumento")
                .join("ac.consejero con", "acon.hora")
                .join("al.carrera car", "car.facultad")
                .searchFields("acon.titulo", "al.codigo", "per.numeroDocIdentidad", "acon.fecha")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .filter("ca.id", cicloAcademico)
                .filter("con.id", consejero)
                .orderBy("acon.fecha", "acon.hora");
        setCondicion(filter, sql);

        return all(sql);
    }

    private void setCondicion(DynatableFilter filter, DynatableSql sql) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return;
        }
        for (String key : queries.keySet()) {
            if (key.equals("search")) {
                continue;
            }
            String value = (String) queries.get(key);
            switch (value) {
                case "carrera":
                    sql.filter("car.id", new Long(value));
                    break;
                case "consejero":
                    sql.filter("con.id", new Long(value));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void deleteByCiclo(CicloAcademico cicloAcademico) {

        StringBuilder sql = new StringBuilder();
        sql.append(" delete from ").append(ReunionAlumnoConsejero.class.getSimpleName()).append(" as rac ");
        sql.append(" where rac.alumnoConsejero.id in  (");
        sql.append("     select  ac.id ");
        sql.append("      from ").append(AlumnoConsejero.class.getSimpleName()).append(" as ac ");
        sql.append("      where ac.cicloAcademico.id = :CICLO_ACADEMICO ");
        sql.append("  )");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("CICLO_ACADEMICO", cicloAcademico.getId());
        query.executeUpdate();
    }

    @Override
    public List<ReunionAlumnoConsejero> allDynatableByCicloAcademico(DynatableFilter filter, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(ReunionAlumnoConsejero.class, "rac")
                .join("alumnoConsejero ac", "agendaConsejero acon", "ac.cicloAcademico ca")
                .join("ac.alumno al", "al.persona per", "per.tipoDocumento")
                .join("ac.consejero con", "acon.hora")
                .join("al.carrera car", "car.facultad")
                .searchFields("acon.asunto", "acon.cuerpo", "al.codigo", "per.numeroDocIdentidad", "acon.fecha")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .filter("ca.id", cicloAcademico)
                .orderBy("acon.fecha", "acon.hora");

        return all(sql);
    }

    @Override
    public List<ReunionAlumnoConsejero> allReunionAlumnoConsejeroReporte(FiltroReporteAgendaDTO filtroReporteAgendaDTO, CicloAcademico cicloAcademico) {

        Octavia sql = new Octavia()
                .from(ReunionAlumnoConsejero.class, "rac")
                .join("alumnoConsejero ac", "agendaConsejero acon", "ac.cicloAcademico ca")
                .join("ac.alumno al", "al.persona per", "per.tipoDocumento")
                .join("ac.consejero con", "acon.hora")
                .join("al.carrera car", "car.facultad")
                .filter("ca.id", cicloAcademico)
                .filter("acon.estado", "<>", AgendaConsejeroEstadoEnum.ANU)
                .filter("rac.estado", "<>", ReunionAlumnoConsejeroEstadoEnum.ANU)
                .orderBy("acon.fecha", "acon.hora");

        if (filtroReporteAgendaDTO.getCarrera() != null) {
            sql.filter("car.id", filtroReporteAgendaDTO.getCarrera());
        }

        if (filtroReporteAgendaDTO.getConsejero() != null) {
            sql.filter("con.id", filtroReporteAgendaDTO.getConsejero());
        }

        return all(sql);

    }

}
