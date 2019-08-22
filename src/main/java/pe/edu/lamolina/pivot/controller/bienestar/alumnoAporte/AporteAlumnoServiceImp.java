package pe.edu.lamolina.pivot.controller.bienestar.alumnoAporte;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.aporte.Aporte;
import pe.edu.lamolina.model.aporte.AporteAlumnoCiclo;
import pe.edu.lamolina.model.aporte.AporteCiclo;
import pe.edu.lamolina.model.aporte.AporteSemestral;
import pe.edu.lamolina.model.aporte.GeneracionAportes;
import pe.edu.lamolina.model.aporte.ResumenAporteAlumno;
import pe.edu.lamolina.model.enums.AportesEnum;
import pe.edu.lamolina.model.enums.EstadoAporteEnum;
import pe.edu.lamolina.model.enums.GeneracionAportesEstadoEnum;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_1;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_2;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_2U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_3;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_3U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_4;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_4U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_6;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_6U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_8;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_9;
import pe.edu.lamolina.model.tramite.Reincorporacion;
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
}
