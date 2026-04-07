package pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.Bean.*;
import pe.edu.lamolina.amauta.dao.academico.CarreraDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.AsistenciaNivelacionDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.NotaAlumnoNivelacionDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.nivelacioneegg.AsistenciaNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.TemaAsistencia;

import static pe.edu.lamolina.model.enums.dictadoclases.AsistenciaClasesEstadoEnum.ASISTIO;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class ReporteEGServiceImpl implements ReporteEGService {

    private final NotaAlumnoNivelacionDAO notaAlumnoNivelacionDAO;
    private final AsistenciaNivelacionDAO asistenciaNivelacionDAO;
    private final CarreraDAO carreraDAO;

    @Override
    public List<ResultadoReporteView> allNotasGeneralByCiclo(CicloAcademico cicloAcademico) {
        return notaAlumnoNivelacionDAO.allResultadoNotaSeccionByCicloAndSeccion(cicloAcademico, null);
    }

    @Override
    public List<ResultadoReporteView> allNotasBySeccionAndCiclo(CicloAcademico cicloAcademico, String seccion) {
        return notaAlumnoNivelacionDAO.allResultadoNotaSeccionByCicloAndSeccion(cicloAcademico, seccion);
    }

    @Override
    public List<ResultadoReporteView> allAsistenciaBySeccionAndCiclo(CicloAcademico cicloAcademico, String codSeccion) {
        List<AsistenciaNivelacion> asistencias = asistenciaNivelacionDAO.allByCicloSeccion(cicloAcademico, codSeccion);
        Map<String, List<AsistenciaNivelacion>> alumnosAsistencias = asistencias.stream()
                .collect(Collectors.groupingBy(asiste -> asiste.getAlumnoNivelacion().getAlumno().getCodigo()));

        List<ResultadoReporteView> asistenciaSeccion = asistenciaNivelacionDAO.allByCicloAndSeccion(cicloAcademico, codSeccion);
        asistenciaSeccion.forEach(asiste -> {
            List<AsistenciaNivelacion> asistenciaAlumno = alumnosAsistencias.get(asiste.getMatricula());
            List<TemaAsistencia> temasAsistencia = asistenciaAlumno.stream()
                    .filter(asisteAlu -> asisteAlu.getEstadoEnum() == ASISTIO)
                    .map(asisteAlu -> asisteAlu.getTemaAsistencia())
                    .distinct().collect(Collectors.toList());
            asiste.setAsistencias(temasAsistencia);
        });

        return asistenciaSeccion;
    }

    @Override
    public List<ResultadoReporteView> allIngresantesDesaprobadosByCiclo(CicloAcademico cicloAcademico) {
        return notaAlumnoNivelacionDAO.allIngresantesDesaprobadosByCiclo(cicloAcademico);
    }

    @Override
    public List<ResultadoReporteView> ingresantesDesaprobadosMoodleByCiclo(CicloAcademico cicloAcademico) {
        return notaAlumnoNivelacionDAO.ingresantesDesaprobadosMoodleByCiclo(cicloAcademico);
    }

    @Override
    public ResultadoReporteView allDataProcesada(CicloAcademico cicloAcademico) {
        List<IngresantesExamenAdmisionDTO> examenesAdmision = notaAlumnoNivelacionDAO.allExamenAdmisionByCiclo(cicloAcademico);
        List<IngresantesInscritosNivelacionDTO> inscritosNivelacion = notaAlumnoNivelacionDAO.allInscritosNivelacionByCiclo(cicloAcademico);
        List<IngresantesMateriasNivelacionDTO> materiasNivelacion = notaAlumnoNivelacionDAO.allMateriasNivelacion(cicloAcademico);
        List<IngresantesAsistenciaInscritosDTO> asistencias = notaAlumnoNivelacionDAO.allAsistenciasByCiclo(cicloAcademico);

        ResultadoReporteView reporteView = new ResultadoReporteView();
        reporteView.setIngresantesExamene(examenesAdmision);
        reporteView.setIngresantesInscritos(inscritosNivelacion);
        reporteView.setIngresantesMateria(materiasNivelacion);
        reporteView.setIngresantesAsistencia(asistencias);

        return reporteView;

    }

    @Override
    public List<ResultadoReporteView> resultadoAdmisionByCiclo(CicloAcademico cicloAcademico) {
        return notaAlumnoNivelacionDAO.resultadoAdmisionByCiclo(cicloAcademico);
    }

    @Override
    public List<ResultadoReporteView> ingresantesGeneraByCiclol(CicloAcademico cicloAcademico) {
        return notaAlumnoNivelacionDAO.ingresantesGeneraByCiclo(cicloAcademico);
    }

    @Override
    public List<ResultadoReporteView> cursoNivelacionFormadoByCiclo(CicloAcademico cicloAcademico) {
        return notaAlumnoNivelacionDAO.cursoNivelacionFormadoByCiclo(cicloAcademico);
    }

    @Override
    public ResultadoReporteView informeNivelacionByCarrera(CicloAcademico cicloAcademico, Long idCarrera) {

        List<IngresantesNivelacionCarreraDTO> ingresantesNivelacionCarrera = notaAlumnoNivelacionDAO.allIngresantesNivelacionByCicloCarrera(cicloAcademico, idCarrera);
        List<IngresantesInscritosNivelacionDTO> inscritosNivelacion = notaAlumnoNivelacionDAO.allInscritosNivelacionByCicloAndCarrera(cicloAcademico, idCarrera);
        List<IngresantesAsistenciaInscritosDTO> asistencias = notaAlumnoNivelacionDAO.allAsistenciasByCicloCarrera(cicloAcademico, idCarrera);

        ResultadoReporteView resultadoReporteView = new ResultadoReporteView();
        resultadoReporteView.setCarrera(ingresantesNivelacionCarrera.isEmpty() ? "" : ingresantesNivelacionCarrera.get(0).getCarrera());
        resultadoReporteView.setFacultad(ingresantesNivelacionCarrera.isEmpty() ? "" : ingresantesNivelacionCarrera.get(0).getFacultad());
        resultadoReporteView.setIngresantesNivelacionCarrera(ingresantesNivelacionCarrera);
        resultadoReporteView.setIngresantesInscritos(inscritosNivelacion);
        resultadoReporteView.setIngresantesAsistencia(asistencias);

        return resultadoReporteView;
    }

    @Override
    public List<Carrera> allCarrera() {
        return carreraDAO.allActivasByModalidadEnum(ModalidadEstudioEnum.PRE);
    }
}
