package pe.edu.lamolina.amauta.controller.nivelacioneegg.matriculables;

import pe.edu.lamolina.amauta.controller.nivelacioneegg.alumnosnivelacion.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.alumnosnivelacion.helperalumnoniv.ChangeAlumnoNivelacionService;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.PrelamolinaDAO;
import pe.edu.lamolina.amauta.dao.admision.EvaluadoDAO;
import pe.edu.lamolina.amauta.dao.admision.TemaCicloDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.AlumnoNivelacionDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.ModalidadTemaCicloDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.NotaAlumnoNivelacionDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.calificacion.TemaCiclo;
import pe.edu.lamolina.model.calificacion.TemaExamen;
import static pe.edu.lamolina.model.enums.EstadoEnum.ACT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.INH;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.NMAT;
import static pe.edu.lamolina.model.enums.ModalidadIngresoEnum.CEPRE;
import pe.edu.lamolina.model.inscripcion.Evaluado;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.inscripcion.Prelamolina;
import pe.edu.lamolina.model.nivelacioneegg.AlumnoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.ModalidadTemaCiclo;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.alumnosnivelacion.helpernotaalumno.ChangeNotaAlumnoNivelacionService;
import pe.edu.lamolina.amauta.zelper.misc.Acumulador;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class MatriculablesNivelacionServiceImpl implements MatriculablesNivelacionService {

    private final AlumnoDAO alumnoDAO;
    private final AlumnoNivelacionDAO alumnoNivelacionDAO;
    private final EvaluadoDAO evaluadoDAO;
    private final ModalidadTemaCicloDAO modalidadTemaCicloDAO;
    private final NotaAlumnoNivelacionDAO notaAlumnoNivelacionDAO;
    private final PrelamolinaDAO prelamolinaDAO;
    private final TemaCicloDAO temaCicloDAO;

    private final ChangeNotaAlumnoNivelacionService changeNotaAlumnoNivelacionService;
    private final ChangeAlumnoNivelacionService changeAlumnoNivelacionService;

    private final BigDecimal VEINTE = new BigDecimal("20");

    @Override
    public List<AlumnoNivelacion> allAlumnosByDynatable(DynatableFilter filter, CicloAcademico ciclo) {
        List<AlumnoNivelacion> alumnosNiv = alumnoNivelacionDAO.allByDynatable(filter, ciclo);
        List<NotaAlumnoNivelacion> notasAlumnosAll = notaAlumnoNivelacionDAO.allByAlumnosNivelacion(alumnosNiv);

        List<TemaCiclo> temasCiclosAll = notasAlumnosAll.stream()
                .filter(na -> na.getTemaCiclo() != null)
                .map(na -> na.getTemaCiclo())
                .distinct()
                .collect(Collectors.toList());
        Map<Long, List<TemaCiclo>> mapTemas = temasCiclosAll.stream()
                .collect(Collectors.groupingBy(tc -> tc.getCicloPostula().getId()));

        Map<Long, List<NotaAlumnoNivelacion>> mapNotasAlumnos = notasAlumnosAll.stream()
                .collect(Collectors.groupingBy(nan -> nan.getAlumnoNivelacion().getId()));

        for (AlumnoNivelacion alumnoNiv : alumnosNiv) {
            List<NotaAlumnoNivelacion> notasAlumnos = TypesUtil.getListNotNull(mapNotasAlumnos.get(alumnoNiv.getId()));
            CicloPostula cicloPostula = notasAlumnos.stream()
                    .filter(na -> na.getTemaCiclo() != null)
                    .map(na -> na.getTemaCiclo().getCicloPostula())
                    .findFirst().get();
            List<TemaCiclo> temasCiclo = mapTemas.get(cicloPostula.getId());
            notasAlumnos.stream()
                    .filter(na -> na.getTemaCiclo() == null)
                    .forEach(na -> {
                        Acumulador acumulador = new Acumulador();
                        temasCiclo.stream()
                                .filter(tc -> tc.getTemaExamen().getTemaSuperior() != null)
                                .filter(tc -> tc.getTemaExamen().getTemaSuperior().equals(na.getTemaExamen()))
                                .forEach(tc -> acumulador.incrementar(tc.getPreguntas()));

                        TemaCiclo temaNul = new TemaCiclo();
                        temaNul.setPreguntas(acumulador.getValor());
                        temaNul.setTemaExamen(na.getTemaExamen());
                        na.setTemaCiclo(temaNul);
                    });

            alumnoNiv.setNotasNivelaciones(notasAlumnos);
        }

        return alumnosNiv;
    }

    @Override
    @Transactional
    public void createAlumnos(CicloAcademico ciclo, DataSessionPivot ds) {
        List<AlumnoNivelacion> nivelados = alumnoNivelacionDAO.allByCiclo(ciclo);
        Map<String, AlumnoNivelacion> mapNivelados = nivelados.stream()
                .collect(Collectors.toMap(aln -> aln.getAlumno().getCodigo(), Function.identity()));

        List<TemaCiclo> temasCiclo = temaCicloDAO.allByCiclo(ciclo);
        List<ModalidadTemaCiclo> configuraciones = this.getConfiguraciones(ciclo);

        Map<Long, ModalidadTemaCiclo> mapConfigOtro = configuraciones.stream()
                .filter(mtc -> mtc.getOtrasModalidades())
                .collect(Collectors.toMap(mtc -> mtc.getTemaCiclo().getTemaExamen().getId(), Function.identity()));

        Map<Long, ModalidadTemaCiclo> mapConfigCepre = configuraciones.stream()
                .filter(mtc -> mtc.getModalidadIngreso() != null)
                .filter(mtc -> mtc.getModalidadIngreso().getCodigo().equals(CEPRE.getCode()))
                .collect(Collectors.toMap(mtc -> mtc.getTemaCiclo().getTemaExamen().getId(), Function.identity()));

        List<Alumno> alumnos = alumnoDAO.allIngresantePregradoByCicloIngreso(ciclo);
        List<NotaAlumnoNivelacion> notasSave = new ArrayList();
        List<NotaAlumnoNivelacion> notasUpdate = new ArrayList();

        int nuevos = 0;
        for (Alumno alumno : alumnos) {
            boolean esNumero = this.esCodigoNumerico(alumno.getCodigo());
            AlumnoNivelacion alumnoNiv = mapNivelados.get(alumno.getCodigo());
            if (esNumero && alumnoNiv == null) {
                alumnoNiv = new AlumnoNivelacion();
                Postulante postulante = alumno.getPostulantePregrado();
                Evaluado evaluado = evaluadoDAO.findByPostulante(postulante);
                Prelamolina cepre = prelamolinaDAO.findIngresanteByPostulante(postulante);

                if (evaluado != null) {
                    alumnoNiv.setPuntajeFinal(this.fixPuntaje(evaluado.getPuntajeFinal()));
                    alumnoNiv.setNotaFinal(this.fixPuntaje(evaluado.getNotaFinal()));
                } else if (cepre != null) {
                    alumnoNiv.setPuntajeFinal(this.fixPuntaje(cepre.getPuntajeFinal()));
                } else {
                    Assert.isTrue(false, "No se puede determinar la notas de su examen de admisión");
                }

                alumnoNiv.setAlumno(alumno);
                alumnoNiv.setEvaluado(evaluado);
                alumnoNiv.setPrelamolina(cepre);
                alumnoNiv.setCicloAcademico(ciclo);
                alumnoNiv.setEstadoEnum(NMAT);
                alumnoNiv.setUserRegistro(ds.getUsuario());
                alumnoNiv.setFechaRegistro(new Date());
                alumnoNivelacionDAO.save(alumnoNiv);
                nuevos++;

                Map<Long, NotaAlumnoNivelacion> mapNotasAlumnos = new HashMap();

                if (evaluado != null) {
                    this.crearNotaEvaluado(alumnoNiv, mapNotasAlumnos, evaluado, temasCiclo, mapConfigOtro, notasSave, notasUpdate, ds);
                } else if (cepre != null) {
                    this.crearNotaPrelamolina(alumnoNiv, mapNotasAlumnos, cepre, temasCiclo, mapConfigCepre, notasSave, notasUpdate, ds);
                }
            }

            this.saveNotas(notasSave, true);
            this.updateNotas(notasUpdate, true);
        }

        this.saveNotas(notasSave, false);
        this.updateNotas(notasUpdate, false);
        Assert.isTrue(nuevos > 0, "No se han encontrado nuevos alumnos que agregar");
    }

    @Override
    @Transactional
    public int revisarTodosAlumnos(CicloAcademico ciclo, DataSessionPivot ds) {
        List<AlumnoNivelacion> nivelados = alumnoNivelacionDAO.allByCiclo(ciclo);
        List<AlumnoNivelacion> habiles = nivelados.stream()
                .filter(aluNiv -> aluNiv.getEstadoEnum() != INH)
                .collect(Collectors.toList());
        List<NotaAlumnoNivelacion> notasAlumnoAll = notaAlumnoNivelacionDAO.allByAlumnosNivelacion(habiles);
        Map<Long, List<NotaAlumnoNivelacion>> mapNotaAlumnos = notasAlumnoAll.stream()
                .collect(Collectors.groupingBy(nan -> nan.getAlumnoNivelacion().getId()));

        List<TemaCiclo> temasCiclo = temaCicloDAO.allByCiclo(ciclo);
        List<ModalidadTemaCiclo> configuraciones = this.getConfiguraciones(ciclo);

        Map<Long, ModalidadTemaCiclo> mapConfigOtro = configuraciones.stream()
                .filter(mtc -> mtc.getOtrasModalidades())
                .collect(Collectors.toMap(mtc -> mtc.getTemaCiclo().getTemaExamen().getId(), Function.identity()));

        Map<Long, ModalidadTemaCiclo> mapConfigCepre = configuraciones.stream()
                .filter(mtc -> mtc.getModalidadIngreso() != null)
                .filter(mtc -> mtc.getModalidadIngreso().getCodigo().equals(CEPRE.getCode()))
                .collect(Collectors.toMap(mtc -> mtc.getTemaCiclo().getTemaExamen().getId(), Function.identity()));

        List<NotaAlumnoNivelacion> notasSave = new ArrayList();
        List<NotaAlumnoNivelacion> notasUpdate = new ArrayList();

        int revisiones = 0;
        for (AlumnoNivelacion alumnoNiv : habiles) {
            List<NotaAlumnoNivelacion> notasAlumno = TypesUtil.getListNotNull(mapNotaAlumnos.get(alumnoNiv.getId()));
            Map<Long, NotaAlumnoNivelacion> mapNotasAlumnos = notasAlumno.stream()
                    .collect(Collectors.toMap(nan -> nan.getTemaExamen().getId(), Function.identity()));

            Evaluado evaluado = alumnoNiv.getEvaluado();
            Prelamolina cepre = alumnoNiv.getPrelamolina();

            if (evaluado != null) {
                this.crearNotaEvaluado(alumnoNiv, mapNotasAlumnos, evaluado, temasCiclo, mapConfigOtro, notasSave, notasUpdate, ds);
            } else if (cepre != null) {
                this.crearNotaPrelamolina(alumnoNiv, mapNotasAlumnos, cepre, temasCiclo, mapConfigCepre, notasSave, notasUpdate, ds);
            }

            revisiones += this.saveNotas(notasSave, true);
            revisiones += this.updateNotas(notasUpdate, true);
        }

        revisiones += this.saveNotas(notasSave, false);
        revisiones += this.updateNotas(notasUpdate, false);
        return revisiones;
    }

    @Override
    @Transactional
    public int revisarAlumno(AlumnoNivelacion alumnoNivForm, DataSessionPivot ds) {
        AlumnoNivelacion alumnoNiv = alumnoNivelacionDAO.find(alumnoNivForm.getId());
        Assert.isNotNull(alumnoNiv, "No se pudo ubicar del alumno que desea revisar");
        CicloAcademico ciclo = alumnoNiv.getCicloAcademico();
        List<TemaCiclo> temasCiclo = temaCicloDAO.allByCiclo(ciclo);

        List<ModalidadTemaCiclo> configuraciones = this.getConfiguraciones(ciclo);

        Map<Long, ModalidadTemaCiclo> mapConfigOtro = configuraciones.stream()
                .filter(mtc -> mtc.getOtrasModalidades())
                .collect(Collectors.toMap(mtc -> mtc.getTemaCiclo().getTemaExamen().getId(), Function.identity()));

        Map<Long, ModalidadTemaCiclo> mapConfigCepre = configuraciones.stream()
                .filter(mtc -> mtc.getModalidadIngreso() != null)
                .filter(mtc -> mtc.getModalidadIngreso().getCodigo().equals(CEPRE.getCode()))
                .collect(Collectors.toMap(mtc -> mtc.getTemaCiclo().getTemaExamen().getId(), Function.identity()));

        List<NotaAlumnoNivelacion> notasAlumno = notaAlumnoNivelacionDAO.allByAlumnoNivelacion(alumnoNiv);
        Map<Long, NotaAlumnoNivelacion> mapNotasAlumnos = notasAlumno.stream()
                .collect(Collectors.toMap(nan -> nan.getTemaExamen().getId(), Function.identity()));

        Evaluado evaluado = alumnoNiv.getEvaluado();
        Prelamolina cepre = alumnoNiv.getPrelamolina();
        List<NotaAlumnoNivelacion> notasSave = new ArrayList();
        List<NotaAlumnoNivelacion> notasUpdate = new ArrayList();

        if (evaluado != null) {
            this.crearNotaEvaluado(alumnoNiv, mapNotasAlumnos, evaluado, temasCiclo, mapConfigOtro, notasSave, notasUpdate, ds);
        } else if (cepre != null) {
            this.crearNotaPrelamolina(alumnoNiv, mapNotasAlumnos, cepre, temasCiclo, mapConfigCepre, notasSave, notasUpdate, ds);
        }

        int cambios = this.saveNotas(notasSave, false);
        cambios += this.updateNotas(notasUpdate, false);
        return cambios;
    }

    @Override
    public List<Alumno> searchAlumno(String nombre, DataSessionPivot ds) {
        return alumnoDAO.allByNamePregrado(nombre);
    }

    @Override
    @Transactional
    public void addAlumno(Alumno alumnoForm, CicloAcademico ciclo, DataSessionPivot ds) {
        Alumno alumno = alumnoDAO.find(alumnoForm);
        Assert.isNotNull(alumno, "No existe el alumno que desea agregar");
        Assert.isTrue(alumno.isPregrado(), "Solo apto para alumnos de pregrado");
        Assert.isNotNull(alumno.getPostulantePregrado(), "Este alumno no tiene información de postulante");

        boolean esNumero = this.esCodigoNumerico(alumno.getCodigo());
        Assert.isTrue(esNumero, "Este alumno no está habilitado para ser agregado");

        AlumnoNivelacion alumnoNiv = alumnoNivelacionDAO.findByAlumnoCiclo(alumno, ciclo);
        Assert.isNull(alumnoNiv, "Este alumno ya fue agregado");

        CicloAcademico cicloExamen = alumno.getPostulantePregrado().getCicloPostula().getCicloAcademico();
        List<TemaCiclo> temasCiclo = temaCicloDAO.allByCiclo(cicloExamen);

        List<ModalidadTemaCiclo> configuraciones = this.getConfiguraciones(ciclo);

        Map<Long, ModalidadTemaCiclo> mapConfigOtro = configuraciones.stream()
                .filter(mtc -> mtc.getOtrasModalidades())
                .collect(Collectors.toMap(mtc -> mtc.getTemaCiclo().getTemaExamen().getId(), Function.identity()));

        Map<Long, ModalidadTemaCiclo> mapConfigCepre = configuraciones.stream()
                .filter(mtc -> mtc.getModalidadIngreso() != null)
                .filter(mtc -> mtc.getModalidadIngreso().getCodigo().equals(CEPRE.getCode()))
                .collect(Collectors.toMap(mtc -> mtc.getTemaCiclo().getTemaExamen().getId(), Function.identity()));

        alumnoNiv = new AlumnoNivelacion();
        Postulante postulante = alumno.getPostulantePregrado();
        Evaluado evaluado = evaluadoDAO.findByPostulante(postulante);
        Prelamolina cepre = prelamolinaDAO.findIngresanteByPostulante(postulante);

        if (evaluado != null) {
            alumnoNiv.setPuntajeFinal(this.fixPuntaje(evaluado.getPuntajeFinal()));
            alumnoNiv.setNotaFinal(this.fixPuntaje(evaluado.getNotaFinal()));
        } else if (cepre != null) {
            alumnoNiv.setPuntajeFinal(cepre.getPuntajeFinal());
        } else {
            Assert.isTrue(false, "No se puede determinar la notas de su examen de admisión");
        }

        alumnoNiv.setAlumno(alumno);
        alumnoNiv.setEvaluado(evaluado);
        alumnoNiv.setPrelamolina(cepre);
        alumnoNiv.setCicloAcademico(ciclo);
        alumnoNiv.setEstadoEnum(NMAT);
        alumnoNiv.setUserRegistro(ds.getUsuario());
        alumnoNiv.setFechaRegistro(new Date());
        alumnoNivelacionDAO.save(alumnoNiv);

        Map<Long, NotaAlumnoNivelacion> mapNotasAlumnos = new HashMap();
        List<NotaAlumnoNivelacion> notasSave = new ArrayList();
        List<NotaAlumnoNivelacion> notasUpdate = new ArrayList();

        if (evaluado != null) {
            this.crearNotaEvaluado(alumnoNiv, mapNotasAlumnos, evaluado, temasCiclo, mapConfigOtro, notasSave, notasUpdate, ds);
        } else if (cepre != null) {
            this.crearNotaPrelamolina(alumnoNiv, mapNotasAlumnos, cepre, temasCiclo, mapConfigCepre, notasSave, notasUpdate, ds);
        }
        this.saveNotas(notasSave, false);
        this.updateNotas(notasUpdate, false);
    }

    @Override
    @Transactional
    public void deshabilitarAlumno(AlumnoNivelacion alumnoNivForm, DataSessionPivot ds) {
        AlumnoNivelacion alumnoNiv = alumnoNivelacionDAO.find(alumnoNivForm.getId());
        Assert.isNotNull(alumnoNiv, "No se pudo ubicar el registro del alumno que desea modificar");
        Assert.isFalse(alumnoNiv.getEstadoEnum() == MAT, "El alumno no debe estar matriculado en ningún curso");
        Assert.isTrue(alumnoNiv.getEstadoEnum() == NMAT, "El alumno debe tener el estado NO MATRICULADO");
        Assert.isNotNull(alumnoNivForm.getMotivo(), "No ha indicado el motivo");

        if (StringUtils.isBlank(alumnoNiv.getCambios())) {
            String cambios = changeAlumnoNivelacionService.createCambiosJson(alumnoNiv, null, alumnoNiv.getCambios());
            alumnoNiv.setCambios(cambios);
        }

        alumnoNiv.setEstadoEnum(INH);
        alumnoNiv.setUserModificacion(ds.getUsuario());
        alumnoNiv.setFechaModificacion(new Date());

        String cambiosTwo = changeAlumnoNivelacionService.createCambiosJson(alumnoNiv, alumnoNivForm.getMotivo(), alumnoNiv.getCambios());
        alumnoNiv.setCambios(cambiosTwo);
        alumnoNivelacionDAO.update(alumnoNiv);
    }

    @Override
    @Transactional
    public void habilitarAlumno(AlumnoNivelacion alumnoNivForm, DataSessionPivot ds) {
        AlumnoNivelacion alumnoNiv = alumnoNivelacionDAO.find(alumnoNivForm.getId());
        Assert.isNotNull(alumnoNiv, "No se pudo ubicar el registro del alumno que desea modificar");
        Assert.isTrue(alumnoNiv.getEstadoEnum() == INH, "El alumno ya no se encuentra deshabilitado");

        if (StringUtils.isBlank(alumnoNiv.getCambios())) {
            String cambios = changeAlumnoNivelacionService.createCambiosJson(alumnoNiv, null, alumnoNiv.getCambios());
            alumnoNiv.setCambios(cambios);
        }

        alumnoNiv.setEstadoEnum(NMAT);
        alumnoNiv.setUserModificacion(ds.getUsuario());
        alumnoNiv.setFechaModificacion(new Date());

        String cambiosTwo = changeAlumnoNivelacionService.createCambiosJson(alumnoNiv, alumnoNivForm.getMotivo(), alumnoNiv.getCambios());
        alumnoNiv.setCambios(cambiosTwo);
        alumnoNivelacionDAO.update(alumnoNiv);
    }

    private BigDecimal fixPuntaje(BigDecimal puntaje) {
        if (puntaje == null) {
            return null;
        }
        return puntaje.setScale(4, RoundingMode.DOWN);
    }

    private List<ModalidadTemaCiclo> getConfiguraciones(CicloAcademico ciclo) {
        List<ModalidadTemaCiclo> configuraciones = modalidadTemaCicloDAO.allByCiclo(ciclo);
        Assert.isFalse(configuraciones.isEmpty(), "No hay configuraciones de nota mínima de aprobación");

        List<ModalidadTemaCiclo> inactivos = configuraciones.stream()
                .filter(mtc -> mtc.getEstadoEnum() != ACT)
                .collect(Collectors.toList());
        Assert.isTrue(inactivos.isEmpty(), "Todas las configuraciones, de nota mínima de aprobación, deben estar activas");

        return configuraciones;
    }

    private int saveNotas(List<NotaAlumnoNivelacion> notasSave, boolean control) {
        int cambios = 0;
        if (!control) {
            cambios = notasSave.size();
            notaAlumnoNivelacionDAO.saveList(notasSave);
            notasSave.clear();

        } else if (notasSave.size() > 800) {
            cambios = notasSave.size();
            notaAlumnoNivelacionDAO.saveList(notasSave);
            notasSave.clear();
        }
        return cambios;
    }

    private int updateNotas(List<NotaAlumnoNivelacion> notasUpdate, boolean control) {
        int cambios = 0;
        if (!control) {
            cambios = notasUpdate.size();
            notaAlumnoNivelacionDAO.updateList(notasUpdate, "notaExamen", "puntajeExamen", "temaAprobado", "cambios", "userModificacion", "fechaModificacion");
            notasUpdate.clear();

        } else if (notasUpdate.size() > 800) {
            cambios = notasUpdate.size();
            notaAlumnoNivelacionDAO.updateList(notasUpdate, "notaExamen", "puntajeExamen", "temaAprobado", "cambios", "userModificacion", "fechaModificacion");
            notasUpdate.clear();
        }
        return cambios;
    }

    private void crearNotaPrelamolina(
            AlumnoNivelacion alumnoNiv,
            Map<Long, NotaAlumnoNivelacion> mapNotasAlumnos,
            Prelamolina cepre,
            List<TemaCiclo> temasCiclo,
            Map<Long, ModalidadTemaCiclo> mapConfig,
            List<NotaAlumnoNivelacion> notasSave,
            List<NotaAlumnoNivelacion> notasUpdate,
            DataSessionPivot ds) {

        DateTime today = new DateTime();
        List<String> codigos = new ArrayList();

        for (TemaCiclo temaCiclo : temasCiclo) {
            TemaExamen temaExamen = temaCiclo.getTemaExamen();
            ModalidadTemaCiclo config = mapConfig.get(temaExamen.getId());

            NotaAlumnoNivelacion notaBD = mapNotasAlumnos.get(temaExamen.getId());
            String dataInicio = this.comparableNotaAlumnoJson(notaBD);
            String cambios = null;

            NotaAlumnoNivelacion nota = new NotaAlumnoNivelacion();
            if (notaBD != null) {
                BeanUtils.copyProperties(notaBD, nota);
                if (StringUtils.isBlank(notaBD.getCambios())) {
                    cambios = changeNotaAlumnoNivelacionService.createCambiosJson(nota, null, notaBD.getCambios());
                }

            } else {
                nota.setAlumnoNivelacion(alumnoNiv);
                nota.setTemaCiclo(temaCiclo);
                nota.setTemaExamen(temaCiclo.getTemaExamen());
                nota.setEstadoEnum(NMAT);
                nota.setEsMatriculable(false);
                nota.setTemaAprobado(false);
                nota.setUserRegistro(ds.getUsuario());
                nota.setFechaRegistro(today.toDate());
            }

            TemaExamen temaSuperior = temaCiclo.getTemaExamen().getTemaSuperior();
            if (temaSuperior != null && !codigos.contains(temaSuperior.getCodigo())) {
                if (temaSuperior.getCodigo().equals("MAT")) {
                    this.crearMatematicasPrelamolina(alumnoNiv, cepre, temaSuperior, config, temasCiclo, mapNotasAlumnos, notasSave, notasUpdate, today, ds);
                    codigos.add(temaSuperior.getCodigo());
                }
            }

            if (temaExamen.getCodigo().equals("RV")) {
                this.saveNotaNivelacion(cepre.getPuntajeRv(), null, config.getPuntajeMinimo(), nota);

            } else if (temaExamen.getCodigo().equals("RM")) {
                this.saveNotaNivelacion(cepre.getPuntajeRm(), null, config.getPuntajeMinimo(), nota);

            } else if (temaExamen.getCodigo().equals("FIS")) {
                this.saveNotaNivelacion(cepre.getPuntajeFisica(), null, config.getPuntajeMinimo(), nota);

            } else if (temaExamen.getCodigo().equals("QUI")) {
                this.saveNotaNivelacion(cepre.getPuntajeQuimica(), null, config.getPuntajeMinimo(), nota);

            } else if (temaExamen.getCodigo().equals("BIO")) {
                this.saveNotaNivelacion(cepre.getPuntajeBiologia(), null, config.getPuntajeMinimo(), nota);

            } else if (temaExamen.getCodigo().equals("ARI")) {
                this.saveNotaNivelacion(cepre.getPuntajeAritmetica(), null, config.getPuntajeMinimo(), nota);

            } else if (temaExamen.getCodigo().equals("ALG")) {
                this.saveNotaNivelacion(cepre.getPuntajeAlgebra(), null, config.getPuntajeMinimo(), nota);

            } else if (temaExamen.getCodigo().equals("GEOM")) {
                this.saveNotaNivelacion(cepre.getPuntajeGeometria(), null, config.getPuntajeMinimo(), nota);

            } else if (temaExamen.getCodigo().equals("TRI")) {
                this.saveNotaNivelacion(cepre.getPuntajeTrigonometria(), null, config.getPuntajeMinimo(), nota);

            } else if (temaExamen.getCodigo().equals("ECO")) {
                this.saveNotaNivelacion(cepre.getPuntajeEconomia(), null, config.getPuntajeMinimo(), nota);

            } else if (temaExamen.getCodigo().equals("HIS")) {
                this.saveNotaNivelacion(cepre.getPuntajeHistoria(), null, config.getPuntajeMinimo(), nota);

            } else if (temaExamen.getCodigo().equals("GEOG")) {
                this.saveNotaNivelacion(cepre.getPuntajeGeografia(), null, config.getPuntajeMinimo(), nota);
            }

            if (nota.getPuntajeExamen() == null) {
                continue;
            }

            if (nota.getId() == null) {
                notasSave.add(nota);

            } else {
                String dataFinal = this.comparableNotaAlumnoJson(nota);
                if (!dataInicio.equals(dataFinal)) {
                    if (StringUtils.isBlank(nota.getCambios())) {
                        nota.setCambios(cambios);
                    }

                    nota.setUserModificacion(ds.getUsuario());
                    nota.setFechaModificacion(today.toDate());
                    String cambiosTwo = changeNotaAlumnoNivelacionService.createCambiosJson(nota, "Revisión de nota aprobatoria", nota.getCambios());
                    nota.setCambios(cambiosTwo);
                    notasUpdate.add(nota);
                }
            }
        }
    }

    private void crearNotaEvaluado(
            AlumnoNivelacion alumnoNiv,
            Map<Long, NotaAlumnoNivelacion> mapNotasAlumnos,
            Evaluado evaluado,
            List<TemaCiclo> temasCiclo,
            Map<Long, ModalidadTemaCiclo> mapConfig,
            List<NotaAlumnoNivelacion> notasSave,
            List<NotaAlumnoNivelacion> notasUpdate,
            DataSessionPivot ds) {

        DateTime today = new DateTime();
        List<String> codigos = new ArrayList();

        for (TemaCiclo temaCiclo : temasCiclo) {
            TemaExamen temaExamen = temaCiclo.getTemaExamen();
            ModalidadTemaCiclo config = mapConfig.get(temaExamen.getId());

            NotaAlumnoNivelacion notaBD = mapNotasAlumnos.get(temaExamen.getId());
            String dataInicio = this.comparableNotaAlumnoJson(notaBD);
            String cambios = null;

            NotaAlumnoNivelacion nota = new NotaAlumnoNivelacion();
            if (notaBD != null) {
                BeanUtils.copyProperties(notaBD, nota);
                if (StringUtils.isBlank(notaBD.getCambios())) {
                    cambios = changeNotaAlumnoNivelacionService.createCambiosJson(nota, null, notaBD.getCambios());
                }

            } else {
                nota.setAlumnoNivelacion(alumnoNiv);
                nota.setTemaCiclo(temaCiclo);
                nota.setTemaExamen(temaExamen);
                nota.setEstadoEnum(NMAT);
                nota.setEsMatriculable(false);
                nota.setTemaAprobado(false);
                nota.setUserRegistro(ds.getUsuario());
                nota.setFechaRegistro(today.toDate());
            }

            TemaExamen temaSuperior = temaCiclo.getTemaExamen().getTemaSuperior();
            if (temaSuperior != null) {
                nota.setEstadoEnum(INH);
            }

            if (temaSuperior != null && !codigos.contains(temaSuperior.getCodigo())) {
                if (temaSuperior.getCodigo().equals("MAT")) {
                    this.crearMatematicasEvaluado(alumnoNiv, evaluado, temaSuperior, config, temasCiclo, mapNotasAlumnos, notasSave, notasUpdate, today, ds);
                    codigos.add(temaSuperior.getCodigo());
                }
            }

            if (temaExamen.getCodigo().equals("RV")) {
                this.saveNotaNivelacion(evaluado.getPuntajeRv(), evaluado.getNotaRv(), config.getPuntajeMinimo(), nota);

            } else if (temaExamen.getCodigo().equals("RM")) {
                this.saveNotaNivelacion(evaluado.getPuntajeRm(), evaluado.getNotaRm(), config.getPuntajeMinimo(), nota);

            } else if (temaExamen.getCodigo().equals("FIS")) {
                this.saveNotaNivelacion(evaluado.getPuntajeFisica(), evaluado.getNotaFisica(), config.getPuntajeMinimo(), nota);

            } else if (temaExamen.getCodigo().equals("QUI")) {
                this.saveNotaNivelacion(evaluado.getPuntajeQuimica(), evaluado.getNotaQuimica(), config.getPuntajeMinimo(), nota);

            } else if (temaExamen.getCodigo().equals("BIO")) {
                this.saveNotaNivelacion(evaluado.getPuntajeBiologia(), evaluado.getNotaBiologia(), config.getPuntajeMinimo(), nota);

            } else if (temaExamen.getCodigo().equals("ARI")) {
                this.saveNotaNivelacion(evaluado.getPuntajeAritmetica(), evaluado.getNotaAritmetica(), config.getPuntajeMinimo(), nota);

            } else if (temaExamen.getCodigo().equals("ALG")) {
                this.saveNotaNivelacion(evaluado.getPuntajeAlgebra(), evaluado.getNotaAlgebra(), config.getPuntajeMinimo(), nota);

            } else if (temaExamen.getCodigo().equals("GEOM")) {
                this.saveNotaNivelacion(evaluado.getPuntajeGeometria(), evaluado.getNotaGeometria(), config.getPuntajeMinimo(), nota);

            } else if (temaExamen.getCodigo().equals("TRI")) {
                this.saveNotaNivelacion(evaluado.getPuntajeTrigonometria(), evaluado.getNotaTrigonometria(), config.getPuntajeMinimo(), nota);

            } else if (temaExamen.getCodigo().equals("ECO")) {
                this.saveNotaNivelacion(evaluado.getPuntajeEconomia(), evaluado.getNotaEconomia(), config.getPuntajeMinimo(), nota);

            } else if (temaExamen.getCodigo().equals("HIS")) {
                this.saveNotaNivelacion(evaluado.getPuntajeHistoria(), evaluado.getNotaHistoria(), config.getPuntajeMinimo(), nota);

            } else if (temaExamen.getCodigo().equals("GEOG")) {
                this.saveNotaNivelacion(evaluado.getPuntajeGeografia(), evaluado.getNotaGeografia(), config.getPuntajeMinimo(), nota);
            }

            if (nota.getPuntajeExamen() == null) {
                continue;
            }

            if (nota.getId() == null) {
                notasSave.add(nota);

            } else {
                String dataFinal = this.comparableNotaAlumnoJson(nota);
                if (!dataInicio.equals(dataFinal)) {
                    if (StringUtils.isBlank(nota.getCambios())) {
                        nota.setCambios(cambios);
                    }

                    nota.setUserModificacion(ds.getUsuario());
                    nota.setFechaModificacion(today.toDate());
                    String cambiosTwo = changeNotaAlumnoNivelacionService.createCambiosJson(nota, "Revisión de nota aprobatoria", nota.getCambios());
                    nota.setCambios(cambiosTwo);
                    notasUpdate.add(nota);
                }
            }
        }
    }

    private void crearMatematicasEvaluado(
            AlumnoNivelacion alumnoNiv,
            Evaluado evaluado,
            TemaExamen temaExamen,
            ModalidadTemaCiclo config,
            List<TemaCiclo> temasCiclo,
            Map<Long, NotaAlumnoNivelacion> mapNotasAlumnos,
            List<NotaAlumnoNivelacion> notasSave,
            List<NotaAlumnoNivelacion> notasUpdate,
            DateTime today,
            DataSessionPivot ds) {

        NotaAlumnoNivelacion notaBD = mapNotasAlumnos.get(temaExamen.getId());
        String dataInicio = this.comparableNotaAlumnoJson(notaBD);
        String cambios = null;

        NotaAlumnoNivelacion nota = new NotaAlumnoNivelacion();
        if (notaBD != null) {
            BeanUtils.copyProperties(notaBD, nota);
            if (StringUtils.isBlank(notaBD.getCambios())) {
                cambios = changeNotaAlumnoNivelacionService.createCambiosJson(nota, null, notaBD.getCambios());
            }

        } else {
            nota.setAlumnoNivelacion(alumnoNiv);
            nota.setTemaExamen(temaExamen);
            nota.setEstadoEnum(NMAT);
            nota.setEsMatriculable(false);
            nota.setTemaAprobado(false);
            nota.setUserRegistro(ds.getUsuario());
            nota.setFechaRegistro(today.toDate());
        }

        int preguntas = 0;
        BigDecimal puntaje = BigDecimal.ZERO;
        if (evaluado.getPuntajeAlgebra() != null) {
            puntaje = puntaje.add(evaluado.getPuntajeAlgebra());
            preguntas += this.getPreguntaso("ALG", temasCiclo);
        }
        if (evaluado.getPuntajeAritmetica() != null) {
            puntaje = puntaje.add(evaluado.getPuntajeAritmetica());
            preguntas += this.getPreguntaso("ARI", temasCiclo);
        }
        if (evaluado.getPuntajeGeometria() != null) {
            puntaje = puntaje.add(evaluado.getPuntajeGeometria());
            preguntas += this.getPreguntaso("GEOM", temasCiclo);
        }
        if (evaluado.getPuntajeTrigonometria() != null) {
            puntaje = puntaje.add(evaluado.getPuntajeTrigonometria());
            preguntas += this.getPreguntaso("TRI", temasCiclo);
        }

        BigDecimal notaFinal = BigDecimal.ZERO;
        if (puntaje.compareTo(BigDecimal.ZERO) != 0) {
            notaFinal = VEINTE.multiply(puntaje).divide(new BigDecimal(preguntas), 4, RoundingMode.HALF_UP);
        }

        this.saveNotaNivelacion(puntaje, notaFinal, config.getPuntajeMinimo(), nota);

        if (nota.getId() == null) {
            notasSave.add(nota);

        } else {
            String dataFinal = this.comparableNotaAlumnoJson(nota);
            if (!dataInicio.equals(dataFinal)) {
                if (StringUtils.isBlank(nota.getCambios())) {
                    nota.setCambios(cambios);
                }

                nota.setUserModificacion(ds.getUsuario());
                nota.setFechaModificacion(today.toDate());
                String cambiosTwo = changeNotaAlumnoNivelacionService.createCambiosJson(nota, "Revisión de nota aprobatoria", nota.getCambios());
                nota.setCambios(cambiosTwo);
                notasUpdate.add(nota);
            }
        }
    }

    private void crearMatematicasPrelamolina(
            AlumnoNivelacion alumnoNiv,
            Prelamolina prelamolina,
            TemaExamen temaExamen,
            ModalidadTemaCiclo config,
            List<TemaCiclo> temasCiclo,
            Map<Long, NotaAlumnoNivelacion> mapNotasAlumnos,
            List<NotaAlumnoNivelacion> notasSave,
            List<NotaAlumnoNivelacion> notasUpdate,
            DateTime today,
            DataSessionPivot ds) {

        NotaAlumnoNivelacion notaBD = mapNotasAlumnos.get(temaExamen.getId());
        String dataInicio = this.comparableNotaAlumnoJson(notaBD);
        String cambios = null;

        NotaAlumnoNivelacion nota = new NotaAlumnoNivelacion();
        if (notaBD != null) {
            BeanUtils.copyProperties(notaBD, nota);
            if (StringUtils.isBlank(notaBD.getCambios())) {
                cambios = changeNotaAlumnoNivelacionService.createCambiosJson(nota, null, notaBD.getCambios());
            }

        } else {
            nota.setAlumnoNivelacion(alumnoNiv);
            nota.setTemaExamen(temaExamen);
            nota.setEstadoEnum(NMAT);
            nota.setEsMatriculable(false);
            nota.setTemaAprobado(false);
            nota.setUserRegistro(ds.getUsuario());
            nota.setFechaRegistro(today.toDate());
        }

        int preguntas = 0;
        BigDecimal puntaje = BigDecimal.ZERO;
        if (prelamolina.getPuntajeAlgebra() != null) {
            puntaje = puntaje.add(prelamolina.getPuntajeAlgebra());
            preguntas += this.getPreguntaso("ALG", temasCiclo);
        }
        if (prelamolina.getPuntajeAritmetica() != null) {
            puntaje = puntaje.add(prelamolina.getPuntajeAritmetica());
            preguntas += this.getPreguntaso("ARI", temasCiclo);
        }
        if (prelamolina.getPuntajeGeometria() != null) {
            puntaje = puntaje.add(prelamolina.getPuntajeGeometria());
            preguntas += this.getPreguntaso("GEOM", temasCiclo);
        }
        if (prelamolina.getPuntajeTrigonometria() != null) {
            puntaje = puntaje.add(prelamolina.getPuntajeTrigonometria());
            preguntas += this.getPreguntaso("TRI", temasCiclo);
        }

        BigDecimal notaFinal = BigDecimal.ZERO;
        if (puntaje.compareTo(BigDecimal.ZERO) != 0) {
            notaFinal = VEINTE.multiply(puntaje).divide(new BigDecimal(preguntas), 4, RoundingMode.HALF_UP);
        }

        this.saveNotaNivelacion(puntaje, notaFinal, config.getPuntajeMinimo(), nota);

        if (nota.getId() == null) {
            notasSave.add(nota);

        } else {
            String dataFinal = this.comparableNotaAlumnoJson(nota);
            if (!dataInicio.equals(dataFinal)) {
                if (StringUtils.isBlank(nota.getCambios())) {
                    nota.setCambios(cambios);
                }

                nota.setUserModificacion(ds.getUsuario());
                nota.setFechaModificacion(today.toDate());
                String cambiosTwo = changeNotaAlumnoNivelacionService.createCambiosJson(nota, "Revisión de nota aprobatoria", nota.getCambios());
                nota.setCambios(cambiosTwo);
                notasUpdate.add(nota);
            }
        }
    }

    private Integer getPreguntaso(String codigo, List<TemaCiclo> temasCiclo) {
        TemaCiclo temaCiclo = temasCiclo.stream()
                .filter(tc -> tc.getTemaExamen().getCodigo().equals(codigo))
                .findFirst().orElse(null);
        if (temaCiclo == null) {
            return 0;
        }
        return temaCiclo.getPreguntas();
    }

    private void saveNotaNivelacion(
            BigDecimal puntaje,
            BigDecimal nota,
            BigDecimal puntajeBase,
            NotaAlumnoNivelacion notaAlumno) {

        if (puntaje != null) {
            notaAlumno.setPuntajeExamen(this.fixPuntaje(puntaje));
            notaAlumno.setNotaExamen(this.fixPuntaje(nota));
            notaAlumno.setTemaAprobado(puntaje.compareTo(puntajeBase) >= 0);
        }
    }

    private boolean esCodigoNumerico(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            return false;
        }

        try {
            Integer.parseInt(codigo);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String comparableNotaAlumnoJson(NotaAlumnoNivelacion nota) {
        if (nota == null) {
            return "";
        }
        return JaneHelper.from(nota)
                .only("notaExamen,puntajeExamen,temaAprobado")
                .json().toString();
    }

}
