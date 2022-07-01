package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import pe.edu.lamolina.amauta.dao.academico.PlanCurricularDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.controller.reporte.dto.plancurricular.PlanEstudiosCursoElectivoDTO;
import pe.edu.lamolina.amauta.controller.reporte.dto.plancurricular.PlanEstudiosCursoRegularDTO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.OrientacionCarrera;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.enums.EstadoEnum;

@Repository
public class PlanCurricularDAOH extends AbstractEasyDAO<PlanCurricular> implements PlanCurricularDAO {

    public PlanCurricularDAOH() {
        super();
        setClazz(PlanCurricular.class);
    }

    @Override
    public PlanCurricular find(long id) {
        Octavia sql = Octavia.query()
                .from(PlanCurricular.class, "pc")
                .join("carrera car", "car.facultad fac", "car.modalidadEstudio me")
                .left("orientacionCarrera ocar", "cicloInicioVigencia cic")
                .filter("pc.id", id);

        return find(sql);
    }

    @Override
    public List<PlanCurricular> allByDynatable(DynatableFilter filter, List<Carrera> carreras) {
        DynatableSql sql = new DynatableSql(filter)
                .from(PlanCurricular.class, "pc")
                .join("carrera car", "car.facultad fac", "car.modalidadEstudio me")
                .left("orientacionCarrera ocar", "cicloInicioVigencia cic")
                .searchFields("car.nombre")
                .orderBy("pc.id desc");

        if (!carreras.isEmpty()) {
            sql.in("car.id", carreras);
        }

        return all(sql);
    }

    @Override
    public void updatePlanCurricular(PlanCurricular planCurricular) {
        StringBuilder sql = new StringBuilder();
        sql.append(" update ").append(PlanCurricular.class.getSimpleName()).append(" as pc ");
        sql.append("    set fechaAprobado = :FECHA_APROBADO,  ");
        sql.append("        orientacionCarrera.id = :ORIENTACION  ");
        sql.append("  where id = :PLAN_CURRICULAR ");
        Query query = getCurrentSession().createQuery(sql.toString());

        query.setParameter("FECHA_APROBADO", planCurricular.getFechaAprobado());
        query.setParameter("PLAN_CURRICULAR", planCurricular.getId());

        if (planCurricular.getOrientacionCarrera() == null) {
            query.setParameter("ORIENTACION", null);
        } else {
            query.setParameter("ORIENTACION", planCurricular.getOrientacionCarrera().getId());
        }

        query.executeUpdate();
    }

    @Override
    public List<PlanCurricular> allActivoByCarrera(Carrera carrera) {
        Octavia sql = Octavia.query()
                .from(PlanCurricular.class, "pc")
                .join("carrera car", "car.facultad fac", "car.modalidadEstudio me")
                .left("orientacionCarrera ocar", "cicloInicioVigencia cic")
                .isNull("ocar.id")
                //                .filter("estado", EstadoEnum.ACT)
                .filter("carrera", carrera);

        return all(sql);
    }

    @Override
    public List<PlanCurricular> allActivoByCarreraOrientacion(Carrera carrera) {
        Octavia sql = Octavia.query()
                .from(PlanCurricular.class, "pc")
                .join("carrera car", "car.facultad fac", "car.modalidadEstudio me")
                .left("orientacionCarrera ocar", "cicloInicioVigencia cic")
                .filter("carrera", carrera);

        return all(sql);
    }

    @Override
    public List<PlanCurricular> allActivoByOrientacion(Carrera carrera, OrientacionCarrera orientacion) {
        Octavia sql = Octavia.query()
                .from(PlanCurricular.class, "pc")
                .join("carrera car", "car.facultad fac", "car.modalidadEstudio me", "orientacionCarrera ocar")
                .left("cicloInicioVigencia cic")
                //                .filter("estado", EstadoEnum.ACT)
                .filter("ocar.id", orientacion)
                .filter("carrera", carrera);

        return all(sql);
    }

    @Override
    public List<PlanCurricular> allActivosByCarrera(Carrera carrera) {
        Octavia sql = Octavia.query()
                .from(PlanCurricular.class, "pc")
                .join("carrera car", "car.facultad fac", "car.modalidadEstudio me")
                .left("cicloInicioVigencia cic", "orientacionCarrera ocar")
                //                .filter("estado", EstadoEnum.ACT)
                .filter("carrera", carrera);
        return all(sql);
    }

    @Override
    public List<PlanCurricular> allActivo() {
        Octavia sql = Octavia.query()
                .from(PlanCurricular.class, "pc")
                .join("carrera car", "car.facultad fac", "car.modalidadEstudio me")
                .left("orientacionCarrera ocar", "cicloInicioVigencia cic");

        return all(sql);
    }

    @Override
    public List<PlanCurricular> findById(PlanCurricular planCurricular) {
        Octavia sql = Octavia.query()
                .from(PlanCurricular.class, "pc")
                .join("carrera car", "car.facultad fac", "car.modalidadEstudio me")
                .left("orientacionCarrera ocar", "cicloInicioVigencia cic")
                .isNull("ocar.id")
                //                .filter("estado", EstadoEnum.ACT)
                .filter("pc.id", planCurricular);

        return all(sql);
    }

    @Override
    public List<PlanCurricular> allCambioActivoByCarrera(Carrera carrera) {
        Octavia sql = Octavia.query()
                .from(PlanCurricular.class, "pc")
                .join("carrera car", "car.facultad fac", "car.modalidadEstudio me")
                .left("orientacionCarrera ocar", "cicloInicioVigencia cic")
                .isNull("ocar.id")
                .filter("estado", EstadoEnum.ACT)
                .filter("carrera", carrera)
                .orderBy("cic.codigo desc");
        return all(sql);
    }

    @Override
    public List<PlanEstudiosCursoRegularDTO> reportePlanCurricularCursoRegular(Long idPlanCurricular) {
        StringBuilder sql = new StringBuilder();
        sql.append("select pc.id idPlanCurricular, cc.id idCurriculaCurso, fac.nombre facultad, car.nombre especialidad, ")
                .append("case cc.numero_ciclo ")
                .append("when '1' then '01' ")
                .append("when '2' then '02' ")
                .append("when '3' then '03' ")
                .append("when '4' then '04' ")
                .append("when '5' then '05' ")
                .append("when '6' then '06' ")
                .append("when '7' then '07' ")
                .append("when '8' then '08' ")
                .append("when '9' then '09' ")
                .append("when '10' then '10' ")
                .append("else '' end nivel, ")
                .append("cu.codigo codigoCurso, cu.nombre nombreCurso, tcc.nombre tipoCurso, cu.horas_teoria horasTeoria, cu.horas_practica horasPractica, ")
                .append("cc.creditos creditos, ")
                .append("group_concat(distinct coalesce(rcc1.cur_cod, '') separator ' ') cursoRequisito, ")
                .append("case ")
                .append("when cc.creditos_requisito = 0  then '' ")
                .append("else cc.creditos_requisito ")
                .append("end creditosRequisito, cc.creditos creditosOtros, cap.year year ")
                .append("from aca_curso_curricula cc ")
                .append("join aca_curso cu on cc.id_curso = cu.id ")
                .append("join aca_tipo_curso_curricula tcc on cc.id_tipo_curso_curricula = tcc.id ")
                .append("join aca_plan_curricular pc on cc.id_plan_curricular = pc.id ")
                .append("join aca_carrera car on pc.id_carrera = car.id ")
                .append("join aca_facultad fac on car.id_facultad = fac.id ")
                .append("join aca_plan_curricular pcu on cc.id_plan_curricular = pcu.id ")
                .append("join aca_ciclo_academico cap on pcu.id_ciclo_inicio_vigencia = cap.id ")
                .append("left join (                                                                                                                      ")
                .append("        select rcc.id id_rcc, rcc.id_curso_curricula id_cc, rcc.id_curso_requisito id_cr, cu2.nombre cur_nom, cu2.codigo cur_cod ")
                .append("        from aca_requisito_curso_curricula rcc                                                                                   ")
                .append("        join aca_curso_curricula cc1 on rcc.id_curso_curricula = cc1.id                                                          ")
                .append("        join aca_curso_curricula cc2 on rcc.id_curso_requisito = cc2.id                                                          ")
                .append("        join aca_curso cu2 on cc2.id_curso = cu2.id                                                                              ")
                .append("        join aca_tipo_curso_curricula tcc on cc2.id_tipo_curso_curricula = tcc.id                                                ")
                .append("        where rcc.estado = 'ACT'                                                                                                 ")
                .append("     ) rcc1 on cc.id = rcc1.id_cc                                                                                                ")
                .append("where pc.id = ").append(idPlanCurricular).append(" ")
                .append("and cc.estado not in ('CAD') ")
                .append("group by cc.id ")
                .append("order by cc.numero_ciclo,cu.nombre");

        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addScalar("idPlanCurricular", LongType.INSTANCE)
                .addScalar("idCurriculaCurso", LongType.INSTANCE)
                .addScalar("facultad", StringType.INSTANCE)
                .addScalar("especialidad", StringType.INSTANCE)
                .addScalar("nivel", StringType.INSTANCE)
                .addScalar("codigoCurso", StringType.INSTANCE)
                .addScalar("nombreCurso", StringType.INSTANCE)
                .addScalar("tipoCurso", StringType.INSTANCE)
                .addScalar("horasTeoria", LongType.INSTANCE)
                .addScalar("horasPractica", LongType.INSTANCE)
                .addScalar("creditos", LongType.INSTANCE)
                .addScalar("cursoRequisito", StringType.INSTANCE)
                .addScalar("creditosRequisito", StringType.INSTANCE)
                .addScalar("creditosOtros", LongType.INSTANCE)
                .addScalar("year", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(PlanEstudiosCursoRegularDTO.class));
        return query.list();

    }

    @Override
    public List<PlanEstudiosCursoElectivoDTO> reportePlanCurricularCursoElectivo(Long idPlanCurricular) {
        StringBuilder sql = new StringBuilder();
        sql.append("select                                                                                                                         ")
                .append("pc.id idPlanCurricular,                                                                                                   ")
                .append("coc.id idCursoOpcionalCurricula,                                                                                          ")
                .append("tcc.nombre tipo, tcc.codigo codigoTipo,                                                                                   ")
                .append("cu.codigo codigoCurso, cu.codigo_anterior1 codigoAnteriorCurso,  cu.nombre nombreCurso, cu.horas_teoria horasTeoria,      ")
                .append("cu.horas_practica horasPractica, cu.creditos creditos, cu.tipo_curso tipoCurso,                                           ")
                .append("coc.creditos creditosCursoOpcionalCurricula, cu3.codigo cursosEquivalente, coc.creditos_requisito creditosRequisito,      ")
                .append("group_concat(coalesce(t1.cod1, t1.cod2) separator ' ') cursosRequisito, t2.codigo cursosPreRequisito, ca.year             ")
                .append("from aca_curso_opcional_curricula coc                                                                                     ")
                .append("join aca_curso cu on coc.id_curso = cu.id                                                                                 ")
                .append("join aca_plan_curricular pc on coc.id_plan_curricular = pc.id                                                             ")
                .append("join aca_tipo_curso_curricula tcc on coc.id_tipo_curso_curricula = tcc.id                                                 ")
                .append("left join(                                                                                                                ")
                .append("    select coc.id, coc1.id id_coc1,cu.codigo cod1, cu1.codigo cod2                                                        ")
                .append("    from aca_requisito_curso_opcional rco                                                                                 ")
                .append("    left join aca_curso_opcional_curricula coc on rco.id_curso_opcional = coc.id                                          ")
                .append("    left join aca_curso_curricula cc on rco.id_curso_requisito_curricula = cc.id                                          ")
                .append("    left join aca_curso cu on cc.id_curso = cu.id                                                                         ")
                .append("    left join aca_tipo_curso_curricula tcc on cc.id_tipo_curso_curricula = tcc.id                                         ")
                .append("    left join aca_curso_opcional_curricula coc1 on rco.id_curso_requisito_opcional = coc1.id                              ")
                .append("    left join aca_curso cu1 on coc1.id_curso = cu1.id                                                                     ")
                .append("    left join aca_tipo_curso_curricula tcc1 on coc1.id_tipo_curso_curricula = tcc1.id                                     ")
                .append("    where rco.estado = 'ACT'                                                                                              ")
                .append(") t1 on coc.id = t1.id                                                                                                    ")
                .append("left join (                                                                                                               ")
                .append("    select rco.id, rco.id_curso_requisito_opcional id_cro, cu.codigo                                                              ")
                .append("    from aca_requisito_curso_opcional rco                                                                                 ")
                .append("    left join aca_curso_opcional_curricula coc on rco.id_curso_opcional = coc.id                                          ")
                .append("    left join aca_curso cu on coc.id_curso = cu.id                                                                        ")
                .append("    where rco.estado = 'ACT'                                                                                              ")
                .append(") t2 on coc.id = t2.id_cro                                                                                                ")
                .append("left join aca_curso_equivalente_electivo cee on coc.id = cee.id_curso_opcional_curricula                                  ")
                .append("left join aca_curso cu3 on cee.id_curso_equivalente = cu3.id                                                              ")
                .append("join aca_ciclo_academico ca on  pc.id_ciclo_inicio_vigencia = ca.id                                                       ")
                .append("where pc.id = ").append(idPlanCurricular).append(" ")
                .append("group by coc.id, cu3.id, t2.id                                                                                            ")
                .append("order by tcc.orden desc, cu.nombre                                                                                        ");
        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addScalar("idPlanCurricular", LongType.INSTANCE)
                .addScalar("idCursoOpcionalCurricula", LongType.INSTANCE)
                .addScalar("tipo", StringType.INSTANCE)
                .addScalar("codigoTipo", StringType.INSTANCE)
                .addScalar("codigoCurso", StringType.INSTANCE)
                .addScalar("codigoAnteriorCurso", StringType.INSTANCE)
                .addScalar("nombreCurso", StringType.INSTANCE)
                .addScalar("horasTeoria", LongType.INSTANCE)
                .addScalar("horasPractica", LongType.INSTANCE)
                .addScalar("creditos", LongType.INSTANCE)
                .addScalar("tipoCurso", StringType.INSTANCE)
                .addScalar("creditosCursoOpcionalCurricula", LongType.INSTANCE)
                .addScalar("cursosEquivalente", StringType.INSTANCE)
                .addScalar("creditosRequisito", LongType.INSTANCE)
                .addScalar("cursosRequisito", StringType.INSTANCE)
                .addScalar("cursosPreRequisito", StringType.INSTANCE)
                .addScalar("year", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(PlanEstudiosCursoElectivoDTO.class));
        return query.list();
    }

}
