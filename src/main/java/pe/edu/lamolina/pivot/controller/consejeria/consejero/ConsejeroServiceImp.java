package pe.edu.lamolina.pivot.controller.consejeria.consejero;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.bean.AconsejadoEstadoBean;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.consejeria.AlumnoConsejeroDAO;

@Service
public class ConsejeroServiceImp implements ConsejeroService {

    @Autowired
    AlumnoConsejeroDAO alumnoConsejeroDAO;
    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Override
    public List<AlumnoConsejero> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico, Persona persona) {
        List<MatriculaResumen> matriculaResumen = matriculaResumenDAO.allByCiclo(cicloAcademico);
        Map<Long, MatriculaResumen> alumnoResumen = TypesUtil.convertListToMap("alumno.id", matriculaResumen);
        List<AlumnoConsejero> alumnoConsejeros = alumnoConsejeroDAO.allByPersona(filter, cicloAcademico, persona);
        alumnoConsejeros.forEach(x -> {
            x.setEstadoMatriculableEnum(alumnoResumen.get(x.getAlumno().getId()).getEstadoEnum());
        });
        return alumnoConsejeros;
    }

    @Override
    public AconsejadoEstadoBean allByPersona(Persona persona, CicloAcademico cicloAcademico) {
        Long countMatriculable = matriculaResumenDAO.countMatriculablesByConsejero(persona, cicloAcademico);
        Long countNoMatriculados = matriculaResumenDAO.countNoMatriculablesByConsejero(persona, cicloAcademico);
        Long countRetiroCiclo = matriculaResumenDAO.countRetiroCicloByConsejero(persona, cicloAcademico);
        AconsejadoEstadoBean aconsejadoEstadoBean = new AconsejadoEstadoBean();
//        aconsejadoEstadoBean.setMatriculados(countMatriculable);
//        aconsejadoEstadoBean.setNoMatriculados(countNoMatriculados);
//        aconsejadoEstadoBean.setRetiroCiclo(countRetiroCiclo);
        return aconsejadoEstadoBean;
    }

}
