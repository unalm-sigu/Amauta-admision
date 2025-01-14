<template>
    <div>
        <header class="header b-b padder-lg">

            <div class="pull-right m-t-sm">
                <div class="dropdown">
                    <a v-bind:href="origen" class="btn btn-default">Regresar</a>
                    <template v-if="seccion.estadoNotas != 'CER' ">
                        <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">
                            Acciones &nbsp; <span class="caret"></span>
                        </button>

                        <ul class="dropdown-menu dropdown-menu-right">
                            <li><a v-on:click.prevent="descargar" class="dropdown-item pointer">Descargar alumnos</a></li>
                        </ul>
                    </template>
                </div>
            </div>

            <h2 class="m-b-xs"> Alumnos matriculados - {{seccion.cursoCiclo.curso.nombre}}</h2>
            <h4 class="block m-t-xs text-primary">
                <strong>Sección:</strong> {{seccion.codigo}}  / 
                <strong>Ciclo:</strong> {{ciclo.descripcion}}
            </h4>
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
                                        <th class="v-middle text-center wd-60" colspan="2">Alumno</th>
                                        <th class="v-middle "></th>
                                        <th class="v-middle text-center wd-15">Estado</th>
                                        <th class="wd-1"></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="item,idx in props.data">
                                        <td class="v-middle">
                                            <template>
                                                <foto-persona
                                                    v-bind:persona="item.alumnoNivelacion.alumno.persona"
                                                    v-bind:modalidad="item.alumnoNivelacion.alumno.modalidadEstudio">
                                                </foto-persona>
                                            </template>
                                        </td>

                                        <td class="v-middle">
                                            <template>
                                                <info-alumno
                                                    v-bind:alumno="item.alumnoNivelacion.alumno"
                                                    v-bind:persona="item.alumnoNivelacion.alumno.persona"
                                                    v-bind:goto-info="false">
                                                </info-alumno>
                                            </template>
                                        </td>

                                        <td class="v-middle">
                                        </td>

                                        <td class="v-middle text-center">
                                            <div v-bind:class="classEstado(item)" class="label">
                                                {{item.estadoEnum.value}}
                                            </div>
                                        </td>

                                        <td class="v-middle text-center"></td>
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
                idModalConfirm: "id-modal-alumnado",
                pagination: {'total-items': 0, 'items-per-page': 1000, 'max-size': 3, 'boundary-link-numbers': true},
                origen: origen,
                ciclo: JSON.parse(cicloJson),
                seccion: JSON.parse(seccionJson),
                raptorURL: `/${rutaModulo}/${seccion.id}/listMatriculados`,
                configDate: {
                    format: 'DD/MM/YYYY',
                    locale: 'es'
                }
            };
        },

        mounted() {},
        computed: {},

        methods: {
            descargar() {
                //this.$refs.modalAddLeccion.open(this.$refs.raptor);
            },
            reloadSeccion() {
                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/findSeccion`,
                    body: {id: this.seccion.id}
                })).then((resp) => this.seccion = resp.data.data);
            },

            classEstado(item) {
                if (item.estado === 'MAT') {
                    return "label-success";
                } else if (item.estado === 'NMAT') {
                    return "label-warning";
                }
                return "label-danger";
            },

            // metodos genericos
            activarNumeric: myUtils.activarNumeric,
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };

</script>