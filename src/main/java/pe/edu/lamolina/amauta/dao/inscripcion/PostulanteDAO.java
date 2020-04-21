package pe.edu.lamolina.amauta.dao.inscripcion;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface PostulanteDAO extends EasyDAO<Postulante> {

    List<Postulante> allByPersona(Persona persona);

    Postulante findByDNICiclo(String dni, CicloPostula ciclo);

    Postulante findByDocIdentidadCiclo(TipoDocIdentidad tipoDoc, String nroDoc, CicloPostula ciclo);

    Postulante findByCodigoCiclo(String codigo, CicloPostula ciclo);

}
