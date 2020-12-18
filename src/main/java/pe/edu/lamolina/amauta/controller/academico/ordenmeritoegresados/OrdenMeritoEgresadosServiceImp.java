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
        egresadoDAO.deleteInfoOrdenMeritoByCicloAcademico(cicloAcademico);
        controlOrdenMeritoDAO.deleteByCicloAcademico(cicloAcademico);

        List<Egresado> egresadosDB = egresadoDAO.allByCicloAcademico(cicloAcademico);

        Date now = new Date();

        ControlMeritoEgresado comCiclo = new ControlMeritoEgresado();
        comCiclo.setCicloAcademico(cicloAcademico);
        comCiclo.setEscala(ControlOrdenMeritoEscalaEnum.CICLO);
        comCiclo.setEstado(ControlOrdenMeritoEstadoEnum.CRE);

        comCiclo.setUserRegistro(ds.getUsuario());
        comCiclo.setFechaRegistro(now);

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

//        List<Facultad> facultadUnica = facultades.stream().filter(fac -> fac.getCarrera().size() == 1).collect(Collectors.toList());
        List<Facultad> facultadUnica = facultades.stream().collect(Collectors.toList());
        Collections.sort(facultadUnica, new Facultad.CompareCodigo());
        List<Facultad> noFacultadUnica = facultades.stream().filter(fac -> fac.getCarrera().size() > 1).collect(Collectors.toList());
        Collections.sort(noFacultadUnica, new Facultad.CompareCodigo());

        Map<Facultad, List<Egresado>> mapAlumnoByFacultad = egresadosDB.stream().filter(ac -> facultadUnica.contains(ac.getCarrera().getFacultad())).collect(Collectors.groupingBy(ac -> ac.getCarrera().getFacultad()));
        Map<Carrera, List<Egresado>> mapAlumnosByCarrera = egresadosDB.stream().filter(ac -> noFacultadUnica.contains(ac.getCarrera().getFacultad())).collect(Collectors.groupingBy(ac -> ac.getCarrera()));

        Map<Long, List<Egresado>> mapAlumnosByIdFac = TypesUtil.convertListToMapList("carrera.facultad.id", egresadosDB);
        Map<Long, List<Egresado>> mapAlumnosByIdCarr = TypesUtil.convertListToMapList("carrera.id", egresadosDB);

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
        comCiclo.setTotalAlumnos(total);
        controlOrdenMeritoDAO.save(comCiclo);

        for (Facultad fac : facultadUnica) {
            ControlMeritoEgresado com = new ControlMeritoEgresado();
            List<Egresado> egresados = mapAlumnosByIdFac.get(fac.getId());

            if (egresados == null) {
                egresados = new ArrayList<>();
            }

            com.setFacultad(fac);
            com.setCicloAcademico(cicloAcademico);
            com.setEscala(ControlOrdenMeritoEscalaEnum.FAC);
            com.setEstado(ControlOrdenMeritoEstadoEnum.CRE);
            com.setTotalAlumnos(egresados.size());

            com.setUserRegistro(ds.getUsuario());
            com.setFechaRegistro(now);

            controlOrdenMeritoDAO.save(com);

            for (Egresado egresado : egresados) {
                egresado.setControlMeritoFacultad(com);
                egresado.setControlMeritoCiclo(comCiclo);
            }

            controlOrdenMeritoDAO.update(com);
        }

        for (Facultad fac : noFacultadUnica) {
            for (Carrera carr : fac.getCarrera()) {

                ControlMeritoEgresado com = new ControlMeritoEgresado();
                System.out.println("esp ==> " + carr.getCodigo());
                List<Egresado> alumnosCiclo = mapAlumnosByIdCarr.get(carr.getId());

                if (alumnosCiclo == null) {
                    alumnosCiclo = new ArrayList<>();
                }

                com.setCarrera(carr);
                com.setCicloAcademico(cicloAcademico);
                com.setEscala(ControlOrdenMeritoEscalaEnum.ESPE);
                com.setEstado(ControlOrdenMeritoEstadoEnum.CRE);
                com.setTotalAlumnos(alumnosCiclo.size());
                com.setUserRegistro(ds.getUsuario());
                com.setFechaRegistro(now);
                if (alumnosCiclo.isEmpty()) {
                    continue;
                }
                controlOrdenMeritoDAO.save(com);

                for (Egresado alumnoCiclo : alumnosCiclo) {
                    alumnoCiclo.setControlMeritoCarrera(com);
                    alumnoCiclo.setControlMeritoCiclo(comCiclo);
                }

                controlOrdenMeritoDAO.update(com);
            }
        }

        controlOrdenMeritoDAO.update(comCiclo);

        for (Egresado egresado : egresadosDB) {
            BigDecimal promedio = egresado.getAlumno().getPromedioAcumulado();
            promedio = promedio.setScale(2, RoundingMode.HALF_UP);
            egresado.setPromedioAcumulado(promedio);
            egresado.setCreditosAcumulados(mapAlumnosCiclo.get(egresado.getAlumno().getId()).getCreditosAcumulados());
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
