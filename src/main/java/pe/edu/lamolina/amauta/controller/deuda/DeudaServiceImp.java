package pe.edu.lamolina.amauta.controller.deuda;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.aporte.AporteAlumnoCiclo;
import pe.edu.lamolina.model.aporte.SaldoAfavorAlumno;
import pe.edu.lamolina.model.enums.DeudaEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoAporteEnum;
import pe.edu.lamolina.model.enums.ProcesoMethodEnum;
import pe.edu.lamolina.model.finanzas.Acreencia;
import pe.edu.lamolina.model.finanzas.DeudaAlumno;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.amauta.dao.aporte.AporteAlumnoCicloDAO;
import pe.edu.lamolina.amauta.dao.aporte.SaldoAfavorAlumnoDAO;
import pe.edu.lamolina.amauta.dao.finanza.AcreenciaDAO;
import pe.edu.lamolina.amauta.dao.finanza.DeudaAlumnoDAO;
import pe.edu.lamolina.amauta.dao.finanza.DeudaInteresadoDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Service
@Transactional
public class DeudaServiceImp implements DeudaService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AcreenciaDAO acreenciaDAO;

    @Autowired
    DeudaAlumnoDAO deudaAlumnoDAO;

    @Autowired
    DeudaInteresadoDAO deudaInteresadoDAO;

    @Autowired
    AporteAlumnoCicloDAO aporteAlumnoCicloDAO;

    @Autowired
    SaldoAfavorAlumnoDAO saldoAfavorAlumnoDAO;

    @Override
    public void cleanDeudas(DataSessionPivot ds) {
        DateTime today = new DateTime().withDayOfMonth(9);
        List<DeudaDTO> deudasRepetidas = acreenciaDAO.allDeudasRepetidasAffetDate(today);
        List<Persona> personas = new ArrayList<>();
        deudasRepetidas.forEach(x -> personas.add(new Persona(x.getPersonaId())));
        List<Acreencia> acreencias = acreenciaDAO.allByPersonasAndEstado(personas, DeudaEstadoEnum.DEU);
        List<SaldoAfavorAlumno> saldosAfavor = saldoAfavorAlumnoDAO.allByPersonas(personas);
        this.procesarDeudasAlumno(acreencias, deudasRepetidas, saldosAfavor, ds);
    }

    public void procesarDeudasAlumno(List<Acreencia> acreencias, List<DeudaDTO> deudasRepetidas, List<SaldoAfavorAlumno> saldosAfavor, DataSessionPivot ds) {
        List<DeudaDTO> deudasAlumnosRepetidas = deudasRepetidas.stream()
                .filter(x -> x.isDeudaAlumno())
                .collect(Collectors.toList());

        List<Acreencia> acreenciasDeudaAlumno = acreencias.stream()
                .filter(x -> x.isTablaDeudaAlumno()).collect(Collectors.toList());

        List<DeudaAlumno> deudasAlumnos = acreenciasDeudaAlumno.stream()
                .filter(x -> !x.isEstadoAnulado())
                .map(x -> new DeudaAlumno(x.getInstanciaTabla())).collect(Collectors.toList());
        deudasAlumnos = deudaAlumnoDAO.allById(deudasAlumnos);

        List<AporteAlumnoCiclo> aportesAlumnosCiclo = aporteAlumnoCicloDAO.allByDeudasAlumno(deudasAlumnos);
        for (DeudaAlumno deudasAlumno : deudasAlumnos) {
            List<AporteAlumnoCiclo> aportesByDeuda = aportesAlumnosCiclo.stream()
                    .filter(x -> x.getDeudaAlumno().equals(deudasAlumno))
                    .collect(Collectors.toList());
            aportesByDeuda = aportesByDeuda == null ? new ArrayList<>() : aportesByDeuda;
            deudasAlumno.setAportesAlumnosCiclo(aportesByDeuda);
        }
        FOR_DEUDA_DTO:
        for (DeudaDTO deudaAlumnoRepetida : deudasAlumnosRepetidas) {
            logger.debug("DEUDA REPETIDA");
            logger.debug(deudaAlumnoRepetida.toString());

            List<SaldoAfavorAlumno> saldosAfavorByAlumnoAndCuenta = saldosAfavor.stream()
                    .filter(x -> x.getAlumno().getPersona().equals(deudaAlumnoRepetida.getPersonaId()))
                    .filter(x -> x.getCuentaBancaria().getId().equals(deudaAlumnoRepetida.getCuentaBancaria()))
                    .collect(Collectors.toList());
            if (!saldosAfavorByAlumnoAndCuenta.isEmpty()) {
                continue;
            }

            List<Acreencia> acreenciaByRepeticion = acreenciasDeudaAlumno.stream()
                    .filter(x -> x.getPersona().getId().equals(deudaAlumnoRepetida.getPersonaId()))
                    .filter(x -> x.getMonto().compareTo(deudaAlumnoRepetida.getMonto()) == 0)
                    .filter(x -> x.getCuentaBancaria().getId().equals(deudaAlumnoRepetida.getCuentaBancaria()))
                    .filter(x -> x.getDescripcion().equals(deudaAlumnoRepetida.getDescripcion()))
                    .collect(Collectors.toList());
            Collections.sort(acreenciasDeudaAlumno, (x1, x2) -> x1.getFechaRegistro().compareTo(x2.getFechaRegistro()));

            for (Acreencia acreencia : acreenciaByRepeticion) {
                List<DeudaAlumno> deudasAlumno = deudasAlumnos.stream()
                        .filter(x -> x.getId().equals(acreencia.getInstanciaTabla()))
                        .collect(Collectors.toList());
                if (deudasAlumno.size() > 1) {
                    //esta inconsistencia no la podemos manejar por el momento
                    continue FOR_DEUDA_DTO;
                }
                if (deudasAlumno.isEmpty()) {
                    acreencia.setSeleccionado(true);
                    continue;
                }
                DeudaAlumno deudaAlumno = deudasAlumno.get(0);
                if (deudaAlumno.isEstadoPagado()) {
                    //esta inconsistencia no la podemos manejar por el momento
                    continue FOR_DEUDA_DTO;
                }
                acreencia.setSeleccionado(true);
                for (AporteAlumnoCiclo aporteAlumnoCiclo : deudaAlumno.getAportesAlumnosCiclo()) {
                    if (aporteAlumnoCiclo.getEstadoEnum() == EstadoAporteEnum.PAGO) {
                        acreencia.setSeleccionado(false);
                    }
                }
            }
            for (Acreencia acreencia : acreenciaByRepeticion) {
                if (acreencia.isSeleccionado()) {
                    Acreencia acreenciaUpd = new Acreencia(acreencia.getId());
                    acreenciaUpd.setEstadoEnum(DeudaEstadoEnum.ANU);
                    acreenciaUpd.setFechaActualizacion(ds.getFechaAccionAudit());
                    acreenciaUpd.setProcesoActualizacionEnum(ProcesoMethodEnum.CLN_DEU);
                    acreenciaDAO.updateColumns(acreenciaUpd, "estado", "fechaActualizacion", "procesoActualizacion");
                    break;
                }
            }
        }
       // throw new PhobosException("no pasaras papu");
    }

}
