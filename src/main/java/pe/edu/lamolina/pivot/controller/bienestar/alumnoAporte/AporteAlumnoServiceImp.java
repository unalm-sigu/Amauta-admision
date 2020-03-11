package pe.edu.lamolina.pivot.controller.bienestar.alumnoAporte;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.aporte.GeneracionAportes;
import pe.edu.lamolina.model.enums.GeneracionAportesEstadoEnum;
import pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.tramiteRetiroCiclo.ResponseRestService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.aporte.AporteAlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.aporte.AporteCicloDAO;
import pe.edu.lamolina.pivot.dao.aporte.AporteSemestralDAO;
import pe.edu.lamolina.pivot.dao.aporte.GeneracionAportesDAO;
import pe.edu.lamolina.pivot.dao.aporte.ResumenAporteAlumnoDAO;
import pe.edu.lamolina.pivot.dao.finanza.AcreenciaDAO;
import pe.edu.lamolina.pivot.dao.finanza.DeudaAlumnoDAO;
import pe.edu.lamolina.pivot.dao.tramite.ReincorporacionDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class AporteAlumnoServiceImp implements AporteAlumnoService {

    @Autowired
    GeneracionAportesDAO generacionAportesDAO;

    @Autowired
    ResumenAporteAlumnoDAO resumenAporteAlumnoDAO;

    @Autowired
    AporteAlumnoCicloDAO aporteAlumnoCicloDAO;

    @Autowired
    ReincorporacionDAO reincorporacionDAO;

    @Autowired
    AporteCicloDAO aporteCicloDAO;

    @Autowired
    DeudaAlumnoDAO deudaAlumnoDAO;

    @Autowired
    AporteSemestralDAO aporteSemestralDAO;

    @Autowired
    AcreenciaDAO acreenciaDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    ResponseRestService responseRestService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void generarAportes(Alumno alumno, CicloAcademico ciclo, MatriculaResumen matriculaResumen, DataSessionPivot ds) {
        responseRestService.createToken(ds);
        GeneracionAportes generador = generacionAportesDAO.findByCicloAcademico(ciclo);
        if (generador == null) {
            return;
        }
        if (!Arrays.asList(GeneracionAportesEstadoEnum.BOL, GeneracionAportesEstadoEnum.GEN).contains(generador.getEstadoEnum())) {
            return;
        }

        JsonResponse jsonResponse = responseRestService.generarAporte(alumno, ciclo, matriculaResumen, ds);

        Assert.isTrue(jsonResponse.getSuccess(), "Se produjo un error al agregar aportes. Comuniquese con mesa de ayuda.");

    }

    @Override
    public void generarAporteCarnet(CicloAcademico cicloAcademico, MatriculaResumen matriculaResumen, DataSessionPivot ds) {
        responseRestService.createToken(ds);
        GeneracionAportes generador = generacionAportesDAO.findByCicloAcademico(cicloAcademico);
        if (generador == null) {
            return;
        }
        if (!Arrays.asList(GeneracionAportesEstadoEnum.BOL, GeneracionAportesEstadoEnum.GEN)
                .contains(generador.getEstadoEnum())) {
            return;
        }

        JsonResponse jsonResponse = responseRestService.generarAporteCarnet(matriculaResumen, ds);

        Assert.isTrue(jsonResponse.getSuccess(), "Se produjo un error al agregar aportes. Comuniquese con mesa de ayuda.");
    }

    @Override
    public void generarAporteSegundaCarrera(CicloAcademico cicloAcademico, MatriculaResumen matriculaResumen, DataSessionPivot ds) {
        responseRestService.createToken(ds);
        GeneracionAportes generador = generacionAportesDAO.findByCicloAcademico(cicloAcademico);
        if (generador == null) {
            return;
        }
        if (!Arrays.asList(GeneracionAportesEstadoEnum.BOL, GeneracionAportesEstadoEnum.GEN)
                .contains(generador.getEstadoEnum())) {
            return;
        }

        JsonResponse jsonResponse = responseRestService.generarAporteSegundaCarrera(matriculaResumen, ds);

        Assert.isTrue(jsonResponse.getSuccess(), "Se produjo un error al agregar aportes. Comuniquese con mesa de ayuda.");
    }

    @Override
    public void quitarAporteCarnet(CicloAcademico cicloAcademico, MatriculaResumen matriculaResumen, DataSessionPivot ds) {
        responseRestService.createToken(ds);
        GeneracionAportes generador = generacionAportesDAO.findByCicloAcademico(cicloAcademico);
        if (generador == null) {
            return;
        }
        if (!Arrays.asList(GeneracionAportesEstadoEnum.BOL, GeneracionAportesEstadoEnum.GEN).contains(generador.getEstadoEnum())) {
            return;
        }

        JsonResponse jsonResponse = responseRestService.eliminarAporteCarnet(matriculaResumen, ds);

        Assert.isTrue(jsonResponse.getSuccess(), "Se produjo un error al agregar aportes. Comuniquese con mesa de ayuda.");
    }

    @Override
    public void quitarAporteDuplicadoCarnet(CicloAcademico cicloAcademico, MatriculaResumen matriculaResumen, DataSessionPivot ds) {
        responseRestService.createToken(ds);
        GeneracionAportes generador = generacionAportesDAO.findByCicloAcademico(cicloAcademico);
        if (generador == null) {
            return;
        }
        if (!Arrays.asList(GeneracionAportesEstadoEnum.BOL, GeneracionAportesEstadoEnum.GEN).contains(generador.getEstadoEnum())) {
            return;
        }

        JsonResponse jsonResponse = responseRestService.eliminarAporteDuplicadoCarnet(matriculaResumen, ds);

        Assert.isTrue(jsonResponse.getSuccess(), "Se produjo un error al agregar aportes. Comuniquese con mesa de ayuda.");
    }

    @Override
    public void generarAporteDuplicadoCarnet(CicloAcademico cicloAcademico, MatriculaResumen matriculaResumen, DataSessionPivot ds) {
        responseRestService.createToken(ds);
        GeneracionAportes generador = generacionAportesDAO.findByCicloAcademico(cicloAcademico);
        if (generador == null) {
            return;
        }
        if (!Arrays.asList(GeneracionAportesEstadoEnum.BOL, GeneracionAportesEstadoEnum.GEN)
                .contains(generador.getEstadoEnum())) {
            return;
        }

        JsonResponse jsonResponse = responseRestService.generarAporteDuplicadoCarnet(matriculaResumen, ds);

        Assert.isTrue(jsonResponse.getSuccess(), "Se produjo un error al agregar aportes. Comuniquese con mesa de ayuda.");
    }

}
