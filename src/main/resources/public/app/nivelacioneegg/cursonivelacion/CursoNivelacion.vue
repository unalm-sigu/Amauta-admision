<template>
    <div>
        <header class="header b-b padder-lg">
            <div class="pull-right m-t-sm">
                <div class="dropdown">
                    <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">
                        Acciones <span class="caret"></span>
                    </button>

                    <ul class="dropdown-menu dropdown-menu-right">
                        <li><a v-on:click.prevent="nuevoCurso" href="#" class="dropdown-item">
                                Nuevo Curso
                            </a>
                        </li>
                    </ul>
                </div>
            </div>

            <h2> Cursos Nivelación {{ciclo.descripcion}}</h2>
        </header>

        <section class="wrapper-lg">
            <section class="panel m-b-md">
                <section class="panel-body">

                    <raptor-table v-bind:url="cursosNivelacionURL" 
                                  v-bind:pagination="pagination"
                                  ref="raptorConfigs">
                        <template scope="props" >
                            <table class="table table-striped">
                                <thead class="panel panel-heading">
                                    <tr>
                                        <th class="v-middle">Curso</th>
                                        <th class="v-middle">Dependencia</th>
                                        <th class="v-middle">Estado</th>
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
                                            <span class="block text-primary bold">Facultad {{item.departamentoAcademico.facultad.nombre}}</span>
                                            <span class="block"><b>Dpto. Acad.</b> {{item.departamentoAcademico.nombre}}</span>
                                        </td>
                                        <td class="v-middle text-center">
                                            <div v-bind:class="estadoClass(item)">
                                                {{item.estadoEnum.value}}
                                            </div>
                                        </td>


                                        <td class="v-middle text-center">
                                            <div class="dropdown actions">
                                                <a class="dropdown-toggle" data-toggle="dropdown">
                                                    <i class="fa fa-cog"></i>
                                                </a>
                                                <ul class="dropdown-menu pull-right">
                                                    <li v-if="item.estado == 'PEN' " class="pointer"><a v-on:click="editar(item)">Editar</a></li>
                                                    <li v-if="item.estado == 'PEN' " class="pointer"><a v-on:click="activar(item)">Activar</a></li>
                                                    <li v-if="item.estado == 'INA' " class="pointer"><a v-on:click="desactivar(item)">Desactivar</a></li>
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

        <modal-curso ref="modalCurso"></modal-curso>

        <!--        <modal-confirm ref="modalConfirm"></modal-confirm>
                <modal-info ref="modalInfo"></modal-info>
                <modal-editar ref="modalEditar"></modal-editar>-->
    </div>

</template>
<script>
//    Vue.component("multiselect", window.VueMultiselect.default);
//    Vue.component('date-picker', window.VueBootstrapDatetimePicker);

//    const ModalConfirm = httpVueLoader('/app/_componentes/ModalConfirm.vue');
//    const ModalInfo = httpVueLoader('/app/_componentes/ModalInfo.vue');
    const ModalCurso = httpVueLoader('./ModalCurso.vue');

    module.exports = {
        components: {
            ModalCurso
        },
        data() {
            return {
                idModalCurso: "id-modal-curso",
                ciclo: JSON.parse(cicloJson),
                cursosNivelacionURL: `/${rutaModulo}/list`,
                pagination: {'total-items': 0, 'items-per-page': 100, 'max-size': 3, 'boundary-link-numbers': true},
                configDate: {
                    format: 'DD/MM/YYYY',
                    locale: 'es'
                }
            };
        },
        mounted() {

        },
        computed: {

        },
        methods: {

            nuevoCurso() {
                this.$refs.modalCurso.open();
            },
            estadoClass(item) {
                if (item.estado === 'ACT') {
                    return "label label-success";
                }
                if (item.estado === 'PEN') {
                    return "label label-default";
                }
                return "";
            },
            activar(item) {
//                let config = VUE_MODAL.structConfirm({
//                    id: this.idModalConfirm,
//                    message: "¿Seguro que desea activar este registro?",
//                    okbtn: "Si, activar",
//                    okclass: "btn-primary",
//                    okaction: () => {
//                        myUtils.axios(VUE_AXIOS.structModalClose({
//                            url: `/${rutaModulo}/activar`,
//                            modal: this.$refs.modalConfirm.getModal(),
//                            raptor: this.$refs.raptorConfigs,
//                            body: {id: item.id}
//                        }));
//                    }
//                });
//
//                this.$refs.modalConfirm.open(config);
            },
            desactivar(item) {
//                let config = VUE_MODAL.structConfirm({
//                    id: this.idModalConfirm,
//                    message: "¿Seguro que desea desactivar este registro?",
//                    okbtn: "Si, desactivar",
//                    okclass: "btn-danger",
//                    okaction: () => {
//                        myUtils.axios(VUE_AXIOS.structModalClose({
//                            url: `/${rutaModulo}/desactivar`,
//                            modal: this.$refs.modalConfirm.getModal(),
//                            raptor: this.$refs.raptorConfigs,
//                            body: {id: item.id}
//                        }));
//                    }
//                });
//
//                this.$refs.modalConfirm.open(config);
            },

            // metodos genericos
//            activarNumeric: myUtils.activarNumeric,
//            getObjectId: myUtils.getObjectId,
//            getObjectName: myUtils.getObjectName,
//            commas: myUtils.commas
        }
    };

</script>