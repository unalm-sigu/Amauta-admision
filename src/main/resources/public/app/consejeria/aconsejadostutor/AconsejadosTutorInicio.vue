<template>
    <div>
        <header class="header b-b padder-lg">

            <div class="pull-right m-t-md">                
                <button class="btn btn-primary btn-sm dropdown-toggle" type="button"  data-toggle="dropdown"> Acciones </button>
                <ul class="dropdown-menu pointer">
                    <li><a v-bind:href="rutaInforme()"> Descargar tutorados </a></li>
                    <li><a v-bind:href="crearInforme()"> {{menuInforme()}} </a></li>
                    <li><a v-on:click="verEncuesta"> Encuesta satisfacción </a></li>
                </ul>
            </div>

            <h2>Alumnos Tutorados {{ciclo.descripcion}}</h2>

            <span class="block h4 text-primary m-t-xxs m-b-xxs"><strong>Tutor:</strong> {{persona.nombreCompleto}}</span>    
            <span class="block m-b" v-if="departamento"><strong>Departamento Académico:</strong> {{departamento.nombre}}</span>
        </header>

        <section class="wrapper-lg">
            <section class="panel m-b-md">
                <section class="panel-body">

                    <raptor-table v-bind:url="aconsejadosURL" v-bind:preload="true" ref="raptorTuto">
                        <div slot="header">
                            <div class="col-md-8 preguntas-col">
                                <div >
                                    <div  class="col-md-2 text-center" v-bind:class="bgColorClass['matriculado']">
                                        <a  v-on:click="findAconsejado('matriculado')" class="text-success pointer" >
                                            <span class="h2 bold" v-text="count.matriculados"></span>
                                            <small class="block m-b-xs">Matriculados</small>
                                        </a>
                                    </div>
                                    <div  class="col-md-2 text-center" v-bind:class="bgColorClass['noMatriculado']">
                                        <a v-on:click="findAconsejado('noMatriculado')" class="text-black pointer" >
                                            <span class="h2 bold" v-text="count.noMatriculados"></span>
                                            <small class="block m-b-xs">No Matriculados</small>
                                        </a>
                                    </div>
                                    <div  class="col-md-2 text-center"  v-bind:class="bgColorClass['retirado']" >
                                        <a  v-on:click="findAconsejado('retirado')" class="text-primary pointer" >
                                            <span class="h2 bold" v-text="count.retiroCiclo"></span>
                                            <small class="block m-b-xs">Retirado Ciclo</small>
                                        </a>
                                    </div>
                                </div> 
                            </div>

                            <div class="col-md-4">
                                <div class="pull-right" v-if="informe.id">
                                    <div class="block v-middle text-center">
                                        <a v-bind:href="crearInforme()" class="pointer">
                                            <i v-bind:class="classInforme()" 
                                               class="fa fa-file-text fa-3x" aria-hidden="true"></i>
                                            <div v-bind:class="classInforme()">{{textoInforme()}}</div>
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <template scope="props" >
                            <table class="table table-striped">
                                <thead class="panel panel-heading">
                                    <tr>
                                        <th class="col-md-4 v-middle text-center"  colspan="2">Estudiante</th>
                                        <th class="col-md-3 v-middle text-center">Resumen Académico</th>
                                        <th class="col-md-1 v-middle text-center">Estado Matrícula</th>
                                        <th class="col-md-2 v-middle text-center">Última cita</th>
                                        <th class="col-md-2 v-middle text-center">Plan tutorial</th>
                                        <th class=""></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="item in props.data">

                                        <td class="v-middle">
                                            <template>
                                                <foto-persona v-bind:persona="item.alumno.persona"></foto-persona>
                                            </template>
                                        </td>

                                        <td class="v-middle">
                                            <template>
                                                <info-alumno v-bind:alumno="item.alumno" v-bind:persona="item.alumno.persona" v-bind:goto-info="true"></info-alumno>
                                            </template>

                                            <small v-if="item.alumno.persona.emailCompania" class="block text-primary">
                                                <i class="fa fa-envelope-o"></i> {{item.alumno.persona.emailCompania}}
                                            </small>

                                            <span v-if="item.beneficioUtlimoCiclo == 1" class="label label-success">Solicitó Beneficio de Útimo Ciclo</span>
                                        </td>

                                        <td class="v-middle">
                                            <span class="block" v-if="item.consejero">                                                            
                                                <span class=" bold"> Consejero: </span>
                                                {{item.consejero.colaborador.codigo}}
                                            </span>
                                            <span class="block" v-if="item.alumno.cicloIngreso != null">                                                            
                                                <span class=" bold"> Ciclo Ingreso: </span>
                                                {{item.alumno.cicloIngreso.descripcion}}
                                            </span>
                                            <span class="block">                                                            
                                                <span class=" bold"> Situación Academica: </span>
                                                {{item.alumno.situacionAcademica.nombre}}
                                            </span>
                                            <span class="block">                                                            
                                                <span class=" bold"> CCA: </span>
                                                {{item.alumno.creditosCursados}}
                                            </span>
                                            <span class="block">                                                            
                                                <span class=" bold"> CAPA: </span>
                                                {{item.alumno.creditosAprobados}}
                                            </span>
                                            <span class="block">                                                            
                                                <span class=" bold"> PPA: </span>
                                                {{item.alumno.promedioAcumulado}}
                                            </span>
                                        </td>

                                        <td class="v-middle text-center">
                                            <span v-if="item.estadoMatriculableEnum.name == 'MAT'">
                                                <small class="block">Créditos: {{item.creditosMatriculados}}</small>
                                                <small class="block">Cursos: {{item.cursosMatriculados}}</small>
                                                <span class="label label-success">{{item.estadoMatriculableEnum.value}}</span>
                                            </span>

                                            <span v-else="" class="label label-danger">
                                                {{item.estadoMatriculableEnum.value}}
                                            </span>
                                        </td>

                                        <td class="v-middle text-center">
                                            <template v-if="item.ultimoMensaje.id">
                                                <div class="m-b">
                                                    <span v-bind:class="classEstadoCita(item.ultimoMensaje)" class="label">
                                                        {{item.ultimoMensaje.estadoEnum.value}}
                                                    </span>
                                                </div>
                                                <div><b>Fecha:</b> {{item.ultimoMensaje.fecha}}</div>
                                                <div><b>Hora:</b> {{item.ultimoMensaje.hora}}</div>
                                            </template>
                                        </td>

                                        <td class="v-middle text-center">
                                            <template v-if="item.alumno.carrera.codigo == consejero.carrera.codigo">
                                                <div class="block">
                                                    <div v-bind:class="classTener(item.tienePlanes)" class="label">
                                                        <span v-if="item.tienePlanes">PLAN</span>
                                                        <span v-else="">S/Plan</span>
                                                    </div>
                                                </div>

                                                <div class="block">
                                                    <div v-bind:class="classTener(item.tieneCaracterizacion)" class="label">
                                                        <span v-if="item.tieneCaracterizacion">CARAC</span>
                                                        <span v-else="">S/Carac.</span>
                                                    </div>
                                                </div>

                                                <div class="block">
                                                    <div v-bind:class="classTener(item.tieneMapaEmpatia)" class="label">
                                                        <span v-if="item.tieneMapaEmpatia">MAPA</span>
                                                        <span v-else="">S/Mapa</span>
                                                    </div>
                                                </div>
                                            </template>
                                            
                                            <template v-else="">
                                                <div v-on:click="verMalaAsignacion(item)" class="block pointer">
                                                    <i class="fa fa-exclamation-circle fa-3x text-warning" aria-hidden="true"></i>
                                                </div>
                                            </template>

                                            <div class="block m-t">
                                                <span class="label label-primary">
                                                    {{item.estadoEnum.value}}
                                                </span>
                                            </div>
                                        </td>

                                        <td class="v-middle">
                                            <div class="dropdown actions">
                                                <a class="dropdown-toggle" data-toggle="dropdown">
                                                    <i class="fa fa-cog"></i>
                                                </a>
                                                <ul class="dropdown-menu pull-right">
                                                    <li><a v-bind:href="urlAcademico(item.alumno)">Información académica</a></li>
                                                    <li v-if='item.beneficioUtlimoCiclo == 0' class="pointer"><a v-on:click="solicitudBeneficio(item)">Beneficio útimo ciclo</a></li>
                                                    <li role="separator" class="divider"></li>
                                                    <li class="pointer"><a v-bind:href="planificar(item)">Planificar</a></li>
                                                    <li class="pointer"><a v-on:click="agendar(item)">Agendar</a></li>
                                                    <li class="pointer"><a v-on:click="derivar(item)">Derivar</a></li>
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
        <modal-encuesta ref="modalEncuesta"></modal-encuesta>
    </div>

</template>
<script>
    Vue.component("multiselect", window.VueMultiselect.default);
    Vue.component('date-picker', window.VueBootstrapDatetimePicker);

    const FotoPersona = httpVueLoader('/app/_componentes/FotoPersonaAlumno.vue');
    const InfoAlumno = httpVueLoader('/app/_componentes/InfoAlumno.vue');
    const ModalConfirm = httpVueLoader('/app/_componentes/ModalConfirm.vue');
    const ModalInfo = httpVueLoader('/app/_componentes/ModalInfo.vue');
    const ModalEncuesta = httpVueLoader('./ModalEncuesta.vue');

    module.exports = {
        components: {
            ModalConfirm, ModalInfo, FotoPersona, InfoAlumno, ModalEncuesta
        },
        data() {
            return {
                idModalConfirm: "id-modal-confirm-tutorados",
                idModalInfo: "id-modal-info-tutorados",
                aconsejadosURL: `/${rutaModulo}/list`,
                ciclo: JSON.parse(cicloJson),
                departamento: JSON.parse(departamentoJson),
                persona: JSON.parse(personaJson),
                consejero: JSON.parse(consejeroJson),
                informe: JSON.parse(informeJson),
                carreraSelect: {},
                seleccionado: '',
                bgColorClass: {sinconsejero: '', activo: ''},
                count: {matriculados: 0, noMatriculados: 0, retiroCiclo: 0},
                configDate: {
                    format: 'DD/MM/YYYY',
                    locale: 'es',
                    useCurrent: false
                }
            };
        },
        mounted() {
            this.countData();
            let query = this.$refs.raptorTuto.getParameterByName('queries[estado]');
            query = (query == null) ? '' : query;
            if (query != '') {
                this.$refs.raptorTuto.querie.push({name: 'estado', value: query});
                this.$refs.raptorTuto.repreload();
            }
        },
        computed: {
        },
        methods: {
            findAconsejado(tipo) {
                this.$refs.raptorTuto.querie = [];
                if (this.seleccionado === '') {
                    this.bgColorClass[tipo] = 'bg-light';
                    this.seleccionado = tipo;
                    this.$refs.raptorTuto.querie.push({name: 'estado', value: tipo});
                } else if (this.seleccionado !== '' && this.seleccionado !== tipo) {
                    this.bgColorClass[this.seleccionado] = '';
                    this.bgColorClass[tipo] = 'bg-light';
                    this.seleccionado = tipo;
                    this.$refs.raptorTuto.querie.push({name: 'estado', value: tipo});
                } else if (this.seleccionado !== '' && this.seleccionado === tipo) {
                    this.bgColorClass[this.seleccionado] = '';
                    this.seleccionado = '';
                    this.$refs.raptorTuto.changeUrl('queries[estado ]', null);
                }
                this.$refs.raptorTuto.loadRemoteData();
            },
            countData() {
                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/countData`,
                    body: {idCarrera: this.carreraSelect.id}
                })).then(response => {
                    this.count = response.data.data;
                });
            },
            solicitudBeneficio(item) {
                let $vue = this;

                var sexo = item.alumno.persona.sexo == 'M' ? 'al alumno ' : 'a la alumna ';
                var alumno = sexo + item.alumno.persona.apellidosNombres;
                var ciclo = item.cicloAcademico.descripcion;


                swal('¿Esta seguro que desea asignar el beneficio de último ciclo ' + alumno + ' en el ciclo ' + ciclo + ' ?', {
                    icon: "warning",
                    closeOnClickOutside: false,
                    closeOnEsc: false,
                    dangerMode: true,
                    buttons: {
                        cancel: {text: "Cancelar", closeModal: true, visible: true},
                        confirm: {text: "Aceptar", closeModal: false}
                    }
                }).then((value) => {
                    if (value != true) {
                        return;
                    }
                    $.ajax({
                        method: 'POST',
                        url: APP.url("consejeria/aconsejadostutor/solicitudBeneficio"),
                        data: JSON.stringify(item),
                        contentType: "application/json",
                        success: function (response) {
                            if (response.success) {
                                $vue.$refs.raptorTuto.loadRemoteData();
                                return  swal({text: response.message, icon: "success", button: false, timer: 1000});
                            } else {
                                return  swal({text: response.message, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
                            }
                        },
                        error: function () {
                            return  swal({text: Messages.errorComunicacion, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
                        }
                    });
                }).catch(err => {
                    if (err) {
                        swal(APP.errorComunicacion, "error");
                    } else {
                        swal.stopLoading();
                        swal.close();
                    }
                });
            },
            rutaInforme() {
                return `/${rutaModulo}/reporteAlumnosAconsejados`;
            },
            urlAcademico(item) {
                return `/academico/alumno/${item.id}/infoacademico${myUtils.getOrigenURL()}`;
            },
            planificar(item) {
                return `/${rutaModulo}/${item.alumno.id}/planificacion${myUtils.getOrigenURL()}`;
            },
            agendar(item) {
                let url = `/${rutaModulo}/${item.alumno.id}/agendarTutorado${myUtils.getOrigenURL()}`;
                this.verificarPlan(item, url);
            },
            derivar(item) {
                let url = `/${rutaModulo}/${item.alumno.id}/derivarTutorado${myUtils.getOrigenURL()}`;
                this.verificarPlan(item, url);
            },
            verificarPlan(item, url) {
                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/${item.alumno.id}/tienePlan`
                })).then((resp) => {
                    let info = resp.data.data;
                    if (!info.tienePlan) {
                        notify("Este alumno aún no tiene planificación tutorial", "error");
                    }
                    if (!info.tieneCaracteristicas) {
                        notify("Este alumno aún no tiene su caracterización ", "error");
                    }
                    if (!info.tieneMapaEmpatia) {
                        notify("Este alumno aún no tiene su mapa de empatía ", "error");
                    }
                    if (info.tienePlan && info.tieneCaracteristicas && info.tieneMapaEmpatia) {
                        location.href = url;
                    }
                });
            },
            verMalaAsignacion(item) {
                let info = '<h3 class="m-b">Diferente especialidades</h3>';
                info += `<p>El tutorado se encuentra en la especialidad de <span class="text-primary">${item.alumno.carrera.nombre}</span>. `;
                info += `Sin embargo, el tutor está asignado a la especialidad de `;
                info += `<span class="text-primary">${this.consejero.carrera.nombre}</span>.</p>`;

                this.$refs.modalInfo.open({
                    id: this.idModalInfo,
                    message: info
                });
            },
            crearInforme() {
                let url = `/${rutaModulo}/${this.consejero.id}/informefinal${myUtils.getOrigenURL()}`;
                return url;
            },
            verEncuesta() {
                this.$refs.modalEncuesta.open(this.consejero);
            },
            classTener(tiene) {
                if (tiene) {
                    return "label-success";
                }
                return "label-danger";
            },
            classEstadoCita(cita) {
                if (cita.estado === 'PENDIENTE') {
                    return "label-primary";
                } else if (cita.estado === 'NO_ASISTIO') {
                    return "label-danger";
                } else if (cita.estado === 'CANCELADA') {
                    return "label-danger";
                } else if (cita.estado === 'REALIZADA') {
                    return "label-success";
                } else if (cita.estado === 'REPROGRAMADA') {
                    return "label-default";
                }
                return "label-default";
            },
            classInforme() {
                if (this.informe.estado === 'PEN') {
                    return "text-warning";
                } else if (this.informe.estado === 'ACT') {
                    return "text-primary";
                } else if (this.informe.estado === 'OBS') {
                    return "text-danger";
                } else if (this.informe.estado === 'ACP') {
                    return "text-success";
                }
                return "";
            },
            textoInforme() {
                if (this.informe.estado === 'PEN') {
                    return "Editando";
                } else if (this.informe.estado === 'ACT') {
                    return "Enviado";
                } else if (this.informe.estado === 'OBS') {
                    return "Observado";
                } else if (this.informe.estado === 'ACP') {
                    return "Aprobado";
                }
                return "";
            },
            menuInforme() {
                if (this.informe.estado === 'ACT') {
                    return "Ver informe";
                } else if (this.informe.estado === 'OBS') {
                    return "Corregir informe";
                } else if (this.informe.estado === 'ACP') {
                    return "Ver informe";
                }
                return "Crear informe final";
            },

            // metodos genericos
            activarNumeric: myUtils.activarNumeric,
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };

</script>