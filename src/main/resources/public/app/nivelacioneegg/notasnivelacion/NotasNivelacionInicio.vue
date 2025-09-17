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
                            <li><a v-on:click.prevent="cerrarActa" class="dropdown-item pointer">Cerrar acta de notas</a></li>
                        </ul>
                    </template>
                </div>
            </div>

            <h2 class="m-b-xs"> Registro Notas - {{seccion.cursoCiclo.curso.nombre}}</h2>
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
                                        <th class="v-middle text-center"></th>
                                        <th class="v-middle text-center">Matrícula</th>
                                        <th class="v-middle wd-50">Alumno</th>
                                        <th v-for="tex in examenes" class="v-middle text-center">
                                            {{tex.tipoExamenNivelacion.simbolo}} 
                                            <template v-if="!actaExamenAbierta">
                                                &nbsp;
                                                <i class="fa fa-pencil-square-o fa-lg text-primary pointer" 
                                                   v-on:click.prevent="abrirExamen(tex)" aria-hidden="true"></i>
                                            </template>
                                        </th>
                                        <th class="v-middle text-center">Nota final</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="item,idx in props.data">
                                        <td class="v-middle text-center">
                                            {{(idx+1)}}
                                        </td>

                                        <td class="v-middle text-center">
                                            {{item.alumnoNivelacion.alumno.codigo}}
                                        </td>
                                        <td class="v-middle">
                                            <h3 class="m-t-xs m-b-xs">
                                                {{item.alumnoNivelacion.alumno.persona.apellidosNombres}}
                                            </h3>
                                        </td>

                                        <td v-for="tex in examenes" class="v-middle text-center">
                                            <template v-if="existeNota(tex,item)">
                                                <template v-if="tex.estado == 'ABI' ">
                                                    <input v-model="getNota(tex,item).notaExamen" class="form-control" type="text" required="yes"
                                                           v-bind:class="classNota(tex,item)"
                                                           v-bind:ref="'nota-'+idx"
                                                           v-on:keyup.enter="sendNota(idx,tex)"/>
                                                </template>
                                                <template v-else="">
                                                    <span v-bind:class="classColorNota(getNota(tex,item))">
                                                        {{commas(getNota(tex,item).notaExamen)}}
                                                    </span>
                                                </template>
                                            </template>
                                            <span v-else="" class="text-danger">
                                                Sin datos
                                            </span>
                                        </td>

                                        <td class="v-middle text-center">
                                            <span v-if="hayNotaCurso(item)" class="h3"
                                                  v-bind:class="classColorNota(item)">
                                                {{commas(item.notaCurso)}}
                                            </span>
                                            <span v-else="" class="text-danger">
                                                Sin datos
                                            </span>
                                        </td>
                                    </tr>
                                    <tr v-if="seccion.estadoNotas != 'CER' ">
                                        <td colspan="3"></td>
                                        <td v-for="tex in examenes" class="v-middle text-center">
                                            <template v-if="tex.estado == 'ABI' ">
                                                <a v-on:click.prevent="cerrarExamen(tex)" class="btn btn-primary">Cerrar registro</a>
                                            </template>
                                        </td>
                                        <td>
                                            <template v-if="actasConNotas">
                                                <a v-on:click.prevent="cerrarActa" class="btn btn-success">Cerrar acta</a>
                                            </template>
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
                idModalConfirm: "id-modal-confirm-notas-nivelacion",
                pagination: {'total-items': 0, 'items-per-page': 1000, 'max-size': 3, 'boundary-link-numbers': true},
                origen: origen,
                ciclo: JSON.parse(cicloJson),
                seccion: JSON.parse(seccionJson),
                examenes: JSON.parse(examenesJson),
                raptorURL: `/${rutaModulo}/${seccion.id}/alumnos`,
                configDate: {
                    format: 'DD/MM/YYYY',
                    locale: 'es'
                }
            };
        },

        mounted() {},
        computed: {
            actaExamenAbierta() {
                if (this.seccion.estadoNotas === 'CER') {
                    return true;
                }
                let examenesAbiertos = this.examenes.filter(ex => ex.estado === "ABI");
                return examenesAbiertos.length > 0;
            },
            actasConNotas() {
                let examenesCerradas = this.examenes.filter(ex => ex.estado === "CER");
                return examenesCerradas.length === this.examenes.length;
            },
        },

        methods: {
            addLeccion() {
                this.$refs.modalAddLeccion.open(this.$refs.raptor);
            },
            verAsistentes(item) {
                const url = APP.url(`${rutaModulo}/${item.id}/asistencia${myUtils.getOrigenURL()}`);
                location.href = url;
            },
            sendNota(idx, examen) {
                let alumno = this.$refs.raptor.data[idx];
                let nota = alumno.examenesAlumno.find(exa => exa.examenCursoNivelacion.id === examen.id);
                console.log("nota=", nota)
                if (!this.isNumeric(nota.notaExamen)) {
                    notify("Por favor, ingresa un valor numérico.", "error");
                    nota.correcto = 2;
                    return; // Salir sin enviar la solicitud
                }

                let payload = {
                    id: nota.id,
                    notaExamen: nota.notaExamen
                };

                console.log("payload=", payload)

                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/registrarNota`,
                    raptor: this.$refs.raptor,
                    rejectError: false,
                    body: payload
                })).then((resp) => {
                    if (resp.data.success) {
                        nota.correcto = 1;
                        const nextInput = this.$refs["nota-" + (idx + 1)];
                        if (nextInput) {
                            nextInput[0].focus();
                        }
                    } else {
                        nota.correcto = 2;
                    }
                });
            },
            isNumeric(value) {
                return !isNaN(parseFloat(value)) && isFinite(value);
            },
            abrirExamen(item) {
                let configModal = VUE_MODAL.structConfirm({
                    id: this.idModalConfirm,
                    message: `¿Está seguro que desea abrir el ingreso de notas del ${item.tipoExamenNivelacion.nombre}?`,
                    okbtn: "Si, abrir",
                    okclass: "btn-success",
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/abrirActa`,
                            modal: this.$refs.modalConfirm.getModal(),
                            raptor: this.$refs.raptor,
                            body: {id: item.id}
                        })).then(() => this.reloadExamenes());
                    }
                });

                this.$refs.modalConfirm.open(configModal);
            },
            cerrarExamen(item) {
                let configModal = VUE_MODAL.structConfirm({
                    id: this.idModalConfirm,
                    message: `¿Está seguro que desea cerrar el ingreso de notas del ${item.tipoExamenNivelacion.nombre}?`,
                    okbtn: "Si, cerrar",
                    okclass: "btn-primary",
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/cerrarActa`,
                            modal: this.$refs.modalConfirm.getModal(),
                            raptor: this.$refs.raptor,
                            body: {id: item.id}
                        })).then(() => this.reloadExamenes());
                    }
                });

                this.$refs.modalConfirm.open(configModal);
            },
            hayNotaCurso(item) {
                if (item.notaCurso === null) {
                    return false;
                }
                return true;
            },
            existeNota(examen, item) {
                let nota = item.examenesAlumno.find(exa => exa.examenCursoNivelacion.id === examen.id);
                if (nota) {
                    return true;
                }
                return false;
            },
            getNota(examen, item) {
                return item.examenesAlumno.find(exa => exa.examenCursoNivelacion.id === examen.id);
            },
            cerrarActa() {
                let configModal = VUE_MODAL.structConfirm({
                    id: "id-modal-confirm-upload",
                    message: "¿Está seguro que desea cerrar el acta de notas?",
                    okbtn: "Si, cerrar",
                    okclass: "btn-success",
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/cerrarNotas`,
                            modal: this.$refs.modalConfirm.getModal(),
                            body: {id: this.seccion.id}
                        })).then(() => this.reloadSeccion());
                    }
                });

                this.$refs.modalConfirm.open(configModal);
            },
            reloadSeccion() {
                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/findSeccion`,
                    body: {id: this.seccion.id}
                })).then((resp) => this.seccion = resp.data.data);
            },
            reloadExamenes() {
                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/allExamenes`,
                    body: {id: this.seccion.id}
                })).then((resp) => this.examenes = resp.data.data);
            },

            classNota(examen, item) {
                let nota = item.examenesAlumno.find(exa => exa.examenCursoNivelacion.id === examen.id);
                if (nota.correcto === 1) {
                    return "bg-success text-dark";
                } else if (nota.correcto === 2) {
                    return "bg-danger text-dark";
                }
                return "";
            },
            classColorNota(nota) {
                if (nota.aprobado) {
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