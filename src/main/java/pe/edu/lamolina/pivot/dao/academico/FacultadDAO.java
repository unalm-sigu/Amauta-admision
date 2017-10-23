package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.Facultad;
import pe.edu.lamolina.pivot.model.general.Compania;

public interface FacultadDAO extends Crud<Facultad> {

    List<Facultad> allDynatable(DynatableFilter filter);

    List<Facultad> allByCompania(Compania compania);

    List<Facultad> allActivos();

}

