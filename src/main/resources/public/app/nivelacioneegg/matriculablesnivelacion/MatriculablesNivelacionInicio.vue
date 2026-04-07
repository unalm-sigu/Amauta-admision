<template>
    <div>
        <header class="header b-b padder-lg">
            <div class="pull-right m-t-sm">
                <div class="dropdown">
                    <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">
                        Acciones &nbsp; <span class="caret"></span>
                    </button>

                    <ul class="dropdown-menu dropdown-menu-right">
                        <li><a v-on:click.prevent="crear" class="dropdown-item pointer">Generar matriculables</a></li>
                        <li><a v-on:click.prevent="inscribirMasivo" class="dropdown-item pointer">Matricular por lote</a></li>
                        <li v-if="ciclo.fechaMatriculaNivelacion">
                            <a v-on:click.prevent="inscribirParcial" class="dropdown-item pointer">Matricular por partes</a>
                        </li>
                    </ul>
                </div>
            </div>

            <h2> Alumnos matriculables {{ciclo.descripcion}}</h2>
        </header>

        <section class="wrapper-lg">
            <section class="panel m-b-xs">
                <section class="panel-body">

                    <div class="col-md-12">
                        <div class="row">
                            <div class="col-md-3 text-center" v-bind:class="bgColorClass['inscritos']">
                                <a v-on:click="verInscritos('inscritos')" class="text-success pointer" >
                                    <span class="h1 block bold" > {{resumen.inscritos}} </span>
                                    <small class="block m-b-xs">Matriculados</small>
                                </a>
                            </div>
                            <div class="col-md-3 text-center" v-bind:class="bgColorClass['pendientes']">
                                <a v-on:click="verInscritos('pendientes')" class="text-warning pointer" >
                                    <span class="h1 block bold" > {{resumen.pendientes}} </span>
                                    <small class="block m-b-xs">No matriculados</small>
                                </a>
                            </div>
                        </div>
                    </div>

                </section>
            </section>

            <section class="panel m-b-md m-t-xs">
                <section class="panel-body">

                    <raptor-table v-bind:url="raptorURL"
                                  v-bind:preload="false"
                                  ref="raptor">
                        <template scope="props" >
                            <table class="table table-striped">
                                <thead class="panel panel-heading">
                                    <tr>
                                        <th class="v-middle text-center" colspan="2">Alumno</th>
                                        <th class="v-middle ">Tema / Curso</th>
                                        <th class="v-middle ">Sección</th>
                                        <th class="v-middle text-center">Nota</th>
                                        <th class="v-middle text-center">Estado</th>
                                        <th class=""></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="item in props.data">
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
                                            <div class="block"><strong>Tema examen:</strong> {{item.temaExamen.nombre}}</div>
                                            <div class="block text-primary"> {{item.curso.codigo}} {{item.curso.nombre}}</div>
                                        </td>
                                        <td class="v-middle">
                                            <template v-if="item.cursoNivelacion">
                                                <div class="block"><strong>Sección:</strong> {{item.cursoNivelacion.codigo}}</div>
                                                <div class="block"><strong>Grupo:</strong> {{item.cursoNivelacion.grupoNivelacion.codigo}}</div>
                                                <div class="block"><strong>Plantilla:</strong> {{item.cursoNivelacion.plantilla.codigo}}</div>
                                                <div class="block" v-if="item.cursoNivelacion.aula">
                                                    <strong>Aula:</strong> {{item.cursoNivelacion.aula.codigo}}
                                                </div>
                                            </template>
                                        </td>

                                        <td class="v-middle text-center">
                                            <span v-if="item.notaCurso" class="h4"
                                                  v-bind:class="classColorNota(item)">
                                                {{commas(item.notaCurso)}}
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
                                                    <li v-if="item.estado == 'MAT' " class="pointer"><a v-on:click="retirar(item)">Retirar</a></li>
                                                    <li v-if="item.estado == 'NMAT' " class="pointer"><a v-on:click="inscribir(item)">Matricular</a></li>
                                                    <li class="pointer"><a v-on:click="infoAcad(item)">Información académica</a></li>
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
        <modal-matricular ref="modalMatricular"></modal-matricular>
    </div>

</template>
<script>
    Vue.component("multiselect", window.VueMultiselect.default);
    Vue.component('date-picker', window.VueBootstrapDatetimePicker);

    const ModalConfirm = httpVueLoader('/app/_componentes/ModalConfirm.vue');
    const ModalInfo = httpVueLoader('/app/_componentes/ModalInfo.vue');
    const FotoPersona = httpVueLoader('/app/_componentes/FotoPersonaAlumno.vue');
    const InfoAlumno = httpVueLoader('/app/_componentes/InfoAlumno.vue');
    const ModalMatricular = httpVueLoader('./ModalMatricular.vue');

    module.exports = {
        components: {
            ModalConfirm, ModalInfo, FotoPersona, InfoAlumno,
            ModalMatricular
        },

        data() {
            return {
                seleccionado: '',
                idModalConfirm: "id-modal-confirm-matriculables-nivelacion",
                ciclo: JSON.parse(cicloJson),
                raptorURL: `/${rutaModulo}/list`,
                bgColorClass: {inscritos: '', pendientes: ''},
                resumen: {inscritos: 0, pendientes: 0},
                configDate: {
                    format: 'DD/MM/YYYY',
                    locale: 'es'
                }
            };
        },

        mounted() {
            let $vue = this;
            let tipo = $vue.$refs.raptor.getParameterByName('queries[situacion]');
            tipo = (tipo === null) ? '' : tipo;
            if (tipo !== '') {
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;
                $vue.$refs.raptor.querie.push({name: 'situacion', value: tipo});
            }
            $vue.$refs.raptor.repreload();
            this.loadResumen();
        },
        computed: {},

        methods: {
            loadResumen() {
                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/resumen`
                })).then((resp) => this.resumen = resp.data.data);
            },

            verInscritos(tipo) {
                let $vue = this;
                if ($vue.seleccionado === '') {
                    $vue.bgColorClass[tipo] = 'bg-light';
                    $vue.seleccionado = tipo;

                    $vue.$refs.raptor.querie.push({name: 'situacion', value: tipo});
                    $vue.$refs.raptor.loadRemoteData();

                } else if ($vue.seleccionado !== '' && $vue.seleccionado !== tipo) {
                    $vue.bgColorClass[$vue.seleccionado] = '';
                    $vue.bgColorClass[tipo] = 'bg-light';
                    $vue.seleccionado = tipo;

                    $vue.$refs.raptor.querie.push({name: 'situacion', value: tipo});
                    $vue.$refs.raptor.loadRemoteData();

                } else if ($vue.seleccionado !== '' && $vue.seleccionado === tipo) {
                    $vue.bgColorClass[$vue.seleccionado] = '';
                    $vue.seleccionado = '';

                    $vue.$refs.raptor.querie = [];
                    $vue.$refs.raptor.changeUrl('queries[situacion]', null);
                    $vue.$refs.raptor.loadRemoteData();
                }
            },

            crear() {
                let config = VUE_MODAL.structConfirm({
                    id: this.idModalConfirm,
                    message: `¿Seguro que desea crear los matriculables de nivelación del ${this.ciclo.descripcion}?`,
                    okbtn: "Si, generar",
                    okclass: "btn-success",
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/generarMatriculables`,
                            modal: this.$refs.modalConfirm.getModal(),
                            raptor: this.$refs.raptor
                        })).then(() => this.loadResumen());
                    }
                });

                this.$refs.modalConfirm.open(config);
            },
            inscribirMasivo() {
                let config = VUE_MODAL.structConfirm({
                    id: this.idModalConfirm,
                    message: `¿Seguro que desea inscribir en sus cursos a los matriculables de nivelación del ${this.ciclo.descripcion}?`,
                    okbtn: "Si, inscribir",
                    okclass: "btn-success",
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/matriculaMasivaTipo2`,
                            modal: this.$refs.modalConfirm.getModal(),
                            raptor: this.$refs.raptor
                        })).then(() => this.loadResumen());
                    }
                });

                this.$refs.modalConfirm.open(config);
            },
            inscribirParcial() {
                let config = VUE_MODAL.structConfirm({
                    id: this.idModalConfirm,
                    message: `¿Seguro que desea inscribir en los cursos que haya espacio a los matriculables de nivelación del ${this.ciclo.descripcion}?`,
                    okbtn: "Si, buscar espacios",
                    okclass: "btn-danger",
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/matriculaMasivaTipo3`,
                            modal: this.$refs.modalConfirm.getModal(),
                            raptor: this.$refs.raptor
                        })).then(() => this.loadResumen());
                    }
                });

                this.$refs.modalConfirm.open(config);
            },

            retirar(item) {
                const payload = {
                    id: item.id,
                    cursoNivelacion: {id: item.cursoNivelacion.id}
                };

                let config = VUE_MODAL.structConfirm({
                    id: this.idModalConfirm,
                    message: `¿Seguro que desea retirar del curso ${item.curso.nombre} al alumno ${item.alumnoNivelacion.alumno.codigo}?`,
                    okbtn: "Si, retirar",
                    okclass: "btn-danger",
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/retirarCurso`,
                            modal: this.$refs.modalConfirm.getModal(),
                            raptor: this.$refs.raptor,
                            body: payload
                        })).then(() => this.loadResumen());
                    }
                });

                this.$refs.modalConfirm.open(config);
            },
            inscribir(item) {
                this.$refs.modalMatricular.open(item, this.$refs.raptor);
            },
            infoAcad(item) {
                let url = APP.url(`academico/alumno/${item.alumnoNivelacion.alumno.id}/infoacademico${myUtils.getOrigenURL()}`);
                location.href = url;
            },

            classEstado(item) {
                if (item.estado === 'NMAT') {
                    return "label-warning";
                } else if (item.estado === 'MAT') {
                    return "label-success";
                }
                return "label-danger";
            },
            classColorNota(item) {
                if (item.aprobado) {
                    return "text-primary";
                }
                return "text-danger";
            },

            // metodos genericos
            activarNumeric: myUtils.activarNumeric,
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };

</script>