package pe.edu.lamolina.amauta.dao.encuesta.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.encuesta.EncuestaPublicadaDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.examen.EncuestaPublicada;
import pe.edu.lamolina.model.examen.TipoExamenVirtual;

@Repository
public class EncuestaPublicadaDAOH extends AbstractEasyDAO<EncuestaPublicada> implements EncuestaPublicadaDAO {

    public EncuestaPublicadaDAOH() {
        super();
        setClazz(EncuestaPublicada.class);
    }

    @Override
    public List<EncuestaPublicada> allByCicloTipo(CicloAcademico ciclo, TipoExamenVirtual tipoEncuesta) {
        Octavia sql = Octavia.query()
                .from(EncuestaPublicada.class, "ep")
                .join("cicloAcademico ci", "examenVirtual ex", "ex.tipoExamen te")
                .filter("te.id", tipoEncuesta)
                .filter("ci.id", ciclo)
                .orderBy("ep.id DESC");

        return all(sql);
    }

}
