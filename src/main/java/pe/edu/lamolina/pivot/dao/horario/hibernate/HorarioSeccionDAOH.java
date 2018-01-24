package pe.edu.lamolina.pivot.dao.horario.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.horario.HorarioSeccion;

@Repository
public class HorarioSeccionDAOH extends AbstractEasyDAO<HorarioSeccion> implements HorarioSeccionDAO {

    public HorarioSeccionDAOH() {
        super();
        setClazz(HorarioSeccion.class);
    }

    @Override
    public List<HorarioSeccion> allBySecciones(List<Seccion> secciones) {
        Octavia sql = Octavia.query()
                .from(HorarioSeccion.class, "hs")
                .join("dia di", "hora ho", "seccion sec")
                .in("sec.id", secciones);

        return all(sql);
    }

    @Override
    public List<HorarioSeccion> allBySeccion(Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(HorarioSeccion.class, "hs")
                .join("dia di", "hora ho", "seccion sec")
                .filter("sec.id", seccion);
        return all(sql);
    }

    @Override
    public List<HorarioSeccion> allByCicloCurso(CicloAcademico cicloAcademico, List<Curso> cursos) {
        Octavia sql = Octavia.query()
                .from(HorarioSeccion.class, "hs")
                .join("dia di", "hora ho", "seccion sec", "sec.grupoSeccion gru", "gru.cicloAcademico ciclo", "gru.curso cu")
                .filter("ciclo.id", cicloAcademico)
                .in("cu.id", cursos);

        return all(sql);
    }
}
