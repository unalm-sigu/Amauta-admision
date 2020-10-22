package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.controller.academico.graduado.GraduadoResumen;
import pe.edu.lamolina.amauta.dao.tramite.ObtencionGradoDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.enums.TipoGradoAcademicoEnum;
import static pe.edu.lamolina.model.enums.TipoGradoAcademicoEnum.BACH;
import static pe.edu.lamolina.model.enums.TipoGradoAcademicoEnum.DOC;
import static pe.edu.lamolina.model.enums.TipoGradoAcademicoEnum.MAE;
import static pe.edu.lamolina.model.enums.TipoGradoAcademicoEnum.TIT;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.ACEP;
import pe.edu.lamolina.model.tramite.ObtencionGrado;
import pe.edu.lamolina.model.tramite.Resolucion;

@Repository
public class ObtencionGradoDAOH extends AbstractEasyDAO<ObtencionGrado> implements ObtencionGradoDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ObtencionGradoDAOH() {
        super();
        setClazz(ObtencionGrado.class);
    }

    @Override
    public List<ObtencionGrado> allByResolucion(Resolucion resolucion) {
        Octavia sql = Octavia.query()
                .from(ObtencionGrado.class, "og")
                .join("resolucion re", "alumno alu", "alu.persona per", "alu.carrera ca", "ca.facultad")
                .join("cicloAcademico", "gradoAcademico", "tramite tr")
                .filter("tr.estado", ACEP)
                .leftJoin("per.tipoDocumento")
                .filter("re.id", resolucion);

        return all(sql);
    }

    @Override
    public List<ObtencionGrado> allByCarrerasDynatable(DynatableFilter filter, List<Carrera> carreras, String todo) {

        DynatableSql sql = new DynatableSql(filter)
                .from(ObtencionGrado.class, "og")
                .join("alumno al", "al.persona per", "al.carrera ca", "ca.modalidadEstudio moe", "ca.facultad fac")
                .join("cicloAcademico", "estadoTramite", "tramite tr", "tr.tipoTramite", "gradoAcademico ga")
                .leftJoin("resolucion res", "res.tipoResolucion")
                .leftJoin("al.situacionAcademica", "per.tipoDocumento tdoc")
                .searchFields("al.codigo", "per.numeroDocIdentidad")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("og.id desc");

        if (!"TODOS".equalsIgnoreCase(todo)) {
            sql.in("ca.id", carreras);
        }

        sql.beginRelativeFilters();
        setCondicionModalidad(filter, sql);

        return all(sql);
    }

    private void setCondicionModalidad(DynatableFilter filter, DynatableSql sql) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return;
        }
        for (String key : queries.keySet()) {
            if (!key.equals("tipo-grado")) {
                continue;
            }
            String tipoGrado = (String) queries.get(key);
            logger.debug("filter tipo-grado ** {}", tipoGrado);
            if (tipoGrado.equalsIgnoreCase("maestria")) {
                sql.filter("ga.tipo", MAE);
            } else if (tipoGrado.equalsIgnoreCase("doctorado")) {
                sql.filter("ga.tipo", DOC);
            } else if (tipoGrado.equalsIgnoreCase("bachiller")) {
                sql.filter("ga.tipo", BACH);
            } else if (tipoGrado.equalsIgnoreCase("titulo")) {
                sql.filter("ga.tipo", TIT);
            }
        }
    }

    @Override
    public GraduadoResumen findResumenGraduados(List<Carrera> carreras, String todo) {

        StringBuilder sql = new StringBuilder();

        sql.append("select new ").append(GraduadoResumen.class.getName());
        sql.append(" (   ");
        sql.append("   sum(case ga.tipo when :BACH then 1 else 0 end),   ");
        sql.append("   sum(case ga.tipo when :TIT then 1 else 0 end),   ");
        sql.append("   sum(case ga.tipo when :MAE then 1 else 0 end),   ");
        sql.append("   sum(case ga.tipo when :DOC then 1 else 0 end)   ");
        sql.append(" )   ");
        sql.append(" from ").append(ObtencionGrado.class.getName()).append(" as egr ");
        sql.append(" inner join egr.gradoAcademico ga ");
        sql.append(" inner join egr.alumno al ");
        sql.append(" inner join al.carrera ca ");
        sql.append(" where 1=1 ");

        if (!"TODOS".equalsIgnoreCase(todo)) {
            sql.append(" and  ca.id in: CARRERAS");
        }

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setString("MAE", MAE.name());
        query.setString("DOC", DOC.name());
        query.setString("BACH", BACH.name());
        query.setString("TIT", TIT.name());

        if (!"TODOS".equalsIgnoreCase(todo)) {
            query.setParameterList("CARRERAS", carreras.stream().map(Carrera::getId).collect(Collectors.toList()));
        }

        return (GraduadoResumen) query.uniqueResult();
    }

    @Override
    public ObtencionGrado findByAlumnoAndTipo(Alumno alumno, TipoGradoAcademicoEnum tipoGradoAcademicoEnum) {
        Octavia sql = Octavia.query()
                .from(ObtencionGrado.class, "og")
                .join("resolucion re", "alumno alu", "alu.persona per", "alu.carrera ca", "ca.facultad")
                .join("cicloAcademico", "gradoAcademico ga")
                .filter("ga.tipo", tipoGradoAcademicoEnum)
                .filter("alu.id", alumno);

        return find(sql);
    }

}
