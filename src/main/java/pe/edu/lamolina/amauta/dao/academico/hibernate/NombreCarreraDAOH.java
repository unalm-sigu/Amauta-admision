package pe.edu.lamolina.amauta.dao.academico.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.academico.NombreCarreraDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.NombreCarrera;
import pe.edu.lamolina.model.general.Idioma;

@Repository
public class NombreCarreraDAOH extends AbstractEasyDAO<NombreCarrera> implements NombreCarreraDAO {

    public NombreCarreraDAOH() {
        super();
        setClazz(NombreCarrera.class);
    }

    @Override
    public NombreCarrera findByIdioma(Carrera carrera, Idioma idioma) {
        Octavia sql = new Octavia()
                .from(NombreCarrera.class, "nc")
                .join("carrera car", "idioma idi")
                .filter("idi", idioma)
                .filter("car.id", carrera);

        return find(sql);
    }

}
