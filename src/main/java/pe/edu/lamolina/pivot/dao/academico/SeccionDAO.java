package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.Seccion;

public interface SeccionDAO extends Crud<Seccion> {

    List<Seccion> allByCargaAcademica(DynatableFilter filter);

}
