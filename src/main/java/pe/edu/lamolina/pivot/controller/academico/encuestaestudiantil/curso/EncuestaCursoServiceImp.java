package pe.edu.lamolina.pivot.controller.academico.encuestaestudiantil.curso;

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
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.encuestaestudiantil.ConfiguraEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaAlumno;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaCurso;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.encuestaestudiantil.PeriodoEncuesta;
import pe.edu.lamolina.model.enums.EncuestaEstadoEnum;
import pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.TipoExamenVirtual;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.encuesta.ConfiguraEncuestaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.CursoSinEncuestaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaAlumnoDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaCursoDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaEstudiantilDAO;
import pe.edu.lamolina.pivot.dao.encuesta.ExamenVirtualDAO;
import pe.edu.lamolina.pivot.dao.encuesta.PeriodoEncuestaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.TipoExamenVirtualDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<EncuestaCurso> allEncuestaCurso(DynatableFilter filter, CicloAcademico ciclo) {
        List<EncuestaCurso> encuestas = encuestaCursoDAO.allByDynatable(filter, ciclo);
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
                List<DocenteSeccion> docentesSecc = mapProfesSecciones.get(gpoSecc.getId());
                docentesSecc = (docentesSecc == null) ? new ArrayList() : docentesSecc;
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
        EncuestaCurso encuesta = encuestaCursoDAO.findEncuestaCurso(encuestaForm);
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
    public EncuestaEstudiantil findEncuestaCurso(CicloAcademico cicloAcademico) {
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
            if (cfg != null) {
                encuesta.getConfiguraEncuesta().add(cfg);
            }
            encuesta.setCursosNoEncuestar(cursoSinEncuestaDAO.allByEncuestaEstudiantil(encuesta));
            List<EncuestaCurso> encCursos = encuestaCursoDAO.allByEncuestaEstudiantil(encuesta);
            int activos = 0;
            int anulados = 0;
            int innecesa = 0;
            int cerrados = 0;
            int sinperio = 0;
            for (EncuestaCurso enCurso : encCursos) {
                switch (enCurso.getEstadoEnum()) {
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
    @Transactional
    public void activarEncuesta(CicloAcademico cicloAcademico, DataSessionPivot ds) {

        TipoExamenVirtual tipoEncuesta = tipoExamenVirtualDAO.findByEnum(TipoExamenVirtualEnum.ENC_CUR);
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
    @Transactional
    public void saveDetalleConfigEncuesta(EncuestaEstudiantil encuestaForm, CicloAcademico ciclo, DataSessionPivot ds) {
        TipoExamenVirtual tipoEncuesta = tipoExamenVirtualDAO.findByEnum(TipoExamenVirtualEnum.ENC_CUR);
        ExamenVirtual encuestaModelo = examenVirtualDAO.findEncuestaActivaByTipo(tipoEncuesta);
        EncuestaEstudiantil encuestaBD = encuestaEstudiantilDAO.findByCicloEncuesta(ciclo, encuestaModelo);
        Assert.isTrue(encuestaBD != null, "Aún no se ha activado la encuesta");

        boolean esSinConfig = encuestaBD.getEstadoEnum() == EncuestaEstadoEnum.CRE;
        boolean esSinData = encuestaBD.getEstadoEnum() == EncuestaEstadoEnum.CFG && encuestaBD.getObjetivosEncuesta() == 0;
        Assert.isTrue(esSinConfig || esSinData, "Ya no puede configurar esta encuesta");

        updateConfigEncuesta(encuestaBD, encuestaForm.getConfiguraEncuesta().get(0), ciclo, ds);
        updateConfigEncuesta(encuestaBD, encuestaForm.getPeriodosEncuesta(), ciclo, ds);

    }

    private void updateConfigEncuesta(EncuestaEstudiantil encuesta, ConfiguraEncuesta configuraEncuestaForm, CicloAcademico ciclo, DataSessionPivot ds) {

        ConfiguraEncuesta configuraEncuestaBD = configuraEncuestaDAO.findByEncuesta(encuesta);
        if (configuraEncuestaBD == null) {
            configuraEncuestaForm.setEncuestaEstudiantil(encuesta);
            configuraEncuestaForm.setCantidadMaximaDocentes(0L);
            configuraEncuestaForm.setDiasEncuesta(0L);
            configuraEncuestaForm.setFechaRegistro(new Date());
            configuraEncuestaForm.setUserRegistro(ds.getUsuario());
            configuraEncuestaForm.setEncuestaTeoriaPractica(configuraEncuestaForm.getEncuestaTeoriaPractica() == null ? 0L : 1L);
            configuraEncuestaDAO.save(configuraEncuestaForm);
        } else {
            configuraEncuestaBD.setCantidadMaximaDocentes(0L);
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
            return "No se puede iniciar la generación de encuestas de docentes";
        }
    }
}
