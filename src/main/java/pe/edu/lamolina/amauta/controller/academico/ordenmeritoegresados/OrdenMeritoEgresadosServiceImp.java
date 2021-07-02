package pe.edu.lamolina.amauta.controller.academico.ordenmeritoegresados;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ControlMeritoEgresado;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.ControlOrdenMeritoEscalaEnum;
import pe.edu.lamolina.model.enums.ControlOrdenMeritoEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.CarreraDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.ControlMeritoEgresadoDAO;
import pe.edu.lamolina.amauta.dao.academico.EgresadoDAO;
import pe.edu.lamolina.amauta.dao.academico.FacultadDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.AlumnoCiclo;

@Service
@Transactional(readOnly = true)
public class OrdenMeritoEgresadosServiceImp implements OrdenMeritoEgresadosService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    FacultadDAO facultadDAO;
    @Autowired
    EgresadoDAO egresadoDAO;
    @Autowired
    ControlMeritoEgresadoDAO controlOrdenMeritoDAO;
    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;
    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;
    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;
    @Autowired
    CarreraDAO carreraDAO;
    @Autowired
    AlumnoDAO alumnoDAO;
    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;

    @Override
    @Transactional
    public void generarDatos(CicloAcademico cicloAcademico, DataSessionPivot ds) {
        logger.debug("generando datos para el ciclo {}", cicloAcademico.getId());
        egresadoDAO.deleteInfoOrdenMeritoByCicloAcademico(cicloAcademico);
        controlOrdenMeritoDAO.deleteByCicloAcademico(cicloAcademico);
        logger.debug("removiendo orden merito success...");

        List<Egresado> egresadosDB = egresadoDAO.allByCicloAcademico(cicloAcademico);

        logger.debug("total de egresados en db x ciclo {}", egresadosDB.size());

        Date now = new Date();

        ControlMeritoEgresado controlCiclo = new ControlMeritoEgresado();
        controlCiclo.setCicloAcademico(cicloAcademico);
        controlCiclo.setEscala(ControlOrdenMeritoEscalaEnum.CICLO);
        controlCiclo.setEstado(ControlOrdenMeritoEstadoEnum.CRE);

        controlCiclo.setUserRegistro(ds.getUsuario());
        controlCiclo.setFechaRegistro(now);

        logger.debug("carreras...");
        List<Carrera> carreras = carreraDAO.allActivasByModalidadEnum(ModalidadEstudioEnum.PRE);
        Collections.sort(carreras, new Carrera.CompareCodigo());
        carreras.forEach(x -> logger.debug("{} {}", x.getId(), x.getNombre()));

        logger.debug("facultades...");
        Map<Long, Facultad> mapFacultad = TypesUtil.convertListToMap("facultad.id", "facultad", carreras);
        List<Facultad> facultades = new ArrayList(mapFacultad.values());
        facultades.forEach(x -> logger.debug("{} {}", x.getId(), x.getNombre()));

        logger.debug("jerarquia...");
        Map<Long, List<Carrera>> mapCarreras = TypesUtil.convertListToMapList("facultad.id", carreras);
        for (Map.Entry<Long, List<Carrera>> entry : mapCarreras.entrySet()) {
            Facultad fac = mapFacultad.get(entry.getKey());
            List<Carrera> carrerasFac = entry.getValue();
            fac.setCarrera(carrerasFac);
            logger.debug("-- {}", fac.getNombre());
            for (Carrera car : carrerasFac) {
                logger.debug("---- {}", car.getNombre());
            }
        }

        logger.debug("facultad sin especialidad ...");
        List<Facultad> facultadUnica = facultades.stream().filter(fac -> fac.getCarrera().size() == 1).collect(Collectors.toList());
        Collections.sort(facultadUnica, new Facultad.CompareCodigo());
        facultadUnica.forEach(x -> logger.debug("{} {}", x.getId(), x.getNombre()));

        logger.debug("facultad con especialidad ...");
        List<Facultad> noFacultadUnica = facultades.stream().filter(fac -> fac.getCarrera().size() > 1).collect(Collectors.toList());
        Collections.sort(noFacultadUnica, new Facultad.CompareCodigo());
        noFacultadUnica.forEach(x -> logger.debug("{} {}", x.getId(), x.getNombre()));

        logger.debug("egresado x faculta sin especialidad ...");
        Map<Facultad, List<Egresado>> mapAlumnoByFacultad = egresadosDB.stream()
                .filter(ac -> facultadUnica.contains(ac.getCarrera().getFacultad()))
                .collect(Collectors.groupingBy(ac -> ac.getCarrera().getFacultad()));

        logger.debug("egresado x faculta con especialidad ...");
        Map<Carrera, List<Egresado>> mapAlumnosByCarrera = egresadosDB.stream()
                .filter(ac -> noFacultadUnica.contains(ac.getCarrera().getFacultad()))
                .collect(Collectors.groupingBy(ac -> ac.getCarrera()));

        logger.debug("total egresado x facultad ok ...");
        Map<Long, List<Egresado>> mapAlumnosByIdFacultad = TypesUtil.convertListToMapList("carrera.facultad.id", egresadosDB);
        logger.debug("total egresado x carrera ok ...");
        Map<Long, List<Egresado>> mapAlumnosByIdEspecialidad = TypesUtil.convertListToMapList("carrera.id", egresadosDB);

        List<AlumnoCiclo> alusCiclo = alumnoCicloDAO.allByCicloAcademico(cicloAcademico);
        Map<Long, AlumnoCiclo> mapAlumnosCiclo = new HashMap();
        for (AlumnoCiclo alumnoCiclo : alusCiclo) {
            mapAlumnosCiclo.put(alumnoCiclo.getAlumno().getId(), alumnoCiclo);
        }

        Integer total = 0;
        for (Map.Entry<Facultad, List<Egresado>> entry : mapAlumnoByFacultad.entrySet()) {
            total += entry.getValue().size();
        }
        for (Map.Entry<Carrera, List<Egresado>> entry : mapAlumnosByCarrera.entrySet()) {
            total += entry.getValue().size();
        }
        controlCiclo.setTotalAlumnos(total);
        logger.debug("total egresado X ciclo {}", total);
        controlOrdenMeritoDAO.save(controlCiclo);
        logger.debug("tcontrolCiclo id {}", controlCiclo.getId());

        for (Facultad facultad : facultadUnica) {

            ControlMeritoEgresado controlFacultad = new ControlMeritoEgresado();

            List<Egresado> egresadosXfacultad = mapAlumnosByIdFacultad.getOrDefault(facultad.getId(), new ArrayList());
            if (egresadosXfacultad.isEmpty()) {
                continue;
            }

            controlFacultad.setFacultad(facultad);
            controlFacultad.setCicloAcademico(cicloAcademico);
            controlFacultad.setEscala(ControlOrdenMeritoEscalaEnum.FAC);
            controlFacultad.setEstado(ControlOrdenMeritoEstadoEnum.CRE);
            controlFacultad.setTotalAlumnos(egresadosXfacultad.size());
            controlFacultad.setUserRegistro(ds.getUsuario());
            controlFacultad.setFechaRegistro(now);

            controlOrdenMeritoDAO.save(controlFacultad);

            for (Egresado egresado : egresadosXfacultad) {
                egresado.setControlMeritoFacultad(controlFacultad);
                egresado.setControlMeritoCiclo(controlCiclo);
            }

        }

        for (Facultad facultad : noFacultadUnica) {

            ControlMeritoEgresado controlFacultad = new ControlMeritoEgresado();

            List<Egresado> egresadosXfacultad = mapAlumnosByIdFacultad.getOrDefault(facultad.getId(), new ArrayList());
            if (egresadosXfacultad.isEmpty()) {
                continue;
            }

            controlFacultad.setFacultad(facultad);
            controlFacultad.setCicloAcademico(cicloAcademico);
            controlFacultad.setEscala(ControlOrdenMeritoEscalaEnum.FAC);
            controlFacultad.setEstado(ControlOrdenMeritoEstadoEnum.CRE);
            controlFacultad.setTotalAlumnos(egresadosXfacultad.size());
            controlFacultad.setUserRegistro(ds.getUsuario());
            controlFacultad.setFechaRegistro(now);

            controlOrdenMeritoDAO.save(controlFacultad);

            for (Egresado egresado : egresadosXfacultad) {
                egresado.setControlMeritoFacultad(controlFacultad);
                egresado.setControlMeritoCiclo(controlCiclo);
            }

            for (Carrera carrera : facultad.getCarrera()) {

                ControlMeritoEgresado controlEspecialidad = new ControlMeritoEgresado();

                List<Egresado> egresadosXespecialidad = mapAlumnosByIdEspecialidad.getOrDefault(carrera.getId(), new ArrayList());
                if (egresadosXespecialidad.isEmpty()) {
                    continue;
                }

                controlEspecialidad.setCarrera(carrera);
                controlEspecialidad.setCicloAcademico(cicloAcademico);
                controlEspecialidad.setEscala(ControlOrdenMeritoEscalaEnum.ESPE);
                controlEspecialidad.setEstado(ControlOrdenMeritoEstadoEnum.CRE);
                controlEspecialidad.setTotalAlumnos(egresadosXespecialidad.size());
                controlEspecialidad.setUserRegistro(ds.getUsuario());
                controlEspecialidad.setFechaRegistro(now);

                controlOrdenMeritoDAO.save(controlEspecialidad);

                for (Egresado alumnoCiclo : egresadosXespecialidad) {
                    alumnoCiclo.setControlMeritoCarrera(controlEspecialidad);
                    alumnoCiclo.setControlMeritoCiclo(controlCiclo);
                }

            }
        }

        logger.debug("copiando promedioAcumulado y creditosAcumulados ...");
        for (Egresado egresado : egresadosDB) {
            BigDecimal promedioAcumulado = egresado.getAlumno().getPromedioAcumulado();
            promedioAcumulado = promedioAcumulado.setScale(2, RoundingMode.DOWN);
            egresado.setPromedioAcumulado(promedioAcumulado);
            egresado.setCreditosAcumulados(mapAlumnosCiclo.get(egresado.getAlumno().getId()).getCreditosAcumulados());
            logger.debug("Alumno {} PromedioAcumulado {}  CreditosAcumulados {}", egresado.getAlumno().getCodigo(), egresado.getPromedioAcumulado(), egresado.getCreditosAcumulados());
            egresadoDAO.update(egresado);
        }
    }

    @Override
    @Transactional
    public void calcularMeritos(CicloAcademico cicloAcademico, DataSessionPivot ds) {

        List<ControlMeritoEgresado> coms = controlOrdenMeritoDAO.allByCicloAcademico(cicloAcademico);

        Date now = new Date();

        for (ControlMeritoEgresado com : coms) {
            com.setEstado(ControlOrdenMeritoEstadoEnum.CALC);
            com.setFechaCalculo(now);
            com.setUserCalculo(ds.getUsuario());
        }

        List<Egresado> egresados = egresadoDAO.allByControlesOrdenMerito(coms);

        Collections.sort(egresados, Comparator.comparing(Egresado::getPromedioAcumulado).reversed());

        Integer puesto = 0;
        Integer puestoActual = 0;
        BigDecimal promedio = null;
        for (Egresado egresado : egresados) {
            puestoActual++;
            if (promedio == null || egresado.getPromedioAcumulado().compareTo(promedio) < 0) {
                promedio = egresado.getPromedioAcumulado();
                puesto = puestoActual;
            }
            egresado.setOrdenMeritoCiclo(puesto);
        }

        Integer cuadroCiclo = Math.max(1, puesto / 10);
        Integer quintoCiclo = Math.max(1, puesto / 5);
        Integer tercioCiclo = Math.max(1, puesto / 3);

//        List<Alumno> alumnos = egresados.stream().map(Egresado::getAlumno).collect(Collectors.toList());
        for (Egresado egresado : egresados) {
            Integer puestoAlumno = egresado.getOrdenMeritoCiclo();
            if (puestoAlumno <= cuadroCiclo) {
                egresado.setCuadroHonorCiclo(puestoAlumno);
                egresado.setQuintoSuperiorCiclo(puestoAlumno);
                egresado.setTercioSuperiorCiclo(puestoAlumno);
            } else if (puestoAlumno <= quintoCiclo) {
                egresado.setQuintoSuperiorCiclo(puestoAlumno);
                egresado.setTercioSuperiorCiclo(puestoAlumno);
            } else if (puestoAlumno <= tercioCiclo) {
                egresado.setTercioSuperiorCiclo(puestoAlumno);
            }
        }

        ControlMeritoEgresado comCiclo = coms.stream().filter(com -> com.getEscalaEnum() == ControlOrdenMeritoEscalaEnum.CICLO).findFirst().get();

        Map<ControlMeritoEgresado, List<Egresado>> alumnosByFacultades = egresados.stream().filter(ac -> ac.getControlMeritoFacultad() != null).collect(Collectors.groupingBy(ac -> ac.getControlMeritoFacultad()));
        Map<ControlMeritoEgresado, List<Egresado>> alumnosByCarreras = egresados.stream().filter(ac -> ac.getControlMeritoCarrera() != null).collect(Collectors.groupingBy(ac -> ac.getControlMeritoCarrera()));

        for (Map.Entry<ControlMeritoEgresado, List<Egresado>> entry : alumnosByFacultades.entrySet()) {
            Collections.sort(entry.getValue(), Comparator.comparing(Egresado::getPromedioAcumulado).reversed());
            puesto = 0;
            puestoActual = 0;
            promedio = null;

            for (Egresado egresado : entry.getValue()) {
                puestoActual++;
                if (promedio == null || egresado.getPromedioAcumulado().compareTo(promedio) < 0) {
                    promedio = egresado.getPromedioAcumulado();
                    puesto = puestoActual;
                }
                egresado.setOrdenMeritoFacultad(puesto);
            }

            Integer cuadro = Math.max(1, puesto / 10);
            Integer quinto = Math.max(1, puesto / 5);
            Integer tercio = Math.max(1, puesto / 3);

            for (Egresado egresado : entry.getValue()) {
                Integer puestoAlumno = egresado.getOrdenMeritoFacultad();
                if (puestoAlumno <= cuadro) {
                    egresado.setCuadroHonorFacultad(puestoAlumno);
                    egresado.setQuintoSuperiorFacultad(puestoAlumno);
                    egresado.setTercioSuperiorFacultad(puestoAlumno);
                } else if (puestoAlumno <= quinto) {
                    egresado.setQuintoSuperiorFacultad(puestoAlumno);
                    egresado.setTercioSuperiorFacultad(puestoAlumno);
                } else if (puestoAlumno <= tercio) {
                    egresado.setTercioSuperiorFacultad(puestoAlumno);
                }
            }

            entry.getKey().setEstado(ControlOrdenMeritoEstadoEnum.CALC);
        }

        for (Map.Entry<ControlMeritoEgresado, List<Egresado>> entry : alumnosByCarreras.entrySet()) {
            Collections.sort(entry.getValue(), Comparator.comparing(Egresado::getPromedioAcumulado).reversed());

            puesto = 0;
            puestoActual = 0;
            promedio = null;
            for (Egresado alumnoCiclo : entry.getValue()) {
                puestoActual++;
                if (promedio == null || alumnoCiclo.getPromedioAcumulado().compareTo(promedio) < 0) {
                    promedio = alumnoCiclo.getPromedioAcumulado();
                    puesto = puestoActual;
                }
                alumnoCiclo.setOrdenMeritoCarrera(puesto);
            }

            Integer cuadro = Math.max(1, puesto / 10);
            Integer quinto = Math.max(1, puesto / 5);
            Integer tercio = Math.max(1, puesto / 3);

            for (Egresado egresado : entry.getValue()) {
                Integer puestoAlumno = egresado.getOrdenMeritoCarrera();
                if (puestoAlumno <= cuadro) {
                    egresado.setCuadroHonorCarrera(puestoAlumno);
                    egresado.setQuintoSuperiorCarrera(puestoAlumno);
                    egresado.setTercioSuperiorCarrera(puestoAlumno);
                } else if (puestoAlumno <= quinto) {
                    egresado.setQuintoSuperiorCarrera(puestoAlumno);
                    egresado.setTercioSuperiorCarrera(puestoAlumno);
                } else if (puestoAlumno <= tercio) {
                    egresado.setTercioSuperiorCarrera(puestoAlumno);
                }
            }

            entry.getKey().setEstado(ControlOrdenMeritoEstadoEnum.CALC);
        }

        comCiclo.setEstado(ControlOrdenMeritoEstadoEnum.CALC);

        for (ControlMeritoEgresado com : coms) {
            controlOrdenMeritoDAO.update(com);
        }

        for (Egresado egresado : egresados) {
            egresadoDAO.update(egresado);
        }
    }

    @Override
    public List<ControlMeritoEgresado> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico) {
        return controlOrdenMeritoDAO.allByDynatableCicloAcademico(filter, cicloAcademico);
    }

    @Override
    public CicloAcademico findCicloAcademico(CicloAcademico cicloAcademico) {
        return cicloAcademicoDAO.find(cicloAcademico);
    }

    @Override
    public List<CicloAcademico> allCicloAcademicoForSelect() {
        List<CicloAcademico> cicloAcademicos = new ArrayList<>();
        CicloAcademico cicloActivo = findCicloActivo();
        List<CicloAcademico> ciclosAnterioes = cicloAcademicoDAO.allAnteriores(150, cicloActivo);//era 10
        cicloAcademicos.add(cicloActivo);
        cicloAcademicos.addAll(ciclosAnterioes);
        return cicloAcademicos;
    }

    @Override
    public CicloAcademico findCicloActivo() {
        ModalidadEstudio me = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        return cicloAcademicoDAO.findActivo(me);
    }

    @Override
    public List<Egresado> allAlumnoCicloByControl(DynatableFilter filter, ControlMeritoEgresado controlOrdenMerito) {
        ControlMeritoEgresado controlBD = controlOrdenMeritoDAO.find(controlOrdenMerito.getId());
        switch (controlBD.getEscalaEnum()) {
            case CICLO:
                return egresadoDAO.allByControlMeritoCiclo(filter, controlBD);
            case ESPE:
                return egresadoDAO.allByControlMeritoCarrera(filter, controlBD);
            case FAC:
                return egresadoDAO.allByControlMeritoFacultad(filter, controlBD);
        }

        return null;
    }

    @Override
    public ControlMeritoEgresado find(Long id) {
        return controlOrdenMeritoDAO.find(id);
    }

    @Override
    public List<Alumno> allAlumnoLikeNombres(String parametro) {
        parametro = "%" + parametro.replaceAll(" ", "%") + "%";
        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        return alumnoDAO.allByNameModalidadEstudioCiclo(parametro, modalidad, new CicloAcademico());
    }

    @Override
    @Transactional
    public void saveEgresado(Egresado egresado, Usuario usuario) {

        Alumno almForm = egresado.getAlumno();
        Egresado egresadoBD = egresadoDAO.findByAlumno(almForm);

        if (egresadoBD != null) {
            throw new PhobosException("El alumno " + almForm.getPersona().getNombreCompleto() + " ya se encuentra registrado como egresado.");
        }

        egresado.setEsPrincipal(0);
        egresado.setUserRegistroEgresado(usuario);
        egresado.setFechaRegistroEgresado(new Date());
        egresado.setCarrera(almForm.getCarrera());
//        egresado.setFechaEgresado(fechaEgresado);

        if (almForm.getCarrera().getFacultad() != null) {
            egresado.setFacultad(almForm.getCarrera().getFacultad());
        }
        egresadoDAO.save(egresado);
    }

    @Override
    public List<Egresado> getEgresadosForPdf(CicloAcademico cicloAcademico) {
        return egresadoDAO.allForPdfByCicloAcademico(cicloAcademico);
    }

    @Override
    public List<Facultad> allFacultadesForReporte() {
        List<Carrera> carreras = carreraDAO.allActivasByModalidadEnum(ModalidadEstudioEnum.PRE);
        Collections.sort(carreras, new Carrera.CompareCodigo());

        Map<Long, Facultad> mapFacultad = TypesUtil.convertListToMap("facultad.id", "facultad", carreras);
        List<Facultad> facultades = new ArrayList(mapFacultad.values());

        Map<Long, List<Carrera>> mapCarreras = TypesUtil.convertListToMapList("facultad.id", carreras);
        for (Map.Entry<Long, List<Carrera>> entry : mapCarreras.entrySet()) {
            Facultad fac = mapFacultad.get(entry.getKey());
            List<Carrera> carrerasFac = entry.getValue();
            fac.setCarrera(carrerasFac);
        }

        List<Facultad> facultadUnica = facultades.stream().filter(fac -> fac.getCarrera().size() == 1).collect(Collectors.toList());
        Collections.sort(facultadUnica, new Facultad.CompareCodigo());
        List<Facultad> noFacultadUnica = facultades.stream().filter(fac -> fac.getCarrera().size() > 1).collect(Collectors.toList());
        Collections.sort(noFacultadUnica, new Facultad.CompareCodigo());
        return facultades;
    }

}
