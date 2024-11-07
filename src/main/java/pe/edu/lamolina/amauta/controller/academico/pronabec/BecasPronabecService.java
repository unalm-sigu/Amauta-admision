package pe.edu.lamolina.amauta.controller.academico.pronabec;

import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.pronabec.InformacionBeca;
import pe.edu.lamolina.model.pronabec.TipoBeca;

import java.util.List;

public interface BecasPronabecService {
    List<InformacionBeca> allByDynatable(DynatableFilter filter);
    List<String> cargarBecados(MultipartFile file, DataSessionPivot ds);
    List<InformacionBeca> getHistorialBecas(InformacionBeca infoBeca);
    void saveBecado(InformacionBeca informacionBeca, DataSessionPivot ds);
    List<Persona>  allPersonaAlumno(String nombre, DataSessionPivot ds);
    void eliminarBecado(Long id);
    public void anularBecado(Long id);
    void actualizarInformBecado(InformacionBeca informacionBeca,DataSessionPivot ds);
    List<MatriculadosBecadosBean> allMatriculadosBecados(CicloAcademico cicloAcademico);
    List<BecadosFilterBean> filterBecadosExcel(CicloAcademico cicloAcademico,BecadosFilterBean becadosFilterBean);
    public List<CicloAcademico> allCicloRegular();
    List<BecadosFilterBean> filterActualBecados(CicloAcademico cicloAcademico, BecadosFilterBean becadosFilterBean);
    List<BecadosFilterBean> filterAnteriorBecados(CicloAcademico cicloAcademico, BecadosFilterBean becadosFilterBean);

}
