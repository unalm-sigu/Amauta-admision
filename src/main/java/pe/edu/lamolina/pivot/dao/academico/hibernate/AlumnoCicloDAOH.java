package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ControlOrdenMerito;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.INH;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.NMAT;
import pe.edu.lamolina.model.enums.SituacionAcademicaEnum;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_4;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_4U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_D;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_XD;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import static pe.edu.lamolina.model.enums.TipoCicloEnum.REG;

@Repository
public class AlumnoCicloDAOH extends AbstractEasyDAO<AlumnoCiclo> implements AlumnoCicloDAO {

    public AlumnoCicloDAOH() {
        super();
        setClazz(AlumnoCiclo.class);
    }

    @Override
    public AlumnoCiclo find(long id) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca", "carrera car")
                .join("car.facultad fac", "alu.persona aluper")
                .leftJoin("situacionInicio si", "situacionFinal sf", "userRegistro ur", "orientacionCarrera oc")
                .leftJoin("orientacionCarrera", "situacionInicio")
                .left("controlMeritoCiclo cmcic", "controlMeritoFacultad cmfac", "controlMeritoCarrera cmcar")
                //   .leftJoin("cmc.alumnosComputados")
                .leftJoin("userModificacion um")
                .filter("ac.id", id);
        return find(sql);
    }

    @Override
    public List<AlumnoCiclo> allByCicloAcademicoPlanCurricular(PlanCurricular plan, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("cicloAcademico ca", "alumno alu")
                .filter("cicloAcademico", ciclo)
                .filter("alu.planCurricular", plan);

        return sql.all(getCurrentSession());
    }

    @Override
    public Long countByCicloAcademicoPlanCurricular(CicloAcademico ciclo, PlanCurricular plan) {
        Octavia sql = Octavia.query()
                .selectCount()
                .from(AlumnoCiclo.class, "ac")
                .join("cicloAcademico ca", "alumno alu")
                .filter("cicloAcademico", ciclo)
                .filter("alu.planCurricular", plan);

        return (Long) sql.find(getCurrentSession());
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.MANDATORY)
    public AlumnoCiclo findLock(Long id) {
        return (AlumnoCiclo) getCurrentSession().load(AlumnoCiclo.class, id, LockOptions.UPGRADE);
    }

    @Override
    public AlumnoCiclo findByAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca", "carrera car")
                .left("situacionInicio si", "situacionFinal sf", "orientacionCarrera oc")
                .left("userModificacion um")
                .filter("alu.id", alumno)
                .filter("ca.id", cicloAcademico);
        return find(sql);
    }

    @Override
    public AlumnoCiclo findActiveByAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca", "carrera car")
                .leftJoin("situacionInicio si", "situacionFinal sf", "orientacionCarrera oc")
                .filter("ac.estado", EstadoMatriculaEnum.MAT.name())
                .filter("alu.id", alumno)
                .filter("ca.id", cicloAcademico);
        return find(sql);
    }

    @Override
    public AlumnoCiclo findByAlumnoCicloEstado(Alumno alumno, CicloAcademico cicloAcademico, List<EstadoMatriculaEnum> estadosEnums) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca", "carrera car")
                .leftJoin("situacionInicio si", "situacionFinal sf", "orientacionCarrera oc")
                .in("ac.estado", estadosEnums)
                .filter("alu.id", alumno)
                .filter("ca.id", cicloAcademico);
        return find(sql);
    }

    @Override
    public AlumnoCiclo findLastByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca", "carrera car")
                .left("situacionInicio si", "situacionFinal sf", "orientacionCarrera oc")
                .filter("alu.id", alumno)
                .orderBy("ac.fechaRegistro desc")
                .limit(BigDecimal.ONE.intValue());
        return find(sql);
    }

    @Override
    public AlumnoCiclo findLastActiveRegByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca", "carrera car")
                .left("situacionInicio si", "situacionFinal sf", "orientacionCarrera oc")
                .filter("ca.tipo", TipoCicloEnum.REG.name())
                .filter("alu.id", alumno)
                .filter("ac.estado", EstadoMatriculaEnum.MAT.name())
                .orderBy("ca.codigo desc")
                .limit(BigDecimal.ONE.intValue());
        return find(sql);
    }

    @Override
    public AlumnoCiclo findLastByAlumnoAndSituacion(Alumno alumno, SituacionAcademicaEnum... situacionesAcademicas) {
        List<SituacionAcademicaEnum> situaciones = Arrays.asList(situacionesAcademicas);
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca", "carrera car")
                .left("situacionInicio si", "situacionFinal sf", "orientacionCarrera oc")
                .filter("alu.id", alumno)
                //      .filter("ac.estado", EstadoMatriculaEnum.MAT.name())
                .in("sf.codigo", situaciones.stream().map(x -> x.getValue()).collect(Collectors.toList()))
                .orderBy("ca.codigo desc")
                .limit(BigDecimal.ONE.intValue());
        return find(sql);
    }

    @Override
    public AlumnoCiclo findLastNotInSituacion(Alumno alumno, SituacionAcademicaEnum... situacionAcademicaEnums) {
        List<SituacionAcademicaEnum> situaciones = Arrays.asList(situacionAcademicaEnums);
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca", "carrera car")
                .left("situacionInicio si", "situacionFinal sf", "orientacionCarrera oc")
                .filter("ca.tipo", TipoCicloEnum.REG)
                .filter("alu.id", alumno)
                //      .filter("ac.estado", EstadoMatriculaEnum.MAT.name())
                .notIn("sf.codigo", situaciones.stream().map(x -> x.getValue()).collect(Collectors.toList()))
                .orderBy("ca.codigo desc")
                .limit(BigDecimal.ONE.intValue());
        return find(sql);
    }

    @Override
    public AlumnoCiclo findActiveAnteriorByAlumno(Alumno alumno, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca", "carrera car")
                .left("situacionInicio si", "situacionFinal sf")
                .leftJoin("userModificacion um", "orientacionCarrera oc")
                .filter("alu.id", alumno)
                .filter("ca.codigo", "<", cicloAcademico.getCodigo())
                .filter("ac.estado", EstadoMatriculaEnum.MAT.name())
                .orderBy("ca.year DESC", "ca.numeroCiclo DESC")
                .limit(1);
        return find(sql);
    }

    @Override
    public AlumnoCiclo findAnteriorByAlumno(Alumno alumno, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca", "carrera car")
                .left("situacionInicio si", "situacionFinal sf")
                .leftJoin("userModificacion um", "orientacionCarrera oc")
                .filter("alu.id", alumno)
                .filter("ca.codigo", "<", cicloAcademico.getCodigo())
                //    .filter("ac.estado", EstadoMatriculaEnum.MAT.name())
                .orderBy("ca.year DESC", "ca.numeroCiclo DESC")
                .limit(1);
        return find(sql);
    }

    @Override
    public List<AlumnoCiclo> allAnterioresEQByCicloAlumno(Alumno alumno, CicloAcademico cicloAcademico, Integer limit) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca", "carrera car")
                .left("situacionInicio si", "situacionFinal sf", "userRegistro ur")
                .leftJoin("userModificacion um", "orientacionCarrera oc")
                .filter("alu.id", alumno)
                .filter("ca.codigo", "<=", cicloAcademico.getCodigo())
                .orderBy("ca.year DESC", "ca.numeroCiclo DESC")
                .limit(limit);
        return all(sql);
    }

    @Override
    public AlumnoCiclo findActiveSiguienteByAlumno(Alumno alumno, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca", "carrera car")
                .left("situacionInicio si", "situacionFinal sf", "userRegistro ur")
                .leftJoin("userModificacion um", "orientacionCarrera oc")
                .filter("alu.id", alumno)
                .filter("ca.codigo", ">", cicloAcademico.getCodigo())
                .filter("ac.estado", EstadoMatriculaEnum.MAT.name())
                .orderBy("ca.year asc", "ca.numeroCiclo asc")
                .limit(1);
        return find(sql);
    }

    @Override
    public AlumnoCiclo findInhaSiguienteByAlumno(Alumno alumno, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca", "carrera car")
                .left("situacionInicio si", "situacionFinal sf", "userRegistro ur")
                .left("userModificacion um", "orientacionCarrera oc")
                .filter("alu.id", alumno)
                .filter("ca.codigo", ">", cicloAcademico.getCodigo())
                .filter("ac.estado", EstadoMatriculaEnum.INH.name())
                .orderBy("ca.year asc", "ca.numeroCiclo asc")
                .limit(1);
        return find(sql);
    }

    @Override
    public AlumnoCiclo findInhaAnteriorByAlumno(Alumno alumno, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca", "carrera car")
                .left("situacionInicio si", "situacionFinal sf", "userRegistro ur")
                .left("userModificacion um", "orientacionCarrera oc")
                .filter("alu.id", alumno)
                .filter("ca.codigo", "<", cicloAcademico.getCodigo())
                .filter("ac.estado", EstadoMatriculaEnum.INH.name())
                .orderBy("ca.year DESC", "ca.numeroCiclo DESC")
                .limit(1);
        return find(sql);
    }

    @Override
    public List<AlumnoCiclo> allActivesByAlumnoAsc(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca")
                .leftJoin("situacionInicio si", "situacionFinal sf", "userRegistro ur")
                .leftJoin("userModificacion um")
                .filter("alu.id", alumno)
                .filter("ac.estado", EstadoMatriculaEnum.MAT.name())
                .orderBy("ca.codigo asc");
        return all(sql);
    }

    @Override
    public List<AlumnoCiclo> allByEstadoAndCicloAsc(Alumno alumno, EstadoMatriculaEnum... estadoMatriculaEnum) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca")
                .leftJoin("situacionInicio si", "situacionFinal sf", "userRegistro ur")
                .leftJoin("userModificacion um")
                .filter("alu.id", alumno)
                .in("ac.estado", estadoMatriculaEnum)
                .orderBy("ca.codigo asc");
        return all(sql);
    }

    @Override
    public List<AlumnoCiclo> allByModalidadEstAndSituacionesAcadAndEstadoMatAsc(
            ModalidadEstudio modalidadEstudio,
            Carrera carrera,
            List<SituacionAcademica> situaciones,
            EstadoMatriculaEnum estadoMatriculaEnum) {
        Octavia sql0 = Octavia.query()
                .from(Alumno.class, "alu0")
                .join("modalidadEstudio me0", "situacionAcademica sa0", "cicloActivo ca0")
                .join("persona per0", "carrera car0", "car0.facultad fa0")
                .leftJoin("per0.tipoDocumento td0", "cicloActivo cia0", "cicloIngreso ci0")
                .filter("me0.id", modalidadEstudio)
                .filter("car0.id", carrera)
                .in("sa0.id", situaciones);

        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca")
                .leftJoin("situacionInicio si", "situacionFinal sf", "userRegistro ur")
                .leftJoin("userModificacion um")
                .filter("ac.estado", estadoMatriculaEnum)
                .exists(sql0)
                .linkedBy("alu.id", "alu0.id")
                .orderBy("ca.codigo asc");
        return all(sql);
    }

    @Override
    public List<AlumnoCiclo> allDataByAlumno(
            Alumno alumno,
            EstadoMatriculaEnum estadoMatriculaEnum) {
        Octavia sql0 = Octavia.query()
                .from(Alumno.class, "alu0")
                .join("modalidadEstudio me0", "situacionAcademica sa0", "cicloActivo ca0")
                .join("persona per0", "carrera car0", "car0.facultad fa0")
                .leftJoin("per0.tipoDocumento td0", "cicloActivo cia0", "cicloIngreso ci0")
                .filter("alu0.id", alumno);

        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca")
                .leftJoin("situacionInicio si", "situacionFinal sf", "userRegistro ur")
                .leftJoin("userModificacion um")
                .filter("ac.estado", estadoMatriculaEnum)
                .exists(sql0)
                .linkedBy("alu.id", "alu0.id")
                .orderBy("ca.codigo asc");
        return all(sql);
    }

    @Override
    public List<AlumnoCiclo> allByAlumnoAsc(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca")
                .leftJoin("situacionInicio si", "situacionFinal sf")
                .leftJoin("userModificacion um", "userRegistro ur")
                .filter("alu.id", alumno)
                .orderBy("ca.codigo asc");
        return all(sql);
    }

    @Override
    public Long countCiclosEstudiados(Alumno alumno, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .selectCount()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca")
                .filter("alu.id", alumno)
                .filter("ca.codigo", "<=", cicloAcademico.getCodigo())
                .filter("ca.tipo", TipoCicloEnum.REG)
                .filter("ac.estado", EstadoMatriculaEnum.MAT.name());
        return (Long) sql.find(getCurrentSession());
    }

    @Override
    public void updateSituacionInicioFinal(AlumnoCiclo alumnoCiclo) {
        Octavia octavia = Octavia.update(AlumnoCiclo.class);
        octavia.set(alumnoCiclo, "situacionInicio");
        octavia.set(alumnoCiclo, "situacionFinal");
        octavia.set(alumnoCiclo, "fechaModificacion");
        octavia.set(alumnoCiclo, "userModificacion");
        this.update(octavia);
    }

    @Override
    public void updateSituacionInicioFinalEstado(AlumnoCiclo alumnoCiclo) {
        Octavia octavia = Octavia.update(AlumnoCiclo.class);
        octavia.set(alumnoCiclo, "situacionInicio");
        octavia.set(alumnoCiclo, "situacionFinal");
        octavia.set(alumnoCiclo, "estado");
        octavia.set(alumnoCiclo, "fechaModificacion");
        octavia.set(alumnoCiclo, "userModificacion");
        this.update(octavia);
    }

    @Override
    public void updateSituacionFinal(AlumnoCiclo alumnoCiclo) {
        Octavia octavia = Octavia.update(AlumnoCiclo.class);
        octavia.set(alumnoCiclo, "situacionFinal");
        octavia.set(alumnoCiclo, "fechaModificacion");
        octavia.set(alumnoCiclo, "userModificacion");
        this.update(octavia);
    }

    @Override
    public void updateSituacionFinalOnly(AlumnoCiclo alumnoCiclo) {
        Octavia octavia = Octavia.update(AlumnoCiclo.class);
        octavia.set(alumnoCiclo, "situacionFinal");
        this.update(octavia);
    }

    @Override
    public void updateColumns(AlumnoCiclo alumnoCiclo, String... columns) {
        List<String> lColumns = Arrays.asList(columns);
        Octavia octavia = Octavia.update(AlumnoCiclo.class);
        for (String column : lColumns) {
            octavia.set(alumnoCiclo, column);
        }
        this.update(octavia);
    }

    @Override
    public List<AlumnoCiclo> allCicloRegularByAlumno(Alumno alum) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno al", "cicloAcademico ca")
                .filter("al.id", alum)
                .filter("ca.tipo", TipoCicloEnum.REG);
        return sql.all(getCurrentSession());
    }

    @Override
    public AlumnoCiclo findUltimoCicloRegularByAlumno(Alumno alum, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno al", "cicloAcademico ca")
                .filter("al.id", alum)
                .filter("ca.tipo", TipoCicloEnum.REG)
                .orderBy("ca.codigo desc")
                .limit(1);
        return (AlumnoCiclo) sql.find(getCurrentSession());
    }

    @Override
    public List<AlumnoCiclo> allByCicloAcademico(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("cicloAcademico ca", "alumno alu")
                .join("alu.persona per", "carrera car", "car.facultad fac")
                .filter("estado", EstadoMatriculaEnum.MAT)
                .filter("cicloAcademico", ciclo);

        return sql.all(getCurrentSession());
    }

    @Override
    public List<AlumnoCiclo> allWithSituacionByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca")
                .leftJoin("situacionInicio si", "situacionFinal sf")
                .leftJoin("userModificacion um", "userRegistro ur")
                .filter("cicloAcademico", ciclo);

        return all(sql);
    }

    @Override
    public List<AlumnoCiclo> allWithSituacionErrorByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca")
                .leftJoin("situacionInicio si", "situacionFinal sf")
                .leftJoin("userModificacion um", "userRegistro ur")
                .filter("alu.conError", 1)
                .filter("cicloAcademico", ciclo);

        return all(sql);
    }

    @Override
    public List<AlumnoCiclo> allWithSituacionByAlumnos(List<Alumno> alumnos) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca")
                .leftJoin("situacionInicio si", "situacionFinal sf")
                .leftJoin("userModificacion um", "userRegistro ur")
                .in("alu.id", alumnos);

        return all(sql);
    }

    @Override
    public List<AlumnoCiclo> allByControlesOrdenMerito(List<ControlOrdenMerito> coms) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("cicloAcademico ca", "alumno alu")
                .join("alu.persona per", "carrera car", "car.facultad fac")
                .left("controlMeritoCiclo cmc", "controlMeritoFacultad cmf", "controlMeritoCarrera cmca")
                .filter("estado", EstadoMatriculaEnum.MAT)
                .isNotNull("promedioAcumulado")
                .beginBlock()
                .__().in("cmc.id", coms)
                .__().in("cmf.id", coms)
                .__().in("cmca.id", coms)
                .endBlock();

        return sql.all(getCurrentSession());
    }

    @Override
    public void deleteControlMeritoByCiclo(CicloAcademico cicloAcademico) {
        StringBuilder sql = new StringBuilder();

        sql.append("update AlumnoCiclo set ");

        sql.append("controlMeritoCarrera = null, ");
        sql.append("controlMeritoCiclo = null, ");
        sql.append("controlMeritoFacultad = null ");

        sql.append("where cicloAcademico.id = :CICLO");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("CICLO", cicloAcademico.getId());
        query.executeUpdate();
    }

    @Override
    public void deleteOrdenMeritoByCiclo(CicloAcademico cicloAcademico) {
        StringBuilder sql = new StringBuilder();

        sql.append("update AlumnoCiclo set ");

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
        sql.append("tercioSuperiorFacultad = null, ");

        sql.append("ordenMeritoCarreraNivel = null, ");
        sql.append("ordenMeritoCicloNivel = null, ");
        sql.append("ordenMeritoFacultadNivel = null, ");

        sql.append("cuadroHonorCarreraNivel = null, ");
        sql.append("cuadroHonorCicloNivel = null, ");
        sql.append("cuadroHonorFacultadNivel = null, ");

        sql.append("quintoSuperiorCarreraNivel = null, ");
        sql.append("quintoSuperiorCicloNivel = null, ");
        sql.append("quintoSuperiorFacultadNivel = null, ");

        sql.append("tercioSuperiorCarreraNivel = null, ");
        sql.append("tercioSuperiorCicloNivel = null, ");
        sql.append("tercioSuperiorFacultadNivel = null ");

        sql.append("where cicloAcademico.id = :CICLO");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("CICLO", cicloAcademico.getId());
        query.executeUpdate();
    }

    @Override
    public List<AlumnoCiclo> allByControlMeritoCiclo(DynatableFilter filter, ControlOrdenMerito controlBD) {
        DynatableSql sql = new DynatableSql(filter)
                .from(AlumnoCiclo.class, "ac")
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
    public List<AlumnoCiclo> allByControlMeritoCarrera(DynatableFilter filter, ControlOrdenMerito controlBD) {
        DynatableSql sql = new DynatableSql(filter)
                .from(AlumnoCiclo.class, "ac")
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
    public List<AlumnoCiclo> allByControlMeritoFacultad(DynatableFilter filter, ControlOrdenMerito controlBD) {
        DynatableSql sql = new DynatableSql(filter)
                .from(AlumnoCiclo.class, "ac")
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
    public List<AlumnoCiclo> allByControlMeritoCicloNivel(DynatableFilter filter, ControlOrdenMerito controlBD, Integer nivel) {
        DynatableSql sql = new DynatableSql(filter)
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "alu.persona per", "carrera car", "car.facultad fac")
                .join("cicloAcademico ca")
                .join("controlMeritoCiclo control")
                .filter("nivel", nivel)
                .filter("control.id", controlBD)
                .searchFields("alu.codigo", "car.nombre", "fac.nombre")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("ac.ordenMeritoCiclo");

        return all(sql);
    }

    @Override
    public List<AlumnoCiclo> allByControlMeritoCarreraNivel(DynatableFilter filter, ControlOrdenMerito controlBD, Integer nivel) {
        DynatableSql sql = new DynatableSql(filter)
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "alu.persona per", "carrera car", "car.facultad fac")
                .join("cicloAcademico ca")
                .join("controlMeritoCarrera control")
                .filter("nivel", nivel)
                .filter("control.id", controlBD)
                .searchFields("alu.codigo", "car.nombre", "fac.nombre")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("ac.ordenMeritoCiclo");

        return all(sql);
    }

    @Override
    public List<AlumnoCiclo> allByControlMeritoFacultadNivel(DynatableFilter filter, ControlOrdenMerito controlBD, Integer nivel) {
        DynatableSql sql = new DynatableSql(filter)
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "alu.persona per", "carrera car", "car.facultad fac")
                .join("cicloAcademico ca")
                .join("controlMeritoFacultad control")
                .filter("nivel", nivel)
                .filter("control.id", controlBD)
                .searchFields("alu.codigo", "car.nombre", "fac.nombre")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("ac.ordenMeritoCiclo");

        return all(sql);
    }

    @Override
    public List<AlumnoCiclo> allActivosRegularesByCicloResumen(CicloAcademico ciclo) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select {ac.*},{a.*},{sa.*} ");
        sql.append("   from aca_alumno_ciclo as ac ");
        sql.append("   join aca_ciclo_academico as ca on ca.id = ac.id_ciclo_academico ");
        sql.append("   join aca_alumno a on a.id = ac.id_alumno ");
        sql.append("   join aca_situacion_academica sa on sa.id = a.id_situacion_academica ");
        sql.append("   join ( ");
        sql.append("            select aacc.id_alumno as id_alumno, max(ccaa.codigo) as codigo ");
        sql.append("              from aca_alumno_ciclo as aacc ");
        sql.append("              join aca_ciclo_academico as ccaa on ccaa.id = aacc.id_ciclo_academico ");
        sql.append("             where aacc.estado = :MAT ");
        sql.append("               and ccaa.tipo = :REG ");
        sql.append("             group by aacc.id_alumno ");
        sql.append("     ) wac on wac.id_alumno = a.id and wac.codigo = ca.codigo ");
        sql.append("   where exists ( ");
        sql.append("            select 1 ");
        sql.append("              from aca_matricula_resumen as mr ");
        sql.append("             where mr.id_alumno = a.id ");
        sql.append("               and mr.id_ciclo_academico = :CICLO ");
        sql.append("     ) ");

        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addEntity("ac", AlumnoCiclo.class)
                .addEntity("a", Alumno.class)
                .addEntity("sa", SituacionAcademica.class);

        query.setParameter("MAT", "MAT");
        query.setParameter("REG", "REG");
        query.setParameter("CICLO", ciclo.getId());

        List<AlumnoCiclo> alumnosCiclos = new ArrayList();
        List<Object[]> rows = query.list();
        for (Object[] row : rows) {
            AlumnoCiclo ac = (AlumnoCiclo) row[0];
            Alumno alu = (Alumno) row[1];
            SituacionAcademica sit = (SituacionAcademica) row[2];

            ac.setAlumno(alu);
            alu.setSituacionAcademica(sit);
            alumnosCiclos.add(ac);
        }
        return alumnosCiclos;
    }

    @Override
    public AlumnoCiclo findActivosRegularesByCicloResumen(CicloAcademico ciclo, Alumno alumno) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select {ac.*},{a.*},{sa.*} ");
        sql.append("   from aca_alumno_ciclo as ac ");
        sql.append("   join aca_ciclo_academico as ca on ca.id = ac.id_ciclo_academico ");
        sql.append("   join aca_alumno a on a.id = ac.id_alumno  ");
        sql.append("   join aca_situacion_academica sa on sa.id = a.id_situacion_academica ");
        sql.append("   join ( ");
        sql.append("            select aacc.id_alumno as id_alumno, max(ccaa.codigo) as codigo ");
        sql.append("              from aca_alumno_ciclo as aacc ");
        sql.append("              join aca_ciclo_academico as ccaa on ccaa.id = aacc.id_ciclo_academico ");
        sql.append("             where aacc.estado = :MAT ");
        sql.append("               and ccaa.tipo = :REG and aacc.id_alumno = :alumno");
        sql.append("             group by aacc.id_alumno ");
        sql.append("     ) wac on wac.id_alumno = a.id and wac.codigo = ca.codigo ");

        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addEntity("ac", AlumnoCiclo.class)
                .addEntity("a", Alumno.class)
                .addEntity("sa", SituacionAcademica.class);

        query.setParameter("MAT", "MAT");
        query.setParameter("REG", "REG");
        query.setParameter("alumno", alumno.getId());

        Object[] row = (Object[]) query.uniqueResult();
        AlumnoCiclo ac = (AlumnoCiclo) row[0];
        Alumno alu = (Alumno) row[1];
        SituacionAcademica sit = (SituacionAcademica) row[2];

        ac.setAlumno(alu);
        alu.setSituacionAcademica(sit);

        return ac;
    }

    @Override
    public AlumnoCiclo findActivosRegularesByCiclo(CicloAcademico ciclo, Alumno alumno) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select {ac.*},{a.*},{sa.*} ");
        sql.append("   from aca_alumno_ciclo as ac ");
        sql.append("   join aca_ciclo_academico as ca on ca.id = ac.id_ciclo_academico ");
        sql.append("   join aca_alumno a on a.id = ac.id_alumno  ");
        sql.append("   join aca_situacion_academica sa on sa.id = a.id_situacion_academica ");
        sql.append("   join ( ");
        sql.append("            select aacc.id_alumno as id_alumno, ccaa.codigo as codigo ");
        sql.append("              from aca_alumno_ciclo as aacc ");
        sql.append("              join aca_ciclo_academico as ccaa on ccaa.id = :ciclo ");
        sql.append("             where aacc.estado = :MAT ");
        sql.append("               and ccaa.tipo = :REG and aacc.id_alumno = :alumno");
        sql.append("             group by aacc.id_alumno ");
        sql.append("     ) wac on wac.id_alumno = a.id and wac.codigo = ca.codigo ");

        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addEntity("ac", AlumnoCiclo.class)
                .addEntity("a", Alumno.class)
                .addEntity("sa", SituacionAcademica.class);

        query.setParameter("MAT", "MAT");
        query.setParameter("REG", "REG");
        query.setParameter("alumno", alumno.getId());
        query.setParameter("ciclo", ciclo.getId());

        Object[] row = (Object[]) query.uniqueResult();
        AlumnoCiclo ac = (AlumnoCiclo) row[0];
        Alumno alu = (Alumno) row[1];
        SituacionAcademica sit = (SituacionAcademica) row[2];

        ac.setAlumno(alu);
        alu.setSituacionAcademica(sit);

        return ac;
    }

    @Override
    public List<AlumnoCiclo> allByNmatAndInh(List<CicloAcademico> cicloAnt) {

        Octavia sqlSub = Octavia.query()
                .from(Egresado.class, "eg")
                .join("alumno al");

        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("cicloAcademico ca", "alumno alu")
                .left("carrera", "orientacionCarrera", "situacionInicio", "situacionFinal", "controlMeritoCiclo")
                .left("controlMeritoFacultad", "controlMeritoCarrera")
                .left("alu.situacionAcademica sa")
                .notIn("sa.codigo", Arrays.asList(S_4.getValue(), S_D.getValue(), S_XD.getValue(), S_4U.getValue()))
                .notExists(sqlSub)
                .linkedBy("alu.id", "al.id")
                .in("ca.id", cicloAnt)
                .in("estado", Arrays.asList(NMAT, INH));

        return sql.all(getCurrentSession());
    }

    @Override
    public List<AlumnoCiclo> allByAlumnoDescRegular(Alumno alumno) {

        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca")
                .leftJoin("situacionInicio si", "situacionFinal sf", "userRegistro ur")
                //.leftJoin("userModificacion um")
                .filter("alu.id", alumno)
                .filter("ac.estado", EstadoMatriculaEnum.MAT.name())
                .filter("ca.tipo", REG)
                .orderBy("ca.codigo desc");
        return all(sql);
    }

    @Override
    public List<AlumnoCiclo> allByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca")
                .leftJoin("situacionInicio si", "situacionFinal sf")
                //.leftJoin("userModificacion um", "userRegistro ur")
                .filter("alu.id", alumno);
        return all(sql);
    }

    @Override
    public List<AlumnoCiclo> allByAlumnos(List<Alumno> alumnos) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca")
                .leftJoin("situacionInicio si", "situacionFinal sf")
                //.leftJoin("userModificacion um", "userRegistro ur")
                .in("alu.id", alumnos);
        return all(sql);
    }

    @Override
    public List<AlumnoCiclo> allByCicloAcademicos(List<CicloAcademico> ciclos) {

        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("cicloAcademico ca", "alumno alu")
                .join("alu.persona per", "carrera car", "car.facultad fac")
                .in("ca.id", ciclos);

        return sql.all(getCurrentSession());
    }

    @Override
    public void deleteById(AlumnoCiclo alumnoCiclo) {
        StringBuilder sql = new StringBuilder();
        sql.append("delete from ").append(AlumnoCiclo.class.getSimpleName()).append(" ");
        sql.append("where id = :IDD ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("IDD", alumnoCiclo.getId());
        query.executeUpdate();
    }

    @Override
    public List<AlumnoCiclo> allByAlumnosReg(List<Alumno> alumnos) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca")
                .leftJoin("situacionInicio si", "situacionFinal sf", "userRegistro ur")
                //.leftJoin("userModificacion um")
                .in("alu.id", alumnos)
                .filter("ac.estado", EstadoMatriculaEnum.MAT.name())
                .filter("ca.tipo", REG)
                .orderBy("ca.codigo desc");
        return all(sql);
    }

}
