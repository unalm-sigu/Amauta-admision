package pe.edu.lamolina.amauta.controller.academico.encuestaestudiantil.curso;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ListsInspector;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.encuestaestudiantil.ConfiguraEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.CursoSinEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaAlumno;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaCurso;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.encuestaestudiantil.PeriodoEncuesta;
import pe.edu.lamolina.model.enums.EncuestaEstadoEnum;
import pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum;
import static pe.edu.lamolina.model.enums.TipoDictadoGrupoSeccionEnum.MOD;
import static pe.edu.lamolina.model.enums.TipoDictadoGrupoSeccionEnum.SEM;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.TipoExamenVirtual;
import pe.edu.lamolina.amauta.dao.academico.CursoDAO;
import pe.edu.lamolina.amauta.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.FacultadDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.amauta.dao.academico.SeccionDAO;
import pe.edu.lamolina.amauta.dao.encuesta.ConfiguraEncuestaDAO;
import pe.edu.lamolina.amauta.dao.encuesta.CursoSinEncuestaDAO;
import pe.edu.lamolina.amauta.dao.encuesta.EncuestaAlumnoDAO;
import pe.edu.lamolina.amauta.dao.encuesta.EncuestaCursoDAO;
import pe.edu.lamolina.amauta.dao.encuesta.EncuestaEstudiantilDAO;
import pe.edu.lamolina.amauta.dao.encuesta.ExamenVirtualDAO;
import pe.edu.lamolina.amauta.dao.encuesta.PeriodoEncuestaDAO;
import pe.edu.lamolina.amauta.dao.encuesta.TipoExamenVirtualDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class EncuestaCursoServiceImp implements EncuestaCursoService {

    @Autowired
    EncuestaCursoDAO encuestaCursoDAO;
    @Autowired
    DocenteDAO docenteDAO;
    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;
    @Autowired
    EncuestaEstudiantilDAO encuestaEstudiantilDAO;
    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;
    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;
    @Autowired
    ConfiguraEncuestaDAO configuraEncuestaDAO;
    @Autowired
    CursoSinEncuestaDAO cursoSinEncuestaDAO;
    @Autowired
    EncuestaAlumnoDAO encuestaAlumnoDAO;
    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;
    @Autowired
    SeccionDAO seccionDAO;
    @Autowired
    TipoExamenVirtualDAO tipoExamenVirtualDAO;
    @Autowired
    ExamenVirtualDAO examenVirtualDAO;
    @Autowired
    PeriodoEncuestaDAO periodoEncuestaDAO;
    @Autowired
    VisorEncuestaCurso visorEncuestaCurso;
    @Autowired
    GeneradorEncuestaCursoService generadorEncuestaCursoService;
    @Autowired
    CursoDAO cursoDAO;
    @Autowired
    FacultadDAO facultadDAO;
    @Autowired
    DepartamentoAcademicoDAO departamentoAcademicoDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<EncuestaCurso> allEncuestaCurso(DynatableFilter filter, CicloAcademico ciclo, boolean noEsSimultaneo) {
        List<EncuestaCurso> encuestas = encuestaCursoDAO.allByDynatable(filter, ciclo, noEsSimultaneo);
        Map<Long, List<EncuestaCurso>> mapEncuestas = TypesUtil.convertListToMapList("grupoSeccion.id", encuestas);

        List<GrupoSeccion> gpoSecciones = new ArrayList();
        for (EncuestaCurso enc : encuestas) {
            gpoSecciones.add(enc.getGrupoSeccion());
        }

        List<Seccion> secciones = seccionDAO.allActivosByGposSeccion(gpoSecciones);
        List<DocenteSeccion> profesSecciones = docenteSeccionDAO.allPersonasActivasBySecciones(secciones);
        Map<Long, List<DocenteSeccion>> mapProfesSecciones = TypesUtil.convertListToMapList("seccion.grupoSeccion.id", profesSecciones);

        for (Seccion seccion : secciones) {
            if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.PCUR) {
                continue;
            }
            GrupoSeccion gpoSecc = seccion.getGrupoSeccion();
            List<EncuestaCurso> encus = mapEncuestas.get(gpoSecc.getId());
            if (encus == null) {
                continue;
            }

            for (EncuestaCurso encu : encus) {
                GrupoSeccion gpoSeccEnc = encu.getGrupoSeccion();
                List<DocenteSeccion> docentesSecc = TypesUtil.getListNotNull(mapProfesSecciones.get(gpoSecc.getId()));
                gpoSeccEnc.setSecciones(new ArrayList());
                gpoSeccEnc.getSecciones().add(seccion);
                seccion.setDocenteSeccion(docentesSecc);
            }
        }

        return encuestas;
    }

    @Override
    @Transactional
    public void cambiarEstadoEncuesta(EncuestaCurso encuestaForm) {
        EncuestaCurso encuesta = encuestaCursoDAO.findByEncuestaCurso(encuestaForm);
        if (Strings.isNullOrEmpty(encuesta.getEstado())) {
            encuesta.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ANU);
        }
        if (encuesta.getEstadoEnum() == EncuestaEstudiantilEstadoEnum.ACT
                || encuesta.getEstadoEnum() == EncuestaEstudiantilEstadoEnum.TEO) {
            encuesta.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ANU);
            List<EncuestaAlumno> encuestas = encuestaAlumnoDAO.allByEncuestaCurso(encuesta);
            for (EncuestaAlumno encuestaAlumno : encuestas) {
                encuestaAlumno.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ANU);
                encuestaAlumnoDAO.update(encuestaAlumno);
            }
            return;
        }
        encuesta.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
        List<EncuestaAlumno> encuestas = encuestaAlumnoDAO.allByEncuestaCurso(encuesta);
        if (encuestas.size() < 1) {
            throw new PhobosException("No existe encuestas para el docente ");
        }
        for (EncuestaAlumno encuestaAlumno : encuestas) {
            encuestaAlumno.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
            encuestaAlumnoDAO.update(encuestaAlumno);
        }
    }

    @Override
    public EncuestaEstudiantil findEncuestaCursoWithResumen(CicloAcademico cicloAcademico) {
        TipoExamenVirtual tipoEncuesta = tipoExamenVirtualDAO.findByEnum(TipoExamenVirtualEnum.ENC_CUR);
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
            boolean esSimultaneo = false;
            if (cfg != null) {
                if (cfg.getSimultaneo() == 1) {
                    TipoExamenVirtual tipoEncuestaDoc = tipoExamenVirtualDAO.findByEnum(TipoExamenVirtualEnum.ENC_DOC);
                    ExamenVirtual encuestaModeloDoc = examenVirtualDAO.findEncuestaActivaByTipo(tipoEncuestaDoc);
                    EncuestaEstudiantil encuestaDoc = encuestaEstudiantilDAO.findByCicloEncuesta(cicloAcademico, encuestaModeloDoc);
                    cfg = configuraEncuestaDAO.findByEncuesta(encuestaDoc);
                    esSimultaneo = true;
                }
                encuesta.getConfiguraEncuesta().add(cfg);
            }
            encuesta.setCursosNoEncuestar(cursoSinEncuestaDAO.allByEncuestaEstudiantil(encuesta));
            List<EncuestaCurso> encCursos = encuestaCursoDAO.allByEncuestaEstudiantil(encuesta, esSimultaneo);
            int activos = 0;
            int anulados = 0;
            int innecesa = 0;
            int cerrados = 0;
            int sinperio = 0;
            int posgrados = 0;
            int pregrados = 0;
            int modulares = 0;
            int semestrales = 0;

            for (EncuestaCurso encCurso : encCursos) {
                switch (encCurso.getEstadoEnum()) {
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
                if (encCurso.getModalidadEstudio().isPostgrado()) {
                    posgrados++;
                }
                if (encCurso.getModalidadEstudio().isPregrado()) {
                    pregrados++;
                }

                GrupoSeccion gpoSeccion;
                if (esSimultaneo) {
                    gpoSeccion = encCurso.getEncuestaDocente().getDocenteSeccion().getSeccion().getGrupoSeccion();
                } else {
                    gpoSeccion = encCurso.getGrupoSeccion();
                }

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
    @Transactional
    public void activarEncuesta(CicloAcademico cicloAcademico, DataSessionPivot ds) {

        TipoExamenVirtual tipoEncuesta = tipoExamenVirtualDAO.findByEnum(TipoExamenVirtualEnum.ENC_CUR);
        ExamenVirtual encuestaModelo = examenVirtualDAO.findEncuestaActivaByTipo(tipoEncuesta);
        EncuestaEstudiantil encuesta = encuestaEstudiantilDAO.findByCicloEncuesta(cicloAcademico, encuestaModelo);
        Assert.isNull(encuesta, "Ya se activó la encuesta en este ciclo");

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
    @Transactional
    public void saveDetalleConfigEncuesta(EncuestaEstudiantil encuestaForm, CicloAcademico ciclo, DataSessionPivot ds) {
        TipoExamenVirtual tipoEncuesta = tipoExamenVirtualDAO.findByEnum(TipoExamenVirtualEnum.ENC_CUR);
        ExamenVirtual encuestaModelo = examenVirtualDAO.findEncuestaActivaByTipo(tipoEncuesta);
        EncuestaEstudiantil encuestaBD = encuestaEstudiantilDAO.findByCicloEncuesta(ciclo, encuestaModelo);
        Assert.isTrue(encuestaBD != null, "Aún no se ha activado la encuesta");

        boolean esSinConfig = encuestaBD.getEstadoEnum() == EncuestaEstadoEnum.CRE;
        boolean esSinData = encuestaBD.getEstadoEnum() == EncuestaEstadoEnum.CFG && encuestaBD.getObjetivosEncuesta() == 0;
        Assert.isTrue(esSinConfig || esSinData, "Ya no puede configurar esta encuesta");

        boolean esSimultaneo = encuestaForm.getConfiguraEncuesta().get(0).getSimultaneo() == 1;
        ConfiguraEncuesta configDocente = null;
        if (esSimultaneo) {
            EncuestaEstudiantil encuestaDocente = encuestaEstudiantilDAO.findByCicloTipo(ciclo, TipoExamenVirtualEnum.ENC_DOC);
            configDocente = configuraEncuestaDAO.findByEncuesta(encuestaDocente);
            Assert.isNotNull(configDocente, "Primero debe configurar la encuesta de docentes");
        }

        updateConfigEncuesta(encuestaBD, encuestaForm.getConfiguraEncuesta().get(0), configDocente, ds);
        updateConfigEncuesta(encuestaBD, encuestaForm.getPeriodosEncuesta(), configDocente, ciclo, ds);

        List<Curso> cursosNoEncuestar = cursoDAO.allNoEncuestar();
        List<CursoSinEncuesta> cursosSinEncuesta = cursoSinEncuestaDAO.allByEncuestaEstudiantil(encuestaBD);
        Map<Long, Curso> mapCursosNoEncuestar = TypesUtil.convertListToMap("curso.id", "curso", cursosSinEncuesta);

        for (Curso curso : cursosNoEncuestar) {
            Curso cur = mapCursosNoEncuestar.get(curso.getId());
            if (cur == null) {
                CursoSinEncuesta cus = new CursoSinEncuesta();
                cus.setCurso(curso);
                cus.setEncuestaEstudiantil(encuestaBD);
                cus.setFechaCreacion(new Date());
                cus.setUserCreacion(ds.getUsuario());
                cursoSinEncuestaDAO.save(cus);
            }
        }
    }

    private void updateConfigEncuesta(EncuestaEstudiantil encuestaCurso, ConfiguraEncuesta configCursoForm, ConfiguraEncuesta configDocente, DataSessionPivot ds) {
        boolean esSimultaneo = configDocente != null;

        ConfiguraEncuesta configCursoBD = configuraEncuestaDAO.findByEncuesta(encuestaCurso);
        if (configCursoBD == null) {
            configCursoForm.setEncuestaEstudiantil(encuestaCurso);
            configCursoForm.setFechaRegistro(new Date());
            configCursoForm.setUserRegistro(ds.getUsuario());

            if (esSimultaneo) {
                configCursoForm.setCantidadMaximaDocentes(configDocente.getCantidadMaximaDocentes());
                configCursoForm.setDiasEncuesta(configDocente.getDiasEncuesta());
                configCursoForm.setEncuestaTeoriaPractica(configDocente.getEncuestaTeoriaPractica());

            } else {
                configCursoForm.setCantidadMaximaDocentes(0L);
                configCursoForm.setDiasEncuesta(0L);
                configCursoForm.setEncuestaTeoriaPractica(configCursoForm.getEncuestaTeoriaPractica() == null ? 0L : 1L);
            }
            configuraEncuestaDAO.save(configCursoForm);

        } else {
            configCursoBD.setFechaModificacion(new Date());
            configCursoBD.setUserModificacion(ds.getUsuario());

            if (esSimultaneo) {
                configCursoBD.setCantidadMaximaDocentes(0L);
                configCursoBD.setCantidadMinimaAlumnosPregrado(configDocente.getCantidadMinimaAlumnosPregrado());
                configCursoBD.setCantidadMinimaAlumnosPosgrado(configDocente.getCantidadMinimaAlumnosPosgrado());
                configCursoBD.setEncuestaTeoriaPractica(configDocente.getEncuestaTeoriaPractica());

            } else {
                configCursoBD.setCantidadMaximaDocentes(0L);
                configCursoBD.setCantidadMinimaAlumnosPregrado(configCursoForm.getCantidadMinimaAlumnosPregrado());
                configCursoBD.setCantidadMinimaAlumnosPosgrado(configCursoForm.getCantidadMinimaAlumnosPosgrado());
                configCursoBD.setEncuestaTeoriaPractica(configCursoForm.getEncuestaTeoriaPractica() == null ? 0L : 1L);
            }
            configuraEncuestaDAO.update(configCursoBD);
        }

        encuestaCurso.setEstadoEnum(EncuestaEstadoEnum.CFG);
        encuestaEstudiantilDAO.update(encuestaCurso);
    }

    private void updateConfigEncuesta(EncuestaEstudiantil encuesta, List<PeriodoEncuesta> periodosEncuestaForm, ConfiguraEncuesta configDocente, CicloAcademico ciclo, DataSessionPivot ds) {
        boolean esSimultaneo = configDocente != null;
        List<PeriodoEncuesta> periodosEncuestaRevisar = periodosEncuestaForm;
        if (esSimultaneo) {
            periodosEncuestaRevisar = periodoEncuestaDAO.allByEncuesta(configDocente.getEncuestaEstudiantil());
        }

        List<PeriodoEncuesta> periodosEncuestaBD = periodoEncuestaDAO.allByEncuesta(encuesta);
        ListsInspector inspector = TypesUtil.analizeLists(periodosEncuestaBD, periodosEncuestaRevisar, "key");

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
    public String generarEncuesta(CicloAcademico cicloAcademico, DataSessionPivot ds) {
        if (visorEncuestaCurso.iniciar()) {
            TipoExamenVirtual tipoEncuesta = tipoExamenVirtualDAO.findByEnum(TipoExamenVirtualEnum.ENC_CUR);
            ExamenVirtual encuestaModelo = examenVirtualDAO.findEncuestaActivaByTipo(tipoEncuesta);
            EncuestaEstudiantil encuesta = encuestaEstudiantilDAO.findByCicloEncuesta(cicloAcademico, encuestaModelo);

            Assert.isTrue(encuesta != null, "Aun no se ha activado la encuesta para este ciclo");
            Assert.isFalse(encuesta.getEstadoEnum() == EncuestaEstadoEnum.ACT, "Las encuestas ya fueron generadas");
            Assert.isFalse(encuesta.getEstadoEnum() == EncuestaEstadoEnum.CER, "Las encuestas ya fueron cerradas");

            ConfiguraEncuesta configuraEncuesta = configuraEncuestaDAO.findByEncuesta(encuesta);
            Assert.isFalse(configuraEncuesta == null, "Falta configurar la encuesta");

            generadorEncuestaCursoService.generarEncuesta(cicloAcademico, ds);
            return null;

        } else {
            return "No se puede iniciar la generación de encuestas de cursos";
        }
    }

    @Override
    @Transactional
    public void delete(EncuestaEstudiantil encuesta) {
        EncuestaEstudiantil encuestaDB = encuestaEstudiantilDAO.find(encuesta.getId());
        if (encuestaDB == null) {
            throw new PhobosException("La encuesta no existe");
        }
        ConfiguraEncuesta cfg = configuraEncuestaDAO.findByEncuesta(encuesta);
        boolean esSimultaneo = false;
        if (cfg != null) {
            esSimultaneo = (cfg.getSimultaneo() == 1);
        }

        List<EncuestaCurso> encuestasCur = encuestaCursoDAO.allByEncuestaEstudiantil(encuestaDB, esSimultaneo);

        encuestaAlumnoDAO.deleteByEncuestasCursos(encuestasCur);
        encuestaCursoDAO.deleteByEncuestaTipoCurso(encuesta);
        configuraEncuestaDAO.deleteByEncuestaEstudiantil(encuesta);
        periodoEncuestaDAO.deleteByEncuestaEstudiantil(encuesta);
        cursoSinEncuestaDAO.deleteByEncuestaEstudiantil(encuesta);
        encuestaEstudiantilDAO.delete(encuestaDB);
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

    @Override
    public ConfiguraEncuesta findConfigEncuestaCurso(CicloAcademico ciclo) {
        TipoExamenVirtual tipoEncuesta = tipoExamenVirtualDAO.findByEnum(TipoExamenVirtualEnum.ENC_CUR);
        if (tipoEncuesta == null) {
            return null;
        }
        ExamenVirtual encuestaModelo = examenVirtualDAO.findEncuestaActivaByTipo(tipoEncuesta);
        if (encuestaModelo == null) {
            return null;
        }
        EncuestaEstudiantil encuesta = encuestaEstudiantilDAO.findByCicloEncuesta(ciclo, encuestaModelo);
        if (encuesta == null) {
            return null;
        }
        return configuraEncuestaDAO.findByEncuesta(encuesta);
    }

    @Override
    public List<Facultad> allFacultadesFromCursos(CicloAcademico cicloAcademico) {
        return facultadDAO.allFromCursosByCiclo(cicloAcademico);
    }

    @Override
    public List<DepartamentoAcademico> allDepartamentosFromCursos(CicloAcademico cicloAcademico) {
        return departamentoAcademicoDAO.allFromCursosByCiclo(cicloAcademico);
    }

}
