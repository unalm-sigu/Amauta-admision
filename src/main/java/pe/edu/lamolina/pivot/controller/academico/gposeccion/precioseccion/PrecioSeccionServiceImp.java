package pe.edu.lamolina.pivot.controller.academico.gposeccion.precioseccion;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class PrecioSeccionServiceImp implements PrecioSeccionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SeccionDAO seccionDAO;


//    @Override
//    @Transactional
//    public void saveAmpliacionVacante(AmpliacionVacantes ampliacionVacante, DataSessionPivot ds) {
//
//        Seccion seccion = seccionDAO.find(ampliacionVacante.getSeccion());
//        List<AmpliacionVacantes> ampliaciones = ampliacionVacanteDAO.allPendientesBySeccion(seccion);
//        Assert.isTrue(ampliaciones.isEmpty(), "Aún existe solicitudes de ampliación pendientes de atención");
//
//        Seccion seccionSuperior = seccion.getSeccionSuperior();
//        if (seccionSuperior != null) {
//            List<Seccion> secciones = seccionDAO.allByGposSeccion(seccion.getGrupoSeccion());
//            for (Seccion seccBD : secciones) {
//                if (seccion.getId() == seccBD.getId().longValue()) {
//                    continue;
//                }
//                List<AmpliacionVacantes> ampliacionesOtras = ampliacionVacanteDAO.allPendientesBySeccion(seccBD);
//                Assert.isTrue(ampliacionesOtras.isEmpty(), "Aún existe solicitudes de ampliación pendientes para la sección " + seccBD.getCodigo2());
//            }
//        }
//
//        Persona persona = ds.getPersona();
//        Oficina oficinaMain = ampliacionVacante.getOficina();
//        Oficina oficinaReal = oficinaService.findOficinaHija(persona, oficinaMain);
//        Assert.isNotNull(oficinaReal, "Usted no se encuentra activo en la oficina " + oficinaMain.getNombre());
//
//        Colaborador colaborador = colaboradorDAO.findActivoByPersonaOficina(oficinaReal, persona);
//        Assert.isNotNull(colaborador, "Usted no se encuentra activo en la oficina " + oficinaReal.getNombre());
//
//        Assert.isTrue(seccion.getVacantesOcupadas() <= ampliacionVacante.getVacantesFin().intValue(), "No puede disminuir las vacantes menor a la cantidad de matriculados + reservados");
//        Aula aula = seccion.getAula();
//        if (aula != null && aula.getCapacidadAula() != null) {
//            Assert.isTrue(aula.getCapacidadAula().intValue() >= ampliacionVacante.getVacantesFin().intValue(), "No puede exceder la capacidad del aula");
//        }
//
//        if (seccionSuperior != null) {
//            aula = seccionSuperior.getAula();
//            if (aula != null && aula.getCapacidadAula() != null) {
//                int total = seccionSuperior.getVacantesOcupadas() + ampliacionVacante.getIncremento();
//                Assert.isTrue(aula.getCapacidadAula().intValue() >= total, "No puede exceder la capacidad del aula de la sección teórica");
//            }
//        }
//
//        ampliacionVacante.setColaborador(colaborador);
//        ampliacionVacante.setFechaRegistro(new Date());
//        ampliacionVacante.setUserRegistro(ds.getUsuario());
//        ampliacionVacante.setFechaSolicitud(new Date());
//        ampliacionVacante.setUserRegistro(ds.getUsuario());
//        ampliacionVacante.setEstadoEnum(AmpliacionVacanteEstadoEnum.PENDIENTE);
//        ampliacionVacanteDAO.save(ampliacionVacante);
//    }

//    @Override
//    public void savePrecioSeccion(Seccion precioSeccion, DataSessionPivot ds) {
//        Seccion seccion = seccionDAO.find(precioSeccion.getPrecio());
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

}
