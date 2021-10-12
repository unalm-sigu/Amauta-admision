package pe.edu.lamolina.amauta.dao.general;

import java.util.List;
import java.util.Map;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.AccionTramiteAcademico;
import pe.edu.lamolina.model.tramite.AccionTramiteDocumento;

public interface OficinaDAO extends EasyDAO<Oficina> {

    List<Oficina> allByJefe(Persona persona);

    List<Oficina> allByJefeTipoOficinaEnum(Persona persona, TipoOficinaEnum tipoOficina);

    List<Oficina> allByFilter(DynatableFilter filter, List<Oficina> oficinasAcceso, Compania compania);

    List<Oficina> allUnidadSuperior(String nombre, Compania compania);

    List<Oficina> allOficinasByName(String nombre);

    List<Oficina> allByOficinaWithAulas(List<Oficina> oficinas);

    List<Oficina> allByUser(Persona persona);

    List<Oficina> allAndSuperiorOfi();

    List<Oficina> allByName(String nombre);

    Oficina findByCode(String codigo);

    List<Oficina> allByCompania(Compania compania);

    Oficina findByTipoOficinaFacultad(TipoOficinaEnum tipoOficinaEnum, Facultad facultad);

    Oficina findByTipoOficinaDptoAcademico(TipoOficinaEnum tipoOficinaEnum, DepartamentoAcademico departamento);

    Map findOficinaOrigenDestinoByEstadoTramiteAcad(AccionTramiteAcademico accionTramiteAcademico, Alumno alumno);

    List<Oficina> allOficinaByUserMenu(Usuario usuario, Menu menu);

    List<Oficina> allForResoluciones();

    List<Oficina> allByNivel(TipoOficinaEnum tipoOficinaEnum);

    List<Oficina> allByNombre(String nombre, Compania compania);

    Oficina find(Oficina oficina);

    List<Oficina> allOficinaByName(String nombre, Compania compania);

    List<Oficina> allFac();

    List<Oficina> allOficinaConsejero();

    Map findOficinaOrigenDestinoByEstadoTramiteDoc(AccionTramiteDocumento accionTramiteDoc, Alumno alumno);

    List<Oficina> allDireccionPosgrado();

    List<Oficina> allEspecialidadPosgrado();

}
