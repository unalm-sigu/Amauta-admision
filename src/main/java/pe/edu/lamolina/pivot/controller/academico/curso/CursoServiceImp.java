package pe.edu.lamolina.pivot.controller.academico.curso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ListsInspector;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.academico.NombreCursoDAO;
import pe.edu.lamolina.pivot.dao.general.IdiomaDAO;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.model.academico.NombreCurso;
import pe.edu.lamolina.pivot.model.general.Compania;
import pe.edu.lamolina.pivot.model.general.Idioma;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoCreditoEnum;

@Service
@Transactional
public class CursoServiceImp implements CursoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CursoDAO cursoDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    IdiomaDAO idiomaDAO;

    @Autowired
    NombreCursoDAO nombreCursoDAO;

    @Autowired
    DepartamentoAcademicoDAO departamentoAcademicoDAO;

    @Override
    public List<Curso> allByDynatable(DynatableFilter filter, List<DepartamentoAcademico> departamentos) {
        return cursoDAO.allByDynatable(filter, departamentos);
    }

    @Override
    @Transactional
    public void save(Curso curso, Usuario usuario) {
        ObjectUtil.eliminarAttrSinId(curso, "carrera");

        Curso cursoBD;
        if (curso.getId() == null) {
            cursoBD = this.saveCurso(curso);
        } else {
            cursoBD = this.updateCurso(curso);
        }

        if (curso.getIdIdioma() == null) {
            return;
        }

        List<NombreCurso> listaNombres = new ArrayList();
        for (int i = 0; i < curso.getIdIdioma().length; i++) {
            NombreCurso nombreCurso = new NombreCurso();
            nombreCurso.setCurso(curso);
            nombreCurso.setFechaRegistro(new Date());
            nombreCurso.setIdUserRegistro(usuario.getId());
            nombreCurso.setIdioma(new Idioma(curso.getIdIdioma()[i]));
            nombreCurso.setNombre(curso.getNombreIdioma()[i]);
            
            listaNombres.add(nombreCurso);
        }

        ListsInspector inspector = TypesUtil.analizeLists(cursoBD.getNombreCurso(), listaNombres, "idioma.id");

        List<NombreCurso> nuevos = inspector.getNewList();
        for (NombreCurso nuevo : nuevos) {
            nombreCursoDAO.save(nuevo);
        }

        List<NombreCurso> eliminables = inspector.getDeadList();
        for (NombreCurso eliminable : eliminables) {
            nombreCursoDAO.delete(eliminable);
        }

    }

    private Curso saveCurso(Curso curso) {
        curso.setCodigo(this.getCodigo(curso));
        curso.setEstado(EstadoEnum.CRE);
        if (curso.getTipoCredito().equals(TipoCreditoEnum.FIJO.name())) {
            curso.setCreditos(curso.getCreditos());
            curso.setCreditosVariables(null);
        } else {
            curso.setCreditosVariables(curso.getCreditos());
            curso.setCreditos(null);
        }
        cursoDAO.save(curso);
        return curso;
    }

    private Curso updateCurso(Curso curso) {
        Curso cursoBD = cursoDAO.find(curso.getId());
        cursoBD.setNombre(curso.getNombre());
        if (curso.getCarrera() != null) {
            cursoBD.setCarrera(curso.getCarrera());
        }
        if (curso.getTipoCurricula() != null) {
            cursoBD.setTipoCurricula(curso.getTipoCurricula());
        }
        cursoBD.setTipoCredito(curso.getTipoCreditoEnum());
        cursoBD.setHorasTeoria(curso.getHorasTeoria());
        cursoBD.setHorasPractica(curso.getHorasPractica());
        cursoBD.setHorasTeoriaVerano(curso.getHorasTeoriaVerano());
        cursoBD.setHorasPracticaVerano(curso.getHorasPracticaVerano());

        if (curso.getTipoCreditoEnum() == TipoCreditoEnum.FIJO) {
            cursoBD.setCreditos(curso.getCreditos());
            cursoBD.setCreditosVariables(null);

        } else {
            cursoBD.setCreditosVariables(curso.getCreditos());
            cursoBD.setCreditos(null);
        }
        cursoBD.setCoordinador(curso.getCoordinador());
        cursoDAO.update(cursoBD);

        cursoBD.setNombreCurso(nombreCursoDAO.allByCurso(curso));

        return cursoBD;

    }

    public String getCodigo(Curso curso) {
        DepartamentoAcademico dpto = departamentoAcademicoDAO.find(curso.getDepartamentoAcademico().getId());
        String curCodFacultad = dpto.getFacultad().getCodigoCurso();

        String codigo = curCodFacultad + curso.getNivel();
        Curso cursoBD = cursoDAO.findLastByCodigoFacultad(codigo.concat("%"));

        String codCurso = cursoBD.getCodigo();
        String numero = codCurso.substring(3);
        Integer correlativo = Integer.valueOf(numero) + 1;

        if (correlativo > 999) {
            codigo += NumberFormat.codigo(correlativo, 4);
        } else {
            codigo += NumberFormat.codigo(correlativo, 3);
        }
        return codigo;
    }

    @Override
    public Curso find(Long id) {
        Curso curso = cursoDAO.find(id);
        if (curso.getTipoCredito().equals(TipoCreditoEnum.VAR.name())) {
            curso.setCreditos(curso.getCreditosVariables());
        }
        List<NombreCurso> nombres = nombreCursoDAO.allByCurso(curso);
        if (nombres.size() > 0) {
            curso.setNombreCurso(nombres);
        } else {
            curso.setNombreCurso(new ArrayList());
        }
        return curso;
    }

    @Override
    @Transactional
    public void cambiarEstadoCurso(Curso curso) {
        Curso cursoBD = cursoDAO.find(curso.getId());
        if (cursoBD.getEstadoEnum() == EstadoEnum.ACT) {
            cursoBD.setEstado(EstadoEnum.INA);
            cursoBD.setFechaAnulacion(new Date());
            cursoBD.setMotivoAnulacion(curso.getMotivoAnulacion());

        } else {
            cursoBD.setEstado(EstadoEnum.ACT);
        }
        cursoDAO.update(cursoBD);
    }

    @Override
    public List<ModalidadEstudio> modalidadesEstudioPrePost(Compania cia) {
        return modalidadEstudioDAO.allPrePostgrado(cia);
    }

    @Override
    public List<Carrera> allByModalidadEstudioNombre(String codigoEstudio, String nombre) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        return carreraDAO.allByModalidadEstudioNombre(codigoEstudio, nombre);
    }

    @Override
    public List<Idioma> allIdiomas() {
        return idiomaDAO.all();
    }

}
