package pe.edu.lamolina.amauta.dao.finanza;

import java.util.LinkedHashMap;
import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface DeudaInteresadoDAO extends EasyDAO<DeudaInteresado> {

    DeudaInteresado findActivaByPostulante(Postulante postulante);

    DeudaInteresado findLastInactiveByPostulante(Postulante postulanteBD);

    List<DeudaInteresado> allByInteresados(List<Interesado> interesados);

    DeudaInteresado findByProspectoCiclo(Interesado interesado, CicloPostula ciclo);

    List<DeudaInteresado> allActivasByInteresado(Interesado interesado, CicloPostula ciclo);

    List<DeudaInteresado> allByInteresado(Interesado interesado, CicloPostula ciclo);

    List<DeudaInteresado> allByPostulanteModCiclo(Postulante postulante, ModalidadIngreso modalidadIngreso, CicloPostula cicloPostula);

    List<DeudaInteresado> allActivosByCicloPostula(CicloPostula cicloActivo);

    List<LinkedHashMap> deudaInteresadoPagadosByCiclo(CicloPostula ciclo);

    List<DeudaInteresado> allActivosByInteresado(Interesado interesado);

}
