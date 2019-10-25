package pe.edu.lamolina.pivot.controller.academico.encuestaestudiantil.docente;

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
import org.thymeleaf.util.StringUtils;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ListsInspector;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.encuestaestudiantil.ConfiguraEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.CursoSinEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaAlumno;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaCurso;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.encuestaestudiantil.PeriodoEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.PuntajeEncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.RespuestaEncuestaAlumno;
import pe.edu.lamolina.model.encuestaestudiantil.ResumenEncuestaDocente;
import pe.edu.lamolina.model.enums.EncuestaEstadoEnum;
import pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum;
import static pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum.ACT;
import static pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum.ANU;
import static pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum.ENC;
import static pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum.FECH;
import static pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum.TEO;
import static pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum.PEND;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.TipoExamenVirtual;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.encuesta.ConfiguraEncuestaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.CursoSinEncuestaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaAlumnoDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaCursoDAO;
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
    EncuestaCursoDAO encuestaCursoDAO;
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
    @Autowired
    CursoDAO cursoDAO;
    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

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
            List<EncuestaDocente> encDocentes = encuestaDocenteDAO.allByEncuestaEstudiantil(encuesta);
            int activos = 0;
            int anulados = 0;
            int innecesa = 0;
            int cerrados = 0;
            int sinperio = 0;
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
            }
            encuesta.setEncuestasActivas(activos);
            encuesta.setEncuestasAnuladas(anulados);
            encuesta.setEncuestasCerradas(cerrados);
            encuesta.setEncuestasInnecesarias(innecesa);
            encuesta.setEncuestasSinPeriodo(sinperio);
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
    public void cambiarEstadoEncuesta(EncuestaDocente encuestaForm, DataSessionPivot ds) {
        EncuestaDocente encuestaBD = encuestaDocenteDAO.findEncuestaDocente(encuestaForm);
        Assert.isNotNull(encuestaBD, "No existe esta encuesta en la base de datos");

        switch (encuestaForm.getEstadoEnum()) {
            case ACT:
                Assert.isTrue(encuestaBD.getEstadoEnum() == ACT, "La encuesta del docentes ya no se encuentra activa");
                desactivarEncuestaDocente(encuestaBD, encuestaForm, ds);
                break;
            case ANU:
                Assert.isTrue(encuestaBD.getEstadoEnum() == ANU, "La encuesta del docentes ya no se encuentra inactiva");
                activarEncuestaDocente(encuestaBD, ds);
                break;
            case TEO:
                Assert.isTrue(encuestaBD.getEstadoEnum() == TEO, "La encuesta del docentes ya no se encuentra inactiva por la teoría");
                activarEncuestaDocente(encuestaBD, ds);
                break;
            case FECH:
                Assert.isTrue(encuestaBD.getEstadoEnum() == FECH, "La encuesta del docentes ya no se encuentra inactiva por fecha");
                activarEncuestaDocente(encuestaBD, ds);
                break;
            default:
                throw new PhobosException("Este cambio no procede");
        }
    }

    private void activarEncuestaDocente(EncuestaDocente encuestaBD, DataSessionPivot ds) {
        Seccion seccion = encuestaBD.getDocenteSeccion().getSeccion();
        List<MatriculaSeccion> matriculadosSeccion = matriculaSeccionDAO.allMatriculadosBySeccion(seccion);
        Assert.isFalse(matriculadosSeccion.isEmpty(), "No existe alumnos matriculados en esta sección");
        Map<Long, MatriculaSeccion> mapMatriculado = TypesUtil.convertListToMap("matriculaResumen.alumno.id", matriculadosSeccion);

        int total = matriculadosSeccion.size();
        int reprogramadas = 0;

        List<EncuestaAlumno> encuestasAlumnos = encuestaAlumnoDAO.allByEncuestaDocente(encuestaBD);
        Map<Long, EncuestaAlumno> mapEncuAlumno = TypesUtil.convertListToMap("alumno.id", encuestasAlumnos);

        for (MatriculaSeccion matriculado : matriculadosSeccion) {
            Alumno alumno = matriculado.getMatriculaResumen().getAlumno();
            EncuestaAlumno encuAlumno = mapEncuAlumno.get(alumno.getId());
            if (encuAlumno == null) {
                encuAlumno = new EncuestaAlumno();
                encuAlumno.setAlumno(alumno);
                encuAlumno.setEncuestaDocente(encuestaBD);
                encuAlumno.setEstadoEnum(EncuestaEstudiantilEstadoEnum.PEND);
                encuAlumno.setUserRegistro(ds.getUsuario());
                encuAlumno.setFechaRegistro(new Date());
                encuestaAlumnoDAO.save(encuAlumno);
                reprogramadas++;

            } else {
                if (encuAlumno.getEstadoEnum() == ANU) {
                    encuAlumno.setEstadoEnum(ACT);
                    encuestaAlumnoDAO.update(encuAlumno);
                    reprogramadas++;
                }
            }
        }

        for (EncuestaAlumno encuestaAlumno : encuestasAlumnos) {
            MatriculaSeccion matriculado = mapMatriculado.get(encuestaAlumno.getAlumno().getId());
            if (matriculado == null && encuestaAlumno.getEstadoEnum() == ENC) {
                total++;
            }
        }

        encuestaBD.setEstadoEnum(ACT);
        encuestaBD.setAlumnosFin(Long.valueOf(total));
        encuestaBD.setUserModificacion(ds.getUsuario());
        encuestaBD.setFechaModificacion(new Date());
        encuestaDocenteDAO.update(encuestaBD);

        EncuestaEstudiantil encu = encuestaBD.getEncuestaEstudiantil();
        encu.setObjetivosEncuesta(encu.getObjetivosEncuesta() + 1);
        encu.setEncuestasProgramadas(encu.getEncuestasProgramadas() + reprogramadas);
        encuestaEstudiantilDAO.update(encu);
    }

    private void desactivarEncuestaDocente(EncuestaDocente encuestaBD, EncuestaDocente encuestaForm, DataSessionPivot ds) {
        Assert.isFalse(StringUtils.isEmpty(encuestaForm.getDescripcion()), "Debe ingresar un motivo de la desactivación");
        encuestaBD.setEstadoEnum(ANU);
        encuestaBD.setDescripcion(encuestaForm.getDescripcion());
        encuestaBD.setUserModificacion(ds.getUsuario());
        encuestaBD.setFechaModificacion(new Date());

        int desprogramadas = 0;
        List<EncuestaAlumno> encuestas = encuestaAlumnoDAO.allByEncuestaDocente(encuestaBD);
        for (EncuestaAlumno encuestaAlumno : encuestas) {
            if (encuestaAlumno.getEstadoEnum() == PEND) {
                encuestaAlumno.setEstadoEnum(ANU);
                encuestaAlumnoDAO.update(encuestaAlumno);
                desprogramadas++;
            }
        }
        encuestaDocenteDAO.update(encuestaBD);

        EncuestaEstudiantil encu = encuestaBD.getEncuestaEstudiantil();
        encu.setObjetivosEncuesta(encu.getObjetivosEncuesta() - 1);
        encu.setEncuestasProgramadas(encu.getEncuestasProgramadas() - desprogramadas);
        encuestaEstudiantilDAO.update(encu);
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
    public void delete(EncuestaEstudiantil encuestaDoceenteForm) {

        EncuestaEstudiantil encuestaTipoDocente = encuestaEstudiantilDAO.find(encuestaDoceenteForm.getId());
        Assert.isNotNull(encuestaTipoDocente, "La encuesta no existe");

        TipoExamenVirtualEnum tipo = encuestaTipoDocente.getEncuesta().getTipoExamen().getTipoEnum();
        Assert.isTrue(tipo == TipoExamenVirtualEnum.ENC_DOC, "Solo puede eliminarse encuesta de docentes");

        List<EncuestaDocente> encuestasDocentes = encuestaDocenteDAO.allByEncuestaEstudiantil(encuestaTipoDocente);

        encuestaAlumnoDAO.deleteByEncuestasDocentes(encuestasDocentes);
        configuraEncuestaDAO.deleteByEncuestaEstudiantil(encuestaDoceenteForm);
        periodoEncuestaDAO.deleteByEncuestaEstudiantil(encuestaDoceenteForm);
        cursoSinEncuestaDAO.deleteByEncuestaEstudiantil(encuestaDoceenteForm);

        CicloAcademico ciclo = encuestaTipoDocente.getCicloAcademico();
        EncuestaEstudiantil encuestaTipoCurso = encuestaEstudiantilDAO.findByCicloTipo(ciclo, TipoExamenVirtualEnum.ENC_CUR);
        System.out.println("encuestaTipoCurso=" + encuestaTipoCurso);

        if (encuestaTipoCurso != null) {
            ConfiguraEncuesta configCurso = configuraEncuestaDAO.findByEncuesta(encuestaTipoCurso);
            System.out.println("configCurso=" + configCurso);
            if (configCurso != null && configCurso.getSimultaneo() == 1) {
                List<EncuestaCurso> encuestasCursos = encuestaCursoDAO.allByEncuestaEstudiantil(encuestaTipoCurso);
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
    public void publicar(EncuestaEstudiantil encuesta) {
        EncuestaEstudiantil encuestaDB = encuestaEstudiantilDAO.find(encuesta.getId());
        if (encuestaDB == null) {
            throw new PhobosException("La encuesta no existe");
        }

        if (encuestaDB.getEstadoEnum() != EncuestaEstadoEnum.CFG) {
            throw new PhobosException("La encuesta no se encuentra configurada");
        }
        encuestaDB.setEstadoEnum(EncuestaEstadoEnum.ACT);
        encuestaEstudiantilDAO.update(encuestaDB);

    }
}
