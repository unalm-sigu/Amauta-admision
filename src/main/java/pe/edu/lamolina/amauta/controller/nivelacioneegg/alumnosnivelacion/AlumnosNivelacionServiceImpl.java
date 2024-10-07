package pe.edu.lamolina.amauta.controller.nivelacioneegg.alumnosnivelacion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.TypesUtil;
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
import static pe.edu.lamolina.model.enums.ModalidadIngresoEnum.CEPRE;
import pe.edu.lamolina.model.inscripcion.Evaluado;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.inscripcion.Prelamolina;
import pe.edu.lamolina.model.nivelacioneegg.AlumnoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.ModalidadTemaCiclo;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class AlumnosNivelacionServiceImpl implements AlumnosNivelacionService {

    private final AlumnoDAO alumnoDAO;
    private final AlumnoNivelacionDAO alumnoNivelacionDAO;
    private final EvaluadoDAO evaluadoDAO;
    private final ModalidadTemaCicloDAO modalidadTemaCicloDAO;
    private final NotaAlumnoNivelacionDAO notaAlumnoNivelacionDAO;
    private final PrelamolinaDAO prelamolinaDAO;
    private final TemaCicloDAO temaCicloDAO;

    @Override
    public List<AlumnoNivelacion> allAlumnosByDynatable(DynatableFilter filter, CicloAcademico ciclo) {
        List<AlumnoNivelacion> alumnosNiv = alumnoNivelacionDAO.allByDynatable(filter, ciclo);
        List<NotaAlumnoNivelacion> notasAlumnosAll = notaAlumnoNivelacionDAO.allByAlumnosNivelacion(alumnosNiv);
        Map<Long, List<NotaAlumnoNivelacion>> mapNotasAlumnos = notasAlumnosAll.stream()
                .collect(Collectors.groupingBy(nan -> nan.getAlumnoNivelacion().getId()));

        for (AlumnoNivelacion alumnoNiv : alumnosNiv) {
            List<NotaAlumnoNivelacion> notasAlumnos = TypesUtil.getListNotNull(mapNotasAlumnos.get(alumnoNiv.getId()));
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
        List<ModalidadTemaCiclo> configuraciones = modalidadTemaCicloDAO.allByCiclo(ciclo);
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
                    alumnoNiv.setPuntajeFinal(evaluado.getPuntajeFinal());
                    alumnoNiv.setNotaFinal(evaluado.getNotaFinal());
                } else if (cepre != null) {
                    alumnoNiv.setPuntajeFinal(cepre.getPuntajeFinal());
                } else {
                    Assert.isTrue(false, "No se puede determinar la notas de su examen de admisión");
                }

                alumnoNiv.setAlumno(alumno);
                alumnoNiv.setEvaluado(evaluado);
                alumnoNiv.setPrelamolina(cepre);
                alumnoNiv.setCicloAcademico(ciclo);
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
    public void revisarTodosAlumnos(CicloAcademico ciclo, DataSessionPivot ds) {
        List<AlumnoNivelacion> nivelados = alumnoNivelacionDAO.allByCiclo(ciclo);
        List<TemaCiclo> temasCiclo = temaCicloDAO.allByCiclo(ciclo);
        List<ModalidadTemaCiclo> configuraciones = modalidadTemaCicloDAO.allByCiclo(ciclo);

        Map<Long, ModalidadTemaCiclo> mapConfigOtro = configuraciones.stream()
                .filter(mtc -> mtc.getOtrasModalidades())
                .collect(Collectors.toMap(mtc -> mtc.getTemaCiclo().getTemaExamen().getId(), Function.identity()));

        Map<Long, ModalidadTemaCiclo> mapConfigCepre = configuraciones.stream()
                .filter(mtc -> mtc.getModalidadIngreso() != null)
                .filter(mtc -> mtc.getModalidadIngreso().getCodigo().equals(CEPRE.getCode()))
                .collect(Collectors.toMap(mtc -> mtc.getTemaCiclo().getTemaExamen().getId(), Function.identity()));

        List<NotaAlumnoNivelacion> notasSave = new ArrayList();
        List<NotaAlumnoNivelacion> notasUpdate = new ArrayList();

        for (AlumnoNivelacion alumnoNiv : nivelados) {
            List<NotaAlumnoNivelacion> notasAlumno = notaAlumnoNivelacionDAO.allByAlumnoNivelacion(alumnoNiv);
            Map<Long, NotaAlumnoNivelacion> mapNotasAlumnos = notasAlumno.stream()
                    .collect(Collectors.toMap(nan -> nan.getTemaCiclo().getTemaExamen().getId(), Function.identity()));

            Evaluado evaluado = alumnoNiv.getEvaluado();
            Prelamolina cepre = alumnoNiv.getPrelamolina();

            if (evaluado != null) {
                this.crearNotaEvaluado(alumnoNiv, mapNotasAlumnos, evaluado, temasCiclo, mapConfigOtro, notasSave, notasUpdate, ds);
            } else if (cepre != null) {
                this.crearNotaPrelamolina(alumnoNiv, mapNotasAlumnos, cepre, temasCiclo, mapConfigCepre, notasSave, notasUpdate, ds);
            }
            this.saveNotas(notasSave, true);
            this.updateNotas(notasUpdate, true);
        }
        this.saveNotas(notasSave, false);
        this.updateNotas(notasUpdate, false);
    }

    @Override
    @Transactional
    public void revisarAlumno(AlumnoNivelacion alumnoNivForm, DataSessionPivot ds) {
        AlumnoNivelacion alumnoNiv = alumnoNivelacionDAO.find(alumnoNivForm.getId());
        Assert.isNotNull(alumnoNiv, "No se pudo ubicar del alumno que desea revisar");
        CicloAcademico ciclo = alumnoNiv.getCicloAcademico();
        List<TemaCiclo> temasCiclo = temaCicloDAO.allByCiclo(ciclo);

        List<ModalidadTemaCiclo> configuraciones = modalidadTemaCicloDAO.allByCiclo(ciclo);
        Map<Long, ModalidadTemaCiclo> mapConfigOtro = configuraciones.stream()
                .filter(mtc -> mtc.getOtrasModalidades())
                .collect(Collectors.toMap(mtc -> mtc.getTemaCiclo().getTemaExamen().getId(), Function.identity()));

        Map<Long, ModalidadTemaCiclo> mapConfigCepre = configuraciones.stream()
                .filter(mtc -> mtc.getModalidadIngreso() != null)
                .filter(mtc -> mtc.getModalidadIngreso().getCodigo().equals(CEPRE.getCode()))
                .collect(Collectors.toMap(mtc -> mtc.getTemaCiclo().getTemaExamen().getId(), Function.identity()));

        List<NotaAlumnoNivelacion> notasAlumno = notaAlumnoNivelacionDAO.allByAlumnoNivelacion(alumnoNiv);
        Map<Long, NotaAlumnoNivelacion> mapNotasAlumnos = notasAlumno.stream()
                .collect(Collectors.toMap(nan -> nan.getTemaCiclo().getTemaExamen().getId(), Function.identity()));

        Evaluado evaluado = alumnoNiv.getEvaluado();
        Prelamolina cepre = alumnoNiv.getPrelamolina();
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

        List<ModalidadTemaCiclo> configuraciones = modalidadTemaCicloDAO.allByCiclo(ciclo);
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
            alumnoNiv.setPuntajeFinal(evaluado.getPuntajeFinal());
            alumnoNiv.setNotaFinal(evaluado.getNotaFinal());
        } else if (cepre != null) {
            alumnoNiv.setPuntajeFinal(cepre.getPuntajeFinal());
        } else {
            Assert.isTrue(false, "No se puede determinar la notas de su examen de admisión");
        }

        alumnoNiv.setAlumno(alumno);
        alumnoNiv.setEvaluado(evaluado);
        alumnoNiv.setPrelamolina(cepre);
        alumnoNiv.setCicloAcademico(ciclo);
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

    private void saveNotas(List<NotaAlumnoNivelacion> notasSave, boolean control) {
        if (!control) {
            notaAlumnoNivelacionDAO.saveList(notasSave);
            notasSave.clear();
        } else if (notasSave.size() > 800) {
            notaAlumnoNivelacionDAO.saveList(notasSave);
            notasSave.clear();
        }
    }

    private void updateNotas(List<NotaAlumnoNivelacion> notasUpdate, boolean control) {
        if (!control) {
            notaAlumnoNivelacionDAO.updateList(notasUpdate, "notaExamen", "puntajeExamen", "temaAprobado");
            notasUpdate.clear();
        } else if (notasUpdate.size() > 800) {
            notaAlumnoNivelacionDAO.updateList(notasUpdate, "notaExamen", "puntajeExamen", "temaAprobado");
            notasUpdate.clear();
        }
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

        for (TemaCiclo temaCiclo : temasCiclo) {
            TemaExamen temaExamen = temaCiclo.getTemaExamen();
            ModalidadTemaCiclo config = mapConfig.get(temaExamen.getId());

            NotaAlumnoNivelacion nota = mapNotasAlumnos.get(temaExamen.getId());
            if (nota == null) {
                nota = new NotaAlumnoNivelacion();
            }

            nota.setAlumnoNivelacion(alumnoNiv);
            nota.setTemaCiclo(temaCiclo);
            nota.setEsMatriculable(false);
            nota.setTemaAprobado(false);
            nota.setUserRegistro(ds.getUsuario());
            nota.setFechaRegistro(today.toDate());

            if (temaExamen.getCodigo().equals("RV")) {
                this.saveNotaNivelacion(cepre.getPuntajeRv(), null, config.getPuntajeMinimo(), nota, notasSave, notasUpdate);

            } else if (temaExamen.getCodigo().equals("RM")) {
                this.saveNotaNivelacion(cepre.getPuntajeRm(), null, config.getPuntajeMinimo(), nota, notasSave, notasUpdate);

            } else if (temaExamen.getCodigo().equals("FIS")) {
                this.saveNotaNivelacion(cepre.getPuntajeFisica(), null, config.getPuntajeMinimo(), nota, notasSave, notasUpdate);

            } else if (temaExamen.getCodigo().equals("QUI")) {
                this.saveNotaNivelacion(cepre.getPuntajeQuimica(), null, config.getPuntajeMinimo(), nota, notasSave, notasUpdate);

            } else if (temaExamen.getCodigo().equals("BIO")) {
                this.saveNotaNivelacion(cepre.getPuntajeBiologia(), null, config.getPuntajeMinimo(), nota, notasSave, notasUpdate);

            } else if (temaExamen.getCodigo().equals("ARI")) {
                this.saveNotaNivelacion(cepre.getPuntajeAritmetica(), null, config.getPuntajeMinimo(), nota, notasSave, notasUpdate);

            } else if (temaExamen.getCodigo().equals("ALG")) {
                this.saveNotaNivelacion(cepre.getPuntajeAlgebra(), null, config.getPuntajeMinimo(), nota, notasSave, notasUpdate);

            } else if (temaExamen.getCodigo().equals("GEOM")) {
                this.saveNotaNivelacion(cepre.getPuntajeGeometria(), null, config.getPuntajeMinimo(), nota, notasSave, notasUpdate);

            } else if (temaExamen.getCodigo().equals("TRI")) {
                this.saveNotaNivelacion(cepre.getPuntajeTrigonometria(), null, config.getPuntajeMinimo(), nota, notasSave, notasUpdate);

            } else if (temaExamen.getCodigo().equals("ECO")) {
                this.saveNotaNivelacion(cepre.getPuntajeEconomia(), null, config.getPuntajeMinimo(), nota, notasSave, notasUpdate);

            } else if (temaExamen.getCodigo().equals("HIS")) {
                this.saveNotaNivelacion(cepre.getPuntajeHistoria(), null, config.getPuntajeMinimo(), nota, notasSave, notasUpdate);

            } else if (temaExamen.getCodigo().equals("GEOG")) {
                this.saveNotaNivelacion(cepre.getPuntajeGeografia(), null, config.getPuntajeMinimo(), nota, notasSave, notasUpdate);
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

        for (TemaCiclo temaCiclo : temasCiclo) {
            TemaExamen temaExamen = temaCiclo.getTemaExamen();
            ModalidadTemaCiclo config = mapConfig.get(temaExamen.getId());

            NotaAlumnoNivelacion nota = mapNotasAlumnos.get(temaExamen.getId());
            if (nota == null) {
                nota = new NotaAlumnoNivelacion();
            }

            nota.setAlumnoNivelacion(alumnoNiv);
            nota.setTemaCiclo(temaCiclo);
            nota.setEsMatriculable(false);
            nota.setUserRegistro(ds.getUsuario());
            nota.setFechaRegistro(new Date());

            if (temaExamen.getCodigo().equals("RV")) {
                this.saveNotaNivelacion(evaluado.getPuntajeRv(), evaluado.getNotaRv(), config.getPuntajeMinimo(), nota, notasSave, notasUpdate);

            } else if (temaExamen.getCodigo().equals("RM")) {
                this.saveNotaNivelacion(evaluado.getPuntajeRm(), evaluado.getNotaRm(), config.getPuntajeMinimo(), nota, notasSave, notasUpdate);

            } else if (temaExamen.getCodigo().equals("FIS")) {
                this.saveNotaNivelacion(evaluado.getPuntajeFisica(), evaluado.getNotaFisica(), config.getPuntajeMinimo(), nota, notasSave, notasUpdate);

            } else if (temaExamen.getCodigo().equals("QUI")) {
                this.saveNotaNivelacion(evaluado.getPuntajeQuimica(), evaluado.getNotaQuimica(), config.getPuntajeMinimo(), nota, notasSave, notasUpdate);

            } else if (temaExamen.getCodigo().equals("BIO")) {
                this.saveNotaNivelacion(evaluado.getPuntajeBiologia(), evaluado.getNotaBiologia(), config.getPuntajeMinimo(), nota, notasSave, notasUpdate);

            } else if (temaExamen.getCodigo().equals("ARI")) {
                this.saveNotaNivelacion(evaluado.getPuntajeAritmetica(), evaluado.getNotaAritmetica(), config.getPuntajeMinimo(), nota, notasSave, notasUpdate);

            } else if (temaExamen.getCodigo().equals("ALG")) {
                this.saveNotaNivelacion(evaluado.getPuntajeAlgebra(), evaluado.getNotaAlgebra(), config.getPuntajeMinimo(), nota, notasSave, notasUpdate);

            } else if (temaExamen.getCodigo().equals("GEOM")) {
                this.saveNotaNivelacion(evaluado.getPuntajeGeometria(), evaluado.getNotaGeometria(), config.getPuntajeMinimo(), nota, notasSave, notasUpdate);

            } else if (temaExamen.getCodigo().equals("TRI")) {
                this.saveNotaNivelacion(evaluado.getPuntajeTrigonometria(), evaluado.getNotaTrigonometria(), config.getPuntajeMinimo(), nota, notasSave, notasUpdate);

            } else if (temaExamen.getCodigo().equals("ECO")) {
                this.saveNotaNivelacion(evaluado.getPuntajeEconomia(), evaluado.getNotaEconomia(), config.getPuntajeMinimo(), nota, notasSave, notasUpdate);

            } else if (temaExamen.getCodigo().equals("HIS")) {
                this.saveNotaNivelacion(evaluado.getPuntajeHistoria(), evaluado.getNotaHistoria(), config.getPuntajeMinimo(), nota, notasSave, notasUpdate);

            } else if (temaExamen.getCodigo().equals("GEOG")) {
                this.saveNotaNivelacion(evaluado.getPuntajeGeografia(), evaluado.getNotaGeografia(), config.getPuntajeMinimo(), nota, notasSave, notasUpdate);
            }
        }
    }

    private void saveNotaNivelacion(
            BigDecimal puntaje,
            BigDecimal nota,
            BigDecimal puntajeBase,
            NotaAlumnoNivelacion notaAlumno,
            List<NotaAlumnoNivelacion> notasSave,
            List<NotaAlumnoNivelacion> notasUpdate) {

        if (puntaje != null) {
            notaAlumno.setPuntajeExamen(puntaje);
            notaAlumno.setNotaExamen(nota);
            notaAlumno.setTemaAprobado(puntaje.compareTo(puntajeBase) >= 0);

            if (notaAlumno.getId() == null) {
                notasSave.add(notaAlumno);
            } else {
                notasUpdate.add(notaAlumno);
            }
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

}
