package pe.edu.lamolina.amauta.controller.rolexamen.components.cambiaraulaexamen;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoRegular;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.amauta.controller.rolexamen.components.CambiarAula;
import pe.edu.lamolina.amauta.controller.rolexamen.gruporegular.GrupoRegularConnector;
import pe.edu.lamolina.amauta.controller.rolexamen.rolexamenes.RolExamenesService;
import pe.edu.lamolina.amauta.controller.rolexamen.util.RolExamenesLogger;
import pe.edu.lamolina.amauta.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.SeccionDAO;
import pe.edu.lamolina.amauta.dao.general.AulaDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.amauta.dao.rolexamen.AlumnoGrupoEspecialDAO;
import pe.edu.lamolina.amauta.dao.rolexamen.AlumnoGrupoRegularDAO;
import pe.edu.lamolina.amauta.dao.rolexamen.LetraGrupoRegularDAO;
import pe.edu.lamolina.amauta.dao.rolexamen.SeccionGrupoEspecialDAO;
import pe.edu.lamolina.amauta.dao.rolexamen.SeccionGrupoRegularDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = false)
public class CambiarAulaExamenServiceImp implements CambiarAulaExamenService {

    @Autowired
    RolExamenesLogger rolExamenesLogger;

    @Autowired
    SeccionGrupoRegularDAO seccionGrupoRegularDAO;

    @Autowired
    SeccionGrupoEspecialDAO seccionGrupoEspecialDAO;

    @Autowired
    AulaDAO aulaDAO;

    @Autowired
    GrupoRegularConnector grupoRegularConnector;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Autowired
    AlumnoGrupoRegularDAO alumnoGrupoRegularDAO;

    @Autowired
    LetraGrupoRegularDAO letraGrupoRegularDAO;

    @Autowired
    RolExamenesService rolExamenesService;

    @Autowired
    HorarioAulaDAO horarioAulaDAO;

    @Autowired
    AlumnoGrupoEspecialDAO alumnoGrupoEspecialDAO;

    @Override
    public SeccionGrupoEspecial findSeccionGrupoEspecialBySeccionRolExamenes(Seccion seccion, RolExamenes rol) {
        return seccionGrupoEspecialDAO.findByRolExamanesSeccion(rol, seccion, SeccionRolExamenEstadoEnum.ACT);
    }

    @Override
    public SeccionGrupoRegular findSeccionGrupoRegularBySeccionRolExamenes(Seccion seccion, RolExamenes rol) {
        return seccionGrupoRegularDAO.findByRolExamenesSeccion(rol, seccion, SeccionRolExamenEstadoEnum.ACT);
    }

    @Override
    public List<Aula> allActivesAulasOeraForSeccion(Seccion seccion) {
        seccion = seccionDAO.find(seccion.getId());
        List<Aula> aulasOera = aulaDAO.allByOficinaSupervisora(OficinaEnum.OERA, EstadoEnum.ACT);
        int matriculados = seccion.getMatriculados();
        aulasOera.removeIf(x -> x.getAforo() == null);
        aulasOera.removeIf(x -> x.getAforo() < matriculados);
        return aulasOera;
    }

    @Override
    @Transactional
    public void cambiarAulaExamen(CambiarAula cambiarAula, DataSessionPivot ds) {
        SeccionGrupoRegular seccionGrupoRegularOrigen = null;
        SeccionGrupoEspecial seccionGrupoEspecialOrigen = null;

        Aula aulaDestino = aulaDAO.find(cambiarAula.getIdAulaDestino());
        List<HorarioAula> horariosAulasBySeccionRol = null;
        List<Alumno> alumnosOrigen = null;
        LetraGrupoRegular letraGrupoRegular = null;

        this.rolExamenesLogger.iniciarTrasladoToGrupoRegular();

        if (cambiarAula.isTipoGrupoRegularOrigen()) {
            seccionGrupoRegularOrigen = seccionGrupoRegularDAO.find(cambiarAula.getIdSeccionRolExamenesOrigen());
            //this.checkNoPublicado(seccionGrupoRegularOrigen.getLetraGrupoRegular().getRolExamenes());
            horariosAulasBySeccionRol = horarioAulaDAO.allBySeccionGrupoRegular(seccionGrupoRegularOrigen);
            List<AlumnoGrupoRegular> alumnosSeccionRegularOrigen = alumnoGrupoRegularDAO.allBySeccionGrupoRegularAndEstados(seccionGrupoRegularOrigen, AlumnoRolExamenEstadoEnum.ACT);
            alumnosOrigen = alumnosSeccionRegularOrigen.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
            this.procesarCambioAula(letraGrupoRegular,
                    seccionGrupoEspecialOrigen.getSeccion(),
                    seccionGrupoRegularOrigen,
                    aulaDestino,
                    alumnosOrigen,
                    horariosAulasBySeccionRol,
                    ds);
        } else if (cambiarAula.isTipoGrupoEspecialOrigen()) {
            seccionGrupoEspecialOrigen = seccionGrupoEspecialDAO.find(cambiarAula.getIdSeccionRolExamenesOrigen());
            //this.checkNoPublicado(seccionGrupoEspecialOrigen.getRolExamenes());
            horariosAulasBySeccionRol = horarioAulaDAO.allBySeccionGrupoEspecial(seccionGrupoEspecialOrigen);
            List<AlumnoGrupoEspecial> alumnosSeccionRegularOrigen = alumnoGrupoEspecialDAO.allBySeccionGrupoEspecialAndEstados(seccionGrupoEspecialOrigen, AlumnoRolExamenEstadoEnum.ACT);
            alumnosOrigen = alumnosSeccionRegularOrigen.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
            this.procesarCambioAula(letraGrupoRegular,
                    seccionGrupoEspecialOrigen.getSeccion(),
                    seccionGrupoRegularOrigen,
                    aulaDestino,
                    alumnosOrigen,
                    horariosAulasBySeccionRol,
                    ds);
        }
    }

    public boolean procesarCambioAula(
            LetraGrupoRegular letraGrupoRegular,
            Seccion seccion,
            SeccionGrupoRegular seccionGrupoRegularOrigen,
            Aula aulaDestino,
            List<Alumno> alumnosOrigen,
            List<HorarioAula> horariosAulasBySeccionRol,
            DataSessionPivot ds) {
        seccion = seccion.clone();
        List<Aula> aulas = Arrays.asList(aulaDestino);

        List<DocenteSeccion> docentesPrincipalesOrigen = docenteSeccionDAO.allPrincipalesBySecciones(Arrays.asList(seccion));
        List<Docente> docentesOrigen = docentesPrincipalesOrigen.stream().map(x -> x.getDocente()).collect(Collectors.toList());

        boolean validacionCursosMasivos = grupoRegularConnector.validarCursosMasivos(letraGrupoRegular.getGrupoHorasExamen(), docentesOrigen, aulas, alumnosOrigen);
        boolean validacionGruposRegulares = grupoRegularConnector.validarGrupoRegular(letraGrupoRegular.getGrupoHorasExamen(), alumnosOrigen, docentesOrigen, aulas);
        boolean validacionSeccionesEspeciales = grupoRegularConnector.validarGrupoEspecial(letraGrupoRegular.getGrupoHorasExamen(), docentesOrigen, aulas, alumnosOrigen);

        if (validacionCursosMasivos && validacionGruposRegulares && validacionSeccionesEspeciales) {
            SeccionGrupoRegular seccionGrupoRegularUpd = new SeccionGrupoRegular(seccionGrupoRegularOrigen.getId());
            seccionGrupoRegularUpd.setAula(aulaDestino);
            seccionGrupoRegularDAO.updateAula(seccionGrupoRegularUpd);
            // this.cambiarEstadoSeccionGrupoRegular(seccionGrupoRegularOrigen, alumnosSeccionRegularOrigen);
            // rolExamenesService.restoreHorariosAulas(letraGrupoRegular.getRolExamenes(), seccion, seccionGrupoRegularOrigen.getAula());
            for (HorarioAula horarioAula : horariosAulasBySeccionRol) {
                horarioAula.setAula(aulaDestino);
                horarioAulaDAO.update(horarioAula);
            }
        } else {
            throw new PhobosException("Conflictos encontrados.");
        }
        return false;
    }

    public void adad() {

    }

}
