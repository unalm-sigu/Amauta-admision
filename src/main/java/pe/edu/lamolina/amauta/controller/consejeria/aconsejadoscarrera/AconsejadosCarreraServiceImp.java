package pe.edu.lamolina.amauta.controller.consejeria.aconsejadoscarrera;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.controller.consejeria.consejeros.ConsejerosService;
import pe.edu.lamolina.amauta.controller.matricula.tutorsolicitud.TutorSolicitudService;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.ConsejeriaResumen;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoConsejeroDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ConsejeriaResumenDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.consejeria.Consejero;
import static pe.edu.lamolina.model.constantines.GlobalConstantine.ID_CONSEJERO_NN;
import pe.edu.lamolina.model.general.Persona;

@Service
@Transactional(readOnly = true)
public class AconsejadosCarreraServiceImp implements AconsejadosCarreraService {

    @Autowired
    AlumnoDAO alumnoDAO;
    @Autowired
    AlumnoConsejeroDAO alumnoConsejeroDAO;
    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;
    @Autowired
    ConsejeriaResumenDAO consejeriaResumenDAO;
    @Autowired
    VerificadorService verificadorService;
    @Autowired
    ConsejerosService consejeroService;
    @Autowired
    TutorSolicitudService tutorSolicitudservice;

    @Override
    public List<AlumnoConsejero> allAconsejadoByDynatable(Carrera carrera, DynatableFilter filter, CicloAcademico cicloAcademico) {

        List<AlumnoConsejero> aconsejadosCarrera = alumnoConsejeroDAO.allByDynatableCarrera(carrera, filter, cicloAcademico);

        List<Alumno> alumnos = aconsejadosCarrera.stream().map(x -> x.getAlumno())
                .collect(Collectors.toList());

        List<MatriculaResumen> matriculaResumen = matriculaResumenDAO.allByAlumnosCiclo(alumnos, cicloAcademico);

        Map<Long, MatriculaResumen> mapMatriculaResumen = TypesUtil.convertListToMap("alumno.id", matriculaResumen);

        for (AlumnoConsejero alumnoTutor : aconsejadosCarrera) {

            MatriculaResumen matResumen = mapMatriculaResumen.get(alumnoTutor.getAlumno().getId());

            alumnoTutor.setEstadoMatriculableEnum(EstadoMatriculaEnum.INH);

            if (matResumen != null) {

                alumnoTutor.setEstadoMatriculableEnum(matResumen.getEstadoEnum());
                alumnoTutor.setCursosMatriculados(matResumen.getCursosMatriculados());
                alumnoTutor.setCreditosMatriculados(matResumen.getCreditosMatriculados());

            }

        }

        return aconsejadosCarrera;
    }

    @Override
    @Transactional
    public void updateAlumnoConsejero(AlumnoConsejero alumnoConsejeroForm, DataSessionPivot ds) {
        AlumnoConsejero alumnoConsejero = alumnoConsejeroDAO.find(alumnoConsejeroForm.getId());
        alumnoConsejero.setConsejero(alumnoConsejeroForm.getConsejero());
        alumnoConsejero.setFechaAsigna(new Date());
        alumnoConsejeroDAO.update(alumnoConsejero);

        Alumno alumno = alumnoDAO.find(alumnoConsejero.getAlumno());
        alumno.setConsejero(alumnoConsejeroForm.getConsejero());
        alumnoDAO.updateColumns(alumno, "consejero");
    }

    @Override
    public ConsejeriaResumen getResumenByCarreraCiclo(Carrera carrera, CicloAcademico cicloAcademico) {
        ConsejeriaResumen resumen = new ConsejeriaResumen();
        String[] estadoFiltro={"conConsejero","sinConsejero","inhabilitado"};
        resumen.setAconsejadosActivos(Integer.parseInt(alumnoConsejeroDAO.countConsejeria(cicloAcademico,carrera,estadoFiltro[0])+""));
        resumen.setSinconsejeroActivos(Integer.parseInt(alumnoConsejeroDAO.countConsejeria(cicloAcademico,carrera,estadoFiltro[1])+""));
        resumen.setInhabilitados(Integer.parseInt(alumnoConsejeroDAO.countConsejeria(cicloAcademico,carrera,estadoFiltro[2])+""));
        return resumen;
    }

    @Override
    public boolean isRolCape(DataSessionPivot ds) {
        return verificadorService.isRolCape(ds);
    }

    @Override
    public boolean esInformaticoOERA(DataSessionPivot ds) {
        return verificadorService.esInformaticoOERA(ds);
    }

    @Override
    public boolean esAdministradorTutoria(DataSessionPivot ds) {
        return verificadorService.esAdministradorTutoria(ds);
    }

    @Override
    public List<Carrera> allCarreraByPersonaCiclo(Persona persona, CicloAcademico cicloAcademico) {
        return consejeroService.allCarreraByPersonaCiclo(persona, cicloAcademico);
    }

    @Override
    public void revisarConsejeria(Carrera carrera, CicloAcademico cicloAcademico, boolean b, DataSessionPivot ds) {
        consejeroService.revisarConsejeria(carrera, cicloAcademico, b, ds);
    }

    @Override
    public List<Consejero> allByCarrera(String nombre, Carrera carrera) {
        return consejeroService.allByCarrera(nombre, carrera);
    }

    @Override
    @Transactional
    public void solicitudBeneficio(AlumnoConsejero alumnoConsejero, DataSessionPivot ds) {
        tutorSolicitudservice.solicitudBeneficio(alumnoConsejero, ds);
    }

    @Override
    @Transactional
    public void eliminarAlumnoConsejero(Long idAlumnoConsejero) {
        alumnoConsejeroDAO.delete(idAlumnoConsejero);
    }

    @Override
    @Transactional
    public void quitarTutor(Long idAlumnoConsejero) {
        AlumnoConsejero alumnoConsejero = alumnoConsejeroDAO.findAll(idAlumnoConsejero);
        alumnoConsejero.setConsejero(new Consejero(ID_CONSEJERO_NN));
        alumnoConsejeroDAO.updateColumns(alumnoConsejero, "consejero");
    }

}
