package pe.edu.lamolina.pivot.controller.general.oficina;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.FacultadDAO;
import pe.edu.lamolina.pivot.dao.general.ColaboradorDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaDAO;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.Facultad;
import pe.edu.lamolina.pivot.model.general.Colaborador;
import pe.edu.lamolina.pivot.model.general.Compania;
import pe.edu.lamolina.pivot.model.general.Oficina;
import pe.edu.lamolina.pivot.model.general.Persona;
import pe.edu.lamolina.pivot.zelper.enums.OficinaEstadoEnum;

@Service
@Transactional(readOnly = true)
public class OficinaServiceImp implements OficinaService {

    @Autowired
    OficinaDAO oficinaDAO;

    @Autowired
    ColaboradorDAO colaboradorDAO;

    @Autowired
    DepartamentoAcademicoDAO departamentoAcademicoDAO;

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    FacultadDAO facultadDAO;

    @Autowired
    PersonaDAO personaDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<Oficina> allByDynatable(DynatableFilter filter, Compania compania) {
        return oficinaDAO.allByFilter(filter, compania);
    }

    @Override
    public Oficina find(Oficina persona) {
        return oficinaDAO.find(persona.getId());
    }

    @Override
    @Transactional
    public void update(Oficina oficina) {
        ObjectUtil.eliminarAttrSinId(oficina, "oficinaSuperior");
        Oficina oficinaDb = oficinaDAO.find(oficina.getId());
        oficinaDb.setOficinaSuperior(oficina.getOficinaSuperior());
        oficinaDb.setNombre(oficina.getNombre());
        oficinaDb.setCodigo(oficina.getCodigo());
        oficinaDb.setTipoOficina(oficina.getTipoOficina());
        oficinaDb.setInstanciaOficina(oficina.getInstanciaOficina());
        oficinaDAO.update(oficinaDb);
    }

    @Override
    @Transactional
    public void save(Oficina oficina) {
        oficina.setEstado(OficinaEstadoEnum.ACT.name());
        ObjectUtil.eliminarAttrSinId(oficina, "oficinaSuperior");
        oficinaDAO.save(oficina);
    }

    @Override
    @Transactional
    public void delete(Oficina oficina) {
        oficinaDAO.delete(oficina);
    }

    @Override
    public List<Colaborador> allColaborador(List<Oficina> oficinas) {
        if (oficinas.size() < 1) {
            return new ArrayList();
        }
        return colaboradorDAO.allColaborador(oficinas);
    }

    @Override
    public List<Oficina> allUnidadSuperior(String nombre, Compania compania) {
        return oficinaDAO.allUnidadSuperior(nombre, compania);
    }

    @Override
    public List<DepartamentoAcademico> allDepartamento(Compania compania) {
        return departamentoAcademicoDAO.allByCompania(compania);
    }

    @Override
    public List<Carrera> allCarrera(Compania compania) {
        return carreraDAO.allByCompania(compania);
    }

    @Override
    public List<Facultad> allFacultad(Compania compania) {
        return facultadDAO.allByCompania(compania);
    }

    @Override
    @Transactional
    public void estado(Oficina oficina) {
        Oficina oficinaBD = oficinaDAO.find(oficina.getId());
        if (OficinaEstadoEnum.ANU.name().equalsIgnoreCase(oficinaBD.getEstado())) {
            oficinaBD.setEstado(OficinaEstadoEnum.ACT.name());
        } else {
            oficinaBD.setEstado(OficinaEstadoEnum.ANU.name());
        }
        oficinaDAO.update(oficinaBD);
    }

    @Override
    public List<Persona> allPersona(String nombre) {
        return personaDAO.allByNombre(nombre);
    }
}
