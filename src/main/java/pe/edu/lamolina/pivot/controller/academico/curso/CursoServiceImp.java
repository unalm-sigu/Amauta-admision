package pe.edu.lamolina.pivot.controller.academico.curso;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.NombreCurso;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.EPG;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;
import pe.edu.lamolina.model.enums.TipoCreditoEnum;
import pe.edu.lamolina.model.enums.TipoCurriculaEnum;
import pe.edu.lamolina.model.enums.TipoCursoEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.general.TipoCarpeta;
import pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.reporte.dto.CantidadMatriculadosDTO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.academico.NombreCursoDAO;
import pe.edu.lamolina.pivot.dao.general.IdiomaDAO;
import pe.edu.lamolina.pivot.dao.general.TipoCarpetaDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

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

    @Autowired
    DocenteDAO docenteDAO;

    @Autowired
    TipoCarpetaDAO tipoCarpetaDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Override
    public List<Curso> allByDynatable(DynatableFilter filter, List<DepartamentoAcademico> departamentos, CicloAcademico cicloAcademico) {
        logger.debug("size dps {}", departamentos.size());
        List<Curso> cursos = cursoDAO.allByDynatable(filter, departamentos);
        List<CantidadMatriculadosDTO> cantidadMatriculados = matriculaSeccionDAO.cantidadMatriculadosPorCurso(cursos, cicloAcademico, EstadoMatriculaEnum.MAT);
        for (Curso curso : cursos) {
            CantidadMatriculadosDTO cantidadPorCurso = cantidadMatriculados.stream()
                    .filter(x -> x.getCursoId().equals(curso.getId()))
                    .findFirst().orElse(null);
            curso.setMatriculados(cantidadPorCurso == null ? 0 : cantidadPorCurso.getCantidad().intValue());
        }
        return cursos;
    }

    @Override
    @Transactional
    public Curso save(Curso curso, DataSessionPivot ds) {
        Curso cursoBD;
        if (curso.getId() == null) {
            cursoBD = this.saveCurso(curso, ds);
        } else {
            cursoBD = this.updateCurso(curso, ds);
        }
        return cursoBD;
    }

    private Curso saveCurso(Curso curso, DataSessionPivot ds) {
        validarDatosCurso(curso);

        curso.setCodigo(this.getCodigo(curso));
        curso.setCodigoAnterior1(null);
        curso.setEstadoEnum(EstadoEnum.CRE);
        curso.setUserRegsitro(ds.getUsuario());
        curso.setFechaRegistro(new Date());
        cursoDAO.save(curso);

        return curso;
    }

    private Curso updateCurso(Curso curso, DataSessionPivot ds) {
        validarDatosCurso(curso);
        Curso cursoBD = cursoDAO.find(curso.getId());
        cursoBD.setNombre(curso.getNombre());
        if (curso.getCarrera() != null && cursoBD.getCarrera() == null) {
            cursoBD.setCarrera(curso.getCarrera());
        }
        if (curso.getModalidadEstudio() != null && cursoBD.getModalidadEstudio() == null) {
            cursoBD.setModalidadEstudio(curso.getModalidadEstudio());
        }
        if (curso.getDepartamentoAcademico() != null && cursoBD.getDepartamentoAcademico() == null) {
            cursoBD.setDepartamentoAcademico(curso.getDepartamentoAcademico());
        }
        if (!StringUtils.isEmpty(curso.getTipoCurricula()) && cursoBD.getTipoCurriculaEnum() == null) {
            cursoBD.setTipoCurricula(curso.getTipoCurricula());
        }
        if (curso.getNivel() != null && cursoBD.getNivel() == null) {
            cursoBD.setNivel(curso.getNivel());
        }

        cursoBD.setTipoCreditoEnum(curso.getTipoCreditoEnum());
        cursoBD.setTipoCursoEnum(curso.getTipoCursoEnum());
        cursoBD.setHorasTeoria(curso.getHorasTeoria());
        cursoBD.setHorasPractica(curso.getHorasPractica());
        cursoBD.setHorasTeoriaVerano(curso.getHorasTeoriaVerano());
        cursoBD.setHorasPracticaVerano(curso.getHorasPracticaVerano());
        cursoBD.setNoEncuestar(curso.getNoEncuestar());
        cursoBD.setNoCargaAdicional(curso.getNoCargaAdicional());
        cursoBD.setCreditosPractica(curso.getCreditosPractica());
        cursoBD.setCreditosTeoria(curso.getCreditosTeoria());
        cursoBD.setCreditos(curso.getCreditos());
        cursoBD.setCreditosVariables(curso.getCreditosVariables());
        cursoBD.setCoordinador(curso.getCoordinador());
        cursoBD.setTipoCarpetaPractica(curso.getTipoCarpetaPractica());
        cursoBD.setTipoCarpetaTeoria(curso.getTipoCarpetaTeoria());
        cursoBD.setUserModificacion(ds.getUsuario());
        cursoBD.setFechaModificacion(new Date());
        cursoDAO.update(cursoBD);

        return cursoBD;

    }

    private String getCodigo(Curso curso) {
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

    private void validarDatosCurso(Curso curso) {
        ObjectUtil.eliminarAttrSinId(curso);

        curso.setNoEncuestar(curso.getNoEncuestar() == null ? false : curso.getNoEncuestar());
        curso.setNoCargaAdicional(curso.getNoCargaAdicional() == null ? false : curso.getNoCargaAdicional());

        Assert.isFalse(curso.getNombre() == null, "No ingresó el nombre");
        String nombre = curso.getNombre().trim();
        nombre = nombre.replaceAll(" +", " ");
        curso.setNombre(nombre);
        Assert.isNotBlank(curso.getNombre(), "No ingresó el nombre");

        Assert.isFalse(curso.getNombre().equals(curso.getNombre().toUpperCase()), "El nombre no debe ser totalmente en mayúsculas");

        if (curso.getTipoCurriculaEnum() == TipoCurriculaEnum.REG) {
            Assert.isTrue(curso.getTipoCreditoEnum() != null, "Debe indicar el tipo de creditaje del curso");
            if (curso.getTipoCreditoEnum() == TipoCreditoEnum.FIJO) {
                Assert.isTrue(curso.getCreditos() != null, "Debe indicar los créditos del curso");
                Assert.isTrue(curso.getCreditos() > 0, "Debe indicar un creditaje mayor a CERO");
                Assert.isTrue(curso.getCreditosPractica() != null, "Debe indicar los créditos de práctica");
                Assert.isTrue(curso.getCreditosTeoria() != null, "Debe indicar los créditos de teoría");

                Assert.isTrue(curso.getTipoCursoEnum() != TipoCursoEnum.NINGUNO, "Debe indicar el tipo de dictado");
                if (curso.getTipoCursoEnum() == TipoCursoEnum.TEO || curso.getTipoCursoEnum() == TipoCursoEnum.TEOPRA) {
                    Assert.isTrue(curso.getCreditosTeoria() > 0, "Los créditos de la teoría debe ser mayor a CERO");
                }
                if (curso.getTipoCursoEnum() == TipoCursoEnum.PRA || curso.getTipoCursoEnum() == TipoCursoEnum.TEOPRA) {
                    Assert.isTrue(curso.getCreditosPractica() > 0, "Los créditos de la práctica debe ser mayor a CERO");
                }

            } else {
                Assert.isTrue(curso.getCreditosVariables() != null, "Debe indicar los créditos del curso");
                Assert.isTrue(curso.getCreditosVariables() > 0, "Debe indicar un creditaje mayor a CERO");
            }

        } else if (curso.getTipoCurriculaEnum() == TipoCurriculaEnum.ADIC) {
            curso.setCreditos(0);
            curso.setCreditosTeoria(0);
            curso.setCreditosPractica(0);
            curso.setCreditosVariables(0);

        } else {
            curso.setCreditos(null);
            curso.setCreditosTeoria(null);
            curso.setCreditosPractica(null);
            curso.setCreditosVariables(null);
        }

        if (Arrays.asList(TipoCurriculaEnum.REG, TipoCurriculaEnum.ADIC).contains(curso.getTipoCurriculaEnum())) {
            Assert.isTrue(curso.getHorasPractica() != null, "Debe indicar las horas de la práctica");
            Assert.isTrue(curso.getHorasTeoria() != null, "Debe indicar las horas de la teoría");
            Assert.isTrue(curso.getHorasPracticaVerano() != null, "Debe indicar las horas de la práctica en verano");
            Assert.isTrue(curso.getHorasTeoriaVerano() != null, "Debe indicar las horas de la teoría en verano");

            if (curso.getTipoCursoEnum() == TipoCursoEnum.TEO) {
                Assert.isTrue(curso.getHorasPractica() == 0, "Las horas de la práctica debe ser igual a CERO");
            }
            if (curso.getTipoCursoEnum() == TipoCursoEnum.PRA) {
                Assert.isTrue(curso.getHorasTeoria() == 0, "Las horas de la teoría debe ser igual a CERO");
            }
            if (curso.getTipoCursoEnum() == TipoCursoEnum.TEOPRA) {
                if (curso.getHorasTeoria() == 0 && curso.getHorasPractica() == 0) {
                } else {
                    Assert.isTrue(curso.getHorasPractica() > 0, "Las horas de la práctica debe ser mayor CERO");
                    Assert.isTrue(curso.getHorasTeoria() > 0, "Las horas de la teoría debe ser mayor a CERO");
                }
            }
            Assert.isTrue(curso.getNivel() != null, "Debe indicar el nivel del curso");
            Assert.isTrue(curso.getDepartamentoAcademico() != null, "Debe indicar el departamento académico del curso");
            Assert.isTrue(curso.getModalidadEstudio() != null, "Debe indicar la modalidad de estudio");
            Assert.isTrue(Arrays.asList(EPG, PRE).contains(curso.getModalidadEstudio().getCodigoEnum()), "Solo se aceptan las modalidades de pregrado y posgrado");
            if (curso.getModalidadEstudio().getCodigoEnum() == ModalidadEstudioEnum.EPG) {
                Assert.isTrue(curso.getCarrera() != null, "Debe indicar la especialidad de posgrado del curso");
                Assert.isTrue(curso.getNivel() >= 6, "El nivel del curso debe ser mayor o igual a SEIS");
            } else {
                Assert.isTrue(curso.getNivel() < 7, "El nivel del curso debe ser menor o igual a SEIS");
            }

        } else {
            curso.setHorasTeoria(null);
            curso.setHorasTeoriaVerano(null);
            curso.setHorasPractica(null);
            curso.setHorasPracticaVerano(null);
        }
    }

    @Override
    public Curso find(Long id) {
        Curso curso = cursoDAO.find(id);
        if (!StringUtils.isBlank(curso.getTipoCredito()) && curso.getTipoCredito().equals(TipoCreditoEnum.VAR.name())) {
            curso.setCreditos(curso.getCreditosVariables());
        }

        List<NombreCurso> nombres = nombreCursoDAO.allByCurso(curso);
        curso.setNombreCurso(nombres);

        return curso;
    }

    @Override
    @Transactional
    public void cambiarEstadoCurso(Curso curso) {
        Curso cursoBD = cursoDAO.find(curso.getId());
        if (cursoBD.getEstadoEnum() == EstadoEnum.ACT) {
            cursoBD.setEstadoEnum(EstadoEnum.INA);
            cursoBD.setFechaAnulacion(new Date());
            cursoBD.setMotivoAnulacion(curso.getMotivoAnulacion());

        } else {
            cursoBD.setEstadoEnum(EstadoEnum.ACT);
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

    @Override
    public List<DepartamentoAcademico> allDepartamentos(Compania cia) {
        return departamentoAcademicoDAO.allByCompania(cia);
    }

    @Override
    public List<Carrera> allCarrerasByPostgrado() {
        return carreraDAO.allByModalidadEnum(ModalidadEstudioEnum.EPG);
    }

    @Override
    public List<Docente> allDocentesByDepartamento(String nombre, DepartamentoAcademico departamento) {
        return docenteDAO.allByNombreDepartamento(nombre, departamento, 15);
    }

    @Override
    @Transactional
    public NombreCurso saveIdioma(NombreCurso nombreCurso, DataSessionPivot ds) {
        Idioma idioma = nombreCurso.getIdioma();
        Assert.isNotNull(idioma, "No ha indicado el idioma del nombre del curso");
        Assert.isNotBlank(nombreCurso.getNombre(), "No ha indicado la traducción al otro idioma");

        List<NombreCurso> nombresBD = nombreCursoDAO.allByCurso(nombreCurso.getCurso());
        Map<Long, NombreCurso> mapNombres = TypesUtil.convertListToMap("idioma.id", nombresBD);
        NombreCurso nombreIdiomaExiste = mapNombres.get(idioma.getId());
        Assert.isNull(nombreIdiomaExiste, "Ya existe un nombre en este idioma para este curso");

        nombreCurso.setUserRegistro(ds.getUsuario());
        nombreCurso.setFechaRegistro(new Date());
        nombreCursoDAO.save(nombreCurso);

        return nombreCurso;
    }

    @Override
    @Transactional
    public NombreCurso updateIdioma(NombreCurso nombreCurso, DataSessionPivot ds) {
        Assert.isNotBlank(nombreCurso.getNombre(), "No ha indicado la traducción al otro idioma");

        NombreCurso nombresBD = nombreCursoDAO.find(nombreCurso.getId());
        Assert.isNotNull(nombresBD, "Este registro no existe en la base de datos");

        nombresBD.setUserRegistro(ds.getUsuario());
        nombresBD.setFechaRegistro(new Date());
        nombresBD.setNombre(nombreCurso.getNombre());
        nombreCursoDAO.update(nombresBD);

        return nombresBD;
    }

    @Override
    @Transactional
    public void deleteIdioma(NombreCurso nombreCurso, DataSessionPivot ds) {
        NombreCurso nombresBD = nombreCursoDAO.find(nombreCurso.getId());
        Assert.isNotNull(nombresBD, "Este registro ya no existe en la base de datos");
        nombreCursoDAO.delete(nombresBD);
    }

    @Override
    public List<TipoCarpeta> allTiposCarpeta() {
        return tipoCarpetaDAO.all();
    }

    @Override
    public List<MatriculaSeccion> allMatriculasSecciones(List<Curso> curso, CicloAcademico cicloAcademico) {
        List<MatriculaSeccion> matriculasSecciones = matriculaSeccionDAO.allByCurso(curso, cicloAcademico, new String[]{"sec.codigo2", "per.paterno", "per.materno", "per.nombres"}, EstadoMatriculaEnum.MAT);

        return matriculasSecciones;
    }

    @Override
    public List<DocenteSeccion> allDocenteSeccionPrincipalesBySecciones(List<Seccion> secciones) {
        return docenteSeccionDAO.allPrincipalesBySecciones(secciones);
    }
}
