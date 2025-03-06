<template>
    <div>
        <header class="header b-b padder-lg">
            <div class="pull-right m-t-sm">
                <a v-bind:href="origen" class="btn btn-default">Regresar</a>
            </div>

            <h2 class="m-b-xs"> {{leccion.fecha}} - {{leccion.temaClase}}</h2>
            <h4 class="block m-b-xs m-t-xs text-primary">
                <strong>Curso:</strong> {{seccion.cursoCiclo.curso.nombre}}
            </h4>
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
                        <div slot="header">
                            <h4>
                                <span class="text-primary">{{leccion.inscritos}} inscritos</span> &nbsp;&nbsp; | &nbsp;&nbsp;
                                <span class="text-success">{{leccion.asistentes}} asistentes</span> &nbsp;&nbsp; | &nbsp;&nbsp;
                                <span class="text-danger">{{leccion.faltantes}} faltantes</span>
                            </h4>
                        </div>
                        <template scope="props" >
                            <table class="table table-striped">
                                <thead class="panel panel-heading">
                                    <tr>
                                        <th class="v-middle text-center"></th>
                                        <th class="v-middle text-center">Asistió</th>
                                        <th class="v-middle text-center">Matrícula</th>
                                        <th class="v-middle wd-70">Alumno</th>
                                        <th class=""></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="item,idx in props.data">
                                        <td class="v-middle text-center">
                                            {{(idx+1)}}
                                        </td>
                                        <td class="v-middle text-center">
                                            <label v-if="esDocente" class="switch">
                                                <input type="checkbox"
                                                       v-bind:checked="item.estado == 'ASISTIO' "
                                                       v-on:change="changeAsiste(item)" />
                                                <span></span>
                                            </label>
                                            <template v-else="">
                                                <i v-if="item.estado == 'ASISTIO' "
                                                   class="fa fa-check-square-o text-success fa-2x" aria-hidden="true"></i>
                                                <i v-else=""
                                                   class="fa fa-times-circle text-danger fa-2x" aria-hidden="true"></i>
                                            </template>
                                        </td>
                                        <td class="v-middle text-center">
                                            {{item.alumnoNivelacion.alumno.codigo}}
                                        </td>
                                        <td class="v-middle">
                                            <h3 class="m-t-xs m-b-xs" v-bind:class="classNombre(item)">
                                                {{item.alumnoNivelacion.alumno.persona.apellidosNombres}}
                                            </h3>
                                        </td>

                                        <td class="v-middle text-center">
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

    module.exports = {
        components: {
            ModalConfirm, ModalInfo
        },

        data() {
            return {
                idModalConfirm: "id-modal-confirm-asistencia-nivelacion",
                pagination: {'total-items': 0, 'items-per-page': 1000, 'max-size': 3, 'boundary-link-numbers': true},
                origen: origen,
                esDocente: esDocente,
                ciclo: JSON.parse(cicloJson),
                leccion: JSON.parse(leccionJson),
                seccion: leccion.cursoNivelacion,
                raptorURL: `/${rutaModulo}/${leccion.id}/asistentes`,
                configDate: {
                    format: 'DD/MM/YYYY',
                    locale: 'es'
                }
            };
        },

        mounted() {},
        computed: {},

        methods: {
            changeAsiste(item) {
                const payload = {
                    id: item.id,
                    estado: (item.estado === 'ASISTIO') ? 'INASISTENTE' : 'ASISTIO'
                };

                myUtils.axios(VUE_AXIOS.structModalClose({
                    url: `/${rutaModulo}/marcarAsistencia`,
                    raptor: this.$refs.raptor,
                    body: payload
                })).then(() => this.reloadSeccion());
            },
            reloadSeccion() {
                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/findLeccion`,
                    raptor: this.$refs.raptor,
                    body: {id: this.leccion.id}
                })).then((resp) => this.leccion = resp.data.data);
            },

            classNombre(item) {
                if (item.estado === 'INASISTENTE') {
                    return "text-danger";
                }
                return "";
            },

            // metodos genericos
            activarNumeric: myUtils.activarNumeric,
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };

</script>