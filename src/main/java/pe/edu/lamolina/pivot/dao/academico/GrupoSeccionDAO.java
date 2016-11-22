package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;

public interface GrupoSeccionDAO extends Crud<GrupoSeccion> {

    GrupoSeccion find(Long idGrupoSeccion);
}
