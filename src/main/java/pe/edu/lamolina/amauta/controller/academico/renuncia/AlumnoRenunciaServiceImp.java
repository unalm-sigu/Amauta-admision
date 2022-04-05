package pe.edu.lamolina.amauta.controller.academico.renuncia;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.inscripcion.PostulanteDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.SituacionAcademicaEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
public class AlumnoRenunciaServiceImp implements AlumnoRenunciaService {

    private final PostulanteDAO postulanteDAO;
    private final AlumnoDAO alumnoDAO;
    private final MatriculaResumenDAO matriculaResumenDAO;

    @Override
    public List<Postulante> allAlumnosbyDynatable(DynatableFilter filter) {
        return postulanteDAO.allByDynatableRenuncia(filter);
    }

    @Override
    @Transactional
    public void apply(Postulante postulanteForm, DataSessionPivot ds) {

        Postulante postulante = postulanteDAO.findById(postulanteForm.getId());
        log.debug("Aplicando renuncia a vacante a postulante {} id {}", postulante.getCodigo(), postulante.getId());

        Persona persona = postulante.getPersona();
        log.debug("Aplicando renuncia a vacante a persona {} id {}", persona.getNumeroDocIdentidad(), persona.getId());

        CicloAcademico cicloAcademico = postulante.getCicloPostula().getCicloAcademico();
        log.debug("Aplicando en el ciclo  {} id {}", cicloAcademico.getCodigo(), cicloAcademico.getId());

        Alumno alumno = alumnoDAO.findByPersona(persona, cicloAcademico);
        if (alumno == null) {
            log.debug("No existe registro de alumno para el postulante {} id {}", postulante.getCodigo(), postulante.getId());
            return;
        }

        log.debug("Aplicando renuncia a vacante a alumno {} id {}", alumno.getCodigo(), alumno.getId());

        alumno.setSituacionAcademica(new SituacionAcademica(SituacionAcademicaEnum.S_R));

        alumnoDAO.updateColumns(alumno, "situacionAcademica");

        MatriculaResumen matriculaResumenActivo = matriculaResumenDAO.findByAlumnoCiclo(alumno, cicloAcademico);
        if (matriculaResumenActivo == null) {
            log.debug("No existe registro de matricula para el postulante {} id {}", postulante.getCodigo(), postulante.getId());
            return;
        }

        log.debug("Aplicando renuncia a vacante a matricula id {}", matriculaResumenActivo.getId());

        matriculaResumenActivo.setEstadoEnum(EstadoMatriculaEnum.INH);

        matriculaResumenDAO.updateColumns(matriculaResumenActivo, "estado");

    }

}
