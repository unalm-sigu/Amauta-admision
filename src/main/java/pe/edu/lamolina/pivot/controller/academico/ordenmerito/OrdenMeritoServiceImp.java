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
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ControlOrdenMerito;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum;
import pe.edu.lamolina.model.enums.ControlOrdenMeritoEscalaEnum;
import pe.edu.lamolina.model.enums.ControlOrdenMeritoEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
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
        comCiclo.setTotalAlumnos(alumnoCiclos.size());

        comCiclo.setUserRegistro(ds.getUsuario());
        comCiclo.setFechaRegistro(now);

        controlOrdenMeritoDAO.save(comCiclo);

        List<Facultad> facultades = facultadDAO.allNormal();
        Set<Facultad> facultadUnica = facultades.stream().filter(fac -> fac.getCarrera().size() == 1).collect(Collectors.toSet());

        Map<Facultad, List<AlumnoCiclo>> alumnosByFacultades = alumnoCiclos.stream().filter(ac -> facultadUnica.contains(ac.getCarrera().getFacultad())).collect(Collectors.groupingBy(ac -> ac.getCarrera().getFacultad()));
        Map<Carrera, List<AlumnoCiclo>> alumnosByCarreras = alumnoCiclos.stream().filter(ac -> !facultadUnica.contains(ac.getCarrera().getFacultad())).collect(Collectors.groupingBy(ac -> ac.getCarrera()));

        Integer completosCiclo = 0;
        Integer incompletosCiclo = 0;

        for (Map.Entry<Facultad, List<AlumnoCiclo>> entry : alumnosByFacultades.entrySet()) {
            ControlOrdenMerito com = new ControlOrdenMerito();
            com.setFacultad(entry.getKey());
            com.setCicloAcademico(cicloAcademico);
            com.setEscala(ControlOrdenMeritoEscalaEnum.FAC);
            com.setEstado(ControlOrdenMeritoEstadoEnum.CRE);
            com.setTotalAlumnos(entry.getValue().size());

            com.setUserRegistro(ds.getUsuario());
            com.setFechaRegistro(now);

            controlOrdenMeritoDAO.save(com);

            Integer completos = 0;
            Integer incompletos = 0;

            for (AlumnoCiclo alumnoCiclo : entry.getValue()) {
                if (alumnoCiclo.getPromedioCiclo() != null) {
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

        for (Map.Entry<Carrera, List<AlumnoCiclo>> entry : alumnosByCarreras.entrySet()) {
            ControlOrdenMerito com = new ControlOrdenMerito();
            com.setCarrera(entry.getKey());
            com.setCicloAcademico(cicloAcademico);
            com.setEscala(ControlOrdenMeritoEscalaEnum.ESPE);
            com.setEstado(ControlOrdenMeritoEstadoEnum.CRE);
            com.setTotalAlumnos(entry.getValue().size());

            com.setUserRegistro(ds.getUsuario());
            com.setFechaRegistro(now);

            Integer completos = 0;
            Integer incompletos = 0;
            controlOrdenMeritoDAO.save(com);

            for (AlumnoCiclo alumnoCiclo : entry.getValue()) {
                if (alumnoCiclo.getPromedioCiclo() != null) {
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

        comCiclo.setAlumnosComputados(completosCiclo);
        comCiclo.setNoComputados(incompletosCiclo);
        controlOrdenMeritoDAO.update(comCiclo);

        for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
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

        Collections.sort(alumnoCiclos, Comparator.comparing(AlumnoCiclo::getPromedioCiclo).reversed());
        Integer puesto = 0;
        BigDecimal promedio = null;
        for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
            if (promedio == null || alumnoCiclo.getPromedioCiclo().compareTo(promedio) < 0) {
                promedio = alumnoCiclo.getPromedioCiclo();
                puesto++;
            }
            alumnoCiclo.setOrdenMeritoCiclo(puesto);
        }

        Integer cuadroCiclo = puesto / 10;
        Integer tercioCiclo = puesto / 3;

        List<Alumno> alumnos = alumnoCiclos.stream().map(AlumnoCiclo::getAlumno).collect(Collectors.toList());
        List<MatriculaResumen> mrs = matriculaResumenDAO.findNotasIncompletas(alumnos, cicloAcademico);
        Set<String> incompletos = mrs.stream().map(mr -> mr.getAlumno().getCodigo()).collect(Collectors.toSet());

        for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
            alumnoCiclo.setFechaModificacion(now);
            alumnoCiclo.setUserModificacion(ds.getUsuario());

            Integer puestoAlumno = alumnoCiclo.getOrdenMeritoCiclo();
            if (puestoAlumno <= cuadroCiclo) {
                alumnoCiclo.setCuadroHonorCiclo(puestoAlumno);
            } else if (alumnoCiclo.getOrdenMeritoCiclo() <= tercioCiclo) {
                alumnoCiclo.setTercioSuperiorCiclo(puestoAlumno);
            }
        }

        ControlOrdenMerito comCiclo = coms.stream().filter(com -> com.getEscalaEnum() == ControlOrdenMeritoEscalaEnum.CICLO).findFirst().get();

        Map<ControlOrdenMerito, List<AlumnoCiclo>> alumnosByFacultades = alumnoCiclos.stream().filter(ac -> ac.getControlMeritoFacultad() != null).collect(Collectors.groupingBy(ac -> ac.getControlMeritoFacultad()));
        Map<ControlOrdenMerito, List<AlumnoCiclo>> alumnosByCarreras = alumnoCiclos.stream().filter(ac -> ac.getControlMeritoCarrera() != null).collect(Collectors.groupingBy(ac -> ac.getControlMeritoCarrera()));

        for (Map.Entry<ControlOrdenMerito, List<AlumnoCiclo>> entry : alumnosByFacultades.entrySet()) {
            Collections.sort(entry.getValue(), Comparator.comparing(AlumnoCiclo::getPromedioCiclo).reversed());
            puesto = 0;
            promedio = null;

            Integer computados = 0;
            Integer noComputados = 0;

            for (AlumnoCiclo alumnoCiclo : entry.getValue()) {
                if (promedio == null || alumnoCiclo.getPromedioCiclo().compareTo(promedio) < 0) {
                    promedio = alumnoCiclo.getPromedioCiclo();
                    puesto++;
                }
                if (incompletos.contains(alumnoCiclo.getAlumno().getCodigo())) {
                    noComputados++;
                } else {
                    computados++;
                }
                alumnoCiclo.setOrdenMeritoFacultad(puesto);
            }

            Integer cuadro = puesto / 10;
            Integer tercio = puesto / 3;

            for (AlumnoCiclo alumnoCiclo : entry.getValue()) {
                Integer puestoAlumno = alumnoCiclo.getOrdenMeritoFacultad();
                if (puestoAlumno <= cuadro) {
                    alumnoCiclo.setCuadroHonorFacultad(puestoAlumno);
                } else if (alumnoCiclo.getOrdenMeritoCiclo() <= tercio) {
                    alumnoCiclo.setTercioSuperiorFacultad(puestoAlumno);
                }
            }
            entry.getKey().setAlumnosCompletos(computados);
            entry.getKey().setAlumnosIncompletos(noComputados);
            if (noComputados == 0) {
                entry.getKey().setEstado(ControlOrdenMeritoEstadoEnum.CER);
            } else {
                entry.getKey().setEstado(ControlOrdenMeritoEstadoEnum.CALC);
            }
        }

        Integer computadosCiclo = 0;
        Integer noComputadosCiclo = 0;

        for (Map.Entry<ControlOrdenMerito, List<AlumnoCiclo>> entry : alumnosByCarreras.entrySet()) {
            Collections.sort(entry.getValue(), Comparator.comparing(AlumnoCiclo::getPromedioCiclo).reversed());
            puesto = 0;
            promedio = null;
            Integer computados = 0;
            Integer noComputados = 0;
            for (AlumnoCiclo alumnoCiclo : entry.getValue()) {
                if (promedio == null || alumnoCiclo.getPromedioCiclo().compareTo(promedio) < 0) {
                    promedio = alumnoCiclo.getPromedioCiclo();
                    puesto++;
                }
                alumnoCiclo.setOrdenMeritoCarrera(puesto);
            }

            Integer cuadro = puesto / 10;
            Integer tercio = puesto / 3;

            for (AlumnoCiclo alumnoCiclo : entry.getValue()) {
                Integer puestoAlumno = alumnoCiclo.getOrdenMeritoCarrera();
                if (puestoAlumno <= cuadro) {
                    alumnoCiclo.setCuadroHonorCarrera(puestoAlumno);
                } else if (alumnoCiclo.getOrdenMeritoCiclo() <= tercio) {
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

}
