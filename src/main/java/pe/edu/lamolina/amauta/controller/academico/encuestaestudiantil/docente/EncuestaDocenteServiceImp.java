package pe.edu.lamolina.amauta.controller.academico.encuestaestudiantil.docente;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
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
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.encuestaestudiantil.ConfiguraEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.CursoSinEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaCurso;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.encuestaestudiantil.PeriodoEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.PuntajeEncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.RespuestaEncuestaAlumno;
import pe.edu.lamolina.model.encuestaestudiantil.ResumenEncuestaDocente;
import pe.edu.lamolina.model.enums.EncuestaEstadoEnum;
import static pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum.ACT;
import static pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum.ANU;
import static pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum.FECH;
import static pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum.TEO;
import static pe.edu.lamolina.model.enums.RolEnum.DOC;
import static pe.edu.lamolina.model.enums.TipoDictadoGrupoSeccionEnum.MOD;
import static pe.edu.lamolina.model.enums.TipoDictadoGrupoSeccionEnum.SEM;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.TipoExamenVirtual;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.amauta.dao.academico.CursoDAO;
import pe.edu.lamolina.amauta.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.FacultadDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.amauta.dao.encuesta.ConfiguraEncuestaDAO;
import pe.edu.lamolina.amauta.dao.encuesta.CursoSinEncuestaDAO;
import pe.edu.lamolina.amauta.dao.encuesta.EncuestaAlumnoDAO;
import pe.edu.lamolina.amauta.dao.encuesta.EncuestaCursoDAO;
import pe.edu.lamolina.amauta.dao.encuesta.EncuestaDocenteDAO;
import pe.edu.lamolina.amauta.dao.encuesta.EncuestaEstudiantilDAO;
import pe.edu.lamolina.amauta.dao.encuesta.ExamenVirtualDAO;
import pe.edu.lamolina.amauta.dao.encuesta.PeriodoEncuestaDAO;
import pe.edu.lamolina.amauta.dao.encuesta.PuntajeEncuestaDocenteDAO;
import pe.edu.lamolina.amauta.dao.encuesta.RespuestaEncuestaAlumnoDAO;
import pe.edu.lamolina.amauta.dao.encuesta.ResumenEncuestaDocenteDAO;
import pe.edu.lamolina.amauta.dao.encuesta.TipoExamenVirtualDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class EncuestaDocenteServiceImp implements EncuestaDocenteService {

    @Autowired
    EncuestaDocenteDAO encuestaDocenteDAO;
    @Autowired
    EncuestaCursoDAO encuestaCursoDAO;
    @Autowired
    EncuestaEstudiantilDAO encuestaEstudiantilDAO;
    @Autowired
    FacultadDAO facultadDAO;
    @Autowired
    DepartamentoAcademicoDAO departamentoAcademicoDAO;
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
    @Autowired
    CursoDAO cursoDAO;
    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    VerificadorService verificadorService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public EncuestaEstudiantil findEncuestaDocente(CicloAcademico cicloAcademico) {
        TipoExamenVirtual tipoEncuesta = tipoExamenVirtualDAO.findByEnum(TipoExamenVirtualEnum.ENC_DOC);
        ExamenVirtual encuestaModelo = examenVirtualDAO.findEncuestaActivaByTipo(tipoEncuesta);
        return encuestaEstudiantilDAO.findByCicloEncuesta(cicloAcademico, encuestaModelo);
    }

    @Override
    public EncuestaEstudiantil findEncuestaDocenteWithResumen(CicloAcademico cicloAcademico, DataSessionPivot ds, HttpServletRequest request) {
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
            List<Facultad> facultades = this.allAccesoFacultades(ds, request);
            List<DepartamentoAcademico> departamentos = this.allAccesoDepartamentos(ds, facultades, cicloAcademico, request);
            List<EncuestaDocente> encDocentes = encuestaDocenteDAO.allByEncuestaEstudiantil(encuesta, departamentos);
            int activos = 0;
            int anulados = 0;
            int innecesa = 0;
            int cerrados = 0;
            int sinperio = 0;
            int posgrados = 0;
            int pregrados = 0;
            int modulares = 0;
            int semestrales = 0;

            for (EncuestaDocente encDocente : encDocentes) {
                switch (encDocente.getEstadoEnum()) {
                    case ACT:
                        activos++;
                        break;
                    case ANU:
                        anulados++;
                        break;
                    case TEO:
                        innecesa++;
                        break;
                    case CER:
                        cerrados++;
                        break;
                    case FECH:
                        sinperio++;
                        break;
                }
                if (encDocente.getModalidadEstudio().isPostgrado()) {
                    posgrados++;
                }
                if (encDocente.getModalidadEstudio().isPregrado()) {
                    pregrados++;
                }
                GrupoSeccion gpoSeccion = encDocente.getDocenteSeccion().getSeccion().getGrupoSeccion();
                if (gpoSeccion.getTipoDictadoEnum() == MOD) {
                    modulares++;
                }
                if (gpoSeccion.getTipoDictadoEnum() == SEM) {
                    semestrales++;
                }
            }

            encuesta.setEncuestasActivas(activos);
            encuesta.setEncuestasAnuladas(anulados);
            encuesta.setEncuestasCerradas(cerrados);
            encuesta.setEncuestasInnecesarias(innecesa);
            encuesta.setEncuestasSinPeriodo(sinperio);
            encuesta.setEncuestasPosgrado(posgrados);
            encuesta.setEncuestasPregrado(pregrados);
            encuesta.setEncuestasModulares(modulares);
            encuesta.setEncuestasSemestrales(semestrales);
        }

        return encuesta;
    }

    @Override
    public List<EncuestaDocente> allEncuestaDocente(DynatableFilter filter, CicloAcademico ciclo, List<DepartamentoAcademico> departamentos, DataSessionPivot ds) {
        Docente docente = null;
        if (ds.getRolActivo().getCodigoEnum() == DOC) {
            docente = ds.getDocente();
        }

        List<EncuestaDocente> encuestas = encuestaDocenteDAO.allByDynatable(filter, ciclo, departamentos, docente);
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
        Assert.isTrue(verificadorService.isEditorEncuestas(ds), "No tiene permiso para ejecutar esta operación");

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
        if (!verificadorService.isEditorEncuestas(ds)) {
            return "No tiene permiso para ejecutar esta operación";
        }

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
    public void cambiarEstadoEncuesta(EncuestaDocente encuestaForm, CicloAcademico ciclo, DataSessionPivot ds) {
        Assert.isTrue(verificadorService.isEditorEncuestas(ds), "No tiene permiso para ejecutar esta operación");

        EncuestaDocente encuestaBD = encuestaDocenteDAO.findEncuestaDocente(encuestaForm);
        Assert.isNotNull(encuestaBD, "No existe esta encuesta en la base de datos");

        switch (encuestaForm.getEstadoEnum()) {
            case ACT:
                Assert.isTrue(encuestaBD.getEstadoEnum() == ACT, "La encuesta del docentes ya no se encuentra activa");
                generadorEncuestaDocenteService.desactivarEncuestaDocente(encuestaForm, ciclo, ds);
                break;
            case ANU:
                Assert.isTrue(encuestaBD.getEstadoEnum() == ANU, "La encuesta del docentes ya no se encuentra inactiva");
                generadorEncuestaDocenteService.activarEncuestaDocente(encuestaForm, ciclo, ds);
                break;
            case TEO:
                Assert.isTrue(encuestaBD.getEstadoEnum() == TEO, "La encuesta del docentes ya no se encuentra inactiva por la teoría");
                generadorEncuestaDocenteService.activarEncuestaDocente(encuestaForm, ciclo, ds);
                break;
            case FECH:
                Assert.isTrue(encuestaBD.getEstadoEnum() == FECH, "La encuesta del docentes ya no se encuentra inactiva por fecha");
                generadorEncuestaDocenteService.activarEncuestaDocente(encuestaForm, ciclo, ds);
                break;
            default:
                throw new PhobosException("Este cambio no procede");
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
            configuraEncuestaBD.setCantidadMinimaAlumnosPregrado(configuraEncuestaForm.getCantidadMinimaAlumnosPregrado());
            configuraEncuestaBD.setCantidadMinimaAlumnosPosgrado(configuraEncuestaForm.getCantidadMinimaAlumnosPosgrado());
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
        Assert.isTrue(verificadorService.isEditorEncuestas(ds), "No tiene permiso para ejecutar esta operación");

        TipoExamenVirtual tipoEncuesta = tipoExamenVirtualDAO.findByEnum(TipoExamenVirtualEnum.ENC_DOC);
        ExamenVirtual encuestaModelo = examenVirtualDAO.findEncuestaActivaByTipo(tipoEncuesta);
        EncuestaEstudiantil encuestaTipoDocente = encuestaEstudiantilDAO.findByCicloEncuesta(ciclo, encuestaModelo);
        Assert.isNotNull(encuestaTipoDocente, "Aún no se ha activado la encuesta");

        boolean esSinConfig = encuestaTipoDocente.getEstadoEnum() == EncuestaEstadoEnum.CRE;
        boolean esSinData = encuestaTipoDocente.getEstadoEnum() == EncuestaEstadoEnum.CFG && encuestaTipoDocente.getObjetivosEncuesta() == 0;
        Assert.isTrue(esSinConfig || esSinData, "Ya no puede configurar esta encuesta");

        updateConfigEncuesta(encuestaTipoDocente, encuestaForm.getConfiguraEncuesta().get(0), ciclo, ds);
        updateConfigEncuesta(encuestaTipoDocente, encuestaForm.getPeriodosEncuesta(), ciclo, ds);

        List<Curso> cursosNoEncuestar = cursoDAO.allNoEncuestar();
        List<CursoSinEncuesta> cursosSinEncuesta = cursoSinEncuestaDAO.allByEncuestaEstudiantil(encuestaTipoDocente);
        Map<Long, Curso> mapCursoNoEncuestar = TypesUtil.convertListToMap("id", cursosNoEncuestar);
        Map<Long, Curso> mapCursoSinEncuesta = TypesUtil.convertListToMap("curso.id", "curso", cursosSinEncuesta);
        List<Curso> cursosProgramados = cursoDAO.allProgramadosByCiclo(ciclo);

        for (Curso curso : cursosProgramados) {
            Curso cursoNoProgramado = mapCursoNoEncuestar.get(curso.getId());
            Curso cursoSinEncuesta = mapCursoSinEncuesta.get(curso.getId());
            if (cursoNoProgramado != null && cursoSinEncuesta == null) {
                CursoSinEncuesta cus = new CursoSinEncuesta();
                cus.setCurso(curso);
                cus.setEncuestaEstudiantil(encuestaTipoDocente);
                cus.setFechaCreacion(new Date());
                cus.setUserCreacion(ds.getUsuario());
                cursoSinEncuestaDAO.save(cus);

                mapCursoSinEncuesta.put(curso.getId(), curso);
            }
        }
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

    @Override
    @Transactional
    public void delete(EncuestaEstudiantil encuestaDoceenteForm, DataSessionPivot ds) {
        Assert.isTrue(verificadorService.isEditorEncuestas(ds), "No tiene permiso para ejecutar esta operación");
        EncuestaEstudiantil encuestaTipoDocente = encuestaEstudiantilDAO.find(encuestaDoceenteForm.getId());
        Assert.isNotNull(encuestaTipoDocente, "La encuesta no existe");

        TipoExamenVirtualEnum tipo = encuestaTipoDocente.getEncuesta().getTipoExamen().getTipoEnum();
        Assert.isTrue(tipo == TipoExamenVirtualEnum.ENC_DOC, "Solo puede eliminarse encuesta de docentes");

        List<EncuestaDocente> encuestasDocentes = encuestaDocenteDAO.allByEncuestaEstudiantil(encuestaTipoDocente, new ArrayList());

        encuestaAlumnoDAO.deleteByEncuestasDocentes(encuestasDocentes);
        configuraEncuestaDAO.deleteByEncuestaEstudiantil(encuestaDoceenteForm);
        periodoEncuestaDAO.deleteByEncuestaEstudiantil(encuestaDoceenteForm);
        cursoSinEncuestaDAO.deleteByEncuestaEstudiantil(encuestaDoceenteForm);

        CicloAcademico ciclo = encuestaTipoDocente.getCicloAcademico();
        EncuestaEstudiantil encuestaTipoCurso = encuestaEstudiantilDAO.findByCicloTipo(ciclo, TipoExamenVirtualEnum.ENC_CUR);

        if (encuestaTipoCurso != null) {
            ConfiguraEncuesta configCurso = configuraEncuestaDAO.findByEncuesta(encuestaTipoCurso);

            if (configCurso != null && configCurso.getSimultaneo() == 1) {
                List<EncuestaCurso> encuestasCursos = encuestaCursoDAO.allByEncuestaEstudiantil(encuestaTipoCurso, true);
                encuestaAlumnoDAO.deleteByEncuestasCursos(encuestasCursos);
                encuestaCursoDAO.deleteByEncuestaTipoCurso(encuestaTipoCurso);
                configuraEncuestaDAO.deleteByEncuestaEstudiantil(encuestaTipoCurso);
                periodoEncuestaDAO.deleteByEncuestaEstudiantil(encuestaTipoCurso);
                cursoSinEncuestaDAO.deleteByEncuestaEstudiantil(encuestaTipoCurso);
                encuestaEstudiantilDAO.delete(encuestaTipoCurso);
            }
        }

        encuestaDocenteDAO.deleteByEncuestaTipoDocente(encuestaDoceenteForm);
        encuestaEstudiantilDAO.delete(encuestaTipoDocente);

    }

    @Override
    @Transactional
    public void publicar(EncuestaEstudiantil encuestaDocenteForm, DataSessionPivot ds) {
        Assert.isTrue(verificadorService.isEditorEncuestas(ds), "No tiene permiso para ejecutar esta operación");
        EncuestaEstudiantil encuestaDocenteDB = encuestaEstudiantilDAO.find(encuestaDocenteForm.getId());

        Assert.isNotNull(encuestaDocenteDB, "La encuesta no existe");
        Assert.isTrue(encuestaDocenteDB.getEstadoEnum() == EncuestaEstadoEnum.CFG, "La encuesta no se encuentra en estado Configurando");

        encuestaDocenteDB.setEstadoEnum(EncuestaEstadoEnum.ACT);
        encuestaEstudiantilDAO.update(encuestaDocenteDB);

        EncuestaEstudiantil encuestaCurso = encuestaEstudiantilDAO.findByCicloTipo(encuestaDocenteDB.getCicloAcademico(), TipoExamenVirtualEnum.ENC_CUR);
        Assert.isNotNull(encuestaCurso, "No puede publicar si no ha creado la encuesta de cursos");
        ConfiguraEncuesta configCurso = configuraEncuestaDAO.findByEncuesta(encuestaCurso);
        Assert.isNotNull(configCurso, "No puede publicar si no ha configurado la encuesta de cursos");

        if (configCurso.getSimultaneo() == 1) {
            Assert.isTrue(encuestaCurso.getEstadoEnum() == EncuestaEstadoEnum.CFG, "La encuesta de los cursos no se encuentra en estado Configurando");
            Assert.isTrue(encuestaCurso.getObjetivosEncuesta() > 0, "Debe generar las encuestas de cursos");

            encuestaCurso.setEstadoEnum(EncuestaEstadoEnum.ACT);
            encuestaEstudiantilDAO.update(encuestaCurso);
        }

    }

    @Override
    public List<Facultad> allFacultadesFromDocentes(CicloAcademico cicloAcademico, DataSessionPivot ds, HttpServletRequest request) {
        List<Facultad> facultadesAll = facultadDAO.allFromDocentesByCiclo(cicloAcademico);
        if (verificadorService.puedeVerAllFacultades(ds, "ENCUESTA_ESTUDIANTIL")) {
            return facultadesAll;
        }

        List<Facultad> facultades = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.FAC, request, ds);
        if (facultades.isEmpty()) {
            return facultades;
        }

        Map<Long, Facultad> mapFacultad = TypesUtil.convertListToMap("id", facultades);
        List<Facultad> facultadesRpta = new ArrayList();
        for (Facultad fac : facultadesAll) {
            Facultad facultad = mapFacultad.get(fac.getId());
            if (facultad != null) {
                facultadesRpta.add(fac);
            }
        }
        return facultadesRpta;

    }

    @Override
    public List<DepartamentoAcademico> allDepartamentosFromDocentes(
            CicloAcademico cicloAcademico,
            List<Facultad> facultades,
            DataSessionPivot ds,
            HttpServletRequest request) {

        List<DepartamentoAcademico> dptosAll = departamentoAcademicoDAO.allFromDocentesByCiclo(cicloAcademico);
        if (verificadorService.puedeVerAllDepartamentos(ds, "ENCUESTA_ESTUDIANTIL")) {
            return dptosAll;
        }

        List<DepartamentoAcademico> departamentos = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.DPTO, request, ds);
        if (departamentos.isEmpty() && facultades.isEmpty()) {
            return departamentos;
        }

        if (!departamentos.isEmpty() && facultades.isEmpty()) {
            Map<Long, Facultad> mapFacultad = new LinkedHashMap();
            List<DepartamentoAcademico> departamentosRpta = new ArrayList();
            Map<Long, DepartamentoAcademico> mapDptos = TypesUtil.convertListToMap("id", departamentos);
            for (DepartamentoAcademico dpto : dptosAll) {
                DepartamentoAcademico dep = mapDptos.get(dpto.getId());
                if (dep != null) {
                    departamentosRpta.add(dpto);
                    mapFacultad.put(dpto.getFacultad().getId(), dpto.getFacultad());
                }
            }
            facultades.addAll(new ArrayList(mapFacultad.values()));
            Collections.sort(facultades, (f1, f2) -> f1.getNombre().compareTo(f2.getNombre()));
            Collections.sort(departamentosRpta, (d1, d2) -> d1.getNombre().compareTo(d2.getNombre()));
            return departamentosRpta;
        }

        if (departamentos.isEmpty() && !facultades.isEmpty()) {
            Map<Long, Facultad> mapFacultad = TypesUtil.convertListToMap("id", facultades);
            List<DepartamentoAcademico> departamentosRpta = new ArrayList();
            for (DepartamentoAcademico dpto : dptosAll) {
                Facultad fac = mapFacultad.get(dpto.getFacultad().getId());
                if (fac != null) {
                    departamentosRpta.add(dpto);
                }
            }
            Collections.sort(departamentosRpta, (d1, d2) -> d1.getNombre().compareTo(d2.getNombre()));
            return departamentosRpta;
        }

        List<DepartamentoAcademico> departamentosRpta = new ArrayList();
        Map<Long, Facultad> mapFacultadNew = new LinkedHashMap();
        Map<Long, DepartamentoAcademico> mapDptosVer = TypesUtil.convertListToMap("id", departamentos);
        Map<Long, Facultad> mapFacultadVer = TypesUtil.convertListToMap("id", facultades);
        for (DepartamentoAcademico dpto : dptosAll) {
            DepartamentoAcademico dep = mapDptosVer.get(dpto.getId());
            if (dep != null) {
                departamentosRpta.add(dpto);
                Facultad fac = mapFacultadVer.get(dep.getFacultad().getId());
                if (fac == null) {
                    mapFacultadNew.put(dep.getFacultad().getId(), dep.getFacultad());
                }
            }
        }

        for (DepartamentoAcademico dpto : dptosAll) {
            DepartamentoAcademico dep = mapDptosVer.get(dpto.getId());
            if (dep != null) {
                continue;
            }
            Facultad fac = mapFacultadVer.get(dpto.getFacultad().getId());
            if (fac != null) {
                departamentosRpta.add(dpto);
            }
        }

        facultades.addAll(new ArrayList(mapFacultadNew.values()));
        Collections.sort(facultades, (f1, f2) -> f1.getNombre().compareTo(f2.getNombre()));
        Collections.sort(departamentosRpta, (d1, d2) -> d1.getNombre().compareTo(d2.getNombre()));
        return departamentosRpta;
    }

    @Override
    public List<Facultad> allAccesoFacultades(DataSessionPivot ds, HttpServletRequest request) {
        if (verificadorService.puedeVerAllFacultades(ds, "ENCUESTA_ESTUDIANTIL")) {
            return new ArrayList();
        }

        List<Facultad> facultades = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.FAC, request, ds);
        if (facultades.isEmpty()) {
            facultades.add(new Facultad(99999L));
        }
        return facultades;
    }

    @Override
    public List<DepartamentoAcademico> allAccesoDepartamentos(DataSessionPivot ds, List<Facultad> facultades, CicloAcademico ciclo, HttpServletRequest request) {
        if (facultades.isEmpty()) {
            return new ArrayList();
        }
        if (verificadorService.puedeVerAllDepartamentos(ds, "ENCUESTA_ESTUDIANTIL")) {
            return new ArrayList();
        }

        Facultad comodin = null;
        for (Facultad fac : facultades) {
            if (fac.getId() == 99999L) {
                comodin = fac;
            }
        }
        if (comodin != null) {
            facultades.remove(comodin);
        }

        List<DepartamentoAcademico> departamentos = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.DPTO, request, ds);
        if (departamentos.isEmpty() && facultades.isEmpty()) {
            departamentos.add(new DepartamentoAcademico(99999L));
            return departamentos;
        }

        if (!departamentos.isEmpty() && facultades.isEmpty()) {
            return departamentos;
        }

        List<DepartamentoAcademico> dptosAll = departamentoAcademicoDAO.allFromDocentesByCiclo(ciclo);
        Map<Long, Facultad> mapFacultad = TypesUtil.convertListToMap("id", facultades);
        for (DepartamentoAcademico dpto : dptosAll) {
            Facultad fac = mapFacultad.get(dpto.getFacultad().getId());
            if (fac != null) {
                departamentos.add(dpto);
            }
        }
        if (departamentos.isEmpty()) {
            departamentos.add(new DepartamentoAcademico(99999L));
        }
        return departamentos;
    }

    @Override
    public List<DocenteSeccion> allDocenteSeccionNoProcesados(CicloAcademico ciclo, EncuestaEstudiantil encuesta) {
        return docenteSeccionDAO.allNoProcesadosEncuestaByCiclo(ciclo, encuesta);
    }

    @Override
    @Transactional
    public void addDocenteSeccionToEncuesta(DocenteSeccion docenteSeccion, CicloAcademico ciclo, DataSessionPivot ds) {
        EncuestaDocente encuProfeSecc = encuestaDocenteDAO.findByDocenteSeccion(docenteSeccion);
        Assert.isNull(encuProfeSecc, "Este docente-sección ya se encuentra procesado en la encuesta");

        generadorEncuestaDocenteService.generarEncuestaDocente(docenteSeccion, ciclo, ds);

    }

}
