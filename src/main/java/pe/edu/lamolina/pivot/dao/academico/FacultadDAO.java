package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.general.Compania;

public interface FacultadDAO extends EasyDAO<Facultad> {

    List<Facultad> allDynatable(DynatableFilter filter, List<Facultad> facultads);

    List<Facultad> allByCompania(Compania compania);

    List<Facultad> allActivos();

    List<Facultad> allFacultad(String nombre, Compania compania);

    Facultad findByCodigo(String codigo);

    List<Facultad> allNormal();

    List<Facultad> allFromDocentesByCiclo(CicloAcademico cicloAcademico);

    List<Facultad> allFromCursosByCiclo(CicloAcademico cicloAcademico);

}
