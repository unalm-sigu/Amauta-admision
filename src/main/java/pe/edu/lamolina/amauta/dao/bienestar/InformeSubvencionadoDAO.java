package pe.edu.lamolina.amauta.dao.bienestar;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.bienestar.InformeSubvencionado;
import pe.edu.lamolina.model.general.Persona;

public interface InformeSubvencionadoDAO extends EasyDAO<InformeSubvencionado> {

    List<InformeSubvencionado> allBySupervisorCiclo(Persona supervisor, CicloAcademico ciclo, DynatableFilter filter);

}
