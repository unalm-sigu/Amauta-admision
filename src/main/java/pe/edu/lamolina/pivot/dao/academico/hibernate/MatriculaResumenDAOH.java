package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.math.BigDecimal;
import java.util.Arrays;
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
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.TurnoAtencion;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.INH;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.NMAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.PMAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RCI;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.EPG;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.ESP;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.VIS;
import pe.edu.lamolina.model.enums.RolEnum;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_8;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_9;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.controller.academico.alumno.AlumnoResumen;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;

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
                .join("alumno alu", "cicloAcademico ca", "alu.situacionAcademica ")
                .filter("alu.id", alumno)
                .filter("ca.codigo", ciclo.getCodigo());

        return find(sql);
    }

    @Override
    public List<MatriculaResumen> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(MatriculaResumen.class, "mr")
                .join("alumno alu", "cicloAcademico ca", "alu.modalidadEstudio me")
                .left("alu.cicloActivo aluca", "alu.situacionAcademica sa")
                .filter("estado", "!=", INH)
                .filter("ca.id", ciclo);

        return all(sql);
    }

    @Override
    public List<MatriculaResumen> allByCicloFull(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(MatriculaResumen.class, "mr")
                .join("alumno alu", "cicloAcademico ca", "alu.modalidadEstudio me")
                .left("alu.cicloActivo aluca", "alu.situacionAcademica sa")
                .join("alu.persona aluPer", "alu.carrera alucar")
                .join("alucar.facultad fac")
                .leftJoin("aluPer.tipoDocumento td", "alu.cicloIngreso ci")
                .filter("ca.id", ciclo);

        return all(sql);
    }

    @Override
    public MatriculaResumen findByFilter(CicloAcademico ciclo, Alumno alumno, EstadoMatriculaEnum estadoMatriculaCursoEnum) {
        Octavia sql = Octavia.query()
                .from(MatriculaResumen.class, "mr")
                .join("alumno alu", "cicloAcademico ca")
                .left("turnoAtencion");

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
        sql.append(" inner join al.modalidadEstudio moe ");
        sql.append(" inner join car.facultad fac ");
        sql.append(" where ");
        sql.append(" ca.codigo=:prm_ciclo ");

        Query query = getCurrentSession().createQuery(sql.toString());
        //  query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        query.setParameter("prm_ciclo", ciclo.getCodigo());
        query.setString("PRE", PRE.name());
        query.setString("EPG", EPG.name());
        query.setString("VIS", VIS.name());
        query.setString("ESP", ESP.name());
        return (AlumnoResumen) query.uniqueResult();
    }

    @Override
    public List<MatriculaResumen> allByCicloRolDynatable(DynatableFilter filter, List<CicloAcademico> ciclos, String codigo, List<Long> filtros) {

        DynatableSql sql = new DynatableSql(filter);
        switch (RolEnum.valueOf(codigo)) {
            case MOD:
            case FAC:
            case ESP:
                sql.from(MatriculaResumen.class, "mr")
                        .join("alumno al", "cicloAcademico ca", "al.persona per", "per.tipoDocumento tdoc", "al.carrera car", "al.situacionAcademica sita")
                        .join("ca.modalidadEstudio moe", "car.facultad fac")
                        .leftJoin("al.cicloIngreso ci", "al.cicloActivo cia", "turnoAtencion ta", "cicloAcademicoInfo")
                        .in("ca.codigo", ciclos)
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
                        .leftJoin("al.cicloIngreso ci", "al.cicloActivo cia", "turnoAtencion ta", "cicloAcademicoInfo")
                        .in("ca.codigo", ciclos)
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
        query.setParameter("prm_prioridad_fin", turnoAtencion.getPrioridadFin());
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
        StringBuilder strb = new StringBuilder("update MatriculaResumen mr set mr.prioridad = :nuleable , mr.puntajePrioridad = :nuleable , ");
        strb.append(" mr.creditosCursadosCiclo = :nuleable , mr.creditosAcumulados = :nuleable , mr.creditosAprobadosCiclo = :nuleable , mr.creditosAprobadosAcumulados = :nuleable , ");
        strb.append(" mr.cicloAcademicoInfo = :nuleable  ");
        strb.append(" where mr.id in ( :ids )");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("nuleable", null);
        query.setParameterList("ids", matriculables);
        query.executeUpdate();
    }

    @Override
    public void deleteMatriculable(CicloAcademico cicloAcademico) {
        StringBuilder strb = new StringBuilder("delete from MatriculaResumen mr ");
        strb.append(" where mr.cicloAcademico.id = :ciclo");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("ciclo", cicloAcademico.getId());
        query.executeUpdate();
    }

    @Override
    public void saveMatriculables(List<Long> alumnos, CicloAcademico academico) {
        StringBuilder strb = new StringBuilder("");
        strb.append("insert into ");
        strb.append("MatriculaResumen ");
        strb.append("(");
        strb.append("alumno,");
        strb.append("cicloAcademico,");
        strb.append("situacionInicio,");
        strb.append("creditosRetirados,");
        strb.append("creditosMatriculados,");
        strb.append("cursosMatriculados,");
        strb.append("cursosRetirados,");
        strb.append("porcentajeAvance,");
        strb.append("notaAcumulada,");
        strb.append("notaAvance,");
        strb.append("notaFinal,");
        strb.append("estado, ");
        strb.append("esUltimoCiclo, ");
        strb.append("creditosTrikaPagados ");
        strb.append(")");
        strb.append("select ");
        strb.append("alum,");
        strb.append("cic, ");
        strb.append("sit, ");
        strb.append("0, ");
        strb.append("0, ");
        strb.append("0, ");
        strb.append("0, ");
        strb.append("0, ");
        strb.append("'0', ");
        strb.append("'0', ");
        strb.append("'0', ");
        strb.append("'NMAT', ");
        strb.append("CASE WHEN alum.creditosAprobados  >= ").append(Constantine.CAPA_ULTIMO_CICLO);
        strb.append(" THEN ").append(true).append(" ELSE ").append(false).append(" END, ");
        strb.append("0 ");
        strb.append("from Alumno as alum ");
        strb.append("inner join alum.modalidadEstudio me ");
        strb.append("inner join alum.situacionAcademica sit, ");
        strb.append("CicloAcademico cic ");
        strb.append("where alum.id in (:alumnos) and cic.id = :ciclo ");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("ciclo", academico.getId());
        query.setParameterList("alumnos", alumnos);
        query.executeUpdate();
    }

    @Override
    public MatriculaResumen findByAntPrioridad(MatriculaResumen matri, CicloAcademico cicloAcademico, Boolean esUltimoCiclo) {
        Octavia sql = new Octavia()
                .from(MatriculaResumen.class, "mr")
                .join("mr.cicloAcademico aca", "alumno alum")
                .join("alum.situacionAcademica sit", "alum.modalidadEstudio mod");
        if (esUltimoCiclo) {
            sql.filter("alum.creditosAprobados", ">", Constantine.CAPA_ULTIMO_CICLO);
        } else {
            sql.filter("alum.creditosAprobados", "<", Constantine.CAPA_ULTIMO_CICLO);
        }
        sql.filter("aca.id", cicloAcademico)
                .notIn("sit.id", Arrays.asList(S_8, S_9))
                .filter("mr.puntajePrioridad", "<=", matri.getPuntajePrioridad())
                .orderBy("mr.puntajePrioridad desc")
                .limit(1);

        return find(sql);
    }

    @Override
    public MatriculaResumen findByDesPrioridad(MatriculaResumen matri, CicloAcademico cicloAcademico, Boolean esUltimoCiclo) {
        Octavia sql = new Octavia()
                .from(MatriculaResumen.class, "mr")
                .join("mr.cicloAcademico aca", "alumno alum")
                .join("alum.situacionAcademica sit", "alum.modalidadEstudio mod");
        if (esUltimoCiclo) {
            sql.filter("alum.creditosAprobados", ">", Constantine.CAPA_ULTIMO_CICLO);
        } else {
            sql.filter("alum.creditosAprobados", "<", Constantine.CAPA_ULTIMO_CICLO);
        }
        sql.filter("aca.id", cicloAcademico)
                .notIn("sit.id", Arrays.asList(S_8, S_9))
                .filter("mr.puntajePrioridad", ">=", matri.getPuntajePrioridad())
                .orderBy("mr.puntajePrioridad asc")
                .limit(1);

        return find(sql);
    }

    @Override
    public void savePosGradoVerano(List<String> situaciones, CicloAcademico cicloAcademicoAnterior, CicloAcademico academico) {
        StringBuilder strb = new StringBuilder("");
        strb.append("insert into ");
        strb.append("MatriculaResumen ");
        strb.append("(");
        strb.append("alumno,");
        strb.append("cicloAcademico,");
        strb.append("situacionInicio,");
        strb.append("creditosRetirados,");
        strb.append("creditosMatriculados,");
        strb.append("cursosMatriculados,");
        strb.append("cursosRetirados,");
        strb.append("porcentajeAvance,");
        strb.append("notaAcumulada,");
        strb.append("notaAvance,");
        strb.append("notaFinal,");
        strb.append("estado, ");
        strb.append("creditosTrikaPagados ");
        strb.append(")");
        strb.append("select ");
        strb.append("alum,");
        strb.append(":cicloActual, ");
        strb.append("sit, ");
        strb.append("0, ");
        strb.append("0, ");
        strb.append("0, ");
        strb.append("0, ");
        strb.append("0, ");
        strb.append("'0', ");
        strb.append("'0', ");
        strb.append("'0', ");
        strb.append("'NMAT', ");
        strb.append("0 ");
        strb.append("from MatriculaResumen mat ");
        strb.append("inner join mat.alumno alum ");
        strb.append("inner join alum.cicloActivoRegular car ");
        strb.append("inner join alum.modalidadEstudio me ");
        strb.append("inner join alum.situacionAcademica sit ");
        strb.append("inner join mat.cicloAcademico cic ");
        strb.append("where sit.codigo not in ( :codigos ) and me.codigo = 'EPG'");
        strb.append("and cic.id = car.id ");
        strb.append("and car.codigo >= :ciclo ");
        strb.append("and not exists (select e.id from Egresado e where e.alumno = alum)");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("cicloActual", academico);
        query.setParameter("ciclo", cicloAcademicoAnterior.getCodigo());
        query.setParameterList("codigos", situaciones);
        query.executeUpdate();
    }

    @Override
    public void savePreGradoVerano(List<String> situacionesPregrado, CicloAcademico cicloAcademicoAnterior, CicloAcademico academico) {
        StringBuilder strb = new StringBuilder("");
        strb.append("insert into ");
        strb.append("MatriculaResumen ");
        strb.append("(");
        strb.append("alumno,");
        strb.append("creditosTrikaPagados,");
        strb.append("cicloAcademico,");
        strb.append("situacionInicio,");
        strb.append("creditosRetirados,");
        strb.append("creditosMatriculados,");
        strb.append("cursosMatriculados,");
        strb.append("cursosRetirados,");
        strb.append("porcentajeAvance,");
        strb.append("notaAcumulada,");
        strb.append("notaAvance,");
        strb.append("notaFinal,");
        strb.append("estado");
        strb.append(")");
        strb.append("select ");
        strb.append("alum,");
        strb.append("0,");
        strb.append(":cicloActual, ");
        strb.append("sit, ");
        strb.append("0, ");
        strb.append("0, ");
        strb.append("0, ");
        strb.append("0, ");
        strb.append("0, ");
        strb.append("'0', ");
        strb.append("'0', ");
        strb.append("'0', ");
        strb.append("'NMAT' ");
        strb.append("from MatriculaResumen mat ");
        strb.append("inner join mat.alumno alum ");
        strb.append("inner join alum.cicloActivoRegular car ");
        strb.append("inner join alum.modalidadEstudio me ");
        strb.append("inner join alum.situacionAcademica sit ");
        strb.append("inner join mat.cicloAcademico cic ");
        strb.append("where sit.codigo not in  ( :codigos ) and me.codigo = 'PRE'");
        strb.append("and cic.id = car.id ");
        strb.append("and car.codigo >= :ciclo ");
        strb.append("and not exists (select e.id from Egresado e where e.alumno = alum)");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("cicloActual", academico);
        query.setParameter("ciclo", cicloAcademicoAnterior.getCodigo());
        query.setParameterList("codigos", situacionesPregrado);
        query.executeUpdate();
    }

    @Override
    public Long countMatriculablesByConsejero(Persona persona, CicloAcademico cicloAcademico) {
        Octavia sqlSub = new Octavia()
                .from(AlumnoConsejero.class, "ac")
                .join("alumno al1", "consejero con")
                .join("con.colaborador col", "col.persona per")
                .join("cicloAcademico ca1")
                .filter("ca1.id", cicloAcademico)
                .filter("per.id", persona);

        Octavia sql = new Octavia()
                .from(MatriculaResumen.class, "mr")
                .join("alumno al", "al.carrera car")
                .join("cicloAcademico ca")
                .exists(sqlSub)
                .linkedBy("al.id", "al1.id")
                .filter("ca.id", cicloAcademico)
                .filter("estado", MAT);

        return Long.parseLong(sql.all(getCurrentSession()).size() + "");
    }

    @Override
    public Long countNoMatriculablesByConsejero(Persona persona, CicloAcademico cicloAcademico) {
        Octavia sqlSub = new Octavia()
                .from(AlumnoConsejero.class, "ac")
                .join("alumno al1", "consejero con")
                .join("con.colaborador col", "col.persona per")
                .join("cicloAcademico ca1")
                .filter("ca1.id", cicloAcademico)
                .filter("per.id", persona);

        Octavia sql = new Octavia()
                .from(MatriculaResumen.class, "mr")
                .join("alumno al", "al.carrera car")
                .join("cicloAcademico ca")
                .exists(sqlSub)
                .linkedBy("al.id", "al1.id")
                .filter("ca.id", cicloAcademico)
                .filter("estado", NMAT);

        return Long.parseLong(sql.all(getCurrentSession()).size() + "");
    }

    @Override
    public Long countRetiroCicloByConsejero(Persona persona, CicloAcademico cicloAcademico) {
        Octavia sqlSub = new Octavia()
                .from(AlumnoConsejero.class, "ac")
                .join("alumno al1", "consejero con")
                .join("con.colaborador col", "col.persona per")
                .join("cicloAcademico ca1")
                .filter("ca1.id", cicloAcademico)
                .filter("per.id", persona);

        Octavia sql = new Octavia()
                .from(MatriculaResumen.class, "mr")
                .join("alumno al", "al.carrera car")
                .join("cicloAcademico ca")
                .exists(sqlSub)
                .linkedBy("al.id", "al1.id")
                .filter("ca.id", cicloAcademico)
                .filter("estado", RCI);

        return Long.parseLong(sql.all(getCurrentSession()).size() + "");
    }

    @Override
    public List<MatriculaResumen> allByCicloMATAndNMAT(CicloAcademico cicloBD) {

        Octavia sql = Octavia.query()
                .from(MatriculaResumen.class, "mr")
                .join("alumno alu", "cicloAcademico ca", "alu.modalidadEstudio me")
                .left("alu.cicloActivo aluca", "alu.situacionAcademica sa")
                .filter("ca.id", cicloBD)
                .in("estado", Arrays.asList(MAT, NMAT));

        return all(sql);
    }

    @Override
    public List<MatriculaResumen> allByCicloMat(CicloAcademico ciclo) {

        Octavia subSql = new Octavia()
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr1")
                .filter("estado", PMAT);

        Octavia sql = Octavia.query()
                .from(MatriculaResumen.class, "mr")
                .join("alumno alu", "alu.persona per", "cicloAcademico ca")
                .leftJoin("turnoAtencion ta")
                .exists(subSql)
                .linkedBy("mr.id", "mr1.id")
                .filter("ca.id", ciclo);
        return all(sql);
    }

    @Override
    public void updateCreditos(MatriculaResumen matri) {
        Octavia sql = Octavia.update(MatriculaResumen.class);
        sql.set(matri, "estado");
        sql.set(matri, "cursosMatriculados");
        sql.set(matri, "creditosMatriculados");
        this.update(sql);
    }

    @Override
    public void updateColumns(MatriculaResumen matriculaResumenUpd, String... columns) {
        Octavia sql = Octavia.update(MatriculaResumen.class);
        for (String column : columns) {
            sql.set(matriculaResumenUpd, column);
        }
        this.update(sql);
    }

    @Override
    public void updateBeneficiado(MatriculaResumen matriculaResumen) {
        Octavia octavia = Octavia.update(MatriculaResumen.class);
        octavia.set(matriculaResumen, "esBeneficiadoUltimoCiclo");
        octavia.set(matriculaResumen, "fechaBeneficiadoUtlCiclo");
        this.update(octavia);
    }

    @Override
    public List<MatriculaResumen> allSinConsejeria(Carrera carrera, CicloAcademico cicloAcademico) {
        Octavia subquery = new Octavia()
                .from(AlumnoConsejero.class, "ac")
                .join("alumno al1", "consejero con")
                .join("cicloAcademico ca1")
                .filter("ca1.id", cicloAcademico);

        Octavia sql = new Octavia()
                .from(MatriculaResumen.class, "mr")
                .join("alumno al", "al.carrera car")
                .join("cicloAcademico ca")
                .notExists(subquery)
                .linkedBy("al.id", "al1.id")
                .filter("ca.id", cicloAcademico)
                .filter("car.id", carrera);

        return all(sql);
    }

}
