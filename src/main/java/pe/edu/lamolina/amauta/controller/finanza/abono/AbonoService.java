package pe.edu.lamolina.amauta.controller.finanza.abono;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.model.general.Observado;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.finanzas.ItemCargaAbono;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface AbonoService {

    List<ItemCargaAbono> allAbonosByPostulante(CicloPostula ciclo, DynatableFilter filter);

//    List<Observado> loadArchivoDiario(MultipartFile file, CicloPostula ciclo, DataSessionPivot ds);
    List<Observado> loadArchivoDiario(MultipartFile file,  DataSessionPivot ds);

    List<Observado> loadArchivoHistorico(MultipartFile file, CicloPostula ciclo, DataSessionPivot ds);

    void asignarPostulante(ItemCargaAbono itemCargaAbono, CicloPostula ciclo, DataSessionPivot ds);

    List<ItemCargaAbono> allExtornados(ItemCargaAbono itemCargaForm);

    void reasignarExtorno(ItemCargaAbono form, DataSessionPivot ds);

    void revisarDeudasCompletas(List<DeudaInteresado> deudas, Postulante postulante, ModalidadIngreso modalidad, CicloPostula ciclo);

}
