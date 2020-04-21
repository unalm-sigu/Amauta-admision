package pe.edu.lamolina.amauta.dao.encuesta.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.enums.EncuestaEstadoEnum;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.amauta.dao.encuesta.EncuestaEstudiantilDAO;

@Repository
public class EncuestaEstudiantilDAOH extends AbstractEasyDAO<EncuestaEstudiantil> implements EncuestaEstudiantilDAO {

    public EncuestaEstudiantilDAOH() {
        super();
        setClazz(EncuestaEstudiantil.class);
    }

    @Override
    public EncuestaEstudiantil find(long id) {
        Octavia sql = Octavia.query()
                .from(EncuestaEstudiantil.class, "ep")
                .join("encuesta en", "cicloAcademico ci", "en.tipoExamen")
                .filter("ep.id", id);
        return find(sql);
    }

    @Override
    public EncuestaEstudiantil findByCicloEncuesta(CicloAcademico ciclo, ExamenVirtual encuesta) {
        Octavia sql = Octavia.query()
                .from(EncuestaEstudiantil.class, "ep")
                .join("encuesta en", "cicloAcademico ci", "en.tipoExamen")
                .filter("en.id", encuesta)
                .filter("ci.id", ciclo);
        return find(sql);
    }

    @Override
    public EncuestaEstudiantil findByCicloTipo(CicloAcademico ciclo, TipoExamenVirtualEnum tipoEnum) {
        Octavia sql = Octavia.query()
                .from(EncuestaEstudiantil.class, "ep")
                .join("encuesta en", "cicloAcademico ci", "en.tipoExamen tipo")
                .filter("en.estado", EncuestaEstadoEnum.ACT)
                .filter("tipo.codigo", tipoEnum)
                .filter("ci.id", ciclo);
        return find(sql);
    }

    @Override
    public List<EncuestaEstudiantil> allByEncuestas(List<ExamenVirtual> encuestas) {
        Octavia sql = Octavia.query()
                .from(EncuestaEstudiantil.class, "ep")
                .join("encuesta en", "cicloAcademico ci")
                .in("en.id", encuestas);
        return all(sql);
    }

}
