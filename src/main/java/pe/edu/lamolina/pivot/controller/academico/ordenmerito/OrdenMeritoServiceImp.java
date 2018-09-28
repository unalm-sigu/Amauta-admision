package pe.edu.lamolina.pivot.controller.academico.ordenmerito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ControlOrdenMerito;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.ControlOrdenMeritoEscalaEnum;
import pe.edu.lamolina.model.enums.ControlOrdenMeritoEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.ControlOrdenMeritoDAO;
import pe.edu.lamolina.pivot.dao.academico.FacultadDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class OrdenMeritoServiceImp implements OrdenMeritoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    FacultadDAO facultadDAO;
    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;
    @Autowired
    ControlOrdenMeritoDAO controlOrdenMeritoDAO;
    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;
    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;
    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;
    @Autowired
    CarreraDAO carreraDAO;

    @Override
    @Transactional
    public void generarDatos(CicloAcademico cicloAcademico, DataSessionPivot ds) {
        alumnoCicloDAO.deleteInfoOrdenMeritoByCicloAcademico(cicloAcademico);
        controlOrdenMeritoDAO.deleteByCicloAcademico(cicloAcademico);

        List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allByCicloAcademico(cicloAcademico);

        Date now = new Date();

        ControlOrdenMerito comCiclo = new ControlOrdenMerito();
        comCiclo.setCicloAcademico(cicloAcademico);
        comCiclo.setEscala(ControlOrdenMeritoEscalaEnum.CICLO);
        comCiclo.setEstado(ControlOrdenMeritoEstadoEnum.CRE);
        comCiclo.setAlumnosCompletos(0);
        comCiclo.setAlumnosComputados(0);
        comCiclo.setAlumnosIncompletos(0);
        comCiclo.setNoComputados(0);

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

        List<Facultad> facultadUnica = facultades.stream().filter(fac -> fac.getCarrera().size() == 1).collect(Collectors.toList());
        Collections.sort(facultadUnica, new Facultad.CompareCodigo());
        List<Facultad> noFacultadUnica = facultades.stream().filter(fac -> fac.getCarrera().size() > 1).collect(Collectors.toList());
        Collections.sort(noFacultadUnica, new Facultad.CompareCodigo());

        Map<Facultad, List<AlumnoCiclo>> mapAlumnoByFacultad = alumnoCiclos.stream().filter(ac -> facultadUnica.contains(ac.getCarrera().getFacultad())).collect(Collectors.groupingBy(ac -> ac.getCarrera().getFacultad()));
        Map<Carrera, List<AlumnoCiclo>> mapAlumnosByCarrera = alumnoCiclos.stream().filter(ac -> noFacultadUnica.contains(ac.getCarrera().getFacultad())).collect(Collectors.groupingBy(ac -> ac.getCarrera()));

        Map<Long, List<AlumnoCiclo>> mapAlumnosByIdFac = TypesUtil.convertListToMapList("carrera.facultad.id", alumnoCiclos);
        Map<Long, List<AlumnoCiclo>> mapAlumnosByIdCarr = TypesUtil.convertListToMapList("carrera.id", alumnoCiclos);

        Integer total = 0;
        for (Map.Entry<Facultad, List<AlumnoCiclo>> entry : mapAlumnoByFacultad.entrySet()) {
            total += entry.getValue().size();
        }
        for (Map.Entry<Carrera, List<AlumnoCiclo>> entry : mapAlumnosByCarrera.entrySet()) {
            total += entry.getValue().size();
        }
        comCiclo.setTotalAlumnos(total);
        controlOrdenMeritoDAO.save(comCiclo);

        Integer completosCiclo = 0;
        Integer incompletosCiclo = 0;

        for (Facultad fac : facultadUnica) {
            ControlOrdenMerito com = new ControlOrdenMerito();
            System.out.println("fac ==> " + fac.getCodigo());
            List<AlumnoCiclo> alumnosCiclo = mapAlumnosByIdFac.get(fac.getId());

            com.setFacultad(fac);
            com.setCicloAcademico(cicloAcademico);
            com.setEscala(ControlOrdenMeritoEscalaEnum.FAC);
            com.setEstado(ControlOrdenMeritoEstadoEnum.CRE);
            com.setTotalAlumnos(alumnosCiclo.size());
            com.setAlumnosCompletos(0);
            com.setAlumnosComputados(0);
            com.setAlumnosIncompletos(0);
            com.setNoComputados(0);

            com.setUserRegistro(ds.getUsuario());
            com.setFechaRegistro(now);

            controlOrdenMeritoDAO.save(com);

            Integer completos = 0;
            Integer incompletos = 0;

            for (AlumnoCiclo alumnoCiclo : alumnosCiclo) {
                if (alumnoCiclo.getPromedioAcumulado() != null) {
                    completos++;
                } else {
                    incompletos++;
                }
                alumnoCiclo.setControlMeritoFacultad(com);
                alumnoCiclo.setControlMeritoCiclo(comCiclo);
            }

            completosCiclo += completos;
            incompletosCiclo += incompletos;

            com.setAlumnosComputados(completos);
            com.setNoComputados(incompletos);
            controlOrdenMeritoDAO.update(com);
        }

        for (Facultad fac : noFacultadUnica) {
            for (Carrera carr : fac.getCarrera()) {

                ControlOrdenMerito com = new ControlOrdenMerito();
                System.out.println("esp ==> " + carr.getCodigo());
                List<AlumnoCiclo> alumnosCiclo = mapAlumnosByIdCarr.get(carr.getId());

                com.setCarrera(carr);
                com.setCicloAcademico(cicloAcademico);
                com.setEscala(ControlOrdenMeritoEscalaEnum.ESPE);
                com.setEstado(ControlOrdenMeritoEstadoEnum.CRE);
                com.setTotalAlumnos(alumnosCiclo.size());
                com.setAlumnosCompletos(0);
                com.setAlumnosComputados(0);
                com.setAlumnosIncompletos(0);
                com.setNoComputados(0);

                com.setUserRegistro(ds.getUsuario());
                com.setFechaRegistro(now);

                Integer completos = 0;
                Integer incompletos = 0;
                controlOrdenMeritoDAO.save(com);

                for (AlumnoCiclo alumnoCiclo : alumnosCiclo) {
                    if (alumnoCiclo.getPromedioAcumulado() != null) {
                        completos++;
                    } else {
                        incompletos++;
                    }
                    alumnoCiclo.setControlMeritoCarrera(com);
                    alumnoCiclo.setControlMeritoCiclo(comCiclo);
                }

                completosCiclo += completos;
                incompletosCiclo += incompletos;

                com.setAlumnosComputados(completos);
                com.setNoComputados(incompletos);
                controlOrdenMeritoDAO.update(com);
            }
        }

        comCiclo.setAlumnosComputados(completosCiclo);
        comCiclo.setNoComputados(incompletosCiclo);
        controlOrdenMeritoDAO.update(comCiclo);

        for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
            Integer creditos = alumnoCiclo.getCreditosAprobadosAcumulados() + alumnoCiclo.getCreditosConvalidados();
            alumnoCiclo.setNivel(creditos / 40 + 1);
            alumnoCicloDAO.update(alumnoCiclo);
        }
    }

    @Override
    @Transactional
    public void calcularMeritos(CicloAcademico cicloAcademico, DataSessionPivot ds) {

        List<ControlOrdenMerito> coms = controlOrdenMeritoDAO.allByCicloAcademico(cicloAcademico);

        Date now = new Date();

        for (ControlOrdenMerito com : coms) {
            com.setEstado(ControlOrdenMeritoEstadoEnum.CALC);
            com.setFechaCalculo(now);
            com.setUserCalculo(ds.getUsuario());
        }

        List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allByControlesOrdenMerito(coms);

        Collections.sort(alumnoCiclos, Comparator.comparing(AlumnoCiclo::getPromedioAcumulado).reversed());

        Integer puesto = 0;
        Integer puestoActual = 0;
        BigDecimal promedio = null;
        for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
            puestoActual++;
            if (promedio == null || alumnoCiclo.getPromedioAcumulado().compareTo(promedio) < 0) {
                promedio = alumnoCiclo.getPromedioAcumulado();
                puesto = puestoActual;
            }
            alumnoCiclo.setOrdenMeritoCiclo(puesto);
        }

        Integer cuadroCiclo = Math.max(1, puesto / 10);
        Integer quintoCiclo = Math.max(1, puesto / 5);
        Integer tercioCiclo = Math.max(1, puesto / 3);

        List<Alumno> alumnos = alumnoCiclos.stream().map(AlumnoCiclo::getAlumno).collect(Collectors.toList());
        List<MatriculaResumen> mrs = matriculaResumenDAO.findNotasIncompletas(alumnos, cicloAcademico);
        Set<String> incompletos = mrs.stream().map(mr -> mr.getAlumno().getCodigo()).collect(Collectors.toSet());

        for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
            alumnoCiclo.setFechaModificacion(now);
            alumnoCiclo.setUserModificacion(ds.getUsuario());

            Integer puestoAlumno = alumnoCiclo.getOrdenMeritoCiclo();
            if (puestoAlumno <= cuadroCiclo) {
                alumnoCiclo.setCuadroHonorCiclo(puestoAlumno);
                alumnoCiclo.setQuintoSuperiorCiclo(puestoAlumno);
                alumnoCiclo.setTercioSuperiorCiclo(puestoAlumno);
            } else if (puestoAlumno <= quintoCiclo) {
                alumnoCiclo.setQuintoSuperiorCiclo(puestoAlumno);
                alumnoCiclo.setTercioSuperiorCiclo(puestoAlumno);
            } else if (puestoAlumno <= tercioCiclo) {
                alumnoCiclo.setTercioSuperiorCiclo(puestoAlumno);
            }
        }

        Map<Integer, List<AlumnoCiclo>> alumnosByNivelCiclo = alumnoCiclos.stream().collect(Collectors.groupingBy(AlumnoCiclo::getNivel));
        this.calcularMeritosNivel(alumnosByNivelCiclo, ControlOrdenMeritoEscalaEnum.CICLO);

        ControlOrdenMerito comCiclo = coms.stream().filter(com -> com.getEscalaEnum() == ControlOrdenMeritoEscalaEnum.CICLO).findFirst().get();

        Map<ControlOrdenMerito, List<AlumnoCiclo>> alumnosByFacultades = alumnoCiclos.stream().filter(ac -> ac.getControlMeritoFacultad() != null).collect(Collectors.groupingBy(ac -> ac.getControlMeritoFacultad()));
        Map<ControlOrdenMerito, List<AlumnoCiclo>> alumnosByCarreras = alumnoCiclos.stream().filter(ac -> ac.getControlMeritoCarrera() != null).collect(Collectors.groupingBy(ac -> ac.getControlMeritoCarrera()));

        Integer computadosCiclo = 0;
        Integer noComputadosCiclo = 0;

        for (Map.Entry<ControlOrdenMerito, List<AlumnoCiclo>> entry : alumnosByFacultades.entrySet()) {
            Collections.sort(entry.getValue(), Comparator.comparing(AlumnoCiclo::getPromedioAcumulado).reversed());

            Map<Integer, List<AlumnoCiclo>> alumnosByNivel = entry.getValue().stream().collect(Collectors.groupingBy(AlumnoCiclo::getNivel));
            this.calcularMeritosNivel(alumnosByNivel, ControlOrdenMeritoEscalaEnum.FAC);
            puesto = 0;
            puestoActual = 0;
            promedio = null;

            Integer computados = 0;
            Integer noComputados = 0;

            for (AlumnoCiclo alumnoCiclo : entry.getValue()) {
                puestoActual++;
                if (promedio == null || alumnoCiclo.getPromedioAcumulado().compareTo(promedio) < 0) {
                    promedio = alumnoCiclo.getPromedioAcumulado();
                    puesto = puestoActual;
                }
                if (incompletos.contains(alumnoCiclo.getAlumno().getCodigo())) {
                    noComputados++;
                } else {
                    computados++;
                }
                alumnoCiclo.setOrdenMeritoFacultad(puesto);
            }

            Integer cuadro = Math.max(1, puesto / 10);
            Integer quinto = Math.max(1, puesto / 5);
            Integer tercio = Math.max(1, puesto / 3);

            for (AlumnoCiclo alumnoCiclo : entry.getValue()) {
                Integer puestoAlumno = alumnoCiclo.getOrdenMeritoFacultad();
                if (puestoAlumno <= cuadro) {
                    alumnoCiclo.setCuadroHonorFacultad(puestoAlumno);
                    alumnoCiclo.setQuintoSuperiorFacultad(puestoAlumno);
                    alumnoCiclo.setTercioSuperiorFacultad(puestoAlumno);
                } else if (puestoAlumno <= quinto) {
                    alumnoCiclo.setQuintoSuperiorFacultad(puestoAlumno);
                    alumnoCiclo.setTercioSuperiorFacultad(puestoAlumno);
                } else if (puestoAlumno <= tercio) {
                    alumnoCiclo.setTercioSuperiorFacultad(puestoAlumno);
                }
            }
            entry.getKey().setAlumnosCompletos(computados);
            entry.getKey().setAlumnosIncompletos(noComputados);

            computadosCiclo += computados;
            noComputadosCiclo += noComputados;

            if (noComputados == 0) {
                entry.getKey().setEstado(ControlOrdenMeritoEstadoEnum.CER);
            } else {
                entry.getKey().setEstado(ControlOrdenMeritoEstadoEnum.CALC);
            }
        }

        for (Map.Entry<ControlOrdenMerito, List<AlumnoCiclo>> entry : alumnosByCarreras.entrySet()) {
            Collections.sort(entry.getValue(), Comparator.comparing(AlumnoCiclo::getPromedioAcumulado).reversed());

            Map<Integer, List<AlumnoCiclo>> alumnosByNivel = entry.getValue().stream().collect(Collectors.groupingBy(AlumnoCiclo::getNivel));
            this.calcularMeritosNivel(alumnosByNivel, ControlOrdenMeritoEscalaEnum.ESPE);

            puesto = 0;
            puestoActual = 0;
            promedio = null;
            Integer computados = 0;
            Integer noComputados = 0;
            for (AlumnoCiclo alumnoCiclo : entry.getValue()) {
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

            for (AlumnoCiclo alumnoCiclo : entry.getValue()) {
                Integer puestoAlumno = alumnoCiclo.getOrdenMeritoCarrera();
                if (puestoAlumno <= cuadro) {
                    alumnoCiclo.setCuadroHonorCarrera(puestoAlumno);
                    alumnoCiclo.setQuintoSuperiorCarrera(puestoAlumno);
                    alumnoCiclo.setTercioSuperiorCarrera(puestoAlumno);
                } else if (puestoAlumno <= quinto) {
                    alumnoCiclo.setQuintoSuperiorCarrera(puestoAlumno);
                    alumnoCiclo.setTercioSuperiorCarrera(puestoAlumno);
                } else if (puestoAlumno <= tercio) {
                    alumnoCiclo.setTercioSuperiorCarrera(puestoAlumno);
                }
                if (incompletos.contains(alumnoCiclo.getAlumno().getCodigo())) {
                    noComputados++;
                } else {
                    computados++;
                }
            }

            computadosCiclo += computados;
            noComputadosCiclo += noComputados;

            entry.getKey().setAlumnosCompletos(computados);
            entry.getKey().setAlumnosIncompletos(noComputados);

            if (noComputados == 0) {
                entry.getKey().setEstado(ControlOrdenMeritoEstadoEnum.CER);
            } else {
                entry.getKey().setEstado(ControlOrdenMeritoEstadoEnum.CALC);
            }
        }

        comCiclo.setAlumnosCompletos(computadosCiclo);
        comCiclo.setAlumnosIncompletos(noComputadosCiclo);
        if (noComputadosCiclo == 0) {
            comCiclo.setEstado(ControlOrdenMeritoEstadoEnum.CER);
        } else {
            comCiclo.setEstado(ControlOrdenMeritoEstadoEnum.CALC);
        }

        for (ControlOrdenMerito com : coms) {
            controlOrdenMeritoDAO.update(com);
        }

        for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
            alumnoCicloDAO.update(alumnoCiclo);
        }
    }

    private void calcularMeritosNivel(Map<Integer, List<AlumnoCiclo>> map, ControlOrdenMeritoEscalaEnum escala) {
        for (Map.Entry<Integer, List<AlumnoCiclo>> entry : map.entrySet()) {
            Integer puesto = 0;
            Integer puestoActual = 0;
            BigDecimal promedio = null;

            List<AlumnoCiclo> as = entry.getValue();
            as.sort(Comparator.comparing(AlumnoCiclo::getPromedioAcumulado).reversed());
            for (AlumnoCiclo alumnoCiclo : as) {
                puestoActual++;
                if (promedio == null || alumnoCiclo.getPromedioAcumulado().compareTo(promedio) < 0) {
                    promedio = alumnoCiclo.getPromedioAcumulado();
                    puesto = puestoActual;
                }
                switch (escala) {
                    case CICLO:
                        alumnoCiclo.setOrdenMeritoCicloNivel(puesto);
                        break;
                    case ESPE:
                        alumnoCiclo.setOrdenMeritoCarreraNivel(puesto);
                        break;
                    case FAC:
                        alumnoCiclo.setOrdenMeritoFacultadNivel(puesto);
                        break;
                    default:
                        throw new AssertionError();
                }
            }

            Integer cuadro = Math.max(1, puesto / 10);
            Integer quinto = Math.max(1, puesto / 5);
            Integer tercio = Math.max(1, puesto / 3);

            for (AlumnoCiclo alumnoCiclo : as) {
                Integer puestoAlumno;
                switch (escala) {
                    case CICLO:
                        puestoAlumno = alumnoCiclo.getOrdenMeritoCicloNivel();
                        break;
                    case ESPE:
                        puestoAlumno = alumnoCiclo.getOrdenMeritoCarreraNivel();
                        break;
                    case FAC:
                        puestoAlumno = alumnoCiclo.getOrdenMeritoFacultadNivel();
                        break;
                    default:
                        throw new AssertionError();
                }
                if (puestoAlumno <= cuadro) {
                    switch (escala) {
                        case CICLO:
                            alumnoCiclo.setCuadroHonorCicloNivel(puestoAlumno);
                            alumnoCiclo.setQuintoSuperiorCicloNivel(puestoAlumno);
                            alumnoCiclo.setTercioSuperiorCicloNivel(puestoAlumno);
                            break;
                        case ESPE:
                            alumnoCiclo.setCuadroHonorCarreraNivel(puestoAlumno);
                            alumnoCiclo.setQuintoSuperiorCarreraNivel(puestoAlumno);
                            alumnoCiclo.setTercioSuperiorCarreraNivel(puestoAlumno);
                            break;
                        case FAC:
                            alumnoCiclo.setCuadroHonorFacultadNivel(puestoAlumno);
                            alumnoCiclo.setQuintoSuperiorFacultadNivel(puestoAlumno);
                            alumnoCiclo.setTercioSuperiorFacultadNivel(puestoAlumno);
                            break;
                        default:
                            throw new AssertionError();
                    }

                } else if (puestoAlumno <= quinto) {
                    switch (escala) {
                        case CICLO:
                            alumnoCiclo.setQuintoSuperiorCicloNivel(puestoAlumno);
                            alumnoCiclo.setTercioSuperiorCicloNivel(puestoAlumno);
                            break;
                        case ESPE:
                            alumnoCiclo.setQuintoSuperiorCarreraNivel(puestoAlumno);
                            alumnoCiclo.setTercioSuperiorCarreraNivel(puestoAlumno);
                            break;
                        case FAC:
                            alumnoCiclo.setQuintoSuperiorFacultadNivel(puestoAlumno);
                            alumnoCiclo.setTercioSuperiorFacultadNivel(puestoAlumno);
                            break;
                        default:
                            throw new AssertionError();
                    }
                } else if (puestoAlumno <= tercio) {
                    switch (escala) {
                        case CICLO:
                            alumnoCiclo.setTercioSuperiorCicloNivel(puestoAlumno);
                            break;
                        case ESPE:
                            alumnoCiclo.setTercioSuperiorCarreraNivel(puestoAlumno);
                            break;
                        case FAC:
                            alumnoCiclo.setTercioSuperiorFacultadNivel(puestoAlumno);
                            break;
                        default:
                            throw new AssertionError();
                    }
                }
            }
        }

    }

    @Override
    public List<ControlOrdenMerito> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico) {
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
        List<CicloAcademico> ciclosAnterioes = cicloAcademicoDAO.allAnteriores(10, cicloActivo);
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
    public List<AlumnoCiclo> allAlumnoCicloByControl(DynatableFilter filter, ControlOrdenMerito controlOrdenMerito) {
        ControlOrdenMerito controlBD = controlOrdenMeritoDAO.find(controlOrdenMerito.getId());
        switch (controlBD.getEscalaEnum()) {
            case CICLO:
                return alumnoCicloDAO.allByControlMeritoCiclo(filter, controlBD);
            case ESPE:
                return alumnoCicloDAO.allByControlMeritoCarrera(filter, controlBD);
            case FAC:
                return alumnoCicloDAO.allByControlMeritoFacultad(filter, controlBD);
        }

        return null;
    }

    @Override
    public ControlOrdenMerito find(Long id) {
        return controlOrdenMeritoDAO.find(id);
    }

    @Override
    public List<AlumnoCiclo> allAlumnoCicloByControlNivel(DynatableFilter filter, ControlOrdenMerito controlOrdenMerito, Integer nivel) {
        if (nivel == 0) {
            return allAlumnoCicloByControl(filter, controlOrdenMerito);
        } else {
            ControlOrdenMerito controlBD = controlOrdenMeritoDAO.find(controlOrdenMerito.getId());

            switch (controlBD.getEscalaEnum()) {
                case CICLO:
                    return alumnoCicloDAO.allByControlMeritoCicloNivel(filter, controlBD, nivel);
                case ESPE:
                    return alumnoCicloDAO.allByControlMeritoCarreraNivel(filter, controlBD, nivel);
                case FAC:
                    return alumnoCicloDAO.allByControlMeritoFacultadNivel(filter, controlBD, nivel);
            }
        }
        return null;
    }

}
