<template>
    <div>

        <button type="button" v-on:click="add" class="btn btn-default pull-right m-t-md">Agregar Alumno</button>

    </div>
</template>

<script>
    module.exports = {
        data() {
            return {
                resolucion: {tramiteBachiller: []},
                alumnos: [],
                isEdicion: false
            };
        },
        mounted: function () {
            let $vue = this;
        },
        methods: {
            add() {
                let $vue = this;
                $vue.resolucion.tramiteBachiller.push({seleccionado: true});
            },
            del(index) {
                let $vue = this;
                $vue.resolucion.tramiteBachiller.splice(index, 1);
            },
        }
    };
</script>