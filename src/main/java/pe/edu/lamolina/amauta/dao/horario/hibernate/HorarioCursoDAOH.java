package pe.edu.lamolina.amauta.dao.horario.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioCursoDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.horario.PlantillaNivelacion;
import pe.edu.lamolina.model.horario.HorarioCurso;

@Repository
public class HorarioCursoDAOH extends AbstractEasyDAO<HorarioCurso> implements HorarioCursoDAO {

    public HorarioCursoDAOH() {
        super();
        setClazz(HorarioCurso.class);
    }

    @Override
    public List<HorarioCurso> allByCursoCicloPlantilla(CursoCicloAcademico cursoCiclo, PlantillaNivelacion plantilla) {
        Octavia sql = Octavia.query()
                .from(HorarioCurso.class, "hc")
                .join("cursoCiclo cc", "plantilla pl", "cc.curso", "cc.cicloAcademico")
                .filter("cc.id", cursoCiclo)
                .filter("pl.id", plantilla)
                .orderBy("hc.semana");

        return all(sql);
    }

    @Override
    public List<HorarioCurso> allByCicloPlantilla(CicloAcademico ciclo, PlantillaNivelacion plantilla) {
        Octavia sql = Octavia.query()
                .from(HorarioCurso.class, "hc")
                .join("cursoCiclo cc", "plantilla pl")
                .join("cc.curso", "cc.cicloAcademico ci")
                .filter("ci.id", ciclo)
                .filter("pl.id", plantilla);

        return all(sql);
    }

    @Override
    public List<HorarioCurso> allByCursosCiclo(List<CursoCicloAcademico> cursosCiclo) {
        Octavia sql = Octavia.query()
                .from(HorarioCurso.class, "hc")
                .join("cursoCiclo cc", "plantilla pl", "cc.curso", "cc.cicloAcademico")
                .in("cc.id", cursosCiclo)
                .orderBy("hc.semana");

        return all(sql);
    }

    @Override
    public List<HorarioCurso> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(HorarioCurso.class, "hc")
                .join("cursoCiclo cc", "plantilla pl")
                .join("cc.curso", "cc.cicloAcademico ci")
                .filter("ci.id", ciclo);

        return all(sql);
    }

}
