package pe.edu.lamolina.pivot.controller.posgrado.tarifa;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ListsInspector;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.posgrado.ConceptoPosgrado;
import pe.edu.lamolina.model.posgrado.TarifaCarrera;
import pe.edu.lamolina.model.posgrado.TarifaConcepto;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.posgrado.ConceptoPosgradoDAO;
import pe.edu.lamolina.pivot.dao.posgrado.TarifaCarreraDAO;
import pe.edu.lamolina.pivot.dao.posgrado.TarifaConceptoDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class TarifaServiceImp implements TarifaService {

    @Autowired
    TarifaCarreraDAO tarifaCarreraDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    ConceptoPosgradoDAO conceptoPosgradoDAO;

    @Autowired
    TarifaConceptoDAO tarifaConceptoDAO;

    @Override
    @Transactional
    public void save(TarifaCarrera tarifaCarrera, DataSessionPivot ds) {
        tarifaCarrera.setEstado(EstadoEnum.CRE.name());
        tarifaCarrera.setUserRegistro(ds.getUsuario());
        tarifaCarrera.setFechaRegistro(new Date());
        tarifaCarreraDAO.save(tarifaCarrera);
        for (TarifaConcepto tarifaConcepto : tarifaCarrera.getTarifaConcepto()) {
            tarifaConcepto.setActivo(Boolean.TRUE);
            tarifaConcepto.setTarifaCarrera(tarifaCarrera);
            tarifaConceptoDAO.save(tarifaConcepto);
        }
    }

    @Override
    @Transactional
    public void clonar(TarifaCarrera tarifaCarrera, DataSessionPivot ds) {
        TarifaCarrera clon = new TarifaCarrera();
        clon.setUserRegistro(ds.getUsuario());
        clon.setEstado(EstadoEnum.CRE.name());
        clon.setFechaRegistro(new Date());
        tarifaCarreraDAO.save(clon);
    }

    @Override
    public TarifaCarrera find(Long id) {
        TarifaCarrera tc = tarifaCarreraDAO.find(id);
        tc.setTarifaConcepto(tarifaConceptoDAO.allByTarifaCarrera(tc));
        return tarifaCarreraDAO.find(id);
    }

    @Override
    @Transactional
    public void update(TarifaCarrera tc, DataSessionPivot ds) {
        TarifaCarrera tarifaBD = tarifaCarreraDAO.find(tc.getId());
        tarifaBD.setCicloInicio(tc.getCicloInicio());
        tarifaBD.setCostoCreditoExceso(tc.getCostoCreditoExceso());
        tarifaBD.setCostoCreditoMinimo(tc.getCostoCreditoMinimo());
        tarifaBD.setCreditosMaximo(tc.getCreditosMaximo());
        tarifaBD.setCreditosMinimo(tc.getCreditosMinimo());
        tarifaBD.setDescuentoCash(tc.getDescuentoCash());
        tarifaBD.setDescuentoSegundoCash(tc.getDescuentoSegundoCash());
        tarifaBD.setTasaInteres(tc.getTasaInteres());
        tarifaBD.setMaximoCuotas(tc.getMaximoCuotas());
        tarifaBD.setMonto(tc.getMonto());
        tarifaBD.setMora(tc.getMora());
        tarifaBD.setTipoMonto(tc.getTipoMontoEnum());
        tarifaCarreraDAO.update(tarifaBD);

        List<TarifaConcepto> listBD = tarifaConceptoDAO.allByTarifaCarrera(tarifaBD);
        Map<Long, TarifaConcepto> mapTarifaConceptoBD = listBD.stream().collect(Collectors.toMap(TarifaConcepto::getId, x -> x));

        ListsInspector inspector = TypesUtil.analizeLists(listBD, tc.getTarifaConcepto(), "id");

        for (TarifaConcepto tcon : (List<TarifaConcepto>) inspector.getDeadList()) {
            tarifaConceptoDAO.delete(tcon);
        }
        for (TarifaConcepto tcon : (List<TarifaConcepto>) inspector.getNewList()) {
            tcon.setActivo(Boolean.TRUE);
            tcon.setTarifaCarrera(tarifaBD);
            tarifaConceptoDAO.save(tcon);
        }
        for (TarifaConcepto tcon : (List<TarifaConcepto>) inspector.getOldListForm()) {
            TarifaConcepto tcBD = mapTarifaConceptoBD.get(tcon.getId());
            tcBD.setConceptoPosgrado(tcon.getConceptoPosgrado());
            tcBD.setFraccionable(tcon.getFraccionable());
            tcBD.setMonto(tcon.getMonto());
            tcBD.setMontoMinimoInicial(tcon.getMontoMinimoInicial());
            tcBD.setPorcentajeInicial(tcon.getPorcentajeInicial());
            tcBD.setPorcentajeMinimoInicial(tcon.getPorcentajeMinimoInicial());
            tarifaConceptoDAO.update(tcon);
        }
    }

    @Override
    @Transactional
    public void eliminar(TarifaCarrera tarifaCarrera, DataSessionPivot ds) {
        Assert.isTrue(tarifaCarrera.getEstado().equals(EstadoEnum.CRE.name()), "Solo se pueden elimintar tarifas en estado creado");
        tarifaConceptoDAO.deleteAllByTarifaCarrera(tarifaCarrera);
        tarifaCarreraDAO.delete(tarifaCarrera);
    }

    @Override
    @Transactional
    public void activar(TarifaCarrera tarifaCarrera, DataSessionPivot ds) {
        TarifaCarrera tarifaBD = tarifaCarreraDAO.find(tarifaCarrera.getId());
        tarifaBD.setEstado(EstadoEnum.ACT.name());
        tarifaBD.setUserActivacion(ds.getUsuario());
        tarifaBD.setFechaActivacion(new Date());
        tarifaCarreraDAO.update(tarifaBD);
    }

    @Override
    public List<TarifaCarrera> allByDynatable(DynatableFilter filter) {
        return tarifaCarreraDAO.allByDynatable(filter);
    }

    @Override
    public List<Carrera> allCarreraMaestria() {
        return carreraDAO.allActivasByModalidadEnum(ModalidadEstudioEnum.EPG);
    }

    @Override
    public List<CicloAcademico> allCicloAcademico() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int yearinit = year - 6;
        int yearend = year + 3;
        return cicloAcademicoDAO.allPregradoByRange(yearinit, yearend);
    }

    @Override
    public List<ConceptoPosgrado> allConceptoPosgrado() {
        return conceptoPosgradoDAO.all();
    }

}
