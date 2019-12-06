package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCachimbos;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.academico.Seccion;
import static pe.edu.lamolina.model.enums.EstadoCursoCachimboEnum.ACT;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.EstadoPlanCalificaEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;

@Repository
public class CursoDAOH extends AbstractEasyDAO<Curso> implements CursoDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public CursoDAOH() {
        super();
        setClazz(Curso.class);
    }

    @Override
    public Curso find(long idCurso) {
        Octavia sql = Octavia.query()
                .from(Curso.class, "cu")
                .leftJoin("modalidadEstudio", "carrera", "coordinador")
                .leftJoin("planCalificacion pc", "departamentoAcademico da", "da.facultad")
                .filter("cu.id", idCurso);

        return find(sql);
    }

    @Override
    public List<Curso> all() {
        Octavia sql = Octavia.query()
                .from(Curso.class, "cu")
                .leftJoin("modalidadEstudio", "carrera", "coordinador")
                .leftJoin("planCalificacion pc", "departamentoAcademico da", "da.facultad");

        return all(sql);
    }

    @Override
    public List<Curso> allForSistemaCalificacion(String nombre, DepartamentoAcademico departamento, PlanCalificacion planCalificacion, CicloAcademico ciclo) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        StringBuilder sql = new StringBuilder();
        sql.append("  from ").append(Curso.class.getName()).append(" as cur ");
        sql.append(" left join fetch cur.departamentoAcademico da ");
        sql.append(" left join fetch cur.planCalificacion pc ");
        sql.append(" where 1=1 ");

        if (planCalificacion.isTipoCicloNivelacion()) {
            sql.append("  and   ( cur.planCalificacion.id != :PLAN_CAL or cur.planCalificacion is null) ");
        } else if (planCalificacion.isTipoCicloRegular()) {
            sql.append("  and   ( cur.planCalificacion.id != :PLAN_CAL or cur.planCalificacion is null) ");
        } else {
            throw new PhobosException("El plan calificacion no tiene tipo de ciclo");
        }

        sql.append("  and    cur.id in ( select cu.id ");
        sql.append("                       from ").append(GrupoSeccion.class.getSimpleName()).append(" as gs ");
        sql.append("                      inner join gs.curso cu ");
        sql.append("                      inner join gs.cicloAcademico ca ");
        sql.append("                      where ca.id = :CICLO ) ");
        sql.append("  and    da.id = :DEP_ACA ");
        sql.append("  and    cur.nombre like :NOMBRE ");
        sql.append(" order by cur.nombre ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("PLAN_CAL", planCalificacion.getId());
        query.setParameter("DEP_ACA", departamento.getId());
        query.setParameter("CICLO", ciclo.getId());
        query.setString("NOMBRE", nombre);
        query.setMaxResults(15);

        return query.list();
    }

    @Override
    public List<Curso> allByPlan(PlanCalificacion plan) {
        Octavia sql = Octavia.query()
                .from(Curso.class, "cu")
                .join("planCalificacion pc")
                .filter("pc.id", plan);

        return all(sql);
    }

    @Override
    public List<Curso> allByPlanes(List<PlanCalificacion> planes) {
        Octavia sql = Octavia.query()
                .from(Curso.class, "cu")
                .join("planCalificacion pc")
                .in("pc.id", planes);

        return all(sql);
    }

    @Override
    public List<Curso> allByPlanRegular(PlanCalificacion plan) {
        Octavia sql = Octavia.query()
                .from(Curso.class, "cu")
                .join("planCalificacionRegular pc")
                .filter("pc.id", plan);

        return all(sql);
    }

    @Override
    public List<Curso> allRegularesByPlanes(List<PlanCalificacion> planes) {
        Octavia sql = Octavia.query()
                .from(Curso.class, "cu")
                .join("planCalificacionRegular pc")
                .in("pc.id", planes);

        return all(sql);
    }

    @Override
    public List<Curso> allActiveByPlan(PlanCalificacion plan) {
        Octavia sql = Octavia.query()
                .from(Curso.class, "cu")
                .leftJoin("planCalificacion pc", "planCalificacionRegular pcr")
                .filter("pc.estado", EstadoPlanCalificaEnum.ACT);

        if (plan.isTipoCicloNivelacion()) {
            sql.filter("pc.id", plan);
        } else if (plan.isTipoCicloRegular()) {
            sql.filter("pcr.id", plan);
        } else {
            throw new PhobosException("PLan calificacion de tipo de ciclo");
        }

        return all(sql);
    }

    @Override
    public Curso findByCode(String codigo) {
        Octavia sql = Octavia.query()
                .from(Curso.class, "cu")
                .leftJoin("planCalificacion pc", "departamentoAcademico da", "da.facultad")
                .filter("cu.codigo", codigo);

        return find(sql);
    }

    @Override
    public List<Curso> allByDynatable(DynatableFilter filter, List<ModalidadEstudio> modalidades, List<Carrera> carreras, List<DepartamentoAcademico> departamentos) {
        if (modalidades == null && carreras.isEmpty() && departamentos.isEmpty()) {
            filter.setFiltered(0);
            filter.setTotal(0);
            return new ArrayList();
        }

        DynatableSql sql = new DynatableSql(filter)
                .from(Curso.class, "cu")
                .join("departamentoAcademico da", "da.facultad fa")
                .leftJoin("planCalificacion pc", "carrera ca", "coordinador co", "co.persona per")
                .leftJoin("modalidadEstudio me")
                .searchFields("cu.nombre", "cu.codigo", "cu.codigoAnterior1", "fa.nombre", "da.nombre", "cu.estado", "ca.nombre")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("cu.id desc");

        if (modalidades == null || modalidades.isEmpty()) {
            sql.__().beginBlock()
                    .__().in("me.codigo", Arrays.asList(ModalidadEstudioEnum.EPG, ModalidadEstudioEnum.PRE))
                    .__().isNull("me.id")
                    .endBlock();
        } else {
            sql.__().beginBlock()
                    .__().in("me.id", modalidades)
                    .__().isNull("me.id")
                    .endBlock();
        }

        if (!departamentos.isEmpty()) {
            sql.in("da.id", departamentos);
        }
        if (!carreras.isEmpty()) {
            sql.__().beginBlock()
                    .__().in("ca.id", carreras)
                    .__().isNull("ca.id")
                    .endBlock();
        }

        return all(sql);
    }

    @Override
    public List<Curso> allByNombreTipoCurricula(String nombre, List<String> tiposCurriculaEnum, Integer limit) {
        Octavia sql = Octavia.query()
                .from(Curso.class, "cur")
                .left("departamentoAcademico da", "da.facultad", "carrera ca");
        if (StringUtils.isNotBlank(nombre)) {
            sql.beginBlock()
                    .__().like("cur.nombre", nombre)
                    .__().like("cur.codigo", nombre)
                    .endBlock();
        }
        sql.in("cur.tipoCurricula", tiposCurriculaEnum)
                .filter("estado", ACT)
                .orderBy("cur.nombre")
                .limit(limit);

        return all(sql);
    }

    @Override
    public List<Curso> allByCodigo(String codigo) {
        Octavia sql = Octavia.query()
                .from(Curso.class, "cur")
                .like("cur.codigo", codigo)
                .orderBy("cur.nombre");

        return all(sql);
    }

    @Override
    public List<Curso> allByCodigosAntiguos(List<String> codigosAntiguos) {
        Octavia sql = Octavia.query()
                .from(Curso.class, "cur")
                .in("cur.codigoAnterior1", codigosAntiguos);

        return all(sql);
    }

    @Override
    public Curso findLastByCodigoFacultad(String codigo) {
        Octavia sql = Octavia.query()
                .from(Curso.class, "cu")
                .join("departamentoAcademico da", "da.facultad fa")
                .filter("cu.codigo", "like", codigo)
                .orderBy("cu.codigo DESC")
                .limit(1);

        return find(sql);
    }

    @Override
    public List<Curso> allForProgramacion(String nombre) {
        Octavia sql = Octavia.query()
                .from(Curso.class, "cur")
                .join("departamentoAcademico da", "modalidadEstudio")
                .leftJoin("carrera", "da.facultad")
                .beginBlock()
                .__().like("cur.nombre", nombre)
                .__().like("cur.codigo", nombre)
                .endBlock()
                .orderBy("cur.nombre")
                .limit(15);

        return all(sql);
    }

    @Override
    public List<Curso> allCursoByName(String nombre) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Curso.class, "cur")
                .join("departamentoAcademico dep", "dep.facultad")
                .leftJoin("carrera car", "car.facultad fa", "planCalificacion  pc", "planCalificacionRegular pcr", "coordinador cor")
                .filter("cur.estado", ACT)
                .beginBlock()
                .__().filter("cur.nombre", "like", nombre)
                .__().filter("cur.codigo", "like", nombre)
                .__().filter("cur.codigoAnterior1", "like", nombre)
                .endBlock()
                .limit(15);
        return all(sql);
    }

    @Override
    public List<Curso> allCursoByNameCiclo(String nombre, CicloAcademico ciclo) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .selectDistinct("cur")
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ci")
                .join("cur.departamentoAcademico dep", "dep.facultad")
                .leftJoin("cur.carrera car")
                .filter("ci.id", ciclo)
                .filter("estado", ACT)
                .beginBlock()
                .__().filter("cur.nombre", "like", nombre)
                .__().filter("cur.codigo", "like", nombre)
                .__().filter("cur.codigoAnterior1", "like", nombre)
                .endBlock()
                .limit(15);
        return all(sql);
    }

    @Override
    public List<Curso> allCursoCachimbosByCicloAcademico(CicloAcademico cicloAcademico, Carrera carrera) {
        Octavia sql = Octavia.query()
                .select("cur")
                .from(CursoCachimbos.class, "cc")
                .join("curso cur", "carrera car", "car.facultad fac", "cicloAcademico ciclo")
                .filter("car.id", carrera)
                .filter("ciclo.id", cicloAcademico);

        return all(sql);
    }

    @Override
    public List<Curso> allCursoByNameExceptList(String nombre, List<Curso> cursos) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Curso.class, "cur")
                .join("departamentoAcademico dep")
                .leftJoin("carrera car", "car.facultad fa", "planCalificacion  pc", "planCalificacionRegular pcr", "coordinador cor", "modalidadEstudio me")
                .notIn("cur.id", cursos)
                .filter("cur.estado", ACT)
                .beginBlock()
                .__().filter("cur.nombre", "like", nombre)
                .__().filter("cur.codigo", "like", nombre)
                .__().filter("cur.codigoAnterior1", "like", nombre)
                .endBlock()
                .limit(15);
        return all(sql);
    }

    @Override
    public List<Curso> allByDptoEstado(Long idDpto, String estado) {
        Octavia sql = Octavia.query()
                .from(Curso.class, "cu")
                .join("departamentoAcademico da")
                .filter("da.id", idDpto)
                .filter("cu.estado", estado);
        return all(sql);
    }

    @Override
    public List<Curso> allActiveByCodigo(String codigo, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .selectDistinct("cu")
                .from(GrupoSeccion.class, "gs")
                .join("curso cu")
                .join("cicloAcademico ci")
                .filter("gs.estado", ACT.name())
                .like("cu.codigo", codigo)
                .limit(15);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Curso> allByModalidadEstudioNombre(ModalidadEstudioEnum moda, String nombre) {

        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Curso.class, "cur")
                .join("modalidadEstudio mo", "departamentoAcademico da")
                .join("da.facultad")
                .filter("mo.codigo", moda)
                .beginBlock()
                .__().filter("cur.nombre", "like", nombre)
                .__().filter("cur.codigo", "like", nombre)
                .__().filter("cur.codigoAnterior1", "like", nombre)
                .endBlock()
                .limit(15);
        return all(sql);

    }

    @Override
    public List<Curso> allNoEncuestar() {
        Octavia sql = Octavia.query(Curso.class, "cu")
                .filter("cu.noEncuestar", 1);
        return all(sql);
    }

    @Override
    public List<Curso> searchLikeNombre(String nombre, Integer limit) {
        Octavia sql = Octavia.query();
        sql.from(Curso.class, "cur");
        sql.beginBlock()
                .__().complexFilter("concat(coalesce(cur.codigo,''),' ',coalesce(cur.nombre,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(cur.nombre,''),' ',coalesce(cur.codigo,''))", "like", nombre);
        sql.endBlock();
        sql.limit(limit);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Curso> allForExamenByCiclo(String nombre, RolExamenes rolExamenes, CicloAcademico cicloAcademico) {
        Octavia subQuery = Octavia.query()
                .from(CursoMasivoExamen.class, "cme")
                .join("rolExamenes re", "curso cur")
                .filter("re.id", rolExamenes);

        Octavia sql = Octavia.query()
                .selectDistinct("cu")
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.cicloAcademico ca", "mc.curso cu")
                .join("cu.departamentoAcademico", "cu.modalidadEstudio mes")
                .filter("mc.estado", EstadoMatriculaEnum.MAT)
                .filter("ca.id", cicloAcademico)
                .filter("mes.codigo", ModalidadEstudioEnum.PRE)
                .beginBlock()
                .__().filter("cu.codigo", "like", nombre)
                .__().filter("cu.nombre", "like", nombre)
                .endBlock()
                .notExists(subQuery)
                .linkedBy("cu.id", "cur.id")
                .orderBy("cu.nombre")
                .limit(15);
        return all(sql);

    }

    @Override
    public List<Curso> searchLikeNombreNotIn(String parametro, List<Curso> cursos) {
        Octavia sql = Octavia.query()
                .from(Curso.class, "cur")
                .filter("cur.estado", EstadoEnum.ACT)
                .notIn("cur.id", cursos)
                .beginBlock()
                .__().filter("cur.nombre", "like", parametro)
                .__().filter("cur.codigo", "like", parametro)
                .endBlock()
                .limit(6);
        return all(sql);
    }

    @Override
    public Curso findCurso(Curso curso) {
        Octavia sql = Octavia.query()
                .from(Curso.class, "cu")
                .filter("cu.id", curso);
        return find(sql);
    }

    @Override
    public List<Curso> allProgramadosByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .selectDistinct("cur")
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.cicloAcademico ci", "gs.curso cur")
                .filter("ci.id", ciclo)
                .filter("gs.estado", ACT)
                .filter("sec.estado", ACT);

        return all(sql);
    }

}
