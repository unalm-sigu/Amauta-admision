package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.encuestaestudiantil.ConfiguraEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.pivot.dao.encuesta.ConfiguraEncuestaDAO;

@Repository
public class ConfiguraEncuestaDAOH extends AbstractEasyDAO<ConfiguraEncuesta> implements ConfiguraEncuestaDAO {

    public ConfiguraEncuestaDAOH() {
        super();
        setClazz(ConfiguraEncuesta.class);
    }

    @Override
    public ConfiguraEncuesta findByEncuestaEstudiantil(EncuestaEstudiantil encuestaEstudiantil) {
        Octavia sql = Octavia.query()
                .from(ConfiguraEncuesta.class, "ce")
                .join("encuestaEstudiantil ee")
                .filter("ee.id", encuestaEstudiantil);
        return find(sql);
    }

    @Override
    public ConfiguraEncuesta find(ConfiguraEncuesta configuraEncuestaForm) {
        Octavia sql = Octavia.query()
                .from(ConfiguraEncuesta.class, "ce")
                .join("encuestaEstudiantil ee")
                .filter("ce.id", configuraEncuestaForm);
        return find(sql);
    }

    @Override
    public ConfiguraEncuesta findByCicloEncuesta(CicloAcademico ciclo, ExamenVirtual encuesta) {
        Octavia sql = Octavia.query()
                .from(ConfiguraEncuesta.class, "ce")
                .join("encuestaEstudiantil ee", "ee.encuesta en", "ee.cicloAcademico ca")
                .filter("en.id", encuesta)
                .filter("ca.id", ciclo);
        return find(sql);
    }

    @Override
    public ConfiguraEncuesta findConfiguraEncuestaByEncuestaEstudiantil(EncuestaEstudiantil encuestaEstudiantil) {
        Octavia sql = Octavia.query()
                .from(ConfiguraEncuesta.class, "ce")
                .join("encuestaEstudiantil ee", "ee.encuesta en", "ee.cicloAcademico ca")
                .filter("ee.id", encuestaEstudiantil);
        return find(sql);
    }

}
