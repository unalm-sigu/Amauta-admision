<template>
    <div>
        <div class="pull-left">
            <div v-if="persona.tipoFoto=='POSTUL' "
                 class="img-responsive img-thumbnail img-circle div-foto-list">
                <img class="img-foto-list"
                     v-bind:src="persona.rutaFoto" />
            </div>

            <img v-else-if="persona.tipoFoto=='COMUN' "
                 class="img-responsive img-thumbnail img-circle div-foto-alumno-list"
                 v-bind:src="persona.rutaFoto" />

            <img v-else=""
                 class="img-foto-tempo img-responsive img-thumbnail img-circle"
                 v-bind:src="persona.rutaFoto" />
        </div>
    </div>
</template>

<script>
    module.exports = {
        props: {
            persona: {}
        }
    };
</script>