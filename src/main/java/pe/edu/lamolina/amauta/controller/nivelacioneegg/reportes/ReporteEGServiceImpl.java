package pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.Bean.ResultadoNotaSeccion;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.NotaAlumnoNivelacionDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class ReporteEGServiceImpl implements ReporteEGService {

    private final NotaAlumnoNivelacionDAO notaAlumnoNivelacionDAO;

    @Override
    public List<ResultadoNotaSeccion> allGeneralNotaSeccionByCiclo(CicloAcademico cicloAcademico) {
        return notaAlumnoNivelacionDAO.allResultadoNotaSeccionByCicloAndSeccion(cicloAcademico, null);
    }

    @Override
    public List<ResultadoNotaSeccion> allBySeccionAndCiclo(CicloAcademico cicloAcademico, String seccion) {
        return notaAlumnoNivelacionDAO.allResultadoNotaSeccionByCicloAndSeccion(cicloAcademico, seccion);
    }
    
    

}
