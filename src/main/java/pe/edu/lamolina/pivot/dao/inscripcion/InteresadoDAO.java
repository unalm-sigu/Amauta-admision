package pe.edu.lamolina.pivot.dao.inscripcion;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Interesado;

public interface InteresadoDAO extends EasyDAO<Interesado> {

    Interesado findLock(Long id);

    List<Interesado> allByNombre(String nombre, CicloPostula ciclo);

    Interesado findUltimoByDocumentoIdentidad(String documento);

    Interesado findByDocumentoIdentidad(String documento, CicloPostula ciclo);

    Interesado findByCelular(String celular, CicloPostula ciclo);

    Interesado findUltimoByCelular(String celular);

    Interesado findByFacebook(String faceIdentifier, CicloPostula ciclo);

}

