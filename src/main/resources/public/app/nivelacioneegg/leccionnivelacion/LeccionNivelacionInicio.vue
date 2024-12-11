<template>
    <div>
        <header class="header b-b padder-lg">
            <div class="pull-right m-t-sm">

                <div class="dropdown">
                    <a v-bind:href="origen" class="btn btn-default">Regresar</a>

                    <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">
                        Acciones &nbsp; <span class="caret"></span>
                    </button>

                    <ul class="dropdown-menu dropdown-menu-right">
                        <li><a v-on:click.prevent="addLeccion" class="dropdown-item pointer">Agregar lección</a></li>
                    </ul>
                </div>
            </div>

            <h2 class="m-b-xs"> Lecciones {{seccion.cursoCiclo.curso.nombre}}</h2>
            <h4 class="block m-t-xs text-primary">
                <strong>Sección:</strong> {{seccion.codigo}}  / 
                <strong>Ciclo:</strong> {{ciclo.descripcion}}
            </h4>
        </header>

        <section class="wrapper-lg">
            <section class="panel m-b-md">
                <section class="panel-body">

                    <raptor-table v-bind:url="raptorURL"
                                  ref="raptor">
                        <template scope="props" >
                            <table class="table table-striped">
                                <thead class="panel panel-heading">
                                    <tr>
                                        <th class="v-middle text-center">Fecha</th>
                                        <th class="v-middle text-center">Hora inicio</th>
                                        <th class="v-middle text-center">Tiempo</th>
                                        <th class="v-middle wd-40">Tema lección</th>
                                        <th class="v-middle text-center">Inscritos</th>
                                        <th class="v-middle text-center">Asistentes</th>
                                        <th class="v-middle text-center">Faltantes</th>
                                        <th class=""></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="item in props.data">
                                        <td class="v-middle text-center">
                                            <div class="block text-primary bold pointer"
                                                 v-on:click="verAsistentes(item)">
                                                {{item.fecha}}
                                            </div>
                                        </td>
                                        <td class="v-middle text-center">
                                            <div class="block">{{item.horaInicio.descripcion}}</div>
                                        </td>
                                        <td class="v-middle text-center">
                                            <div class="block">{{item.cantidadHoras}} horas</div>
                                        </td>

                                        <td class="v-middle">
                                            <div class="block">{{item.temaClase}}</div>
                                        </td>

                                        <td class="v-middle text-center">
                                            <div class="block">{{item.inscritos}}</div>
                                        </td>

                                        <td class="v-middle text-center">
                                            <div class="block">{{item.asistentes}}</div>
                                        </td>

                                        <td class="v-middle text-center">
                                            <div class="block">{{item.faltantes}}</div>
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
        <modal-add-leccion ref="modalAddLeccion"></modal-add-leccion>
    </div>

</template>
<script>
    Vue.component("multiselect", window.VueMultiselect.default);
    Vue.component('date-picker', window.VueBootstrapDatetimePicker);

    const ModalConfirm = httpVueLoader('/app/_componentes/ModalConfirm.vue');
    const ModalInfo = httpVueLoader('/app/_componentes/ModalInfo.vue');
    const ModalAddLeccion = httpVueLoader('./ModalAddLeccion.vue');

    module.exports = {
        components: {
            ModalConfirm, ModalInfo, ModalAddLeccion
        },

        data() {
            return {
                idModalConfirm: "id-modal-confirm-leccion-nivelacion",
                origen: origen,
                ciclo: JSON.parse(cicloJson),
                seccion: JSON.parse(seccionJson),
                raptorURL: `/${rutaModulo}/${seccion.id}/listLecciones`,
                configDate: {
                    format: 'DD/MM/YYYY',
                    locale: 'es'
                }
            };
        },

        mounted() {},
        computed: {},

        methods: {
            addLeccion() {
                this.$refs.modalAddLeccion.open(this.$refs.raptor);
            },
            verAsistentes(item) {
                const url = APP.url(`${rutaModulo}/${item.id}/asistencia${myUtils.getOrigenURL()}`);
                location.href = url;
            },

            // metodos genericos
            activarNumeric: myUtils.activarNumeric,
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };

</script>