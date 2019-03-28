package pe.edu.lamolina.pivot.dao.mensajeria.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DetalleGrupoAlumno;
import pe.edu.lamolina.model.academico.GrupoAlumno;
import pe.edu.lamolina.pivot.dao.mensajeria.DetalleGrupoAlumnoDAO;

@Repository
public class DetalleGrupoAlumnoDAOH extends AbstractEasyDAO<DetalleGrupoAlumno> implements DetalleGrupoAlumnoDAO {

    public DetalleGrupoAlumnoDAOH() {
        super();
        setClazz(DetalleGrupoAlumno.class);
    }

    @Override
    public List<DetalleGrupoAlumno> allByDynatbleGrupoAlumno(DynatableFilter filter, GrupoAlumno grupo) {
        DynatableSql sql = new DynatableSql(filter)
                .from(DetalleGrupoAlumno.class, "dga")
                .join("grupoAlumno ga")
                .leftJoin("modalidadEstudio", "situacionAcademica", "facultad", "carrera", "grupoSeccion", "seccion s", "curso")
                .leftJoin("s.grupoSeccion", "s.grupoHoras")
                .filter("ga.id", grupo)
                .searchFields("dga.matriculados", "dga.matriculables")
                .orderBy("dga.id desc");

        return all(sql);
    }

    @Override
    public List<DetalleGrupoAlumno> allByGrupoAlumnoCiclo(GrupoAlumno gpoAlumno) {
        Octavia sql = Octavia.query()
                .from(DetalleGrupoAlumno.class, "dga")
                .join("grupoAlumno ga")
                .leftJoin("modalidadEstudio", "situacionAcademica", "facultad", "carrera", "grupoSeccion gs", "seccion s", "curso")
                .leftJoin("s.grupoSeccion ggss", "s.grupoHoras", "ggss.curso", "ggss.cicloAcademico")
                .leftJoin("gs.curso", "gs.cicloAcademico")
                .filter("ga.id", gpoAlumno);

        return all(sql);
    }

}
