package pe.edu.lamolina.pivot.dao.academico.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CambioAulaGrupo;
import pe.edu.lamolina.pivot.dao.academico.CambioAulaGrupoDAO;

@Repository
public class CambioAulaGrupoDAOH extends AbstractEasyDAO<CambioAulaGrupo> implements CambioAulaGrupoDAO {


//    @Override
//    public List<AmpliacionVacantes> allBySeccion(Seccion seccion) {
//        Octavia sql = Octavia.query()
//                .from(AmpliacionVacantes.class, "av")
//                .join("seccion se", "colaborador co", "co.cargo ca", "oficina ofi")
//                .filter("se.id", seccion)
//                .orderBy("av.fechaSolicitud desc");
//        return all(sql);
//    }
   
}
