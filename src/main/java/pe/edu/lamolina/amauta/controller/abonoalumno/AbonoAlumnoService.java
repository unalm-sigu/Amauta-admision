package pe.edu.lamolina.amauta.controller.abonoalumno;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.finanzas.ItemCargaAbono;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface AbonoAlumnoService {

    List<ItemCargaAbono> allAbonosByAlumno(CicloAcademico ciclo, DynatableFilter filter);

    List<Observado> loadArchivoHistorico(MultipartFile file, CicloAcademico ciclo, Usuario usuario);

//    void asignarPostulante(ItemCargaAbono itemCargaAbono, CicloAcademico ciclo, DataSessionPivot ds);
    List<ItemCargaAbono> allExtornados(ItemCargaAbono itemCargaForm);

    void reasignarExtorno(ItemCargaAbono form, Usuario usuario);

    void revisarDeudasCompletas(List<DeudaInteresado> deudas, Postulante postulante, ModalidadIngreso modalidad, CicloPostula ciclo);

    CicloPostula findCicloActivo();

    List<Observado> loadArchivoDiario(MultipartFile file, CicloAcademico ciclo, Usuario usuario);
}
