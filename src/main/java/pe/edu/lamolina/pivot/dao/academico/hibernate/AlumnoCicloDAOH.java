package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.CicloAcademico;

@Repository
public class AlumnoCicloDAOH extends AbstractEasyDAO<AlumnoCiclo> implements AlumnoCicloDAO {

    public AlumnoCicloDAOH() {
        super();
        setClazz(AlumnoCiclo.class);
    }

    @Override
    public AlumnoCiclo findByAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca", "carrera car", "orientacionCarrera oc")
                .leftJoin("situacionInicio si", "situacionFinal sf", "userRegistro ur")
                .leftJoin("userModificacion um")
                .filter("alu.id", alumno)
                .filter("ca.id", cicloAcademico);
        return find(sql);
    }

    @Override
    public List<AlumnoCiclo> allByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu")
                //   .leftJoin("situacionInicio si", "situacionFinal sf", "userRegistro ur")
                //    .leftJoin("userModificacion um")
                .filter("alu.id", alumno);
        return all(sql);
    }

}
