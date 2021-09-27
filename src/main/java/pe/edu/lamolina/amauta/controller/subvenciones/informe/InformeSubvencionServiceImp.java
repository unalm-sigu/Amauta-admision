package pe.edu.lamolina.amauta.controller.subvenciones.informe;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.dao.bienestar.InformeSubvencionadoDAO;
import pe.edu.lamolina.amauta.dao.tramite.AlumnoBolsaInvestigacionDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.bienestar.InformeSubvencionado;
import static pe.edu.lamolina.model.enums.EstadoInformeSubvencionEnum.ACEPTADO;
import static pe.edu.lamolina.model.enums.EstadoInformeSubvencionEnum.ENTREGADO;
import static pe.edu.lamolina.model.enums.EstadoInformeSubvencionEnum.OBSERVA;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.tramite.AlumnoBolsaInvestigacion;
import pe.edu.lamolina.model.tramite.TramiteSubvencion;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class InformeSubvencionServiceImp implements InformeSubvencionService {

    private final AlumnoBolsaInvestigacionDAO alumnoBolsaInvestigacionDAO;
    private final InformeSubvencionadoDAO informeSubvencionadoDAO;

    @Override
    public List<InformeSubvencionado> allInformesByDynatble(Persona supervisor, CicloAcademico ciclo, DynatableFilter filter) {
        List<InformeSubvencionado> informes = informeSubvencionadoDAO.allBySupervisorCiclo(supervisor, ciclo, filter);

        List<TramiteSubvencion> subvenciones = informes.stream()
                .map(informe -> informe.getAlumnoSubvencionado().getTramiteSubvencion())
                .collect(Collectors.toList());

        List<AlumnoBolsaInvestigacion> alumnoBolsas = alumnoBolsaInvestigacionDAO.allByTramitesSubvenciones(subvenciones);
        Map<Long, AlumnoBolsaInvestigacion> mapAlumnoBolsa = TypesUtil.convertListToMap("tramiteSubvencion.id", alumnoBolsas);

        for (InformeSubvencionado informe : informes) {
            TramiteSubvencion subvencion = informe.getAlumnoSubvencionado().getTramiteSubvencion();
            AlumnoBolsaInvestigacion alumnoBolsa = mapAlumnoBolsa.get(subvencion.getId());
            if (alumnoBolsa != null) {
                informe.setTituloInvestigacion(alumnoBolsa.getNombreInvestigacion());
            }
        }

        return informes;
    }

    @Override
    @Transactional
    public void aprobarInforme(InformeSubvencionado informeForm, Persona supervisorForm, DataSessionPivot ds) {
        InformeSubvencionado informeBD = informeSubvencionadoDAO.find(informeForm.getId());
        Assert.isNotNull(informeBD, "No se pudo ubicar el registro de este informe");

        Assert.isTrue(informeBD.getEstadoEnum() == ENTREGADO, "Este informe debe estar en estado " + ENTREGADO.getValue());

        Persona supervisorBD = informeBD.getSupervisorVoBo().getPersona();
        Assert.isTrue(supervisorBD.getId().equals(supervisorForm.getId()), "El supervisor de este informe corresponde a otra persona");

        informeBD.setEstadoEnum(ACEPTADO);
        informeBD.setFechaVoBo(new Date());
        informeBD.setSupervisorVoBo(informeBD.getSupervisorVoBo());
        informeSubvencionadoDAO.update(informeBD);

    }

    @Override
    @Transactional
    public void observarInforme(InformeSubvencionado informeForm, Persona supervisorForm, DataSessionPivot ds) {
        InformeSubvencionado informeBD = informeSubvencionadoDAO.find(informeForm.getId());
        Assert.isNotNull(informeBD, "No se pudo ubicar el registro de este informe");

        Assert.isTrue(informeBD.getEstadoEnum() == ENTREGADO, "Este informe debe estar en estado " + ENTREGADO.getValue());

        Persona supervisorBD = informeBD.getSupervisorVoBo().getPersona();
        Assert.isTrue(supervisorBD.getId().equals(supervisorForm.getId()), "El supervisor de este informe corresponde a otra persona");

        informeBD.setEstadoEnum(OBSERVA);
        informeBD.setObservaciones(informeForm.getObservaciones());
        informeBD.setFechaObservaciones(new Date());
        informeSubvencionadoDAO.update(informeBD);

    }

}
