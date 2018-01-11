package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import java.util.Map;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.EPG;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.ESP;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.VIS;
import pe.edu.lamolina.model.enums.PersonaEstadoEnum;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.controller.academico.alumno.AlumnoResumen;
import pe.edu.lamolina.pivot.controller.academico.matriculable.MatriculableResumen;

@Repository
public class AlumnoDAOH extends AbstractEasyDAO<Alumno> implements AlumnoDAO {

    public AlumnoDAOH() {
        super();
        setClazz(Alumno.class);
    }

    @Override
    public Alumno findByCodigo(String codigoAlumno) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per")
                .filter("alu.codigo", codigoAlumno);

        return find(sql);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.MANDATORY)
    public Alumno findLock(Long id) {
        return (Alumno) getCurrentSession().load(Alumno.class, id, LockOptions.UPGRADE);
    }

    @Override
    public List<Alumno> allByPersona(Persona persona) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per")
                .filter("per.id", persona);

        return all(sql);
    }

    @Override
    public List<Alumno> allByRolDynatable(DynatableFilter filter, String codigo, List<Long> filtros) {

        DynatableSql sql = new DynatableSql(filter)
                .from(Alumno.class, "al")
                .join("persona per", "per.tipoDocumento tdoc", "cicloIngreso ci", "cicloActivo cia", "carrera ca", "situacionAcademica sita")
                .join("ca.modalidadEstudio moe", "ca.facultad fac")
                .searchFields("ca.nombre", "al.estado", "al.codigo")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchSubqueryFields("ca.nombre")
                .orderBy("al.id desc");

        switch (RolEnum.valueOf(codigo)) {
            case MOD:
                sql.in("moe.id", filtros);
                break;
            case FAC:
                sql.in("fac.id", filtros);
                break;
            case ESP:
                sql.in("ca.id", filtros);
                break;
            default:
                break;
        }

        sql.beginRelativeFilters();
        setCondicionModalidad(filter, sql);

        List<Alumno> alumnos = sql.all(getCurrentSession());
        return alumnos;
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
            String values = (String) queries.get(key);
            if (values.equals("pregrado")) {
                sql.filter("moe.codigo", PRE);
            } else if (values.equals("postgrado")) {
                sql.filter("moe.codigo", EPG);
            } else if (values.equals("visitante")) {
                sql.filter("moe.codigo", VIS);
            } else if (values.equals("especiales")) {
                sql.filter("moe.codigo", ESP);
            }
        }

    }

    @Override
    public AlumnoResumen findResumen() {
        StringBuilder sql = new StringBuilder();

        sql.append("select new ").append(AlumnoResumen.class.getName());
        sql.append(" (   ");
        sql.append("   sum(case moe.codigo when :PRE then 1 else 0 end),   ");
        sql.append("   sum(case moe.codigo when :EPG then 1 else 0 end),   ");
        sql.append("   sum(case moe.codigo when :VIS  then 1 else 0 end),   ");
        sql.append("   sum(case moe.codigo when :ESP  then 1 else 0 end)   ");
        sql.append(" )   ");
        sql.append("  from ").append(Alumno.class.getName()).append(" as al ");
        sql.append(" inner join al.carrera ca ");
        sql.append(" inner join al.cicloActivo cia ");
        sql.append("  inner join ca.modalidadEstudio moe ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setString("PRE", PRE.name());
        query.setString("EPG", EPG.name());
        query.setString("VIS", VIS.name());
        query.setString("ESP", ESP.name());

        return (AlumnoResumen) query.uniqueResult();
    }

    @Override
    public List<Alumno> allByCicloRolDynatable(DynatableFilter filter, CicloAcademico cicloAcademico, String codigo, List<Long> filtros) {

        DynatableSql sql = new DynatableSql(filter);
        switch (RolEnum.valueOf(codigo)) {
            case TODO:
                sql.from(Alumno.class, "al")
                        .join("persona per", "per.tipoDocumento tdoc", "cicloIngreso ci", "cicloActivo cia", "carrera ca", "situacionAcademica sita")
                        .join("ca.modalidadEstudio moe", "ca.facultad fac")
                        .filter("cia.id", cicloAcademico)
                        .searchFields("ca.nombre", "al.estado", "al.codigo")
                        .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                        .searchSubqueryFields("ca.nombre")
                        .orderBy("al.id desc");
                break;
            case MOD:
                sql.from(Alumno.class, "al")
                        .join("persona per", "per.tipoDocumento tdoc", "cicloIngreso ci", "cicloActivo cia", "carrera ca", "situacionAcademica sita")
                        .join("ca.modalidadEstudio moe", "ca.facultad fac")
                        .filter("cia.id", cicloAcademico)
                        .searchFields("ca.nombre", "al.estado", "al.codigo")
                        .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                        .searchSubqueryFields("ca.nombre")
                        .in("moe.id", filtros)
                        .orderBy("al.id desc");
                break;
            case FAC:
                sql.from(Alumno.class, "al")
                        .join("persona per", "per.tipoDocumento tdoc", "cicloIngreso ci", "cicloActivo cia", "carrera ca", "situacionAcademica sita")
                        .join("ca.modalidadEstudio moe", "ca.facultad fac")
                        .filter("cia.id", cicloAcademico)
                        .searchFields("ca.nombre", "al.estado", "al.codigo")
                        .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                        .searchSubqueryFields("ca.nombre")
                        .in("fac.id", filtros)
                        .orderBy("al.id desc");
                break;
            case ESP:
                sql.from(Alumno.class, "al")
                        .join("persona per", "per.tipoDocumento tdoc", "cicloIngreso ci", "cicloActivo cia", "carrera ca", "situacionAcademica sita")
                        .join("ca.modalidadEstudio moe", "ca.facultad fac")
                        .filter("cia.id", cicloAcademico)
                        .searchFields("ca.nombre", "al.estado", "al.codigo")
                        .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                        .searchSubqueryFields("ca.nombre")
                        .in("ca.id", filtros)
                        .orderBy("al.id desc");
                break;
            default:
                sql.from(Alumno.class, "al")
                        .join("persona per", "per.tipoDocumento tdoc", "cicloIngreso ci", "cicloActivo cia", "carrera ca", "situacionAcademica sita")
                        .join("ca.modalidadEstudio moe", "ca.facultad fac")
                        .filter("cia.id", cicloAcademico)
                        .searchFields("ca.nombre", "al.estado", "al.codigo")
                        .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                        .searchSubqueryFields("ca.nombre")
                        .orderBy("al.id desc");
                break;
        }

        sql.beginRelativeFilters();
        setCondicionModalidad(filter, sql);

        return sql.all(getCurrentSession());

    }

    @Override
    public MatriculableResumen findResumenByCiclo(CicloAcademico cicloAcademico) {
        StringBuilder sql = new StringBuilder();

        sql.append("select new ").append(MatriculableResumen.class.getName());
        sql.append(" (   ");
        sql.append("   sum(case moe.codigo when :PRE then 1 else 0 end),   ");
        sql.append("   sum(case moe.codigo when :EPG then 1 else 0 end),   ");
        sql.append("   sum(case moe.codigo when :VIS  then 1 else 0 end),   ");
        sql.append("   sum(case moe.codigo when :ESP  then 1 else 0 end)   ");
        sql.append(" )   ");
        sql.append("  from ").append(Alumno.class.getName()).append(" as al ");
        sql.append(" inner join al.carrera ca ");
        sql.append(" inner join al.cicloActivo cia ");
        sql.append(" inner join ca.modalidadEstudio moe ");
        sql.append(" where cia.id = :CICLO ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setString("PRE", PRE.name());
        query.setString("EPG", EPG.name());
        query.setString("VIS", VIS.name());
        query.setString("ESP", ESP.name());
        query.setLong("CICLO", cicloAcademico.getId());

        return (MatriculableResumen) query.uniqueResult();
    }

    @Override
    public List<Alumno> allAlumnoByName(String nombre) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per", "carrera car", "car.facultad fa")
                .leftJoin("per.tipoDocumento td")
                .filter("per.estado", PersonaEstadoEnum.ACT)
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("per.numeroDocIdentidad", "like", nombre)
                .endBlock()
                .limit(15);
        return sql.all(getCurrentSession());
    }

    @Override
    public Alumno findByPersona(Persona persona, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per", "carrera car", "car.facultad fa")
                .leftJoin("per.tipoDocumento td", "cicloActivo ci")
                .filter("per.id", persona)
                .filter("ci.id", cicloAcademico);
        return (Alumno) sql.find(getCurrentSession());
    }

    @Override
    public List<Alumno> allIngresantePregradoByCiclo(ModalidadEstudio modalidad, CicloAcademico cicloAcademico, List<Alumno> alumnoExclude) {

        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per", "carrera car", "car.facultad fa", "modalidadEstudio moe")
                .leftJoin("per.tipoDocumento td", "cicloIngreso ci")
                .filter("moe.id", modalidad);
        if (!(alumnoExclude == null || alumnoExclude.isEmpty())) {
            sql.notIn("alu.id", alumnoExclude);
        }
        sql.filter("ci.id", cicloAcademico);
        return all(sql);
    }

    @Override
    public List<Alumno> allAlumnoIngresantePregradoByNameCiclo(String nombre, ModalidadEstudio modalidad, CicloAcademico cicloAcademico) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per", "carrera car", "car.facultad fa", "modalidadEstudio moe")
                .leftJoin("per.tipoDocumento td", "cicloIngreso ci")
                .filter("per.estado", PersonaEstadoEnum.ACT)
                .filter("moe.id", modalidad)
                .filter("ci.id", cicloAcademico)
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("per.numeroDocIdentidad", "like", nombre)
                .endBlock()
                .limit(15);
        return sql.all(getCurrentSession());
    }

}
