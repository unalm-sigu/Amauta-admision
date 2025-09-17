package pe.edu.lamolina.amauta.dao.horario.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioCursoDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.horario.GrupoHorasNivelacion;
import pe.edu.lamolina.model.horario.HorarioCurso;

@Repository
public class HorarioCursoDAOH extends AbstractEasyDAO<HorarioCurso> implements HorarioCursoDAO {

    public HorarioCursoDAOH() {
        super();
        setClazz(HorarioCurso.class);
    }

    @Override
    public List<HorarioCurso> allByCursoCicloHorario(CursoCicloAcademico cursoCiclo, GrupoHorasNivelacion grupoHoras) {
        Octavia sql = Octavia.query()
                .from(HorarioCurso.class, "hc")
                .join("cursoCiclo cc", "grupoHoras gh", "cc.curso", "cc.cicloAcademico")
                .filter("cc.id", cursoCiclo)
                .filter("gh.id", grupoHoras)
                .orderBy("hc.semana");

        return all(sql);
    }

    @Override
    public List<HorarioCurso> allByCicloHorario(CicloAcademico ciclo, GrupoHorasNivelacion grupoHoras) {
        Octavia sql = Octavia.query()
                .from(HorarioCurso.class, "hc")
                .join("cursoCiclo cc", "grupoHoras gh")
                .join("cc.curso", "cc.cicloAcademico ci")
                .filter("ci.id", ciclo)
                .filter("gh.id", grupoHoras);

        return all(sql);
    }

    @Override
    public List<HorarioCurso> allByCursosCiclo(List<CursoCicloAcademico> cursosCiclo) {
        Octavia sql = Octavia.query()
                .from(HorarioCurso.class, "hc")
                .join("cursoCiclo cc", "grupoHoras gh", "cc.curso", "cc.cicloAcademico")
                .in("cc.id", cursosCiclo)
                .orderBy("hc.semana");

        return all(sql);
    }

}
