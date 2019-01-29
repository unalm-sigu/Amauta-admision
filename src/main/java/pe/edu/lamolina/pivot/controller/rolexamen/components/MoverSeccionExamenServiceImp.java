package pe.edu.lamolina.pivot.controller.rolexamen.components;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.DocenteRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoCursoMasivoEnum;
import pe.edu.lamolina.model.enums.RolExamenesEstadoEnum;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.rolexamen.AlumnoCursoMasivo;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoRegular;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.DocenteCursoMasivo;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionCursoMasivo;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.pivot.controller.rolexamen.cursomasivos.CursoMasivosService;
import pe.edu.lamolina.pivot.controller.rolexamen.gruporegular.GrupoRegularConnector;
import pe.edu.lamolina.pivot.controller.rolexamen.util.RolExamenesLogger;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoCursoMasivoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoGrupoEspecialDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoGrupoRegularDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.CursoMasivoExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.DocenteCursoMasivoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.GrupoHorasExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.LetraGrupoRegularDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionCursoMasivoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionGrupoEspecialDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionGrupoRegularDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class MoverSeccionExamenServiceImp implements MoverSeccionExamenService {

    @Autowired
    SeccionCursoMasivoDAO seccionCursoMasivoDAO;

    @Autowired
    SeccionGrupoRegularDAO seccionGrupoRegularDAO;

    @Autowired
    SeccionGrupoEspecialDAO seccionGrupoEspecialDAO;

    @Autowired
    LetraGrupoRegularDAO letraGrupoRegularDAO;

    @Autowired
    CursoMasivoExamenDAO cursoMasivoExamenDAO;

    @Autowired
    GrupoHorasExamenDAO grupoHorasExamenDAO;

    @Autowired
    AlumnoCursoMasivoDAO alumnoCursoMasivoDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Autowired
    GrupoRegularConnector grupoRegularConnector;

    @Autowired
    CursoMasivosService cursoMasivosService;

    @Autowired
    RolExamenesLogger rolExamenesLogger;

    @Autowired
    DocenteCursoMasivoDAO docenteCursoMasivoDAO;

    @Autowired
    AlumnoGrupoRegularDAO alumnoGrupoRegularDAO;

    @Autowired
    AlumnoGrupoEspecialDAO alumnoGrupoEspecialDAO;

    @Override
    public SeccionCursoMasivo findSeccionCursoMasivoBySeccion(Seccion seccion) {
        SeccionCursoMasivo seccionCursoMasivo = seccionCursoMasivoDAO.findBySeccion(seccion, SeccionRolExamenEstadoEnum.ACT);
        return seccionCursoMasivo;
    }

    @Override
    public SeccionGrupoRegular findSeccionGrupoRegularBySeccion(Seccion seccion) {
        SeccionGrupoRegular seccionGrupoRegular = seccionGrupoRegularDAO.findBySeccion(seccion, SeccionRolExamenEstadoEnum.ACT);
        return seccionGrupoRegular;
    }

    @Override
    public SeccionGrupoEspecial findSeccionGrupoEspecialBySeccion(Seccion seccion) {
        SeccionGrupoEspecial seccionGrupoEspecial = seccionGrupoEspecialDAO.findBySeccion(seccion, SeccionRolExamenEstadoEnum.ACT);
        return seccionGrupoEspecial;
    }

    @Override
    public List<CursoMasivoExamen> allActiveCursosMasivosByRolExamenes(RolExamenes rolExamenes) {
        List<CursoMasivoExamen> cursoMasivos = cursoMasivoExamenDAO.allByRolExamenes(rolExamenes, EstadoCursoMasivoEnum.ACT);
        return cursoMasivos;
    }

    @Override
    public List<LetraGrupoRegular> allLetrasGruposRegularesByRolExamenes(RolExamenes rolExamenes) {
        List<LetraGrupoRegular> letrasGruposGrupoRegulares = letraGrupoRegularDAO.allByRolExamenes(rolExamenes);
        return letrasGruposGrupoRegulares;
    }

    @Override
    public GrupoHorasExamen findGrupoHorasExamen(GrupoHorasExamen grupoHorasExamen) {
        return grupoHorasExamenDAO.find(grupoHorasExamen.getId());
    }

    private void checkNoPublicado(RolExamenes rol) {
        Assert.isTrue(rol.getEstadoEnum() != RolExamenesEstadoEnum.PUB, "El rol de exámenes ya ha sido publicado");
    }

    @Override
    @Transactional
    public void cambioHorarioExamenSeccion(CambioHorarioExamenSeccion cambioHorarioExamenSeccion, DataSessionPivot ds) {

        SeccionCursoMasivo seccionCursoMasivoOrigen = null;
        SeccionGrupoRegular seccionGrupoRegularOrigen = null;
        SeccionGrupoEspecial seccionGrupoEspecialOrigen = null;

        LetraGrupoRegular letraGrupoRegularDestino = null;
        CursoMasivoExamen cursoMasivoExamenDestino = null;

        if (cambioHorarioExamenSeccion.isTipoGrupMasivooOrigen()) {
            seccionCursoMasivoOrigen = seccionCursoMasivoDAO.find(cambioHorarioExamenSeccion.getIdSeccionRolExamenesOrigen());
            this.checkNoPublicado(seccionCursoMasivoOrigen.getCursoMasivoExamen().getRolExamenes());
        } else if (cambioHorarioExamenSeccion.isTipoGrupoRegularOrigen()) {
            seccionGrupoRegularOrigen = seccionGrupoRegularDAO.find(cambioHorarioExamenSeccion.getIdSeccionRolExamenesOrigen());
            this.checkNoPublicado(seccionGrupoRegularOrigen.getLetraGrupoRegular().getRolExamenes());
        } else if (cambioHorarioExamenSeccion.isTipoGrupoEspecialOrigen()) {
            seccionGrupoEspecialOrigen = seccionGrupoEspecialDAO.find(cambioHorarioExamenSeccion.getIdSeccionRolExamenesOrigen());
            this.checkNoPublicado(seccionGrupoEspecialOrigen.getRolExamenes());
        }

        if (cambioHorarioExamenSeccion.isTipoGrupRegularoDestino()) {
            letraGrupoRegularDestino = letraGrupoRegularDAO.find(cambioHorarioExamenSeccion.getIdTipoGrupoExamenDestino());
            if (cambioHorarioExamenSeccion.isTipoGrupMasivooOrigen()) {
                this.validarSeccionOrigenEstadoActivo(seccionCursoMasivoOrigen.isEstadoActivo());
                this.validarRolExamenesTraslado(seccionCursoMasivoOrigen.getCursoMasivoExamen().getRolExamenes());
                this.trasladarToLetraGrupoRegular(seccionCursoMasivoOrigen, letraGrupoRegularDestino, ds);
            } else if (cambioHorarioExamenSeccion.isTipoGrupoRegularOrigen()) {
                this.validarSeccionOrigenEstadoActivo(seccionGrupoRegularOrigen.isEstadoActivo());
                this.validarRolExamenesTraslado(seccionGrupoRegularOrigen.getLetraGrupoRegular().getRolExamenes());
                Assert.isFalse(seccionGrupoRegularOrigen.getLetraGrupoRegular().equals(letraGrupoRegularDestino), "Debe elegir una letra distinta.");
                this.trasladarToLetraGrupoRegular(seccionGrupoRegularOrigen, letraGrupoRegularDestino, ds);
            } else if (cambioHorarioExamenSeccion.isTipoGrupoEspecialOrigen()) {
                this.validarSeccionOrigenEstadoActivo(seccionGrupoEspecialOrigen.isEstadoActivo());
                this.validarRolExamenesTraslado(seccionGrupoEspecialOrigen.getRolExamenes());
                Assert.isFalse(seccionGrupoEspecialOrigen.getGrupoHorasExamen().equals(letraGrupoRegularDestino.getGrupoHorasExamen()), "Debe elegir una letra distinta.");
                this.trasladarToLetraGrupoRegular(seccionGrupoEspecialOrigen, letraGrupoRegularDestino, ds);
            }
        } else if (cambioHorarioExamenSeccion.isTipoGrupoMasivoDestino()) {
            cursoMasivoExamenDestino = cursoMasivoExamenDAO.find(cambioHorarioExamenSeccion.getIdTipoGrupoExamenDestino());
            if (cambioHorarioExamenSeccion.isTipoGrupMasivooOrigen()) {
                this.validarSeccionOrigenEstadoActivo(seccionCursoMasivoOrigen.isEstadoActivo());
                this.validarRolExamenesTraslado(cursoMasivoExamenDestino.getRolExamenes());
                Assert.isFalse(seccionCursoMasivoOrigen.getCursoMasivoExamen().equals(cursoMasivoExamenDestino), "El curso masivo destino debe ser distinto.");
                this.trasladarToSeccionCursoMasivo(seccionCursoMasivoOrigen, cursoMasivoExamenDestino, ds);
            } else if (cambioHorarioExamenSeccion.isTipoGrupoRegularOrigen()) {
                this.validarSeccionOrigenEstadoActivo(seccionGrupoRegularOrigen.isEstadoActivo());
                this.validarRolExamenesTraslado(seccionGrupoRegularOrigen.getLetraGrupoRegular().getRolExamenes());
                this.trasladarToSeccionCursoMasivo(seccionGrupoRegularOrigen, cursoMasivoExamenDestino, ds);
            } else if (cambioHorarioExamenSeccion.isTipoGrupoEspecialOrigen()) {
                this.validarSeccionOrigenEstadoActivo(seccionGrupoEspecialOrigen.isEstadoActivo());
                this.validarRolExamenesTraslado(seccionGrupoEspecialOrigen.getRolExamenes());
                this.trasladarToSeccionCursoMasivo(seccionGrupoEspecialOrigen, cursoMasivoExamenDestino, ds);
            }
        }
    }

    public void validarSeccionOrigenEstadoActivo(boolean estadoActivo) {
        Assert.isTrue(estadoActivo, "El estado de la seccion debe ser activo.");
    }

    public void validarRolExamenesTraslado(RolExamenes rolExamenes) {
        Assert.isFalse(rolExamenes.isSituacionConfigurarGrupoEspecial(), "Los grupos especiales deben estar configurados previamente.");
    }

    public void trasladarToLetraGrupoRegular(SeccionCursoMasivo seccionCursoMasivoOrigen, LetraGrupoRegular letraGrupoRegular, DataSessionPivot ds) {
        Seccion seccion = seccionDAO.find(seccionCursoMasivoOrigen.getSeccion());
        seccion = seccion.clone();
        List<Aula> aulas = Arrays.asList(seccion.getAula());

        List<DocenteSeccion> docentesPrincipalesOrigen = docenteSeccionDAO.allPrincipalesBySecciones(Arrays.asList(seccion));
        List<Docente> docentesOrigen = docentesPrincipalesOrigen.stream().map(x -> x.getDocente()).collect(Collectors.toList());

        List<AlumnoCursoMasivo> alumnosCursoMasivosOrigen = alumnoCursoMasivoDAO.allBySeccionCursosMasivos(seccionCursoMasivoOrigen, AlumnoRolExamenEstadoEnum.ACT);
        List<Alumno> alumnosOrigen = alumnosCursoMasivosOrigen.stream().map(x -> x.getAlumno()).collect(Collectors.toList());

        this.rolExamenesLogger.iniciarTrasladoToGrupoRegular();
        boolean validacionCursosMasivos = grupoRegularConnector.validarCursosMasivos(letraGrupoRegular.getGrupoHorasExamen(), docentesOrigen, aulas, alumnosOrigen);
        boolean validacionGruposRegulares = grupoRegularConnector.validarGrupoRegular(letraGrupoRegular.getGrupoHorasExamen(), alumnosOrigen, docentesOrigen, aulas);
        boolean validacionSeccionesEspeciales = grupoRegularConnector.validarGrupoEspecial(letraGrupoRegular.getGrupoHorasExamen(), docentesOrigen, aulas, alumnosOrigen);

        if (validacionCursosMasivos && validacionGruposRegulares && validacionSeccionesEspeciales) {
            SeccionGrupoRegular seccionGrupoRegular = grupoRegularConnector.crearObjectSeccionGrupoRegular(seccion, letraGrupoRegular, ds);
            for (AlumnoCursoMasivo alumnoCursoMasivo : alumnosCursoMasivosOrigen) {
                AlumnoGrupoRegular alumnoGrupoRegular = grupoRegularConnector.crearObjectAlumnoGrupoRegular(alumnoCursoMasivo.getAlumno(), seccionGrupoRegular, ds);
                seccionGrupoRegular.getAlumnosGruposRegulares().add(alumnoGrupoRegular);
            }
            seccionGrupoRegularDAO.save(seccionGrupoRegular);
            this.cambiarEstadoSeccionCursoMasivo(seccionCursoMasivoOrigen, alumnosCursoMasivosOrigen);
        } else {
            throw new PhobosException("Conflictos encontrados.");
        }
    }

    public void trasladarToLetraGrupoRegular(SeccionGrupoRegular seccionGrupoRegularOrigen, LetraGrupoRegular letraGrupoRegular, DataSessionPivot ds) {
        Seccion seccion = seccionDAO.find(seccionGrupoRegularOrigen.getSeccion());
        seccion = seccion.clone();
        List<Aula> aulas = Arrays.asList(seccion.getAula());

        List<DocenteSeccion> docentesPrincipalesOrigen = docenteSeccionDAO.allPrincipalesBySecciones(Arrays.asList(seccion));
        List<Docente> docentesOrigen = docentesPrincipalesOrigen.stream().map(x -> x.getDocente()).collect(Collectors.toList());

        List<AlumnoGrupoRegular> alumnosSeccionRegularOrigen = alumnoGrupoRegularDAO.allBySeccionGrupoRegularAndEstados(seccionGrupoRegularOrigen, AlumnoRolExamenEstadoEnum.ACT);
        List<Alumno> alumnosOrigen = alumnosSeccionRegularOrigen.stream().map(x -> x.getAlumno()).collect(Collectors.toList());

        this.rolExamenesLogger.iniciarTrasladoToGrupoRegular();

        boolean validacionCursosMasivos = grupoRegularConnector.validarCursosMasivos(letraGrupoRegular.getGrupoHorasExamen(), docentesOrigen, aulas, alumnosOrigen);
        boolean validacionGruposRegulares = grupoRegularConnector.validarGrupoRegular(letraGrupoRegular.getGrupoHorasExamen(), alumnosOrigen, docentesOrigen, aulas);
        boolean validacionSeccionesEspeciales = grupoRegularConnector.validarGrupoEspecial(letraGrupoRegular.getGrupoHorasExamen(), docentesOrigen, aulas, alumnosOrigen);

        if (validacionCursosMasivos && validacionGruposRegulares && validacionSeccionesEspeciales) {
            SeccionGrupoRegular seccionGrupoRegular = grupoRegularConnector.crearObjectSeccionGrupoRegular(seccion, letraGrupoRegular, ds);
            for (AlumnoGrupoRegular alumnoGrupoRegularEach : alumnosSeccionRegularOrigen) {
                AlumnoGrupoRegular alumnoGrupoRegular = grupoRegularConnector.crearObjectAlumnoGrupoRegular(alumnoGrupoRegularEach.getAlumno(), seccionGrupoRegular, ds);
                seccionGrupoRegular.getAlumnosGruposRegulares().add(alumnoGrupoRegular);
            }
            seccionGrupoRegularDAO.save(seccionGrupoRegular);
            this.cambiarEstadoSeccionGrupoRegular(seccionGrupoRegularOrigen, alumnosSeccionRegularOrigen);
        } else {
            throw new PhobosException("Conflictos encontrados.");
        }
    }

    public void trasladarToLetraGrupoRegular(SeccionGrupoEspecial seccionCursoMasivoOrigen, LetraGrupoRegular letraGrupoRegular, DataSessionPivot ds) {
        Seccion seccion = seccionDAO.find(seccionCursoMasivoOrigen.getSeccion());
        seccion = seccion.clone();
        List<Aula> aulas = Arrays.asList(seccion.getAula());

        List<DocenteSeccion> docentesPrincipalesOrigen = docenteSeccionDAO.allPrincipalesBySecciones(Arrays.asList(seccion));
        List<Docente> docentesOrigen = docentesPrincipalesOrigen.stream().map(x -> x.getDocente()).collect(Collectors.toList());

        List<AlumnoGrupoEspecial> alumnosSeccionEspecialOrigen = alumnoGrupoEspecialDAO.allBySeccionGrupoEspecialAndEstados(seccionCursoMasivoOrigen, AlumnoRolExamenEstadoEnum.ACT);
        List<Alumno> alumnosOrigen = alumnosSeccionEspecialOrigen.stream().map(x -> x.getAlumno()).collect(Collectors.toList());

        this.rolExamenesLogger.iniciarTrasladoToGrupoRegular();

        boolean validacionCursosMasivos = grupoRegularConnector.validarCursosMasivos(letraGrupoRegular.getGrupoHorasExamen(), docentesOrigen, aulas, alumnosOrigen);
        boolean validacionGruposRegulares = grupoRegularConnector.validarGrupoRegular(letraGrupoRegular.getGrupoHorasExamen(), alumnosOrigen, docentesOrigen, aulas);
        boolean validacionSeccionesEspeciales = grupoRegularConnector.validarGrupoEspecial(letraGrupoRegular.getGrupoHorasExamen(), docentesOrigen, aulas, alumnosOrigen);

        if (validacionCursosMasivos && validacionGruposRegulares && validacionSeccionesEspeciales) {
            SeccionGrupoRegular seccionGrupoRegular = grupoRegularConnector.crearObjectSeccionGrupoRegular(seccion, letraGrupoRegular, ds);
            for (AlumnoGrupoEspecial alumnoGrupoEspecialEach : alumnosSeccionEspecialOrigen) {
                AlumnoGrupoRegular alumnoGrupoRegular = grupoRegularConnector.crearObjectAlumnoGrupoRegular(alumnoGrupoEspecialEach.getAlumno(), seccionGrupoRegular, ds);
                seccionGrupoRegular.getAlumnosGruposRegulares().add(alumnoGrupoRegular);
            }
            seccionGrupoRegularDAO.save(seccionGrupoRegular);
            this.cambiarEstadoSeccionGrupoEspecial(seccionCursoMasivoOrigen, alumnosSeccionEspecialOrigen);
        } else {
            throw new PhobosException("Conflictos encontrados.");
        }
    }

    public void validaSeccionOrigen(Seccion seccion, RolExamenes rolExamenes) {
        CursoMasivoExamen cursoMasivoExamen = cursoMasivoExamenDAO.findByCursoAndRolExamenes(seccion.getGrupoSeccion().getCurso(), rolExamenes, EstadoCursoMasivoEnum.ACT);
        Assert.isNotNull(cursoMasivoExamen, "La seccion que desea mover a grupo masivo debe ser de un curso configurado como masivo.");
    }

    public void trasladarToSeccionCursoMasivo(SeccionCursoMasivo seccionCursoMasivoOrigen, CursoMasivoExamen cursoMasivoExamenDestino, DataSessionPivot ds) {
        Seccion seccion = seccionDAO.find(seccionCursoMasivoOrigen.getSeccion());
        this.validaSeccionOrigen(seccion, seccionCursoMasivoOrigen.getCursoMasivoExamen().getRolExamenes());
        seccion = seccion.clone();
        //  List<Aula> aulas = Arrays.asList(seccion.getAula());
        List<Aula> aulas = new ArrayList<Aula>();

        List<DocenteSeccion> docentesPrincipalesOrigen = docenteSeccionDAO.allPrincipalesBySecciones(Arrays.asList(seccion));
        List<Docente> docentesOrigen = docentesPrincipalesOrigen.stream().map(x -> x.getDocente()).collect(Collectors.toList());

        List<AlumnoCursoMasivo> alumnosCursoMasivosOrigen = alumnoCursoMasivoDAO.allBySeccionCursosMasivos(seccionCursoMasivoOrigen, AlumnoRolExamenEstadoEnum.ACT);
        List<Alumno> alumnosOrigen = alumnosCursoMasivosOrigen.stream().map(x -> x.getAlumno()).collect(Collectors.toList());

        this.rolExamenesLogger.iniciarTrasladoToCursoMasivo();
        boolean validacionCursosMasivos = cursoMasivosService.validateCruceCursosMasivos(seccionCursoMasivoOrigen.getCursoMasivoExamen(), alumnosOrigen, docentesOrigen, aulas);
        boolean validacionGruposRegulares = grupoRegularConnector.validarGrupoRegular(cursoMasivoExamenDestino.getGrupoHorasExamen(), alumnosOrigen, docentesOrigen, aulas);
        boolean validacionSeccionesEspeciales = grupoRegularConnector.validarGrupoEspecial(cursoMasivoExamenDestino.getGrupoHorasExamen(), docentesOrigen, aulas, alumnosOrigen);

        if (validacionCursosMasivos && validacionGruposRegulares && validacionSeccionesEspeciales) {
            DocenteCursoMasivo docenteCursoMasivo = new DocenteCursoMasivo();
            docenteCursoMasivo.setCursoMasivoExamen(cursoMasivoExamenDestino);
            docenteCursoMasivo.setDocente(docentesOrigen.get(0));
            docenteCursoMasivo.setFechaRegistro(ds.getFechaAccionAudit());
            docenteCursoMasivo.setSecciones(BigDecimal.ZERO.intValue());
            docenteCursoMasivo.setUserRegistro(ds.getUsuario());
            docenteCursoMasivo.setEstadoEnum(DocenteRolExamenEstadoEnum.ACT);
            docenteCursoMasivoDAO.save(docenteCursoMasivo);

            SeccionCursoMasivo seccionCursoMasivo = new SeccionCursoMasivo();
            seccionCursoMasivo.setCursoMasivoExamen(cursoMasivoExamenDestino);
            seccionCursoMasivo.setEstadoEnum(SeccionRolExamenEstadoEnum.ACT);
            seccionCursoMasivo.setSeccion(seccion);
            seccionCursoMasivo.setFechaRegistro(new Date());
            seccionCursoMasivo.setUserRegistro(ds.getUsuario());

            seccionCursoMasivoDAO.save(seccionCursoMasivo);

            for (AlumnoCursoMasivo alumnoCursoMasivoEach : alumnosCursoMasivosOrigen) {
                Alumno alumno = alumnoCursoMasivoEach.getAlumno();
                AlumnoCursoMasivo alumnoCursoMasivo = new AlumnoCursoMasivo();
                alumnoCursoMasivo.setAlumno(alumno);
                alumnoCursoMasivo.setCursoMasivoExamen(cursoMasivoExamenDestino);
                alumnoCursoMasivo.setSeccionCursoMasivo(seccionCursoMasivo);
                alumnoCursoMasivo.setEstadoEnum(AlumnoRolExamenEstadoEnum.ACT);
                alumnoCursoMasivo.setFechaRegistro(new Date());
                alumnoCursoMasivo.setUserRegistro(ds.getUsuario());
                alumnoCursoMasivoDAO.save(alumnoCursoMasivo);
            }
            this.cambiarEstadoSeccionCursoMasivo(seccionCursoMasivoOrigen, alumnosCursoMasivosOrigen);
        } else {
            throw new PhobosException("Conflictos encontrados.");
        }

    }

    public void trasladarToSeccionCursoMasivo(SeccionGrupoRegular seccionGrupoRegularOrigen, CursoMasivoExamen cursoMasivoExamenDestino, DataSessionPivot ds) {
        Seccion seccion = seccionDAO.find(seccionGrupoRegularOrigen.getSeccion());
        this.validaSeccionOrigen(seccion, seccionGrupoRegularOrigen.getLetraGrupoRegular().getRolExamenes());
        seccion = seccion.clone();
        //  List<Aula> aulas = Arrays.asList(seccion.getAula());
        List<Aula> aulas = new ArrayList<Aula>();

        List<DocenteSeccion> docentesPrincipalesOrigen = docenteSeccionDAO.allPrincipalesBySecciones(Arrays.asList(seccion));
        List<Docente> docentesOrigen = docentesPrincipalesOrigen.stream().map(x -> x.getDocente()).collect(Collectors.toList());

        List<AlumnoGrupoRegular> alumnosGrupoRegularOrigen = alumnoGrupoRegularDAO.allBySeccionGrupoRegularAndEstados(seccionGrupoRegularOrigen, AlumnoRolExamenEstadoEnum.ACT);
        List<Alumno> alumnosOrigen = alumnosGrupoRegularOrigen.stream().map(x -> x.getAlumno()).collect(Collectors.toList());

        this.rolExamenesLogger.iniciarTrasladoToCursoMasivo();
        boolean validacionCursosMasivos = cursoMasivosService.validateCruceCursosMasivos(cursoMasivoExamenDestino, alumnosOrigen, docentesOrigen, aulas);
        boolean validacionGruposRegulares = grupoRegularConnector.validarGrupoRegular(cursoMasivoExamenDestino.getGrupoHorasExamen(), alumnosOrigen, docentesOrigen, aulas);
        boolean validacionSeccionesEspeciales = grupoRegularConnector.validarGrupoEspecial(cursoMasivoExamenDestino.getGrupoHorasExamen(), docentesOrigen, aulas, alumnosOrigen);

        if (validacionCursosMasivos && validacionGruposRegulares && validacionSeccionesEspeciales) {
            DocenteCursoMasivo docenteCursoMasivo = new DocenteCursoMasivo();
            docenteCursoMasivo.setCursoMasivoExamen(cursoMasivoExamenDestino);
            docenteCursoMasivo.setDocente(docentesOrigen.get(0));
            docenteCursoMasivo.setFechaRegistro(ds.getFechaAccionAudit());
            docenteCursoMasivo.setSecciones(BigDecimal.ZERO.intValue());
            docenteCursoMasivo.setUserRegistro(ds.getUsuario());
            docenteCursoMasivo.setEstadoEnum(DocenteRolExamenEstadoEnum.ACT);
            docenteCursoMasivoDAO.save(docenteCursoMasivo);

            SeccionCursoMasivo seccionCursoMasivo = new SeccionCursoMasivo();
            seccionCursoMasivo.setCursoMasivoExamen(cursoMasivoExamenDestino);
            seccionCursoMasivo.setEstadoEnum(SeccionRolExamenEstadoEnum.ACT);
            seccionCursoMasivo.setSeccion(seccion);
            seccionCursoMasivo.setFechaRegistro(new Date());
            seccionCursoMasivo.setUserRegistro(ds.getUsuario());

            seccionCursoMasivoDAO.save(seccionCursoMasivo);

            for (AlumnoGrupoRegular alumnoGrupoRegularEach : alumnosGrupoRegularOrigen) {
                Alumno alumno = alumnoGrupoRegularEach.getAlumno();
                AlumnoCursoMasivo alumnoCursoMasivo = new AlumnoCursoMasivo();
                alumnoCursoMasivo.setAlumno(alumno);
                alumnoCursoMasivo.setCursoMasivoExamen(cursoMasivoExamenDestino);
                alumnoCursoMasivo.setSeccionCursoMasivo(seccionCursoMasivo);
                alumnoCursoMasivo.setEstadoEnum(AlumnoRolExamenEstadoEnum.ACT);
                alumnoCursoMasivo.setFechaRegistro(new Date());
                alumnoCursoMasivo.setUserRegistro(ds.getUsuario());
                alumnoCursoMasivoDAO.save(alumnoCursoMasivo);
            }
            this.cambiarEstadoSeccionGrupoRegular(seccionGrupoRegularOrigen, alumnosGrupoRegularOrigen);
        } else {
            throw new PhobosException("Conflictos encontrados.");
        }
    }

    public void trasladarToSeccionCursoMasivo(SeccionGrupoEspecial seccionGrupoEspecialOrigen, CursoMasivoExamen cursoMasivoExamenDestino, DataSessionPivot ds) {
        Seccion seccion = seccionDAO.find(seccionGrupoEspecialOrigen.getSeccion());
        this.validaSeccionOrigen(seccion, seccionGrupoEspecialOrigen.getRolExamenes());
        seccion = seccion.clone();
        //  List<Aula> aulas = Arrays.asList(seccion.getAula());
        List<Aula> aulas = new ArrayList<Aula>();

        List<DocenteSeccion> docentesPrincipalesOrigen = docenteSeccionDAO.allPrincipalesBySecciones(Arrays.asList(seccion));
        List<Docente> docentesOrigen = docentesPrincipalesOrigen.stream().map(x -> x.getDocente()).collect(Collectors.toList());

        List<AlumnoGrupoEspecial> alumnosGrupoEspecialOrigen = alumnoGrupoEspecialDAO.allBySeccionGrupoEspecialAndEstados(seccionGrupoEspecialOrigen, AlumnoRolExamenEstadoEnum.ACT);
        List<Alumno> alumnosOrigen = alumnosGrupoEspecialOrigen.stream().map(x -> x.getAlumno()).collect(Collectors.toList());

        this.rolExamenesLogger.iniciarTrasladoToCursoMasivo();
        boolean validacionCursosMasivos = cursoMasivosService.validateCruceCursosMasivos(cursoMasivoExamenDestino, alumnosOrigen, docentesOrigen, aulas);
        boolean validacionGruposRegulares = grupoRegularConnector.validarGrupoRegular(cursoMasivoExamenDestino.getGrupoHorasExamen(), alumnosOrigen, docentesOrigen, aulas);
        boolean validacionSeccionesEspeciales = grupoRegularConnector.validarGrupoEspecial(cursoMasivoExamenDestino.getGrupoHorasExamen(), docentesOrigen, aulas, alumnosOrigen);

        if (validacionCursosMasivos && validacionGruposRegulares && validacionSeccionesEspeciales) {
            DocenteCursoMasivo docenteCursoMasivo = new DocenteCursoMasivo();
            docenteCursoMasivo.setCursoMasivoExamen(cursoMasivoExamenDestino);
            docenteCursoMasivo.setDocente(docentesOrigen.get(0));
            docenteCursoMasivo.setFechaRegistro(ds.getFechaAccionAudit());
            docenteCursoMasivo.setSecciones(BigDecimal.ZERO.intValue());
            docenteCursoMasivo.setUserRegistro(ds.getUsuario());
            docenteCursoMasivo.setEstadoEnum(DocenteRolExamenEstadoEnum.ACT);
            docenteCursoMasivoDAO.save(docenteCursoMasivo);

            SeccionCursoMasivo seccionCursoMasivo = new SeccionCursoMasivo();
            seccionCursoMasivo.setCursoMasivoExamen(cursoMasivoExamenDestino);
            seccionCursoMasivo.setEstadoEnum(SeccionRolExamenEstadoEnum.ACT);
            seccionCursoMasivo.setSeccion(seccion);
            seccionCursoMasivo.setFechaRegistro(new Date());
            seccionCursoMasivo.setUserRegistro(ds.getUsuario());

            seccionCursoMasivoDAO.save(seccionCursoMasivo);

            for (AlumnoGrupoEspecial alumnoGrupoEspecialEach : alumnosGrupoEspecialOrigen) {
                Alumno alumno = alumnoGrupoEspecialEach.getAlumno();
                AlumnoCursoMasivo alumnoCursoMasivo = new AlumnoCursoMasivo();
                alumnoCursoMasivo.setAlumno(alumno);
                alumnoCursoMasivo.setCursoMasivoExamen(cursoMasivoExamenDestino);
                alumnoCursoMasivo.setSeccionCursoMasivo(seccionCursoMasivo);
                alumnoCursoMasivo.setEstadoEnum(AlumnoRolExamenEstadoEnum.ACT);
                alumnoCursoMasivo.setFechaRegistro(new Date());
                alumnoCursoMasivo.setUserRegistro(ds.getUsuario());
                alumnoCursoMasivoDAO.save(alumnoCursoMasivo);
            }
            this.cambiarEstadoSeccionGrupoEspecial(seccionGrupoEspecialOrigen, alumnosGrupoEspecialOrigen);
        } else {
            throw new PhobosException("Conflictos encontrados.");
        }
    }

    public void cambiarEstadoSeccionCursoMasivo(SeccionCursoMasivo seccionCursoMasivo, List<AlumnoCursoMasivo> alumnosCursoMasivosOrigen) {
        SeccionCursoMasivo seccionCursoMasivoUpd = new SeccionCursoMasivo(seccionCursoMasivo.getId());
        seccionCursoMasivoUpd.setEstadoEnum(SeccionRolExamenEstadoEnum.TRA);
        seccionCursoMasivoDAO.updateEstado(seccionCursoMasivoUpd);

        for (AlumnoCursoMasivo alumnosCursoMasivo : alumnosCursoMasivosOrigen) {
            AlumnoCursoMasivo alumnoCursoMasivoUpd = new AlumnoCursoMasivo(alumnosCursoMasivo.getId());
            alumnoCursoMasivoUpd.setEstadoEnum(AlumnoRolExamenEstadoEnum.TRA);
            alumnoCursoMasivoDAO.updateEstado(alumnosCursoMasivo);
        }
        //todo remove aula and docente from tablas de masivos
    }

    public void cambiarEstadoSeccionGrupoRegular(SeccionGrupoRegular seccionGrupoRegular, List<AlumnoGrupoRegular> alumnoGrupoRegulars) {
        SeccionGrupoRegular seccionGrupoRegularUpd = new SeccionGrupoRegular(seccionGrupoRegular.getId());
        seccionGrupoRegularUpd.setEstadoEnum(SeccionRolExamenEstadoEnum.TRA);
        seccionGrupoRegularDAO.updateEstado(seccionGrupoRegularUpd);

        for (AlumnoGrupoRegular alumnoGrupoRegular : alumnoGrupoRegulars) {
            AlumnoCursoMasivo alumnoCursoMasivoUpd = new AlumnoCursoMasivo(alumnoGrupoRegular.getId());
            alumnoCursoMasivoUpd.setEstadoEnum(AlumnoRolExamenEstadoEnum.TRA);
            alumnoGrupoRegularDAO.updateEstado(alumnoGrupoRegular);
        }
        //todo remove aula and docente from tablas de masivos
    }

    public void cambiarEstadoSeccionGrupoEspecial(SeccionGrupoEspecial seccionGrupoEspecial, List<AlumnoGrupoEspecial> alumnosGrupoEspecial) {
        SeccionGrupoEspecial seccionGrupoEspecialUpd = new SeccionGrupoEspecial(seccionGrupoEspecial.getId());
        seccionGrupoEspecialUpd.setEstadoEnum(SeccionRolExamenEstadoEnum.TRA);
        seccionGrupoEspecialDAO.updateEstado(seccionGrupoEspecialUpd);

        for (AlumnoGrupoEspecial alumnoGrupoEspecial : alumnosGrupoEspecial) {
            AlumnoGrupoEspecial alumnoGrupoEspecialUpd = new AlumnoGrupoEspecial(alumnoGrupoEspecial.getId());
            alumnoGrupoEspecialUpd.setEstadoEnum(AlumnoRolExamenEstadoEnum.TRA);
            alumnoGrupoEspecialDAO.updateEstado(alumnoGrupoEspecialUpd);
        }
        //todo remove aula and docente from tablas de masivos
    }

}
