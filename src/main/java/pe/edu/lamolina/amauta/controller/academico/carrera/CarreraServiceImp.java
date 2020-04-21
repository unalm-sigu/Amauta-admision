package pe.edu.lamolina.amauta.controller.academico.carrera;

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
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AreaPosgrado;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.OrientacionCarrera;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.enums.EnteAcademicoEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.matricula.AlumnoAvanceCurricular;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.amauta.dao.academico.AreaPosgradoDAO;
import pe.edu.lamolina.amauta.dao.academico.CarreraDAO;
import pe.edu.lamolina.amauta.dao.academico.FacultadDAO;
import pe.edu.lamolina.amauta.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.amauta.dao.academico.OrientacionCarreraDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class CarreraServiceImp implements CarreraService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    FacultadDAO facultadDAO;

    @Autowired
    AreaPosgradoDAO areaPosgradoDAO;

    @Autowired
    OrientacionCarreraDAO orientacionCarreraDAO;

    @Override
    public List<Carrera> allByDynatable(DynatableFilter filter) {
        List<Carrera> carreras = carreraDAO.allByDynatable(filter);
        List<OrientacionCarrera> orientaciones = orientacionCarreraDAO.allByCarreras(carreras);
        Map<Long, List<OrientacionCarrera>> mapOrientaciones = TypesUtil.convertListToMapList("carrera.id", orientaciones);

        for (Carrera carrera : carreras) {
            List<OrientacionCarrera> orientacionesCarr = mapOrientaciones.get(carrera.getId());
            orientacionesCarr = (orientacionesCarr == null) ? new ArrayList() : orientacionesCarr;
            carrera.setOrientacionCarrera(orientacionesCarr);
        }
        return carreras;
    }

    @Override
    @Transactional
    public void cambiarEstadoCarrera(Carrera carrera) {
        Carrera carrreraBD = carreraDAO.find(carrera.getId());
        if (carrera.getEstadoEnum() == EnteAcademicoEstadoEnum.ACT) {
            carrreraBD.setEstadoEnum(EnteAcademicoEstadoEnum.INA);
            carrreraBD.setMotivoAnulacion(carrera.getMotivoAnulacion());
            carrreraBD.setFechaAnulacion(new Date());
        } else {
            carrreraBD.setEstadoEnum(EnteAcademicoEstadoEnum.ACT);
        }
        carreraDAO.update(carrreraBD);
    }

    @Override
    @Transactional
    public void cambiarEstadoAdmision(Carrera carrera) {
        Carrera carrreraBD = carreraDAO.find(carrera.getId());
        if (carrreraBD.getEstadoAdmisionEnum() == EnteAcademicoEstadoEnum.ACT) {
            carrreraBD.setEstadoAdmisionEnum(EnteAcademicoEstadoEnum.INA);
        } else {
            carrreraBD.setEstadoAdmisionEnum(EnteAcademicoEstadoEnum.ACT);
        }
        carreraDAO.update(carrreraBD);
    }

    @Override
    public List<ModalidadEstudio> allPrePostgrado(Compania cia) {
        return modalidadEstudioDAO.allPrePostgrado(cia);
    }

    @Override
    @Transactional
    public Carrera save(Carrera carreraForm, DataSessionPivot ds) {

        ObjectUtil.eliminarAttrSinId(carreraForm);

        if (carreraForm.getId() == null) {

            carreraForm.setEstadoEnum(EnteAcademicoEstadoEnum.CRE);
            carreraForm.setEstadoAdmisionEnum(EnteAcademicoEstadoEnum.CRE);
            carreraForm.setUserRegistro(ds.getUsuario());
            carreraForm.setFechaRegistro(new Date());
            carreraDAO.save(carreraForm);

            return carreraForm;

        }

        Carrera carreraBD = carreraDAO.find(carreraForm.getId());
        carreraBD.setNombre(carreraForm.getNombre());
        carreraBD.setFacultad(carreraForm.getFacultad());
        carreraBD.setAreaPosgrado(carreraForm.getAreaPosgrado());
        carreraDAO.update(carreraBD);

        return carreraBD;
    }

    private String findLastCodigo(Carrera carrera, OrientacionCarrera orientacion) {
        if (orientacion == null) {
            orientacion = orientacionCarreraDAO.findLastByCarrera(carrera);
        }

        String correlativoTmp = "";
        Integer correlativo = null;
        if (orientacion != null) {
            String codigo = orientacion.getCodigo();
            Integer anchoCodigo = orientacion.getCodigo().length();
            correlativoTmp = codigo.substring(anchoCodigo - 1, anchoCodigo);

            correlativo = Integer.valueOf(correlativoTmp) + 1;

        } else {
            correlativo = 1;
        }

        return NumberFormat.codigo(correlativo, 2);
    }

    @Override
    public List<Facultad> allFacultades() {
        return facultadDAO.allNormal();
    }

    @Override
    public Carrera find(Long id) {
        Carrera carrera = carreraDAO.find(id);
        List<OrientacionCarrera> orientaciones = orientacionCarreraDAO.allByCarrera(carrera);
        carrera.setOrientacionCarrera(orientaciones);

        return carrera;
    }

//    @Override
//    @Transactional
//    public void saveOrientacion(Long idCarrera, Long idOrientacion, String nombreOrientacion, Usuario usuario) {
//        try {
//            if (idOrientacion == null) {
//                Carrera carrera = carreraDAO.find(idCarrera);
//                String correlativo = ""; //this.findLastCodigo(carrera);
//
//                OrientacionCarrera oriCarrera = new OrientacionCarrera();
//                oriCarrera.setCarrera(carrera);
//                oriCarrera.setCodigo(carrera.getCodigo() + correlativo);
//                oriCarrera.setNombre(nombreOrientacion);
//                oriCarrera.setEstado(EstadoEnum.ACT.name());
//                oriCarrera.setUserRegistro(usuario);
//                oriCarrera.setFechaRegistro(new Date());
//                orientacionCarreraDAO.save(oriCarrera);
//
//            } else {
//                OrientacionCarrera orientacion = orientacionCarreraDAO.find(idOrientacion);
//                orientacion.setNombre(nombreOrientacion);
//                orientacionCarreraDAO.update(orientacion);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
    @Override
    @Transactional
    public List<OrientacionCarrera> saveOrientaciones(Carrera carreraForm, DataSessionPivot ds) {
        Assert.isNotNull(carreraForm.getOrientacionCarrera(), "No ha enviado las orientaciones que desea guardar");
        Assert.isFalse(carreraForm.getOrientacionCarrera().isEmpty(), "No ha enviado las orientaciones que desea guardar");

        Carrera carreraBD = carreraDAO.find(carreraForm.getId());
        OrientacionCarrera orientacionAntes = null;

        List<OrientacionCarrera> orientacionesBD = orientacionCarreraDAO.allByCarrera(carreraBD);
        List<OrientacionCarrera> nuevasOrientacionesBD = new ArrayList();
        List<OrientacionCarrera> orientacionesForm = carreraForm.getOrientacionCarrera();
        for (OrientacionCarrera orientacion : orientacionesForm) {
            if (orientacion.getId() == null) {
                String correlativo = this.findLastCodigo(carreraBD, orientacionAntes);
                orientacion.setCodigo(carreraBD.getCodigo() + correlativo);
                orientacion.setCarrera(carreraBD);
                orientacion.setEstadoEnum(EstadoEnum.ACT);
                orientacion.setUserRegistro(ds.getUsuario());
                orientacion.setFechaRegistro(new Date());
                orientacion.setUserActivacion(ds.getUsuario());
                orientacion.setFechaActivacion(new Date());
                orientacionCarreraDAO.save(orientacion);

                nuevasOrientacionesBD.add(orientacion);
                orientacionAntes = orientacion;
            }
        }
        Assert.isFalse(nuevasOrientacionesBD.isEmpty(), "No ha enviado las orientaciones que desea guardar");
        orientacionesBD.addAll(nuevasOrientacionesBD);

        return orientacionesBD;
    }

//    @Override
//    @Transactional
//    public void deleteOrientacion(Long idOrientacion) {
//        orientacionCarreraDAO.delete(idOrientacion);
//    }
    @Override
    @Transactional
    public OrientacionCarrera deleteOrientacion(OrientacionCarrera orientacionForm, DataSessionPivot ds) {
        OrientacionCarrera orientacionBD = orientacionCarreraDAO.find(orientacionForm.getId());
        Assert.isNotNull(orientacionBD, "La Orientación que desea eliminar ya no existe en el sistema");
        //Assert.isTrue(1 == 2, "Horror en el sistema");

        OrientacionCarrera orientacionAlumnos = null;
        OrientacionCarrera orientacionPlan = orientacionCarreraDAO.findForPlanCurriculares(orientacionForm);
        if (orientacionPlan == null) {
            orientacionAlumnos = orientacionCarreraDAO.findForAlumnos(orientacionForm);
        }

        if (orientacionPlan == null && orientacionAlumnos == null) {
            orientacionCarreraDAO.delete(orientacionBD);
            return null;
        } else {
            Assert.isFalse(orientacionBD.getEstadoEnum() != EstadoEnum.ACT, "La Orientación ya se encuentra desactivada");
            orientacionBD.setEstadoEnum(EstadoEnum.INA);
            orientacionBD.setMotivoAnulacion(orientacionForm.getMotivoAnulacion());
            orientacionBD.setFechaAnulacion(new Date());
            orientacionBD.setUserAnulacion(ds.getUsuario());
            orientacionCarreraDAO.update(orientacionBD);
            return orientacionBD;
        }

    }

    @Override
    @Transactional
    public OrientacionCarrera activarOrientacion(OrientacionCarrera orientacionForm, DataSessionPivot ds) {
        OrientacionCarrera orientacionBD = orientacionCarreraDAO.find(orientacionForm.getId());
//        Assert.isTrue(1 == 2, "Horror en el sistema");
        Assert.isFalse(orientacionBD.getEstadoEnum() == EstadoEnum.ACT, "La Orientación ya se encuentra activa");
        orientacionBD.setEstadoEnum(EstadoEnum.ACT);
        orientacionBD.setUserActivacion(ds.getUsuario());
        orientacionBD.setFechaActivacion(new Date());
        orientacionBD.setUserAnulacion(null);
        orientacionBD.setFechaAnulacion(null);
        orientacionBD.setMotivoAnulacion(null);
        orientacionCarreraDAO.update(orientacionBD);

        return orientacionBD;
    }

//    @Override
//    public List<OrientacionCarrera> allByIdCarreraDynatable(DynatableFilter filter, Long idCarrera) {
//        return orientacionCarreraDAO.allByIdCarreraDynatable(filter, idCarrera);
//    }
    @Override
    @Transactional
    public OrientacionCarrera editarOrientacion(OrientacionCarrera orientacionForm, DataSessionPivot ds) {
        OrientacionCarrera orientacionBD = orientacionCarreraDAO.find(orientacionForm.getId());
        Assert.isNotNull(orientacionBD, "La Orientación que solicita modificar no existe en el sistema");
        Assert.isTrue(orientacionBD.getEstadoEnum() == EstadoEnum.ACT, "La Orientación debe encontrarse activa");
        orientacionBD.setNombre(orientacionForm.getNombre());
        orientacionCarreraDAO.update(orientacionBD);

        return orientacionBD;
    }

    @Override
    public CarreraResumen resumen() {
        return carreraDAO.resumen();
    }

    @Override
    public List<Carrera> all() {
        return carreraDAO.all();
    }

    @Override
    public List<AreaPosgrado> allAreaPosgrado() {
        return areaPosgradoDAO.all();
    }

}
