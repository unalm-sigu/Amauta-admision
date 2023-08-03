package pe.edu.lamolina.amauta.dao.consejeria.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.consejeria.PlanTutorialDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tutoria.PlanTutorial;

@Repository
public class PlanTutorialDAOH extends AbstractEasyDAO<PlanTutorial> implements PlanTutorialDAO {

    public PlanTutorialDAOH() {
        super();
        setClazz(PlanTutorial.class);
    }

    @Override
    public List<PlanTutorial> allByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo) {
        Octavia sql = new Octavia()
                .from(PlanTutorial.class, "pt")
                .join("alumno alu", "cicloAcademico ci")
                .filter("ci.id", ciclo)
                .filter("alu.id", alumno)
                .orderBy("pt.codigo");

        return all(sql);
    }

    @Override
    public List<PlanTutorial> allByAlumnosCiclo(List<Alumno> alumnos, CicloAcademico ciclo) {
        Octavia sql = new Octavia()
                .from(PlanTutorial.class, "pt")
                .join("alumno alu", "cicloAcademico ci")
                .filter("ci.id", ciclo)
                .in("alu.id", alumnos)
                .orderBy("pt.codigo");

        return all(sql);
    }

}
