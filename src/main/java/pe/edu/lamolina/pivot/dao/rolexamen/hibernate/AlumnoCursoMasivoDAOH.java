package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.rolexamen.AlumnoCursoMasivo;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoCursoMasivoDAO;

@Repository
public class AlumnoCursoMasivoDAOH extends AbstractEasyDAO<AlumnoCursoMasivo> implements AlumnoCursoMasivoDAO {

    public AlumnoCursoMasivoDAOH() {
        super();
        setClazz(AlumnoCursoMasivo.class);
    }        
}
