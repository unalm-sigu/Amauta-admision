package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.rolexamen.AlumnoCursoMasivo;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoCursoMasivoDAO;

@Repository
public class AlumnoCursoMasivoDAOH extends AbstractEasyDAO<AlumnoCursoMasivo> implements AlumnoCursoMasivoDAO {

    public AlumnoCursoMasivoDAOH() {
        super();
        setClazz(AlumnoCursoMasivo.class);
    }        

    @Override
    public List<AlumnoCursoMasivo> allAlumnoByCursoMasivo(Long id) {
         Octavia sql = Octavia.query()
                .from(AlumnoCursoMasivo.class, "acm")
                .join("cursoMasivoExamen cme", "userRegistro ur")
                .filter("cme.id", id);
        return all(sql);
    }
}
