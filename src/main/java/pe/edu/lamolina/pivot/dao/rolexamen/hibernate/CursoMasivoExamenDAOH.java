package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.enums.EstadoCursoMasivoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import static pe.edu.lamolina.model.enums.EstadoEnum.ACT;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.pivot.dao.rolexamen.CursoMasivoExamenDAO;

@Repository
public class CursoMasivoExamenDAOH extends AbstractEasyDAO<CursoMasivoExamen> implements CursoMasivoExamenDAO {

    public CursoMasivoExamenDAOH() {
        super();
        setClazz(CursoMasivoExamen.class);
    }

    @Override
    public List<CursoMasivoExamen> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(CursoMasivoExamen.class, "cme")
                .join("rolExamenes re", "curso cur") //, "dia di", "hora hr"
                .join("re.eventoCicloAcademico eca", "eca.cicloAcademico ca")
                .filter("cur.estado", EstadoEnum.ACT)
                .filter("ca.id", cicloAcademico)
                .searchFields("ca.descripcion", "cur.nombre")
                .orderBy("cme.id desc");
        return all(sql);
    }

    @Override
    public List<Curso> allCursosByCicloActivo(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .selectDistinct("cu")
                .from(GrupoSeccion.class, "gs")
                .join("curso cu")
                .join("cicloAcademico ci")
                .left("cu.carrera", "cu.modalidadEstudio")
                .filter("gs.estado", ACT.name());
        return sql.all(getCurrentSession());
    }

    @Override
    public List<RolExamenes> allRolExamenesByCicloActivo(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(RolExamenes.class, "rexa")
                .join("eventoCicloAcademico eca")
                .join("eca.eventoAcademico ea", "eca.cicloAcademico ca")
                .filter("ca.id", cicloAcademico);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<CursoMasivoExamen> allByRolExamenes(RolExamenes rolExamenes) {
        Octavia sql = Octavia.query()
                .from(CursoMasivoExamen.class, "cme")
                .join("rolExamenes re", "userRegistro ur", "curso cu")
                //      .leftJoin("dia d", "hora h")
                .left("ur.persona urPer", "grupoHorasExamen ghe", "ghe.dia", "ghe.horaInicio", "ghe.horaFin")
                .filter("re.id", rolExamenes)
                .orderBy("cme.id desc");
        return all(sql);
    }

    @Override
    public CursoMasivoExamen find(Long id) {
        Octavia sql = Octavia.query()
                .from(CursoMasivoExamen.class, "cm")
                .join("rolExamenes re", "userRegistro ur", "curso cu")
                .filter("cm.id", id);

        return find(sql);
    }

    @Override
    public void updateEstadoExcluido(CursoMasivoExamen cursoMasivoExamen) {
        cursoMasivoExamen.setEstadoEnum(EstadoCursoMasivoEnum.EXC);

        Octavia octavia = Octavia.update(CursoMasivoExamen.class);
        octavia.set(cursoMasivoExamen, "estado");
        octavia.set(cursoMasivoExamen, "usuarioExclusion");
        octavia.set(cursoMasivoExamen, "fechaExclusion");
        this.update(octavia);
    }

    @Override
    public void updateFechaExamen(CursoMasivoExamen cursoMasivoExamen) {
        Octavia octavia = Octavia.update(CursoMasivoExamen.class);
        octavia.set(cursoMasivoExamen, "grupoHorasExamen");
        this.update(octavia);
    }

}
