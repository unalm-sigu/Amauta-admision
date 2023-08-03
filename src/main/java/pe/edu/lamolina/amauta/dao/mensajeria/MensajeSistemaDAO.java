package pe.edu.lamolina.amauta.dao.mensajeria;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.enums.NombreTablasEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.social.MensajeSistema;

public interface MensajeSistemaDAO extends EasyDAO<MensajeSistema> {

    MensajeSistema findByTablaInstancia(NombreTablasEnum tabla, Long instancia);

    List<MensajeSistema> allPendientesByDocente(Docente docente);

    List<MensajeSistema> allPendientesByPersona(Persona persona);

}
