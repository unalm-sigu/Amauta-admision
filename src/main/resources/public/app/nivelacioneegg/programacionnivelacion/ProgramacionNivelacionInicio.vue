<template>
    <div>
        <header class="header b-b padder-lg">
            <div class="pull-right m-t-sm">
                <div class="dropdown">
                    <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">
                        Acciones &nbsp; <span class="caret"></span>
                    </button>

                    <ul class="dropdown-menu dropdown-menu-right">
                        <li><a v-on:click.prevent="addCurso" class="dropdown-item pointer">Agregar sección</a></li>
                    </ul>
                </div>
            </div>

            <h2> Programación horarios cursos nivelación {{ciclo.descripcion}}</h2>
        </header>

        <section class="wrapper-lg">
            <section class="panel m-b-md">
                <section class="panel-body">

                    <raptor-table v-bind:url="raptorURL"
                                  ref="raptorCursos">
                        <template scope="props" >
                            <table class="table table-striped">
                                <thead class="panel panel-heading">
                                    <tr>
                                        <th class="v-middle">Curso</th>
                                        <th class="v-middle">Docente</th>
                                        <th class="v-middle text-center">Aula</th>
                                        <th class="v-middle text-center">Horario</th>
                                        <th class="v-middle text-center">Vac / Mat</th>
                                        <th class="v-middle text-center">Estado</th>
                                        <th class=""></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="item in props.data">
                                        <td class="v-middle">
                                            <div class="block text-primary bold">{{item.cursoCiclo.curso.nombre}}</div>
                                            <div class="block">{{item.cursoCiclo.curso.codigo}} - Horas: {{item.horasDictado}}</div>
                                            <div class="block"><strong>Sección:</strong> {{item.codigo}}</div>
                                        </td>

                                        <td class="v-middle">
                                            <span v-if="item.docente.codigo == 'N.N.' " class="text-danger bold">
                                                Desconocido
                                            </span>
                                            <template v-else="">
                                                {{item.docente.persona.apellidosNombres}}
                                            </template>
                                        </td>

                                        <td class="v-middle text-center">
                                            <template v-if="item.aula">
                                                <span class="block bold">{{item.aula.codigo}}</span>
                                                <span class="block">Cap.: {{item.aula.capacidadAula}}</span>

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
                                            <span class="block">
                                                {{item.vacantes}} / 
                                                <span v-bind:class="classMatriculados(item)">
                                                    {{item.matriculados}}
                                                </span>
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
                                                    <li v-if="item.estado == 'ACT' " class="pointer"><a v-on:click="bloquear(item)">Bloquear</a></li>
                                                    <li v-if="item.estado == 'ACT' " class="pointer"><a v-on:click="cancelar(item)">Cancelar</a></li>
                                                    <li v-if="item.estado == 'CAN' " class="pointer"><a v-on:click="reactivar(item)">Reactivar</a></li>
                                                    <li v-if="item.estado == 'BLO' " class="pointer"><a v-on:click="desbloquear(item)">Desbloquear</a></li>
                                                    <li class="divider"> </li>
                                                    <li v-if="item.estado != 'CAN' " class="pointer"><a v-on:click="changeDocente(item)">Cambiar docente</a></li>
                                                    <li v-if="item.estado != 'CAN' " class="pointer"><a v-on:click="changeAula(item)">Cambiar aula</a></li>
                                                    <li v-if="item.estado != 'CAN' " class="pointer"><a v-on:click="changeGrupoHoras(item)">Cambiar grupo</a></li>
                                                    <li v-if="item.estado != 'CAN' " class="pointer"><a v-on:click="changeVacantes(item)">Cambiar vacantes</a></li>
                                                    <li class="divider"> </li>
                                                    <li class="pointer"><a v-on:click="cambios(item)">Historial de cambios</a></li>
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
        <modal-add-curso ref="modalAddCurso"></modal-add-curso>
        <modal-add-horario ref="modalAddHorario"></modal-add-horario>
        <modal-change-docente ref="modalChangeDocente"></modal-change-docente>
        <modal-change-aula ref="modalChangeAula"></modal-change-aula>
        <modal-change-grupo ref="modalChangeGrupo"></modal-change-grupo>
        <modal-change-vacantes ref="modalChangeVacantes"></modal-change-vacantes>
        <modal-cambios ref="modalCambios"></modal-cambios>
        <modal-cancelar ref="modalCancelar"></modal-cancelar>
        <modal-reactivar ref="modalReactivar"></modal-reactivar>
    </div>

</template>
<script>
    Vue.component("multiselect", window.VueMultiselect.default);
    Vue.component('date-picker', window.VueBootstrapDatetimePicker);

    const ModalConfirm = httpVueLoader('/app/_componentes/ModalConfirm.vue');
    const ModalInfo = httpVueLoader('/app/_componentes/ModalInfo.vue');
    const ModalAddCurso = httpVueLoader('./ModalAddCurso.vue');
    const ModalAddHorario = httpVueLoader('./ModalAddHorario.vue');
    const ModalChangeDocente = httpVueLoader('./ModalChangeDocente.vue');
    const ModalChangeAula = httpVueLoader('./ModalChangeAula.vue');
    const ModalChangeGrupo = httpVueLoader('./ModalChangeGrupo.vue');
    const ModalChangeVacantes = httpVueLoader('./ModalChangeVacantes.vue');
    const ModalCambios = httpVueLoader('./ModalCambios.vue');
    const ModalCancelar = httpVueLoader('./ModalCancelar.vue');
    const ModalReactivar = httpVueLoader('./ModalReactivar.vue');

    module.exports = {
        components: {
            ModalConfirm, ModalInfo, ModalAddCurso, ModalAddHorario,
            ModalChangeDocente, ModalChangeAula, ModalChangeGrupo,
            ModalChangeVacantes, ModalCambios, ModalCancelar,
            ModalReactivar
        },

        data() {
            return {
                idModalConfirm: "id-modal-confirm-programacion-cursos-nivelacion",
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
            bloquear(item) {
                let config = VUE_MODAL.structConfirm({
                    id: this.idModalConfirm,
                    message: `¿Seguro que desea bloquear la sección ${item.codigo}?`,
                    okbtn: "Si, bloquear",
                    okclass: "btn-warning",
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/changeEstado/BLO`,
                            modal: this.$refs.modalConfirm.getModal(),
                            raptor: this.$refs.raptorCursos,
                            body: {id: item.id}
                        }));
                    }
                });

                this.$refs.modalConfirm.open(config);
            },
            desbloquear(item) {
                let config = VUE_MODAL.structConfirm({
                    id: this.idModalConfirm,
                    message: `¿Seguro que desea desbloquear la sección ${item.codigo}?`,
                    okbtn: "Si, desbloquear",
                    okclass: "btn-success",
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/changeEstado/D_BLO`,
                            modal: this.$refs.modalConfirm.getModal(),
                            raptor: this.$refs.raptorCursos,
                            body: {id: item.id}
                        }));
                    }
                });

                this.$refs.modalConfirm.open(config);
            },
            reactivar(item) {
                this.$refs.modalReactivar.open(item, this.$refs.raptorCursos);
            },
            cancelar(item) {
                this.$refs.modalCancelar.open(item, this.$refs.raptorCursos);
            },

            changeDocente(item) {
                this.$refs.modalChangeDocente.open(item, this.$refs.raptorCursos);
            },
            changeAula(item) {
                this.$refs.modalChangeAula.open(item, this.$refs.raptorCursos);
            },
            changeGrupoHoras(item) {
                this.$refs.modalChangeGrupo.open(item, this.$refs.raptorCursos);
            },
            changeVacantes(item) {
                this.$refs.modalChangeVacantes.open(item, this.$refs.raptorCursos);
            },
            cambios(item) {
                this.$refs.modalCambios.open(item);
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
                    return "text-danger";
                }
                return "text-primary";
            },

            addCurso() {
                this.$refs.modalAddCurso.open(this.$refs.raptorCursos);
            },
            setHorario(item) {
                this.$refs.modalAddHorario.open(item, this.$refs.raptorCursos);
            },

            // metodos genericos
            activarNumeric: myUtils.activarNumeric,
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };

</script>