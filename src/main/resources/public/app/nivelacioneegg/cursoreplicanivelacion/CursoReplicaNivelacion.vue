<template>
    <div>
        <header class="header b-b padder-lg">
            <h2> Lista Cursos de Nivelación relacionados con cursos regulares {{ciclo.descripcion}}</h2>
        </header>

        <section class="wrapper-lg">
            <section class="panel m-b-md">
                <section class="panel-body">

                    <raptor-table v-bind:url="cursosReplicaNivelacionURL" 
                                  v-bind:pagination="pagination"
                                  ref="raptorCurso">
                        <template scope="props" >
                            <table class="table table-striped">
                                <thead class="panel panel-heading">
                                    <tr>
                                        <th class="v-middle">Curso Nivelación</th>
                                        <th class="v-middle">Cursos Regulares</th>
                                        <th class=""></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="item in props.data">
                                        <td class="v-middle">
                                            <span class="block text-primary h4 m-t-xs m-b-xs">{{item.nombre}} </span>
                                            <span class="block bold"><b>{{item.codigo}}</b> </span>
                                        </td>
                                        <td class="v-middle">
                                            <div v-if="item.cursosReplica">
                                                <table class="table table-hover pointer"  v-on:click="relacionar(item)">
                                                    <tbody>
                                                        <tr v-for="regular in item.cursosReplica">
                                                            <td>{{regular.cursoRegular.codigo}} - {{regular.cursoRegular.nombre}}</td>
                                                        </tr>
                                                    </tbody>
                                                </table>
                                            </div>
                                            <div v-else="">
                                                <table class="table table-hover">
                                                    <!--<thead></thead>-->
                                                    <tbody>
                                                        <tr>
                                                            <td><p>Sin cursos regulares relacionados</p></td>
                                                        </tr>
                                                    </tbody>
                                                </table>
                                                <!--<p>Sin cursos regulares relacionados</p>-->
                                            </div>
                                        </td>


                                        <td class="v-middle text-center">
                                            <div class="dropdown actions">
                                                <a class="dropdown-toggle" data-toggle="dropdown">
                                                    <i class="fa fa-cog"></i>
                                                </a>
                                                <ul class="dropdown-menu pull-right">
                                                    <li class="pointer"><a v-on:click="relacionar(item)">Relacionar</a></li> </ul>
                                            </div>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </template>
                    </raptor-table>

                </section>
            </section>

        </section>

        <modal-relacion-curso-regular ref="modalRelacionCursoRegular"></modal-relacion-curso-regular>
    </div>

</template>

<script>
    Vue.component("multiselect", window.VueMultiselect.default);

    const ModalRelacionCursoRegular = httpVueLoader('./ModalRelacionCursoRegular.vue');
    
    module.exports = {
        components: {
            ModalRelacionCursoRegular
        },
        data() {
            return {
                idModalRelacion: "id-modal-relacion",
                ciclo: JSON.parse(cicloJson),
                cursosReplicaNivelacionURL: `/${rutaModulo}/list`,
                pagination: {'total-items': 0, 'items-per-page': 100, 'max-size': 3, 'boundary-link-numbers': true}
            };
        },
        computed: {

        },
        methods: {
            relacionar(item) {
                this.$refs.modalRelacionCursoRegular.abrirModalRelacion(item, this.$refs.raptorCurso);
            }
        }
    };

</script>