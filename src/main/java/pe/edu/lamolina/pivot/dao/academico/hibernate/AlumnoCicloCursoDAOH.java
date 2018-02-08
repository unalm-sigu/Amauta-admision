package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;

@Repository
public class AlumnoCicloCursoDAOH extends AbstractEasyDAO<AlumnoCicloCurso> implements AlumnoCicloCursoDAO {

    public AlumnoCicloCursoDAOH() {
        super();
        setClazz(AlumnoCicloCurso.class);
    }

    @Override
    public List<AlumnoCicloCurso> findHistorial(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico", "acc.curso")
                .filter("al.id", alumno);

        return sql.all(getCurrentSession());
    }

    @Override
    public List<AlumnoCicloCurso> allByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cu")
                .join("ac.carrera", "ac.situacionInicio")
                .left("ac.situacionFinal", "ac.orientacionCarrera")
                .filter("al.id", alumno)
                .orderBy("ca.codigo desc", "cu.nombre");

        return sql.all(getCurrentSession());
    }

    @Override
    public List<AlumnoCicloCurso> allByAlumnoOrdeyByCurso(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCicloCurso.class, "acc")
                .join("alumnoCiclo ac", "ac.alumno al", "ac.cicloAcademico ca", "acc.curso cu")
                .join("ac.carrera", "ac.situacionInicio")
                .left("ac.situacionFinal", "ac.orientacionCarrera")
                .filter("al.id", alumno)
                .orderBy("cu.nombre asc", "cu.nombre");

        return sql.all(getCurrentSession());
    }
}
