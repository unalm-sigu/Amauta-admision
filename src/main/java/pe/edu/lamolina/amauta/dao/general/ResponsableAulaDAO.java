package pe.edu.lamolina.amauta.dao.general;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.TipoResponsableEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.ResponsableAula;

public interface ResponsableAulaDAO extends EasyDAO<ResponsableAula> {

    List<ResponsableAula> allByPersona(Persona personaResponsable, EstadoEnum... estados);

    List<ResponsableAula> allByPersona(List<Persona> personaResponsable, EstadoEnum... estados);

    List<ResponsableAula> allByAulas(List<Aula> aulas, EstadoEnum... estados);

    List<ResponsableAula> allByEstado(EstadoEnum... estados);

    List<ResponsableAula> allByResponsableAulas(DynatableFilter filter, EstadoEnum... estados);

    ResponsableAula findByPersonaAndTipo(Persona persona, TipoResponsableEnum tipoResponsableEnum, EstadoEnum... estados);

}
