package pe.edu.lamolina.amauta.dao.academico;

import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.amauta.controller.academico.pronabec.BecadosFilterBean;
import pe.edu.lamolina.amauta.controller.academico.pronabec.MatriculadosBecadosBean;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.pronabec.InformacionBeca;
import pe.edu.lamolina.model.pronabec.TipoBeca;

import java.util.List;

public interface BecasPronabecDAO extends EasyDAO<InformacionBeca> {
    List<InformacionBeca> allByFilter(DynatableFilter filter);
    List<InformacionBeca> finByPersonaIds(InformacionBeca infoBeca);
    List<Persona> allByName(String nombre);
    List<MatriculadosBecadosBean> allMatriculadosBecadosPregrado(CicloAcademico cicloAcademico);
    List<BecadosFilterBean> allBecadosFilterExcel(CicloAcademico cicloAcademico, ModalidadEstudio modalidadEstudio,BecadosFilterBean becadosFilterBean);
    List<BecadosFilterBean> filterActualBecados(CicloAcademico cicloAcademico, ModalidadEstudio modalidadEstudio, BecadosFilterBean becadosFilterBean);
    List<BecadosFilterBean> filterAnteriorBecados(CicloAcademico cicloAcademico, ModalidadEstudio modalidadEstudio, BecadosFilterBean becadosFilterBean);

}
