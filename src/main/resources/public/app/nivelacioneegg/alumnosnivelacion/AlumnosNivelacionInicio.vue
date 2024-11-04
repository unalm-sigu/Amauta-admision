<template>
    <div>
        <header class="header b-b padder-lg">
            <div class="pull-right m-t-sm">
                <div class="dropdown">
                    <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">
                        Acciones &nbsp; <span class="caret"></span>
                    </button>

                    <ul class="dropdown-menu dropdown-menu-right">
                        <li><a v-on:click.prevent="crearAlumnos" class="dropdown-item pointer">Crear alumnos nuevos</a></li>
                        <li><a v-on:click.prevent="revisarAlumnos" class="dropdown-item pointer">Revisar alumnos</a></li>
                        <li><a v-on:click.prevent="addAlumno" class="dropdown-item pointer">Agregar alumno</a></li>
                    </ul>
                </div>
            </div>

            <h2> Alumnos para nivelación {{ciclo.descripcion}}</h2>
        </header>

        <section class="wrapper-lg">
            <section class="panel m-b-md">
                <section class="panel-body">

                    <raptor-table v-bind:url="alumnosURL"
                                  ref="raptorAlumnos">
                        <template scope="props" >
                            <table class="table table-striped">
                                <thead class="panel panel-heading">
                                    <tr>
                                        <th class="v-middle text-center" colspan="2">Alumno</th>
                                        <th class="v-middle wd-55">Temas</th>
                                        <th class="v-middle text-center">Estado</th>
                                        <th class=""></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="item in props.data">
                                        <td class="v-middle">
                                            <template>
                                                <foto-persona
                                                    v-bind:persona="item.alumno.persona"
                                                    v-bind:modalidad="item.alumno.modalidadEstudio">
                                                </foto-persona>
                                            </template>
                                        </td>

                                        <td class="v-middle">
                                            <template>
                                                <info-alumno
                                                    v-bind:alumno="item.alumno"
                                                    v-bind:persona="item.alumno.persona"
                                                    v-bind:goto-info="false">
                                                </info-alumno>
                                            </template>
                                        </td>

                                        <td class="v-middle">
                                            <span class="block m-b-xs"  >
                                                <span style="color:#fff">.</span>
                                                <transition name="slide-fade">
                                                    <span v-if="!item.ocultar" class="nombre-tema">{{item.descripcion}}</span>
                                                </transition>
                                            </span>

                                            <template v-for="tema in item.notasNivelaciones">
                                                <span class="circle-recorrido pointer text-center"
                                                      v-on:mouseover="verDetalle(item,tema)"
                                                      v-on:mouseout="noverDetalle(item)"
                                                      v-bind:class="classAprobado(tema)">
                                                    {{tema.temaExamen.codigo}}
                                                </span>
                                            </template>
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
                                                    <li v-if="item.estado == 'NMAT' " class="pointer"><a v-on:click="deshabilitar(item)">Deshabilitar</a></li>
                                                    <li v-if="item.estado == 'INH' " class="pointer"><a v-on:click="reactivar(item)">Reactivar alumno</a></li>
                                                    <li v-if="item.estado == 'NMAT' " class="pointer"><a v-on:click="revisar(item)">Revisar nota aprobatoria</a></li>
                                                    <li v-if="item.cambios.length > 0" class="pointer"><a v-on:click="verCambios(item)">Ver cambios</a></li>
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
        <modal-deshabilitar ref="modalDeshabilitar"></modal-deshabilitar>
        <modal-cambios ref="modalCambios"></modal-cambios>
        <modal-add-alumno ref="modalAddAlumno"></modal-add-alumno>
    </div>

</template>
<script>
    Vue.component("multiselect", window.VueMultiselect.default);
    Vue.component('date-picker', window.VueBootstrapDatetimePicker);

    const ModalConfirm = httpVueLoader('/app/_componentes/ModalConfirm.vue');
    const ModalInfo = httpVueLoader('/app/_componentes/ModalInfo.vue');
    const FotoPersona = httpVueLoader('/app/_componentes/FotoPersonaAlumno.vue');
    const InfoAlumno = httpVueLoader('/app/_componentes/InfoAlumno.vue');
    const ModalDeshabilitar = httpVueLoader('./ModalDeshabilitar.vue');
    const ModalCambios = httpVueLoader('./ModalCambios.vue');
    const ModalAddAlumno = httpVueLoader('./ModalAddAlumno.vue');

    module.exports = {
        components: {
            ModalConfirm, ModalInfo, FotoPersona, InfoAlumno,
            ModalDeshabilitar, ModalCambios, ModalAddAlumno
        },
        data() {
            return {
                idModalConfirm: "id-modal-confirm-alumnos-nivelacion",
                ciclo: JSON.parse(cicloJson),
                alumnosURL: `/${rutaModulo}/list`,
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
            classAprobado(item) {
                if (item.temaAprobado) {
                    return "bgr-success";
                }
                return "bgr-danger";
            },
            classEstado(item) {
                if (item.estado === 'NMAT') {
                    return "label-primary";
                } else if (item.estado === 'MAT') {
                    return "label-success";
                }
                return "label-danger";
            },
            crearAlumnos() {
                let config = VUE_MODAL.structConfirm({
                    id: this.idModalConfirm,
                    message: `¿Seguro que desea crear los alumnos para el ciclo ${this.ciclo.descripcion}?`,
                    okbtn: "Si, crear alumnos",
                    okclass: "btn-primary",
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/createAlumnos`,
                            modal: this.$refs.modalConfirm.getModal(),
                            raptor: this.$refs.raptorAlumnos
                        }));
                    }
                });

                this.$refs.modalConfirm.open(config);
            },
            revisarAlumnos() {
                let config = VUE_MODAL.structConfirm({
                    id: this.idModalConfirm,
                    message: `¿Seguro que desea revisar las notas aprobatorias de los alumnos registrados?`,
                    okbtn: "Si, revisar",
                    okclass: "btn-primary",
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/revisarTodosAlumnos`,
                            modal: this.$refs.modalConfirm.getModal(),
                            raptor: this.$refs.raptorAlumnos
                        }));
                    }
                });

                this.$refs.modalConfirm.open(config);
            },
            revisar(item) {
                let config = VUE_MODAL.structConfirm({
                    id: this.idModalConfirm,
                    message: `¿Seguro que desea revisar las notas aprobatorias del estudiante [${item.alumno.codigo}] ${item.alumno.persona.apellidosNombres}?`,
                    okbtn: "Si, revisar",
                    okclass: "btn-primary",
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/revisarAlumno`,
                            modal: this.$refs.modalConfirm.getModal(),
                            raptor: this.$refs.raptorAlumnos,
                            body: {id: item.id}
                        }));
                    }
                });

                this.$refs.modalConfirm.open(config);
            },
            addAlumno() {
                this.$refs.modalAddAlumno.open(this.$refs.raptorAlumnos);
            },
            deshabilitar(item) {
                this.$refs.modalDeshabilitar.open(item, this.$refs.raptorAlumnos);
            },
            verCambios(item) {
                this.$refs.modalCambios.open(item);
            },
            reactivar(item) {
                let config = VUE_MODAL.structConfirm({
                    id: this.idModalConfirm,
                    message: `¿Seguro que desea reactivar al estudiante [${item.alumno.codigo}] ${item.alumno.persona.apellidosNombres}?`,
                    okbtn: "Si, reactivar",
                    okclass: "btn-success",
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/habilitarAlumno`,
                            modal: this.$refs.modalConfirm.getModal(),
                            raptor: this.$refs.raptorAlumnos,
                            body: {id: item.id}
                        }));
                    }
                });

                this.$refs.modalConfirm.open(config);
            },

            verDetalle(item, tema) {
                item.descripcion = tema.temaExamen.nombre;
                item.descripcion += " : Puntaje " + tema.puntajeExamen + " de " + tema.temaCiclo.preguntas;
                item.ocultar = false;
            },
            noverDetalle(item) {
                item.ocultar = true;
            },

            // metodos genericos
            activarNumeric: myUtils.activarNumeric,
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };

</script>