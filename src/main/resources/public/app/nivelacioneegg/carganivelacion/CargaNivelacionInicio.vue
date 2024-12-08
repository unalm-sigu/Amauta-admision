<template>
    <div>
        <header class="header b-b padder-lg">
            <h2> Carga Académica Nivelación {{ciclo.descripcion}}</h2>
        </header>

        <section class="wrapper-lg">
            <section class="panel m-b-md">
                <section class="panel-body">

                    <raptor-table v-bind:url="raptorURL"
                                  v-bind:paginate="false"
                                  v-bind:pagination="pagination"
                                  ref="raptor">
                        <template scope="props" >
                            <table class="table table-striped">
                                <thead class="panel panel-heading">
                                    <tr>
                                        <th class="v-middle">Curso</th>
                                        <th class="v-middle text-center">Sección</th>
                                        <th class="v-middle text-center">Aula</th>
                                        <th class="v-middle text-center">Horario</th>
                                        <th class="v-middle text-center">Matriculados</th>
                                        <th class="v-middle text-center">Estado</th>
                                        <th class=""></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="item in props.data">
                                        <td class="v-middle">
                                            <div class="block text-primary bold h4 m-t-xs m-b-xs">
                                                {{item.cursoCiclo.curso.nombre}}
                                            </div>
                                            <div class="block">{{item.cursoCiclo.curso.codigo}}</div>
                                        </td>

                                        <td class="v-middle text-center">
                                            <div class="block"><strong>Sección:</strong> {{item.codigo}}</div>
                                            <div class="block"><strong>Horas:</strong> {{item.horasDictado}}</div>

                                        </td>

                                        <td class="v-middle text-center">
                                            <template v-if="item.aula">
                                                <span class="block bold">{{item.aula.codigo}}</span>
                                            </template>
                                        </td>

                                        <td class="v-middle text-center">
                                            <span class="block">
                                                Grupo {{item.grupoHoras.codigo}}
                                            </span>

                                            <a v-bind:class="classHorario(item)"
                                               v-on:click="setHorario(item)" class="pointer">
                                                <i class="fa fa-calendar fa-lg" aria-hidden="true"></i>
                                            </a>
                                        </td>

                                        <td class="v-middle text-center">
                                            <span class="circle-recorrido"
                                                  v-bind:class="classMatriculados(item)">
                                                {{item.matriculados}}
                                            </span>
                                        </td>

                                        <td class="v-middle text-center">
                                            <div v-bind:class="classEstado(item)" class="label">
                                                {{item.estadoEnum.value}}
                                            </div>
                                        </td>

                                        <td class="v-middle text-center">
                                            <div class="dropdown actions">
                                                <a class="dropdown-toggle" data-toggle="dropdown">
                                                    <i class="fa fa-cog"></i>
                                                </a>
                                                <ul class="dropdown-menu pull-right">
                                                    <li v-if="item.estado == 'CRE' " class="pointer"><a v-on:click="activar(item)">Activar</a></li>
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
    </div>

</template>
<script>
    Vue.component("multiselect", window.VueMultiselect.default);
    Vue.component('date-picker', window.VueBootstrapDatetimePicker);

    const ModalConfirm = httpVueLoader('/app/_componentes/ModalConfirm.vue');
    const ModalInfo = httpVueLoader('/app/_componentes/ModalInfo.vue');
    //const ModalReactivar = httpVueLoader('./ModalReactivar.vue');

    module.exports = {
        components: {
            ModalConfirm, ModalInfo
        },

        data() {
            return {
                idModalConfirm: "id-modal-confirm-carga-academica-nivelacion",
                pagination: {'total-items': 0, 'items-per-page': 1000, 'max-size': 3, 'boundary-link-numbers': true},
                ciclo: JSON.parse(cicloJson),
                raptorURL: `/${rutaModulo}/list`,
                configDate: {
                    format: 'DD/MM/YYYY',
                    locale: 'es'
                }
            };
        },

        mounted() {},
        computed: {},

        methods: {
            activar(item) {
                let config = VUE_MODAL.structConfirm({
                    id: this.idModalConfirm,
                    message: `¿Seguro que desea activar la sección ${item.codigo}?`,
                    okbtn: "Si, activar",
                    okclass: "btn-success",
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/changeEstado/ACT`,
                            modal: this.$refs.modalConfirm.getModal(),
                            raptor: this.$refs.raptorCursos,
                            body: {id: item.id}
                        }));
                    }
                });

                this.$refs.modalConfirm.open(config);
            },

            classEstado(item) {
                if (item.estado === 'CRE') {
                    return "label-default";
                } else if (item.estado === 'ACT') {
                    return "label-success";
                } else if (item.estado === 'BLO') {
                    return "label-warning";
                }
                return "label-danger";
            },
            classHorario(item) {
                if (item.horariosCurso.length === 0) {
                    return "text-danger";
                }
                return "text-primary";
            },
            classMatriculados(item) {
                if (item.matriculados === 0) {
                    return "bgr-danger";
                }
                return "bgr-success";
            },

            // metodos genericos
            activarNumeric: myUtils.activarNumeric,
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };

</script>