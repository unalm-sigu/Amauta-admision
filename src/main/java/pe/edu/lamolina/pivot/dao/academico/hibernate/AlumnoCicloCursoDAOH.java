package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.math.BigDecimal;
import java.util.List;
import org.hibernate.Query;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.tramite.AutorizacionRegistro;

@Repository
public class AlumnoCicloCursoDAOH extends AbstractEasyDAO<AlumnoCicloCurso> implements AlumnoCicloCursoDAO {

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
    public List<AlumnoCicloCurso> allActivoByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico", "acc.curso")
                .filter("al.id", alumno)
                .filter("registroActivo", 1);

        return sql.all(getCurrentSession());
    }

    @Override
    public List<AlumnoCicloCurso> allAprobadoActivoByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico", "acc.curso cu")
                .left("cu.departamentoAcademico")
                .filter("al.id", alumno)
                .filter("estaAprobado", 1)
                .filter("registroActivo", 1);

        return sql.all(getCurrentSession());
    }

    @Override
    public List<AlumnoCicloCurso> allActivoByAlumnoCiclo(AlumnoCiclo alumnoCiclo) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico", "acc.curso cu")
                .left("cu.departamentoAcademico")
                .filter("ac.id", alumnoCiclo)
                .filter("registroActivo", 1);

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
                .filter("registroActivo", 1);

        return (Long) sql.find(getCurrentSession());
    }

    @Override
    public List<AlumnoCicloCurso> findHistorial(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico", "acc.curso")
                .filter("al.id", alumno);

        return sql.all(getCurrentSession());
    }

    @Override
    public List<AlumnoCicloCurso> allByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cu")
                .join("ac.carrera", "ac.situacionInicio")
                .left("ac.situacionFinal", "ac.orientacionCarrera")
                .filter("al.id", alumno)
                .orderBy("ca.codigo desc", "cu.nombre");

        return sql.all(getCurrentSession());
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
                .join("ac.carrera", "ac.situacionInicio")
                .left("ac.situacionFinal", "ac.orientacionCarrera")
                .filter("al.id", alumno)
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
    public List<AlumnoCicloCurso> allByAlumnoOrderByCurso(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cu")
                .join("ac.carrera", "ac.situacionInicio")
                .left("ac.situacionFinal", "ac.orientacionCarrera")
                .filter("al.id", alumno)
                .orderBy("cu.nombre");

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
        sql.filter("areg.id", autorizacionRegistro);
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

}
