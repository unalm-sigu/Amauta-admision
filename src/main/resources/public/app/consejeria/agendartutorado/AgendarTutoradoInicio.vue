<template>
    <div>
        <header class="header b-b padder-lg">
            <div class="btn-group pull-right">
                <a class="btn btn-default dropdown-toggle pull-right" v-bind:href="origen"> Regresar</a>
            </div>

            <h2> Citas agendadas con tutorado {{ciclo.descripcion}}</h2>

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
                                            <button v-on:click.prevent="addCita" class="btn btn-primary">Crear cita</button>
                                        </div>
                                    </div>
                                </div>
                            </div>

                        </div>

                    </template>

                    <div v-else="" class="alert alert-danger">
                        <h3 v-if="carrerasDiferentes">La especialidad del alumno es {{alumno.carrera.nombre}}, sin embargo la del tutor es {{consejero.carrera.nombre}}</h3>
                        <h3 v-else="">Usted no es el consejero del alumno seleccionado</h3>
                    </div>

                </section>
            </section>

            <section class="panel m-b-md" v-if="tienePermiso">
                <section class="panel-body">

                    <raptor-table v-bind:url="citasURL" ref="raptorCitas">
                        <template scope="props" >
                            <table class="table table-striped">
                                <thead class="panel panel-heading">
                                    <tr>
                                        <th class="col-md-1 v-middle text-center">Fecha/hora programada</th>
                                        <th class="col-md-1 v-middle text-center">Fecha/hora realizada</th>
                                        <th class="col-md-4 v-middle">Asunto</th>
                                        <th class="col-md-4 v-middle">Consejero</th>
                                        <th class="v-middle text-center">Objetivos</th>
                                        <th class="col-md-1 v-middle text-center">Fecha creación</th>
                                        <th class="col-md-1 v-middle text-center">Estado</th>
                                        <th class=""></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="item in props.data">
                                        <td class="v-middle text-center">
                                            <div class="text-primary block">{{item.fecha}}</div>
                                            <small class="block">{{item.hora}}</small>
                                        </td>

                                        <td class="v-middle text-center">
                                            <template v-if="item.estado == 'REALIZADA' ">
                                                <div class="text-primary block">{{item.fechaRealizada}}</div>
                                                <small class="block">{{item.horaInicio}} - {{item.horaFin}}</small>
                                            </template>

                                            <template v-if="item.estado == 'NO_ASISTIO' ">
                                                <span v-bind:class="classEstado(item)" class="label">{{item.estadoEnum.value}}</span>
                                            </template>
                                        </td>


                                        <td class="v-middle">
                                            <div>{{verTexto(item.asunto,80)}}</div>
                                        </td>
                                        <td class="v-middle">
                                            <div class="text-primary bold">{{item.consejero.colaborador.persona.apellidosNombres}}</div>
                                            <div>{{item.consejero.colaborador.persona.emailCompania}}</div>
                                        </td>
                                        <td class="v-middle text-center">
                                            <span v-on:click="verCita(item)" class="circle-recorrido bgr-success pointer">
                                                {{item.planesTutoriales.length}}
                                            </span>
                                        </td>
                                        <td class="v-middle text-center">
                                            <div class="text-primary">{{item.fechaRegistro.split(' ')[0]}}</div>
                                            <div>{{item.fechaRegistro.split(' ')[1]}}</div>
                                        </td>
                                        <td class="v-middle text-center">
                                            <div class="block">
                                                <span v-bind:class="classEstado(item)" class="label">{{item.estadoEnum.value}}</span>
                                            </div>

                                            <template v-if="item.motivoPostergacion">
                                                <i v-on:click="verMotivo(item)" class="fa fa-comments fa-lg pointer m-t-sm" aria-hidden="true"></i>
                                            </template>
                                            <template v-if="item.conclusiones">
                                                <i v-on:click="verConclusiones(item)" class="fa fa-comments fa-lg pointer m-t-sm" aria-hidden="true"></i>
                                            </template>
                                        </td>

                                        <td class="v-middle text-center">
                                            <div class="dropdown actions">
                                                <a class="dropdown-toggle" data-toggle="dropdown">
                                                    <i class="fa fa-cog"></i>
                                                </a>
                                                <ul class="dropdown-menu pull-right">
                                                    <li class="pointer"><a v-on:click="verCita(item)">Ver cita</a></li>
                                                    <li role="separator" class="divider"></li>
                                                    <li v-if="esEditable(item)" class="pointer"><a v-on:click="editar(item)">Editar cita</a></li>
                                                    <li v-if="esEditable(item)" class="pointer"><a v-on:click="asistencia(item)">Registrar asistencia</a></li>
                                                    <li v-if="esReprogramable(item)" class="pointer"><a v-on:click="reprogramar(item)">Reprogramar cita</a></li>
                                                    <li v-if="esEditable(item)" class="pointer"><a v-on:click="cancelar(item)">Cancelar cita</a></li>
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
        <modal-add-cita ref="modalAddCita"></modal-add-cita>
        <modal-editar-cita ref="modalEditarCita"></modal-editar-cita>
        <modal-postergar-cita ref="modalPostergarCita"></modal-postergar-cita>
        <modal-asistencia-cita ref="modalAsistenciaCita"></modal-asistencia-cita>
        <modal-cita-tutor ref="modalCitaTutor"></modal-cita-tutor>
    </div>

</template>
<script>
    Vue.component("multiselect", window.VueMultiselect.default);
    Vue.component('date-picker', window.VueBootstrapDatetimePicker);

    const FotoPersona = httpVueLoader('/app/_componentes/FotoPersonaAlumno.vue');
    const InfoAlumno = httpVueLoader('/app/_componentes/InfoAlumno.vue');
    const ModalConfirm = httpVueLoader('/app/_componentes/ModalConfirm.vue');
    const ModalInfo = httpVueLoader('/app/_componentes/ModalInfo.vue');
    const ModalAddCita = httpVueLoader('./ModalAddCita.vue');
    const ModalEditarCita = httpVueLoader('./ModalEditarCita.vue');
    const ModalPostergarCita = httpVueLoader('./ModalPostergarCita.vue');
    const ModalAsistenciaCita = httpVueLoader('./ModalAsistenciaCita.vue');
    const ModalCitaTutor = httpVueLoader('./ModalCitaTutor.vue');

    module.exports = {
        components: {
            ModalConfirm, ModalInfo, FotoPersona, InfoAlumno,
            ModalAddCita, ModalEditarCita, ModalPostergarCita,
            ModalAsistenciaCita, ModalCitaTutor
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
                idModalConfirm: "id-modal-confirm-agendar-tutorado",
                idModalInfo: "id-modal-info-agendar-tutorado",
                citasURL: `/${rutaModulo}/${alumnoBean.id}/allCitasTutorizadas`,
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
            addCita() {
                let config = {
                    alumno: this.alumno,
                    okbtn: "Crea cita",
                    tital: "Crea cita tutorizada"
                };

                this.$refs.modalAddCita.open(config, this.$refs.raptorCitas);
            },
            verCita(item) {
                let cita = JSON.parse(JSON.stringify(item));
                this.$refs.modalCitaTutor.open(cita);
            },
            classEstado(item) {
                if (item.estado === 'PENDIENTE') {
                    return "label-primary";
                } else if (item.estado === 'NO_ASISTIO') {
                    return "label-danger";
                } else if (item.estado === 'CANCELADA') {
                    return "label-danger";
                } else if (item.estado === 'REALIZADA') {
                    return "label-success";
                } else if (item.estado === 'REPROGRAMADA') {
                    return "label-default";
                }
                return "label-default";
            },
            verTexto(texto, tamanio) {
                let nuevo = texto.slice(0, tamanio);
                if (texto.length > tamanio) {
                    nuevo += "...";
                }
                return nuevo;
            },
            cancelar(item) {
                let config = VUE_MODAL.structConfirm({
                    id: this.idModalConfirm,
                    message: "¿Seguro que desea cancelar esta cita?",
                    okbtn: "Si, cancelar",
                    okclass: "btn-danger",
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/cancelarCitaTutorado`,
                            modal: this.$refs.modalConfirm.getModal(),
                            raptor: this.$refs.raptorCitas,
                            body: {id: item.id}
                        }));
                    }
                });

                this.$refs.modalConfirm.open(config);
            },
            verMotivo(item) {
                let info = '<h3 class="m-b">Motivo de la postegación</h3>';
                info += `<p>${item.motivoPostergacion}</p>`;

                this.$refs.modalInfo.open({
                    id: this.idModalInfo,
                    message: info
                });
            },
            verConclusiones(item) {
                let info = '<h3 class="m-b">Comentarios de la cita</h3>';
                info += `<p>${item.conclusiones}</p>`;

                this.$refs.modalInfo.open({
                    id: this.idModalInfo,
                    message: info
                });
            },
            editar(item) {
                let config = {
                    alumno: this.alumno,
                    cita: JSON.parse(JSON.stringify(item))
                };

                this.$refs.modalEditarCita.open(config, this.$refs.raptorCitas);
            },
            reprogramar(item) {
                let config = {
                    alumno: this.alumno,
                    cita: JSON.parse(JSON.stringify(item))
                };

                this.$refs.modalPostergarCita.open(config, this.$refs.raptorCitas);
            },
            asistencia(item) {
                let config = {
                    alumno: this.alumno,
                    cita: JSON.parse(JSON.stringify(item))
                };

                this.$refs.modalAsistenciaCita.open(config, this.$refs.raptorCitas);
            },
            esEditable(item) {
                return item.estado === 'PENDIENTE';
            },
            esReprogramable(item) {
                return ['PENDIENTE', 'CANCELADA'].includes(item.estado);
            },
            carrerasDiferentes() {
                if (!this.alumno.carrera) {
                    return false;
                }
                if (!this.alumno.carrera.codigo) {
                    return false;
                }
                if (!this.consejero.carrera) {
                    return false;
                }
                if (!this.consejero.carrera.codigo) {
                    return false;
                }

                return this.alumno.carrera.codigo !== this.consejero.carrera.codigo;
            },

            // metodos genericos
            activarNumeric: myUtils.activarNumeric,
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };

</script>