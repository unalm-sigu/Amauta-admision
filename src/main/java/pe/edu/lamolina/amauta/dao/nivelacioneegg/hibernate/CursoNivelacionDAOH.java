package pe.edu.lamolina.amauta.dao.nivelacioneegg.hibernate;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.CursoNivelacionDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.horario.GrupoHorasNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;

@Repository
public class CursoNivelacionDAOH extends AbstractEasyDAO<CursoNivelacion> implements CursoNivelacionDAO {

    public CursoNivelacionDAOH() {
        super();
        setClazz(CursoNivelacion.class);
    }

    @Override
    public CursoNivelacion find(long id) {
        Octavia sql = Octavia.query()
                .from(CursoNivelacion.class, "cn")
                .join("docente doc", "cursoCiclo cuci")
                .join("cuci.curso cu", "cuci.cicloAcademico ci")
                .leftJoin("aula", "doc.persona per")
                .filter("cn.id", id);

        return find(sql);
    }

    @Override
    public List<CursoNivelacion> allByDynatable(DynatableFilter filter, CicloAcademico ciclo) {
        DynatableSql sql = new DynatableSql(filter)
                .from(CursoNivelacion.class, "cn")
                .join("docente doc", "cursoCiclo cuci")
                .join("cuci.curso cu", "cuci.cicloAcademico ci")
                .leftJoin("aula", "doc.persona per")
                .filter("ci.id", ciclo)
//                .notIn("cn.estado", Arrays.asList(SeccionEstadoEnum.CAN.name()))
                .searchFields("doc.codigo", "cn.codigo", "per.numeroDocIdentidad", "cu.codigo", "cu.nombre")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("cn.id DESC");

        return all(sql);
    }

    @Override
    public List<CursoNivelacion> allDocenteByDynatable(DynatableFilter filter, CicloAcademico ciclo, Docente docente) {
        DynatableSql sql = new DynatableSql(filter)
                .from(CursoNivelacion.class, "cn")
                .join("docente doc", "cursoCiclo cuci")
                .join("cuci.curso cu", "cuci.cicloAcademico ci")
                .leftJoin("aula au", "doc.persona per")
                .filter("ci.id", ciclo)
                .filter("doc.id", docente)
                .searchFields("cu.codigo", "cu.nombre", "cn.codigo", "au.codigo")
                .orderBy("cu.nombre");

        return all(sql);
    }

    @Override
    public List<CursoNivelacion> allByCursoCiclo(CursoCicloAcademico cursoCiclo, GrupoHorasNivelacion grupoHoras) {
        Octavia sql = Octavia.query()
                .from(CursoNivelacion.class, "cn")
                .join("docente doc", "cursoCiclo cuci", "grupoHoras gh")
                .join("cuci.curso cu", "cuci.cicloAcademico ci")
                .leftJoin("aula", "doc.persona per")
                .filter("gh.id", grupoHoras)
                .filter("cuci.id", cursoCiclo);

        return all(sql);
    }

    @Override
    public List<CursoNivelacion> allByDocenteCiclo(Docente docente, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(CursoNivelacion.class, "cn")
                .join("docente doc", "cursoCiclo cuci", "grupoHoras gh")
                .join("cuci.curso cu", "cuci.cicloAcademico ci")
                .leftJoin("aula", "doc.persona per")
                .filter("doc.id", docente)
                .filter("ci.id", ciclo);

        return all(sql);
    }

    @Override
    public List<CursoNivelacion> allActivosByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(CursoNivelacion.class, "cn")
                .join("docente doc", "cursoCiclo cuci", "grupoHoras gh")
                .join("cuci.curso cu", "cuci.cicloAcademico ci")
                .leftJoin("aula", "doc.persona per")
                .filter("cn.estado", SeccionEstadoEnum.ACT)
                .filter("cn.estadoNotas", EstadoGrupoSeccionEnum.ABI)
                .filter("ci.id", ciclo);

        return all(sql);
    }

    @Override
    public CursoNivelacion findLastByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(CursoNivelacion.class, "cn")
                .join("docente doc", "cursoCiclo cuci", "grupoHoras gh")
                .join("cuci.curso cu", "cuci.cicloAcademico ci")
                .leftJoin("aula", "doc.persona per")
                .filter("ci.id", ciclo)
                .orderBy("cn.codigo DESC")
                .limit(1);

        return find(sql);
    }

}
