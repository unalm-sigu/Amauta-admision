package pe.edu.lamolina.amauta.controller.academico.profesor.contratoprofesor;

import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.ContratoDocenteEstadoEnum;
import pe.edu.lamolina.model.enums.DocenteEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.rrhh.ContratoDocente;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteDAO;
import pe.edu.lamolina.amauta.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.amauta.dao.rrhh.ContratoDocenteDAO;
import pe.edu.lamolina.amauta.dao.tramite.ResolucionDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class ContratoServiceImp implements ContratoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ContratoDocenteDAO contratoDocenteDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    ResolucionDAO resolucionDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    DocenteDAO docenteDAO;

    @Override
    @Transactional
    public void addResolucionConsejo(ContratoDocente contratoDocente, Resolucion resolucionConsejo, DataSessionPivot ds) {
        ContratoDocente cdBD = contratoDocenteDAO.find(contratoDocente.getId());

        cdBD.setResolucionConsejo(resolucionConsejo);
        cdBD.setUserConsejo(ds.getUsuario());
        cdBD.setFechaConsejo(new Date());

        if (cdBD.getResolucionFacultad() != null) {
            cdBD.setEstado(ContratoDocenteEstadoEnum.RESL);
        }

        contratoDocenteDAO.update(cdBD);
    }

    @Override
    @Transactional
    public void addResolucionFacultad(ContratoDocente contratoDocente, Resolucion resolucionFacultad, DataSessionPivot ds) {
        ContratoDocente cdBD = contratoDocenteDAO.find(contratoDocente.getId());

        cdBD.setResolucionFacultad(resolucionFacultad);
        cdBD.setUserFacultad(ds.getUsuario());
        cdBD.setFechaFacultad(new Date());

        if (cdBD.getResolucionConsejo() != null) {
            cdBD.setEstado(ContratoDocenteEstadoEnum.RESL);
        }

        contratoDocenteDAO.update(cdBD);
    }

    @Override
    @Transactional
    public void addVistoBueno(ContratoDocente contratoDocente, DataSessionPivot ds) {
        ContratoDocente cdBD = contratoDocenteDAO.find(contratoDocente.getId());

        cdBD.setUserVobo(ds.getUsuario());
        cdBD.setFechaVobo(new Date());

        CicloAcademico cicloInicio = cdBD.getCicloInicioContrato();
        CicloAcademico cicloFin = cdBD.getCicloFinContrato();

        ModalidadEstudio me = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        CicloAcademico actual = cicloAcademicoDAO.findActivo(me);

        if (cicloInicio.getCodigo().compareTo(actual.getCodigo()) <= 0 && cicloFin.getCodigo().compareTo(actual.getCodigo()) > 0) {
            cdBD.setEstado(ContratoDocenteEstadoEnum.ACT);
        } else if (cicloFin.getCodigo().compareTo(actual.getCodigo()) < 0) {
            cdBD.setEstado(ContratoDocenteEstadoEnum.VENC);
        }

        contratoDocenteDAO.update(cdBD);

    }

    @Override
    public List<ContratoDocente> allByDynatable(DynatableFilter filter, Docente docente) {
        return contratoDocenteDAO.allByDynatableProfesor(filter, docente);
    }

    @Override
    public List<CicloAcademico> allCicloByName(String nombre) {
        return cicloAcademicoDAO.allCicloByName(nombre);
    }

    @Override
    @Transactional
    public void finalizar(ContratoDocente contratoDocente, DataSessionPivot ds) {
        ContratoDocente cdBD = contratoDocenteDAO.find(contratoDocente.getId());
        Assert.isTrue(cdBD.getEstadoEnum() == ContratoDocenteEstadoEnum.ACT, "El contrato no está activo");

        cdBD.setEstado(ContratoDocenteEstadoEnum.CFIN);
        contratoDocenteDAO.update(cdBD);
    }

    @Override
    @Transactional
    public void save(Docente docente, ContratoDocente contratoDocente, DataSessionPivot ds) {
        CicloAcademico cicloInicio = cicloAcademicoDAO.find(contratoDocente.getCicloInicioContrato().getId());
        CicloAcademico cicloFin = cicloAcademicoDAO.find(contratoDocente.getCicloFinContrato().getId());

        Assert.isTrue(cicloInicio.getCodigo().compareTo(cicloFin.getCodigo()) <= 0, "El ciclo final no puede ser menor que el inicial");
        Assert.isTrue(contratoDocenteDAO.allByPeriodoDocente(cicloInicio, cicloFin, docente).isEmpty(), "Existen contratos activos en el periodo escogido");

        contratoDocente.setDocente(docente);
        contratoDocente.setEstado(ContratoDocenteEstadoEnum.PEND);
        contratoDocente.setUserRegistro(ds.getUsuario());
        contratoDocente.setFechaRegistro(new Date());

        contratoDocenteDAO.save(contratoDocente);

        ModalidadEstudio me = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        CicloAcademico actual = cicloAcademicoDAO.findActivo(me);

        if (cicloInicio.getCodigo().compareTo(actual.getCodigo()) <= 0 && cicloFin.getCodigo().compareTo(actual.getCodigo()) > 0) {
            Docente docenteBD = docenteDAO.find(docente.getId());

            docenteBD.setCategoria(contratoDocente.getCategoria());
            docenteBD.setSituacion(contratoDocente.getSituacion());
            docenteBD.setDedicacion(contratoDocente.getDedicacion());

            docenteBD.setCicloInicioContrato(cicloInicio);
            docenteBD.setCicloFinContrato(cicloFin);

            docenteBD.setCategoria(contratoDocente.getCategoria());
            docenteBD.setEstado(DocenteEstadoEnum.ACT);

            docenteDAO.update(docenteBD);
        }

    }

    @Override
    public List<Resolucion> searchResolucionConsejo(String nombre) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        return resolucionDAO.allByNombre(nombre);
    }

    @Override
    public List<Resolucion> searchResolucionFacultad(String nombre) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        return resolucionDAO.allByNombre(nombre);
    }

}
