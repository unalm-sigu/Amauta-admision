package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.TurnoAtencion;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.EPG;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.ESP;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.VIS;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.pivot.controller.academico.alumno.AlumnoResumen;

@Repository
public class MatriculaResumenDAOH extends AbstractEasyDAO<MatriculaResumen> implements MatriculaResumenDAO {

    public MatriculaResumenDAOH() {
        super();
        setClazz(MatriculaResumen.class);
    }

    @Override
    public MatriculaResumen findByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(MatriculaResumen.class, "mr")
                .join("alumno alu", "cicloAcademico ca")
                .filter("alu.id", alumno)
                .filter("ca.id", ciclo);

        return find(sql);
    }

    @Override
    public List<MatriculaResumen> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(MatriculaResumen.class, "mr")
                .join("alumno alu", "cicloAcademico ca", "alu.modalidadEstudio me")
                .left("alu.cicloActivo aluca", "alu.situacionAcademica sa")
                .filter("ca.id", ciclo);

        return all(sql);
    }

    @Override
    public MatriculaResumen findByFilter(CicloAcademico ciclo, Alumno alumno, EstadoMatriculaEnum estadoMatriculaCursoEnum) {
        Octavia sql = Octavia.query()
                .from(MatriculaResumen.class, "mr")
                .join("alumno alu", "cicloAcademico ca");

        if (ciclo != null) {
            sql.filter("ca.id", ciclo);
        }
        if (alumno != null) {
            sql.filter("alu.id", alumno);
        }
        if (estadoMatriculaCursoEnum != null) {
            sql.filter("mr.estado", estadoMatriculaCursoEnum);
        }

        return find(sql);
    }

    @Override
    public AlumnoResumen findResumenByCicloRolDynateable(CicloAcademico ciclo, String codigo, List<Long> filtros) {
        StringBuilder sql = new StringBuilder();
        sql.append("select new ").append(AlumnoResumen.class.getName());
        sql.append(" (   ");
        sql.append("   COALESCE(sum(case moe.codigo when :PRE then 1 else 0 end),0) AS pregrado,   ");
        sql.append("   COALESCE(sum(case moe.codigo when :EPG then 1 else 0 end),0) AS postgrado,   ");
        sql.append("   COALESCE(sum(case moe.codigo when :VIS  then 1 else 0 end),0) AS visitante,   ");
        sql.append("   COALESCE(sum(case moe.codigo when :ESP  then 1 else 0 end),0) AS  especiales  ");
        sql.append(" )   ");
        sql.append("  from ").append(MatriculaResumen.class.getName()).append(" as mr ");
        sql.append(" inner join mr.alumno al ");
        sql.append(" inner join mr.cicloAcademico ca ");
        sql.append(" inner join al.persona per ");
        sql.append(" inner join al.carrera car ");
        sql.append(" inner join al.situacionAcademica sita ");
        sql.append(" inner join ca.modalidadEstudio moe ");
        sql.append(" inner join car.facultad fac ");
        sql.append(" where 1=1 ");
        sql.append(" and ca.id=:prm_ciclo ");

        Query query = getCurrentSession().createQuery(sql.toString());
        //  query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        query.setParameter("prm_ciclo", ciclo.getId());
        query.setString("PRE", PRE.name());
        query.setString("EPG", EPG.name());
        query.setString("VIS", VIS.name());
        query.setString("ESP", ESP.name());
        return (AlumnoResumen) query.uniqueResult();
    }

    @Override
    public List<MatriculaResumen> allByCicloRolDynatable(DynatableFilter filter, CicloAcademico ciclo, String codigo, List<Long> filtros) {

        DynatableSql sql = new DynatableSql(filter);
        switch (RolEnum.valueOf(codigo)) {
            case MOD:
            case FAC:
            case ESP:
                sql.from(MatriculaResumen.class, "mr")
                        .join("alumno al", "cicloAcademico ca", "al.persona per", "per.tipoDocumento tdoc", "al.carrera car", "al.situacionAcademica sita")
                        .join("ca.modalidadEstudio moe", "car.facultad fac")
                        .leftJoin("al.cicloIngreso ci", "al.cicloActivo cia", "turnoAtencion ta")
                        .filter("ca.id", ciclo)
                        .searchFields("car.nombre", "fac.nombre", "al.codigo", "mr.prioridad", "mr.puntajePrioridad")
                        .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                        .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                        .in("car.id", filtros)
                        .orderBy("mr.prioridad", "per.paterno", "per.materno", "per.nombres");
                break;
            case TODO:
            default:
                sql.from(MatriculaResumen.class, "mr")
                        .join("alumno al", "cicloAcademico ca", "al.persona per", "per.tipoDocumento tdoc", "al.carrera car", "al.situacionAcademica sita")
                        .join("ca.modalidadEstudio moe", "car.facultad fac")
                        .leftJoin("al.cicloIngreso ci", "al.cicloActivo cia", "turnoAtencion ta")
                        .filter("ca.id", ciclo)
                        .searchFields("car.nombre", "fac.nombre", "al.codigo", "mr.prioridad", "mr.puntajePrioridad")
                        .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                        .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                        .orderBy("mr.prioridad", "per.paterno", "per.materno", "per.nombres");
                break;
        }

        sql.beginRelativeFilters();
        setCondicionModalidad(filter, sql);

        return sql.all(getCurrentSession());

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
    public void updatePuntajePrioridad(MatriculaResumen matriculaResumen) {
        Octavia octavia = Octavia.update(MatriculaResumen.class);
        octavia.set(matriculaResumen, "puntajePrioridad");
        this.update(octavia);
    }

    @Override
    public void updatePrioridad(MatriculaResumen matriculaResumen) {
        Octavia octavia = Octavia.update(MatriculaResumen.class);
        octavia.set(matriculaResumen, "prioridad");
        this.update(octavia);
    }

    @Override
    public List<MatriculaResumen> allNoMatriculadoByCiclo(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(MatriculaResumen.class, "mr")
                .join("alumno alu", "cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .filter("mr.estado", EstadoMatriculaEnum.NMAT)
                .orderBy("mr.prioridad");
        return all(sql);
    }

    @Override
    public void updateTurnoAtencion(CicloAcademico cicloAcademico, TurnoAtencion turnoAtencion) {
        StringBuilder strb = new StringBuilder("update MatriculaResumen mr set mr.turnoAtencion.id=:prm_turno ");
        strb.append(" where mr.prioridad>=:prm_prioridad_ini and mr.prioridad<=:prm_prioridad_fin ");
        strb.append(" and  mr.cicloAcademico.id=:prm_ciclo");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("prm_turno", turnoAtencion.getId());
        query.setParameter("prm_prioridad_ini", BigDecimal.valueOf(turnoAtencion.getPrioridadInicio()));
        query.setParameter("prm_prioridad_fin", BigDecimal.valueOf(turnoAtencion.getPrioridadFin()));
        query.setParameter("prm_ciclo", cicloAcademico.getId());
        query.executeUpdate();
    }

    @Override
    public List<MatriculaResumen> allMatriculaResumenByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query(MatriculaResumen.class, "mr")
                .join("alumno alu", "cicloAcademico ca")
                .filter("alu.id", alumno);
        return sql.all(getCurrentSession());
    }

    @Override
    public MatriculaResumen findMatriculadoByAlumno(CicloAcademico cicloAcademico, Alumno alumno) {
        Octavia sql = Octavia.query(MatriculaResumen.class, "mr")
                .join("cicloAcademico ca", "alumno al")
                .filter("ca.id", cicloAcademico.getId())
                .filter("al.id", alumno.getId())
                .filter("mr.estado", EstadoMatriculaEnum.MAT.name());

        return find(sql);
    }

    @Override
    public List<MatriculaResumen> allByAlumnosCiclo(List<Alumno> alumnos, CicloAcademico ciclo) {
        Octavia sql = Octavia.query(MatriculaResumen.class, "mr")
                .join("alumno alu", "cicloAcademico ca")
                .in("alu.id", alumnos)
                .filter("ca.id", ciclo);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<MatriculaResumen> findNotasIncompletas(List<Alumno> alumnos, CicloAcademico cicloAcademico) {
        Octavia subquery = Octavia.query(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr2")
                .filter("estado", "!=", EstadoMatriculaEnum.MAT)
                .filter("porcentajeAvanceNota", 100);

        Octavia sql = Octavia.query(MatriculaResumen.class, "mr")
                .join("alumno alu", "cicloAcademico ca")
                .exists(subquery)
                .linkedBy("mr.id", "mr2.id")
                .in("alu.id", alumnos)
                .filter("estado", EstadoMatriculaEnum.MAT)
                .filter("ca.id", cicloAcademico);

        return sql.all(getCurrentSession());
    }

    @Override
    public void updateList(List<Long> matriculables) {
      StringBuilder strb = new StringBuilder("update MatriculaResumen mr set mr.prioridad=:prioridad , mr.puntajePrioridad = :puntaje");
        strb.append(" where mr.id in ( :ids )");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("prioridad", null);
        query.setParameter("puntaje", null);
        query.setParameterList("ids", matriculables);
        query.executeUpdate();
    }

}
