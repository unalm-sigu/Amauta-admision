package pe.edu.lamolina.amauta.dao.consejeria.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoCualidadDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.tutoria.AlumnoCualidad;

@Repository
public class AlumnoCualidadDAOH extends AbstractEasyDAO<AlumnoCualidad> implements AlumnoCualidadDAO {

    public AlumnoCualidadDAOH() {
        super();
        setClazz(AlumnoCualidad.class);
    }

    @Override
    public List<AlumnoCualidad> allByAlumno(Alumno alumno) {
        Octavia sql = new Octavia()
                .from(AlumnoCualidad.class, "acu")
                .join("alumno alu", "tipoCualidadAlumno tca")
                .filter("alu.id", alumno);

        return all(sql);
    }

    @Override
    public List<AlumnoCualidad> allByAlumnos(List<Alumno> alumnos) {
        Octavia sql = new Octavia()
                .from(AlumnoCualidad.class, "acu")
                .join("alumno alu", "tipoCualidadAlumno tca")
                .in("alu.id", alumnos);

        return all(sql);
    }

    @Override
    public List<AlumnoCualidad> allByAlumnoTipoCualidad(Alumno alumno, String tipoCualidad) {
        Octavia sql = new Octavia()
                .from(AlumnoCualidad.class, "acu")
                .join("alumno alu", "tipoCualidadAlumno tca")
                .filter("alu.id", alumno)
                .filter("tca.tipoCualidad", tipoCualidad)
                .orderBy("tca.orden");

        return all(sql);
    }

}
