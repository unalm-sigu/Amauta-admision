package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import pe.albatross.octavia.easydao.AbstractEasyDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.socioeconomico.FichaSocioeconomica;
import pe.edu.lamolina.pivot.dao.encuesta.FichaSocioeconomicaDAO;

@Repository
public class FichaSocioeconomicaDAOH extends AbstractEasyDAO<FichaSocioeconomica> implements FichaSocioeconomicaDAO {

    public FichaSocioeconomicaDAOH() {
        super();
        setClazz(FichaSocioeconomica.class);
    }

    @Override
    public FichaSocioeconomica findByAlumno(Alumno alumno, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(FichaSocioeconomica.class, "fs")
                .join("alumno alum", "cicloAcademico ca")
                .filter("alum.id", alumno)
                .filter("ca.id", cicloAcademico);
        return find(sql);
    }
}
