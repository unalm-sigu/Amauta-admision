package pe.edu.lamolina.amauta.controller.nivelacioneegg.confignotanivelacion;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.amauta.dao.academico.PrelamolinaDAO;
import pe.edu.lamolina.amauta.dao.admision.EvaluadoDAO;
import pe.edu.lamolina.amauta.dao.admision.ModalidadIngresoDAO;
import pe.edu.lamolina.amauta.dao.admision.TemaCicloDAO;
import pe.edu.lamolina.amauta.dao.encuesta.CicloPostulaDAO;
import pe.edu.lamolina.amauta.dao.inscripcion.EventoCicloDAO;
import pe.edu.lamolina.amauta.dao.inscripcion.EventoDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.ModalidadTemaCicloDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.NotaAlumnoNivelacionDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.calificacion.TemaCiclo;
import pe.edu.lamolina.model.calificacion.TemaExamen;
import static pe.edu.lamolina.model.enums.EstadoEnum.ACT;
import static pe.edu.lamolina.model.enums.EstadoEnum.PEN;
import static pe.edu.lamolina.model.enums.EventoEnum.EXAM;
import pe.edu.lamolina.model.enums.ModalidadIngresoEnum;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Evaluado;
import pe.edu.lamolina.model.inscripcion.Evento;
import pe.edu.lamolina.model.inscripcion.EventoCiclo;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.Prelamolina;
import pe.edu.lamolina.model.nivelacioneegg.ModalidadTemaCiclo;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class ConfigNotaNivelacionServiceImpl implements ConfigNotaNivelacionService {

    private final CicloPostulaDAO cicloPostulaDAO;
    private final EvaluadoDAO evaluadoDAO;
    private final EventoCicloDAO eventoCicloDAO;
    private final EventoDAO eventoDAO;
    private final ModalidadIngresoDAO modalidadIngresoDAO;
    private final ModalidadTemaCicloDAO modalidadTemaCicloDAO;
    private final NotaAlumnoNivelacionDAO notaAlumnoNivelacionDAO;
    private final PrelamolinaDAO prelamolinaDAO;
    private final TemaCicloDAO temaCicloDAO;

    private final BigDecimal ONCE = new BigDecimal("10.5");
    private final BigDecimal VEINTE = new BigDecimal("20");
    private final BigDecimal CIEN = new BigDecimal("100");
    private final BigDecimal CIEN_NEG = new BigDecimal("-100");

    private final VerificadorService verificadorService;

    private void verificarPermiso(DataSessionPivot ds) {
        boolean esOperador = verificadorService.esOperadorEEGG(ds);
        Assert.isTrue(esOperador, "No tiene permiso para ejecutar esta operación");
    }

    @Override
    @Transactional
    public void revisarNotasExamen(CicloAcademico ciclo, DataSessionPivot ds) {
        boolean esOperadorEEGG = verificadorService.esOperadorEEGG(ds);
        if (esOperadorEEGG) {
            this.crearDatosIniciales(ciclo, ds);
        }

    }

    private void crearDatosIniciales(CicloAcademico ciclo, DataSessionPivot ds) {
        this.verificarPermiso(ds);

        List<TemaCiclo> temasCiclo = temaCicloDAO.allByCiclo(ciclo);
        if (temasCiclo.isEmpty()) {
            return;
        }

        List<TemaCiclo> sinMinimos = temasCiclo.stream()
                .filter(tc -> tc.getPuntajeMaximo() == null)
                .collect(Collectors.toList());

        if (sinMinimos.isEmpty()) {
            return;
        }

        CicloPostula cicloPostula = cicloPostulaDAO.findByCicloAcademico(ciclo);
        if (cicloPostula == null) {
            return;
        }

        Evento examen = eventoDAO.findByCode(EXAM.name());
        List<EventoCiclo> eventosExamen = eventoCicloDAO.allByEventoCiclo(examen, cicloPostula);
        if (eventosExamen.isEmpty()) {
            return;
        }

        Optional<EventoCiclo> fechaMaxima = eventosExamen.stream()
                .max(Comparator.comparing(EventoCiclo::getFechaInicio));
        Date fechaExamen = new LocalDate(fechaMaxima.get().getFechaInicio()).toDate();
        Date hoy = new LocalDate().toDate();
        if (!hoy.after(fechaExamen)) {
            return;
        }

        List<Evaluado> evaluados = evaluadoDAO.allByCiclo(ciclo);
        if (evaluados.isEmpty()) {
            return;
        }

        List<NotaAlumnoNivelacion> notasExamen = notaAlumnoNivelacionDAO.allByCiclo(ciclo);
        if (!notasExamen.isEmpty()) {
            for (TemaCiclo temaCiclo : temasCiclo) {
                Optional<NotaAlumnoNivelacion> puntajeMin = notasExamen.stream()
                        .filter(ne -> ne.getTemaCiclo().equals(temaCiclo))
                        .filter(ne -> ne.getPuntajeExamen() != null)
                        .min(Comparator.comparing(NotaAlumnoNivelacion::getPuntajeExamen));
                Optional<NotaAlumnoNivelacion> puntajeMax = notasExamen.stream()
                        .filter(ne -> ne.getTemaCiclo().equals(temaCiclo))
                        .filter(ne -> ne.getPuntajeExamen() != null)
                        .max(Comparator.comparing(NotaAlumnoNivelacion::getPuntajeExamen));

                Optional<NotaAlumnoNivelacion> notaMin = notasExamen.stream()
                        .filter(ne -> ne.getTemaCiclo().equals(temaCiclo))
                        .filter(ne -> ne.getNotaExamen() != null)
                        .min(Comparator.comparing(NotaAlumnoNivelacion::getNotaExamen));
                Optional<NotaAlumnoNivelacion> notaMax = notasExamen.stream()
                        .filter(ne -> ne.getTemaCiclo().equals(temaCiclo))
                        .filter(ne -> ne.getNotaExamen() != null)
                        .max(Comparator.comparing(NotaAlumnoNivelacion::getNotaExamen));

                temaCiclo.setPuntajeMinimo(puntajeMin.get().getPuntajeExamen());
                temaCiclo.setPuntajeMaximo(puntajeMax.get().getPuntajeExamen());
                temaCiclo.setNotaMinima(notaMin.get().getNotaExamen());
                temaCiclo.setNotaMaxima(notaMax.get().getNotaExamen());
                temaCicloDAO.update(temaCiclo);
            }
            return;
        }

        List<Prelamolina> ingresantes = prelamolinaDAO.allIngresanteByCiclo(cicloPostula);

        for (TemaCiclo temaCiclo : temasCiclo) {
            TemaExamen tema = temaCiclo.getTemaExamen();
            BigDecimal puntajeMin = null;
            BigDecimal puntajeMax = null;
            BigDecimal notaMin = null;
            BigDecimal notaMax = null;
            BigDecimal puntajeCepreMin = null;
            BigDecimal puntajeCepreMax = null;

            if (tema.getCodigo().equals("RM")) {
                puntajeMin = evaluados.stream()
                        .filter(ne -> ne.getPuntajeRm() != null)
                        .min(Comparator.comparing(Evaluado::getPuntajeRm))
                        .get().getPuntajeRm();
                puntajeMax = evaluados.stream()
                        .filter(ne -> ne.getPuntajeRm() != null)
                        .max(Comparator.comparing(Evaluado::getPuntajeRm))
                        .get().getPuntajeRm();
                notaMin = evaluados.stream()
                        .filter(ne -> ne.getNotaRm() != null)
                        .min(Comparator.comparing(Evaluado::getNotaRm))
                        .get().getNotaRm();
                notaMax = evaluados.stream()
                        .filter(ne -> ne.getNotaRm() != null)
                        .max(Comparator.comparing(Evaluado::getNotaRm))
                        .get().getNotaRm();
                puntajeCepreMin = ingresantes.stream()
                        .filter(ne -> ne.getPuntajeRm() != null)
                        .min(Comparator.comparing(Prelamolina::getPuntajeRm))
                        .get().getPuntajeRm();
                puntajeCepreMax = ingresantes.stream()
                        .filter(ne -> ne.getPuntajeRm() != null)
                        .max(Comparator.comparing(Prelamolina::getPuntajeRm))
                        .get().getPuntajeRm();

            } else if (tema.getCodigo().equals("RV")) {
                puntajeMin = evaluados.stream()
                        .filter(ne -> ne.getPuntajeRv() != null)
                        .min(Comparator.comparing(Evaluado::getPuntajeRv))
                        .get().getPuntajeRv();
                puntajeMax = evaluados.stream()
                        .filter(ne -> ne.getPuntajeRv() != null)
                        .max(Comparator.comparing(Evaluado::getPuntajeRv))
                        .get().getPuntajeRv();
                notaMin = evaluados.stream()
                        .filter(ne -> ne.getNotaRv() != null)
                        .min(Comparator.comparing(Evaluado::getNotaRv))
                        .get().getNotaRv();
                notaMax = evaluados.stream()
                        .filter(ne -> ne.getNotaRv() != null)
                        .max(Comparator.comparing(Evaluado::getNotaRv))
                        .get().getNotaRv();
                puntajeCepreMin = ingresantes.stream()
                        .filter(ne -> ne.getPuntajeRv() != null)
                        .min(Comparator.comparing(Prelamolina::getPuntajeRv))
                        .get().getPuntajeRv();
                puntajeCepreMax = ingresantes.stream()
                        .filter(ne -> ne.getPuntajeRv() != null)
                        .max(Comparator.comparing(Prelamolina::getPuntajeRv))
                        .get().getPuntajeRv();

            } else if (tema.getCodigo().equals("FIS")) {
                puntajeMin = evaluados.stream()
                        .filter(ne -> ne.getPuntajeFisica() != null)
                        .min(Comparator.comparing(Evaluado::getPuntajeFisica))
                        .get().getPuntajeFisica();
                puntajeMax = evaluados.stream()
                        .filter(ne -> ne.getPuntajeFisica() != null)
                        .max(Comparator.comparing(Evaluado::getPuntajeFisica))
                        .get().getPuntajeFisica();
                notaMin = evaluados.stream()
                        .filter(ne -> ne.getNotaFisica() != null)
                        .min(Comparator.comparing(Evaluado::getNotaFisica))
                        .get().getNotaFisica();
                notaMax = evaluados.stream()
                        .filter(ne -> ne.getNotaFisica() != null)
                        .max(Comparator.comparing(Evaluado::getNotaFisica))
                        .get().getNotaFisica();
                puntajeCepreMin = ingresantes.stream()
                        .filter(ne -> ne.getPuntajeFisica() != null)
                        .min(Comparator.comparing(Prelamolina::getPuntajeFisica))
                        .get().getPuntajeFisica();
                puntajeCepreMax = ingresantes.stream()
                        .filter(ne -> ne.getPuntajeFisica() != null)
                        .max(Comparator.comparing(Prelamolina::getPuntajeFisica))
                        .get().getPuntajeFisica();

            } else if (tema.getCodigo().equals("QUI")) {
                puntajeMin = evaluados.stream()
                        .filter(ne -> ne.getPuntajeQuimica() != null)
                        .min(Comparator.comparing(Evaluado::getPuntajeQuimica))
                        .get().getPuntajeQuimica();
                puntajeMax = evaluados.stream()
                        .filter(ne -> ne.getPuntajeQuimica() != null)
                        .max(Comparator.comparing(Evaluado::getPuntajeQuimica))
                        .get().getPuntajeQuimica();
                notaMin = evaluados.stream()
                        .filter(ne -> ne.getNotaQuimica() != null)
                        .min(Comparator.comparing(Evaluado::getNotaQuimica))
                        .get().getNotaQuimica();
                notaMax = evaluados.stream()
                        .filter(ne -> ne.getNotaQuimica() != null)
                        .max(Comparator.comparing(Evaluado::getNotaQuimica))
                        .get().getNotaQuimica();
                puntajeCepreMin = ingresantes.stream()
                        .filter(ne -> ne.getPuntajeQuimica() != null)
                        .min(Comparator.comparing(Prelamolina::getPuntajeQuimica))
                        .get().getPuntajeQuimica();
                puntajeCepreMax = ingresantes.stream()
                        .filter(ne -> ne.getPuntajeQuimica() != null)
                        .max(Comparator.comparing(Prelamolina::getPuntajeQuimica))
                        .get().getPuntajeQuimica();

            } else if (tema.getCodigo().equals("BIO")) {
                puntajeMin = evaluados.stream()
                        .filter(ne -> ne.getPuntajeBiologia() != null)
                        .min(Comparator.comparing(Evaluado::getPuntajeBiologia))
                        .get().getPuntajeBiologia();
                puntajeMax = evaluados.stream()
                        .filter(ne -> ne.getPuntajeBiologia() != null)
                        .max(Comparator.comparing(Evaluado::getPuntajeBiologia))
                        .get().getPuntajeBiologia();
                notaMin = evaluados.stream()
                        .filter(ne -> ne.getNotaBiologia() != null)
                        .min(Comparator.comparing(Evaluado::getNotaBiologia))
                        .get().getNotaBiologia();
                notaMax = evaluados.stream()
                        .filter(ne -> ne.getNotaBiologia() != null)
                        .max(Comparator.comparing(Evaluado::getNotaBiologia))
                        .get().getNotaBiologia();
                puntajeCepreMin = ingresantes.stream()
                        .filter(ne -> ne.getPuntajeBiologia() != null)
                        .min(Comparator.comparing(Prelamolina::getPuntajeBiologia))
                        .get().getPuntajeBiologia();
                puntajeCepreMax = ingresantes.stream()
                        .filter(ne -> ne.getPuntajeBiologia() != null)
                        .max(Comparator.comparing(Prelamolina::getPuntajeBiologia))
                        .get().getPuntajeBiologia();

            } else if (tema.getCodigo().equals("ARI")) {
                puntajeMin = evaluados.stream()
                        .filter(ne -> ne.getPuntajeAritmetica() != null)
                        .min(Comparator.comparing(Evaluado::getPuntajeAritmetica))
                        .get().getPuntajeAritmetica();
                puntajeMax = evaluados.stream()
                        .filter(ne -> ne.getPuntajeAritmetica() != null)
                        .max(Comparator.comparing(Evaluado::getPuntajeAritmetica))
                        .get().getPuntajeAritmetica();
                notaMin = evaluados.stream()
                        .filter(ne -> ne.getNotaAritmetica() != null)
                        .min(Comparator.comparing(Evaluado::getNotaAritmetica))
                        .get().getNotaAritmetica();
                notaMax = evaluados.stream()
                        .filter(ne -> ne.getNotaAritmetica() != null)
                        .max(Comparator.comparing(Evaluado::getNotaAritmetica))
                        .get().getNotaAritmetica();
                puntajeCepreMin = ingresantes.stream()
                        .filter(ne -> ne.getPuntajeAritmetica() != null)
                        .min(Comparator.comparing(Prelamolina::getPuntajeAritmetica))
                        .get().getPuntajeAritmetica();
                puntajeCepreMax = ingresantes.stream()
                        .filter(ne -> ne.getPuntajeAritmetica() != null)
                        .max(Comparator.comparing(Prelamolina::getPuntajeAritmetica))
                        .get().getPuntajeAritmetica();

            } else if (tema.getCodigo().equals("ALG")) {
                puntajeMin = evaluados.stream()
                        .filter(ne -> ne.getPuntajeAlgebra() != null)
                        .min(Comparator.comparing(Evaluado::getPuntajeAlgebra))
                        .get().getPuntajeAlgebra();
                puntajeMax = evaluados.stream()
                        .filter(ne -> ne.getPuntajeAlgebra() != null)
                        .max(Comparator.comparing(Evaluado::getPuntajeAlgebra))
                        .get().getPuntajeAlgebra();
                notaMin = evaluados.stream()
                        .filter(ne -> ne.getNotaAlgebra() != null)
                        .min(Comparator.comparing(Evaluado::getNotaAlgebra))
                        .get().getNotaAlgebra();
                notaMax = evaluados.stream()
                        .filter(ne -> ne.getNotaAlgebra() != null)
                        .max(Comparator.comparing(Evaluado::getNotaAlgebra))
                        .get().getNotaAlgebra();
                puntajeCepreMin = ingresantes.stream()
                        .filter(ne -> ne.getPuntajeAlgebra() != null)
                        .min(Comparator.comparing(Prelamolina::getPuntajeAlgebra))
                        .get().getPuntajeAlgebra();
                puntajeCepreMax = ingresantes.stream()
                        .filter(ne -> ne.getPuntajeAlgebra() != null)
                        .max(Comparator.comparing(Prelamolina::getPuntajeAlgebra))
                        .get().getPuntajeAlgebra();

            } else if (tema.getCodigo().equals("GEOM")) {
                puntajeMin = evaluados.stream()
                        .filter(ne -> ne.getPuntajeGeometria() != null)
                        .min(Comparator.comparing(Evaluado::getPuntajeGeometria))
                        .get().getPuntajeGeometria();
                puntajeMax = evaluados.stream()
                        .filter(ne -> ne.getPuntajeGeometria() != null)
                        .max(Comparator.comparing(Evaluado::getPuntajeGeometria))
                        .get().getPuntajeGeometria();
                notaMin = evaluados.stream()
                        .filter(ne -> ne.getNotaGeometria() != null)
                        .min(Comparator.comparing(Evaluado::getNotaGeometria))
                        .get().getNotaGeometria();
                notaMax = evaluados.stream()
                        .filter(ne -> ne.getNotaGeometria() != null)
                        .max(Comparator.comparing(Evaluado::getNotaGeometria))
                        .get().getNotaGeometria();
                puntajeCepreMin = ingresantes.stream()
                        .filter(ne -> ne.getPuntajeGeometria() != null)
                        .min(Comparator.comparing(Prelamolina::getPuntajeGeometria))
                        .get().getPuntajeGeometria();
                puntajeCepreMax = ingresantes.stream()
                        .filter(ne -> ne.getPuntajeGeometria() != null)
                        .max(Comparator.comparing(Prelamolina::getPuntajeGeometria))
                        .get().getPuntajeGeometria();

            } else if (tema.getCodigo().equals("TRI")) {
                puntajeMin = evaluados.stream()
                        .filter(ne -> ne.getPuntajeTrigonometria() != null)
                        .min(Comparator.comparing(Evaluado::getPuntajeTrigonometria))
                        .get().getPuntajeTrigonometria();
                puntajeMax = evaluados.stream()
                        .filter(ne -> ne.getPuntajeTrigonometria() != null)
                        .max(Comparator.comparing(Evaluado::getPuntajeTrigonometria))
                        .get().getPuntajeTrigonometria();
                notaMin = evaluados.stream()
                        .filter(ne -> ne.getNotaTrigonometria() != null)
                        .min(Comparator.comparing(Evaluado::getNotaTrigonometria))
                        .get().getNotaTrigonometria();
                notaMax = evaluados.stream()
                        .filter(ne -> ne.getNotaTrigonometria() != null)
                        .max(Comparator.comparing(Evaluado::getNotaTrigonometria))
                        .get().getNotaTrigonometria();
                puntajeCepreMin = ingresantes.stream()
                        .filter(ne -> ne.getPuntajeTrigonometria() != null)
                        .min(Comparator.comparing(Prelamolina::getPuntajeTrigonometria))
                        .get().getPuntajeTrigonometria();
                puntajeCepreMax = ingresantes.stream()
                        .filter(ne -> ne.getPuntajeTrigonometria() != null)
                        .max(Comparator.comparing(Prelamolina::getPuntajeTrigonometria))
                        .get().getPuntajeTrigonometria();

            } else if (tema.getCodigo().equals("ECO")) {
                puntajeMin = evaluados.stream()
                        .filter(ne -> ne.getPuntajeEconomia() != null)
                        .min(Comparator.comparing(Evaluado::getPuntajeEconomia))
                        .get().getPuntajeEconomia();
                puntajeMax = evaluados.stream()
                        .filter(ne -> ne.getPuntajeEconomia() != null)
                        .max(Comparator.comparing(Evaluado::getPuntajeEconomia))
                        .get().getPuntajeEconomia();
                notaMin = evaluados.stream()
                        .filter(ne -> ne.getNotaEconomia() != null)
                        .min(Comparator.comparing(Evaluado::getNotaEconomia))
                        .get().getNotaEconomia();
                notaMax = evaluados.stream()
                        .filter(ne -> ne.getNotaEconomia() != null)
                        .max(Comparator.comparing(Evaluado::getNotaEconomia))
                        .get().getNotaEconomia();
                puntajeCepreMin = ingresantes.stream()
                        .filter(ne -> ne.getPuntajeEconomia() != null)
                        .min(Comparator.comparing(Prelamolina::getPuntajeEconomia))
                        .get().getPuntajeEconomia();
                puntajeCepreMax = ingresantes.stream()
                        .filter(ne -> ne.getPuntajeEconomia() != null)
                        .max(Comparator.comparing(Prelamolina::getPuntajeEconomia))
                        .get().getPuntajeEconomia();

            } else if (tema.getCodigo().equals("HIS")) {
                puntajeMin = evaluados.stream()
                        .filter(ne -> ne.getPuntajeHistoria() != null)
                        .min(Comparator.comparing(Evaluado::getPuntajeHistoria))
                        .get().getPuntajeHistoria();
                puntajeMax = evaluados.stream()
                        .filter(ne -> ne.getPuntajeHistoria() != null)
                        .max(Comparator.comparing(Evaluado::getPuntajeHistoria))
                        .get().getPuntajeHistoria();
                notaMin = evaluados.stream()
                        .filter(ne -> ne.getNotaHistoria() != null)
                        .min(Comparator.comparing(Evaluado::getNotaHistoria))
                        .get().getNotaHistoria();
                notaMax = evaluados.stream()
                        .filter(ne -> ne.getNotaHistoria() != null)
                        .max(Comparator.comparing(Evaluado::getNotaHistoria))
                        .get().getNotaHistoria();
                puntajeCepreMin = ingresantes.stream()
                        .filter(ne -> ne.getPuntajeHistoria() != null)
                        .min(Comparator.comparing(Prelamolina::getPuntajeHistoria))
                        .get().getPuntajeHistoria();
                puntajeCepreMax = ingresantes.stream()
                        .filter(ne -> ne.getPuntajeHistoria() != null)
                        .max(Comparator.comparing(Prelamolina::getPuntajeHistoria))
                        .get().getPuntajeHistoria();

            } else if (tema.getCodigo().equals("GEOG")) {
                puntajeMin = evaluados.stream()
                        .filter(ne -> ne.getPuntajeGeografia() != null)
                        .min(Comparator.comparing(Evaluado::getPuntajeGeografia))
                        .get().getPuntajeGeografia();
                puntajeMax = evaluados.stream()
                        .filter(ne -> ne.getPuntajeGeografia() != null)
                        .max(Comparator.comparing(Evaluado::getPuntajeGeografia))
                        .get().getPuntajeGeografia();
                notaMin = evaluados.stream()
                        .filter(ne -> ne.getNotaGeografia() != null)
                        .min(Comparator.comparing(Evaluado::getNotaGeografia))
                        .get().getNotaGeografia();
                notaMax = evaluados.stream()
                        .filter(ne -> ne.getNotaGeografia() != null)
                        .max(Comparator.comparing(Evaluado::getNotaGeografia))
                        .get().getNotaGeografia();
                puntajeCepreMin = ingresantes.stream()
                        .filter(ne -> ne.getPuntajeGeografia() != null)
                        .min(Comparator.comparing(Prelamolina::getPuntajeGeografia))
                        .get().getPuntajeGeografia();
                puntajeCepreMax = ingresantes.stream()
                        .filter(ne -> ne.getPuntajeGeografia() != null)
                        .max(Comparator.comparing(Prelamolina::getPuntajeGeografia))
                        .get().getPuntajeGeografia();
            }

            temaCiclo.setPuntajeMinimo(puntajeMin);
            temaCiclo.setPuntajeMaximo(puntajeMax);
            temaCiclo.setNotaMinima(notaMin);
            temaCiclo.setNotaMaxima(notaMax);
            temaCiclo.setPuntajeCepreMinimo(puntajeCepreMin);
            temaCiclo.setPuntajeCepreMaximo(puntajeCepreMax);
            temaCicloDAO.update(temaCiclo);
        }

    }

    @Override
    @Transactional
    public void revisarDatos(CicloAcademico ciclo, DataSessionPivot ds) {
        CicloPostula cicloPostula = cicloPostulaDAO.findByCicloAcademico(ciclo);
        if (cicloPostula == null) {
            return;
        }

        Evento examen = eventoDAO.findByCode(EXAM.name());
        List<EventoCiclo> eventosExamen = eventoCicloDAO.allByEventoCiclo(examen, cicloPostula);
        if (eventosExamen.isEmpty()) {
            return;
        }

        Optional<EventoCiclo> fechaMaxima = eventosExamen.stream()
                .max(Comparator.comparing(EventoCiclo::getFechaInicio));
        Date fechaExamen = new LocalDate(fechaMaxima.get().getFechaInicio()).toDate();
        Date hoy = new LocalDate().toDate();
        if (!hoy.after(fechaExamen)) {
            return;
        }

        List<ModalidadTemaCiclo> configsCiclo = modalidadTemaCicloDAO.allByCiclo(ciclo);
        if (!configsCiclo.isEmpty()) {
            return;
        }

        ModalidadIngreso modalidad = modalidadIngresoDAO.findByCode(ModalidadIngresoEnum.CEPRE.getCode());
        Assert.isNotNull(modalidad, "No se pudo ubicar la modalidad CEPRE");
        List<TemaCiclo> temasCiclo = temaCicloDAO.allByCiclo(ciclo);
        Map<Long, TemaCiclo> mapTemaCiclo = temasCiclo.stream()
                .collect(Collectors.toMap(tc -> tc.getTemaExamen().getId(), Function.identity()));

        List<TemaExamen> temasSuper = this.allTemasSuperiores(temasCiclo);
        Map<Long, TemaExamen> mapTemaSuper = temasSuper.stream()
                .collect(Collectors.toMap(ts -> ts.getId(), Function.identity()));
        List<String> codigos = new ArrayList();

        for (TemaCiclo temaCiclo : temasCiclo) {
            TemaCiclo temaSuper = this.getTemaCicloSuper(temaCiclo, mapTemaSuper, mapTemaCiclo, codigos);
            if (temaSuper != null) {
                this.createModalidadTema(ciclo, modalidad, temaSuper, ds);
            }

            this.createModalidadTema(ciclo, modalidad, temaCiclo, ds);
        }

        codigos.clear();
        for (TemaCiclo temaCiclo : temasCiclo) {
            TemaCiclo temaSuper = this.getTemaCicloSuper(temaCiclo, mapTemaSuper, mapTemaCiclo, codigos);
            if (temaSuper != null) {
                this.createModalidadTema(ciclo, null, temaSuper, ds);
            }
            this.createModalidadTema(ciclo, null, temaCiclo, ds);
        }
    }

    private TemaCiclo getTemaCicloSuper(TemaCiclo temaCiclo, Map<Long, TemaExamen> mapTemaSuper, Map<Long, TemaCiclo> mapTemaCiclo, List<String> codigos) {
        if (temaCiclo.getTemaExamen().getTemaSuperior() != null) {
            TemaExamen temaSuper = mapTemaSuper.get(temaCiclo.getTemaExamen().getTemaSuperior().getId());
            if (!codigos.contains(temaSuper.getCodigo())) {
                codigos.add(temaSuper.getCodigo());
                TemaCiclo tcs = new TemaCiclo();
                tcs.setPreguntas(0);
                tcs.setTemaExamen(temaSuper);
                for (TemaExamen inferior : temaSuper.getTemasInferiores()) {
                    TemaCiclo tc = mapTemaCiclo.get(inferior.getId());
                    if (tc != null) {
                        tcs.setPreguntas(tcs.getPreguntas() + tc.getPreguntas());
                    }
                }
                return tcs;
            }
        }
        return null;
    }

    private void createModalidadTema(CicloAcademico ciclo, ModalidadIngreso modalidad, TemaCiclo temaCiclo, DataSessionPivot ds) {
        BigDecimal puntajeMin = ONCE.multiply(new BigDecimal(temaCiclo.getPreguntas())).divide(VEINTE, 4, RoundingMode.HALF_UP);

        ModalidadTemaCiclo item = new ModalidadTemaCiclo();
        if (temaCiclo.getId() != null) {
            item.setTemaCiclo(temaCiclo);
        }

        item.setCicloAcademico(ciclo);
        item.setTemaExamen(temaCiclo.getTemaExamen());
        item.setModalidadIngreso(modalidad);
        item.setOtrasModalidades(modalidad == null);
        item.setNotaMinima(ONCE);
        item.setPuntajeMinimo(puntajeMin);
        item.setEstadoEnum(PEN);
        item.setUserRegistro(ds.getUsuario());
        item.setFechaRegistro(new Date());
        modalidadTemaCicloDAO.save(item);
    }

    private List<TemaExamen> allTemasSuperiores(List<TemaCiclo> temasCiclo) {
        List<TemaExamen> superiores = temasCiclo.stream()
                .filter(tc -> tc.getTemaExamen().getTemaSuperior() != null)
                .map(tc -> tc.getTemaExamen().getTemaSuperior())
                .distinct()
                .collect(Collectors.toList());
        Map<Long, TemaExamen> mapTemaSuper = superiores.stream()
                .collect(Collectors.toMap(te -> te.getId(), Function.identity()));

        superiores.forEach(sup -> sup.setTemasInferiores(new ArrayList()));

        temasCiclo.forEach(tc -> {
            TemaExamen tema = mapTemaSuper.get(tc.getTemaExamen().getId());
            if (tema != null) {
                superiores.remove(tema);
            }
            if (tc.getTemaExamen().getTemaSuperior() != null) {
                TemaExamen temaSuper = mapTemaSuper.get(tc.getTemaExamen().getTemaSuperior().getId());
                temaSuper.getTemasInferiores().add(tc.getTemaExamen());
            }
        });
        return superiores;
    }

    @Override
    public List<ModalidadTemaCiclo> allConfiguracionsByDynatable(DynatableFilter filter, CicloAcademico ciclo) {
        List<ModalidadTemaCiclo> items = modalidadTemaCicloDAO.allByDynatable(filter, ciclo);
        items.stream()
                .filter(mtc -> mtc.getTemaCiclo() == null)
                .forEach(mtc -> {
                    TemaExamen tema = mtc.getTemaExamen();
                    TemaCiclo temaPadre = this.crearTemaCiclo(tema, items, mtc.getModalidadIngreso());
                    mtc.setTemaCiclo(temaPadre);
                });
        return items;
    }

    private TemaCiclo crearTemaCiclo(TemaExamen temaSuper, List<ModalidadTemaCiclo> items, ModalidadIngreso modalidad) {
        TemaCiclo tc = new TemaCiclo();
        tc.setPreguntas(0);
        if (modalidad != null && modalidad.isPreLaMolina()) {
            tc.setPuntajeCepreMinimo(CIEN);
            tc.setPuntajeCepreMaximo(CIEN_NEG);
        } else {
            tc.setPuntajeMinimo(CIEN);
            tc.setPuntajeMaximo(CIEN_NEG);
        }

        items.stream()
                .filter(mtc -> mtc.getTemaExamen().getTemaSuperior() != null)
                .filter(mtc -> mtc.getTemaExamen().getTemaSuperior().equals(temaSuper))
                .filter(mtc -> {
                    if (mtc.getModalidadIngreso() == null && modalidad == null) {
                        return true;
                    }
                    if (mtc.getModalidadIngreso() != null && modalidad != null) {
                        return mtc.getModalidadIngreso().getId().equals(modalidad.getId());
                    }
                    return false;
                })
                .forEach(mtc -> {
                    TemaCiclo hijo = mtc.getTemaCiclo();
                    tc.setPreguntas(tc.getPreguntas() + hijo.getPreguntas());
                    if (modalidad != null && modalidad.isPreLaMolina()) {
                        if (hijo.getPuntajeCepreMinimo().compareTo(tc.getPuntajeCepreMinimo()) < 0) {
                            tc.setPuntajeCepreMinimo(hijo.getPuntajeCepreMinimo());
                        }
                        if (hijo.getPuntajeCepreMaximo().compareTo(tc.getPuntajeCepreMaximo()) > 0) {
                            tc.setPuntajeCepreMaximo(hijo.getPuntajeCepreMaximo());
                        }
                    } else {
                        if (hijo.getPuntajeMinimo().compareTo(tc.getPuntajeMinimo()) < 0) {
                            tc.setPuntajeMinimo(hijo.getPuntajeMinimo());
                        }
                        if (hijo.getPuntajeMaximo().compareTo(tc.getPuntajeMaximo()) > 0) {
                            tc.setPuntajeMaximo(hijo.getPuntajeMaximo());
                        }
                    }
                });

        return tc;
    }

    @Override
    @Transactional
    public void saveConfig(ModalidadTemaCiclo configForm, DataSessionPivot ds) {
        this.verificarPermiso(ds);

        Assert.isNotNull(configForm.getNotaMinima(), "No ha indicado la nota mínima aprobatoria");
        ModalidadTemaCiclo config = modalidadTemaCicloDAO.find(configForm.getId());
        Assert.isNotNull(config, "No se ha podido ubicar el registro que desea modificar");
        Assert.isTrue(config.getEstadoEnum() == PEN, "Este registro ya no se puede modificar");

        TemaCiclo temaCiclo = config.getTemaCiclo();

        BigDecimal puntajeMin = configForm.getNotaMinima()
                .multiply(new BigDecimal(temaCiclo.getPreguntas()))
                .divide(VEINTE, 4, RoundingMode.HALF_UP);

        config.setNotaMinima(configForm.getNotaMinima());
        config.setPuntajeMinimo(puntajeMin);
        config.setUserModificacion(ds.getUsuario());
        config.setFechaModificacion(new Date());
        modalidadTemaCicloDAO.update(config);
    }

    @Override
    @Transactional
    public int activarTodos(CicloAcademico ciclo, DataSessionPivot ds) {
        this.verificarPermiso(ds);

        DateTime today = new DateTime();
        List<ModalidadTemaCiclo> configuraciones = modalidadTemaCicloDAO.allByCiclo(ciclo);

        List<ModalidadTemaCiclo> inactivos = configuraciones.stream()
                .filter(mtc -> mtc.getEstadoEnum() == PEN)
                .collect(Collectors.toList());
        Assert.isFalse(inactivos.isEmpty(), "No hay registros que activar");

        inactivos.forEach(mtc -> {
            mtc.setEstadoEnum(ACT);
            mtc.setUserActivacion(ds.getUsuario());
            mtc.setFechaActivacion(today.toDate());
            modalidadTemaCicloDAO.update(mtc);
        });

        return inactivos.size();
    }

    @Override
    @Transactional
    public void activar(ModalidadTemaCiclo configForm, DataSessionPivot ds) {
        this.verificarPermiso(ds);

        ModalidadTemaCiclo config = modalidadTemaCicloDAO.find(configForm.getId());
        Assert.isNotNull(config, "No se pudo ubicar el registro que desea modificar");
        Assert.isTrue(config.getEstadoEnum() == PEN, "No se puede activar este registro");

        config.setEstadoEnum(ACT);
        config.setUserActivacion(ds.getUsuario());
        config.setFechaActivacion(new Date());
        modalidadTemaCicloDAO.update(config);
    }

    @Override
    @Transactional
    public void desactivar(ModalidadTemaCiclo configForm, DataSessionPivot ds) {
        this.verificarPermiso(ds);

        ModalidadTemaCiclo config = modalidadTemaCicloDAO.find(configForm.getId());
        Assert.isNotNull(config, "No se pudo ubicar el registro que desea modificar");
        Assert.isTrue(config.getEstadoEnum() == ACT, "No se puede desactivar este registro");

        config.setEstadoEnum(PEN);
        config.setUserActivacion(null);
        config.setFechaActivacion(null);
        modalidadTemaCicloDAO.update(config);
    }

}
