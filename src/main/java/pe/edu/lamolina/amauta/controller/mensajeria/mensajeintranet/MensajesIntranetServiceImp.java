package pe.edu.lamolina.amauta.controller.mensajeria.mensajeintranet;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoMensajeIntranet;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DetalleGrupoAlumno;
import pe.edu.lamolina.model.academico.GrupoAlumno;
import pe.edu.lamolina.model.academico.MensajeIntranet;
import pe.edu.lamolina.model.academico.TipoMensajeIntranet;
import pe.edu.lamolina.model.enums.TipoMensajeIntranetEnum;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.mensajeria.AlumnoMensajeIntranetDAO;
import pe.edu.lamolina.amauta.dao.mensajeria.DetalleGrupoAlumnoDAO;
import pe.edu.lamolina.amauta.dao.mensajeria.GrupoAlumnoDAO;
import pe.edu.lamolina.amauta.dao.mensajeria.MensajeIntranetDAO;
import pe.edu.lamolina.amauta.dao.mensajeria.TipoMensajeIntranetDAO;

@Service
@Transactional(readOnly = true)
public class MensajesIntranetServiceImp implements MensajesIntranetService {

    @Autowired
    GrupoAlumnoDAO grupoAlumnoDAO;
    @Autowired
    TipoMensajeIntranetDAO tipoMensajeIntranetDAO;
    @Autowired
    MensajeIntranetDAO mensajeIntranetDAO;
    @Autowired
    DetalleGrupoAlumnoDAO detalleGrupoAlumnoDAO;
    @Autowired
    AlumnoDAO alumnoDAO;
    @Autowired
    AlumnoMensajeIntranetDAO alumnoMensajeIntranetDAO;

    @Override
    public List<GrupoAlumno> allGruposAlumnos() {
        return grupoAlumnoDAO.all();
    }

    @Override
    public List<TipoMensajeIntranet> allTiposMensajes() {
        return tipoMensajeIntranetDAO.all();
    }

    @Override
    public List<MensajeIntranet> allByDynatble(DynatableFilter filter) {
        return mensajeIntranetDAO.allByDynatble(filter);
    }

    @Override
    @Transactional
    public void saveMensajeria(MensajeIntranet mensajeria, CicloAcademico ciclo, Usuario usuario) {
        if (mensajeria.getConCronograma() == null) {
            mensajeria.setConCronograma(0);
        }
        mensajeria.setCicloAcademico(ciclo);
        mensajeria.setUserRegistro(usuario);
        mensajeria.setFechaRegistro(new Date());
        mensajeIntranetDAO.save(mensajeria);

        if (mensajeria.getTipoMensajeIntranet() != null) {
            TipoMensajeIntranet tipo = tipoMensajeIntranetDAO.find(mensajeria.getTipoMensajeIntranet().getId());
            System.out.println(tipo.getCodigo());
            if (tipo.getCodigoEnum() != TipoMensajeIntranetEnum.MSG_GRAL) {
                return;
            }
            System.out.println(mensajeria.getEsMensajeAppmovil());
            if (!mensajeria.getEsMensajeAppmovil()) {
                return;
            }

            List<Alumno> alumnos = new ArrayList();
            GrupoAlumno gpoAlumno = mensajeria.getGrupoAlumno();

            List<DetalleGrupoAlumno> detalle = detalleGrupoAlumnoDAO.allByGrupoAlumnoCiclo(gpoAlumno);
            for (DetalleGrupoAlumno dga : detalle) {

                if (dga.getMatriculados() == 1) {
                    List<Alumno> matriculados = alumnoDAO.allMatriculadosByDetalleGpoAlu(dga, ciclo);
                    alumnos.addAll(matriculados);
                }
                if (dga.getMatriculables() == 1) {
                    List<Alumno> matriculables = alumnoDAO.allMatriculablesByDetalleGpoAlu(dga, ciclo);
                    alumnos.addAll(matriculables);
                }
            }
            System.out.println("alumnos: " + alumnos.size());

            alumnoMensajeIntranetDAO.createMessage(mensajeria, alumnos);

//            Map<Long, Alumno> mapAlumnos = new LinkedHashMap();
//            for (Alumno alu : alumnos) {
//                Alumno alumno = mapAlumnos.get(alu.getId());
//                if (alumno != null) {
//                    continue;
//                }
//                AlumnoMensajeIntranet aluMsg = new AlumnoMensajeIntranet();
//                aluMsg.setAlumno(alumno);
//                aluMsg.setMensajeIntranet(mensajeria);
//                alumnoMensajeIntranetDAO.save(aluMsg);
//
//                mapAlumnos.put(alu.getId(), alu);
//            }
        }

    }

    @Override
    @Transactional
    public void updateMensajeria(MensajeIntranet mensajeriaForm, CicloAcademico cicloAcademico, Usuario usuario) {
        MensajeIntranet mensajeria = mensajeIntranetDAO.find(mensajeriaForm);
        if (mensajeria == null) {
            throw new PhobosException("La mensajería que intenta editar no es correcta");
        }
        mensajeriaForm.setFechaRegistro(mensajeria.getFechaRegistro());
        mensajeriaForm.setUserRegistro(usuario);
        mensajeriaForm.setCicloAcademico(mensajeria.getCicloAcademico());
        mensajeIntranetDAO.update(mensajeriaForm);
    }

    @Override
    @Transactional
    public void eliminar(MensajeIntranet mensajeria) {
        mensajeIntranetDAO.delete(mensajeria);
    }

    @Override
    public MensajeIntranet findMensajeria(Long id) {
        return mensajeIntranetDAO.find(id);
    }

}
