<template>
    <div>
        <header class="header b-b padder-lg">
            <div class="btn-group pull-right">
                <a class="btn btn-default dropdown-toggle pull-right" v-bind:href="origen"> Regresar</a>
            </div>

            <h2> Derivaciones del tutorado {{ciclo.descripcion}}</h2>

        </header>

        <section class="wrapper-lg">
            <section class="panel m-b-md">
                <section class="panel-body">

                    <template v-if="tienePermiso">

                        <div class="row">
                            <div class="col-md-2">
                                <foto-persona v-bind:persona="alumno.persona"></foto-persona>
                            </div>
                            <div class="col-md-10">
                                <div class="row">
                                    <div class="col-md-6">
                                        <info-alumno v-bind:persona="alumno.persona" v-bind:alumno="alumno"></info-alumno>
                                    </div>
                                    <div class="col-md-6">
                                        <div v-if="alumno.persona.emailCompania" class="block m-b-xs">
                                            <i class="fa fa-envelope" aria-hidden="true"></i>
                                            {{alumno.persona.emailCompania}}
                                        </div>
                                        <div v-if="alumno.persona.email" class="block m-b-xs">
                                            <i class="fa fa-envelope-o" aria-hidden="true"></i>
                                            {{alumno.persona.email}}
                                        </div>
                                        <div v-if="alumno.persona.celular" class="block m-b-xs">
                                            <i class="fa fa-phone" aria-hidden="true"></i>
                                            {{alumno.persona.celular}}
                                        </div>
                                        <div v-if="alumno.persona.telefono" class="block m-b-xs">
                                            <i class="fa fa-volume-control-phone" aria-hidden="true"></i>
                                            {{alumno.persona.telefono}}
                                        </div>
                                    </div>
                                </div>

                                <div class="row">
                                    <div class="col-md-10">
                                        <h3 class="bold m-t-sm">
                                            <span class="text-primary">Tutor:</span>
                                            {{consejero.colaborador.persona.apellidosNombres}}
                                        </h3>
                                    </div>
                                    <div v-if="esConsejero" class="col-md-2">
                                        <div class="pull-right">
                                            <button v-on:click.prevent="addDerivacion" class="btn btn-primary">Derivar a otra área</button>
                                        </div>
                                    </div>
                                </div>
                            </div>

                        </div>

                    </template>

                    <div v-else="" class="alert alert-danger">
                        <h3>Usted no es el consejero del alumno seleccionado</h3>
                    </div>

                </section>
            </section>

            <section class="panel m-b-md" v-if="tienePermiso">
                <section class="panel-body">

                    <raptor-table v-bind:url="derivasURL" ref="raptorDerivas">
                        <template scope="props" >
                            <table class="table table-striped">
                                <thead class="panel panel-heading">
                                    <tr>
                                        <th class="col-md-3 v-middle">Remitente</th>
                                        <th class="col-md-4 v-middle">Derivación</th>
                                        <th class="col-md-3 v-middle">Motivo</th>
                                        <th class="col-md-1 v-middle">Respuesta</th>
                                        <th class="col-md-1 v-middle text-center">Estado</th>
                                        <th class=""></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="item in props.data">
                                        <td class="v-middle">
                                            <div class="text-primary">{{item.personaRemitente.apellidosNombres}}</div>
                                            <div>{{item.tipoRemitenteDerivacion.nombre}}</div>
                                        </td>
                                        <td class="v-middle">
                                            <template v-if="item.tipoAtencionTutorado.grupoAtencion == 'ESPECIALIZADA' ">
                                                <div>Atención especializada</div>
                                            </template>
                                            <div class="text-primary">{{item.tipoAtencionTutorado.nombre}}</div>

                                            <template v-if="item.tipoAtencionTutorado.codigo == 'ASESORIA_CURSO' ">
                                                <div> De {{item.curso.codigo}} - {{item.curso.nombre}} </div>
                                            </template>
                                            <template v-if="item.tipoAtencionTutorado.codigo == 'SEMINARIO_REFORZAR' ">
                                                <div> En {{item.curso.codigo}} - {{item.curso.nombre}} </div>
                                            </template>
                                        </td>
                                        <td class="v-middle">
                                            <div>{{item.motivoDerivacion}}</div>
                                        </td>
                                        <td class="v-middle">

                                        </td>

                                        <td class="v-middle text-center">
                                            <div class="dropdown actions">
                                                <a class="dropdown-toggle" data-toggle="dropdown">
                                                    <i class="fa fa-cog"></i>
                                                </a>
                                                <ul class="dropdown-menu pull-right">
                                                    <li class="pointer"><a>Ver cita</a></li>
                                                    <li role="separator" class="divider"></li>
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
        <modal-add-derivacion ref="modalAddDerivacion"></modal-add-derivacion>
    </div>

</template>
<script>
    Vue.component("multiselect", window.VueMultiselect.default);
    Vue.component('date-picker', window.VueBootstrapDatetimePicker);

    const FotoPersona = httpVueLoader('/app/_componentes/FotoPersonaAlumno.vue');
    const InfoAlumno = httpVueLoader('/app/_componentes/InfoAlumno.vue');
    const ModalConfirm = httpVueLoader('/app/_componentes/ModalConfirm.vue');
    const ModalInfo = httpVueLoader('/app/_componentes/ModalInfo.vue');
    const ModalAddDerivacion = httpVueLoader('./ModalAddDerivacion.vue');

    module.exports = {
        components: {
            ModalConfirm, ModalInfo, FotoPersona, InfoAlumno,
            ModalAddDerivacion
        },
        data() {
            return {
                ciclo: JSON.parse(cicloJson),
                alumno: JSON.parse(alumnoJson),
                consejero: JSON.parse(consejeroJson),
                alumnoConsejero: JSON.parse(alumnoConsejeroJson),
                origen: origen,
                tienePermiso: tienePermiso,
                esConsejero: esConsejero,
                derivasURL: `/${rutaModulo}/${alumnoBean.id}/allDerivaciones`,
                idModalConfirm: "id-modal-confirm-derivar-tutorado",
                idModalInfo: "id-modal-info-derivar-tutorado",
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
            addDerivacion() {
                let config = {
                    alumno: this.alumno
                };
                this.$refs.modalAddDerivacion.open(config, this.$refs.raptorDerivas);
            },

            // metodos genericos
            activarNumeric: myUtils.activarNumeric,
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };

</script>