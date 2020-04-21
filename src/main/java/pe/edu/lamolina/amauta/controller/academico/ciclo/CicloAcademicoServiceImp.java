package pe.edu.lamolina.amauta.controller.academico.ciclo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.NumeroCicloAcademicoEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.ModalidadEstudioDAO;

@Service
@Transactional(readOnly = true)
public class CicloAcademicoServiceImp implements CicloAcademicoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Override
    public List<CicloAcademico> allCicloAcademico(Integer maxResultado) {
        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        return cicloAcademicoDAO.allForChanges(maxResultado, modalidad);
    }

    @Override
    public CicloAcademico getCicloAcademico(Long cicloAcademico) {
        return cicloAcademicoDAO.find(cicloAcademico);
    }

    @Override
    @Transactional
    public void delete(CicloAcademico cicloAcademico) {
        cicloAcademicoDAO.delete(cicloAcademico);
    }

    @Override
    @Transactional
    public void save(CicloAcademico cicloAcademico, Usuario usuario) {

        ObjectUtil.eliminarAttrSinId(cicloAcademico, "modalidadEstudio");
        if (cicloAcademico.getModalidadEstudio() == null) {
            throw new PhobosException("Tiene que especificar la modalidad de estudio.");
        }
        for (NumeroCicloAcademicoEnum numeroCicloAcademicoEnum : NumeroCicloAcademicoEnum.values()) {
            CicloAcademico cicloAcademicoNew = new CicloAcademico();
            cicloAcademicoNew.setEstadoEnum(CicloAcademicoEstadoEnum.CRE);
            cicloAcademicoNew.setNumeroCiclo(numeroCicloAcademicoEnum.getValue());
            cicloAcademicoNew.setDescripcion(numeroCicloAcademicoEnum.getDescripcion().replace("XXXX", cicloAcademico.getYear().toString()));
            cicloAcademicoNew.setDescripcion2(numeroCicloAcademicoEnum.getDescripcion2().replace("XXXX", cicloAcademico.getYear().toString()));
            cicloAcademicoNew.setDescripcion3(numeroCicloAcademicoEnum.getDescripcion3().replace("XXXX", cicloAcademico.getYear().toString()));
            cicloAcademicoNew.setCodigo(numeroCicloAcademicoEnum.getCodigo().replace("XXXX", cicloAcademico.getYear().toString()));
            cicloAcademicoNew.setFechaRegistro(new Date());
            cicloAcademicoNew.setUserRegistro(usuario);
            cicloAcademicoNew.setYear(cicloAcademico.getYear());
            cicloAcademicoNew.setModalidadEstudio(cicloAcademico.getModalidadEstudio());
            cicloAcademicoDAO.save(cicloAcademicoNew);
        }
    }

    @Override
    @Transactional
    public void update(CicloAcademico cicloAcademico, Usuario usuario) {
        CicloAcademico cicloAcademicoDB = cicloAcademicoDAO.findByCiclo(cicloAcademico);
        ObjectUtil.eliminarAttrSinId(cicloAcademico, "modalidadEstudio");
        if (cicloAcademico.getModalidadEstudio() == null) {
            throw new PhobosException("Tiene que especificar la modalidad de estudio.");
        }
        NumeroCicloAcademicoEnum numeroCicloAcademicoEnum = NumeroCicloAcademicoEnum.get(cicloAcademicoDB.getNumeroCiclo());
        cicloAcademico.setEstadoEnum(CicloAcademicoEstadoEnum.CRE);
        cicloAcademicoDB.setYear(cicloAcademico.getYear());
        cicloAcademicoDB.setModalidadEstudio(cicloAcademico.getModalidadEstudio());
        cicloAcademicoDB.setDescripcion(numeroCicloAcademicoEnum.getDescripcion().replace("XXXX", cicloAcademico.getYear().toString()));
        cicloAcademicoDB.setDescripcion2(numeroCicloAcademicoEnum.getDescripcion2().replace("XXXX", cicloAcademico.getYear().toString()));
        cicloAcademicoDB.setDescripcion3(numeroCicloAcademicoEnum.getDescripcion3().replace("XXXX", cicloAcademico.getYear().toString()));
        cicloAcademicoDB.setCodigo(numeroCicloAcademicoEnum.getCodigo().replace("XXXX", cicloAcademico.getYear().toString()));
        cicloAcademicoDAO.update(cicloAcademicoDB);
    }

    @Override
    public CicloAcademico findCicloAcademico(CicloAcademico cicloAcademico) {
        return cicloAcademicoDAO.findByCiclo(cicloAcademico);
    }

    @Override
    public List<CicloAcademico> allByDynatable(DynatableFilter filter) {
        if (filter.getQueries() == null) {
            filter.setFiltered(0);
            filter.setTotal(0);
            return new ArrayList();
        }
        return cicloAcademicoDAO.allByDynatable(filter);
    }

    @Override
    public List<ModalidadEstudio> allPrePostgrado(Compania cia) {
        return modalidadEstudioDAO.allPrePostgrado(cia);
    }

    @Override
    @Transactional
    public void anular(CicloAcademico cicloAcademico) {

        CicloAcademico cicloAcademicoDB = cicloAcademicoDAO.findByCiclo(cicloAcademico);

        if (!(CicloAcademicoEstadoEnum.CFG.name().equalsIgnoreCase(cicloAcademicoDB.getEstado())
                || CicloAcademicoEstadoEnum.ACT.name().equalsIgnoreCase(cicloAcademicoDB.getEstado()))) {
            throw new PhobosException("Su estado previo debe ser CONFIGURADO o ACTIVO");
        }

        List<GrupoSeccion> grupos = grupoSeccionDAO.allActivoByCiclo(cicloAcademicoDB);
        if (!grupos.isEmpty()) {
            throw new PhobosException("No puede anular un ciclo académico que contiene datos");
        }

        cicloAcademicoDB.setEstadoEnum(CicloAcademicoEstadoEnum.ANU);
        cicloAcademicoDB.setMotivoAnulacion(cicloAcademico.getMotivoAnulacion());
        cicloAcademicoDAO.update(cicloAcademicoDB);
    }

    @Override
    @Transactional
    public void desactivar(CicloAcademico cicloAcademico) {
        CicloAcademico cicloAcademicoDB = cicloAcademicoDAO.findByCiclo(cicloAcademico);

        if (!(CicloAcademicoEstadoEnum.CRE.name().equalsIgnoreCase(cicloAcademicoDB.getEstado()))) {
            throw new PhobosException("Su estado previo debe ser CREADO");
        }

        List<GrupoSeccion> grupos = grupoSeccionDAO.allActivoByCiclo(cicloAcademicoDB);
        if (!grupos.isEmpty()) {
            throw new PhobosException("No puede desactivar un ciclo académico que contiene datos");
        }

        cicloAcademicoDB.setEstadoEnum(CicloAcademicoEstadoEnum.DES);
        cicloAcademicoDB.setMotivoAnulacion("No se usa el ciclo.");
        cicloAcademicoDAO.update(cicloAcademicoDB);

    }

    @Override
    @Transactional
    public void configurar(CicloAcademico cicloAcademico) {
        CicloAcademico cicloAcademicoDB = cicloAcademicoDAO.findByCiclo(cicloAcademico);

        if (!(CicloAcademicoEstadoEnum.CRE.name().equalsIgnoreCase(cicloAcademicoDB.getEstado()))) {
            throw new PhobosException("Su estado previo debe ser CREADO");
        }
        cicloAcademicoDB.setEstadoEnum(CicloAcademicoEstadoEnum.CFG);
        cicloAcademicoDAO.update(cicloAcademicoDB);

    }

    @Override
    @Transactional
    public void activar(CicloAcademico cicloAcademico) {
        CicloAcademico cicloAcademicoDB = cicloAcademicoDAO.findByCiclo(cicloAcademico);

        if (!(CicloAcademicoEstadoEnum.CFG.name().equalsIgnoreCase(cicloAcademicoDB.getEstado())
                || CicloAcademicoEstadoEnum.ACT.name().equalsIgnoreCase(cicloAcademicoDB.getEstado()))) {
            throw new PhobosException("Su estado previo debe ser CONFIGURADO o ACTIVO");
        }

        CicloAcademico cicloAcademicoActivo = cicloAcademicoDAO.findActivoByModalidad(cicloAcademicoDB.getModalidadEstudio());
        if (cicloAcademicoActivo != null) {
            cicloAcademicoActivo.setEstadoEnum(CicloAcademicoEstadoEnum.PEND);
            cicloAcademicoDAO.update(cicloAcademicoActivo);
        }

        cicloAcademicoDB.setEstadoEnum(CicloAcademicoEstadoEnum.ACT);
        cicloAcademicoDAO.update(cicloAcademicoDB);

    }

    @Override
    @Transactional
    public void cerrar(CicloAcademico cicloAcademico) {
        CicloAcademico cicloAcademicoDB = cicloAcademicoDAO.findByCiclo(cicloAcademico);

        if (!(CicloAcademicoEstadoEnum.PEND.name().equalsIgnoreCase(cicloAcademicoDB.getEstado())
                || CicloAcademicoEstadoEnum.ACT.name().equalsIgnoreCase(cicloAcademicoDB.getEstado()))) {
            throw new PhobosException("Su estado previo debe ser PENDIENTE o ACTIVO");
        }

        List<GrupoSeccion> grupos = grupoSeccionDAO.allActivoByCicloGrupoNoCerrado(cicloAcademicoDB);
        if (!grupos.isEmpty()) {
            throw new PhobosException("No se puede cerrar el ciclo académico , aun contiene actas sin cerrar.");
        }

        cicloAcademicoDB.setEstadoEnum(CicloAcademicoEstadoEnum.CER);
        cicloAcademicoDAO.update(cicloAcademicoDB);

    }

    @Override
    @Transactional
    public void pendiente(CicloAcademico cicloAcademico) {
        CicloAcademico cicloAcademicoDB = cicloAcademicoDAO.findByCiclo(cicloAcademico);

        if (!(CicloAcademicoEstadoEnum.ACT.name().equalsIgnoreCase(cicloAcademicoDB.getEstado()))) {
            throw new PhobosException("Su estado previo debe ser ACTIVO");
        }

        cicloAcademicoDB.setEstadoEnum(CicloAcademicoEstadoEnum.PEND);
        cicloAcademicoDAO.update(cicloAcademicoDB);

    }

    @Override
    public List<Integer> allYear() {
        List<Integer> margen = new ArrayList<>();
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Integer year = cal.get(Calendar.YEAR);
        margen.add(year - 1);
        margen.add(year);
        margen.add(year + 1);
        margen.add(year + 2);
        return margen;
    }

    @Override
    @Transactional
    public void changeVisiblelogin(CicloAcademico cicloAcademico) {
        CicloAcademico academico = cicloAcademicoDAO.find(cicloAcademico);
        academico.setVisibleLogin(academico.getVisibleLogin() ? false : true);
        cicloAcademicoDAO.update(academico);
    }

}
