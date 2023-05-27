<template>
    <div>

        <section class="panel-body m-t-sm">
            <div class="m-b-sm">        
                <h3 class="m-t-n" style="display: inline-block" v-text="titulo"></h3>
                <div class="pull-right m-t-n">
                </div>
            </div>
            <div v-if="cursos.length === 0">
                <div class="row">
                    <span class="text-muted col-md-12">
                        No tiene cursos matriculados en el ciclo
                    </span>
                </div>
            </div>
            <div v-else="">
                <div class="row m-b-md">
                    <div class="col-md-3 col-md-offset-2">
                        <div class="col-md-7 text-right">
                            <span > Cantidad de cursos: </span>
                        </div>
                        <div class="col-md-5 text-left">
                            <span class="estado-blue" v-text="resumen.cursosMatriculados"></span>
                        </div>
                    </div>
                    <div class="col-md-3 col-md-offset-2">
                        <div class="col-md-7 text-right">
                            <span > Cantidad de créditos: </span>
                        </div>
                        <div class="col-md-5 text-left">
                            <span class="estado-blue" v-text="resumen.creditosMatriculados"></span>
                        </div>
                    </div>
                </div>
                <table class="table table-body-hover">
                    <thead>
                        <tr>
                            <th class="col-md-5 v-middle text-left">Curso</th>
                            <th class="v-middle text-center">Créditos</th>
                            <th></th>
                            <th class="col-md-1 v-middle text-center">Sección</th>
                            <th class="v-middle text-center">Grupo</th>
                            <th class="v-middle text-center">Aula</th>
                            <th class="col-md-4 v-middle text-left">Docente</th>
                            <th class="col-md-1 v-middle text-center">Nota Final</th>
                            <th class="col-md-1 v-middle text-center">Estado</th>
                        </tr>
                    </thead>
                    <tbody  v-for="item in cursos" v-bind:key="item.id">
                        <tr>
                            <td v-bind:rowspan="item.curso.tipoCurso === 'TEOPRA' ? 2 : 1"
                                class="v-middle">
                                <span class="block m-t-sm">
                                    <span class="text-left h4 text-primary bold" v-text="item.curso.nombre"></span>
                                    <span class="block text-muted p-no-margin">
                                        {{item.curso.codigo}} &nbsp;
                                        <i class="fa fa-bookmark text-primary"></i> {{item.curso.tpc}}
                                    </span>
                                    <small class="block text-muted m-b-sm">
                                        Dpto: {{item.curso.departamentoAcademico.nombre}}
                                    </small>
                                </span>
                            </td>
                            <td v-bind:rowspan="item.curso.tipoCurso === 'TEOPRA' ? 2 : 1"
                                class="text-center v-middle">
                                <span class="estado-blue">{{item.creditos}}</span>
                            </td>
                            <td class="text-center v-middle">
                                <div v-for="(matSecc,idx) in item.matriculaSeccion" v-if="idx==0">
                                    <span v-if="matSecc.seccion.tipoSeccion==='PCUR'" class="fa fa-flask"></span>
                                    <span v-else-if="matSecc.seccion.tipoSeccion==='PRA'" class="fa fa-flask"></span>
                                    <span v-else="" class="fa fa-book"></span>
                                </div>
                            </td>
                            <td class="text-center v-middle">
                                <div v-for="(matSecc,idx) in item.matriculaSeccion" v-if="idx==0">
                                    <span v-text="matSecc.seccion.codigo2"></span>
                                </div>
                            </td>
                            <td class="text-center v-middle">
                                <div v-for="(matSecc,idx) in item.matriculaSeccion" v-if="idx==0">
                                    <span v-text="matSecc.seccion.grupoHoras.codigo"></span>
                                </div>
                            </td>
                            <td class="text-center v-middle">
                                <div v-for="(matSecc,idx) in item.matriculaSeccion" v-if="idx==0">
                                    <span v-text="matSecc.seccion.aula.codigo"></span>
                                </div>
                            </td>
                            <td class="v-middle">
                                <div v-for="(matSecc,idx) in item.matriculaSeccion" v-if="idx==0">
                                    <div v-for="(docSecc,ind) in matSecc.seccion.docenteSeccion" class="block">
                                        <span v-if="docSecc.docente.codigo !== 'N.N.'" 
                                              v-text="docSecc.docente.codigo + ' - '+ docSecc.docente.persona.nombreCompleto"></span>
                                        <span v-else="" v-text="docSecc.docente.codigo + ' - Desconocido'"></span>
                                    </div>
                                </div>
                            </td>

                            <td v-bind:rowspan="item.curso.tipoCurso === 'TEOPRA' ? 2 : 1" class="v-middle text-center">
                                <span v-bind:class="colornota(item.notaFinal)" v-text="item.notaFinal"></span>
                            </td>

                            <td v-bind:rowspan="item.curso.tipoCurso === 'TEOPRA' ? 2 : 1" class="v-middle text-center">
                                <span class="label" v-bind:class="labelclass(item)" v-text="item.estadoEnum.value"></span>
                            </td>
                        </tr>
                        <tr v-if="item.curso.tipoCurso === 'TEOPRA'">
                            <td class="text-center v-middle">
                                <div v-for="(matSecc,idx) in item.matriculaSeccion" v-if="idx==1">
                                    <span v-if="matSecc.seccion.tipoSeccion === 'PCUR' " class="fa fa-flask"></span>
                                    <span v-else-if="matSecc.seccion.tipoSeccion === 'PRA' " class="fa fa-flask"></span>
                                    <span v-else="" class="fa fa-book"></span>
                                </div>
                            </td>
                            <td class="text-center v-middle">
                                <div v-for="(matSecc,idx) in item.matriculaSeccion" v-if="idx==1">
                                    <span v-text="matSecc.seccion.codigo2"></span>
                                </div>
                            </td>
                            <td class="text-center v-middle">
                                <div v-for="(matSecc,idx) in item.matriculaSeccion" v-if="idx==1">
                                    <span v-text="matSecc.seccion.grupoHoras.codigo"></span>
                                </div>
                            </td>
                            <td class="text-center v-middle">
                                <div v-for="(matSecc,idx) in item.matriculaSeccion" v-if="idx==1">
                                    <span v-text="matSecc.seccion.aula.codigo"></span>
                                </div>
                            </td>
                            <td class="v-middle">
                                <div v-for="(matSecc,idx) in item.matriculaSeccion" v-if="idx==1">
                                    <div v-for="(docSecc,ind) in matSecc.seccion.docenteSeccion" class="block">
                                        <span v-if="docSecc.docente.codigo !== 'N.N.'" 
                                              v-text="docSecc.docente.codigo + ' - '+ docSecc.docente.persona.nombreCompleto"></span>
                                        <span v-else="" v-text="docSecc.docente.codigo + ' - Desconocido'"></span>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
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
            alumno: {}
        },

        components: {
            ModalConfirm, ModalInfo
        },

        data() {
            return {
                cursos: [],
                ciclo: {},
                resumen: {}
            };
        },

        computed: {
            titulo() {
                return 'Cursos Matriculados ' + this.ciclo.descripcion;
            }
        },

        mounted() {
        },

        methods: {
            obtenerDatos() {
                let $vue = this;
                $.ajax({
                    method: 'GET',
                    url: APP.url('academico/alumno/' + $vue.alumno.id + '/cursosmatriculados'),
                    contentType: "application/json",
                    success: function (response) {
                        $vue.cursos = response.data.cursos;
                        $vue.ciclo = response.data.ciclo;
                        $vue.resumen = response.data.resumen;
                    }
                });
            },
            colornota(nota) {
                let $vue = this;
                if ($vue.alumno.modalidadEstudio.nombre === 'Posgrado') {
                    return {
                        'text-danger': nota < 13
                    };
                } else {
                    return {
                        'text-danger': nota < 11
                    };
                }
            },
            displayCreditos(item) {
                if (item.curso.creditos === 1) {
                    return "1 crédito";
                } else {
                    return item.curso.creditos + " créditos";
                }
            },
            labelclass(item) {
                return {
                    'label-success': item.estado === 'MAT',
                    'label-warning': item.estado === 'PMAT',
                    'label-danger': item.estado === 'RCU' || item.estado === 'RET' || item.estado === 'RCI'
                };
            }
        }
    };
</script>