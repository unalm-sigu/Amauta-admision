package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Insecto;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.NMAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RCI;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RCU;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RET;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import pe.edu.lamolina.model.tramite.AutorizacionRegistro;

@Repository
public class AlumnoCicloCursoDAOH extends AbstractEasyDAO<AlumnoCicloCurso> implements AlumnoCicloCursoDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public AlumnoCicloCursoDAOH() {
        super();
        setClazz(AlumnoCicloCurso.class);
    }

    @Override
    public AlumnoCicloCurso findByAlumnoCicloCurso(Alumno alumno, CicloAcademico cicloAcademico, Curso curso) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cur")
                .filter("al.id", alumno)
                .filter("ca.id", cicloAcademico)
                .filter("cur.id", curso)
                .filter("acc.estado", EstadoMatriculaEnum.MAT.name())
                .filter("ac.estado", EstadoMatriculaEnum.MAT.name())
                .filter("acc.registroActivo", BigDecimal.ONE.intValue());
        return find(sql);
    }

    @Override
    public AlumnoCicloCurso findByAlumnoCicloCursoEstados(Alumno alumno, CicloAcademico cicloAcademico, Curso curso, List<EstadoMatriculaEnum> estados) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cur")
                .filter("al.id", alumno)
                .filter("ca.id", cicloAcademico)
                .filter("cur.id", curso)
                .in("acc.estado", estados)
                .in("ac.estado", estados);
        return find(sql);
    }

    @Override
    public List<AlumnoCicloCurso> allAprobadoActivoByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico", "acc.curso cu")
                .left("cu.departamentoAcademico")
                .filter("al.id", alumno)
                .in("ac.estado", Arrays.asList(MAT, NMAT))
                .filter("acc.estado", MAT)
                .filter("acc.estaAprobado", 1)
                .filter("acc.registroActivo", 1);

        return all(sql);
    }

    @Override
    public List<AlumnoCicloCurso> allAprobadoActivoByAlumnos(List<Alumno> alumnos) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico", "acc.curso cu")
                .left("cu.departamentoAcademico")
                .in("al.id", alumnos)
                .in("ac.estado", Arrays.asList(MAT, NMAT))
                .filter("acc.estado", MAT)
                .filter("acc.estaAprobado", 1)
                .filter("acc.registroActivo", 1);

        return all(sql);
    }

    @Override
    public List<AlumnoCicloCurso> allVecesLlevadoByAlumnos(List<Alumno> alumnos) {
        Octavia sql = Octavia.query()
                .select("al.id", "cu.id", "count(*)")
                .into(AlumnoCicloCurso.class)
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cu")
                .left("cu.departamentoAcademico")
                .filter("ca.tipo", TipoCicloEnum.REG)
                .filter("acc.estado", "!=", RCI)
                .filter("acc.estado", "!=", RCU)
                .filter("acc.estado", "!=", RET)
                .in("al.id", alumnos)
                .filter("acc.registroActivo", 1)
                .groupBy("al.id", "cu.id");

        return all(sql);
    }

    @Override
    public List<AlumnoCicloCurso> allActivoByAlumnoCiclo(AlumnoCiclo alumnoCiclo) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico", "acc.curso cu")
                .left("cu.departamentoAcademico")
                .filter("ac.id", alumnoCiclo)
                .filter("registroActivo", 1);

        return all(sql);
    }

    @Override
    public List<AlumnoCicloCurso> allByAlumnoCicloNoFilters(AlumnoCiclo alumnoCiclo) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico", "acc.curso cu")
                .left("cu.departamentoAcademico")
                .filter("ac.id", alumnoCiclo);

        return sql.all(getCurrentSession());
    }

    @Override
    public List<AlumnoCicloCurso> allDesaprobadoActivoByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico", "acc.curso")
                .filter("al.id", alumno)
                .filter("estaAprobado", 0)
                .filter("registroActivo", 1);

        return sql.all(getCurrentSession());
    }

    @Override
    public Long countByCursoAlumno(Curso curso, Alumno alumno) {
        Octavia sql = Octavia.query()
                .selectCount()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico", "acc.curso")
                .filter("acc.curso", curso)
                .filter("ac.alumno", alumno)
                .filter("acc.estado", EstadoMatriculaEnum.MAT.name())
                .filter("registroActivo", 1);

        return (Long) sql.find(getCurrentSession());
    }

    @Override
    public Long countByAlumnoCiclo(AlumnoCiclo alumnoCiclo) {
        Octavia sql = Octavia.query()
                .selectCount()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico", "acc.curso")
                .filter("ac.id", alumnoCiclo);
        return (Long) sql.find(getCurrentSession());
    }

    @Override
    public List<AlumnoCicloCurso> findHistorial(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico", "acc.curso")
                .filter("al.id", alumno)
                .filter("acc.registroActivo", BigDecimal.ONE.intValue());

        return sql.all(getCurrentSession());
    }

    @Override
    public List<AlumnoCicloCurso> allActivosByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cu")
                .join("ac.carrera", "ac.situacionInicio")
                .left("ac.situacionFinal", "ac.orientacionCarrera", "tipoCursoCurricula")
                .filter("al.id", alumno)
                .filter("acc.registroActivo", BigDecimal.ONE.intValue())
                .orderBy("ca.codigo desc", "cu.nombre");

        return all(sql);
    }

    @Override
    public List<AlumnoCicloCurso> allByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cu")
                .join("ac.carrera", "ac.situacionInicio")
                .left("ac.situacionFinal", "ac.orientacionCarrera", "tipoCursoCurricula")
                .filter("al.id", alumno)
                .orderBy("ca.codigo desc", "cu.nombre");

        return all(sql);
    }

    @Override
    public List<AlumnoCicloCurso> allByAlumnoAndAlumnoCiclo(Alumno alumno, AlumnoCiclo alumnoCiclo) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cu")
                .join("ac.carrera", "ac.situacionInicio")
                .left("ac.situacionFinal", "ac.orientacionCarrera")
                .filter("al.id", alumno)
                .filter("ac.id", alumnoCiclo)
                .orderBy("ca.codigo desc", "cu.nombre");

        return sql.all(getCurrentSession());
    }

    @Override
    public List<AlumnoCicloCurso> allOperativesByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cu")
                .join("ac.carrera")
                .left("ac.situacionFinal", "ac.orientacionCarrera", "ac.situacionInicio", "cu.modalidadEstudio")
                .filter("al.id", alumno)
                .filter("acc.estado", EstadoMatriculaEnum.MAT.name())
                .filter("acc.registroActivo", BigDecimal.ONE.intValue())
                .orderBy("ca.codigo desc", "cu.nombre");

        return sql.all(getCurrentSession());
    }

    @Override
    public List<AlumnoCicloCurso> allOperativesPendingByYear(String year) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cu")
                .join("ac.carrera")
                .left("ac.situacionFinal", "ac.orientacionCarrera", "ac.situacionInicio")
                .complexFilter("SUBSTRING(al.codigo,1,4)", year)
                .filter("acc.estado", EstadoMatriculaEnum.MAT.name())
                .filter("acc.registroActivo", BigDecimal.ONE.intValue())
                .filter("al.promedioProcesado", 0)
                .orderBy("ca.codigo desc", "cu.nombre");

        return sql.all(getCurrentSession());
    }

    @Override
    public List<AlumnoCicloCurso> allOperativesByAlumnoAnterioresCiclo(Alumno alumno, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cu")
                .join("ac.carrera", "ac.situacionInicio")
                .left("ac.situacionFinal", "ac.orientacionCarrera")
                .filter("ca.codigo", "<", cicloAcademico.getCodigo())
                .filter("al.id", alumno)
                .filter("acc.estado", EstadoMatriculaEnum.MAT.name())
                .filter("ac.estado", EstadoMatriculaEnum.MAT.name())
                .filter("acc.registroActivo", BigDecimal.ONE.intValue())
                .orderBy("ca.codigo desc", "cu.nombre");

        return sql.all(getCurrentSession());
    }

    @Override
    public List<AlumnoCicloCurso> allOperatives(ModalidadEstudio modalidadEstudio,
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
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cu")
                .join("ac.carrera", "ac.situacionInicio")
                .left("ac.situacionFinal", "ac.orientacionCarrera")
                //   .filter("ca.codigo", "<", cicloAcademico.getCodigo())
                .filter("acc.estado", EstadoMatriculaEnum.MAT.name())
                .filter("ac.estado", EstadoMatriculaEnum.MAT.name())
                .filter("acc.registroActivo", BigDecimal.ONE.intValue())
                .exists(sql0)
                .linkedBy("al.id", "alu0.id")
                .orderBy("ca.codigo desc", "cu.nombre");

        return sql.all(getCurrentSession());
    }

    @Override
    public List<AlumnoCicloCurso> allFullDataByAlumno(Alumno alumno,
            EstadoMatriculaEnum estadoMatriculaEnum) {

        Octavia sql0 = Octavia.query()
                .from(Alumno.class, "alu0")
                .join("modalidadEstudio me0", "situacionAcademica sa0", "cicloActivo ca0")
                .join("persona per0", "carrera car0", "car0.facultad fa0")
                .leftJoin("per0.tipoDocumento td0", "cicloActivo cia0", "cicloIngreso ci0")
                .filter("alu0.id", alumno);

        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cu")
                .join("ac.carrera", "ac.situacionInicio")
                .left("ac.situacionFinal", "ac.orientacionCarrera")
                //   .filter("ca.codigo", "<", cicloAcademico.getCodigo())
                .filter("acc.estado", EstadoMatriculaEnum.MAT.name())
                .filter("ac.estado", EstadoMatriculaEnum.MAT.name())
                .filter("acc.registroActivo", BigDecimal.ONE.intValue())
                .exists(sql0)
                .linkedBy("al.id", "alu0.id")
                .orderBy("ca.codigo desc", "cu.nombre");

        return sql.all(getCurrentSession());
    }

    @Override
    public List<AlumnoCicloCurso> allOperativesByAlumnoCicloLessOrEqual(Alumno alumno, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cu")
                .join("ac.carrera")
                .left("ac.situacionFinal", "ac.orientacionCarrera", "ac.situacionInicio")
                .filter("ca.codigo", "<=", cicloAcademico.getCodigo())
                .filter("al.id", alumno)
                .filter("acc.estado", EstadoMatriculaEnum.MAT.name())
                .filter("ac.estado", EstadoMatriculaEnum.MAT.name())
                .filter("acc.registroActivo", BigDecimal.ONE.intValue())
                .orderBy("ca.codigo desc", "cu.nombre");

        return sql.all(getCurrentSession());
    }

    @Override
    public List<AlumnoCicloCurso> allOperativesByAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cu")
                .join("ac.carrera", "ac.situacionInicio")
                .left("ac.situacionFinal", "ac.orientacionCarrera")
                .filter("acc.estado", EstadoMatriculaEnum.MAT.name())
                .filter("ac.estado", EstadoMatriculaEnum.MAT.name())
                .filter("al.id", alumno)
                .filter("ca.id", cicloAcademico)
                .filter("acc.registroActivo", BigDecimal.ONE.intValue());

        return all(sql);
    }

    @Override
    public List<AlumnoCicloCurso> allOperativesByModalidadEstudio(ModalidadEstudioEnum modalidadEstudioEnum) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cu")
                .join("ac.carrera", "ac.situacionInicio", "ca.modalidadEstudio mde")
                .left("ac.situacionFinal", "ac.orientacionCarrera")
                .filter("acc.estado", EstadoMatriculaEnum.MAT.name())
                .filter("ac.estado", EstadoMatriculaEnum.MAT.name())
                .filter("mde.codigo", modalidadEstudioEnum)
                .filter("acc.registroActivo", BigDecimal.ONE.intValue());

        return all(sql);
    }

    @Override
    public List<AlumnoCicloCurso> allByAlumnoOrderByCurso(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cu")
                .join("ac.carrera", "ac.situacionInicio")
                .left("ac.situacionFinal", "ac.orientacionCarrera", "tipoCursoCurricula")
                .filter("acc.registroActivo", BigDecimal.ONE.intValue())
                .filter("al.id", alumno)
                .orderBy("cu.nombre");

        return sql.all(getCurrentSession());
    }

    @Override
    public List<AlumnoCicloCurso> allByAlumnoOrderByTipoCurso(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cu")
                .join("ac.carrera", "ac.situacionInicio")
                .left("ac.situacionFinal", "ac.orientacionCarrera", "tipoCursoCurricula tc")
                .filter("acc.registroActivo", BigDecimal.ONE.intValue())
                .filter("al.id", alumno)
                .filter("acc.estaAprobado", BigDecimal.ONE.intValue())
                .orderBy("tc.orden", "ca.codigo");

        return sql.all(getCurrentSession());
    }

    @Override
    public Long countByCursoAlumnoAnterioresCiclo(Curso curso, Alumno alumno, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .selectCount()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cu")
                .join("ac.carrera", "ac.situacionInicio")
                .left("ac.situacionFinal", "ac.orientacionCarrera")
                .filter("ca.codigo", "<", cicloAcademico.getCodigo())
                .filter("al.id", alumno)
                .filter("cu.id", curso)
                .filter("acc.registroActivo", BigDecimal.ONE.intValue())
                .filter("acc.estado", EstadoMatriculaEnum.MAT.name())
                .filter("ac.estado", EstadoMatriculaEnum.MAT.name());

        return (Long) sql.find(getCurrentSession());
    }

    @Override
    public Long countByCursoAlumnoAnterioresCicloReg(Curso curso, Alumno alumno, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .selectCount()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cu")
                .join("ac.carrera", "ac.situacionInicio")
                .left("ac.situacionFinal", "ac.orientacionCarrera")
                .filter("ca.codigo", "<", cicloAcademico.getCodigo())
                .filter("al.id", alumno)
                .filter("cu.id", curso)
                .filter("acc.registroActivo", BigDecimal.ONE.intValue())
                .filter("ca.tipo", TipoCicloEnum.REG)
                .filter("acc.estado", EstadoMatriculaEnum.MAT.name())
                .filter("ac.estado", EstadoMatriculaEnum.MAT.name());

        return (Long) sql.find(getCurrentSession());
    }

    @Override
    public void deleteByAlumnoCiclo(AlumnoCiclo alumnoCiclo) {

        StringBuilder sql = new StringBuilder();
        sql.append(" delete from AlumnoCicloCurso ")
                .append("   where  alumnoCiclo.id = :ALUMNOCICLO ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("ALUMNOCICLO", alumnoCiclo.getId());
        query.executeUpdate();
    }

    @Override
    public AlumnoCicloCurso find(AlumnoCicloCurso alumnoCicloCursoForm) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cu")
                .filter("acc.id", alumnoCicloCursoForm);
        return find(sql);
    }

    @Override
    public List<AlumnoCicloCurso> allByAlumnoCiclo(AlumnoCiclo alumnoCiclo) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cur")
                .filter("ac.id", alumnoCiclo)
                .filter("acc.estado", EstadoMatriculaEnum.MAT.name())
                .filter("acc.registroActivo", BigDecimal.ONE.intValue());
        return all(sql);
    }

    @Override
    public List<AlumnoCicloCurso> allStateByAlumnoCiclo(AlumnoCiclo alumnoCiclo) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cur")
                .filter("ac.id", alumnoCiclo);
        return all(sql);
    }

    @Override
    public List<AlumnoCicloCurso> allByAlumnoCicloActivosOrAutorizacionRegistro(AlumnoCiclo alumnoCiclo, AutorizacionRegistro autorizacionRegistro) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cur")
                .left("autorizacionRegistro areg", "acc.alumnoCicloCursoOrigen")
                .filter("ac.id", alumnoCiclo);
        if (autorizacionRegistro != null) {
            sql.beginBlock()
                    .filter("areg.id", autorizacionRegistro);

        }
        sql.beginBlock()
                .filter("acc.estado", EstadoMatriculaEnum.MAT.name())
                .filter("acc.registroActivo", BigDecimal.ONE.intValue())
                .endBlock();
        if (autorizacionRegistro != null) {
            sql.endBlock();
        }
        return all(sql);
    }

    @Override
    public List<AlumnoCicloCurso> allByAlumnoCicloActivosAndAutorizacionRegistro(AlumnoCiclo alumnoCiclo, AutorizacionRegistro autorizacionRegistro) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cur")
                .left("autorizacionRegistro areg", "acc.alumnoCicloCursoOrigen")
                .filter("ac.id", alumnoCiclo);
        if (autorizacionRegistro != null) {
            sql.filter("areg.id", autorizacionRegistro);
        }
        sql.orderBy("cur.codigo", "cur.nombre");
        return all(sql);
    }

    @Override
    public List<AlumnoCicloCurso> allByAutorizacionRegistro(AutorizacionRegistro autorizacionRegistro) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cur")
                .left("autorizacionRegistro areg", "acc.alumnoCicloCursoOrigen")
                .filter("areg.id", autorizacionRegistro);
        return all(sql);
    }

    @Override
    public List<AlumnoCicloCurso> allByAlumnoCicloAsc(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cu")
                .join("ac.carrera", "ac.situacionInicio")
                .left("ac.situacionFinal", "ac.orientacionCarrera")
                .filter("al.id", alumno)
                .filter("acc.registroActivo", BigDecimal.ONE.intValue())
                .orderBy("ca.codigo asc", "cu.nombre");

        return sql.all(getCurrentSession());
    }

    @Override
    public void updateEstadoRegistroActivo(AlumnoCicloCurso alumnoCicloCurso) {
        Octavia octavia = Octavia.update(AlumnoCicloCurso.class);
        octavia.set(alumnoCicloCurso, "estado");
        octavia.set(alumnoCicloCurso, "registroActivo");
        this.update(octavia);
    }

    @Override
    public void updateCurso(AlumnoCicloCurso cursosAprobado) {
        Octavia octavia = Octavia.update(AlumnoCicloCurso.class);
        octavia.set(cursosAprobado, "tipoCursoCurricula", "esEquivalente", "cursoEquivalente");
        this.update(octavia);
    }

    @Override
    public List<AlumnoCicloCurso> allDesaproActivoByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico", "acc.curso cu")
                .left("cu.departamentoAcademico")
                .filter("al.id", alumno)
                .filter("ac.estado", MAT)
                .filter("estaAprobado", 0)
                .filter("acc.registroActivo", 1);

        return all(sql);
    }

    @Override
    public List<AlumnoCicloCurso> allDesaproActivoByAlumnos(List<Alumno> alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico", "acc.curso cu")
                .left("cu.departamentoAcademico")
                .in("al.id", alumno)
                .filter("ac.estado", MAT)
                .filter("estaAprobado", 0)
                .filter("acc.registroActivo", 1);

        return all(sql);
    }

    @Override
    public List<AlumnoCicloCurso> allByNombre(Alumno alumno, CicloAcademico academico, String nombre) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico aca", "acc.curso cu")
                .left("cu.departamentoAcademico")
                .filter("al.id", alumno)
                .filter("aca.id", academico);
        if (nombre != null) {
            nombre = "%" + nombre.replaceAll(" ", "%") + "%";
            sql.beginBlock()
                    .__().filter("cu.nombre", "like", nombre)
                    .__().filter("cu.codigo", "like", nombre)
                    .endBlock();
        }
        sql.filter("acc.registroActivo", BigDecimal.ONE.intValue())
                .notIn("acc.estado", Arrays.asList(RCU.name(), RCI.name()));

        return all(sql);
    }

    @Override
    public List<AlumnoCicloCurso> allOperativesByAlumnos(List<Alumno> alumnos) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cu")
                .join("ac.carrera")
                .left("ac.situacionFinal", "ac.orientacionCarrera", "ac.situacionInicio", "cu.modalidadEstudio")
                .in("al.id", alumnos)
                .filter("acc.estado", EstadoMatriculaEnum.MAT.name())
                .filter("acc.registroActivo", BigDecimal.ONE.intValue())
                .orderBy("ca.codigo desc", "cu.nombre");

        return all(sql);
    }

    @Override
    public List<AlumnoCicloCurso> allByAlumnos(List<Alumno> alumnos) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "curso cu", "ac.cicloAcademico")
                .in("al.id", alumnos);

        return all(sql);
    }

    @Override
    public AlumnoCicloCurso allByAlumnoCicloCurso(Alumno alumno, CicloAcademico cicloAcademico, Curso curso) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cur")
                .filter("al.id", alumno)
                .filter("ca.id", cicloAcademico)
                .filter("cur.id", curso)
                .filter("acc.registroActivo", BigDecimal.ONE.intValue());
        return find(sql);
    }

    @Override
    public List<AlumnoCicloCurso> allByAlumnosCiclos(List<AlumnoCiclo> alumnosCiclos) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cur")
                .in("acc.estado", Arrays.asList(EstadoMatriculaEnum.MAT.name(), RCI.name(), RET.name(), RCU.name()))
                .in("ac.estado", Arrays.asList(EstadoMatriculaEnum.MAT.name(), RCI.name(), RET.name(), RCU.name()))
                //                .filter("acc.registroActivo", BigDecimal.ONE.intValue())
                .in("ac.id", alumnosCiclos);
        return all(sql);
    }

    @Override
    public List<AlumnoCicloCurso> allCursadosByAlumnosCurso(List<Alumno> alumnos, Curso curso) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cur")
                .in("al.id", alumnos)
                .filter("cur.id", curso)
                .filter("acc.estado", EstadoMatriculaEnum.MAT)
                .filter("ac.estado", EstadoMatriculaEnum.MAT)
                .filter("acc.registroActivo", BigDecimal.ONE.intValue());
        return all(sql);
    }

    @Override
    public int updateList(List<AlumnoCicloCurso> alumnosCiclosCursos, String... columnas) {
        if (alumnosCiclosCursos.isEmpty()) {
            return 0;
        }

        long t1 = System.currentTimeMillis();
        Insecto sql = Insecto.createUpdate(AlumnoCicloCurso.class)
                .set(columnas)
                .with(alumnosCiclosCursos);

        Query query = getCurrentSession().createSQLQuery(sql.toString());
        int rows = query.executeUpdate();

        long t2 = System.currentTimeMillis();
        logger.info("{} AlumnoCicloCurso's actualizados en {} mseg....", rows, (t2 - t1));
        return rows;
    }

    @Override
    public void updateColumns(AlumnoCicloCurso alumnoCicloCursoFound, String... params) {
        Octavia octavia = Octavia.update(AlumnoCicloCurso.class);
        for (String column : params) {
            octavia.set(alumnoCicloCursoFound, column);
        }
        System.out.println("UPDATE " + octavia.toString());
        this.update(octavia);
    }

}
