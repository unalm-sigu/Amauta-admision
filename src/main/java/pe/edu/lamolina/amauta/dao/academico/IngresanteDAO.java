package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.inscripcion.Ingresante;

public interface IngresanteDAO extends EasyDAO<Ingresante> {

    List<Ingresante> allByCicloAcademico(CicloAcademico cicloAcademico);

    List<Ingresante> allByCicloAcademicoModalidadIngreso(List<CicloAcademico> ciclosQuintosAnteriores, String code);
    

}
