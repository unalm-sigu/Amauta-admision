<template>
  <div>
    <template>
      <a v-bind:href="urlInfoAlumno(alumno)">
        <strong class="text-primary h5">{{ persona.apellidosNombres }}</strong>
      </a>
    </template>
    <br>

    <strong>Matrícula:</strong>
    <span v-text='alumno.codigo'></span>

    <template v-if="persona.tipoDocumento"> |
      <strong>{{ persona.tipoDocumento.simbolo }}:</strong>
      <span v-text='persona.numeroDocIdentidad'></span>
    </template>
    <br>

    <template v-if="esFacultad(alumno)">
                <span class="block text-info bold">
                    Facultad de {{ alumno.carrera.facultad.nombre }}
                </span>
    </template>
    <span class="block text-success">{{ alumno.carrera.nombre }}</span>
    <span class="block">{{ alumno.modalidadEstudio.nombre }} </span>
    <span class="block"><strong>Situación Académica: </strong>{{ alumno.situacionAcademica.nombre }} </span>
  </div>

</template>
<script>
module.exports = {
  props: {
    alumno: {},
    persona: {}
  },
  methods: {
    esFacultad(alumno) {
      return (alumno.modalidadEstudio.codigo === "PRE" && alumno.carrera.nombre !== alumno.carrera.facultad.nombre);
    },
    urlInfoAlumno(alumno) {
      return `/academico/alumno/${alumno.id}/infoacademico${myUtils.getOrigenURL()}`;
    }
  }
};
</script>