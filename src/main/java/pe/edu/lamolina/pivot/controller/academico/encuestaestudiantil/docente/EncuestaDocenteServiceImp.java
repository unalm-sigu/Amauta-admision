package pe.edu.lamolina.pivot.controller.academico.encuestaestudiantil.docente;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ListsInspector;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.encuestaestudiantil.ConfiguraEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaAlumno;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.encuestaestudiantil.PeriodoEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.PuntajeEncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.RespuestaEncuestaAlumno;
import pe.edu.lamolina.model.encuestaestudiantil.ResumenEncuestaDocente;
import pe.edu.lamolina.model.enums.EncuestaEstadoEnum;
import pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.TipoExamenVirtual;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.encuesta.ConfiguraEncuestaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.CursoSinEncuestaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaAlumnoDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaDocenteDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaEstudiantilDAO;
import pe.edu.lamolina.pivot.dao.encuesta.ExamenVirtualDAO;
import pe.edu.lamolina.pivot.dao.encuesta.PeriodoEncuestaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.PuntajeEncuestaDocenteDAO;
import pe.edu.lamolina.pivot.dao.encuesta.RespuestaEncuestaAlumnoDAO;
import pe.edu.lamolina.pivot.dao.encuesta.ResumenEncuestaDocenteDAO;
import pe.edu.lamolina.pivot.dao.encuesta.TipoExamenVirtualDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class EncuestaDocenteServiceImp implements EncuestaDocenteService {

    @Autowired
    EncuestaDocenteDAO encuestaDocenteDAO;
    @Autowired
    EncuestaEstudiantilDAO encuestaEstudiantilDAO;
    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;
    @Autowired
    EncuestaAlumnoDAO encuestaAlumnoDAO;
    @Autowired
    ExamenVirtualDAO examenVirtualDAO;
    @Autowired
    TipoExamenVirtualDAO tipoExamenVirtualDAO;
    @Autowired
    ConfiguraEncuestaDAO configuraEncuestaDAO;
    @Autowired
    PeriodoEncuestaDAO periodoEncuestaDAO;
    @Autowired
    CursoSinEncuestaDAO cursoSinEncuestaDAO;
    @Autowired
    VisorEncuestaDocente visorEncuestaDocente;
    @Autowired
    GeneradorEncuestaDocenteService generadorEncuestaDocenteService;
    @Autowired
    ResumenEncuestaDocenteDAO resumenEncuestaDocenteDAO;
    @Autowired
    RespuestaEncuestaAlumnoDAO respuestaEncuestaAlumnoDAO;
    @Autowired
    PuntajeEncuestaDocenteDAO puntajeEncuestaDocenteDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public EncuestaEstudiantil findEncuestaDocente(CicloAcademico cicloAcademico) {
        TipoExamenVirtual tipoEncuesta = tipoExamenVirtualDAO.findByEnum(TipoExamenVirtualEnum.ENC_DOC);
        ExamenVirtual encuestaModelo = examenVirtualDAO.findEncuestaActivaByTipo(tipoEncuesta);
        EncuestaEstudiantil encuesta = null;
        if (encuestaModelo != null) {
            encuesta = encuestaEstudiantilDAO.findByCicloEncuesta(cicloAcademico, encuestaModelo);
        }
        if (encuesta == null) {
            encuesta = new EncuestaEstudiantil();
            encuesta.setEstadoEnum(EncuestaEstadoEnum.NCRE);
        }

        encuesta.setPeriodosEncuesta(new ArrayList());
        encuesta.setConfiguraEncuesta(new ArrayList());
        encuesta.setCursosNoEncuestar(new ArrayList());
        if (encuesta.getId() != null) {
            encuesta.setPeriodosEncuesta(periodoEncuestaDAO.allByEncuesta(encuesta));
            ConfiguraEncuesta cfg = configuraEncuestaDAO.findByEncuesta(encuesta);
            if (cfg != null) {
                encuesta.getConfiguraEncuesta().add(cfg);
            }
            encuesta.setCursosNoEncuestar(cursoSinEncuestaDAO.allByEncuestaEstudiantil(encuesta));
        }

        return encuesta;
    }

    @Override
    public List<EncuestaDocente> allEncuestaDocente(DynatableFilter filter, CicloAcademico ciclo) {
        List<EncuestaDocente> encuestas = encuestaDocenteDAO.allByDynatable(filter, ciclo);
        List<Seccion> secciones = new ArrayList();
        for (EncuestaDocente encuesta : encuestas) {
            Seccion seccion = encuesta.getDocenteSeccion().getSeccion();
            secciones.add(seccion);
        }
        List<DocenteSeccion> profesSecciones = docenteSeccionDAO.allPersonasActivasBySecciones(secciones);
        Map<Long, List<DocenteSeccion>> mapProfesBySeccion = TypesUtil.convertListToMapList("seccion.id", profesSecciones);
        for (EncuestaDocente encuesta : encuestas) {
            Seccion seccion = encuesta.getDocenteSeccion().getSeccion();
            List<DocenteSeccion> profesSecc = mapProfesBySeccion.get(seccion.getId());
            profesSecc = (profesSecc == null) ? new ArrayList() : profesSecc;
            seccion.setDocenteSeccion(profesSecc);
        }

        return encuestas;
    }

    @Override
    @Transactional
    public void activarEncuesta(CicloAcademico cicloAcademico, DataSessionPivot ds) {
        TipoExamenVirtual tipoEncuesta = tipoExamenVirtualDAO.findByEnum(TipoExamenVirtualEnum.ENC_DOC);
        ExamenVirtual encuestaModelo = examenVirtualDAO.findEncuestaActivaByTipo(tipoEncuesta);
        EncuestaEstudiantil encuesta = encuestaEstudiantilDAO.findByCicloEncuesta(cicloAcademico, encuestaModelo);
        Assert.isTrue(encuesta == null, "Ya se activó la encuesta en este ciclo");

        encuesta = new EncuestaEstudiantil();
        encuesta.setCicloAcademico(cicloAcademico);
        encuesta.setEncuesta(encuestaModelo);
        encuesta.setEstadoEnum(EncuestaEstadoEnum.CRE);
        encuesta.setEncuestasEjecutadas(0);
        encuesta.setEncuestasProgramadas(0);
        encuesta.setObjetivosEncuesta(0);
        encuesta.setObjetivosEncuestados(0);
        encuesta.setUserRegistro(ds.getUsuario());
        encuesta.setFechaRegistro(new Date());
        encuestaEstudiantilDAO.save(encuesta);
    }

    @Override
    public String generarEncuesta(CicloAcademico cicloAcademico, DataSessionPivot ds) {
        if (visorEncuestaDocente.iniciar()) {
            TipoExamenVirtual tipoEncuesta = tipoExamenVirtualDAO.findByEnum(TipoExamenVirtualEnum.ENC_DOC);
            ExamenVirtual encuestaModelo = examenVirtualDAO.findEncuestaActivaByTipo(tipoEncuesta);
            EncuestaEstudiantil encuesta = encuestaEstudiantilDAO.findByCicloEncuesta(cicloAcademico, encuestaModelo);

            Assert.isTrue(encuesta != null, "Aun no se ha activado la encuesta para este ciclo");
            Assert.isFalse(encuesta.getEstadoEnum() == EncuestaEstadoEnum.ACT, "Las encuestas ya fueron generadas");
            Assert.isFalse(encuesta.getEstadoEnum() == EncuestaEstadoEnum.CER, "Las encuestas ya fueron cerradas");

            ConfiguraEncuesta configuraEncuesta = configuraEncuestaDAO.findByEncuesta(encuesta);
            Assert.isFalse(configuraEncuesta == null, "Falta configurar la encuesta");

            generadorEncuestaDocenteService.generarEncuesta(cicloAcademico, ds);
            return null;
        } else {
            return "No se puede iniciar la generación de encuestas de docentes";
        }

    }

    @Override
    @Transactional
    public void cambiarEstadoEncuesta(EncuestaDocente encuestaForm) {
        EncuestaDocente encuesta = encuestaDocenteDAO.findEncuestaDocente(encuestaForm);
        if (Strings.isNullOrEmpty(encuesta.getEstado())) {
            encuesta.setEstadoEnum(EncuestaEstudiantilEstadoEnum.INA);
        }
        if (encuesta.getEstadoEnum() == EncuestaEstudiantilEstadoEnum.ACT
                || encuesta.getEstadoEnum() == EncuestaEstudiantilEstadoEnum.TEO) {
            encuesta.setEstadoEnum(EncuestaEstudiantilEstadoEnum.INA);
            List<EncuestaAlumno> encuestas = encuestaAlumnoDAO.allByEncuestaDocente(encuesta);
            for (EncuestaAlumno encuestaAlumno : encuestas) {
                encuestaAlumno.setEstadoEnum(EncuestaEstudiantilEstadoEnum.INA);
                encuestaAlumnoDAO.update(encuestaAlumno);
            }
            return;
        }
        encuesta.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
        List<EncuestaAlumno> encuestas = encuestaAlumnoDAO.allByEncuestaDocente(encuesta);
        if (encuestas.size() < 1) {
            throw new PhobosException("No existe encuestas para el docente ");
        }
        for (EncuestaAlumno encuestaAlumno : encuestas) {
            encuestaAlumno.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
            encuestaAlumnoDAO.update(encuestaAlumno);
        }
    }

    private void updateConfigEncuesta(EncuestaEstudiantil encuesta, ConfiguraEncuesta configuraEncuestaForm, CicloAcademico ciclo, DataSessionPivot ds) {

        ConfiguraEncuesta configuraEncuestaBD = configuraEncuestaDAO.findByEncuesta(encuesta);
        if (configuraEncuestaBD == null) {
            configuraEncuestaForm.setEncuestaEstudiantil(encuesta);
            configuraEncuestaForm.setFechaRegistro(new Date());
            configuraEncuestaForm.setUserRegistro(ds.getUsuario());
            configuraEncuestaForm.setEncuestaTeoriaPractica(configuraEncuestaForm.getEncuestaTeoriaPractica() == null ? 0L : 1L);
            configuraEncuestaDAO.save(configuraEncuestaForm);
        } else {
            configuraEncuestaBD.setCantidadMaximaDocentes(configuraEncuestaForm.getCantidadMaximaDocentes());
            configuraEncuestaBD.setCantidadMinimaAlumnos(configuraEncuestaForm.getCantidadMinimaAlumnos());
            configuraEncuestaBD.setEncuestaTeoriaPractica(configuraEncuestaForm.getEncuestaTeoriaPractica() == null ? 0L : 1L);
            configuraEncuestaBD.setFechaModificacion(new Date());
            configuraEncuestaBD.setUserModificacion(ds.getUsuario());
            configuraEncuestaDAO.update(configuraEncuestaBD);
        }

        encuesta.setEstadoEnum(EncuestaEstadoEnum.CFG);
        encuestaEstudiantilDAO.update(encuesta);
    }

    private void updateConfigEncuesta(EncuestaEstudiantil encuesta, List<PeriodoEncuesta> periodosEncuestaForm, CicloAcademico ciclo, DataSessionPivot ds) {
        List<PeriodoEncuesta> periodosEncuestaBD = periodoEncuestaDAO.allByEncuesta(encuesta);
        ListsInspector inspector = TypesUtil.analizeLists(periodosEncuestaBD, periodosEncuestaForm, "key");

        List<PeriodoEncuesta> periodosDelete = inspector.getDeadList();
        for (PeriodoEncuesta periodo : periodosDelete) {
            periodoEncuestaDAO.delete(periodo);
        }

        List<PeriodoEncuesta> periodosAdd = inspector.getNewList();
        for (PeriodoEncuesta periodo : periodosAdd) {
            periodo.setEncuestaEstudiantil(encuesta);
            periodo.setFechaRegsitro(new Date());
            periodo.setUserRegistro(ds.getUsuario());
            periodoEncuestaDAO.save(periodo);
        }
    }

    @Override
    @Transactional
    public void saveDetalleConfigEncuesta(EncuestaEstudiantil encuestaForm, CicloAcademico ciclo, DataSessionPivot ds) {
        TipoExamenVirtual tipoEncuesta = tipoExamenVirtualDAO.findByEnum(TipoExamenVirtualEnum.ENC_DOC);
        ExamenVirtual encuestaModelo = examenVirtualDAO.findEncuestaActivaByTipo(tipoEncuesta);
        EncuestaEstudiantil encuestaBD = encuestaEstudiantilDAO.findByCicloEncuesta(ciclo, encuestaModelo);
        Assert.isTrue(encuestaBD != null, "Aún no se ha activado la encuesta");

        boolean esSinConfig = encuestaBD.getEstadoEnum() == EncuestaEstadoEnum.CRE;
        boolean esSinData = encuestaBD.getEstadoEnum() == EncuestaEstadoEnum.CFG && encuestaBD.getObjetivosEncuesta() == 0;
        Assert.isTrue(esSinConfig || esSinData, "Ya no puede configurar esta encuesta");

        updateConfigEncuesta(encuestaBD, encuestaForm.getConfiguraEncuesta().get(0), ciclo, ds);
        updateConfigEncuesta(encuestaBD, encuestaForm.getPeriodosEncuesta(), ciclo, ds);

    }

    @Override
    public List<ResumenEncuestaDocente> resumenPreguntasLikert(EncuestaDocente encuestaDocente) {
        return resumenEncuestaDocenteDAO.allByEncuestaDocente(encuestaDocente);
    }

    @Override
    public List<String> resumenComentarios(EncuestaDocente encuestaDocente) {
        List<RespuestaEncuestaAlumno> respuestas = respuestaEncuestaAlumnoDAO.allComentariosByEncuestaDocente(encuestaDocente);
        return respuestas.stream().map(RespuestaEncuestaAlumno::getComentario).collect(Collectors.toList());
    }

    @Override
    public List<PuntajeEncuestaDocente> resumenPuntajeTemas(EncuestaDocente encuestaDocente) {
        return puntajeEncuestaDocenteDAO.allByEncuestaDocente(encuestaDocente);
    }
}
