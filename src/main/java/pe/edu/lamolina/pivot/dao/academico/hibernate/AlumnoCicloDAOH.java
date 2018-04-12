package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.math.BigDecimal;
import java.util.List;
import org.hibernate.LockOptions;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.TipoCicloEnum;

@Repository
public class AlumnoCicloDAOH extends AbstractEasyDAO<AlumnoCiclo> implements AlumnoCicloDAO {

    public AlumnoCicloDAOH() {
        super();
        setClazz(AlumnoCiclo.class);
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
                .leftJoin("situacionInicio si", "situacionFinal sf", "userRegistro ur", "orientacionCarrera oc")
                .leftJoin("userModificacion um")
                .filter("alu.id", alumno)
                .filter("ca.id", cicloAcademico);
        return find(sql);
    }

    @Override
    public AlumnoCiclo findActiveByAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca", "carrera car")
                .leftJoin("situacionInicio si", "situacionFinal sf", "userRegistro ur", "orientacionCarrera oc")
                .leftJoin("userModificacion um")
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
                .leftJoin("situacionInicio si", "situacionFinal sf", "userRegistro ur", "orientacionCarrera oc")
                .leftJoin("userModificacion um")
                .in("ac.estado", estadosEnums)
                .filter("alu.id", alumno)
                .filter("ca.id", cicloAcademico);
        return find(sql);
    }

    @Override
    public AlumnoCiclo findLastByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca", "carrera car", "orientacionCarrera oc")
                .join("situacionInicio si", "situacionFinal sf", "userRegistro ur")
                .leftJoin("userModificacion um")
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
                .left("situacionInicio si", "situacionFinal sf", "userRegistro ur", "orientacionCarrera oc")
                .leftJoin("userModificacion um")
                .filter("ca.tipo", TipoCicloEnum.REG.name())
                .filter("alu.id", alumno)
                .filter("ac.estado", EstadoMatriculaEnum.MAT.name())
                .orderBy("ca.codigo desc")
                .limit(BigDecimal.ONE.intValue());
        return find(sql);
    }

    @Override
    public AlumnoCiclo findActiveAnteriorByAlumno(Alumno alumno, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca", "carrera car")
                .join("situacionInicio si", "situacionFinal sf", "userRegistro ur")
                .leftJoin("userModificacion um", "orientacionCarrera oc")
                .filter("alu.id", alumno)
                .filter("ca.codigo", "<", cicloAcademico.getCodigo())
                .filter("ac.estado", EstadoMatriculaEnum.MAT.name())
                .orderBy("ca.year DESC", "ca.numeroCiclo DESC")
                .limit(1);
        return find(sql);
    }

    @Override
    public List<AlumnoCiclo> allAnterioresEQByCicloAlumno(Alumno alumno, CicloAcademico cicloAcademico, Integer limit) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca", "carrera car")
                .join("situacionInicio si", "situacionFinal sf", "userRegistro ur")
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
                .join("situacionInicio si", "situacionFinal sf", "userRegistro ur")
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
                .join("situacionInicio si", "situacionFinal sf", "userRegistro ur")
                .leftJoin("userModificacion um", "orientacionCarrera oc")
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
                .join("situacionInicio si", "situacionFinal sf", "userRegistro ur")
                .leftJoin("userModificacion um", "orientacionCarrera oc")
                .filter("alu.id", alumno)
                .filter("ca.codigo", "<", cicloAcademico.getCodigo())
                .filter("ac.estado", EstadoMatriculaEnum.INH.name())
                .orderBy("ca.year DESC", "ca.numeroCiclo DESC")
                .limit(1);
        return find(sql);
    }

    @Override
    public List<AlumnoCiclo> allByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu")
                .leftJoin("situacionInicio si", "situacionFinal sf", "userRegistro ur")
                .leftJoin("userModificacion um", "alumnoCicloCurso acc")
                .filter("alu.id", alumno);
        return all(sql);
    }

    @Override
    public List<AlumnoCiclo> allActivesByAlumnoAsc(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca")
                //   .leftJoin("situacionInicio si", "situacionFinal sf", "userRegistro ur")
                //    .leftJoin("userModificacion um")
                .filter("alu.id", alumno)
                .filter("ac.estado", EstadoMatriculaEnum.MAT.name())
                .orderBy("ca.codigo asc");
        return all(sql);
    }

    @Override
    public List<AlumnoCiclo> allByAlumnoAsc(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca")
                //   .leftJoin("situacionInicio si", "situacionFinal sf", "userRegistro ur")
                //    .leftJoin("userModificacion um")
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
    public void updateSituacionFinal(AlumnoCiclo alumnoCiclo) {
        Octavia octavia = Octavia.update(AlumnoCiclo.class);
        octavia.set(alumnoCiclo, "situacionFinal");
        octavia.set(alumnoCiclo, "fechaModificacion");
        octavia.set(alumnoCiclo, "userModificacion");
        this.update(octavia);
    }

}
