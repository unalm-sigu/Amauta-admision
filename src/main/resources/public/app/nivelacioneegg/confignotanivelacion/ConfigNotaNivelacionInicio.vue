<template>
    <div>
        <header class="header b-b padder-lg">
            <div class="pull-right m-t-sm">
                <div class="dropdown">
                    <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">
                        Acciones <span class="caret"></span>
                    </button>

                    <ul class="dropdown-menu dropdown-menu-right">
                        <li><a v-on:click.prevent="activarTodos" href="#" class="dropdown-item">
                                Activar todos
                            </a>
                        </li>
                    </ul>
                </div>
            </div>

            <h2> Configuración de nota mínima {{ciclo.descripcion}}</h2>
        </header>

        <section class="wrapper-lg">
            <section class="panel m-b-md">
                <section class="panel-body">

                    <raptor-table v-bind:url="configsURL" 
                                  v-bind:pagination="pagination"
                                  ref="raptorConfigs">
                        <template scope="props" >
                            <table class="table table-striped">
                                <thead class="panel panel-heading">
                                    <tr>
                                        <th class="v-middle">Modalidad ingreso</th>
                                        <th class="v-middle">Tema examen</th>
                                        <th class="v-middle text-center">Preguntas</th>
                                        <th class="v-middle text-center">Puntaje mín/máx examen</th>
                                        <th class="v-middle text-center">Nota mín/máx examen</th>
                                        <th class="v-middle text-center">Puntaje mínimo aprobatorio</th>
                                        <th class="v-middle text-center">Nota mínima aprobatoria</th>
                                        <th class="v-middle text-center">Estado</th>
                                        <th class=""></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="item in props.data">
                                        <td class="v-middle">
                                            <div class="text-primary block"
                                                 v-if="item.modalidadIngreso">
                                                {{item.modalidadIngreso.nombre}}
                                            </div>
                                            <div class="text-primary block"
                                                 v-else="">
                                                Otras modalidades
                                            </div>
                                        </td>

                                        <td class="v-middle">
                                            <div class="text-primary block">
                                                {{item.temaExamen.nombre}}
                                            </div>
                                        </td>

                                        <td class="v-middle text-center">
                                            {{item.temaCiclo.preguntas}}
                                        </td>

                                        <td class="v-middle text-center">
                                            <template v-if="item.otrasModalidades">
                                                <div class="block">
                                                    {{puntaje(item.temaCiclo.puntajeMinimo)}} /
                                                    {{puntaje(item.temaCiclo.puntajeMaximo)}}
                                                </div>
                                            </template>
                                            <template v-else="">
                                                <div class="block">
                                                    {{puntaje(item.temaCiclo.puntajeCepreMinimo)}} /
                                                    {{puntaje(item.temaCiclo.puntajeCepreMaximo)}}
                                                </div>
                                            </template>
                                        </td>

                                        <td class="v-middle text-center">
                                            <template v-if="item.otrasModalidades">
                                                <div class="block">
                                                    {{puntaje(item.temaCiclo.notaMinima)}} /
                                                    {{puntaje(item.temaCiclo.notaMaxima)}}
                                                </div>
                                            </template>
                                        </td>

                                        <td class="v-middle text-center">
                                            {{puntaje(item.puntajeMinimo)}}
                                        </td>

                                        <td class="v-middle text-center">
                                            {{puntaje(item.notaMinima)}}
                                        </td>
                                        <td class="v-middle text-center">
                                            <div v-if="item.temaCiclo.id"
                                                 v-bind:class="estadoClass(item)" class="label">
                                                {{item.estadoEnum.value}}
                                            </div>
                                        </td>

                                        <td class="v-middle text-center">
                                            <div v-if="item.temaCiclo.id" class="dropdown actions">
                                                <a class="dropdown-toggle" data-toggle="dropdown">
                                                    <i class="fa fa-cog"></i>
                                                </a>
                                                <ul class="dropdown-menu pull-right">
                                                    <li v-if="item.estado == 'PEN' " class="pointer"><a v-on:click="editar(item)">Editar</a></li>
                                                    <li v-if="item.estado == 'PEN' " class="pointer"><a v-on:click="activar(item)">Activar</a></li>
                                                    <li v-if="item.estado == 'ACT' " class="pointer"><a v-on:click="desactivar(item)">Desactivar</a></li>
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

        <modal-confirm ref="modalConfirm"></modal-confirm>
        <modal-info ref="modalInfo"></modal-info>
        <modal-editar ref="modalEditar"></modal-editar>
    </div>

</template>
<script>
    Vue.component("multiselect", window.VueMultiselect.default);
    Vue.component('date-picker', window.VueBootstrapDatetimePicker);

    const ModalConfirm = httpVueLoader('/app/_componentes/ModalConfirm.vue');
    const ModalInfo = httpVueLoader('/app/_componentes/ModalInfo.vue');
    const ModalEditar = httpVueLoader('./ModalEditar.vue');

    module.exports = {
        components: {
            ModalConfirm, ModalInfo, ModalEditar
        },
        data() {
            return {
                idModalConfirm: "id-modal-confirm-config-notas",
                ciclo: JSON.parse(cicloJson),
                configsURL: `/${rutaModulo}/list`,
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
            puntaje(nota) {
                if (nota) {
                    return myUtils.commas(nota);
                }
                return "";
            },
            editar(item) {
                this.$refs.modalEditar.open(item, this.$refs.raptorConfigs);
            },
            estadoClass(item) {
                if (item.estado === 'ACT') {
                    return "label-success";
                }
                if (item.estado === 'PEN') {
                    return "label-default";
                }
                return "";
            },
            activar(item) {
                let config = VUE_MODAL.structConfirm({
                    id: this.idModalConfirm,
                    message: "¿Seguro que desea activar este registro?",
                    okbtn: "Si, activar",
                    okclass: "btn-primary",
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/activar`,
                            modal: this.$refs.modalConfirm.getModal(),
                            raptor: this.$refs.raptorConfigs,
                            body: {id: item.id}
                        }));
                    }
                });

                this.$refs.modalConfirm.open(config);
            },
            activarTodos() {
                let config = VUE_MODAL.structConfirm({
                    id: this.idModalConfirm,
                    message: "¿Seguro que desea activar todos los registros?",
                    okbtn: "Si, activar",
                    okclass: "btn-primary",
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/activarTodos`,
                            modal: this.$refs.modalConfirm.getModal(),
                            raptor: this.$refs.raptorConfigs
                        }));
                    }
                });

                this.$refs.modalConfirm.open(config);
            },
            desactivar(item) {
                let config = VUE_MODAL.structConfirm({
                    id: this.idModalConfirm,
                    message: "¿Seguro que desea desactivar este registro?",
                    okbtn: "Si, desactivar",
                    okclass: "btn-danger",
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/desactivar`,
                            modal: this.$refs.modalConfirm.getModal(),
                            raptor: this.$refs.raptorConfigs,
                            body: {id: item.id}
                        }));
                    }
                });

                this.$refs.modalConfirm.open(config);
            },

            // metodos genericos
            activarNumeric: myUtils.activarNumeric,
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };

</script>