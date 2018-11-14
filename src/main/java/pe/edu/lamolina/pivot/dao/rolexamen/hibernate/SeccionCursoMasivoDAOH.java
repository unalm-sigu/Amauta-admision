package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.SeccionCursoMasivo;
import pe.edu.lamolina.pivot.dao.rolexamen.*;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;

@Repository
public class SeccionCursoMasivoDAOH extends AbstractEasyDAO<SeccionCursoMasivo> implements SeccionCursoMasivoDAO {

    public SeccionCursoMasivoDAOH() {
        super();
        setClazz(SeccionCursoMasivo.class);
    }

    @Override
    public List<SeccionCursoMasivo> allByCursoMasivoExamenAndEstados(
            CursoMasivoExamen cursoMasivoExamen, List<SeccionRolExamenEstadoEnum> estados) {
        Octavia sql = Octavia.query()  
                .from(SeccionCursoMasivo.class, "scm")
                .join(" CursoMasivoExamen cme", "seccion sec")
                .filter("cme.id", cursoMasivoExamen)
                .in("scm.estado", estados);
        return all(sql);
    }

//    @Override
//    public void updateEstado(SeccionGrupoRegular seccionGrupoRegularUpd) {
//        Octavia octavia = Octavia.update(SeccionGrupoRegular.class);
//        octavia.set(seccionGrupoRegularUpd, "estado");
//        octavia.set(seccionGrupoRegularUpd, "usuarioExclusion");
//        octavia.set(seccionGrupoRegularUpd, "fechaExclusion");
//        this.update(octavia);
//    }
}
