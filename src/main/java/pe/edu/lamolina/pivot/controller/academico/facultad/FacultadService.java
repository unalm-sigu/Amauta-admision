package pe.edu.lamolina.pivot.controller.academico.facultad;

import java.util.List;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.Facultad;
import pe.edu.lamolina.pivot.model.general.Compania;

public interface FacultadService {

    List<Facultad> allFacultad(DynatableFilter filter);

    Facultad findFacultad(Long idFacultad);

    void save(Facultad facultad);

    void update(Facultad facultad);

    void delete(Facultad facultad);

    void estado(Facultad facultad);

    List<Facultad> allFacultad(String nombre, Compania compania);
}
