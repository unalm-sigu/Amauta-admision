package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.consejeria.Consejero;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.NMAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RCI;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.EPG;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.ESP;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.VIS;
import pe.edu.lamolina.model.enums.PersonaEstadoEnum;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.enums.SituacionAcademicaEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.controller.academico.alumno.AlumnoResumen;
import pe.edu.lamolina.pivot.controller.matricula.matriculable.MatriculableResumen;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;

@Repository
public class AlumnoDAOH extends AbstractEasyDAO<Alumno> implements AlumnoDAO {

    public AlumnoDAOH() {
        super();
        setClazz(Alumno.class);
    }

    @Override
    public Alumno findLock(Long id) {
        StringBuilder sql = new StringBuilder()
                .append("select {a.*} from aca_alumno as a where a.id = :ID_ALUMNO for update ");
        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addEntity("a", Alumno.class);

        query.setParameter("ID_ALUMNO", id);

        return (Alumno) query.uniqueResult();
    }

    @Override
    public Alumno find(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per", "carrera car", "car.facultad fa")
                .leftJoin("per.tipoDocumento td", "cicloActivo cia", "cicloIngreso ci", "modalidadEstudio me", "situacionAcademica situ")
                .filter("alu.id", alumno);
        return (Alumno) sql.find(getCurrentSession());
    }

    @Override
    public Alumno findAllInfo(Long id) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("modalidadEstudio me", "carrera ca", "ca.facultad")
                .left("planCurricular pc", "situacionAcademica sa", "pc.cicloInicioVigencia", "pc.carrera")
                .left("cicloIngreso", "cicloActivo", "postulantePregrado pp", "pp.modalidadIngreso mi")
                .left("orientacionCarrera")
                .filter("alu.id", id);

        return (Alumno) sql.find(getCurrentSession());
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
    public Alumno findFlatByCodigo(String codigoAlumno) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .filter("alu.codigo", codigoAlumno);

        return find(sql);
    }

    @Override
    public Alumno findByPersonaCicloIngreso(Persona persona, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per", "carrera car", "car.facultad fa", "cicloIngreso ci")
                .leftJoin("per.tipoDocumento td")
                .filter("per.id", persona)
                .filter("ci.id", ciclo);
        return (Alumno) sql.find(getCurrentSession());
    }

    @Override
    public Alumno findSituacionAcademica(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("situacionAcademica sia")
                .filter("alu.id", alumno);
        return find(sql);
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
    public Alumno findByPersona(Persona persona) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per", "carrera car", "car.facultad fa")
                .leftJoin("per.tipoDocumento td", "cicloActivo ci")
                .filter("per.id", persona);
        return (Alumno) sql.find(getCurrentSession());
    }

    @Override
    public Long countByPlanCurricular(PlanCurricular plan) {
        Octavia sql = Octavia.query()
                .selectCount()
                .from(Alumno.class, "alu")
                .join("planCurricular pc")
                .filter("alu.planCurricular", plan);

        return (Long) sql.find(getCurrentSession());
    }

    @Override
    public List<Alumno> allByFacultadDynatable(DynatableFilter filter, List<Carrera> carrera) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Alumno.class, "al")
                .join("persona per", "carrera ca", "ca.modalidadEstudio moe", "ca.facultad fac")
                .leftJoin("situacionAcademica sita", "per.tipoDocumento tdoc", "cicloIngreso ci", "cicloActivo cia")
                .in("ca.id", carrera)
                .searchFields("ca.nombre", "al.estado", "al.codigo", "per.numeroDocIdentidad")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("al.id desc");

        sql.beginRelativeFilters();
        setCondicionModalidad(filter, sql);

        return all(sql);
    }

    @Override
    public List<Alumno> allByModalidadesDynatable(DynatableFilter filter, CicloAcademico cicloAcademico, List<String> modalidades) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Alumno.class, "al")
                .join("persona per", "carrera ca", "ca.modalidadEstudio moe", "ca.facultad fac", "al.cicloActivo aca")
                .leftJoin("situacionAcademica sita", "per.tipoDocumento tdoc", "cicloIngreso ci")
                .searchFields("ca.nombre", "al.estado", "al.codigo", "per.numeroDocIdentidad")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                // .filter("ca.id", cicloAcademico)
                .in("moe.codigo", modalidades)
                .orderBy("al.id desc");

        sql.beginRelativeFilters();
        setCondicionModalidad(filter, sql);

        return all(sql);
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
    public List<Alumno> allByPlanCurricular(PlanCurricular planCurricular) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("planCurricular pc")
                .filter("planCurricular", planCurricular);

        return all(sql);
    }

    @Override
    public List<Alumno> allByRolDynatable(DynatableFilter filter, List<Carrera> carreras) {

        DynatableSql sql = new DynatableSql(filter)
                .from(Alumno.class, "al")
                .join("persona per", "carrera ca", "ca.modalidadEstudio moe", "ca.facultad fac")
                .leftJoin("situacionAcademica sita", "per.tipoDocumento tdoc", "cicloIngreso ci", "cicloActivo cia")
                .in("ca.id", carreras)
                .searchFields("ca.nombre", "al.estado", "al.codigo", "per.numeroDocIdentidad")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("al.id desc");

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
            } else if (values.equals("especial")) {
                sql.filter("moe.codigo", ESP);
            }
        }

    }

    @Override
    public List<Alumno> allByCicloRolDynatable(DynatableFilter filter, CicloAcademico ciclo, String codigo, List<Long> filtros) {

        DynatableSql sql = new DynatableSql(filter);
        switch (RolEnum.valueOf(codigo)) {
            case TODO:
                sql.from(Alumno.class, "al")
                        .join("persona per", "per.tipoDocumento tdoc", "carrera ca", "situacionAcademica sita")
                        .join("ca.modalidadEstudio moe", "ca.facultad fac")
                        .leftJoin("cicloIngreso ci", "cicloActivo cia")
                        .filter("cia.id", ciclo)
                        .searchFields("ca.nombre", "al.estado", "al.codigo")
                        .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                        .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                        .searchSubqueryFields("ca.nombre")
                        .orderBy("al.id desc");
                break;
            case MOD:
                sql.from(Alumno.class, "al")
                        .join("persona per", "per.tipoDocumento tdoc", "cicloIngreso ci", "cicloActivo cia", "carrera ca", "situacionAcademica sita")
                        .join("ca.modalidadEstudio moe", "ca.facultad fac")
                        .filter("cia.id", ciclo)
                        .searchFields("ca.nombre", "al.estado", "al.codigo")
                        .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                        .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                        .searchSubqueryFields("ca.nombre")
                        .in("moe.id", filtros)
                        .orderBy("al.id desc");
                break;
            case FAC:
                sql.from(Alumno.class, "al")
                        .join("persona per", "per.tipoDocumento tdoc", "cicloIngreso ci", "cicloActivo cia", "carrera ca", "situacionAcademica sita")
                        .join("ca.modalidadEstudio moe", "ca.facultad fac")
                        .filter("cia.id", ciclo)
                        .searchFields("ca.nombre", "al.estado", "al.codigo")
                        .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                        .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                        .searchSubqueryFields("ca.nombre")
                        .in("fac.id", filtros)
                        .orderBy("al.id desc");
                break;
            case ESP:
                sql.from(Alumno.class, "al")
                        .join("persona per", "per.tipoDocumento tdoc", "cicloIngreso ci", "cicloActivo cia", "carrera ca", "situacionAcademica sita")
                        .join("ca.modalidadEstudio moe", "ca.facultad fac")
                        .filter("cia.id", ciclo)
                        .searchFields("ca.nombre", "al.estado", "al.codigo")
                        .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                        .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                        .searchSubqueryFields("ca.nombre")
                        .in("ca.id", filtros)
                        .orderBy("al.id desc");
                break;
            default:
                sql.from(Alumno.class, "al")
                        .join("persona per", "per.tipoDocumento tdoc", "cicloIngreso ci", "cicloActivo cia", "carrera ca", "situacionAcademica sita")
                        .join("ca.modalidadEstudio moe", "ca.facultad fac")
                        .filter("cia.id", ciclo)
                        .searchFields("ca.nombre", "al.estado", "al.codigo")
                        .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                        .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                        .searchSubqueryFields("ca.nombre")
                        .orderBy("al.id desc");
                break;
        }

        sql.beginRelativeFilters();
        setCondicionModalidad(filter, sql);

        return sql.all(getCurrentSession());

    }

    @Override
    public List<Alumno> allByName(String nombre) {
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
                .__().filter("alu.codigo", "like", nombre)
                .endBlock()
                .limit(15);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Alumno> allDesertorByName(String nombre, Long idInstancia) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per", "carrera car", "car.facultad fa")
                .join("situacionAcademica sa", "modalidadEstudio me")
                .leftJoin("per.tipoDocumento td")
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("per.numeroDocIdentidad", "like", nombre)
                .__().filter("alu.codigo", "like", nombre)
                .endBlock()
                .filter("per.estado", PersonaEstadoEnum.ACT)
                .filter("sa.codigo", SituacionAcademicaEnum.S_D.getValue())
                .filter("fa.id", idInstancia)
                .limit(15);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Alumno> allIngresantePregradoByCiclo(ModalidadEstudio modalidad, CicloAcademico ciclo, List<Alumno> existentes) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per", "carrera car", "car.facultad fa", "modalidadEstudio moe", "situacionAcademica sa")
                .leftJoin("per.tipoDocumento td", "cicloIngreso ci")
                .filter("sa.codigo", SituacionAcademicaEnum.S_8.getValue())
                .filter("moe.id", modalidad)
                .filter("ci.id", ciclo);

        if (!(existentes == null || existentes.isEmpty())) {
            sql.notIn("alu.id", existentes);
        }

        return all(sql);
    }

    @Override
    public List<Alumno> allByNameModalidadEstudioCiclo(String nombre, ModalidadEstudio modalidad, CicloAcademico cicloAcademico) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per", "carrera car", "car.facultad fa", "modalidadEstudio moe")
                .leftJoin("per.tipoDocumento td", "cicloIngreso ci")
                .filter("per.estado", PersonaEstadoEnum.ACT)
                .filter("moe.id", modalidad)
                //                .filter("ci.id", cicloAcademico)
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("per.numeroDocIdentidad", "like", nombre)
                .__().filter("alu.codigo", "like", nombre)
                .endBlock()
                .limit(15);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Alumno> allByPersonas(List<Persona> personas) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per", "carrera car", "car.facultad fa")
                .leftJoin("per.tipoDocumento td", "cicloActivo ci")
                .in("per.id", personas);
        return sql.all(getCurrentSession());

    }

    @Override
    public List<Alumno> allBySituaciones(ModalidadEstudio modalidad, List<SituacionAcademica> situaciones) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("modalidadEstudio me", "situacionAcademica sa", "cicloActivo ca")
                .join("persona per", "carrera car", "car.facultad fa")
                .leftJoin("per.tipoDocumento td", "cicloActivo cia", "cicloIngreso ci")
                .filter("me.id", modalidad)
                .in("sa.id", situaciones);
        return all(sql);
    }

    @Override
    public List<Alumno> allByNombreFacultad(String nombre, List<Facultad> facultad) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per", "carrera car", "car.facultad fa", "situacionAcademica sa")
                .join("modalidadEstudio me")
                .leftJoin("per.tipoDocumento td", "orientacionCarrera oc")
                .in("fa.id", facultad)
                .filter("per.estado", PersonaEstadoEnum.ACT)
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("per.numeroDocIdentidad", "like", nombre)
                .__().filter("alu.codigo", "like", nombre)
                .endBlock()
                .limit(15);

        return sql.all(getCurrentSession());
    }

    @Override
    public List<Alumno> allByIds(Long[] idAlumnos) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per", "carrera car", "car.facultad fa")
                .leftJoin("per.tipoDocumento td", "cicloActivo ci", "modalidadEstudio me", "situacionAcademica situ")
                .in("alu.id", idAlumnos);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Alumno> allByNameSinMatriculaResumen(String nombre, CicloAcademico cicloAcademico) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia subQuery = new Octavia()
                .from(MatriculaResumen.class, "mr")
                .join("alumno alum")
                .filter("cicloAcademico", cicloAcademico);

        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per", "carrera car", "car.facultad fa")
                .leftJoin("per.tipoDocumento td")
                .filter("per.estado", PersonaEstadoEnum.ACT)
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("per.numeroDocIdentidad", "like", nombre)
                .__().filter("alu.codigo", "like", nombre)
                .endBlock()
                .notExists(subQuery)
                .linkedBy("alu.id", "alum.id")
                .limit(15);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Alumno> allByCarreraCicloMayores(Carrera carrera, String codigoCiclo) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per", "carrera car", "car.facultad fa")
                .leftJoin("per.tipoDocumento td", "cicloActivo ci")
                .leftJoin("modalidadEstudio me", "situacionAcademica situ")
                .leftJoin("cicloIngreso cci")
                .filter("car.id", carrera)
                .filter("cci.codigo", ">=", codigoCiclo);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Alumno> allByGpoSeccion(GrupoSeccion gpoSecc) {
        Octavia sql = Octavia.query()
                .selectDistinct("alu")
                .from(MatriculaSeccion.class, "ms")
                .join("seccion s", "s.grupoSeccion gs", "matriculaResumen mr")
                .join("mr.alumno alu", "alu.persona", "alu.modalidadEstudio", "alu.situacionAcademica")
                .left("alu.cicloActivoRegular")
                .filter("gs.id", gpoSecc);
        return sql.all(getCurrentSession());
    }

    @Override
    public void updateCicloActivoSituacionAcad(Alumno alumno) {
        Octavia octavia = Octavia.update(Alumno.class);
        octavia.set(alumno, "situacionAcademica");
        octavia.set(alumno, "cicloActivo");
        this.update(octavia);
    }

    @Override
    public void updateSituacionAcad(Alumno alumno) {
        Octavia octavia = Octavia.update(Alumno.class);
        octavia.set(alumno, "situacionAcademica");
        this.update(octavia);
    }

    @Override
    public void updateSituacionCicloCapa(Alumno alumno) {
        Octavia octavia = Octavia.update(Alumno.class);
        octavia.set(alumno, "situacionAcademica");
        octavia.set(alumno, "creditosAprobados");
        octavia.set(alumno, "cicloActivo");
        octavia.set(alumno, "creditosCursados");
        this.update(octavia);
    }

    @Override
    public void updateSituacionCicloCapaPPA(Alumno alumno) {
        Octavia octavia = Octavia.update(Alumno.class);
        octavia.set(alumno, "situacionAcademica");
        octavia.set(alumno, "creditosAprobados");
        octavia.set(alumno, "cicloActivo");
        octavia.set(alumno, "creditosCursados");
        octavia.set(alumno, "promedioAcumulado");
        this.update(octavia);
    }

    @Override
    public void updateSituacionCapaCredCur(Alumno alumno) {
        Octavia octavia = Octavia.update(Alumno.class);
        octavia.set(alumno, "situacionAcademica");
        octavia.set(alumno, "creditosAprobados");
        octavia.set(alumno, "creditosCursados");
        this.update(octavia);
    }

    @Override
    public void updateCicloActivoRegular(Alumno alumno) {
        Octavia octavia = Octavia.update(Alumno.class);
        octavia.set(alumno, "cicloActivoRegular");
        this.update(octavia);
    }

    @Override
    public void updateFields(Alumno alumno, String[] fields) {
        Octavia octavia = Octavia.update(Alumno.class);
        for (String field : fields) {
            octavia.set(alumno, field);
        }
        this.update(octavia);
    }

    @Override
    public void updatePromedioProcesado(Alumno alumno) {
        Octavia octavia = Octavia.update(Alumno.class);
        octavia.set(alumno, "promedioProcesado");
        this.update(octavia);
    }

    @Override
    public void updatePlanCurricular(Alumno alumno) {
        Octavia sql = Octavia.update(Alumno.class);
        sql.set(alumno, "planCurricular");
        sql.set(alumno, "orientacionCarrera");
        this.update(sql);
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
    public MatriculableResumen findResumenByCiclo(CicloAcademico cicloAcademico) {
        StringBuilder sql = new StringBuilder();

        sql.append("select new ").append(MatriculableResumen.class.getName());
        sql.append(" (   ");
        sql.append("   COALESCE(sum(case moe.codigo when :PRE then 1 else 0 end),0),   ");
        sql.append("   COALESCE(sum(case moe.codigo when :EPG then 1 else 0 end),0),   ");
        sql.append("   COALESCE(sum(case moe.codigo when :VIS  then 1 else 0 end),0),   ");
        sql.append("   COALESCE(sum(case moe.codigo when :ESP  then 1 else 0 end),0)   ");
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
    public List<String> allYearsCiclos() {
        StringBuilder strb = new StringBuilder();
        strb.append("select distinct SUBSTRING(a.codigo,1,4) from Alumno a order by SUBSTRING(a.codigo,1,4) asc");
        Query query = getCurrentSession().createQuery(strb.toString());
        return query.list();
    }

    @Override
    public List<Alumno> allPendingPromedioByCicloYear(String year) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("alu.modalidadEstudio me")
                .left("alu.cicloActivo aluca", "alu.situacionAcademica sa")
                .join("alu.persona aluPer", "alu.carrera alucar")
                .join("alucar.facultad fac")
                .leftJoin("aluPer.tipoDocumento td", "alu.cicloIngreso ci")
                .filter("alu.promedioProcesado", 0)
                .complexFilter("SUBSTRING(alu.codigo,1,4)", year);

        return all(sql);
    }

    @Override
    public List<Alumno> allPendingPREPromedioByCicloYear(String year) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("alu.modalidadEstudio me")
                .left("alu.cicloActivo aluca", "alu.situacionAcademica sa")
                .join("alu.persona aluPer", "alu.carrera alucar")
                .join("alucar.facultad fac")
                .leftJoin("aluPer.tipoDocumento td", "alu.cicloIngreso ci")
                .filter("alu.promedioProcesado", 0)
                .filter("me.codigo", ModalidadEstudioEnum.PRE.name())
                .complexFilter("SUBSTRING(alu.codigo,1,4)", year);

        return all(sql);
    }

    @Override
    public List<Alumno> allPendingEpgPromedioByCicloYear(String year) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("alu.modalidadEstudio me")
                .left("alu.cicloActivo aluca", "alu.situacionAcademica sa")
                .join("alu.persona aluPer", "alu.carrera alucar")
                .join("alucar.facultad fac")
                .leftJoin("aluPer.tipoDocumento td", "alu.cicloIngreso ci")
                .filter("alu.promedioProcesado", 0)
                .filter("me.codigo", ModalidadEstudioEnum.EPG.name())
                .complexFilter("SUBSTRING(alu.codigo,1,4)", year);

        return all(sql);
    }

    @Override
    public List<Alumno> allIngresantesByCiclos(List<CicloAcademico> ciclosIngresantes) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("alu.modalidadEstudio me", "cicloIngreso ci", "alu.situacionAcademica sa")
                .join("alu.persona per", "alu.carrera car")
                .join("car.facultad fac")
                .leftJoin("per.tipoDocumento td")
                .in("ci.id", ciclosIngresantes);

        return all(sql);
    }

    @Override
    public List<Alumno> allMatriculadosNoEgresadosByCiclos(List<CicloAcademico> ciclosPrevios) {
        Octavia sqlSub = new Octavia()
                .from(Egresado.class, "egre")
                .join("alumno alum");

        Octavia sql = Octavia.query()
                .selectDistinct("alu")
                .from(MatriculaResumen.class, "mr")
                .join("alumno alu", "alu.modalidadEstudio me", "cicloAcademico ci", "alu.situacionAcademica sa")
                .join("alu.persona per", "alu.carrera car")
                .join("car.facultad fac")
                .leftJoin("per.tipoDocumento td")
                .notExists(sqlSub)
                .linkedBy("alu.id", "alum.id")
                .in("mr.estado", Arrays.asList(MAT, RCI))
                .in("ci.id", ciclosPrevios);

        return all(sql);
    }

    @Override
    public List<Alumno> allEstudiaronByCiclos(List<CicloAcademico> ciclosPrevios) {
        Octavia sqlSub = new Octavia()
                .from(Egresado.class, "egre")
                .join("alumno alum");

        Octavia sql = Octavia.query()
                .selectDistinct("alu")
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "alu.modalidadEstudio me", "cicloAcademico ci", "alu.situacionAcademica sa")
                .join("alu.persona per", "alu.carrera car")
                .join("car.facultad fac")
                .leftJoin("per.tipoDocumento td")
                .notExists(sqlSub)
                .linkedBy("alu.id", "alum.id")
                .in("ac.estado", Arrays.asList(MAT, RCI))
                .in("ci.id", ciclosPrevios);

        return all(sql);
    }

    @Override
    public List<Alumno> findAlumnosConsejeria(Long carrera, CicloAcademico cicloAcademico) {
        Consejero consejeroNN = new Consejero();
        consejeroNN.setId(Constantine.ID_CONSEJERO_NN);

        Octavia sql = Octavia.query().selectDistinct("alu")
                .from(MatriculaResumen.class, "mr")
                .join("alumno alu", "cicloAcademico ci", "alu.carrera car")
                .leftJoin("alu.consejero conse")
                .filter("ci.id", cicloAcademico)
                .filter("car.id", carrera)
                .in("mr.estado", Arrays.asList(NMAT, MAT, RCI))
                .beginBlock()
                .__().isNull("alu.consejero")
                .__().filter("alu.consejero", consejeroNN)
                .endBlock();

        return all(sql);
    }

    @Override
    public List<Alumno> allByNameCondicional(String nombre, CicloAcademico cicloAcademico) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia subQuery = new Octavia()
                .from(MatriculaResumen.class, "mr")
                .join("alumno alum")
                .filter("cicloAcademico", cicloAcademico);

        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per", "carrera car", "car.facultad fa", "situacionAcademica sa")
                .leftJoin("per.tipoDocumento td")
                .filter("per.estado", PersonaEstadoEnum.ACT)
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("per.numeroDocIdentidad", "like", nombre)
                .__().filter("alu.codigo", "like", nombre)
                .endBlock()
                .notExists(subQuery)
                .in("sa.codigo", Arrays.asList(SituacionAcademicaEnum.S_6.getValue(), SituacionAcademicaEnum.S_4.getValue()))
                .linkedBy("alu.id", "alum.id")
                .limit(15);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Alumno> allByNameCicloAcademico(String nombre, CicloAcademico cicloAcademico) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per", "carrera car", "car.facultad fa")
                .leftJoin("per.tipoDocumento td", "cicloActivo ca")
                .filter("per.estado", PersonaEstadoEnum.ACT)
                .filter("ca.id", cicloAcademico)
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("per.numeroDocIdentidad", "like", nombre)
                .__().filter("alu.codigo", "like", nombre)
                .endBlock()
                .limit(15);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Alumno> allByAlumnos(List<Alumno> alumnos) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per", "carrera car", "car.facultad fa")
                .leftJoin("per.tipoDocumento td", "cicloActivo ci", "modalidadEstudio me", "situacionAcademica situ")
                .in("alu.id", alumnos);
        return all(sql);
    }

}
