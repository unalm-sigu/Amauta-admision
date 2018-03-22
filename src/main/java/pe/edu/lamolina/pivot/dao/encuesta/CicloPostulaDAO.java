package pe.edu.lamolina.pivot.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

public interface CicloPostulaDAO extends EasyDAO<CicloPostula> {

    CicloPostula find(CicloPostula ciclo);

    CicloPostula findActivo(ModalidadEstudio modalidad);

    CicloPostula findByCicloAcademico(CicloAcademico cicloAcadBD);

    CicloPostula findCicloAnterior();

    CicloPostula findByCodigo(String codigo, ModalidadEstudio modalidad);

    CicloPostula findUltimo();

    List<CicloPostula> allByDaynatable(DynatableFilter filter, ModalidadEstudio modalidad);

    List<CicloPostula> allCicloPostula();

    List<CicloPostula> allCiclosMenores(CicloPostula cicloPostula);

    List<CicloPostula> allAnteriores(int ciclos, CicloPostula cicloPostula);

}
