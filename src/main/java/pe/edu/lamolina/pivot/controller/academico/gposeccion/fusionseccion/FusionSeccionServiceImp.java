package pe.edu.lamolina.pivot.controller.academico.gposeccion.fusionseccion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.vacantes.VacanteAlumno;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class FusionSeccionServiceImp implements FusionSeccionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    HorarioSeccionDAO horarioSeccionDAO;

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Override
    public List<Alumno> allAlumnoBySeccion(Seccion seccion) {
        List<MatriculaSeccion> matriculasSeccion = matriculaSeccionDAO.allMatriculadosBySeccion(seccion);
        Map<Long, Alumno> mapAlumnos = TypesUtil.convertListToMap("matriculaResumen.alumno.id", "matriculaResumen.alumno", matriculasSeccion);
        return mapAlumnos.values().stream().collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void trasladar(Fusion trasladoForm, CicloAcademico ciclo, DataSessionPivot ds) {

        Seccion destino = seccionDAO.find(trasladoForm.getSeccionDestino());
        Seccion origen = seccionDAO.find(trasladoForm.getSeccionOrigen());

        Seccion origenSup = null;
        Seccion destinoSup = null;
        boolean esTeoPrac = false;
        boolean mismoGpoSecc = false;
        if (origen.getTipoSeccionEnum() == TipoSeccionEnum.PCUR) {
            esTeoPrac = true;
            origenSup = origen.getSeccionSuperior();
            destinoSup = destino.getSeccionSuperior();

            GrupoSeccion gpoSecc = origen.getGrupoSeccion();
            grupoSeccionDAO.findLock(gpoSecc.getId());

            if (origenSup.getId().longValue() == destinoSup.getId()) {
                mismoGpoSecc = true;

            } else {
                GrupoSeccion gpoSeccDest = destino.getGrupoSeccion();
                grupoSeccionDAO.findLock(gpoSeccDest.getId());
            }
        }

        logger.debug("seccion  :  {}  seccion origen {}  ", destino.getId(), origen.getId());

        Assert.isTrue(destino.getEstadoEnum() == SeccionEstadoEnum.ACT, "La sección destino se encuentra desactivada");
        Assert.isTrue(destino.getVacantesDisponibles() >= trasladoForm.getAlumnosid().length, "No hay vacantes suficientes");
        Assert.isTrue(origen.getGrupoSeccion().getCicloAcademico().getId().longValue() == ciclo.getId(), "El cambio no corresponde al ciclo");
        Assert.isTrue(destino.getGrupoSeccion().getCicloAcademico().getId().longValue() == ciclo.getId(), "El cambio no corresponde al ciclo");
        Assert.isTrue(destino.getGrupoSeccion().getCurso().getId().longValue() == origen.getGrupoSeccion().getCurso().getId(), "Las secciones deben corresponder al mismo curso");

        Long[] idAlumnos = trasladoForm.getAlumnosid();
        List<Alumno> alumnos = alumnoDAO.allByIds(idAlumnos);
        List<MatriculaSeccion> matriculadosSecciones = matriculaSeccionDAO.allMatriculadosByAlumnosCiclo(alumnos, ciclo);
        Map<Long, Seccion> mapSeccion = TypesUtil.convertListToMap("seccion.id", "seccion", matriculadosSecciones);
        Map<Long, List<Seccion>> mapSeccionByAlu = TypesUtil.convertListToMapList("matriculaResumen.alumno.id", "seccion", matriculadosSecciones);

        List<Seccion> secciones = new ArrayList(mapSeccion.values());
        secciones.add(origen);
        secciones.add(destino);
        if (origen.getSeccionSuperior() != null) {
            secciones.add(origen.getSeccionSuperior());
            secciones.add(destino.getSeccionSuperior());
        }

        List<HorarioSeccion> horariosSecciones = horarioSeccionDAO.allBySecciones(secciones);
        Map<Long, List<HorarioSeccion>> mapHorarios = TypesUtil.convertListToMapList("seccion.id", horariosSecciones);

        for (Alumno alumno : alumnos) {
            boolean hayCruce = tieneCruce(alumno, origen, destino, mapSeccionByAlu, mapHorarios);
            Assert.isFalse(hayCruce, "El alumno con matrícula " + alumno.getCodigo() + " tiene cruce de horario");
        }

        List<Seccion> seccionesOrigen = new ArrayList();
        seccionesOrigen.add(origen);
        if (origen.getSeccionSuperior() != null) {
            seccionesOrigen.add(origen.getSeccionSuperior());
        }

        List<MatriculaSeccion> matriculadosSeccionOrigen = matriculaSeccionDAO.allMatriculadosByAlumnosSecciones(alumnos, seccionesOrigen);
        System.out.println("matriculadosSeccionOrigen ::: " + matriculadosSeccionOrigen.size());
        for (MatriculaSeccion matSecc : matriculadosSeccionOrigen) {
            System.out.println(matSecc.getMatriculaResumen().getAlumno().getCodigo() + " " + matSecc.getSeccion().getCodigo2());
        }
        Map<Long, List<MatriculaSeccion>> mapMatriSeccion = TypesUtil.convertListToMapList("matriculaResumen.alumno.id", matriculadosSeccionOrigen);

        for (Alumno alumno : alumnos) {
            List<MatriculaSeccion> matriSeccionAlu = mapMatriSeccion.get(alumno.getId());
            if (esTeoPrac && !mismoGpoSecc) {
                MatriculaSeccion matSecc = getMatriSeccion(matriSeccionAlu, TipoSeccionEnum.TCUR);
                matSecc.setEstadoEnum(EstadoMatriculaEnum.TRAS);
                matriculaSeccionDAO.update(matSecc);

                MatriculaSeccion newMatriculaSeccion = new MatriculaSeccion();
                newMatriculaSeccion.setVisible(null);
                newMatriculaSeccion.setMatriculaResumen(matSecc.getMatriculaResumen());
                newMatriculaSeccion.setFechaRegistro(new Date());
                newMatriculaSeccion.setUserRegistro(ds.getUsuario());
                newMatriculaSeccion.setSeccion(destinoSup);
                newMatriculaSeccion.setEstadoEnum(EstadoMatriculaEnum.MAT);
                matriculaSeccionDAO.save(newMatriculaSeccion);
            }

            MatriculaSeccion matSecc = matriSeccionAlu.get(0);
            if (esTeoPrac) {
                matSecc = getMatriSeccion(matriSeccionAlu, TipoSeccionEnum.PCUR);
            }
            matSecc.setEstadoEnum(EstadoMatriculaEnum.TRAS);
            matriculaSeccionDAO.update(matSecc);

            MatriculaSeccion newMatriculaSeccion = new MatriculaSeccion();
            newMatriculaSeccion.setVisible(null);
            newMatriculaSeccion.setMatriculaResumen(matSecc.getMatriculaResumen());
            newMatriculaSeccion.setFechaRegistro(new Date());
            newMatriculaSeccion.setUserRegistro(ds.getUsuario());
            newMatriculaSeccion.setSeccion(destino);
            newMatriculaSeccion.setEstadoEnum(EstadoMatriculaEnum.MAT);
            matriculaSeccionDAO.save(newMatriculaSeccion);

            // FALTA VACANTE-ALUMNO COMO REGISTRO ANULADO
            // FALTA VACANTE-ALUMNO COMO NUEVO REGISTRO
        }

        seccionDAO.updateMatriculados(destino, destino.getMatriculados() + alumnos.size());
        seccionDAO.updateMatriculados(origen, origen.getMatriculados() - alumnos.size());
        if (esTeoPrac && !mismoGpoSecc) {
            seccionDAO.updateMatriculados(destinoSup, destinoSup.getMatriculados() + alumnos.size());
            seccionDAO.updateMatriculados(origenSup, origenSup.getMatriculados() - alumnos.size());
        }

//        for (Long alumnoId : idAlumnos) {
//
//            MatriculaResumen matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(new Alumno(alumnoId), ciclo);
//
//            Alumno alumnoDb = matriculaResumen.getAlumno();
//
//            logger.debug("alumno  :  {}  ", alumnoDb.getPersona().getNombreCompleto());
//            logger.debug("matriculaResumen  :  {}  ", matriculaResumen.getId());
//            logger.debug("seccion  :  {}  ", destino.getId());
//            MatriculaSeccion matriculaSeccion = matriculaSeccionDAO.findByMatriculaResumenSeccion(matriculaResumen, origen);
//            logger.debug("matriculaSeccion existe  :  {}  ", matriculaSeccion != null);
//
//            if (matriculaSeccion != null) {
//                logger.debug("matriculaSeccion  :  {}  ", matriculaSeccion.getId());
//                matriculaSeccion.setEstadoEnum(EstadoMatriculaEnum.TRAS);
//                matriculaSeccionDAO.update(matriculaSeccion);
//            }
//
//            MatriculaSeccion justMatriculadoOnSeccion = matriculaSeccionDAO.findByMatriculaResumenSeccion(matriculaResumen, destino);
//
//            if (justMatriculadoOnSeccion != null) {
//
//                logger.debug("ya matriculado MatriculaSeccion  :  {}  ", justMatriculadoOnSeccion.getId());
//                justMatriculadoOnSeccion.setEstadoEnum(EstadoMatriculaEnum.MAT);
//                matriculaSeccionDAO.update(justMatriculadoOnSeccion);
//
//            } else {
//
//                MatriculaSeccion newMatriculaSeccion = new MatriculaSeccion();
//                newMatriculaSeccion.setVisible(null);
//                newMatriculaSeccion.setMatriculaResumen(matriculaResumen);
//                newMatriculaSeccion.setFechaRegistro(new Date());
//                newMatriculaSeccion.setUserRegistro(ds.getUsuario());
//                newMatriculaSeccion.setSeccion(destino);
//                newMatriculaSeccion.setEstadoEnum(EstadoMatriculaEnum.MAT);
//                matriculaSeccionDAO.save(newMatriculaSeccion);
//                logger.debug("creado  :  {}  ", newMatriculaSeccion.getId());
//            }
//
//            if (TipoSeccionEnum.PCUR.name().equalsIgnoreCase(origen.getTipoSeccion())) {
//
//                logger.debug(" ***** TipoSeccionEnum PCUR ***** ");
//
//                logger.debug("alumno  :  {}  ", alumnoDb.getPersona().getNombreCompleto());
//                logger.debug("matriculaResumen  :  {}  ", matriculaResumen.getId());
//                logger.debug("seccion  :  {}  ", destino.getSeccionSuperior().getId());
//                MatriculaSeccion teoMatriculaSeccion = matriculaSeccionDAO.findByMatriculaResumenSeccion(matriculaResumen, origen.getSeccionSuperior());
//                logger.debug("MatriculaSeccion existe  :  {}  ", teoMatriculaSeccion != null);
//
//                if (teoMatriculaSeccion != null) {
//                    logger.debug("matriculaSeccion  :  {}  ", matriculaSeccion.getId());
//                    teoMatriculaSeccion.setEstadoEnum(EstadoMatriculaEnum.TRAS);
//                    matriculaSeccionDAO.update(teoMatriculaSeccion);
//                }
//
//                MatriculaSeccion justTeoMatriculadoOnSeccion = matriculaSeccionDAO.findByMatriculaResumenSeccion(matriculaResumen, destino.getSeccionSuperior());
//
//                if (justTeoMatriculadoOnSeccion != null) {
//
//                    logger.debug("ya matriculado MatriculaSeccion  :  {}  ", justTeoMatriculadoOnSeccion.getId());
//                    justTeoMatriculadoOnSeccion.setEstadoEnum(EstadoMatriculaEnum.MAT);
//                    matriculaSeccionDAO.update(justTeoMatriculadoOnSeccion);
//
//                } else {
//
//                    MatriculaSeccion newTeoMatriculaSeccion = new MatriculaSeccion();
//                    newTeoMatriculaSeccion.setVisible(null);
//                    newTeoMatriculaSeccion.setMatriculaResumen(matriculaResumen);
//                    newTeoMatriculaSeccion.setFechaRegistro(new Date());
//                    newTeoMatriculaSeccion.setUserRegistro(ds.getUsuario());
//                    newTeoMatriculaSeccion.setSeccion(destino.getSeccionSuperior());
//                    newTeoMatriculaSeccion.setEstadoEnum(EstadoMatriculaEnum.MAT);
//                    matriculaSeccionDAO.save(newTeoMatriculaSeccion);
//                    logger.debug("creado :  {}", newTeoMatriculaSeccion.getId());
//                }
//            }
//        }
    }

    private MatriculaSeccion getMatriSeccion(List<MatriculaSeccion> matriSecciones, TipoSeccionEnum tipoSeccion) {
        for (MatriculaSeccion matSeccion : matriSecciones) {
            Seccion seccion = matSeccion.getSeccion();
            if (seccion.getTipoSeccionEnum() == tipoSeccion) {
                return matSeccion;
            }
        }
        return null;
    }

    @Override
    public List<Seccion> allSeccionDisponible(Seccion seccionForm, CicloAcademico ciclo) {
        Seccion seccion = seccionDAO.find(seccionForm);
        Curso curso = seccion.getGrupoSeccion().getCurso();
        return seccionDAO.allByCursoCicloExceptSeccion(curso, ciclo, seccion);
    }

    private MatriculaSeccion getTeoria(List<MatriculaSeccion> misMatriculaSecciones, Seccion seccion) {
        for (MatriculaSeccion misMatriculaSeccione : misMatriculaSecciones) {
            if (misMatriculaSeccione.getSeccion().getId().longValue() == seccion.getId()) {
                return misMatriculaSeccione;
            }
        }
        return null;
    }

    @Override
    public List<Alumno> allAlumnoCruce(Seccion seccionOrigen, Seccion seccionDestino, CicloAcademico ciclo) {
        List<MatriculaSeccion> matriculadosSeccionOrigen = matriculaSeccionDAO.allMatriculadosBySeccion(seccionOrigen);
        Map<Long, Alumno> mapAlumnos = TypesUtil.convertListToMap("matriculaResumen.alumno.id", "matriculaResumen.alumno", matriculadosSeccionOrigen);
        List<Alumno> alumnos = mapAlumnos.values().stream().collect(Collectors.toList());

        List<MatriculaSeccion> matriculadosSecciones = matriculaSeccionDAO.allMatriculadosByAlumnosCiclo(alumnos, ciclo);
        Map<Long, Seccion> mapSeccion = TypesUtil.convertListToMap("seccion.id", "seccion", matriculadosSecciones);
        Map<Long, List<Seccion>> mapSeccionByAlu = TypesUtil.convertListToMapList("matriculaResumen.alumno.id", "seccion", matriculadosSecciones);

        Seccion origen = seccionDAO.find(seccionOrigen);
        Seccion destino = seccionDAO.find(seccionDestino);

        Assert.isTrue(origen.getGrupoSeccion().getCicloAcademico().getId().longValue() == ciclo.getId(), "El cambio no corresponde al ciclo");
        Assert.isTrue(destino.getGrupoSeccion().getCicloAcademico().getId().longValue() == ciclo.getId(), "El cambio no corresponde al ciclo");
        Assert.isTrue(destino.getGrupoSeccion().getCurso().getId().longValue() == origen.getGrupoSeccion().getCurso().getId(), "Las secciones deben corresponder al mismo curso");

        List<Seccion> secciones = new ArrayList(mapSeccion.values());
        secciones.add(origen);
        secciones.add(destino);
        if (origen.getSeccionSuperior() != null) {
            secciones.add(origen.getSeccionSuperior());
            secciones.add(destino.getSeccionSuperior());
        }

        List<HorarioSeccion> horariosSecciones = horarioSeccionDAO.allBySecciones(secciones);
        Map<Long, List<HorarioSeccion>> mapHorarios = TypesUtil.convertListToMapList("seccion.id", horariosSecciones);

        for (Alumno alumno : alumnos) {
            boolean hayCruce = tieneCruce(alumno, origen, destino, mapSeccionByAlu, mapHorarios);
            alumno.setHayCruceHorario(hayCruce);
        }

        return alumnos;
    }

    private boolean tieneCruce(
            Alumno alumno,
            Seccion origen,
            Seccion destino,
            Map<Long, List<Seccion>> mapSeccionByAlu,
            Map<Long, List<HorarioSeccion>> mapHorarios) {

        List<Seccion> seccionesAlu = mapSeccionByAlu.get(alumno.getId());
        seccionesAlu = (seccionesAlu == null) ? new ArrayList() : seccionesAlu;
        List<HorarioSeccion> horarioAlu = horarioSecciones(seccionesAlu, mapHorarios);

        List<Seccion> seccionesOrigen = new ArrayList();
        seccionesOrigen.add(origen);
        if (origen.getSeccionSuperior() != null) {
            seccionesOrigen.add(origen.getSeccionSuperior());
        }
        List<HorarioSeccion> horarioOrigen = horarioSecciones(seccionesOrigen, mapHorarios);
        quitarHorario(horarioAlu, horarioOrigen);

        List<Seccion> seccionesDestino = new ArrayList();
        seccionesDestino.add(destino);
        if (destino.getSeccionSuperior() != null) {
            seccionesDestino.add(destino.getSeccionSuperior());
        }

        Map<String, HorarioSeccion> mapHorarioAlu = TypesUtil.convertListToMap("horaDia", horarioAlu);
        List<HorarioSeccion> horarioDestino = horarioSecciones(seccionesDestino, mapHorarios);

        for (HorarioSeccion hdia : horarioDestino) {
            HorarioSeccion hdiaComun = mapHorarioAlu.get(hdia.getHoraDia());
            if (hdiaComun != null) {
                return true;
            }
        }
        return false;

    }

    private void quitarHorario(List<HorarioSeccion> horarioBase, List<HorarioSeccion> horarioQuitar) {
        Map<String, HorarioSeccion> mapHorarioBase = TypesUtil.convertListToMap("horaDia", horarioBase);
        for (HorarioSeccion hdia : horarioQuitar) {
            HorarioSeccion hdiaComun = mapHorarioBase.get(hdia.getHoraDia());
            if (hdiaComun != null) {
                horarioBase.remove(hdiaComun);
            }
        }
    }

    private List<HorarioSeccion> horarioSecciones(List<Seccion> secciones, Map<Long, List<HorarioSeccion>> mapHorarios) {
        List<HorarioSeccion> horario = new ArrayList();
        for (Seccion seccion : secciones) {
            List<HorarioSeccion> hdiaSeccion = mapHorarios.get(seccion.getId());
            hdiaSeccion = (hdiaSeccion == null) ? new ArrayList() : hdiaSeccion;
            horario.addAll(hdiaSeccion);
        }
        return horario;
    }

}
