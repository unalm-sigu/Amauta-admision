<template>
    <div>
        <template v-if="persona">
            <template v-if="gotoInfo">
                <a v-bind:href="urlInfoAlumno(alumno)">
                    <strong class="text-primary h5">{{persona.apellidosNombres}}</strong>
                </a>
            </template>
            <template v-else="">
                <strong class="text-primary h5">{{persona.apellidosNombres}}</strong>
            </template>
            <br>

            <strong>Matrícula:</strong>
            <span v-text='alumno.codigo'></span>

            <template v-if="persona.tipoDocumento"> |
                <strong>{{persona.tipoDocumento.simbolo}}:</strong>
                <span v-text='persona.numeroDocIdentidad'></span>
            </template>
            <br>

            <span v-if="esPosgrado(alumno.carrera)" class="block text-info bold">
                {{alumno.carrera.tipoEnum.value}} en {{alumno.carrera.nombre}}
            </span>
            <template v-else-if="esFacultad(alumno)">
                <span class="block text-info bold">
                    Facultad de {{alumno.carrera.facultad.nombre}}
                </span>
            </template>
            <span v-if="noEsPosgrado(alumno.carrera)" class="block text-success">{{alumno.carrera.nombre}}</span>
            <span class="block">{{alumno.modalidadEstudio.nombre}}</span>
        </template>
    </div>

</template>
<script>
    module.exports = {
        props: {
            alumno: {},
            persona: {},
            gotoInfo: false
        },
        methods: {
            esPosgrado(carrera) {
                return ['MAE', 'DOC'].includes(carrera.tipo);
            },
            noEsPosgrado(carrera) {
                return !['MAE', 'DOC'].includes(carrera.tipo);
            },
            esFacultad(alumno) {
                return (alumno.modalidadEstudio.codigo === "PRE" && alumno.carrera.nombre !== alumno.carrera.facultad.nombre);
            },
            urlInfoAlumno(alumno) {
                return `/academico/alumno/${alumno.id}/infoacademico${myUtils.getOrigenURL()}`;
            }
        }
    };
</script>