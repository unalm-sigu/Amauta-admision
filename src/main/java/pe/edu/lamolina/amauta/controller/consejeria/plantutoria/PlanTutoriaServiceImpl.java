package pe.edu.lamolina.amauta.controller.consejeria.plantutoria;

import java.util.ArrayList;
import java.util.Comparator;
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
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoConsejeroDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoCualidadDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ConsejeroDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ObjetivoCitaConsejeroDAO;
import pe.edu.lamolina.amauta.dao.consejeria.PlanTutorialDAO;
import pe.edu.lamolina.amauta.dao.consejeria.TipoCualidadAlumnoDAO;
import pe.edu.lamolina.amauta.zelper.misc.Acumulador;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.enums.EstadoEnum;
import static pe.edu.lamolina.model.enums.consejeria.TipoCualidadAlumnoEnum.CARACTERISTICA;
import static pe.edu.lamolina.model.enums.consejeria.TipoCualidadAlumnoEnum.MAPA_EMPATIA;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.tutoria.AlumnoCualidad;
import pe.edu.lamolina.model.tutoria.ObjetivoCitaConsejero;
import pe.edu.lamolina.model.tutoria.PlanTutorial;
import pe.edu.lamolina.model.tutoria.TipoCualidadAlumno;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class PlanTutoriaServiceImpl implements PlanTutoriaService {

    private final AlumnoConsejeroDAO alumnoConsejeroDAO;
    private final AlumnoCualidadDAO alumnoCualidadDAO;
    private final AlumnoDAO alumnoDAO;
    private final ConsejeroDAO consejeroDAO;
    private final ObjetivoCitaConsejeroDAO objetivoCitaConsejeroDAO;
    private final PlanTutorialDAO planTutorialDAO;
    private final TipoCualidadAlumnoDAO tipoCualidadAlumnoDAO;

    private final VerificadorService verificadorService;

    @Override
    public Alumno findAlumno(Alumno alumno) {
        return alumnoDAO.findAllInfo(alumno.getId());
    }

    @Override
    public boolean verificarConsejero(Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds) {
        if (alumno.getCarrera() == null) {
            alumno = alumnoDAO.findAllInfo(alumno.getId());
        }

        Consejero consejero = consejeroDAO.findByPersonaCarrera(ds.getPersona(), alumno.getCarrera());
        if (consejero == null) {
            return false;
        }

        if (consejero.getEstadoEnum() != EstadoEnum.ACT) {
            return false;
        }

        AlumnoConsejero alumnoConsejero = alumnoConsejeroDAO.findByAlumnoCiclo(alumno, ciclo);
        if (alumnoConsejero == null) {
            return false;
        }

        return consejero.getId().equals(alumnoConsejero.getConsejero().getId());
    }

    @Override
    public boolean tienePermiso(Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds) {
        if (alumno.getCarrera() == null) {
            alumno = alumnoDAO.findAllInfo(alumno.getId());
        }

        boolean esConsejero = this.verificarConsejero(alumno, ciclo, ds);
        if (esConsejero) {
            return true;
        }
        boolean esCoordinador = verificadorService.esCoordinadorConsejeria(ds, alumno.getCarrera());
        if (esCoordinador) {
            return true;
        }
        boolean esJefeCarrera = verificadorService.esJefeCarrera(ds, alumno.getCarrera());
        if (esJefeCarrera) {
            return true;
        }

        return verificadorService.esInformaticoOERA(ds);
    }

    @Override
    public Consejero findConsejero(Persona persona, Carrera carrera) {
        return consejeroDAO.findByPersonaCarrera(persona, carrera);
    }

    @Override
    public AlumnoConsejero findAlumnoConsejero(Alumno alumno, CicloAcademico ciclo) {
        AlumnoConsejero alumnoConsejero = alumnoConsejeroDAO.findByAlumnoCiclo(alumno, ciclo);
        if (alumnoConsejero == null) {
            alumnoConsejero = new AlumnoConsejero();
            alumnoConsejero.setConsejero(new Consejero());
        }
        return alumnoConsejero;
    }

    @Override
    public List<TipoCualidadAlumno> allTiposCualidades() {
        return tipoCualidadAlumnoDAO.all();
    }

    @Override
    public List<AlumnoCualidad> allCualidadesAlumno(Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds) {
        boolean tienePermiso = this.tienePermiso(alumno, ciclo, ds);
        if (tienePermiso) {
            return alumnoCualidadDAO.allByAlumno(alumno);
        }
        return new ArrayList();
    }

    @Override
    public List<PlanTutorial> allPlanesTutoria(Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds) {
        boolean tienePermiso = this.tienePermiso(alumno, ciclo, ds);
        if (tienePermiso) {
            return planTutorialDAO.allByAlumnoCiclo(alumno, ciclo);
        }
        return new ArrayList();
    }

    @Override
    @Transactional
    public void saveCaracteristicas(List<AlumnoCualidad> cualidadesForm, Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds) {
        DateTime today = new DateTime();
        boolean esConsejero = this.verificarConsejero(alumno, ciclo, ds);
        Assert.isTrue(esConsejero, "Usted no tiene permiso de crear o modificar estos datos");

        Assert.isFalse(cualidadesForm.isEmpty(), "Debe enviar registros");
        List<TipoCualidadAlumno> tipos = tipoCualidadAlumnoDAO.all();
        TipoCualidadAlumno tipoForm = cualidadesForm.get(0).getTipoCualidadAlumno();
        TipoCualidadAlumno tipoFirst = this.findTipo(tipoForm, tipos);

        cualidadesForm.forEach(cualidad -> {
            Assert.isNotNull(cualidad.getTipoCualidadAlumno(), "No ha indicado el tipo de cualidad entre los registros enviados");
            Assert.isNotNull(cualidad.getTipoCualidadAlumno().getId(), "No ha indicado el tipo de cualidad entre los registros enviados");
            Assert.isNotNull(cualidad.getAlumno(), "No ha indicado el alumno entre los registros enviados");
            Assert.isNotNull(cualidad.getAlumno().getId(), "No ha indicado el alumno entre los registros enviados");
            Assert.isNotNull(cualidad.getDescripcion(), "No ha indicado la característica del tutorado entre los registros enviados");

            TipoCualidadAlumno tipoBD = this.findTipo(cualidad.getTipoCualidadAlumno(), tipos);
            Assert.isTrue(tipoBD.getTipoCualidadEnum() == tipoFirst.getTipoCualidadEnum(), "Ha enviado diferentes tipos de cualidades");

            Assert.isTrue(cualidad.getAlumno().equals(alumno),
                    "Los registros que envió no corresponde al tutorado seleccionado");
        });

        List< AlumnoCualidad> cualidades = alumnoCualidadDAO.allByAlumnoTipoCualidad(alumno, tipoFirst.getTipoCualidad());
        String dataForm = this.createDateCualidades(cualidadesForm);
        String dataDB = this.createDateCualidades(cualidades);
        Assert.isFalse(dataForm.equals(dataDB), "No se han detectado cambios en los registrados enviados");

        Map<Long, AlumnoCualidad> mapCualidad = cualidades.stream()
                .collect(Collectors.toMap(cualidad -> cualidad.getTipoCualidadAlumno().getId(), Function.identity()));

        cualidadesForm.forEach(cualidadForm -> {
            AlumnoCualidad cualidad = mapCualidad.get(cualidadForm.getTipoCualidadAlumno().getId());
            if (cualidad == null) {
                cualidad = new AlumnoCualidad();
                cualidad.setAlumno(alumno);
                cualidad.setDescripcion(cualidadForm.getDescripcion());
                cualidad.setTipoCualidadAlumno(cualidadForm.getTipoCualidadAlumno());
                cualidad.setUserRegistro(ds.getUsuario());
                cualidad.setFechaRegistro(today.toDate());
                alumnoCualidadDAO.save(cualidad);

            } else if (!cualidadForm.getDescripcion().equals(cualidad.getDescripcion())) {
                cualidad.setDescripcion(cualidadForm.getDescripcion());
                cualidad.setUserModificacion(ds.getUsuario());
                cualidad.setFechaModificacion(today.toDate());
                alumnoCualidadDAO.update(cualidad);
            }
        });
    }

    private TipoCualidadAlumno findTipo(TipoCualidadAlumno tipoForm, List<TipoCualidadAlumno> tipos) {
        TipoCualidadAlumno tipoBD = tipos.stream()
                .filter(tipo -> tipo.getId().equals(tipoForm.getId()))
                .findFirst()
                .orElse(null);
        Assert.isNotNull(tipoBD, "El tipo de cualidad no existe en la base de datos");
        return tipoBD;
    }

    @Override
    @Transactional
    public void savePlanTutorial(List<PlanTutorial> planesForm, Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds) {
        DateTime today = new DateTime();
        boolean esConsejero = this.verificarConsejero(alumno, ciclo, ds);
        Assert.isTrue(esConsejero, "Usted no tiene permiso de crear o modificar estos datos");

        Assert.isFalse(planesForm.isEmpty(), "Debe enviar registros");
        List<PlanTutorial> planesNuevos = planesForm.stream()
                .filter(plan -> plan.getId() == null)
                .collect(Collectors.toList());

        List<PlanTutorial> planesExisten = planesForm.stream()
                .filter(plan -> plan.getId() != null)
                .collect(Collectors.toList());

        List<PlanTutorial> planes = planTutorialDAO.allByAlumnoCiclo(alumno, ciclo);
        Map<Long, PlanTutorial> mapPlanes = planes.stream().collect(Collectors.toMap(PlanTutorial::getId, Function.identity()));

        Acumulador cambios = new Acumulador();
        planesExisten.forEach(plan -> {
            PlanTutorial planBD = mapPlanes.get(plan.getId());
            Assert.isNotNull(planBD, "El registro " + plan.getCodigo() + " no pertenece a este alumno");

            String planFormString = this.createPlanJson(plan);
            String planBDString = this.createPlanJson(planBD);

            if (!planFormString.equals(planBDString)) {
                planBD.setNecesidad(plan.getNecesidad());
                planBD.setObjetivo(plan.getObjetivo());
                planBD.setEstrategiaTutorial(plan.getEstrategiaTutorial());
                planBD.setAccionesImplicadas(plan.getAccionesImplicadas());
                planBD.setUserModificacion(ds.getUsuario());
                planBD.setFechaModificacion(today.toDate());
                planTutorialDAO.update(planBD);

                cambios.incrementar();
            }
        });

        Integer maximo = this.getMaxCodigo(planes);
        Acumulador coder = new Acumulador(maximo);
        AlumnoConsejero alumnoConsejero = alumnoConsejeroDAO.findByAlumnoCiclo(alumno, ciclo);

        planesNuevos.forEach(plan -> {
            String code = "P" + NumberFormat.codigo(coder.getValor(), 2);

            PlanTutorial planBD = new PlanTutorial();
            planBD.setCodigo(code);
            planBD.setAlumno(alumno);
            planBD.setCicloAcademico(ciclo);
            planBD.setConsejero(alumnoConsejero.getConsejero());
            planBD.setNecesidad(plan.getNecesidad());
            planBD.setObjetivo(plan.getObjetivo());
            planBD.setEstrategiaTutorial(plan.getEstrategiaTutorial());
            planBD.setAccionesImplicadas(plan.getAccionesImplicadas());
            planBD.setUserRegistro(ds.getUsuario());
            planBD.setFechaRegistro(today.toDate());
            planTutorialDAO.save(planBD);

            cambios.incrementar();
            coder.incrementar();
        });

        Assert.isTrue(cambios.getValor() > 0, "No ha enviado registros o cambios nuevos");

    }

    private Integer getMaxCodigo(List<PlanTutorial> planes) {
        if (planes.isEmpty()) {
            return 1;
        }

        String codigo = planes.stream()
                .map(plan -> plan.getCodigo())
                .max(Comparator.comparing(String::valueOf))
                .get();

        return Integer.valueOf(codigo.substring(1)) + 1;
    }

    @Override
    @Transactional
    public void deletePlanTutorial(PlanTutorial planForm, Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds) {
        boolean esConsejero = this.verificarConsejero(alumno, ciclo, ds);
        Assert.isTrue(esConsejero, "Usted no tiene permiso para eliminar estos datos");

        PlanTutorial planBD = planTutorialDAO.find(planForm.getId());
        Assert.isNotNull(planBD, "El registro que desea eliminar no existe");

        List<ObjetivoCitaConsejero> objetivosPlan = objetivoCitaConsejeroDAO.allByPlanTutorial(planForm);
        Assert.isTrue(objetivosPlan.isEmpty(), "Este registro esta relacionado a citas con el consejero");

        long time = System.currentTimeMillis();
        long pasado = planBD.getFechaRegistro().getTime();
        long diff = time - pasado;
        Assert.isTrue(diff < 60 * 60 * 1000, "Este registro ya no puede ser eliminado");

        planTutorialDAO.delete(planBD);
    }

    @Override
    public boolean tieneCaracteristicas(Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds) {
        return !alumnoCualidadDAO.allByAlumnoTipoCualidad(alumno, CARACTERISTICA.name()).isEmpty();
    }

    @Override
    public boolean tieneMapaEmpatia(Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds) {
        return !alumnoCualidadDAO.allByAlumnoTipoCualidad(alumno, MAPA_EMPATIA.name()).isEmpty();
    }

    @Override
    public boolean tienePlanTutorial(Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds) {
        return !planTutorialDAO.allByAlumnoCiclo(alumno, ciclo).isEmpty();
    }

    private String createPlanJson(PlanTutorial plan) {
        return JaneHelper
                .from(plan)
                .only("necesidad,objetivo,estrategiaTutorial,accionesImplicadas")
                .json().toString();
    }

    private String createDateCualidades(List<AlumnoCualidad> cualidades) {
        return JaneHelper
                .from(cualidades)
                .only("id,descripcion")
                .join("alumno", "id")
                .join("tipoCualidadAlumno", "id")
                .array().toString();
    }

}
