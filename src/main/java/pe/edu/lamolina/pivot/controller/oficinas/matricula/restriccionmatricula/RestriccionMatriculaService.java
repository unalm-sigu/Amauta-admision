package pe.edu.lamolina.pivot.controller.oficinas.matricula.restriccionmatricula;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.DeudaMaterialAlumno;

import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface RestriccionMatriculaService {

    List<DeudaMaterialAlumno> allDeudaAlumno(DynatableFilter filter, DataSessionPivot ds);

    void anularDeuda(DeudaMaterialAlumno deuda, DataSessionPivot ds);

    void levantarDeuda(DeudaMaterialAlumno deuda, DataSessionPivot ds);

    void guardarDeuda(DeudaMaterialAlumno deudaForm);

    List<Oficina> allOficina();

    List<String> cargarDeudas(MultipartFile file, Oficina tipo, DataSessionPivot ds);

    public void save(DeudaMaterialAlumno deuda, DataSessionPivot ds);

}
