package pe.edu.lamolina.pivot.dao.academico.hibernate;

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
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ControlMeritoEgresado;
import pe.edu.lamolina.model.academico.Egresado;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.EPG;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.ESP;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.VIS;
import pe.edu.lamolina.pivot.controller.academico.egresado.EgresadoResumen;
import pe.edu.lamolina.pivot.dao.academico.EgresadoDAO;

@Repository
public class EgresadoDAOH extends AbstractEasyDAO<Egresado> implements EgresadoDAO {

    public EgresadoDAOH() {
        super();
        setClazz(Egresado.class);
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Egresado findByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(Egresado.class, "e")
                .join("alumno alu")
                .filter("alu.id", alumno);

        return find(sql);
    }

    @Override
    public Egresado findPrincipalByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(Egresado.class, "e")
                .join("alumno alu")
                //.filter("esPrincipal", BigDecimal.ONE.intValue())
                .filter("alu.id", alumno);
        return find(sql);
    }

    @Override
    public List<Egresado> allByCicloAcademico(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(Egresado.class, "ac")
                .join("cicloAcademico ca", "alumno alu")
                .join("alu.persona per", "carrera car", "car.facultad fac")
                .filter("cicloAcademico", ciclo);

        return sql.all(getCurrentSession());
    }

    @Override
    public List<Egresado> allByControlesOrdenMerito(List<ControlMeritoEgresado> coms) {
        Octavia sql = Octavia.query()
                .from(Egresado.class, "ac")
                .join("cicloAcademico ca", "alumno alu")
                .join("alu.persona per", "carrera car", "car.facultad fac")
                .left("controlMeritoCiclo cmc", "controlMeritoFacultad cmf", "controlMeritoCarrera cmca")
                .isNotNull("promedioAcumulado")
                .beginBlock()
                .__().in("cmc.id", coms)
                .__().in("cmf.id", coms)
                .__().in("cmca.id", coms)
                .endBlock();

        return sql.all(getCurrentSession());
    }

    @Override
    public void deleteInfoOrdenMeritoByCicloAcademico(CicloAcademico cicloAcademico) {
        StringBuilder sql = new StringBuilder();

        sql.append("update Egresado set ");

        sql.append("controlMeritoCarrera = null, ");
        sql.append("controlMeritoCiclo = null, ");
        sql.append("controlMeritoFacultad = null, ");

        sql.append("ordenMeritoCarrera = null, ");
        sql.append("ordenMeritoCiclo = null, ");
        sql.append("ordenMeritoFacultad = null, ");

        sql.append("cuadroHonorCarrera = null, ");
        sql.append("cuadroHonorCiclo = null, ");
        sql.append("cuadroHonorFacultad = null, ");

        sql.append("quintoSuperiorCarrera = null, ");
        sql.append("quintoSuperiorCiclo = null, ");
        sql.append("quintoSuperiorFacultad = null, ");

        sql.append("tercioSuperiorCarrera = null, ");
        sql.append("tercioSuperiorCiclo = null, ");
        sql.append("tercioSuperiorFacultad = null ");

        sql.append("where cicloAcademico.id = :CICLO");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("CICLO", cicloAcademico.getId());
        query.executeUpdate();
    }

    @Override
    public List<Egresado> allByControlMeritoCiclo(DynatableFilter filter, ControlMeritoEgresado controlBD) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Egresado.class, "ac")
                .join("alumno alu", "alu.persona per", "carrera car", "car.facultad fac")
                .join("cicloAcademico ca")
                .join("controlMeritoCiclo control")
                .filter("control.id", controlBD)
                .searchFields("alu.codigo", "car.nombre", "fac.nombre")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("ac.ordenMeritoCiclo");

        return all(sql);
    }

    @Override
    public List<Egresado> allByControlMeritoCarrera(DynatableFilter filter, ControlMeritoEgresado controlBD) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Egresado.class, "ac")
                .join("alumno alu", "alu.persona per", "carrera car", "car.facultad fac")
                .join("cicloAcademico ca")
                .join("controlMeritoCarrera control")
                .filter("control.id", controlBD)
                .searchFields("alu.codigo", "car.nombre", "fac.nombre")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("ac.ordenMeritoCarrera");

        return all(sql);
    }

    @Override
    public List<Egresado> allByControlMeritoFacultad(DynatableFilter filter, ControlMeritoEgresado controlBD) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Egresado.class, "ac")
                .join("alumno alu", "alu.persona per", "carrera car", "car.facultad fac")
                .join("cicloAcademico ca")
                .join("controlMeritoFacultad control")
                .filter("control.id", controlBD)
                .searchFields("alu.codigo", "car.nombre", "fac.nombre")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("ac.ordenMeritoFacultad");

        return all(sql);
    }

    @Override
    public List<Egresado> allByAlumnos(List<Alumno> alumnos) {
        Octavia sql = Octavia.query()
                .from(Egresado.class, "e")
                .join("alumno alu")
                .in("alu.id", alumnos);

        return all(sql);
    }

    @Override
    public List<Egresado> allByCarrerasDynatable(DynatableFilter filter, List<Carrera> carreras, String todo) {

        DynatableSql sql = new DynatableSql(filter)
                .from(Egresado.class, "egr")
                .join("alumno al", "al.persona per", "al.carrera ca", "ca.modalidadEstudio moe", "ca.facultad fac")
                .leftJoin("al.situacionAcademica sita", "per.tipoDocumento tdoc", "al.cicloIngreso ci", "al.cicloActivo cia")
                .searchFields("ca.nombre", "al.estado", "al.codigo", "per.numeroDocIdentidad")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("al.id desc");

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
            if (!key.equals("moe.codigo")) {
                continue;
            }
            String codigoo = (String) queries.get(key);
            logger.debug("filter codigo ** {}", codigoo);
            if (codigoo.equalsIgnoreCase("pregrado")) {
                sql.filter("moe.codigo", PRE);
            } else if (codigoo.equalsIgnoreCase("posgrado")) {
                sql.filter("moe.codigo", EPG);
            } else if (codigoo.equalsIgnoreCase("visitante")) {
                sql.filter("moe.codigo", VIS);
            } else if (codigoo.equalsIgnoreCase("especial")) {
                sql.filter("moe.codigo", ESP);
            }
        }
    }

    @Override
    public EgresadoResumen findResumenEgresado(List<Carrera> carreras, String todo) {

        StringBuilder sql = new StringBuilder();

        sql.append("select new ").append(EgresadoResumen.class.getName());
        sql.append(" (   ");
        sql.append("   sum(case moe.codigo when :PRE then 1 else 0 end),   ");
        sql.append("   sum(case moe.codigo when :EPG then 1 else 0 end)   ");
        sql.append(" )   ");
        sql.append(" from ").append(Egresado.class.getName()).append(" as egr ");
        sql.append(" inner join egr.alumno al ");
        sql.append(" inner join al.carrera ca ");
        sql.append(" left join al.cicloActivo cia ");
        sql.append(" inner join ca.modalidadEstudio moe ");
        sql.append(" where 1=1 ");
        
        if (!"TODOS".equalsIgnoreCase(todo)) {
            sql.append(" and  ca.id in: CARRERAS");
        }

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setString("PRE", PRE.name());
        query.setString("EPG", EPG.name());
        
        if (!"TODOS".equalsIgnoreCase(todo)) {
            query.setParameterList("CARRERAS", carreras.stream().map(Carrera::getId).collect(Collectors.toList()));
        }

        return (EgresadoResumen) query.uniqueResult();
    }

}
