<template>
    <div>
        <header class="header b-b padder-lg">
            <div class="pull-right m-t-sm">
                <div class="dropdown">
                    <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">
                        Acciones <span class="caret"></span>
                    </button>

                    <ul class="dropdown-menu dropdown-menu-right">
                        <li><a v-on:click.prevent="crearAlumnos" class="dropdown-item">
                                Crear alumnos nuevos
                            </a>
                        </li>
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
                                        <th class="v-middle">Temas</th>
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
                                                    {{tema.temaCiclo.temaExamen.codigo}}
                                                </span>
                                            </template>
                                        </td>

                                        <td class="v-middle text-center">
                                            <div class="dropdown actions">
                                                <a class="dropdown-toggle" data-toggle="dropdown">
                                                    <i class="fa fa-cog"></i>
                                                </a>
                                                <ul class="dropdown-menu pull-right">
                                                    <li class="pointer"><a href="#">Editar</a></li>
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
    const FotoPersona = httpVueLoader('/app/_componentes/FotoPersonaAlumno.vue');
    const InfoAlumno = httpVueLoader('/app/_componentes/InfoAlumno.vue');

    module.exports = {
        components: {
            ModalConfirm, ModalInfo, FotoPersona, InfoAlumno
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

            verDetalle(item, tema) {
                item.descripcion = tema.temaCiclo.temaExamen.nombre;
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