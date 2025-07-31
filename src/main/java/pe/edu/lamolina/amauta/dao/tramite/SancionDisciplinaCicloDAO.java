package pe.edu.lamolina.amauta.dao.tramite;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoRegular;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.SancionDisciplina;
import pe.edu.lamolina.model.tramite.SancionDisciplinaCiclo;

import java.util.List;

public interface SancionDisciplinaCicloDAO extends EasyDAO<SancionDisciplinaCiclo> {
    int saveAll(List<SancionDisciplinaCiclo> ciclosAcademicos);
    List<SancionDisciplinaCiclo> findBySancionDisciplina(SancionDisciplina sancion);
    List<SancionDisciplinaCiclo> allSancionDisciplinaByResolucion(Resolucion resolucion);
}
