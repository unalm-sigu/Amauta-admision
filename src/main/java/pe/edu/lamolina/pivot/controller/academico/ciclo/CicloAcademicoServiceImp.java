package pe.edu.lamolina.pivot.controller.academico.ciclo;

import java.util.ArrayList;
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
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.CicloEstadoEnum;
import pe.edu.lamolina.model.enums.NumeroCicloAcademicoEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;

@Service
@Transactional(readOnly = true)
public class CicloAcademicoServiceImp implements CicloAcademicoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Override
    public List<CicloAcademico> allCicloAcademico(Integer maxResultado) {
        return cicloAcademicoDAO.allForChanges(maxResultado);
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
        if (cicloAcademico.getNumeroCiclo() == null) {
            throw new PhobosException("Tiene que especificar el número de ciclo.");
        }
        if (cicloAcademico.getModalidadEstudio() == null) {
            throw new PhobosException("Tiene que especificar la modalidad de estudio.");
        }
        NumeroCicloAcademicoEnum numeroCicloAcademicoEnum = NumeroCicloAcademicoEnum.get(cicloAcademico.getNumeroCiclo());
        cicloAcademico.setEstado(CicloEstadoEnum.CRE);
        cicloAcademico.setDescripcion(numeroCicloAcademicoEnum.getDescripcion().replace("XXXX", cicloAcademico.getYear().toString()));
        cicloAcademico.setDescripcion2(numeroCicloAcademicoEnum.getDescripcion2().replace("XXXX", cicloAcademico.getYear().toString()));
        cicloAcademico.setDescripcion3(numeroCicloAcademicoEnum.getDescripcion3().replace("XXXX", cicloAcademico.getYear().toString()));
        cicloAcademico.setCodigo(numeroCicloAcademicoEnum.getCodigo().replace("XXXX", cicloAcademico.getYear().toString()));
        cicloAcademico.setFechaRegistro(new Date());
        cicloAcademico.setUserRegistro(usuario);
        cicloAcademicoDAO.save(cicloAcademico);
    }

    @Override
    @Transactional
    public void update(CicloAcademico cicloAcademico, Usuario usuario) {
        CicloAcademico cicloAcademicoDB = cicloAcademicoDAO.findCicloAcademico(cicloAcademico);
        ObjectUtil.eliminarAttrSinId(cicloAcademico, "modalidadEstudio");
        if (cicloAcademico.getNumeroCiclo() == null) {
            throw new PhobosException("Tiene que especificar el número de ciclo.");
        }
        if (cicloAcademico.getModalidadEstudio() == null) {
            throw new PhobosException("Tiene que especificar la modalidad de estudio.");
        }
        NumeroCicloAcademicoEnum numeroCicloAcademicoEnum = NumeroCicloAcademicoEnum.get(cicloAcademico.getNumeroCiclo());
        cicloAcademico.setEstado(CicloEstadoEnum.CRE);
        cicloAcademicoDB.setYear(cicloAcademico.getYear());
        cicloAcademicoDB.setNumeroCiclo(cicloAcademico.getNumeroCiclo());
        cicloAcademicoDB.setModalidadEstudio(cicloAcademico.getModalidadEstudio());
        cicloAcademicoDB.setDescripcion(numeroCicloAcademicoEnum.getDescripcion().replace("XXXX", cicloAcademico.getYear().toString()));
        cicloAcademicoDB.setDescripcion2(numeroCicloAcademicoEnum.getDescripcion2().replace("XXXX", cicloAcademico.getYear().toString()));
        cicloAcademicoDB.setDescripcion3(numeroCicloAcademicoEnum.getDescripcion3().replace("XXXX", cicloAcademico.getYear().toString()));
        cicloAcademicoDB.setCodigo(numeroCicloAcademicoEnum.getCodigo().replace("XXXX", cicloAcademico.getYear().toString()));
        cicloAcademicoDAO.update(cicloAcademicoDB);
    }

    @Override
    public CicloAcademico findCicloAcademico(CicloAcademico cicloAcademico) {
        return cicloAcademicoDAO.findCicloAcademico(cicloAcademico);
    }

    @Override
    public List<CicloAcademico> allByDynatable(DynatableFilter filter) {
        if (filter.getQueries() == null) {
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
    public void cerrar(CicloAcademico cicloAcademico) {
        CicloAcademico cicloAcademicoDB = cicloAcademicoDAO.findCicloAcademico(cicloAcademico);
        cicloAcademicoDB.setEstado(CicloEstadoEnum.CER);
        cicloAcademicoDAO.update(cicloAcademicoDB);
    }

    @Override
    @Transactional
    public void anular(CicloAcademico cicloAcademico) {
        CicloAcademico cicloAcademicoDB = cicloAcademicoDAO.findCicloAcademico(cicloAcademico);
        cicloAcademicoDB.setEstado(CicloEstadoEnum.ANU);
        cicloAcademicoDB.setMotivoAnulacion(cicloAcademico.getMotivoAnulacion());
        cicloAcademicoDAO.update(cicloAcademicoDB);
    }

    @Override
    @Transactional
    public void desactivar(CicloAcademico cicloAcademico) {
        CicloAcademico cicloAcademicoDB = cicloAcademicoDAO.findCicloAcademico(cicloAcademico);
        cicloAcademicoDB.setEstado(CicloEstadoEnum.DES);
        cicloAcademicoDB.setMotivoAnulacion("No se usa el ciclo.");
        cicloAcademicoDAO.update(cicloAcademicoDB);
    }

    @Override
    @Transactional
    public void activar(CicloAcademico cicloAcademico) {
        CicloAcademico cicloAcademicoDB = cicloAcademicoDAO.findCicloAcademico(cicloAcademico);
        CicloAcademico cicloAcademicoActivo = cicloAcademicoDAO.findCicloAcademicoActivo();
        if (cicloAcademicoActivo != null) {
            cicloAcademicoActivo.setEstado(CicloEstadoEnum.PEND);
        }
        cicloAcademicoDB.setEstado(CicloEstadoEnum.ACT);
        cicloAcademicoDAO.update(cicloAcademicoActivo);
        cicloAcademicoDAO.update(cicloAcademicoDB);
    }

    @Override
    @Transactional
    public void pendiente(CicloAcademico cicloAcademico) {
        CicloAcademico cicloAcademicoDB = cicloAcademicoDAO.findCicloAcademico(cicloAcademico);
        cicloAcademicoDB.setEstado(CicloEstadoEnum.PEND);
        cicloAcademicoDAO.update(cicloAcademicoDB);
    }

}
