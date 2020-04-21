package pe.edu.lamolina.amauta.dao.finanza;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.finanzas.AbonoPostulante;
import pe.edu.lamolina.model.finanzas.ItemCargaAbono;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface AbonoPostulanteDAO extends EasyDAO<AbonoPostulante> {

    List<AbonoPostulante> allByPostulante(Postulante postul);

    AbonoPostulante findByItemCarga(ItemCargaAbono item);

    AbonoPostulante findByPostulante(Postulante postulatenBD);

}
