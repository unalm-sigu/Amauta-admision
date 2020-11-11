package pe.edu.lamolina.amauta.dao.academico.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.academico.NombreFacultadDAO;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.NombreFacultad;
import pe.edu.lamolina.model.general.Idioma;

@Repository
public class NombreFacultadDAOH extends AbstractEasyDAO<NombreFacultad> implements NombreFacultadDAO {

    public NombreFacultadDAOH() {
        super();
        setClazz(NombreFacultad.class);
    }

    @Override
    public NombreFacultad findByIdioma(Facultad facultadAlumno, Idioma idioma) {
        Octavia sql = new Octavia()
                .from(NombreFacultad.class, "nf")
                .join("idioma idi", "facultad fac")
                .filter("idi.id", idioma)
                .filter("fac.id", facultadAlumno);
        return find(sql);
    }

}
