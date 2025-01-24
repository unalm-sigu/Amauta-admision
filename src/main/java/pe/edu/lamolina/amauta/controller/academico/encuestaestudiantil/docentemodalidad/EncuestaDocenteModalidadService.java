package pe.edu.lamolina.amauta.controller.academico.encuestaestudiantil.docentemodalidad;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.thymeleaf.context.Context;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocenteModalidad;
import pe.edu.lamolina.model.encuestaestudiantil.PuntajeEncuestaDocenteModalidad;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;

public interface EncuestaDocenteModalidadService {

    List<EncuestaDocenteModalidad> allByDynatableCicloAcademico(DynatableFilter filter, CicloAcademico ciclo, List<DepartamentoAcademico> departamentos, DataSessionPivot ds);

    void reporte(EncuestaDocenteModalidad encuestaDocenteModalidad, Model model);

    List<Context> reporteTodos(CicloAcademico cicloAcademico, ModalidadEstudioEnum modalidadEstudioEnum, List<DepartamentoAcademico> departamentos);

    List<Context> reporteUnicoDocenteMultipleCiclo(List<CicloAcademico> cicloAcademicos, ModalidadEstudioEnum modalidadEstudioEnum, List<DepartamentoAcademico> departamentos, Long idDocente);

    List<PuntajeEncuestaDocenteModalidad> resumenTemas(EncuestaDocenteModalidad encuestaDocenteModalidad);

    List<Facultad> allAccesoFacultades(DataSessionPivot ds, HttpServletRequest request, String codeRequest);

    List<DepartamentoAcademico> allAccesoDepartamentos(DataSessionPivot ds, List<Facultad> facultades, CicloAcademico ciclo, HttpServletRequest request, String codeRequest);

    List<CicloAcademico> allCicloAcademico();

}
