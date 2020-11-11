package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.academico.NombreGradoDAO;
import pe.edu.lamolina.model.academico.GradoAcademico;
import pe.edu.lamolina.model.academico.NombreGrado;
import pe.edu.lamolina.model.general.Idioma;

@Repository
public class NombreGradoDAOH extends AbstractEasyDAO<NombreGrado> implements NombreGradoDAO {

    public NombreGradoDAOH() {
        super();
        setClazz(NombreGrado.class);
    }

    @Override
    public NombreGrado findByIdioma(GradoAcademico gradoAcademico, Idioma idioma) {
        Octavia sql = new Octavia()
                .from(NombreGrado.class, "nf")
                .join("idioma idi", "gradoAcademico ga")
                .filter("idi.id", idioma)
                .filter("ga.id", gradoAcademico);
        return find(sql);
    }

    @Override
    public List<NombreGrado> allByTitulo(GradoAcademico gradoAcademico) {
        Octavia sql = new Octavia()
                .from(NombreGrado.class, "nf")
                .join("idioma idi", "gradoAcademico ga")
                .filter("ga.id", gradoAcademico);
        return all(sql);

    }

    @Override
    public List<NombreGrado> allByIdioma(Idioma idioma) {
        Octavia sql = new Octavia()
                .from(NombreGrado.class, "nf")
                .join("idioma idi", "gradoAcademico ta")
                .filter("idi.id", idioma);
        return all(sql);

    }

}
