package pe.edu.lamolina.pivot.controller.posgrado.cuotasalumno;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.FrenchMethod;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.AlumnoCuotaEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.posgrado.AlumnoConceptoMatricula;
import pe.edu.lamolina.model.posgrado.AlumnoCuotaMatricula;
import pe.edu.lamolina.model.posgrado.AlumnoResumenCuotas;
import pe.edu.lamolina.model.posgrado.ConceptoPosgrado;
import pe.edu.lamolina.model.posgrado.TarifaCarrera;
import pe.edu.lamolina.model.posgrado.TarifaConcepto;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.posgrado.AlumnoConceptoMatriculaDAO;
import pe.edu.lamolina.pivot.dao.posgrado.AlumnoCuotaMatriculaDAO;
import pe.edu.lamolina.pivot.dao.posgrado.AlumnoResumenCuotasDAO;
import pe.edu.lamolina.pivot.dao.posgrado.ConceptoPosgradoDAO;
import pe.edu.lamolina.pivot.dao.posgrado.TarifaCarreraDAO;
import pe.edu.lamolina.pivot.dao.posgrado.TarifaConceptoDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class CuotasAlumnoServiceImp implements CuotasAlumnoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    TarifaCarreraDAO tarifaCarreraDAO;

    @Autowired
    ConceptoPosgradoDAO conceptoPosgradoDAO;

    @Autowired
    TarifaConceptoDAO tarifaConceptoDAO;

    @Autowired
    AlumnoResumenCuotasDAO alumnoResumenCuotasDAO;

    @Autowired
    AlumnoConceptoMatriculaDAO alumnoConceptoMatriculaDAO;

    @Autowired
    AlumnoCuotaMatriculaDAO alumnoCuotaMatriculaDAO;

    @Override
    public List<Alumno> allAlumnosPosgrado(DynatableFilter filter, CicloAcademico cicloAcademico) {
        List<String> modalidadesEstudios = new ArrayList<>();
        modalidadesEstudios.add(ModalidadEstudioEnum.EPG.name());
        modalidadesEstudios.add(ModalidadEstudioEnum.ESP.name());
        return alumnoDAO.allByModalidadesDynatable(filter, cicloAcademico, modalidadesEstudios);
    }

    @Override
    public AlumnoResumenCuotas findAlumnoResumenCuotaByAlumnoAndCiclo(Alumno alumno, CicloAcademico cicloAcademico) {
        AlumnoResumenCuotas alumnoResumenCuotas = alumnoResumenCuotasDAO.findByAlumnoAndCiclo(alumno, cicloAcademico);
        if (alumnoResumenCuotas != null) {
            List<AlumnoCuotaMatricula> alumnoCuotasMatricula = alumnoCuotaMatriculaDAO.allAlumnoResumenCuotas(alumnoResumenCuotas);
            List<AlumnoConceptoMatricula> alumnoConceptoMatricula = alumnoConceptoMatriculaDAO.allAlumnoResumenCuotas(alumnoResumenCuotas);
            alumnoResumenCuotas.setAlumnoConceptosMatricula(alumnoConceptoMatricula);
            alumnoResumenCuotas.setAlumnoCuotasMatricula(alumnoCuotasMatricula);
        }
        return alumnoResumenCuotas;
    }

    @Override
    public List<TarifaCarrera> allByCarrera(Carrera carrera) {
        List<TarifaCarrera> tarifasCarrera = tarifaCarreraDAO.allByCarrera(carrera);
        for (TarifaCarrera tarifaCarrera : tarifasCarrera) {
            List<TarifaConcepto> tarifasConcepto = tarifaConceptoDAO.allByTarifaCarrera(tarifaCarrera);
            Collections.sort(tarifasConcepto, (p1, p2) -> p1.getFraccionable().compareTo(p2.getFraccionable()));
            tarifaCarrera.setTarifasConcepto(tarifasConcepto);
        }
        return tarifasCarrera;
    }

    @Override
    public Alumno findAlumno(Alumno alumno) {
        return alumnoDAO.find(alumno);
    }

    @Override
    public TarifaConcepto findTarifaConceptoByConceptoPosgrado(ConceptoPosgrado conceptoPosgrado) {
        return tarifaConceptoDAO.findByConceptoPosgrado(conceptoPosgrado);
    }

    @Override
    public TarifaCarrera findTarifaCarrera(Long id) {
        return tarifaCarreraDAO.find(id);
    }

    @Override
    public AlumnoResumenCuotas generarCuotasAlumno(AlumnoResumenCuotas alumnoResumenCuotas, DataSessionPivot ds) {
        DateTime today = new DateTime();

        List<TarifaConcepto> tarifasConcepto = tarifaConceptoDAO.allByTarifaCarrera(alumnoResumenCuotas.getTarifaCarrera());
        Collections.sort(tarifasConcepto, (p1, p2) -> p1.getFraccionable().compareTo(p2.getFraccionable()));

        List<AlumnoConceptoMatricula> alumnosConceptoMatriculas = new ArrayList<>();
        List<AlumnoCuotaMatricula> alumnosCuotasMatricula = new ArrayList<>();
        AlumnoConceptoMatricula alumnoConceptoMatriculaFracionado = null;

        TarifaCarrera tarifaCarrera = tarifaCarreraDAO.find(alumnoResumenCuotas.getTarifaCarrera().getId());
        alumnoResumenCuotas.setCreditosExceso(BigDecimal.ZERO.intValue());
        if (alumnoResumenCuotas.getEsCreditosExcedido()) {
            alumnoResumenCuotas.setCreditosExceso(alumnoResumenCuotas.getCreditosMaximo() - tarifaCarrera.getCreditosMaximo());
        }

        for (TarifaConcepto tarifaConcepto : tarifasConcepto) {
            logger.debug("Fraccionable {}", tarifaConcepto.getFraccionable());
            AlumnoConceptoMatricula alumnoConceptoMatricula = new AlumnoConceptoMatricula();
            alumnoConceptoMatricula.setAlumnoResumenCuotas(alumnoResumenCuotas);
            alumnoConceptoMatricula.setConceptoPosgrado(tarifaConcepto.getConceptoPosgrado());
            alumnoConceptoMatricula.setCuotas(!tarifaConcepto.getFraccionable() ? 1 : alumnoResumenCuotas.getCuotas());
            //   alumnoConceptoMatricula.setDescuento(BigDecimal.ZERO);
            alumnoConceptoMatricula.setFechaRegistro(today.toDate());
            //   alumnoConceptoMatricula.setFraccionado(tarifaConcepto.getFraccionable() ? 1 : 0);
            alumnoConceptoMatricula.setUserRegistro(ds.getUsuario());
            alumnoConceptoMatricula.setInicial(BigDecimal.ONE);
            alumnoConceptoMatricula.setMonto(tarifaConcepto.getMonto());
            alumnoConceptoMatricula.setDescuento(BigDecimal.ZERO);

            alumnoConceptoMatricula.setInicial(tarifaConcepto.getMontoMinimoInicial());
            alumnoConceptoMatricula.setFraccionado(BigDecimal.ZERO);

            if (tarifaConcepto.getFraccionable()) {
                if (alumnoResumenCuotas.getPorcentajeMontoInicial().compareTo(BigDecimal.valueOf(100)) == 0) {
                    alumnoResumenCuotas.setPagoCash(Boolean.TRUE);
                }
                if (alumnoResumenCuotas.getEsCreditosMinimo()) {
                    alumnoConceptoMatricula.setMonto(tarifaCarrera.getCostoCreditoMinimo().multiply(new BigDecimal(alumnoResumenCuotas.getCreditosMaximo())));
                } else if (alumnoResumenCuotas.getEsCreditosExcedido()) {
                    alumnoConceptoMatricula.setMonto(tarifaConcepto.getMonto());
                    alumnoConceptoMatricula.setMonto(
                            alumnoConceptoMatricula.getMonto().add(
                                    tarifaCarrera.getCostoCreditoExceso().multiply(new BigDecimal(alumnoResumenCuotas.getCreditosMaximo()))
                            )
                    );
                }
                if (alumnoResumenCuotas.getPagoCash()) {
                    BigDecimal descuentoCashDecimal = tarifaCarrera.getDescuentoCash().divide(BigDecimal.valueOf(100));
                    alumnoConceptoMatricula.setDescuento(alumnoConceptoMatricula.getMonto().multiply(descuentoCashDecimal));
                    alumnoConceptoMatricula.setMonto(
                            alumnoConceptoMatricula.getMonto().subtract(alumnoConceptoMatricula.getDescuento())
                    );
                }

                BigDecimal porcentajeInicialDecimal = alumnoResumenCuotas.getPorcentajeMontoInicial().divide(BigDecimal.valueOf(100));
                BigDecimal montoInicial = alumnoConceptoMatricula.getMonto().multiply(porcentajeInicialDecimal);
                BigDecimal montoRestante = alumnoConceptoMatricula.getMonto().subtract(montoInicial);

                alumnoConceptoMatricula.setInicial(montoInicial);
                alumnoConceptoMatricula.setFraccionado(montoRestante);
                alumnoConceptoMatricula.setCuotas(alumnoResumenCuotas.getCuotas());

                alumnoConceptoMatriculaFracionado = (AlumnoConceptoMatricula) alumnoConceptoMatricula.clone();
            }
            alumnosConceptoMatriculas.add(alumnoConceptoMatricula);
        }

        if (!alumnoResumenCuotas.getPagoCash()) {
            BigDecimal tasaDecimal = tarifaCarrera.getTasaInteres().divide(BigDecimal.valueOf(100));
            FrenchMethod fm = new FrenchMethod(alumnoConceptoMatriculaFracionado.getFraccionado(), tasaDecimal, alumnoResumenCuotas.getCuotas(), 2);

            for (int i = 1; i <= alumnoResumenCuotas.getCuotas(); i++) {
                AlumnoCuotaMatricula alumnoCuotaMatricula = new AlumnoCuotaMatricula();
                alumnoCuotaMatricula.setAmortizado(BigDecimal.ZERO);
                alumnoCuotaMatricula.setEstado(AlumnoCuotaEstadoEnum.PEN);
                alumnoCuotaMatricula.setFechaEmision(today.toDate());
                alumnoCuotaMatricula.setFechaPago(today.toDate());

                alumnoCuotaMatricula.setInteres(BigDecimal.ZERO);
                alumnoCuotaMatricula.setMontoBase(BigDecimal.ONE);
                alumnoCuotaMatricula.setMora(BigDecimal.ZERO);
                alumnoCuotaMatricula.setNumeroCuota(i);
                alumnoCuotaMatricula.setSaldo(BigDecimal.ZERO);

                alumnoCuotaMatricula.setMontoCuota(fm.getCuotas().get(i - 1));
                alumnoCuotaMatricula.setSaldo(fm.getCapitales().get(i - 1));
                alumnoCuotaMatricula.setAmortizado(fm.getAmortizaciones().get(i - 1));
                alumnoCuotaMatricula.setInteres(fm.getIntereses().get(i - 1));

                alumnosCuotasMatricula.add(alumnoCuotaMatricula);
            }
        }
        alumnoResumenCuotas.setAlumnoConceptosMatricula(alumnosConceptoMatriculas);
        alumnoResumenCuotas.setAlumnoCuotasMatricula(alumnosCuotasMatricula);

        return alumnoResumenCuotas;
    }

    @Override
    @Transactional
    public void grabarCuotasAlumno(AlumnoResumenCuotas alumnoResumenCuotas, DataSessionPivot ds) {
        DateTime today = new DateTime();
        alumnoResumenCuotas.setUserRegistro(ds.getUsuario());
        alumnoResumenCuotas.setFechaRegistro(today.toDate());
        alumnoResumenCuotas.setCicloAcademico(ds.getCicloAcademico());

        for (AlumnoConceptoMatricula alumnoConceptoMatricula : alumnoResumenCuotas.getAlumnoConceptosMatricula()) {
            alumnoConceptoMatricula.setAlumnoResumenCuotas(alumnoResumenCuotas);
            alumnoConceptoMatricula.setFechaRegistro(today.toDate());
            alumnoConceptoMatricula.setUserRegistro(ds.getUsuario());
            //  alumnoConceptoMatriculaDAO.save(alumnoConceptoMatricula);
        }

        for (AlumnoCuotaMatricula alumnoCuotaMatricula : alumnoResumenCuotas.getAlumnoCuotasMatricula()) {
            alumnoCuotaMatricula.setAlumnoResumenCuotas(alumnoResumenCuotas);
            alumnoCuotaMatricula.setEstado(AlumnoCuotaEstadoEnum.PEN);
            alumnoCuotaMatricula.setFechaEmision(today.toDate());
            alumnoCuotaMatricula.setFechaPago(today.toDate());
            //  alumnoCuotaMatriculaDAO.save(alumnoCuotaMatricula);
        }
        alumnoResumenCuotasDAO.save(alumnoResumenCuotas);
    }

}
