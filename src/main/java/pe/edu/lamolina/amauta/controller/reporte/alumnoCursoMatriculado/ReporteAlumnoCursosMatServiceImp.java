package pe.edu.lamolina.amauta.controller.reporte.alumnoCursoMatriculado;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.SeccionDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;

@Service
@Transactional(readOnly = true)
public class ReporteAlumnoCursosMatServiceImp implements ReporteAlumnoCursosMatService {

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Override
    public List<MatriculaSeccion> downloadReporte(String seccionCode, DataSessionPivot ds) {
        CicloAcademico cicloAcademico = ds.getCicloAcademico();
        Seccion seccion = seccionDAO.findByCode2Ciclo(seccionCode, cicloAcademico);

        List<MatriculaSeccion> lista = matriculaSeccionDAO.allByReporte(seccionCode, cicloAcademico);
        List<DocenteSeccion> docenteSeccions = docenteSeccionDAO.allBySeccion(seccion);
        Map<Long, List<DocenteSeccion>> mapDocente = TypesUtil.convertListToMapList("seccion.id", docenteSeccions);

        for (MatriculaSeccion matriculaSeccion : lista) {
            matriculaSeccion.getSeccion().setDocenteSeccion(mapDocente.get(matriculaSeccion.getSeccion().getId()));
        }
        return lista;
    }

    @Override
    public List<AlumnoPersonalizadoDTO> downloadReporteAlumnoPersonalizado() {
        return matriculaSeccionDAO.listaPersonalizadoReporte();

    }

}
