package pe.edu.lamolina.pivot.controller.docente.cursodirigidofacultad;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import static pe.edu.lamolina.model.enums.TipoOficinaEnum.FAC;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.tramite.CursoDirigidoFacultad;
import pe.edu.lamolina.pivot.controller.general.oficina.OficinaService;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.FacultadDAO;
import pe.edu.lamolina.pivot.dao.general.ColaboradorDAO;
import pe.edu.lamolina.pivot.dao.tramite.CursoDirigidoFacultadDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class CursoDirigidoFacultadServiceImp implements CursoDirigidoFacultadService {

    @Autowired
    ColaboradorDAO colaboradorDAO;

    @Autowired
    FacultadDAO facultadDAO;

    @Autowired
    CursoDAO cursoDAO;

    @Autowired
    CursoDirigidoFacultadDAO cursoDirigidoFacultadDAO;

    @Autowired
    OficinaService oficinaService;

    @Override
    public List<Facultad> findByDocente(DataSessionPivot ds) {
        List<Facultad> lista = new ArrayList<>();
        List<Oficina> oficinasMain = oficinaService.allOficinasMainByPersona(ds.getPersona());

        for (Oficina oficina : oficinasMain) {
            if (oficina.getCodigoEnum() == OficinaEnum.OERA) {
                if (TipoOficinaEnum.FAC == FAC) {
                    lista.addAll(facultadDAO.all());
                }
            }
        }
        return lista;
    }

    @Override
    public List<CursoDirigidoFacultad> allByDocenteFacultadDynatable(Facultad facultad, DynatableFilter filter) {
        return cursoDirigidoFacultadDAO.allByDynatable(facultad, filter);
    }

    @Override
    public List<Curso> allCursoLikeParam(String parametro) {
        parametro = "%" + parametro.replaceAll(" ", "%") + "%";
        return cursoDAO.searchActivoLikeNombre(parametro);
    }

    @Override
    @Transactional
    public void save(CursoDirigidoFacultad cursoDirigidoFacultad, DataSessionPivot ds) {
        cursoDirigidoFacultad.setEstado(EstadoEnum.ACT.name());
        cursoDirigidoFacultad.setFechaRegistro(new Date());
        cursoDirigidoFacultad.setUserRegistro(ds.getUsuario());
        cursoDirigidoFacultadDAO.save(cursoDirigidoFacultad);
    }

}
