package pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.Bean.ResultadoReporteView;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.AsistenciaNivelacionDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.NotaAlumnoNivelacionDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.nivelacioneegg.AsistenciaNivelacion;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class ReporteEGServiceImpl implements ReporteEGService {

    private final NotaAlumnoNivelacionDAO notaAlumnoNivelacionDAO;
    private final AsistenciaNivelacionDAO asistenciaNivelacionDAO;

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
        Map<String, List<AsistenciaNivelacion>> alumnosAsistencias = asistencias.stream().
                collect(Collectors.groupingBy(x -> x.getAlumnoNivelacion().getAlumno().getCodigo()));

        List<ResultadoReporteView> asistenciaSeccion = asistenciaNivelacionDAO.allByCicloAndSeccion(cicloAcademico, codSeccion);
        asistenciaSeccion.forEach(x -> {
            List<AsistenciaNivelacion> asistenciaAlumno = alumnosAsistencias.get(x.getMatricula());
            x.setAsistencias(asistenciaAlumno);
        });

        return asistenciaSeccion;
    }

}
