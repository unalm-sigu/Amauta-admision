package pe.edu.lamolina.pivot.controller.consejeria.aconsejados;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.bean.AconsejadoEstadoBean;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.consejeria.AlumnoConsejeroDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
public class AconsejadoServiceImp implements AconsejadoService {

    @Autowired
    AlumnoConsejeroDAO alumnoConsejeroDAO;
    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Override
    public List<AlumnoConsejero> allAconsejadoByDynatableCarrera(DynatableFilter filter, CicloAcademico cicloAcademico) {
        List<MatriculaResumen> matriculaResumen = matriculaResumenDAO.allByCiclo(cicloAcademico);
        Map<Long, MatriculaResumen> alumnoResumen = TypesUtil.convertListToMap("alumno.id", matriculaResumen);
        List<AlumnoConsejero> alumnoConsejeros = alumnoConsejeroDAO.allByCarrera(filter);
        alumnoConsejeros.forEach(x -> {
            x.setEstadoMatriculableEnum(alumnoResumen.get(x.getAlumno().getId()).getEstadoEnum());
        });
        return alumnoConsejeros;
    }

    @Override
    @Transactional
    public void updateAlumnoConsejero(AlumnoConsejero alumnoConsejeroForm, DataSessionPivot ds) {
        AlumnoConsejero alumnoConsejero = alumnoConsejeroDAO.find(alumnoConsejeroForm.getId());
        alumnoConsejero.setConsejero(alumnoConsejeroForm.getConsejero());
        alumnoConsejero.setFechaAsigna(new Date());
        alumnoConsejeroDAO.update(alumnoConsejero);
    }

    @Override
    public AconsejadoEstadoBean allByCarrera(Carrera carrera, CicloAcademico cicloAcademico) {
        Long countSinConsejero = matriculaResumenDAO.allSinConsejero(carrera, cicloAcademico);
        Long countActivos = matriculaResumenDAO.allConConsejero(carrera, cicloAcademico);
        Long countConConsejeroNN = matriculaResumenDAO.allConConsejeroNN(carrera, cicloAcademico);

        AconsejadoEstadoBean aconsejadoEstadoBean = new AconsejadoEstadoBean();
        aconsejadoEstadoBean.setActivos(countActivos);
        aconsejadoEstadoBean.setSinConsejero(countConConsejeroNN);
        aconsejadoEstadoBean.setSinAsignar(countSinConsejero);
        return aconsejadoEstadoBean;
    }

}
