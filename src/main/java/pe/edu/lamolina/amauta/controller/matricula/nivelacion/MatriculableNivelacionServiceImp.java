package pe.edu.lamolina.amauta.controller.matricula.nivelacion;

import static java.math.BigDecimal.ONE;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.controller.matricula.matriculable.MatriculableConnector;
import pe.edu.lamolina.amauta.controller.matricula.matriculable.MatriculableService;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.auditoria.ClonarCicloNivelacionDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.auditoria.ClonarCicloNivelacion;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.SituacionAcademicaEnum;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_3;
import pe.edu.lamolina.model.enums.TipoCicloEnum;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class MatriculableNivelacionServiceImp implements MatriculableNivelacionService {

    private final MatriculaResumenDAO matriculaResumenDAO;
    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final AlumnoCicloDAO alumnoCicloDAO;
    private final ClonarCicloNivelacionDAO clonarCicloNivelacionDAO;
    private final AlumnoDAO alumnoDAO;
    private final MatriculableService matriculableService;
    private final MatriculableConnector matriculableConector;

    @Override
    @Transactional
    public void ClonarNivelacionDTO(DataSessionPivot ds, ClonarNivelacionDTO clonarNivelacionDTO) {

        int codeInicio = clonarNivelacionDTO.getCicloOrigen().getCodigoInt();
        int codeFin = clonarNivelacionDTO.getCicloDestino().getCodigoInt();
        if (codeInicio >= codeFin) {
            throw new PhobosException("Ciclos no validos");
        }

        CicloAcademico origen = cicloAcademicoDAO.find(clonarNivelacionDTO.getCicloOrigen());
        if (origen.getTipoEnum() != TipoCicloEnum.REG) {
            throw new PhobosException("Ciclo origen no valido");
        }

        CicloAcademico destino = cicloAcademicoDAO.find(clonarNivelacionDTO.getCicloDestino());
        if (destino.getTipoEnum() != TipoCicloEnum.NIV) {
            throw new PhobosException("Ciclo destino no valido");
        }

        List<MatriculaResumen> matriculasOrigen = matriculaResumenDAO
                .allByCicloClonar(clonarNivelacionDTO.getCicloOrigen());
        List<Alumno> alumnos = matriculasOrigen.stream().map(x -> x.getAlumno())
                .collect(Collectors.toList());

        CicloAcademico cicloAcademicoAnterior = cicloAcademicoDAO.findAnteriorActivo(origen);
        log.debug("CICLO ACTIVO REGULAR ANTERIOR {} CODE {}", cicloAcademicoAnterior.getId(), cicloAcademicoAnterior.getCodigo());

        List<AlumnoCiclo> alumnosCicloSuspendido = alumnoCicloDAO.allSuspendidoByCiclo(cicloAcademicoAnterior);
        log.debug("SUSPENDIDOS {}", alumnosCicloSuspendido.size());

        List<AlumnoCiclo> alumnosCiclos = alumnoCicloDAO.allActivosRegularesByCicloResumen(destino);
        Map<Long, AlumnoCiclo> alumnoCicloMap = alumnosCiclos.stream()
                .collect(Collectors.toMap(x -> x.getAlumno().getId(), y -> y, (f, s) -> f));

        List<MatriculaResumen> matriculaResumenesDestino = matriculaResumenDAO.allByCicloClonarDestino(destino, alumnos);
        Map<Long, MatriculaResumen> matriculaDestinoMap = matriculaResumenesDestino
                .stream()
                .collect(Collectors.toMap(x -> x.getAlumno().getId(), y -> y, (f, s) -> f));
        log.debug("DESTINO YA MATRICULADOS {}", matriculaResumenesDestino.size());

        Map<Long, MatriculaResumen> matriculaResumenXAlumnoRegistrado = new LinkedHashMap();

        for (MatriculaResumen matriculaOrigen : matriculasOrigen) {

            Alumno alumno = matriculaOrigen.getAlumno();
            MatriculaResumen matriculaResumen = matriculaDestinoMap.get(alumno.getId());
            if (matriculaResumen != null) {
                matriculaResumenXAlumnoRegistrado.put(alumno.getId(), matriculaResumen);
                continue;
            }
            if ((alumno.getCicloIngreso().getId() == cicloAcademicoAnterior.getId().longValue()
                    && alumno.getSituacionAcademica().getCodigoEnum() == SituacionAcademicaEnum.S_9 && !matriculaOrigen.isEstadoMAT())
                    || (alumno.getCicloIngreso().getId() == origen.getId().longValue()
                    && alumno.getSituacionAcademica().getCodigoEnum() == SituacionAcademicaEnum.S_9 && !matriculaOrigen.isEstadoMAT())) {
                continue;
            }
            matriculaResumen = new MatriculaResumen();
            matriculaResumen.setSituacionInicio(alumno.getSituacionAcademica());
            matriculaResumen.setAlumno(alumno);
            matriculaResumen.setCicloAcademico(destino);
            matriculaResumen.setEstadoEnum(EstadoMatriculaEnum.NMAT);
            matriculaResumen.setTurnoAtencion(null);

            if (alumno.getCicloIngreso().getId() == origen.getId().longValue()) {
                matriculaResumen.setPrioridad(ONE);
            } else {
                matriculaResumen.setPrioridad(matriculaOrigen.getPrioridad());
            }

            matriculaResumen.setCursosMatriculados(0);
            matriculaResumen.setCursosRetirados(0);
            matriculaResumen.setCreditosTrikaPagados(0);
            matriculaResumen.setCreditosTrikaSeparados(0);
            matriculaResumen.setAutorizacionMatricula(Boolean.FALSE);
            matriculaResumen.setEsBeneficiadoUltimoCiclo(Boolean.FALSE);
            matriculaResumen.setCreditosPagados(0);
            matriculaResumen.setCreditosConsumidos(0);

            matriculaResumen.setCreditosMatriculados(0);
            matriculaResumen.setCreditosRetirados(0);
            matriculaResumen.setCursosMatriculados(0);
            matriculaResumen.setCursosRetirados(0);

            matriculaResumen.setCreditosMatriculadosPosgrado(0);
            matriculaResumen.setCreditosMatriculadosPregrado(0);
            matriculaResumen.setCreditosRetirados(0);

            if (matriculaOrigen.getCicloAcademicoInfo() != null) {
                matriculaResumen.setCreditosCursadosCiclo(matriculaOrigen.getCreditosCursadosCiclo());
                matriculaResumen.setCreditosAcumulados(matriculaOrigen.getCreditosAcumulados());
                matriculaResumen.setCreditosAprobadosCiclo(matriculaOrigen.getCreditosAprobadosCiclo());
                matriculaResumen.setCreditosAprobadosAcumulados(matriculaOrigen.getCreditosAprobadosAcumulados());
                matriculaResumen.setPromedioSemestral(matriculaOrigen.getPromedioSemestral());
                matriculaResumen.setCicloAcademicoInfo(matriculaOrigen.getCicloAcademicoInfo());
                matriculaResumen.setPuntajePrioridad(matriculaOrigen.getPuntajePrioridad());
            } else {
                AlumnoCiclo aluCiclo = alumnoCicloMap.get(matriculaOrigen.getAlumno().getId());
                log.debug("ALUMNOOO:: {}", matriculaOrigen.getAlumno().getCodigo());
                if (aluCiclo != null) {
                    matriculaResumen.setCreditosCursadosCiclo(aluCiclo.getCreditosCursadosCiclo());
                    matriculaResumen.setCreditosAcumulados(aluCiclo.getCreditosAcumulados());
                    matriculaResumen.setCreditosAprobadosCiclo(aluCiclo.getCreditosAprobadosCiclo());
                    matriculaResumen.setCreditosAprobadosAcumulados(aluCiclo.getCreditosAprobadosAcumulados());
                    matriculaResumen.setPromedioSemestral(aluCiclo.getPromedioCiclo());
                    matriculaResumen.setCicloAcademicoInfo(aluCiclo.getCicloAcademico());
                    matriculaResumen.setPuntajePrioridad(aluCiclo.getPromedioAcumulado());
                }
            }
            matriculaResumen.setMotivoMatriculable(matriculaOrigen.getMotivoMatriculable());
            matriculaResumenDAO.save(matriculaResumen);
            matriculaResumenXAlumnoRegistrado.put(alumno.getId(), matriculaResumen);

        }

        for (AlumnoCiclo alumnoCiclo : alumnosCicloSuspendido) {

            Alumno alumno = alumnoCiclo.getAlumno();

            MatriculaResumen matriculaResumen = matriculaResumenXAlumnoRegistrado.get(alumno.getId());
            if (matriculaResumen != null) {
                continue;
            }

            matriculaResumen = new MatriculaResumen();

            matriculaResumen.setSituacionInicio(alumnoCiclo.getSituacionFinal());
            matriculaResumen.setAlumno(alumno);
            matriculaResumen.setCicloAcademico(destino);
            matriculaResumen.setEstadoEnum(EstadoMatriculaEnum.NMAT);
            matriculaResumen.setTurnoAtencion(null);

            matriculaResumen.setPrioridad(null);

            matriculaResumen.setCursosMatriculados(0);
            matriculaResumen.setCursosRetirados(0);
            matriculaResumen.setCreditosTrikaPagados(0);
            matriculaResumen.setCreditosTrikaSeparados(0);

            matriculaResumen.setAutorizacionMatricula(Boolean.FALSE);
            matriculaResumen.setEsBeneficiadoUltimoCiclo(Boolean.FALSE);
            matriculaResumen.setCreditosPagados(0);
            matriculaResumen.setCreditosConsumidos(0);

            matriculaResumen.setCreditosMatriculados(0);

            matriculaResumen.setCreditosMatriculadosPosgrado(0);
            matriculaResumen.setCreditosMatriculadosPregrado(0);
            matriculaResumen.setCreditosRetirados(0);
            matriculaResumen.setCreditosCursadosCiclo(alumnoCiclo.getCreditosCursadosCiclo());
            matriculaResumen.setCreditosAcumulados(alumnoCiclo.getCreditosAcumulados());
            matriculaResumen.setCreditosAprobadosCiclo(alumnoCiclo.getCreditosAprobadosCiclo());
            matriculaResumen.setCreditosAprobadosAcumulados(alumnoCiclo.getCreditosAprobadosAcumulados());

            matriculaResumen.setPromedioSemestral(alumnoCiclo.getPromedioCiclo());
//            matriculaResumen.setPuntajePrioridad(alumnoCiclo.getPromedioAcumulado());

            matriculaResumen.setCicloAcademicoInfo(alumnoCiclo.getCicloAcademico());

            matriculaResumen.setMotivoMatriculable(null);

            matriculaResumenDAO.save(matriculaResumen);
            matriculaResumenXAlumnoRegistrado.put(alumno.getId(), matriculaResumen);

            alumno.setSituacionAcademica(new SituacionAcademica(S_3.getId()));
            alumnoDAO.updateColumns(alumno, "situacionAcademica");
            matriculableConector.procesarPrioridadAlumno(matriculaResumen, alumnoCiclo);
            matriculaResumenDAO.update(matriculaResumen);

        }

        destino.setFechaPrioridades(new Date());
        destino.setFechaCierrePrioridades(new Date());
        destino.setFechaVerificaNmat(new Date());
        destino.setFechaMatriculables(new Date());
        cicloAcademicoDAO.update(destino);

        ClonarCicloNivelacion clonarCicloNivelacion = new ClonarCicloNivelacion();
        clonarCicloNivelacion.setUsuario(ds.getUsuario());
        clonarCicloNivelacion.setCicloOrigen(clonarNivelacionDTO.getCicloOrigen());
        clonarCicloNivelacion.setCicloDestino(destino);
        clonarCicloNivelacionDAO.save(clonarCicloNivelacion);

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void generarPrioridad(CicloAcademico cicloDestino) {
        matriculableService.generarPrioridad(cicloDestino);
    }

}
