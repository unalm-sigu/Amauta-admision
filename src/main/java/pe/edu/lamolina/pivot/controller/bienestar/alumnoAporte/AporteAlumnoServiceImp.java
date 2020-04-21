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
import pe.edu.lamolina.model.aporte.Aporte;
import pe.edu.lamolina.model.aporte.GeneracionAportes;
import pe.edu.lamolina.model.enums.GeneracionAportesEstadoEnum;
import pe.edu.lamolina.model.seguridad.TokenIngresante;
import pe.edu.lamolina.pivot.controller.responserest.ResponseRestService;
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
        GeneracionAportes generador = generacionAportesDAO.findByCicloAcademico(ciclo);
        if (generador == null) {
            return;
        }
        if (!Arrays.asList(GeneracionAportesEstadoEnum.BOL, GeneracionAportesEstadoEnum.GEN).contains(generador.getEstadoEnum())) {
            return;
        }

        TokenIngresante token = responseRestService.createToken(ds);
        JsonResponse jsonResponse = responseRestService.generarAporte(alumno, ciclo, matriculaResumen, ds, token);
        Assert.isTrue(jsonResponse.getSuccess(), jsonResponse.getMessage());

    }

    @Override
    public void generarAporteCarnet(CicloAcademico cicloAcademico, MatriculaResumen matriculaResumen, DataSessionPivot ds) {
        GeneracionAportes generador = generacionAportesDAO.findByCicloAcademico(cicloAcademico);
        if (generador == null) {
            return;
        }
        if (!Arrays.asList(GeneracionAportesEstadoEnum.BOL, GeneracionAportesEstadoEnum.GEN)
                .contains(generador.getEstadoEnum())) {
            return;
        }

        TokenIngresante token = responseRestService.createToken(ds);
        JsonResponse jsonResponse = responseRestService.generarAporteCarnet(matriculaResumen, ds, token);
        Assert.isTrue(jsonResponse.getSuccess(), jsonResponse.getMessage());
    }

    @Override
    public void generarAporteSegundaCarrera(CicloAcademico cicloAcademico, MatriculaResumen matriculaResumen, DataSessionPivot ds) {
        GeneracionAportes generador = generacionAportesDAO.findByCicloAcademico(cicloAcademico);
        if (generador == null) {
            return;
        }
        if (!Arrays.asList(GeneracionAportesEstadoEnum.BOL, GeneracionAportesEstadoEnum.GEN)
                .contains(generador.getEstadoEnum())) {
            return;
        }

        TokenIngresante token = responseRestService.createToken(ds);
        JsonResponse jsonResponse = responseRestService.generarAporteSegundaCarrera(matriculaResumen, ds, token);
        Assert.isTrue(jsonResponse.getSuccess(), jsonResponse.getMessage());
    }

    @Override
    public void quitarAporteCarnet(CicloAcademico cicloAcademico, MatriculaResumen matriculaResumen, DataSessionPivot ds) {
        GeneracionAportes generador = generacionAportesDAO.findByCicloAcademico(cicloAcademico);
        if (generador == null) {
            return;
        }
        if (!Arrays.asList(GeneracionAportesEstadoEnum.BOL, GeneracionAportesEstadoEnum.GEN).contains(generador.getEstadoEnum())) {
            return;
        }

        TokenIngresante token = responseRestService.createToken(ds);
        JsonResponse jsonResponse = responseRestService.eliminarAporteCarnet(matriculaResumen, ds, token);
        Assert.isTrue(jsonResponse.getSuccess(), jsonResponse.getMessage());
    }

    @Override
    public void quitarAporteDuplicadoCarnet(CicloAcademico cicloAcademico, MatriculaResumen matriculaResumen, DataSessionPivot ds) {
        GeneracionAportes generador = generacionAportesDAO.findByCicloAcademico(cicloAcademico);
        if (generador == null) {
            return;
        }
        if (!Arrays.asList(GeneracionAportesEstadoEnum.BOL, GeneracionAportesEstadoEnum.GEN).contains(generador.getEstadoEnum())) {
            return;
        }

        TokenIngresante token = responseRestService.createToken(ds);
        JsonResponse jsonResponse = responseRestService.eliminarAporteDuplicadoCarnet(matriculaResumen, ds, token);
        Assert.isTrue(jsonResponse.getSuccess(), jsonResponse.getMessage());

    }

    @Override
    public void generarAporteDuplicadoCarnet(CicloAcademico cicloAcademico, MatriculaResumen matriculaResumen, DataSessionPivot ds) {
        GeneracionAportes generador = generacionAportesDAO.findByCicloAcademico(cicloAcademico);
        if (generador == null) {
            return;
        }
        if (!Arrays.asList(GeneracionAportesEstadoEnum.BOL, GeneracionAportesEstadoEnum.GEN)
                .contains(generador.getEstadoEnum())) {
            return;
        }

        TokenIngresante token = responseRestService.createToken(ds);
        JsonResponse jsonResponse = responseRestService.generarAporteDuplicadoCarnet(matriculaResumen, ds, token);
        Assert.isTrue(jsonResponse.getSuccess(), jsonResponse.getMessage());
    }

    @Override
    public JsonResponse getModificarAporte(CicloAcademico cicloAcademico, MatriculaResumen matriculaResumen, Aporte aporte, DataSessionPivot ds) {
        GeneracionAportes generador = generacionAportesDAO.findByCicloAcademico(cicloAcademico);
        if (generador == null) {
            return null;
        }
        if (!Arrays.asList(GeneracionAportesEstadoEnum.BOL, GeneracionAportesEstadoEnum.GEN).contains(generador.getEstadoEnum())) {
            return null;
        }

        TokenIngresante token = responseRestService.createToken(ds);
        return responseRestService.modificarAporte(matriculaResumen, ds, aporte, token);
    }

    @Override
    public JsonResponse getEliminarAporte(CicloAcademico cicloAcademico, MatriculaResumen matriculaResumen, Aporte aporte, DataSessionPivot ds) {
        GeneracionAportes generador = generacionAportesDAO.findByCicloAcademico(cicloAcademico);
        if (generador == null) {
            return null;
        }
        if (!Arrays.asList(GeneracionAportesEstadoEnum.BOL, GeneracionAportesEstadoEnum.GEN)
                .contains(generador.getEstadoEnum())) {
            return null;
        }

        TokenIngresante token = responseRestService.createToken(ds);
        return responseRestService.eliminarAporte(matriculaResumen, ds, aporte, token);
    }

    @Override
    public void modificarAporte(CicloAcademico cicloAcademico, MatriculaResumen matriculaResumen, Aporte aporte, DataSessionPivot ds) {
        JsonResponse jsonResponse = getModificarAporte(cicloAcademico, matriculaResumen, aporte, ds);
        Assert.isTrue(jsonResponse.getSuccess(), jsonResponse.getMessage());
    }

    @Override
    public void eliminarAporte(CicloAcademico cicloAcademico, MatriculaResumen matriculaResumen, Aporte aporte, DataSessionPivot ds) {
        JsonResponse jsonResponse = getEliminarAporte(cicloAcademico, matriculaResumen, aporte, ds);
        Assert.isTrue(jsonResponse.getSuccess(), jsonResponse.getMessage());
    }

}
