<template>
    <div>
        <section class="panel">
            <section class="panel-body">

                <ul class="nav nav-pills  nav-justified bold">
                    <li role="presentation" v-on:click.prevent="state=1" v-bind:class="{'active':state==1}"><a href="#">Alumno</a></li>
                    <li role="presentation" v-on:click.prevent="state=2" v-bind:class="{'active':state==2}"><a href="#">Historial</a></li>
                </ul>

                <div class="m-lg" >

                    <div  v-if="state==1"  >

                        <alumno-formulario></alumno-formulario>

                    </div>

                    <div  v-if="state==2" >

                        tab 2

                    </div>

                </div>

            </section>
        </section>
    </div>
</template>

<script>

    const AlumnoFormulario = httpVueLoader('/app/academico/historico/AlumnoFormulario.vue');

    module.exports = {
        components: {
            alumnoFormulario: AlumnoFormulario
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