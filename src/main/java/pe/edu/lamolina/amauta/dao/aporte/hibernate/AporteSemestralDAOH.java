package pe.edu.lamolina.amauta.dao.aporte.hibernate;

import java.util.List;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.aporte.Aporte;
import pe.edu.lamolina.model.aporte.AporteSemestral;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.amauta.dao.aporte.AporteSemestralDAO;

@Repository
public class AporteSemestralDAOH extends AbstractEasyDAO<AporteSemestral> implements AporteSemestralDAO {

    public AporteSemestralDAOH() {
        super();
        setClazz(AporteSemestral.class);
    }

    @Override
    public AporteSemestral findActivoByAlumno(Alumno alumno, Aporte aporte) {
        Octavia sql = Octavia.query()
                .from(AporteSemestral.class, "apse")
                .join("aporte ap", "alumno alum")
                .leftJoin("categoriaBienestar cat")
                .filter("alum.id", alumno)
                .filter("ap.id", aporte)
                .filter("apse.estado", EstadoEnum.ACT);
        return find(sql);
    }

    @Override
    public List<AporteSemestral> allActivosByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AporteSemestral.class, "ase")
                .join("aporte ap", "alumno alum")
                .left("categoriaBienestar")
                .filter("alum.id", alumno)
                .filter("ase.estado", EstadoEnum.ACT)
                .orderBy("ap.nombre asc");
        return all(sql);
    }

    @Override
    public List<AporteSemestral> allByAporteAlumnos(Aporte aporte, List<Alumno> alumnos) {
        Octavia sql = Octavia.query()
                .from(AporteSemestral.class, "ase")
                .join("aporte ap", "alumno alum", "categoriaBienestar cat")
                .filter("ase.estado", EstadoEnum.ACT)
                .filter("ap.id", aporte)
                .in("alum.id", alumnos)
                .orderBy("alum.id desc");
        return all(sql);
    }

    @Override
    public AporteSemestral findSemestralByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AporteSemestral.class, "apse")
                .join("aporte ap", "alumno alum")
                .leftJoin("categoriaBienestar cat")
                .filter("alum.id", alumno)
                .filter("ap.codigo", "=","01")
                .filter("apse.estado", EstadoEnum.ACT);
        return find(sql);
    }

}
