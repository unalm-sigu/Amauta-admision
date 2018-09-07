package pe.edu.lamolina.pivot.controller.academico.cargaadicional.docente;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfiguraCargaAdicional;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteCiclo;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Factor1CargaAdicional;
import pe.edu.lamolina.model.academico.Factor2CargaAdicional;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.ConfiguraCargaAdicionalEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.pivot.dao.academico.ConfiguraCargaAdicionalDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.Factor1CargaAdicionalDAO;
import pe.edu.lamolina.pivot.dao.academico.Factor2CargaAdicionalDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class CargaAdicionalDocenteServiceImp implements CargaAdicionalDocenteService {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    DocenteCicloDAO docenteCicloDAO;
    
    @Autowired
    ConfiguraCargaAdicionalDAO configuraCargaAdicionalDAO;
    
    @Autowired
    Factor1CargaAdicionalDAO factor1CargaAdicionalDAO;
    
    @Autowired
    Factor2CargaAdicionalDAO factor2CargaAdicionalDAO;
    
    @Autowired
    DocenteDAO docenteDAO;
    
    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;
    
    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;
    
    @Override
    public List<DocenteCiclo> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico) {
        return docenteCicloDAO.allByDynatableCicloAcademico(filter, cicloAcademico);
    }
    
    @Override
    public ConfiguraCargaAdicional findConfiguracionByCicloAcademico(CicloAcademico cicloAcademico) {
        ConfiguraCargaAdicional conf = configuraCargaAdicionalDAO.findByCicloAcademico(cicloAcademico);
        if (conf == null) {
            return new ConfiguraCargaAdicional();
        }
        return conf;
    }
    
    @Override
    @Transactional
    public void eliminarCarga(CicloAcademico cicloAcademico, DataSessionPivot ds) {
        ConfiguraCargaAdicional conf = configuraCargaAdicionalDAO.findByCicloAcademico(cicloAcademico);
        if (conf.getEstadoEnum() != ConfiguraCargaAdicionalEstadoEnum.CARGA) {
            return;
        }
        conf.setEstado(ConfiguraCargaAdicionalEstadoEnum.CRE);
        docenteCicloDAO.deshacerCarga(cicloAcademico);
        configuraCargaAdicionalDAO.update(conf);
    }
    
    @Override
    @Transactional
    public void eliminarMontos(CicloAcademico cicloAcademico, DataSessionPivot ds) {
        ConfiguraCargaAdicional conf = configuraCargaAdicionalDAO.findByCicloAcademico(cicloAcademico);
        if (conf.getEstadoEnum() != ConfiguraCargaAdicionalEstadoEnum.MONTO) {
            return;
        }
        conf.setEstado(ConfiguraCargaAdicionalEstadoEnum.CARGA);
        docenteCicloDAO.deshacerMontos(cicloAcademico);
        configuraCargaAdicionalDAO.update(conf);
    }
    
    private BigDecimal getCreditos(Seccion seccion) {
        switch (seccion.getTipoSeccionEnum()) {
            case TCUR:
            case TEO:
                return new BigDecimal(seccion.getGrupoSeccion().getCurso().getHorasTeoria());
            case PCUR:
            case PRA:
                return new BigDecimal(seccion.getGrupoSeccion().getCurso().getHorasPractica() / 2);
            default:
                throw new AssertionError();
        }
    }
    
    @Override
    @Transactional
    public void generarCarga(CicloAcademico cicloAcademico, DataSessionPivot ds) {
        ConfiguraCargaAdicional conf = configuraCargaAdicionalDAO.findByCicloAcademico(cicloAcademico);
        if (conf.getEstadoEnum() != ConfiguraCargaAdicionalEstadoEnum.CRE) {
            return;
        }
        
        List<Factor1CargaAdicional> factores1 = factor1CargaAdicionalDAO.allByCicloAcademico(cicloAcademico);
        Map<String, Factor1CargaAdicional> mapFactor1 = factores1.stream().collect(Collectors.toMap(x -> String.format("%s-%s", x.getSituacionDocente().getCodigo(), x.getCategoriaDocente().getCodigo()), x -> x));
        
        List<Factor2CargaAdicional> factores2 = factor2CargaAdicionalDAO.allByCicloAcademico(cicloAcademico);
        checkFactor2(factores2);
        
        NavigableMap<Integer, Integer> map = new TreeMap<Integer, Integer>();
        
        RangeMap<BigDecimal, Factor2CargaAdicional> mapFactor2 = TreeRangeMap.create();
        for (Factor2CargaAdicional factor : factores2) {
            if (factor.getCantidadFin() != null) {
                mapFactor2.put(Range.closed(new BigDecimal(factor.getCantidadInicio()), new BigDecimal(factor.getCantidadFin())), factor);
            } else {
                mapFactor2.put(Range.closed(new BigDecimal(factor.getCantidadInicio()), new BigDecimal(999)), factor);
            }
        }
        
        ModalidadEstudio modalidadEstudio = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        
        List<DocenteSeccion> docenteSeccion = docenteSeccionDAO.allByModalidadEstudioCicloAcademico(modalidadEstudio, cicloAcademico);
        
        Map<Docente, List<DocenteSeccion>> mapDocenteSeccion = docenteSeccion.stream().collect(Collectors.groupingBy(DocenteSeccion::getDocente));
        
        for (Map.Entry<Docente, List<DocenteSeccion>> entry : mapDocenteSeccion.entrySet()) {
            Docente docente = entry.getKey();
            List<DocenteSeccion> docenteSeccions = entry.getValue();
            
            BigDecimal cantidadCreditos = BigDecimal.ZERO;
            BigDecimal promedioAlumnos = BigDecimal.ZERO;
            BigDecimal sumatoriaMatriculados = BigDecimal.ZERO;
            BigDecimal sumatoriaPorcentajeCarga = BigDecimal.ZERO;
            
            for (DocenteSeccion docenteSeccion1 : docenteSeccions) {
                BigDecimal creditos = getCreditos(docenteSeccion1.getSeccion());
                
                BigDecimal porcentajeCarga
                        = docenteSeccion1.getPorcentajeCarga() != null
                        ? docenteSeccion1.getPorcentajeCarga()
                        : BigDecimal.ZERO;
                
                BigDecimal matriculados
                        = docenteSeccion1.getSeccion().getMatriculados() != null
                        ? new BigDecimal(docenteSeccion1.getSeccion().getMatriculados())
                        : BigDecimal.ZERO;
                
                cantidadCreditos = cantidadCreditos.add(porcentajeCarga.multiply(creditos).multiply(matriculados));
                sumatoriaMatriculados = sumatoriaMatriculados.add(matriculados);
                
                promedioAlumnos = promedioAlumnos.add(porcentajeCarga.multiply(matriculados));
                sumatoriaPorcentajeCarga = sumatoriaPorcentajeCarga.add(porcentajeCarga);
            }
            
            if (sumatoriaMatriculados.compareTo(BigDecimal.ZERO) > 0) {
                cantidadCreditos = cantidadCreditos.divide(sumatoriaMatriculados, 4, RoundingMode.HALF_UP);
            } else {
                cantidadCreditos = BigDecimal.ZERO;
            }
            
            if (sumatoriaPorcentajeCarga.compareTo(BigDecimal.ZERO) > 0) {
                promedioAlumnos = promedioAlumnos.divide(sumatoriaPorcentajeCarga, 4, RoundingMode.HALF_UP);
            } else {
                promedioAlumnos = BigDecimal.ZERO;
            }
            
            DocenteCiclo dc = new DocenteCiclo();
            
            dc.setDocente(docente);
            dc.setCicloAcademico(cicloAcademico);
            dc.setPromedioAlumnos(promedioAlumnos);
            dc.setCreditosTotal(cantidadCreditos);
            dc.setModalidadEstudio(modalidadEstudio);
            
            if (docente.getSituacion() == null || docente.getCategoria() == null) {
                dc.setFactor1(BigDecimal.ZERO);
                dc.setCreditosExceso(BigDecimal.ZERO);
            } else {
                Factor1CargaAdicional factor = mapFactor1.get(String.format("%s-%s", docente.getSituacion().getCodigo(), docente.getCategoria().getCodigo()));
                if (factor == null) {
                    dc.setFactor1(BigDecimal.ZERO);
                    dc.setCreditosExceso(BigDecimal.ZERO);
                } else {
                    dc.setFactor1(factor.getFactor());
                    dc.setCreditosExceso(dc.getCreditosTotal().subtract(new BigDecimal(factor.getCreditosMinimo())));
                    if (dc.getCreditosExceso().compareTo(BigDecimal.ZERO) < 0) {
                        dc.setCreditosExceso(BigDecimal.ZERO);
                    }
                }
            }
            
            if (promedioAlumnos.equals(BigDecimal.ZERO)) {
                dc.setFactor2(BigDecimal.ZERO);
            } else {
                Factor2CargaAdicional factor = mapFactor2.get(dc.getPromedioAlumnos());
                if (factor != null) {
                    dc.setFactor2(factor.getFactor());
                } else {
                    dc.setFactor2(BigDecimal.ZERO);
                }
            }
            
            dc.setFechaRegistro(new Date());
            dc.setUserRegistro(ds.getUsuario());
            docenteCicloDAO.save(dc);
            
        }
        
        conf.setEstado(ConfiguraCargaAdicionalEstadoEnum.CARGA);
        configuraCargaAdicionalDAO.update(conf);
    }
    
    private void checkFactor2(List<Factor2CargaAdicional> factores) {
        factores.sort(Comparator.comparing(Factor2CargaAdicional::getCantidadInicio));
        for (int i = 0; i < factores.size(); i++) {
            Factor2CargaAdicional factor = factores.get(i);
            if (i == 0) {
                Assert.isTrue(factor.getCantidadInicio() == 1, "El factor 2 no está configurado");
            } else if (i == factores.size() - 1) {
                Factor2CargaAdicional factorAnterior = factores.get(i - 1);
                Assert.isNull(factor.getCantidadFin(), "El factor 2 no está configurado");
                Assert.isTrue(factor.getCantidadInicio() == factorAnterior.getCantidadFin() + 1, "El factor 2 no está configurado");
            } else {
                Factor2CargaAdicional factorAnterior = factores.get(i - 1);
                Assert.isTrue(factor.getCantidadInicio() != 1, "El factor 2 no está configurado");
                Assert.isTrue(factor.getCantidadInicio() == factorAnterior.getCantidadFin() + 1, "El factor 2 no está configurado");
                Assert.isNotNull(factor.getCantidadFin(), "Los intervalos del factor 2 se cruzan");
            }
        }
    }
    
    @Override
    @Transactional
    public void generarMontos(CicloAcademico cicloAcademico, DataSessionPivot ds) {
        ConfiguraCargaAdicional conf = configuraCargaAdicionalDAO.findByCicloAcademico(cicloAcademico);
        if (conf.getEstadoEnum() != ConfiguraCargaAdicionalEstadoEnum.CARGA) {
            return;
        }
        docenteCicloDAO.generarMontos(cicloAcademico, conf.getRca());
        conf.setEstado(ConfiguraCargaAdicionalEstadoEnum.MONTO);
        configuraCargaAdicionalDAO.update(conf);
    }
    
    @Override
    @Transactional
    public void saveConfiguracion(ConfiguraCargaAdicional configuraCargaAdicional, CicloAcademico cicloAcademico, DataSessionPivot ds) {
        ConfiguraCargaAdicional confBD = configuraCargaAdicionalDAO.findByCicloAcademico(cicloAcademico);
        
        if (confBD == null) {
            configuraCargaAdicional.setEstado(ConfiguraCargaAdicionalEstadoEnum.CRE);
            configuraCargaAdicional.setCicloAcademico(ds.getCicloAcademico());
            
            configuraCargaAdicional.setUserRegistro(ds.getUsuario());
            configuraCargaAdicional.setFechaRegistro(new Date());
            
            configuraCargaAdicionalDAO.save(configuraCargaAdicional);
        } else {
            Assert.isTrue(confBD.getEstadoEnum() == ConfiguraCargaAdicionalEstadoEnum.CRE, "La configuración ya ha sido aceptada");
            
            confBD.setMinimoAlumnos(configuraCargaAdicional.getMinimoAlumnos());
            confBD.setRca(configuraCargaAdicional.getRca());
            
            configuraCargaAdicionalDAO.update(confBD);
        }
    }
    
}
