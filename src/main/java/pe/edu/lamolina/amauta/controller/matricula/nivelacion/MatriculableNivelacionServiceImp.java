package pe.edu.lamolina.amauta.controller.matricula.nivelacion;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
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

    @Override
    @Transactional
    public void ClonarNivelacionDTO(DataSessionPivot ds, ClonarNivelacionDTO clonarNivelacionDTO) {

        int codeInicio = clonarNivelacionDTO.getCicloOrigen().getCodigoInt();
        int codeFin = clonarNivelacionDTO.getCicloDestino().getCodigoInt();
        if (codeInicio >= codeFin) {
            throw new PhobosException("Ciclo no valido");
        }
        CicloAcademico destino = cicloAcademicoDAO.find(clonarNivelacionDTO.getCicloDestino());
        if (destino.getTipoEnum() != TipoCicloEnum.NIV) {
            throw new PhobosException("Ciclo destino no valido");
        }
        List<MatriculaResumen> matriculasOrigen = matriculaResumenDAO
                .allByCicloClonar(clonarNivelacionDTO.getCicloOrigen());
        List<Alumno> alumnos = matriculasOrigen.stream().map(x -> x.getAlumno())
                .collect(Collectors.toList());
        List<MatriculaResumen> matriculaResumenesDestino
                = matriculaResumenDAO.allByCicloClonarDestino(destino, alumnos);
        Map<Long, MatriculaResumen> matriculaDestinoMap = matriculaResumenesDestino
                .stream()
                .collect(Collectors.toMap(x -> x.getAlumno().getId(), y -> y, (f, s) -> f));
        List<AlumnoCiclo> alumnoCiclos
                = alumnoCicloDAO.allByCicloAlumnos(clonarNivelacionDTO.getCicloOrigen(), alumnos);
        Map<Long, AlumnoCiclo> alumnosCicloMap = alumnoCiclos
                .stream()
                .collect(Collectors.toMap(x -> x.getAlumno().getId(), y -> y, (f, s) -> f));
        for (MatriculaResumen matriculaOrigen : matriculasOrigen) {
            Alumno alumno = matriculaOrigen.getAlumno();
            MatriculaResumen matriculaResumen = matriculaDestinoMap.get(alumno.getId());
            if (matriculaResumen != null) {
                continue;
            }
            matriculaResumen = new MatriculaResumen();
            AlumnoCiclo alumnoCiclo = alumnosCicloMap.get(alumno.getId());
            if (alumnoCiclo != null) {
                if (alumnoCiclo.getSituacionFinal() != null) {
                    matriculaResumen.setSituacionInicio(alumnoCiclo.getSituacionFinal());
                } else {
                    matriculaResumen.setSituacionInicio(alumnoCiclo.getSituacionInicio());
                }
            }
            matriculaResumen.setAlumno(alumno);
            matriculaResumen.setCicloAcademico(destino);
            matriculaResumen.setEstadoEnum(EstadoMatriculaEnum.NMAT);
            matriculaResumen.setTurnoAtencion(null);
            matriculaResumen.setPrioridad(matriculaOrigen.getPrioridad());
            matriculaResumen.setPuntajePrioridad(matriculaOrigen.getPuntajePrioridad());
            matriculaResumen.setCursosMatriculados(0);
            matriculaResumen.setCursosRetirados(0);
            matriculaResumen.setCreditosTrikaPagados(0);
            matriculaResumen.setCreditosTrikaSeparados(0);
            matriculaResumen.setPromedioSemestral(matriculaOrigen.getPromedioSemestral());
            matriculaResumen.setAutorizacionMatricula(Boolean.FALSE);
            matriculaResumen.setEsBeneficiadoUltimoCiclo(Boolean.FALSE);
            matriculaResumen.setCreditosPagados(0);
            matriculaResumen.setCreditosConsumidos(0);
            matriculaResumen.setCreditosMatriculados(0);
            matriculaResumen.setCreditosMatriculadosPosgrado(0);
            matriculaResumen.setCreditosMatriculadosPregrado(0);
            matriculaResumen.setCreditosRetirados(0);
            matriculaResumenDAO.save(matriculaResumen);
        }
    }
}
