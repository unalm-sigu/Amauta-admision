package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.academico.NombreTituloAcademicoDAO;
import pe.edu.lamolina.model.academico.NombreTituloAcademico;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.social.TituloAcademico;

@Repository
public class NombreTituloAcademicoDAOH extends AbstractEasyDAO<NombreTituloAcademico> implements NombreTituloAcademicoDAO {

    public NombreTituloAcademicoDAOH() {
        super();
        setClazz(NombreTituloAcademico.class);
    }

    @Override
    public NombreTituloAcademico findByIdioma(TituloAcademico tituloAcademico, Idioma idioma) {
        Octavia sql = new Octavia()
                .from(NombreTituloAcademico.class, "nf")
                .join("idioma idi", "tituloAcademico ta")
                .filter("idi.id", idioma)
                .filter("ta.id", tituloAcademico);
        return find(sql);
    }

    @Override
    public List<NombreTituloAcademico> allByTitulo(TituloAcademico tituloAcademico) {
        Octavia sql = new Octavia()
                .from(NombreTituloAcademico.class, "nf")
                .join("idioma idi", "tituloAcademico ta")
                .filter("ta.id", tituloAcademico);
        return all(sql);

    }

    @Override
    public List<NombreTituloAcademico> allByIdioma(Idioma idioma) {
        Octavia sql = new Octavia()
                .from(NombreTituloAcademico.class, "nf")
                .join("idioma idi", "tituloAcademico ta")
                .filter("idi.id", idioma);
        return all(sql);

    }

}
