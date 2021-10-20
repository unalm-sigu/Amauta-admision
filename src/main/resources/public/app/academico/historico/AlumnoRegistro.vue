<template>
    <div>
        <section class="panel">
            <section class="panel-body">

                <ul class="nav nav-pillss  nav-justified bold">
                    <li role="presentation" v-on:click.prevent="state=1" v-bind:class="{'active':state==1}"><a href="#">Alumno</a></li>
                    <li role="presentation" v-on:click.prevent="state=2" v-bind:class="{'active':state==2}"><a href="#">Historial</a></li>
                </ul>

                <div class="m-lg" >

                    <div  v-if="state==1"  >

                        <alumno-informacion></alumno-informacion>

                    </div>

                    <div  v-if="state==2" >

                        <alumno-historial></alumno-historial>

                    </div>

                </div>

            </section>
        </section>
    </div>
</template>

<script>

    const AlumnoInformacion = httpVueLoader('/app/academico/historico/AlumnoInformacion.vue');
    const AlumnoHistorial = httpVueLoader('/app/academico/historico/AlumnoHistorial.vue');

    module.exports = {
        components: {
            alumnoInformacion: AlumnoInformacion,
            alumnoHistorial: AlumnoHistorial,
        },
        data() {
            return {
                state: 1
            };
        },
        mounted: function () {
            let $vue = this;
            $vue.state = 1
        },
        methods: {

        }
    };
</script>

<style>
    .nav-pillss > li.active > a {
        color: #0275D8 !important;
        background-color: #FFF;
        border-bottom-style: solid ;
        border-bottom-color: #0275D8 !important;
        border-width: 2px;
    }
    .nav-pillss > li > a {
        color: #0275D8 !important;
    }
    .nav-pillss > li.active > a:hover {
        color: #0275D8 !important;
        background-color: #E5E7EE;
    }
    .nav-pillss > li.active > a:focus {
        color: #0275D8 !important;
        background-color: #FFF;
    }
</style>