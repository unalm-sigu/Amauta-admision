package pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.tramiteRetiroCiclo;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.aporte.Aporte;
import pe.edu.lamolina.model.aporte.AporteAlumnoCiclo;
import pe.edu.lamolina.model.aporte.AporteCiclo;
import pe.edu.lamolina.model.aporte.AporteSemestral;
import pe.edu.lamolina.model.aporte.GeneracionAportes;
import pe.edu.lamolina.model.aporte.ResumenAporteAlumno;
import pe.edu.lamolina.model.enums.AmbienteAplicacionEnum;
import pe.edu.lamolina.model.enums.AportesEnum;
import pe.edu.lamolina.model.enums.CuentaBancariaEnum;
import pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum;
import pe.edu.lamolina.model.enums.DeudaEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoAporteEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.GeneracionAportesEstadoEnum;
import pe.edu.lamolina.model.enums.NombreTablasEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.ParametrosSistemasEnum;
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
import pe.edu.lamolina.model.enums.TipoRetiroCicloEnum;
import pe.edu.lamolina.model.enums.TokenEstadoEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.finanzas.Acreencia;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
import pe.edu.lamolina.model.finanzas.DeudaAlumno;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Parametro;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.seguridad.Sistema;
import pe.edu.lamolina.model.seguridad.TokenIngresante;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.pivot.config.DespliegueConfig;
import pe.edu.lamolina.pivot.controller.academico.infoacademico.InfoAcademicoService;
import pe.edu.lamolina.pivot.controller.bienestar.alumnoAporte.AporteAlumnoService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSimultaneoDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.aporte.AporteAlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.aporte.AporteCicloDAO;
import pe.edu.lamolina.pivot.dao.aporte.AporteSemestralDAO;
import pe.edu.lamolina.pivot.dao.aporte.GeneracionAportesDAO;
import pe.edu.lamolina.pivot.dao.aporte.ResumenAporteAlumnoDAO;
import pe.edu.lamolina.pivot.dao.finanza.AcreenciaDAO;
import pe.edu.lamolina.pivot.dao.general.ParametroDAO;
import pe.edu.lamolina.pivot.dao.seguridad.TokenIngresanteDAO;
import pe.edu.lamolina.pivot.dao.tramite.RetiroCicloDAO;
import pe.edu.lamolina.pivot.dao.vacante.VacanteAlumnoDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.dao.finanza.DeudaAlumnoDAO;
import pe.edu.lamolina.pivot.dao.tramite.ReincorporacionDAO;

@Service
@Transactional(readOnly = true)
public class TramiteRetiroCicloServiceImp implements TramiteRetiroCicloService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    RetiroCicloDAO retiroCicloDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Autowired
    AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;

    @Autowired
    MatriculaSimultaneoDAO matriculaSimultaneoDAO;

    @Autowired
    VacanteAlumnoDAO vacanteAlumnoDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    ParametroDAO parametroDAO;

    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;

    @Autowired
    TokenIngresanteDAO tokenIngresanteDAO;

   

    @Autowired
    DespliegueConfig despliegueConfig;

    @Autowired
    AporteAlumnoService aporteAlumnoService;

    @Autowired
    InfoAcademicoService infoAcademicoService;

    @Autowired
    ResponseRestService responseRestService;

    @Override
    public List<CicloAcademico> allCiclos(CicloAcademico academico) {
        return cicloAcademicoDAO.allRegularPre(3, academico);
    }

    @Override
    public List<RetiroCiclo> allByCiclo(CicloAcademico cicloAcademico, DynatableFilter filter) {
        return retiroCicloDAO.allByCiclo(cicloAcademico, filter);
    }

    @Override
    @Transactional
    public void save(RetiroCiclo retiroCiclo, DataSessionPivot ds) {

        Alumno alumno = retiroCiclo.getAlumno();
        alumno = alumnoDAO.find(alumno);

        Boolean isCondicional = Arrays.asList(S_6, S_4).contains(alumno.getSituacionAcademica().getCodigoEnum());
        List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allByAlumnoDescRegular(retiroCiclo.getAlumno());
        AlumnoCiclo alumnoCiclo = alumnoCiclos.stream().filter(x -> Objects.equals(x.getCicloAcademico().getId(), retiroCiclo.getCicloAcademico().getId())).findAny().orElse(null);
        Assert.isNotNull(alumnoCiclo, "El alumno no tiene actividad en el ciclo " + retiroCiclo.getCicloAcademico().getDescripcion());

        RetiroCiclo retiro = new RetiroCiclo();
        retiro.setEstado(TramiteEstadoEnum.PEND);
        if (isCondicional) {
            retiro.setTipoEnum(TipoRetiroCicloEnum.EXCEP);
        } else {
            retiro.setTipoEnum(TipoRetiroCicloEnum.REG);
        }
        retiro.setAlumno(retiroCiclo.getAlumno());
        retiro.setCicloAcademico(retiroCiclo.getCicloAcademico());
        retiro.setCicloRegistro(ds.getCicloAcademico());
        retiro.setUsuario(ds.getUsuario());
        retiro.setMotivo(retiroCiclo.getMotivo());
        retiroCicloDAO.save(retiro);
        if (!isCondicional) {
            updateCursoApro(retiroCiclo);
        }
        aporteAlumnoService.generarAportes(alumno, ds.getCicloAcademico(), ds);
    }

    private void updateCursoApro(RetiroCiclo retiroCiclo) {

        List<AlumnoCursoCurricula> alumnoCursoCurriculas = alumnoCursoCurriculaDAO.allByAlumnoAprob(retiroCiclo.getAlumno(), retiroCiclo.getCicloAcademico());
        for (AlumnoCursoCurricula alumnoCursoCurricula : alumnoCursoCurriculas) {
            alumnoCursoCurricula.setEstadoEnum(CursoCurriculaEstadoEnum.LIMB);
            alumnoCursoCurriculaDAO.update(alumnoCursoCurricula);
        }
    }

    @Override
    @Transactional
    public MatriculaResumen update(RetiroCiclo retiroCiclo, DataSessionPivot ds) {
        RetiroCiclo retiroCiclobd = retiroCicloDAO.find(retiroCiclo.getId());
        retiroCiclobd.setEstado(TramiteEstadoEnum.valueOf(retiroCiclo.getEstado()));
        retiroCicloDAO.update(retiroCiclobd);
        MatriculaResumen matriculaResumen = new MatriculaResumen();
        if (retiroCiclobd.getEstadoEnum() == TramiteEstadoEnum.RCHZ) {
            Alumno alumno = retiroCiclobd.getAlumno();
            CicloAcademico cicloAcademico = ds.getCicloAcademico();

            matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, cicloAcademico);

            JsonResponse jsonResponse = responseRestService.updateRest(matriculaResumen, ds);

            Assert.isTrue(jsonResponse.getSuccess(), "Se produjo un error al eliminar la matrícula. Comuniquese con mesa de ayuda.");

            matriculaResumen.setCursosMatriculados(0);
            matriculaResumen.setCreditosMatriculados(0);
            matriculaResumen.setEstadoEnum(EstadoMatriculaEnum.INH);
            matriculaResumenDAO.update(matriculaResumen);

            alumno = alumnoDAO.find(alumno.getId());
            infoAcademicoService.cambiarPlan(alumno, alumno.getPlanCurricular(), ds);
            /*List<MatriculaCurso> matriculaCursos = matriculaCursoDAO.allByMatriculaResumen(matriculaResumen);
            List<Curso> cursos = matriculaCursos.stream().map(x -> x.getCurso()).collect(Collectors.toList());
            for (MatriculaCurso matriculaCurso : matriculaCursos) {
                matriculaCurso.setEstadoEnum(EstadoMatriculaEnum.RET);
                matriculaCursoDAO.update(matriculaCurso);
            }

            List<MatriculaSimultaneo> matriculaSimultaneos = matriculaSimultaneoDAO.allByMatriculaCurso(matriculaCursos);
            for (MatriculaSimultaneo matriculaSimultaneo : matriculaSimultaneos) {
                matriculaSimultaneoDAO.delete(matriculaSimultaneo);
            }

            List<MatriculaSeccion> matriculaSeccions = matriculaSeccionDAO.allByMatriculaResumen(matriculaResumen);
            for (MatriculaSeccion matriculaSeccion : matriculaSeccions) {
                matriculaSeccion.setEstadoEnum(EstadoMatriculaEnum.RET);
                matriculaSeccionDAO.update(matriculaSeccion);

                Seccion seccion = matriculaSeccion.getSeccion();
                if (cicloAcademico.getTipoEnum() == TipoCicloEnum.REG) {
                    seccion.setPrematriculados(seccion.getPrematriculados() - 1);
                }
                seccion.setMatriculados(seccion.getMatriculados() - 1);
                seccionDAO.update(seccion);

                VacanteAlumno vacanteAlumno = vacanteAlumnoDAO.allByAlumnoAndSeccion(alumno, seccion);
                vacanteAlumno.setEstadoEnum(EstadoVacanteAlumnoEnum.DISP);
                vacanteAlumno.setUserModificacion(null);
                vacanteAlumno.setFechaModificacion(null);
                vacanteAlumno.setAlumno(null);
                vacanteAlumnoDAO.update(vacanteAlumno);

            }
            for (Curso curso : cursos) {

                AlumnoCursoCurricula alumnoCursoCurricula = alumnoCursoCurriculaDAO.findByAlumnoCurso(alumno, curso);
                alumnoCursoCurricula.setEstadoMatriculaEnum(EstadoMatriculaEnum.RET);
                alumnoCursoCurriculaDAO.delete(alumnoCursoCurricula);
            }*/
            // Consultar si existe algun pago al matricularse.
        }
        return matriculaResumen;
    }

    @Override
    public Parametro findParametro() {

        return parametroDAO.findBySistemaAmbienteParametrosSistemas(new Sistema(despliegueConfig.getSistema()),
                AmbienteAplicacionEnum.valueOf(despliegueConfig.getAmbiente().toUpperCase()),
                ParametrosSistemasEnum.SALTO_PIVOT_MATRICULA);
    }

    @Override
    public List<Alumno> allAlumnoByNombre(String nombre, DataSessionPivot ds) {

        return alumnoDAO.allByName(nombre);
    }

    @Override
    @Transactional
    public void createToken(RetiroCiclo retiroCiclo, DataSessionPivot ds) {
        String valor = RandomStringUtils.randomAlphanumeric(45);
        TokenIngresante token = new TokenIngresante();
        token.setEstado(TokenEstadoEnum.ACT);
        token.setFechaRegistro(new Date());
        token.setFechaVencimiento(new DateTime().plusSeconds(5).toDate());
        token.setPersona(ds.getPersona());
        token.setValor(valor);
        token.setUserRegistro(ds.getUsuario());
        tokenIngresanteDAO.save(token);

    }

    
}
