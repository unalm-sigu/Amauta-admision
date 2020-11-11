package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.academico.NombreCicloDAO;
import pe.edu.lamolina.model.academico.NombreCiclo;
import pe.edu.lamolina.model.general.Idioma;

@Repository
public class NombreCicloDAOH extends AbstractEasyDAO<NombreCiclo> implements NombreCicloDAO {

    public NombreCicloDAOH() {
        super();
        setClazz(NombreCiclo.class);
    }

    @Override
    public List<NombreCiclo> allByIdioma(Idioma idioma) {
        Octavia sql = new Octavia()
                .from(NombreCiclo.class, "nc")
                .join("idioma idi")
                .filter("idi.id", idioma);

        return all(sql);
    }

}
