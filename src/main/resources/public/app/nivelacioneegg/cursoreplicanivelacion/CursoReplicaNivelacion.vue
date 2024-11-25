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
                                        <!--<th class="v-middle text-right">Estado</th>-->
                                        <th class=""></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="item in props.data">
                                        <td class="v-middle">
                                            <span class="block text-primary h4 m-t-xs m-b-xs">{{item.nombre}}</span>
                                            <span class="block"><b>{{item.codigo}}</b> 
                                        </td>
                                        <td class="v-middle">
                                            <table>
                                                <thead></thead>
                                                <tbody>
                                                    <tr v-for="regular in item.cursosReplica">
                                                        <td>{{regular.cursoRegular.codigo}} - {{regular.cursoRegular.nombre}}</td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                        </td>


                                        <td class="v-middle text-center">
                                            <div class="dropdown actions">
                                                <a class="dropdown-toggle" data-toggle="dropdown">
                                                    <i class="fa fa-cog"></i>
                                                </a>
                                                <ul class="dropdown-menu pull-right">
                                                    <li class="pointer"><a v-on:click="editar(item)">Editar</a></li>
                                                    <li class="pointer"><a v-on:click="relacionar(item)">Relacionar</a></li>
                                                    <!--                                                    <li v-if="item.estado == 'PEN' " class="pointer"><a v-on:click="activar(item)">Activar</a></li>
                                                                                                        <li v-if="item.estado == 'ACT' " class="pointer"><a v-on:click="anular(item)">Anular</a></li>
                                                                                                        <li v-if="item.estado == 'PEN' " class="pointer"><a v-on:click="eliminar(item)">Eliminar</a></li>
                                                                                                        <li v-if="item.estado == 'ACT' " class="pointer"><a v-on:click="relacionarConTemas(item)">Relacionar con Temas</a></li>-->
                                                </ul>
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

        <!--    <modal-curso ref="modalCurso"></modal-curso>
                <modal-confirm ref="modalConfirm"></modal-confirm>
                <modal-relacion-curso-con-tema ref="modalRelacionCursoConTema"></modal-relacion-curso-con-tema>-->
        <!--<modal-confirm ref="modalConfirm"></modal-confirm>-->
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
//                idModalConfirmacion: "id-modal-confirmacion",
//                idModalRelacionCursoConTema: "id-modal-relacion-curso-con-tema",
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
            },
//            estadoClass(item) {
//                if (item.estado === 'ACT') {
//                    return "label label-success";
//                }
//                if (item.estado === 'PEN') {
//                    return "label label-default";
//                }
//                if (item.estado === 'ANU') {
//                    return "label label-danger";
//                }
//                return "";
//            },
            editar(item) {
//                this.$refs.modalCurso.editar(item, this.$refs.raptorCurso);
            },
//            activar(item) {
//                let config = VUE_MODAL.structConfirm({
//                    id: this.idModalConfirmacion,
//                    message: "¿Seguro que desea activar este registro?",
//                    okbtn: "Si, activar",
//                    okclass: "btn-danger",
//                    okaction: () => {
//                        myUtils.axios(VUE_AXIOS.structModalClose({
//                            url: `/${rutaModulo}/activar`,
//                            modal: this.$refs.modalConfirm.getModal(),
//                            raptor: this.$refs.raptorCurso,
//                            body: {id: item.id}
//                        }));
//                    }
//                });
//                this.$refs.modalConfirm.open(config);
//            },
//            eliminar(item) {
//                let config = VUE_MODAL.structConfirm({
//                    id: this.idModalConfirmacion,
//                    message: "¿Seguro que desea eliminar este curso?",
//                    okbtn: "Si, eliminar",
//                    okclass: "btn-danger",
//                    okaction: () => {
//                        myUtils.axios(VUE_AXIOS.structModalClose({
//                            url: `/${rutaModulo}/eliminar`,
//                            modal: this.$refs.modalConfirm.getModal(),
//                            raptor: this.$refs.raptorCurso,
//                            body: {id: item.id}
//                        }));
//                    }
//                });
//
//                this.$refs.modalConfirm.open(config);
//            },
//            anular(item) {
//                let config = VUE_MODAL.structConfirm({
//                    id: this.idModalConfirmacion,
//                    message: "¿Seguro que desea anular este curso?",
//                    okbtn: "Si, anular",
//                    okclass: "btn-danger",
//                    okaction: () => {
//                        myUtils.axios(VUE_AXIOS.structModalClose({
//                            url: `/${rutaModulo}/activar`,
//                            modal: this.$refs.modalConfirm.getModal(),
//                            raptor: this.$refs.raptorCurso,
//                            body: {id: item.id}
//                        }));
//                    }
//                });
//                this.$refs.modalConfirm.open(config);
//            },
//            relacionarConTemas(item) {
//                this.$refs.modalRelacionCursoConTema.abrirModalRelacion(item, this.$refs.raptorCurso);
//
//            }
        }
    };

</script>