<template>
    <div>

        <section class="panel-body">
            <table>
                <tbody>
                    <tr>
                        <td class="col-lg-2 v-middle">
                            <div v-if="alumno.persona.tipoFoto=='POSTUL'" 
                                 class="img-responsive img-thumbnail img-circle div-foto-list">
                                <img class="img-foto-list" v-bind:src="alumno.persona.rutaFoto" />
                            </div>

                            <img v-else-if="alumno.persona.tipoFoto=='COMUN'"
                                 class="img-responsive img-thumbnail img-circle" 
                                 v-bind:src="alumno.persona.rutaFoto" />

                            <img v-else="" 
                                 class="img-foto-tempo img-responsive img-thumbnail img-circle" v-bind:src="alumno.persona.rutaFoto" />
                        </td>

                        <td class="col-lg-6 v-middle">
                            <div class='text-left'>
                                <div class="row h5 m-t-n m-b" v-if="alumno.persona.paterno != '' ">
                                    <div class="col-lg-2 bold">Paterno</div>
                                    <div class="col-lg-10" v-text="alumno.persona.paterno"></div>
                                </div>
                                <div class="row h5 m-t-xs m-b" v-if="alumno.persona.materno != '' ">
                                    <div class="col-lg-2 bold">Materno</div>
                                    <div class="col-lg-10" v-text="alumno.persona.materno"></div>
                                </div>
                                <div class="row h5 m-t-xs m-b">
                                    <div class="col-lg-2 bold">Nombres</div>
                                    <div class="col-lg-10" v-text="alumno.persona.nombres"></div>
                                </div>
                                <div class="row h5 m-t-xs m-b" v-if="alumno.persona.sexo != '' ">
                                    <div class="col-lg-2 bold">Sexo</div>
                                    <div class="col-lg-10" v-text="alumno.persona.sexoEnum.value"></div>
                                </div>
                                <div class="row h5 m-t-xs m-b" v-if="alumno.persona.numeroDocIdentidad != '' ">
                                    <div class="col-lg-2 bold" v-text="alumno.persona.tipoDocumento.simbolo"></div>
                                    <div class="col-lg-10" v-text="alumno.persona.numeroDocIdentidad"></div>
                                </div>

                                <div class="row h5 m-t-xs m-b" v-if="alumno.tutorOcoordinador != '' ">
                                    <div class="col-lg-2 bold">Tutor</div>
                                    <div class="col-lg-10" v-text="alumno.tutorOcoordinador"></div>
                                </div>

                            </div>
                        </td>
                        
                        <td class="col-lg-4">
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
                        </td>
                    </tr>
                </tbody>
            </table>
            <hr/>

            <div class="row">
                <div class="col-lg-6 col-md-8 col-sm-12">
                    <table class="table table-striped">
                        <tbody>
                            <tr>
                                <td class="col-md-4">
                                    <strong> Número matrícula  </strong>
                                </td>
                                <td class="col-md-8">
                                    <span v-text='alumno.codigo'></span>
                                </td>
                            </tr>

                            <tr>
                                <td class="col-md-4">
                                    <strong> Modalidad estudio  </strong>
                                </td>
                                <td class="col-md-8">
                                    <span v-text='alumno.modalidadEstudio.nombre'></span>
                                </td>
                            </tr>

                            <tr v-if="alumno.modalidadEstudio.codigo == 'PRE' ">
                                <td>
                                    <strong> Facultad  </strong>
                                </td>
                                <td>
                                    <span v-text='alumno.carrera.facultad.nombre'></span>
                                </td>

                            </tr>

                            <tr v-if="alumno.carrera.codigo !== alumno.carrera.facultad.codigo">
                                <td>
                                    <strong v-if="alumno.modalidadEstudio.codigo == 'PRE' "> Especialidad  </strong>
                                    <strong v-if="alumno.modalidadEstudio.codigo == 'EPG' "> {{alumno.carrera.tipoEnum.value}}  </strong>
                                </td>
                                <td>
                                    <span v-text='alumno.carrera.nombre'></span>
                                </td>
                            </tr>

                            <tr v-if='alumno.carrera.orientacionCarrera.length > 0 '>
                                <td>
                                    <strong> Orientación  </strong>
                                </td>
                                <td >
                                    <form>
                                        <multiselect 
                                            v-model="alumno.orientacionCarrera" 
                                            v-bind:options='alumno.carrera.orientacionCarrera'
                                            v-bind:disabled="!showactions"
                                            label='nombre'
                                            track-by='id'
                                            placeholder="Seleccione una orientación"
                                            v-on:input="changeOrientacion(alumno.orientacionCarrera)">
                                        </multiselect>
                                    </form>
                                </td>

                            </tr>
                            <tr>
                                <td>
                                    <strong> Situación académica  </strong>
                                </td>
                                <td>
                                    <span v-text='alumno.situacionAcademica.codigo'></span>
                                    <span v-text='alumno.situacionAcademica.nombre'></span>
                                </td>

                            </tr>
                            <tr>
                                <td>
                                    <strong> Plan de estudios  </strong>
                                </td>
                                <td>
                                    <span v-if="alumno.planCurricular.id != '' ">
                                        {{alumno.planCurricular.cicloInicioVigencia.descripcion}}
                                        <span v-if="alumno.planCurricular.orientacionCarrera.id != '' ">
                                            ({{alumno.planCurricular.orientacionCarrera.nombre}})
                                        </span>
                                    </span>
                                    <span v-else="">No está especificado</span>
                                </td>

                            </tr>

                            <tr v-if="alumno.cicloIngreso.descripcion != '' ">
                                <td>
                                    <strong> Ciclo ingreso  </strong>
                                </td>
                                <td>
                                    <span v-text="alumno.cicloIngreso.descripcion"></span>
                                </td>
                            </tr>

                            <tr v-if="alumno.postulantePregrado.modalidadIngreso.nombre != '' ">
                                <td>
                                    <strong> Modalidad ingreso  </strong>
                                </td>
                                <td>
                                    <span v-text="alumno.postulantePregrado.modalidadIngreso.nombre"></span>
                                </td>
                            </tr>


                            <tr v-if="alumno.fechaMatricula">
                                <td>
                                    <strong>Fecha primera matrícula  </strong>
                                </td>
                                <td>
                                    <span v-text="alumno.fechaMatricula"></span>
                                </td>
                            </tr>


                            <tr>
                                <td>
                                    <strong> Ciclos cursados regulares </strong>
                                </td>
                                <td>
                                    <span v-text="alumno.ciclosRegularesTransient"></span>
                                </td>
                            </tr>


                            <tr v-if="alumno.fechaEgreso">
                                <td>
                                    <strong>Fecha egreso  </strong>
                                </td>
                                <td>
                                    <span v-text="alumno.fechaEgreso"></span>
                                </td>
                            </tr>

                            <tr v-if="alumno.resolucionTitulo">
                                <td>
                                    <strong> Título </strong>
                                </td>
                                <td>
                                    <span v-text="alumno.resolucionTitulo"></span>
                                </td>
                            </tr>

                            <tr v-if="alumno.fechaTitulo">
                                <td>
                                    <strong>Fecha título </strong>
                                </td>
                                <td>
                                    <span v-text="alumno.fechaTitulo"></span>
                                </td>
                            </tr>

                            <tr v-if="alumno.resolucionBachiller">
                                <td>
                                    <strong> Resolucion UNALM Bachiller  </strong>
                                </td>
                                <td>
                                    <span v-text="alumno.resolucionBachiller"></span>
                                </td>
                            </tr>

                            <tr v-if="alumno.fechaBachiller">
                                <td>
                                    <strong>Fecha Res. Bachiller</strong>
                                </td>
                                <td>
                                    <span v-text="alumno.fechaBachiller"></span>
                                </td>
                            </tr>
                            <tr v-if="alumno.resolucionBachillerFacultad">
                                <td>
                                    <strong> Resolucion Facultad Bachiller</strong>
                                </td>
                                <td>
                                    <span v-text="alumno.resolucionBachillerFacultad"></span>
                                </td>
                            </tr>
                            <tr v-if="alumno.fechaBachillerFacultad">
                                <td>
                                    <strong>Fecha Res. Fac. Bachiller  </strong>
                                </td>
                                <td>
                                    <span v-text="alumno.fechaBachillerFacultad"></span>
                                </td>
                            </tr> 

                        </tbody>
                    </table>
                </div>

                <div class="col-lg-6 col-md-8 col-sm-12">
                    <table class="table table-striped">
                        <tbody>
                            <tr>
                                <td class="col-md-6">
                                    <strong> Promedio ponderado currícula  </strong>
                                </td>
                                <td class="col-md-6">
                                    <span>{{verNota(alumno.promedioCarreraAcumulado)}}</span>
                                </td>
                            </tr>

                            <tr>
                                <td>
                                    <strong> Créditos aprobados currícula </strong>
                                </td>
                                <td>
                                    <span>{{alumno.creditosCarreraAprobados}}</span>
                                </td>
                            </tr>

                            <tr>
                                <td>
                                    <strong> Créditos cursados currícula </strong>
                                </td>
                                <td>
                                    <span>{{alumno.creditosCarreraCursados}}</span>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <strong> Promedio ponderado acumulado  </strong>
                                </td>
                                <td>
                                    <span>{{verNota(alumno.promedioAcumulado)}}</span>
                                </td>
                            </tr>

                            <tr>
                                <td>
                                    <strong> Créditos aprobados  </strong>
                                </td>
                                <td>
                                    <span>{{alumno.creditosAprobados}}</span>
                                </td>
                            </tr>
                            <tr v-if='alumno.creditosConvalidados > 0'>
                                <td>
                                    <strong> Créditos convalidados  </strong>
                                </td>
                                <td>
                                    <span>{{alumno.creditosConvalidados}}</span>
                                </td>
                            </tr>

                            <tr>
                                <td>
                                    <strong> Créditos cursados  </strong>
                                </td>
                                <td>
                                    <span>{{alumno.creditosCursados}}</span>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <strong> Ciclos cursados verano  </strong>
                                </td>
                                <td>
                                    <span>{{alumno.ciclosVeranosTransient}}</span>
                                </td>
                            </tr>
                            <tr v-if="validarPromedioGraduado(alumno.situacionAcademica)">
                                <td>
                                    <strong> Promedio ponderado egresado  </strong>
                                </td>
                                <td>
                                    <span>{{alumno.promedioPonderadoGraduacion}}</span>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </section>

        <modal-confirm ref="modalConfirm"></modal-confirm>
        <modal-info ref="modalInfo"></modal-info>

    </div>

</template>
<script>

    const ModalConfirm = httpVueLoader('/app/_componentes/ModalConfirm.vue');
    const ModalInfo = httpVueLoader('/app/_componentes/ModalInfo.vue');

    module.exports = {
        props: {
            alumno: {},
            showactions: {required: false, default: true}
        },

        components: {
            ModalConfirm, ModalInfo
        },

        data() {
            return {
                idModalConfirm: "id-modal-confirm-inicio-info",
                orientacionTmp: null
            };
        },

        mounted() {
            if (this.alumno !== null) {
                this.orientacionTmp = this.alumno.orientacionCarrera;
            }
        },

        methods: {
            changeOrientacion(orientacion) {
                let config = VUE_MODAL.structConfirm({
                    id: this.idModalConfirm,
                    message: "El cambio de orientación afectará el avance curricular y los cursos hábiles ¿Desea proceder con el cambio?",
                    okbtn: "Si, cambiar",
                    okclass: "btn-success",
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/${this.alumno.id}/${orientacion.id}/saveOrientacion`,
                            modal: this.$refs.modalConfirm.getModal()
                        })).then(() => this.$parent.reiniciarPlanes());
                    },
                    cancelaction: () => {
                        this.alumno.orientacionCarrera = this.orientacionTmp;
                    }
                });

                this.$refs.modalConfirm.open(config);
            },
            verNota(notax) {
                return APP.verNota(notax);
            },
            validarPromedioGraduado(situacionAcademica) {
                if (situacionAcademica.egresado || situacionAcademica.graduado) {
                    return true;
                }
                return false;
            }
        }
    };
</script>