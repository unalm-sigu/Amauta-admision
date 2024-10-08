package pe.edu.lamolina.amauta.dao.nivelacioneegg.hibernate;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Insecto;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.NotaAlumnoNivelacionDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.nivelacioneegg.AlumnoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;

@Slf4j
@Repository
public class NotaAlumnoNivelacionDAOH extends AbstractEasyDAO<NotaAlumnoNivelacion> implements NotaAlumnoNivelacionDAO {

    public NotaAlumnoNivelacionDAOH() {
        super();
        setClazz(NotaAlumnoNivelacion.class);
    }

    @Override
    public List<NotaAlumnoNivelacion> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(NotaAlumnoNivelacion.class, "nan")
                .join("alumnoNivelacion an", "an.cicloAcademico ci", "an.alumno")
                .leftJoin("an.prelamolina", "an.evaluado")
                .filter("ci.id", ciclo);

        return all(sql);
    }

    @Override
    public List<NotaAlumnoNivelacion> allByAlumnosCiclo(List<Alumno> alumnos, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(NotaAlumnoNivelacion.class, "nan")
                .join("alumnoNivelacion an", "an.cicloAcademico ci", "an.alumno alu")
                .join("temaCiclo tc", "tc.temaExamen")
                .leftJoin("an.prelamolina", "an.evaluado")
                .in("alu.id", alumnos)
                .filter("ci.id", ciclo);

        return all(sql);
    }

    @Override
    public List<NotaAlumnoNivelacion> allByAlumnoNivelacion(AlumnoNivelacion alumnoNiv) {
        Octavia sql = Octavia.query()
                .from(NotaAlumnoNivelacion.class, "nan")
                .join("alumnoNivelacion an", "an.cicloAcademico ci", "an.alumno alu")
                .join("temaCiclo tc", "tc.temaExamen")
                .leftJoin("an.prelamolina", "an.evaluado")
                .filter("an.id", alumnoNiv);

        return all(sql);
    }

    @Override
    public List<NotaAlumnoNivelacion> allByAlumnosNivelacion(List<AlumnoNivelacion> alumnosNiv) {
        Octavia sql = Octavia.query()
                .from(NotaAlumnoNivelacion.class, "nan")
                .join("alumnoNivelacion an", "an.cicloAcademico ci", "an.alumno alu")
                .join("temaCiclo tc", "tc.temaExamen")
                .leftJoin("an.prelamolina", "an.evaluado")
                .in("an.id", alumnosNiv);

        return all(sql);
    }

    @Override
    public int saveList(List<NotaAlumnoNivelacion> notasAlumnos) {
        if (notasAlumnos.isEmpty()) {
            return 0;
        }

        long t1 = System.currentTimeMillis();
        Insecto sql = Insecto.createInsert()
                .into(NotaAlumnoNivelacion.class)
                .columns("estado", "notaExamen", "puntajeExamen", "temaAprobado",
                        "notaCurso", "esMatriculable", "fechaRegistro",
                        "alumnoNivelacion", "temaCiclo", "curso", "cursoNivelacion", "userRegistro")
                .values(notasAlumnos);

        Query query = getCurrentSession().createSQLQuery(sql.toString());
        int rows = query.executeUpdate();

        long t2 = System.currentTimeMillis();
        log.info("{} NotaAlumnoNivelacion's insertados en {} mseg....", rows, (t2 - t1));
        return rows;
    }

    @Override
    public int updateList(List<NotaAlumnoNivelacion> notasAlumnos, String... columnas) {
        if (notasAlumnos.isEmpty()) {
            return 0;
        }

        long t1 = System.currentTimeMillis();
        Insecto sql = Insecto.createUpdate(NotaAlumnoNivelacion.class)
                .set(columnas)
                .with(notasAlumnos);

        Query query = getCurrentSession().createSQLQuery(sql.toString());
        int rows = query.executeUpdate();

        long t2 = System.currentTimeMillis();
        log.info("{} NotaAlumnoNivelacion's actualizados en {} mseg....", rows, (t2 - t1));
        return rows;
    }

}
