package pe.edu.lamolina.pivot.dao.inscripcion;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;

public interface ModalidadIngresoCicloDAO extends EasyDAO<ModalidadIngresoCiclo> {

    List<ModalidadIngresoCiclo> allByCicloPostula(CicloPostula cicloPostula);

    List<ModalidadIngresoCiclo> allByCicloAcademico(CicloAcademico cicloAcademico);

    List<ModalidadIngresoCiclo> allSuperioresByCiclo(CicloPostula ciclo);

    List<ModalidadIngresoCiclo> allByModalidadPadreCiclo(ModalidadIngreso modalidad, CicloPostula ciclo);

    List<ModalidadIngresoCiclo> allByFilter(CicloPostula ciclo, Integer rindeExamenAdmision);

    ModalidadIngresoCiclo findByModalidadCiclo(ModalidadIngreso modalidad, CicloPostula ciclo);

    List<ModalidadIngresoCiclo> allByModalidadesCiclo(List<ModalidadIngreso> modalidades, List<CicloPostula> ciclosPostula);

}

