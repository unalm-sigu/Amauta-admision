package pe.edu.lamolina.amauta.dao.nivelacioneegg;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.nivelacioneegg.ModalidadTemaCiclo;

public interface ModalidadTemaCicloDAO extends EasyDAO<ModalidadTemaCiclo> {

    List<ModalidadTemaCiclo> allByDynatable(DynatableFilter filter, CicloAcademico ciclo);

    List<ModalidadTemaCiclo> allByCiclo(CicloAcademico ciclo);

}
