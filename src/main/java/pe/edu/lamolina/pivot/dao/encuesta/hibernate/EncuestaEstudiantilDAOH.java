package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.encuesta.EncuestaEstudiantil;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaEstudiantilDAO;

@Repository
public class EncuestaEstudiantilDAOH extends AbstractEasyDAO<EncuestaEstudiantil> implements EncuestaEstudiantilDAO {

    public EncuestaEstudiantilDAOH() {
        super();
        setClazz(EncuestaEstudiantil.class);
    }

    @Override
    public EncuestaEstudiantil findByCicloEncuesta(CicloAcademico ciclo, ExamenVirtual encuesta) {
        Octavia sql = Octavia.query()
                .from(EncuestaEstudiantil.class, "ep")
                .join("encuesta en", "cicloAcademico ci")
                .filter("en.id", encuesta)
                .filter("ci.id", ciclo);
        return find(sql);
    }

}
