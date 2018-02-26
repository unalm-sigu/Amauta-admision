package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import java.util.Map;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.EPG;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.ESP;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.VIS;
import pe.edu.lamolina.model.enums.RolEnum;

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
                .join("alumno alu", "cicloAcademico ca")
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
                        .searchFields("car.nombre", "fac.nombre", "al.estado", "al.codigo", "mr.prioridad", "mr.puntajePrioridad")
                        .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                        .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                        .in("ca.id", filtros)
                        .orderBy("mr.id desc");
                break;
            case TODO:
            default:
                sql.from(MatriculaResumen.class, "mr")
                        .join("alumno al", "cicloAcademico ca", "al.persona per", "per.tipoDocumento tdoc", "al.carrera car", "al.situacionAcademica sita")
                        .join("ca.modalidadEstudio moe", "car.facultad fac")
                        .leftJoin("al.cicloIngreso ci", "al.cicloActivo cia", "turnoAtencion ta")
                        .filter("ca.id", ciclo)
                        .searchFields("car.nombre", "fac.nombre", "al.estado", "al.codigo", "mr.prioridad", "mr.puntajePrioridad")
                        .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                        .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                        .orderBy("mr.id desc");
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

}
