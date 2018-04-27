package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.InasistenciaAlumno;
import pe.edu.lamolina.model.academico.TemaLeccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.pivot.dao.academico.InasistenciaAlumnoDAO;

@Repository
public class InasistenciaAlumnoDAOH extends AbstractEasyDAO<InasistenciaAlumno> implements InasistenciaAlumnoDAO {

    public InasistenciaAlumnoDAOH() {
        super();
        setClazz(InasistenciaAlumno.class);
    }

    @Override
    public List<InasistenciaAlumno> allByTemaLeccionActives(TemaLeccion temaCiclo) {
        Octavia sql = Octavia.query()
                .from(InasistenciaAlumno.class, "ia")
                .join("temaLeccion tl", "matriculaCurso mc")
                .join("mc.matriculaResumen mr", "mr.alumno alu")
                .filter("tl.id", temaCiclo)
                .filter("ia.estado", EstadoEnum.ACT);

        return all(sql);
    }

    @Override
    public void updateEstado(InasistenciaAlumno inasistenciaAlumno) {
        Octavia octavia = Octavia.update(InasistenciaAlumno.class);
        octavia.set(inasistenciaAlumno, "estado");
        octavia.set(inasistenciaAlumno, "fechaModificacion");
        octavia.set(inasistenciaAlumno, "usuarioModificacion");
        this.update(octavia);
    }

}
