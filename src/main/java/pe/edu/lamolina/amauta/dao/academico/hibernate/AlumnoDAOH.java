package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Insecto;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DetalleGrupoAlumno;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.academico.PlanCurricular;

import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.NMAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RCI;

import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;

import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.EPG;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.ESP;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.VIS;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.SOL;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.PEND;

import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.enums.SituacionAcademicaEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.amauta.controller.academico.alumno.AlumnoResumen;
import pe.edu.lamolina.amauta.controller.matricula.matriculable.MatriculableResumen;
import pe.edu.lamolina.model.academico.AlumnoOmisoEleccion;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;

import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_00;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_4;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_4T;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_4U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_7;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_8;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_D;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_E;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_G;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_R;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_RA;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_SS;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_X;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_XD;

import pe.edu.lamolina.model.enums.persona.PersonaEstadoEnum;
import pe.edu.lamolina.model.tramite.SancionDisciplina;

@Slf4j
@Repository
public class AlumnoDAOH extends AbstractEasyDAO<Alumno> implements AlumnoDAO {

    public AlumnoDAOH() {
        super();
        setClazz(Alumno.class);
    }

    @Override
    public Alumno findLock(Long id) {
        StringBuilder sql = new StringBuilder("select {a.*} from aca_alumno as a where a.id = :ID_ALUMNO for update ");
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
                .leftJoin("per.tipoDocumento td", "cicloActivo cia", "cicloIngreso ci", "modalidadEstudio me", "situacionAcademica situ", "cicloActivoRegular cir")
                .leftJoin("per.paisNacer", "orientacionCarrera")
                .leftJoin("per.ubicacionNacer ubn", "ubn.ubicacionSuperior ubnProv", "ubn.tipoUbicacion")
                .leftJoin("ubnProv.ubicacionSuperior ubnDep", "ubnProv.tipoUbicacion", "ubnDep.tipoUbicacion")
                .leftJoin("planCurricular plan", "plan.cicloInicioVigencia")
                .leftJoin("cicloIngreso", "cicloActivo")
                .leftJoin("postulantePregrado pp", "pp.modalidadIngreso mi", "pp.cicloPostula cp", "cp.cicloAcademico")
                .leftJoin("consejero con", "con.colaborador col", "col.persona")
                .filter("alu.id", alumno);

        return find(sql);
    }

    @Override
    public Alumno findAllInfo(Long id) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("modalidadEstudio me", "carrera ca", "ca.facultad", "persona per")
                .leftJoin("planCurricular pc", "situacionAcademica sa", "pc.cicloInicioVigencia", "pc.carrera")
                .leftJoin("cicloIngreso", "cicloActivo")
                .leftJoin("postulantePregrado pp", "pp.modalidadIngreso mi", "pp.cicloPostula cp", "cp.cicloAcademico")
                .leftJoin("orientacionCarrera", "per.tipoDocumento")
                .filter("alu.id", id);

        return find(sql);
    }

    @Override
    public List<Alumno> allWithAllInfo(List<Alumno> alumnos) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("modalidadEstudio me", "carrera ca", "ca.facultad", "persona per")
                .left("planCurricular pc", "situacionAcademica sa", "pc.cicloInicioVigencia", "pc.carrera")
                .left("cicloIngreso", "cicloActivo", "postulantePregrado pp", "pp.modalidadIngreso mi")
                .left("orientacionCarrera", "per.tipoDocumento")
                .in("alu.id", alumnos);

        return all(sql);
    }

    @Override
    public List<Alumno> allInfoByAlumnos(List<Alumno> alumnos) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("modalidadEstudio me", "carrera ca", "ca.facultad", "persona per")
                .left("planCurricular pc", "situacionAcademica sa", "pc.cicloInicioVigencia", "pc.carrera")
                .left("cicloIngreso", "cicloActivo", "postulantePregrado pp", "pp.modalidadIngreso mi")
                .left("orientacionCarrera", "per.tipoDocumento")
                .in("alu.id", alumnos);

        return all(sql);
    }

    @Override
    public Alumno findByCodigo(String codigoAlumno) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("modalidadEstudio me", "persona per")
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
    public List<Alumno> allByCarrerasDynatable(DynatableFilter filter, List<Carrera> carreras, String todo) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Alumno.class, "al")
                .join("persona per", "carrera ca", "modalidadEstudio moe", "ca.facultad fac", "ca.modalidadEstudio")
                .leftJoin("situacionAcademica sita", "per.tipoDocumento tdoc", "cicloIngreso ci", "cicloActivo cia")
                .searchFields("ca.nombre", "al.estado", "al.codigo", "per.numeroDocIdentidad")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("al.id desc");

        if (!todo.equalsIgnoreCase("TODOS")) {
            sql.in("ca.id", carreras);
        }

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
                .join("planCurricular pc", "situacionAcademica sa")
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
                .join("persona per", "carrera car", "car.facultad fa", "modalidadEstudio me")
                .leftJoin("per.tipoDocumento td")
                .filter("per.estado", PersonaEstadoEnum.ACT)
                .in("me.codigo", Arrays.asList(EPG, PRE, VIS, ESP))
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("per.numeroDocIdentidad", "like", nombre)
                .__().filter("alu.codigo", "like", nombre)
                .endBlock()
                .orderBy("per.paterno", "per.materno", "per.nombres")
                .limit(15);

        return all(sql);
    }

    @Override
    public List<Alumno> allAlumnosSancionados(String nombre) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";

        Octavia sqlSub = new Octavia()
                .from(SancionDisciplina.class, "sd")
                .join("alumno alum")
                .filter("sd.estado", SOL);

        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per", "carrera car", "car.facultad fa", "modalidadEstudio me")
                .leftJoin("per.tipoDocumento td")
                .exists(sqlSub)
                .linkedBy("alu.id", "alum.id")
                .filter("per.estado", PersonaEstadoEnum.ACT)
                .in("me.codigo", Arrays.asList(EPG, PRE, VIS, ESP))
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("per.numeroDocIdentidad", "like", nombre)
                .__().filter("alu.codigo", "like", nombre)
                .endBlock()
                .orderBy("per.paterno", "per.materno", "per.nombres")
                .limit(15);

        return all(sql);
    }

    @Override
    public List<Alumno> allByNameFacultad(String nombre, Facultad facultad) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per", "carrera car", "car.facultad fa", "modalidadEstudio me")
                .leftJoin("per.tipoDocumento td")
                .filter("per.estado", PersonaEstadoEnum.ACT);
        if (facultad != null) {
            sql.filter("fa.id", facultad);
        }
        sql.filter("me.codigo", PRE)
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
    public List<Alumno> allByNamePosgrado(String nombre) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";

        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per", "carrera car", "car.facultad fa", "modalidadEstudio me")
                .leftJoin("per.tipoDocumento td")
                .filter("per.estado", PersonaEstadoEnum.ACT)
                .filter("me.codigo", EPG)
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
    public List<Alumno> allByNamePregrado(String nombre) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";

        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per", "carrera car", "car.facultad fa", "modalidadEstudio me")
                .join("cicloIngreso", "postulantePregrado pos", "pos.cicloPostula cp")
                .join("cp.cicloAcademico", "pos.modalidadIngreso")
                .leftJoin("per.tipoDocumento td")
                .filter("per.estado", PersonaEstadoEnum.ACT)
                .filter("me.codigo", PRE)
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("per.numeroDocIdentidad", "like", nombre)
                .__().filter("alu.codigo", "like", nombre)
                .endBlock()
                .orderBy("per.paterno", "per.materno", "per.nombres")
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
                .limit(30);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Alumno> allByNameSinMatriculaResumenPRE_VIS(String nombre, CicloAcademico cicloAcademico) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";

        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per", "carrera car", "car.facultad fa", "modalidadEstudio me")
                .leftJoin("per.tipoDocumento td")
                .filter("per.estado", PersonaEstadoEnum.ACT)
                .in("me.codigo", Arrays.asList(ModalidadEstudioEnum.PRE.name(), ModalidadEstudioEnum.VIS.name()))
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("per.numeroDocIdentidad", "like", nombre)
                .__().filter("alu.codigo", "like", nombre)
                .endBlock()
                .limit(30);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Alumno> allByCarreraCicloMayores(Carrera carrera, String codigoCiclo) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per", "carrera car", "car.facultad fa")
                .leftJoin("per.tipoDocumento td", "cicloActivo ci", "planCurricular")
                .leftJoin("modalidadEstudio me", "situacionAcademica situ", "orientacionCarrera oc")
                .leftJoin("cicloIngreso cci")
                .filter("car.id", carrera)
                .filter("cci.codigo", ">=", codigoCiclo);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Alumno> allMatriculadosByGpoSeccion(GrupoSeccion gpoSecc) {
        Octavia sql = Octavia.query()
                .selectDistinct("alu")
                .from(MatriculaSeccion.class, "ms")
                .join("seccion s", "s.grupoSeccion gs", "matriculaResumen mr")
                .join("mr.alumno alu", "alu.persona", "alu.modalidadEstudio", "alu.situacionAcademica")
                .left("alu.cicloActivoRegular")
                .filter("gs.id", gpoSecc)
                .filter("s.tipoSeccion", "<>", TipoSeccionEnum.PCUR)
                .filter("mr.estado", MAT)
                .filter("ms.estado", MAT);

        return all(sql);
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
        sql.append(" inner join ca.modalidadEstudio moe ");

        Query query = getCurrentSession().createQuery(sql.toString());

        query.setString("PRE", PRE.name());
        query.setString("EPG", EPG.name());
        query.setString("VIS", VIS.name());
        query.setString("ESP", ESP.name());

        return (AlumnoResumen) query.uniqueResult();
    }

    @Override
    public AlumnoResumen findResumen(List<Carrera> carreras) {
        StringBuilder sql = new StringBuilder();

        sql.append("select new ").append(AlumnoResumen.class.getName());
        sql.append(" (   ");
        sql.append("   COALESCE(sum(case moe.codigo when :PRE then 1 else 0 end),0),   ");
        sql.append("   COALESCE(sum(case moe.codigo when :EPG then 1 else 0 end),0),   ");
        sql.append("   COALESCE(sum(case moe.codigo when :VIS then 1 else 0 end),0),   ");
        sql.append("   COALESCE(sum(case moe.codigo when :ESP then 1 else 0 end),0)   ");
        sql.append(" )   ");
        sql.append("  from ").append(Alumno.class.getName()).append(" as al ");
        sql.append(" inner join al.carrera ca ");
        sql.append(" inner join al.modalidadEstudio moe ");
        sql.append(" where ca.id in :CARRERAS ");

        Query query = getCurrentSession().createQuery(sql.toString());

        query.setString("PRE", PRE.name());
        query.setString("EPG", EPG.name());
        query.setString("VIS", VIS.name());
        query.setString("ESP", ESP.name());

        List<Long> idCarreras = carreras.stream().map(x -> x.getId()).collect(Collectors.toList());
        query.setParameterList("CARRERAS", idCarreras);

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
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct SUBSTRING(a.codigo,1,4) ");
        sql.append("  from Alumno a ");
        sql.append("  join a.modalidadEstudio me ");
        sql.append(" where me.codigo in ('PRE','EPG','VIS','ESP') ");
        sql.append("   and a.codigo not like 'P%' ");
        sql.append("   and a.codigo not like 'Q%' ");
        sql.append(" order by SUBSTRING(a.codigo,1,4) desc ");
        Query query = getCurrentSession().createQuery(sql.toString());
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
    public List<Alumno> allPendingPromedioByCicloYearAndModalidadEst(String year, ModalidadEstudioEnum modalidadEstudioEnum) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("alu.modalidadEstudio me")
                .left("alu.cicloActivo aluca", "alu.situacionAcademica sa")
                .join("alu.persona aluPer", "alu.carrera alucar")
                .join("alucar.facultad fac")
                .leftJoin("aluPer.tipoDocumento td", "alu.cicloIngreso ci")
                //.filter("alu.promedioProcesado", 0)
                .filter("me.codigo", modalidadEstudioEnum)
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
    public List<Alumno> allIngresantesByCiclos(List<CicloAcademico> ciclosIngresantes, String modalidad) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("alu.modalidadEstudio me", "cicloIngreso ci", "alu.situacionAcademica sa")
                .join("alu.persona per", "alu.carrera car")
                .join("car.facultad fac")
                .leftJoin("per.tipoDocumento td")
                .filter("me.codigo", modalidad)
                .in("sa.codigo", Arrays.asList("8", "9"))
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
    public List<Alumno> allByAlumnos(List<Alumno> alumnos) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per", "carrera car", "car.facultad fa")
                .leftJoin("per.tipoDocumento td", "cicloActivo ci", "modalidadEstudio me", "situacionAcademica situ")
                .left("cicloIngreso")
                .in("alu.id", alumnos);
        return all(sql);
    }

    @Override
    public List<Alumno> allMatriculadosByDetalleGpoAlu(DetalleGrupoAlumno dga, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .select("alu")
                .from(MatriculaResumen.class, "mr")
                .join("alumno alu", "alu.persona per", "alu.carrera car", "car.facultad fa", "cicloAcademico ca")
                .leftJoin("per.tipoDocumento td", "alu.modalidadEstudio me", "alu.situacionAcademica situ")
                .filter("mr.estado", MAT)
                .filter("ca.id", ciclo);

        if (dga.getCarrera() != null) {
            sql.filter("car.id", dga.getCarrera());
        }
        if (dga.getFacultad() != null) {
            sql.filter("fa.id", dga.getFacultad());
        }
        if (dga.getModalidadEstudio() != null) {
            sql.filter("me.id", dga.getModalidadEstudio());
        }
        if (dga.getSituacionAcademica() != null) {
            sql.filter("situ.id", dga.getSituacionAcademica());
        }

        if (hayDetallesConCurso(dga)) {
            Octavia subQuery = Octavia.query()
                    .from(MatriculaSeccion.class, "ms")
                    .join("matriculaResumen mr", "mr.alumno aa")
                    .join("seccion se", "se.grupoSeccion gs", "gs.curso cu", "gs.cicloAcademico ca")
                    .filter("ms.estado", MAT)
                    .filter("se.estado", SeccionEstadoEnum.ACT);
            if (dga.getCurso() != null) {
                subQuery.filter("cu.id", dga.getCurso());
            }
            if (dga.getGrupoSeccion() != null) {
                subQuery.filter("gs.id", dga.getGrupoSeccion());
            }
            if (dga.getSeccion() != null) {
                subQuery.filter("se.id", dga.getSeccion());
            }
            sql.__().__()
                    .exists(subQuery)
                    .linkedBy("alu.id", "aa.id");
        }

        return all(sql);
    }

    @Override
    public List<Alumno> allMatriculablesByDetalleGpoAlu(DetalleGrupoAlumno dga, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .select("alu")
                .from(MatriculaResumen.class, "mr")
                .join("alumno alu", "alu.persona per", "alu.carrera car", "car.facultad fa", "cicloAcademico ca")
                .leftJoin("per.tipoDocumento td", "alu.modalidadEstudio me", "alu.situacionAcademica situ")
                .in("mr.estado", Arrays.asList(MAT, NMAT))
                .filter("ca.id", ciclo);

        if (dga.getCarrera() != null) {
            sql.filter("car.id", dga.getCarrera());
        }
        if (dga.getFacultad() != null) {
            sql.filter("fa.id", dga.getFacultad());
        }
        if (dga.getModalidadEstudio() != null) {
            sql.filter("me.id", dga.getModalidadEstudio());
        }
        if (dga.getSituacionAcademica() != null) {
            sql.filter("situ.id", dga.getSituacionAcademica());
        }

        if (hayDetallesConCurso(dga)) {
            Octavia subQuery = Octavia.query()
                    .from(MatriculaSeccion.class, "ms")
                    .join("matriculaResumen mr", "mr.alumno aa")
                    .join("seccion se", "se.grupoSeccion gs", "gs.curso cu", "gs.cicloAcademico ca")
                    .filter("ms.estado", MAT)
                    .filter("se.estado", SeccionEstadoEnum.ACT);

            if (dga.getCurso() != null) {
                subQuery.filter("cu.id", dga.getCurso());
            }
            if (dga.getGrupoSeccion() != null) {
                subQuery.filter("gs.id", dga.getGrupoSeccion());
            }
            if (dga.getSeccion() != null) {
                subQuery.filter("se.id", dga.getSeccion());
            }
            sql.__()
                    .exists(subQuery)
                    .linkedBy("alu.id", "aa.id");
        }

        return all(sql);
    }

    private boolean hayDetallesConCurso(DetalleGrupoAlumno dga) {
        if (dga.getCurso() != null) {
            return true;
        }
        if (dga.getGrupoSeccion() != null) {
            return true;
        }
        if (dga.getSeccion() != null) {
            return true;
        }
        return false;
    }

    @Override
    public List<Alumno> allAlumnoByOficina(String nombre, Long instanciaOficina) {
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
                .filter("fa.id", instanciaOficina)
                .limit(15);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Alumno> allByModalidadSituacionesNoAptas(ModalidadEstudioEnum modalidadEnum, List<String> situacionesNoAptas) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("modalidadEstudio me", "carrera ca", "ca.facultad", "persona per", "situacionAcademica sa")
                .notIn("sa.codigo", situacionesNoAptas)
                .filter("me.codigo", modalidadEnum.name());

        return all(sql);
    }

    @Override
    public List<Alumno> allByPlanCarrera(String codigoCarrera) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("modalidadEstudio", "carrera ca", "ca.facultad", "persona", "situacionAcademica")
                .join("planCurricular pc", "pc.carrera cap")
                .filter("cap.codigo", codigoCarrera);

        return all(sql);
    }

    @Override
    public List<Alumno> allPregradoPendingPlanCurricula(CicloAcademico cicloIngreso) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("modalidadEstudio me", "carrera ca", "ca.facultad", "persona", "situacionAcademica", "cicloIngreso ci")
                .leftJoin("planCurricular pc", "pc.carrera cap")
                .filter("me.codigo", ModalidadEstudioEnum.PRE)
                .filter("ci.codigo", cicloIngreso.getCodigo()) //                .isNull("pc.id")
                ;

        return all(sql);
    }

    @Override
    public int updateList(List<Alumno> alumnos, String... columnas) {
        if (alumnos.isEmpty()) {
            return 0;
        }

        long t1 = System.currentTimeMillis();
        Insecto sql = Insecto.createUpdate(Alumno.class)
                .set(columnas)
                .with(alumnos);

        Query query = getCurrentSession().createSQLQuery(sql.toString());
        int rows = query.executeUpdate();

        long t2 = System.currentTimeMillis();
        log.info("{} Alumno's actualizados en {} mseg....", rows, (t2 - t1));
        return rows;
    }

    @Override
    public List<Alumno> allAlumnosbyDynatable(DynatableFilter filter, List<Carrera> carreras) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Alumno.class, "al")
                .join("persona per", "carrera ca", "ca.modalidadEstudio moe", "ca.facultad fac")
                .leftJoin("situacionAcademica sita", "per.tipoDocumento tdoc", "cicloIngreso ci", "cicloActivo cia")
                .searchFields("ca.nombre", "al.estado", "al.codigo", "per.numeroDocIdentidad")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("al.id desc")
                .in("ca.id", carreras);

        sql.beginRelativeFilters();
        setCondicionModalidad(filter, sql);

        return all(sql);
    }

    @Override
    public List<Alumno> pendientesHistorial(CicloAcademico cicloAcademico) {

        Octavia sql = Octavia.query()
                .from(Alumno.class, "al")
                .in("codigo", Arrays.asList("20221251", "20221286", "20221289", "20221326", "20221388", "20221477", "20221489", "20221501", "20230271", "20230217", "20230275", "20230099", "20240363", "20230154", "20230192", "20230214", "20230219", "20230226", "20230264", "20240575", "20230328", "20230503", "20230516", "20240843", "20230564", "20230585", "20230595", "20240899", "20230624", "20230632", "20230694", "20230713", "20230991", "20231290", "20231257", "20231282", "20231315", "20231317", "20231318", "20231364", "20240572", "20231393", "20231421", "20231448", "20231590", "20231598", "20231622", "20240895", "20231689", "20231718", "20240517", "20240824", "20240729", "20240838", "20240715", "20240596", "20240696", "20240387", "20240931", "20240327", "20250120", "20240493", "20240520", "20240602", "20240644", "20240681", "20240704", "20240795", "20240799", "20240802", "20240874", "20240878", "20240919", "20240934", "20240957", "20241503", "20241126", "20241217", "20241287", "20241288", "20241292", "20241304", "20241406", "20241413", "20241466", "20250530", "20241483", "20241494", "20241616", "20250496", "20250016", "20250078", "20250225", "20250680", "20250541", "20250544", "20250664", "20250526", "20250100", "20250108", "20250132", "20250686", "20250242", "20250043", "20250537", "20250049", "20250374", "20250018", "20250019", "20250069", "20250103", "20250246", "20250357", "20250691", "20250699"));
        return sql.all(getCurrentSession());
//        StringBuilder sb = new StringBuilder();
//        sb.append("  select t.id  ");
//        sb.append("  from aca_alumno t ");
//        sb.append("  where t.codigo in  ");
//        sb.append("  ('20250496', ");
//        sb.append("  '20250016', ");
//        sb.append("  '20250078', ");
//        sb.append("  '20250225', ");
//        sb.append("  '20250680'); ");
//
//
//        Query query = getCurrentSession().createSQLQuery(sb.toString());
//                //                .addEntity("alu",Alumno.class)
//                //                .addJoin("me", "alu.id_modalidad_estudio")
//                .addScalar("id", LongType.INSTANCE)
//                .setResultTransformer(Transformers.aliasToBean(Alumno.class));//falto hacer el join con modalidad de estudio
////        query.setParameter("CICLO_SESSION", cicloAcademico.getId());
////        query.setParameter("ESTADO_MAT", EstadoMatriculaEnum.MAT.name());
////        query.setParameter("MODALIDAD", ModalidadEstudioEnum.PRE.name());
//        return query.list();
    }

    @Override
    public List<Alumno> allByNoMatriculableCicloAnt(List<CicloAcademico> cicloAnt) {

        Octavia sqlSub = Octavia.query()
                .from(Egresado.class, "eg")
                .join("alumno al");

        Octavia subQuery = new Octavia()
                .from(MatriculaResumen.class, "mr")
                .join("alumno alum", "cicloAcademico ca")
                .filter("estado", MAT)
                .in("ca.id", cicloAnt);

        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per", "carrera car", "car.facultad fa", "modalidadEstudio me")
                .join("situacionAcademica sa")
                .leftJoin("per.tipoDocumento td")
                .filter("per.estado", PersonaEstadoEnum.ACT)
                .in("me.codigo", Arrays.asList(PRE, VIS))
                .notIn("sa.id", Arrays.asList(S_XD, S_4U, S_G, S_7, S_8, S_4, S_E, S_D, S_R, S_4T, S_SS, S_00, S_X, S_RA))
                .notLike("alu.codigo", "Q")
                .notLike("alu.codigo", "P")
                .notLike("alu.codigo", "T")
                .__().notExists(subQuery)
                .__().linkedBy("alu.id", "alum.id")
                .__().notExists(sqlSub)
                .__().linkedBy("alu.id", "al.id");

        return sql.all(getCurrentSession());
    }

    @Override
    public List<Alumno> allByCustomQuery(CicloAcademico cicloAcademico) {

        StringBuilder sb = new StringBuilder();
        sb.append("   select alu3.id as id   ");
        sb.append("   from aca_matricula_resumen mr   ");
        sb.append("   join aca_ciclo_academico ca on mr.id_ciclo_academico = ca.id   ");
        sb.append("   join aca_alumno alu3 on mr.id_alumno = alu3.id   ");
        sb.append("   join aca_modalidad_estudio me on alu3.id_modalidad_estudio = me.id   ");
        sb.append("   where ca.codigo = '202110' and mr.estado = 'MAT'    ");
        sb.append("   and alu3.id_modalidad_estudio = 1   ");
        sb.append("   and not exists (  ");
        sb.append("                   select distinct fr.matricula  ");
        sb.append("                   from v_alumno_aporte fr   ");
        sb.append("                   where fr.codigo_aporte in ('05','54') and    ");
        sb.append("                   fr.ciclo = '2021-I' and fr.modalidad = 'PRE'    ");
        sb.append("                   and fr.matricula in (   ");
        sb.append("                                   select alu.codigo   ");
        sb.append("                                   from aca_matricula_resumen mr   ");
        sb.append("                                   join aca_ciclo_academico ca on mr.id_ciclo_academico = ca.id   ");
        sb.append("                                   join aca_alumno alu on mr.id_alumno = alu.id   ");
        sb.append("                                   join aca_modalidad_estudio me on alu.id_modalidad_estudio = me.id   ");
        sb.append("                                   where ca.codigo = '202110' and mr.estado = 'MAT' and alu.id_modalidad_estudio = 1   ");
        sb.append("                                       )   ");
        sb.append("                   and fr.matricula = alu3.codigo   ");
        sb.append("                   )   ");
        sb.append("   and not exists (   ");
        sb.append("                       select distinct app.matricula");
        sb.append("                       from v_alumno_aporte app   ");
        sb.append("                       where app.ciclo = '2020-I' and    ");
        sb.append("                       app.codigo_aporte in ('05','54') ");
        sb.append("                       and app.matricula in (   ");
        sb.append("                                       select alu2.codigo ");
        sb.append("                                       from aca_matricula_resumen mr   ");
        sb.append("                                       join aca_ciclo_academico ca on mr.id_ciclo_academico = ca.id   ");
        sb.append("                                       join aca_alumno alu2 on mr.id_alumno = alu2.id   ");
        sb.append("                                       join aca_modalidad_estudio me on alu2.id_modalidad_estudio = me.id   ");
        sb.append("                                       where ca.codigo = '202110' and mr.estado = 'MAT' and alu2.id_modalidad_estudio = 1   ");
        sb.append("                                       and not exists (   ");
        sb.append("                                                       select distinct fr.matricula ");
        sb.append("                                                       from v_alumno_aporte fr   ");
        sb.append("                                                       where fr.codigo_aporte in ('05','54') and    ");
        sb.append("                                                       fr.ciclo = '2021-I' and fr.modalidad = 'PRE'    ");
        sb.append("                                                       and fr.matricula in (   ");
        sb.append("                                                                       select alu.codigo  ");
        sb.append("                                                                       from aca_matricula_resumen mr   ");
        sb.append("                                                                       join aca_ciclo_academico ca on mr.id_ciclo_academico = ca.id   ");
        sb.append("                                                                       join aca_alumno alu on mr.id_alumno = alu.id   ");
        sb.append("                                                                       join aca_modalidad_estudio me on alu.id_modalidad_estudio = me.id   ");
        sb.append("                                                                       where ca.codigo = '202110' and mr.estado = 'MAT' and alu.id_modalidad_estudio = 1   ");
        sb.append("                                                                           )   ");
        sb.append("                                                       and fr.matricula = alu2.codigo   ");
        sb.append("                                                       )   ");
        sb.append("                                                )   ");
        sb.append("                       and app.matricula = alu3.codigo                            ");
        sb.append("                   );   ");

        Query query = getCurrentSession().createSQLQuery(sb.toString())
                .addScalar("id", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(Alumno.class));

        return query.list();
    }

    @Override
    public List<Alumno> allDynatableAlumnoOmisoEleccion(DynatableFilter filter) {

        Octavia sql1 = new Octavia()
                .from(AlumnoOmisoEleccion.class, "aoe")
                .join("alumno al1");

        DynatableSql sql = new DynatableSql(filter)
                .from(Alumno.class, "al")
                .join("al.carrera car", "car.facultad", "al.modalidadEstudio")
                .join("al.persona per", "per.tipoDocumento")
                .searchFields("al.codigo", "per.numeroDocIdentidad")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .exists(sql1)
                .linkedBy("al.id", "al1.id")
                .orderBy("al.id");

        return all(sql);

    }

    @Override
    public List<Alumno> allByCodigos(List<String> codigosMatricula) {

        Octavia query = new Octavia()
                .from(Alumno.class, "al")
                .join("persona per", "per.tipoDocumento tipo")
                .in("al.codigo", codigosMatricula);
        return all(query);

    }

    @Override
    public Alumno findFirstByPersona(Persona persona) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per")
                .filter("per.id", persona)
                .limit(1);
        return find(sql);
    }

    @Override
    public List<Alumno> allAlumnoHistoricoByCarrerasDynatable(DynatableFilter filter, List<Carrera> carreras, String todo) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Alumno.class, "al")
                .join("persona per", "carrera ca", "modalidadEstudio moe", "ca.facultad fac", "ca.modalidadEstudio")
                .leftJoin("situacionAcademica sita", "per.tipoDocumento tdoc", "cicloIngreso ci", "cicloActivo cia")
                .searchFields("ca.nombre", "al.estado", "al.codigo", "per.numeroDocIdentidad")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .filter("al.ingresoAlumnoHistorico", "SI")
                .orderBy("al.id desc");

        if (!todo.equalsIgnoreCase("TODOS")) {
            sql.in("ca.id", carreras);
        }

        sql.beginRelativeFilters();
        setCondicionModalidad(filter, sql);

        return all(sql);
    }

    @Override
    public List<Alumno> correccionNivelacion(CicloAcademico cicloAcademico) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select alu.id ");
        sb.append(" from aca_alumno_ciclo ac ");
        sb.append(" join aca_alumno alu on ac.id_alumno = alu.id ");
        sb.append(" join aca_modalidad_estudio me on alu.id_modalidad_estudio =  me.id ");
        sb.append(" join aca_ciclo_academico ca on ac.id_ciclo_academico = ca.id ");
        sb.append(" where  ca.id = :CICLO_SESSION ");
        sb.append(" and ac.estado = :ESTADO_MAT ");
        sb.append(" and me.codigo = :MODALIDAD ");

        Query query = getCurrentSession().createSQLQuery(sb.toString())
                .addScalar("id", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(Alumno.class));
        query.setParameter("CICLO_SESSION", cicloAcademico.getId());
        query.setParameter("ESTADO_MAT", EstadoMatriculaEnum.MAT.name());
        query.setParameter("MODALIDAD", ModalidadEstudioEnum.PRE.name());
        return query.list();
    }

    @Override
    public List<Alumno> allActivoPregradoByNombre(String nombre) {
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
                .filter("me.codigo", ModalidadEstudioEnum.PRE)
                .limit(50);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Alumno> allIngresantePregradoByCicloIngreso(ModalidadEstudio modalidad, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("persona per", "carrera car", "car.facultad fa", "modalidadEstudio moe", "situacionAcademica sa")
                .leftJoin("per.tipoDocumento td", "cicloIngreso ci")
                .filter("moe.id", modalidad)
                .filter("ci.id", ciclo)
                .notLike("alu.codigo", "Q%");

        return all(sql);
    }

    @Override
    public Alumno findBySitCodigo(String codigoAlumno) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("situacionAcademica")
                .filter("alu.codigo", codigoAlumno);

        return find(sql);
    }

    @Override
    public List<Alumno> allAlumnoByYear(Integer year) {

        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("modalidadEstudio me", "persona per", "cicloIngreso ci")
                .filter("ci.year", year);

        return all(sql);

    }

    @Override
    public List<Alumno> allIngresantePregradoByCicloIngreso(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("modalidadEstudio me", "carrera ca", "ca.facultad", "persona per")
                .join("situacionAcademica sa", "cicloIngreso ci")
                .join("postulantePregrado pp", "pp.modalidadIngreso mi", "pp.cicloPostula cp", "cp.cicloAcademico")
                .leftJoin("per.tipoDocumento")
                .filter("ci.id", ciclo);

        return all(sql);
    }

    @Override
    public List<Alumno> allAlumnosTmp() {
        StringBuilder sb = new StringBuilder();
        sb.append(" select a.* ");
        sb.append(" from aca_matricula_resumen mr ");
        sb.append(" join aca_alumno a on a.id = mr.id_alumno ");
        sb.append(" join aca_ciclo_academico cai on a.id_ciclo_ingreso = cai.id and cai.codigo = '202510' ");
        sb.append(" join aca_situacion_academica sa on a.id_situacion_academica = sa.id ");
        sb.append(" left join octopus.apo_resumen_aporte_alumno ra on ra.id_matricula_resumen = mr.id ");
        sb.append(" where mr.id_ciclo_academico = 1240 and a.id_modalidad_estudio = 1 ");
        sb.append(" and ra.id is null; ");

        Query query = getCurrentSession().createSQLQuery(sb.toString()).addEntity(Alumno.class);
        return query.list();

    }

}
