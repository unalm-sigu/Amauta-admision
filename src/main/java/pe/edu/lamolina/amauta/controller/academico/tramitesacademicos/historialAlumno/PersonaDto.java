/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.edu.lamolina.amauta.controller.academico.tramitesacademicos.historialAlumno;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PersonaDto {

    private String paterno;

    private String materno;

    private String nombres;

    private String sexo;

    private String email;

    private String emailCompania;

    private String celular;

    private String telefono;

    private String numeroDocIdentidad;

    private String direccion;

    private LocalDate fechaNacer;

    private Long idUbicacionNacer;

    private Long idTipoDocumento;

    private Long idPaisNacer;

    private Long idNacionalidad;

    private Long idUbicacionDomicilio;

}
