package pe.edu.lamolina.amauta.dao.encuesta.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.encuesta.RespuestaEncuestaDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.encuesta.AlumnoEncuestaEstadoEnum;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.RespuestaEncuesta;

@Repository
public class RespuestaEncuestaDAOH extends AbstractEasyDAO<RespuestaEncuesta> implements RespuestaEncuestaDAO {

    public RespuestaEncuestaDAOH() {
        super();
        setClazz(RespuestaEncuesta.class);
    }

    @Override
    public List<RespuestaEncuesta> allByAlumnosEncuestaCiclo(List<Alumno> alumnos, ExamenVirtual encuesta, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(RespuestaEncuesta.class, "ep")
                .join("opcionRespuesta opc", "alumnoEncuesta ale", "ale.cicloAcademico ci", "ale.examenVirtual ex", "ale.alumno alu")
                .in("alu.id", alumnos)
                .filter("ex.id", encuesta)
                .filter("ci.id", ciclo)
                .filter("ale.estado", AlumnoEncuestaEstadoEnum.FINALIZADA);

        return all(sql);
    }

}
