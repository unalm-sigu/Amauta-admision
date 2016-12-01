package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;

public interface DocenteSeccionDAO extends Crud<DocenteSeccion> {

    List<DocenteSeccion> allByCargaAcademica(DynatableFilter filter, Docente docente);

    List<DocenteSeccion> allByDocente(Docente docente);

}
