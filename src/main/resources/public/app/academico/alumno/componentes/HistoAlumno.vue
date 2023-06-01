<template>
    <div>

        <section class="panel-body m-t-md">

            <div class="m-b-sm">
                <h3 class="m-t-n" style="display: inline-block" v-if="showTitle">Historial Académico</h3>
                <div class="pull-right m-t-n" v-if="showactions" style="margin-bottom: 15px;">

                    <div class="dropdown">
                        <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">
                            Acciones <span class="caret"></span>
                        </button>

                        <ul class="dropdown-menu dropdown-menu-right">
                            <li v-if="puedeCalcular">
                                <a class="dropdown-item" href="#" v-on:click.prevent="calcularPromedio">
                                    Calcular Promedios
                                </a>
                            </li>
                            <li><a v-on:click.prevent="generarReporteHistorial" href="#" class="dropdown-item">
                                    Generar Reporte
                                </a>
                            </li>
                        </ul>
                    </div>
                    <div v-if="!puedeCalcular">
                        <button class="btn btn-primary hide" type="button">&nbsp;&nbsp;&nbsp;</button>
                    </div>
                </div>
            </div>

            <div class="row m-t-md m-b-sm">
                <div class="col-sm-2">
                    <label class="col-sm-6 control-label">Sólo promedios</label>
                    <div class="col-sm-6">
                        <label class="switch">
                            <input type="checkbox" v-model="typeSearch4" v-on:change="changeSearch4()" />
                            <span></span>
                        </label>
                    </div>
                </div>

                <div class="col-sm-2" v-if="verInfo != 4">
                    <label class="col-sm-6 control-label">Sólo aprobados</label>
                    <div class="col-sm-6">
                        <label class="switch">
                            <input type="checkbox" v-model="typeSearch" v-on:change="changeSearch()" />
                            <span></span>
                        </label>
                    </div>
                </div>

                <div class="col-sm-2" v-if="verInfo != 4">
                    <label class="col-sm-6 control-label">Listado general</label>
                    <div class="col-sm-6">
                        <label class="switch">
                            <input type="checkbox" v-model="typeSearch2" v-on:change="changeSearch2()" />
                            <span></span>
                        </label>
                    </div>
                </div>

                <div class="col-sm-2" v-show="general" v-if="verInfo != 4">
                    <label class="col-sm-6 control-label">Solo un ciclo</label>
                    <div class="col-sm-6">
                        <label class="switch">
                            <input type="checkbox" v-model="typeSearch3" v-on:change="changeSearch3()" />
                            <span></span>
                        </label>
                    </div>
                </div>

                <div class="col-sm-4" v-show="general" v-if="verInfo != 4">
                    <label class="col-sm-3 control-label">Ciclos</label>
                    <div class="col-sm-9">
                        <multiselect v-model="cicloSelect" v-bind:options='promedios' label='nombre' track-by='id'
                                     deselect-label="" select-label="" v-bind:allow-empty="false" v-bind:hide-selected="true"
                                     v-on:input="changeCiclo" v-bind:custom-label='labelCiclo'>
                        </multiselect>

                    </div>
                </div>
            </div>

            <div v-bind:class="classScrollable()" v-show="verInfo==1">
                <table class="table table-striped col-md-12 m-b-lg" v-for="tab in promedios" v-bind:id="tab.id"
                       v-if="verificarCiclo(tab)">
                    <thead>
                        <tr>
                            <td colspan="7" class="td-pad text-center">
                                <div class="header-ciclo block h5 bold bg-primary text-white"
                                     v-text="tab.cicloAcademico.descripcion2"></div>
                            </td>

                        </tr>

                    </thead>
                    <tbody>
                        <tr class="bold h5">
                            <td scope="col" class="col-md-1 text-center">
                                Código
                            </td>
                            <td scope="col" class="col-md-7">
                                Nombre del curso
                            </td>
                            <td scope="col" class="col-md-1 text-center">
                                Créditos
                            </td>
                            <td scope="col" class="col-md-1 text-center">
                                Nota
                            </td>
                            <td scope="col" class="col-md-1 text-center">
                                Estado
                            </td>
                            <td scope="col" class="col-md-1 text-center">
                                Aprobado
                            </td>
                            <td scope="col" class="col-md-1 text-center">
                                Veces
                            </td>
                        </tr>

                        <tr v-for="cur in tab.alumnoCicloCurso" v-if="validarNota(cur,typeSearch)"
                            v-bind:class="styleNota(cur)">

                            <td class="text-center" v-text="cur.curso.codigo"></td>

                            <td class="" v-text="cur.curso.nombre"></td>

                            <td class="text-center" v-text="cur.creditos"></td>

                            <td class="text-center" v-text="cur.nota"></td>

                            <td class="text-center" v-text="cur.estadoEnum.value"></td>

                            <td class="text-center">
                                <i class="fa fa-check-circle text-success fa-lg" v-if="cur.estaAprobado==1"></i>
                                <i class="fa fa-times-circle text-danger fa-lg" v-if="cur.estaAprobado==0"></i>
                            </td>

                            <td class="text-center">
                                <span v-text="cur.vecesCursado"></span> - <span v-text="cur.vecesCursadoRegular"></span>
                            </td>

                        </tr>
                        <tr v-if="verCiclo(tab)">
                            <td colspan="7" style="background-color: #ffffff !important">
                                <div class="col-md-10 col-md-offset-1">

                                    <table class="table table-striped">
                                        <thead>
                                            <tr>
                                                <td scope="col" class="text-center td-pad"
                                                    v-bind:colspan="colspanResumen(tab)+4">
                                                    <div class="header-ciclo block h5 bold bg-light">
                                                        Resumen Académico {{tab.cicloAcademico.descripcion}} &nbsp;-&nbsp;
                                                        {{tab.carrera.nombre}}
                                                    </div>
                                                </td>
                                            </tr>
                                        </thead>

                                        <tbody>
                                            <tr>
                                                <td v-bind:colspan="colspanResumen(tab)+4" class="text-center">
                                                    Situación Académica
                                                    <span class="estado-red">{{tab.situacionFinal.nombre}}</span>
                                                </td>
                                            </tr>

                                            <tr class="">
                                                <td colspan="2" class="wd-33 text-center h5 bold">Semestral</td>
                                                <td colspan="2" class="wd-33 text-center h5 bold">Acumulado</td>
                                                <td v-bind:colspan="colspanResumen(tab)" class="wd-34 text-center h5 bold">
                                                    Mérito Alcanzado 
                                                    <span v-if="tab.nivel">- Nivel {{tab.nivel}}</span>
                                                </td>
                                            </tr>

                                            <tr>
                                                <td class="v-middle">Créditos cursados</td>
                                                <td class="v-middle" v-text="tab.creditosCursadosCiclo"></td>
                                                <td class="v-middle b-l">Créditos cursados</td>
                                                <td class="v-middle" v-text="tab.creditosAcumulados"></td>

                                        <template v-if="isVisibleMerito">
                                            <template v-if="tab.alumno.modalidadEstudio.codigo == 'EPG' ">
                                                <th class="wd-15 v-middle text-center b-l">Especialidad</th>
                                                <th class="v-middle text-center b-r">Nivel</th>
                                            </template>
                                            <template v-else="">
                                                <th class="wd-11 v-middle text-center b-l">Ciclo</th>
                                                <th class="wd-11 v-middle text-center">Facultad</th>
                                                <th class="v-middle text-center b-r">Especial.</th>
                                            </template>
                                        </template>

                                        </tr>

                                        <tr>
                                            <td class="v-middle">Créditos aprobados</td>
                                            <td class="v-middle" v-text="tab.creditosAprobadosCiclo"></td>
                                            <td class="v-middle b-l">Créditos aprobados</td>
                                            <td class="v-middle" v-text="tab.creditosAprobadosAcumulados"></td>

                                        <template v-if="isVisibleMerito">
                                            <template v-if="tab.alumno.modalidadEstudio.codigo == 'EPG' ">
                                                <td class="v-middle text-center b-l" v-if="verCiclo(tab)">
                                                    <span v-if="tab.ordenMeritoCiclo != '' " class="block" v-text="getOrdenMeritoEpg(tab)"></span>
                                                </td>
                                                <td class="v-middle text-center b-r" v-if="verCiclo(tab)">
                                                    <span v-if="verCarrera(tab)">
                                                        <span v-if="tab.ordenMeritoCarrera != '' " class="block" v-text="getOrdenMeritoNivelEpg(tab)"></span>
                                                    </span>
                                                </td>
                                            </template>

                                            <template v-else="">
                                                <td class="align-middle text-center b-l" v-if="verCiclo(tab)">
                                                    <span v-if="tab.ordenMeritoCicloNivel != '' " class="d-block">{{tab.ordenMeritoCicloNivel}} de {{tab.computadosCicloNivel}}</span>
                                                </td>

                                                <td class="align-middle text-center" v-if="verCiclo(tab)">
                                                    <span v-if="tab.ordenMeritoFacultadNivel != '' " class="d-block">{{tab.ordenMeritoFacultadNivel}} de {{tab.computadosFacultadNivel}}</span>
                                                </td>

                                                <td class="align-middle text-center b-r" v-if="verCiclo(tab)">
                                                    <span v-if="verCarrera(tab)">
                                                        <span v-if="tab.ordenMeritoCarreraNivel != '' " class="d-block">{{tab.ordenMeritoCarreraNivel}} de {{tab.computadosCarreraNivel}}</span>
                                                    </span>
                                                </td>
                                            </template>
                                        </template>
                                        </tr>

                                        <tr>
                                            <td class="v-middle">Promedio ponderado</td>
                                            <td class="v-middle" v-text="verNota(tab.promedioCiclo)"></td>
                                            <td class="v-middle b-l">Promedio ponderado</td>
                                            <td class="v-middle" v-text="verNota(tab.promedioAcumulado)"></td>

                                        <template v-if="isVisibleMerito">
                                            <template v-if="tab.alumno.modalidadEstudio.codigo == 'EPG' ">
                                                <td class="v-middle text-center b-l" v-if="verCiclo(tab)">
                                                    <small v-if="tieneMeritoEpg(tab)" class="block" v-text="getMeritoEpg(tab)"></small>
                                                </td>
                                                <td class="v-middle text-center b-r" v-if="verCiclo(tab)">
                                                    <span v-if="verCarrera(tab)">
                                                        <small v-if="tieneMeritoNivelEpg(tab)" class="block" v-text="getMeritoNivelEpg(tab)"></small>
                                                    </span>
                                                </td>
                                            </template>

                                            <template v-else="">
                                                <td class="align-middle text-center b-l" v-if="verCiclo(tab)">
                                                    <p v-if="tieneMeritoNivel('CICLO', tab)" class="d-block" v-text="getMeritoNivel('CICLO', tab)"></p>
                                                </td>

                                                <td class="align-middle text-center" v-if="verCiclo(tab)">
                                                    <p v-if="tieneMeritoNivel('FAC', tab)" class="d-block" v-text="getMeritoNivel('FAC', tab)"></p>
                                                </td>

                                                <td class="align-middle text-center b-r" v-if="verCiclo(tab)">
                                                    <span v-if="verCarrera(tab)">
                                                        <p v-if="tieneMeritoNivel('CARR', tab)" class="d-block" v-text="getMeritoNivel('CARR', tab)"></p>
                                                    </span>
                                                </td>
                                            </template>
                                        </template>
                                        
                                        </tr>

                                        <tr>
                                            <td class="v-middle">Puntaje</td>
                                            <td class="v-middle" v-text="tab.puntajeCiclo"></td>
                                            <td class="v-middle b-l">Puntaje</td>
                                            <td class="v-middle" v-text="tab.puntajeAcumulado"></td>
                                            

                                        <template v-if="isVisibleMerito">
                                            <template v-if="tab.alumno.modalidadEstudio.codigo == 'EPG' ">
                                                <td class="v-middle text-center b-l" v-if="verCiclo(tab)"></td>
                                                <td class="v-middle text-center b-r" v-if="verCiclo(tab)"></td>
                                            </template>

                                            <template v-else="">
                                                <td class="align-middle text-center b-l" v-if="verCiclo(tab)"></td>
                                                <td class="align-middle text-center" v-if="verCiclo(tab)"></td>
                                                <td class="align-middle text-center b-r" v-if="verCiclo(tab)"></td>
                                            </template>
                                        </template>

                                        </tr>

                                        <tr>
                                            <td v-bind:colspan="colspanResumen(tab)+4" class="td-pad col-md-12">
                                                <div class="footer-ciclo block bg-light"></div>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>

                                </div>
                            </td>
                        </tr>


                    </tbody>
                </table>
            </div>

            <div v-show="verInfo==3">
                <table v-if="!general" class="table table-striped col-md-12 ">
                    <thead>
                        <tr class="bold h5">
                            <td scope="col" class="col-md-1 text-center">
                                Código
                            </td>
                            <td scope="col" class="col-md-5">
                                Nombre del curso
                            </td>
                            <td scope="col" class="col-md-1 text-center">
                                Créditos
                            </td>
                            <td scope="col" class="col-md-1 text-center">
                                Nota
                            </td>
                            <td scope="col" class="col-md-1 text-center">
                                Estado
                            </td>
                            <td scope="col" class="col-md-1 text-center">
                                Aprobado
                            </td>
                            <td scope="col" class="col-md-1 text-center">
                                Veces
                            </td>
                            <td scope="col" class="col-md-1 text-center">
                                Ciclo
                            </td>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="cur in cursos" v-if="validarNota(cur,typeSearch)" v-bind:class="styleNota(cur)">


                            <td class="text-center" v-text="cur.curso.codigo"></td>

                            <td class="" v-text="cur.curso.nombre"></td>

                            <td class="text-center" v-text="cur.creditos"></td>

                            <td class="text-center" v-text="cur.nota"></td>

                            <td class="text-center" v-text="cur.estadoEnum.value"></td>

                            <td class="text-center">
                                <i class="fa fa-check-circle text-success fa-lg" v-if="cur.estaAprobado==1"></i>
                                <i class="fa fa-times-circle text-danger fa-lg" v-if="cur.estaAprobado==0"></i>
                            </td>

                            <td class="text-center" v-text="cur.vecesCursado"></td>
                            <td class="text-center" v-text="cur.alumnoCiclo.cicloAcademico.descripcion"></td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <div v-show="verInfo==4">
                <table class="table table-striped col-md-12 m-b-lg">
                    <thead>
                        <tr>
                            <th rowspan="2" class="v-middle text-center">Ciclo</th>
                            <th rowspan="2" class="v-middle text-center">
                                Situación Académica <br/> Nivel
                            </th>

                            <th v-if="isVisibleMerito &amp;&amp; alumno.modalidadEstudio.codigo == 'EPG' " colspan="2"
                                style="width:20%;" class="align-middle text-center b-l">Mérito Alcanzado</th>
                            <th v-if="isVisibleMerito &amp;&amp; alumno.modalidadEstudio.codigo != 'EPG' " colspan="3"
                                style="width:20%;" class="align-middle text-center b-l">Mérito Alcanzado</th>

                            <th colspan="5" class="v-middle text-center">Semestral</th>
                            <th colspan="4" class="v-middle text-center">Acumulado</th>
                        </tr>

                        <tr>
                    <template v-if="isVisibleMerito">
                        <template v-if="alumno.modalidadEstudio.codigo == 'EPG' ">
                            <th style="width:10%;" class="align-middle text-center b-r">Especialidad</th>
                            <th style="width:10%;" class="align-middle text-center b-l">Nivel</th>
                        </template>
                        <template v-else="">
                            <th style="width:10%;" class="align-middle text-center b-l">Ciclo</th>
                            <th style="width:10%;" class="align-middle text-center">Facultad</th>
                            <th style="width:10%;" class="align-middle text-center b-r">Especial.</th>
                        </template>
                    </template>

                    <th class="v-middle text-center">Créd Cur</th>
                    <th class="v-middle text-center">Créd Apr</th>
                    <th class="v-middle text-center">Créd Conv</th>
                    <th class="v-middle text-center">Pje</th>
                    <th class="v-middle text-center">Prom</th>
                    <th class="v-middle text-center">Créd Cur</th>
                    <th class="v-middle text-center">Créd Apr</th>
                    <th class="v-middle text-center">Pje</th>
                    <th class="v-middle text-center">Prom</th>
                    </tr>
                    </thead>
                    <tbody>
                        <tr v-for="tab in promedios" v-bind:class="classCiclo(tab)">
                            <td class="v-middle">
                                <span class="block">{{tab.cicloAcademico.descripcion}}
                                    {{tab.cicloAcademico.tipoEnum.value}}</span>
                                <small class="block">{{tab.estadoEnum.value}}</small>
                            </td>

                            <td class="v-middle" colspan="12" v-if="!verCiclo(tab)"></td>

                            <td class="v-middle" v-if="verCiclo(tab)">
                                <span v-text="tab.situacionFinal.nombre" class="block"></span>
                                <span v-text="'Nivel ' + tab.nivel" class="block"></span>
                            </td>

                    <template v-if="isVisibleMerito">
                        <template v-if="alumno.modalidadEstudio.codigo == 'EPG' ">
                            <td class="v-middle text-center" v-if="verCiclo(tab)">
                                <span v-if="tab.ordenMeritoCiclo != '' " class="block"
                                      v-text="getOrdenMeritoEpg(tab)"></span>
                                <small v-if="tieneMeritoEpg(tab)" class="block" v-text="getMeritoEpg(tab)"></small>
                            </td>
                            <td class="v-middle text-center b-l" v-if="verCiclo(tab)">
                                <span v-if="verCarrera(tab)">
                                    <span v-if="tab.ordenMeritoCarrera != '' " class="block"
                                          v-text="getOrdenMeritoNivelEpg(tab)"></span>
                                    <small v-if="tieneMeritoNivelEpg(tab)" class="block"
                                           v-text="getMeritoNivelEpg(tab)"></small>
                                </span>
                            </td>
                        </template>

                        <template v-else="">
                            <td class="v-middle text-center b-l" v-if="verCiclo(tab)">
                                <span v-if="tab.ordenMeritoCicloNivel != '' "
                                      class="block">{{tab.ordenMeritoCicloNivel}} de
                                    {{tab.computadosCicloNivel}}</span>
                                <p v-if="tieneMeritoNivel('CICLO', tab)" class="block"
                                   v-text="getMeritoNivel('CICLO', tab)"></p>
                            </td>

                            <td class="v-middle text-center" v-if="verCiclo(tab)">
                                <span v-if="tab.ordenMeritoFacultadNivel != '' "
                                      class="block">{{tab.ordenMeritoFacultadNivel}} de
                                    {{tab.computadosFacultadNivel}}</span>
                                <p v-if="tieneMeritoNivel('FAC', tab)" class="d-block"
                                   v-text="getMeritoNivel('FAC', tab)"></p>
                            </td>

                            <td class="v-middle text-center" v-if="verCiclo(tab)">
                                <span v-if="verCarrera(tab)">
                                    <span v-if="tab.ordenMeritoCarreraNivel != '' "
                                          class="block">{{tab.ordenMeritoCarreraNivel}} de
                                        {{tab.computadosCarreraNivel}}</span>
                                    <p v-if="tieneMeritoNivel('CARR', tab)" class="block"
                                       v-text="getMeritoNivel('CARR', tab)"></p>
                                </span>
                            </td>
                        </template>
                    </template>

                    <td class="v-middle text-center b-l" v-if="verCiclo(tab)" v-text="tab.creditosCursadosCiclo">
                    </td>

                    <td class="v-middle text-center" v-if="verCiclo(tab)" v-text="tab.creditosAprobadosCiclo"></td>

                    <td class="v-middle text-center" v-if="verCiclo(tab)" v-text="tab.creditosConvalidados"></td>

                    <td class="v-middle text-center" v-if="verCiclo(tab)" v-text="tab.puntajeCiclo"></td>

                    <td class="v-middle text-center" v-if="verCiclo(tab)" v-bind:class="styleNota(tab)"
                        v-text="verNota(tab.promedioCiclo)"></td>

                    <td class="v-middle text-center b-l" v-if="verCiclo(tab)" v-text="tab.creditosAcumulados"></td>

                    <td class="v-middle text-center" v-if="verCiclo(tab)" v-text="tab.creditosAprobadosAcumulados">
                    </td>

                    <td class="v-middle text-center" v-if="verCiclo(tab)" v-text="tab.puntajeAcumulado"></td>

                    <td class="v-middle text-center" v-if="verCiclo(tab)" v-text="verNota(tab.promedioAcumulado)">
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
            alumno: {},
            showTitle: true,
            showactions: {required: false, default: false}
        },

        components: {
            ModalConfirm, ModalInfo
        },

        data() {
            return {
                cursos: [],
                promedios: [],
                verInfo: 1,
                typeSearch: false,
                typeSearch2: false,
                typeSearch3: false,
                typeSearch4: false,
                cicloSelect: {},
                general: true,
                isVisibleMerito: false,
                puedeCalcular: puedeCalcular,
                idModalConfirm: "id-modal-confirm-histo-alumno"
            };
        },

        mounted() {
            let $vue = this;
            if ($vue.alumno.modalidadEstudio.codigo === "EPG" || $vue.alumno.modalidadEstudio.codigo === "PRE") {
                $vue.isVisibleMerito = true;
            }
        },

        computed: {
            titulo() {
                return 'Historial Académico';
            }
        },

        watch: {
            alumno(newValue) {
                if (this.alumno != null && this.alumno.id != null) {
                    if (this.alumno.id != newValue.id) {
                        this.cargaHistorial();
                    }
                }
            }
        },

        methods: {
            generarReporteHistorial() {
                let $vue = this;

                let allAprobado = false;
                let soloUnCiclo = false;

                if ($vue.typeSearch) {
                    allAprobado = true;
                }
                if ($vue.typeSearch3) {
                    soloUnCiclo = true;
                }

                var tipo = "";
                if ($vue.typeSearch2) { // listado general de cursos
                    tipo = 'LIST';
                } else if ($vue.typeSearch4) {
                    tipo = 'PROM';
                } else {
                    tipo = 'CICLO';
                }

                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/getToken`
                })).then((response) => {
                    var token = response.data.data.token;
                    var url = response.data.data.url;
                    url += `/publico/historialPdf/${this.alumno.id}/${this.cicloSelect.cicloAcademico.id}?`;
                    url += `notas=${allAprobado}&soloUnCiclo=${soloUnCiclo}&tipo=${tipo}&token=${token}`;

                    location.href = url;
                });


            },
            colspanResumen(item) {
                if (item.alumno.modalidadEstudio.codigo === 'EPG') {
                    return 2;
                } else if (item.carrera.codigo === item.carrera.facultad.codigo) {
                    return 2;
                }
                return 3;
            },
            classResumen(item) {
                if (item.carrera.codigo == item.carrera.facultad.codigo) {
                    return "col-md-3";
                }
                return "col-md-2";
            },
            verCarrera(item) {
                if (item.carrera.codigo == item.carrera.facultad.codigo) {
                    return false;
                }
                return true;
            },
            tieneMerito(tipo, item) {
                if (tipo == "CICLO") {
                    if (item.cuadroHonorCiclo == "" && item.quintoSuperiorCiclo == "" && item.tercioSuperiorCiclo == "") {
                        return false;
                    }
                    return true;
                } else if (tipo == "FAC") {
                    if (item.cuadroHonorFacultad == "" && item.quintoSuperiorFacultad == "" && item.tercioSuperiorFacultad == "") {
                        return false;
                    }
                    return true;
                } else if (tipo == "CARR") {
                    if (item.cuadroHonorCarrera == "" && item.quintoSuperiorCarrera == "" && item.tercioSuperiorCarrera == "") {
                        return false;
                    }
                    return true;
                }
                return false;
            },
            tieneMeritoNivel(tipo, item) {
                if (tipo == "CICLO") {
                    if (item.cuadroHonorCicloNivel == "" && item.quintoSuperiorCicloNivel == "" && item.tercioSuperiorCicloNivel == "") {
                        return false;
                    }
                    return true;
                } else if (tipo == "FAC") {
                    if (item.cuadroHonorFacultadNivel == "" && item.quintoSuperiorFacultadNivel == "" && item.tercioSuperiorFacultadNivel == "") {
                        return false;
                    }
                    return true;
                } else if (tipo == "CARR") {
                    if (item.cuadroHonorCarreraNivel == "" && item.quintoSuperiorCarreraNivel == "" && item.tercioSuperiorCarreraNivel == "") {
                        return false;
                    }
                    return true;
                }
                return false;
            },
            getOrdenMerito(tipo, item) {
                let separator = "/";
                if (tipo == "CICLO") {
                    if (item.cuadroHonorCiclo !== "") {
                        return item.cuadroHonorCiclo + separator + item.controlMeritoCiclo.alumnosComputados;
                    } else if (item.quintoSuperiorCiclo !== "") {
                        return item.quintoSuperiorCiclo + separator + item.controlMeritoCiclo.alumnosComputados;
                    } else if (item.tercioSuperiorCiclo !== "") {
                        return item.tercioSuperiorCiclo + separator + item.controlMeritoCiclo.alumnosComputados;
                    }
                } else if (tipo == "FAC") {
                    if (item.cuadroHonorFacultad !== "") {
                        return item.cuadroHonorFacultad + separator + item.controlMeritoFacultad.alumnosComputados;
                    } else if (item.quintoSuperiorFacultad !== "") {
                        return item.quintoSuperiorFacultad + separator + item.controlMeritoFacultad.alumnosComputados;
                    } else if (item.tercioSuperiorFacultad !== "") {
                        return item.tercioSuperiorFacultad + separator + item.controlMeritoFacultad.alumnosComputados;
                    }
                } else if (tipo == "CARR") {
                    if (item.cuadroHonorCarrera !== "") {
                        return item.cuadroHonorCarrera + separator + item.controlMeritoCarrera.alumnosComputados;
                    } else if (item.quintoSuperiorCarrera !== "") {
                        return item.quintoSuperiorCarrera + separator + item.controlMeritoCarrera.alumnosComputados;
                    } else if (item.tercioSuperiorCarrera !== "") {
                        return item.tercioSuperiorCarrera + separator + item.controlMeritoCarrera.alumnosComputados;
                    }
                }
                return "";
            },
            getOrdenMeritoNivel(tipo, item) {
                let $vue = this;
                let separator = "/";
                if (tipo == "CICLO") {
                    if (item.cuadroHonorCicloNivel !== "") {
                        return item.cuadroHonorCiclo + separator + $vue.getComputados(tipo, item);
                    } else if (item.quintoSuperiorCicloNivel !== "") {
                        return item.quintoSuperiorCicloNivel + separator + $vue.getComputados(tipo, item);
                    } else if (item.tercioSuperiorCicloNivel !== "") {
                        return item.tercioSuperiorCicloNivel + separator + $vue.getComputados(tipo, item);
                    }
                } else if (tipo == "FAC") {
                    if (item.cuadroHonorFacultadNivel !== "") {
                        return item.cuadroHonorFacultadNivel + separator + $vue.getComputados(tipo, item);
                    } else if (item.quintoSuperiorFacultadNivel !== "") {
                        return item.quintoSuperiorFacultadNivel + separator + $vue.getComputados(tipo, item);
                    } else if (item.tercioSuperiorFacultadNivel !== "") {
                        return item.tercioSuperiorFacultadNivel + separator + $vue.getComputados(tipo, item);
                    }
                } else if (tipo == "CARR") {
                    if (item.cuadroHonorCarreraNivel !== "") {
                        return item.cuadroHonorCarreraNivel + separator + $vue.getComputados(tipo, item);
                    } else if (item.quintoSuperiorCarreraNivel !== "") {
                        return item.quintoSuperiorCarreraNivel + separator + $vue.getComputados(tipo, item);
                    } else if (item.tercioSuperiorCarreraNivel !== "") {
                        return item.tercioSuperiorCarreraNivel + separator + $vue.getComputados(tipo, item);
                    }
                }
                return "";
            },
            getComputados(tipo, item) {
                if (tipo == "CICLO") {
                    if (item.nivel == 1) {
                        return item.controlMeritoCiclo.computadosNivel1;
                    } else if (item.nivel == 2) {
                        return item.controlMeritoCiclo.computadosNivel2;
                    } else if (item.nivel == 3) {
                        return item.controlMeritoCiclo.computadosNivel3;
                    } else if (item.nivel == 4) {
                        return item.controlMeritoCiclo.computadosNivel4;
                    } else if (item.nivel == 5) {
                        return item.controlMeritoCiclo.computadosNivel5;
                    }
                } else if (tipo == "FAC") {
                    if (item.nivel == 1) {
                        return item.controlMeritoFacultad.computadosNivel1;
                    } else if (item.nivel == 2) {
                        return item.controlMeritoFacultad.computadosNivel2;
                    } else if (item.nivel == 3) {
                        return item.controlMeritoFacultad.computadosNivel3;
                    } else if (item.nivel == 4) {
                        return item.controlMeritoFacultad.computadosNivel4;
                    } else if (item.nivel == 5) {
                        return item.controlMeritoFacultad.computadosNivel5;
                    }
                } else if (tipo == "CARR") {
                    if (item.nivel == 1) {
                        return item.controlMeritoCarrera.computadosNivel1;
                    } else if (item.nivel == 2) {
                        return item.controlMeritoCarrera.computadosNivel2;
                    } else if (item.nivel == 3) {
                        return item.controlMeritoCarrera.computadosNivel3;
                    } else if (item.nivel == 4) {
                        return item.controlMeritoCarrera.computadosNivel4;
                    } else if (item.nivel == 5) {
                        return item.controlMeritoCarrera.computadosNivel5;
                    }
                }
                return "";
            },
            getMerito(tipo, item) {
                if (tipo == "CICLO") {
                    if (item.cuadroHonorCiclo !== "") {
                        return "C.Honor";
                    } else if (item.quintoSuperiorCiclo !== "") {
                        return "5to.Super.";
                    } else if (item.tercioSuperiorCiclo !== "") {
                        return "3cio.Super.";
                    }
                } else if (tipo == "FAC") {
                    if (item.cuadroHonorFacultad !== "") {
                        return "C.Honor";
                    } else if (item.quintoSuperiorFacultad !== "") {
                        return "5to.Super.";
                    } else if (item.tercioSuperiorFacultad !== "") {
                        return "3cio.Super.";
                    }
                } else if (tipo == "CARR") {
                    if (item.cuadroHonorCarrera !== "") {
                        return "C.Honor";
                    } else if (item.quintoSuperiorCarrera !== "") {
                        return "5to.Super.";
                    } else if (item.tercioSuperiorCarrera !== "") {
                        return "3cio.Super.";
                    }
                }
                return "";
            },
            getMeritoNivel(tipo, item) {
                if (tipo == "CICLO") {
                    if (item.cuadroHonorCicloNivel !== "") {
                        return "C.Honor";
                    } else if (item.quintoSuperiorCicloNivel !== "") {
                        return "5to.Super.";
                    } else if (item.tercioSuperiorCicloNivel !== "") {
                        return "3cio.Super.";
                    }
                } else if (tipo == "FAC") {
                    if (item.cuadroHonorFacultadNivel !== "") {
                        return "C.Honor";
                    } else if (item.quintoSuperiorFacultadNivel !== "") {
                        return "5to.Super.";
                    } else if (item.tercioSuperiorFacultadNivel !== "") {
                        return "3cio.Super.";
                    }
                } else if (tipo == "CARR") {
                    if (item.cuadroHonorCarreraNivel !== "") {
                        return "C.Honor";
                    } else if (item.quintoSuperiorCarreraNivel !== "") {
                        return "5to.Super.";
                    } else if (item.tercioSuperiorCarreraNivel !== "") {
                        return "3cio.Super.";
                    }
                }
                return "";
            },
            verCiclo(item) {
                let noVer = {NMAT: "NMAT", RCI: "RCI", ANCI: "ANCI", INH: "INH"};
                let estado = noVer[item.estadoEnum.name];
                if (estado === undefined) {
                    return true;
                }
                return false;
            },
            verNota(notax) {
                return APP.verNota(notax);
            },
            classCiclo(item) {
                if (item.estadoEnum.name == 'NMAT') {
                    return "text-warning";
                } else if (item.cicloAcademico.tipoEnum.name == 'REG') {
                    return "bold";
                } else {
                    return "text-muted";
                }
            },
            changeCiclo(item) {
                let url = location.href + "#" + item.id;
                location.href = "#" + item.id;
            },
            labelCiclo(item, id) {
                if (item.cicloAcademico == undefined) {
                    return "";
                }
                return item.cicloAcademico.descripcion;
            },
            cargaHistorial() {
                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/${this.alumno.id}/historial`
                })).then((response) => {
                    this.promedios = response.data.data.promedios;
                    this.cursos = response.data.data.cursos;
                    this.cicloSelect = {};
                    if (this.promedios.length > 0) {
                        this.cicloSelect = this.promedios[0];
                    }
                });
            },
            classScrollable() {
                let $vue = this;
                if ($vue.typeSearch3) {
                    return "";
                }
                return "pre-scrollable";
            },
            verificarCiclo(item) {
                let $vue = this;
                if ($vue.typeSearch3) {
                    return (item.id == $vue.cicloSelect.id)
                }
                return true;
            },
            changeSearch() {
                let $vue = this;
                $vue.verificarShow();
            },
            changeSearch2() {
                let $vue = this;
                if (!$vue.typeSearch2) {
                    $vue.general = true;
                } else {
                    $vue.general = false;
                    if ($vue.typeSearch3) {
                        $vue.typeSearch3 = false;
                    }
                }

                $vue.verificarShow();
            },
            changeSearch3() {
                let $vue = this;
                $vue.verificarShow();
            },
            changeSearch4() {
                let $vue = this;
                $vue.verificarShow();
            },
            verificarShow() {
                let $vue = this;
                if ($vue.typeSearch4) {
                    $vue.verInfo = 4;
                } else {
                    if (!$vue.typeSearch2 && !$vue.typeSearch3) {
                        $vue.verInfo = 1;
                    }
                    if ($vue.typeSearch2 && !$vue.typeSearch3) {
                        $vue.verInfo = 3;
                    }
                    if (!$vue.typeSearch2 && $vue.typeSearch3) {
                        $vue.verInfo = 1;
                    }
                }
            },
            styleNota(item) {
                if (item.estaAprobado == 1) {
                    return "text-primary";
                } else {
                    return "text-danger";
                }
            },
            validarNota(item, tipo) {
                if (!tipo) {
                    return true;
                } else {
                    return (item.estaAprobado == 1);
                }
            },
            calcularPromedio: function () {
                let vue = this;
                if (vue.alumno.id == null) {
                    return;
                }

                let config = VUE_MODAL.structConfirm({
                    id: this.idModalConfirm,
                    message: "¿Seguro que desea recalcular el promedio?",
                    okbtn: "Si, calcular",
                    okclass: "btn-primary",
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/calcularpromedio`,
                            modal: this.$refs.modalConfirm.getModal(),
                            body: {id: this.alumno.id}
                        })).then(() => {
                            this.cargaHistorial();
                            this.$parent.reloadAlumno();
                        });
                    }
                });

                this.$refs.modalConfirm.open(config);
            },
            getOrdenMeritoEpg(item) {
                let separator = "/";
                if (item.cuadroHonorCarrera !== "") {
                    return item.cuadroHonorCarrera + separator + item.controlMeritoCarrera.alumnosComputados;
                } else if (item.quintoSuperiorCarrera !== "") {
                    return item.quintoSuperiorCarrera + separator + item.controlMeritoCarrera.alumnosComputados;
                } else if (item.tercioSuperiorCarrera !== "") {
                    return item.tercioSuperiorCarrera + separator + item.controlMeritoCarrera.alumnosComputados;
                } else if (item.ordenMeritoCarrera !== "") {
                    return item.ordenMeritoCarrera + separator + item.controlMeritoCarrera.alumnosComputados;
                }
                return "";
            },
            getOrdenMeritoNivelEpg(item) {
                let $vue = this;
                let separator = "/";
                if (item.cuadroHonorCarreraNivel !== "") {
                    return item.cuadroHonorCarreraNivel + separator + $vue.getComputadosEpg(item);
                } else if (item.quintoSuperiorCarreraNivel !== "") {
                    return item.quintoSuperiorCarreraNivel + separator + $vue.getComputadosEpg(item);
                } else if (item.tercioSuperiorCarreraNivel !== "") {
                    return item.tercioSuperiorCarreraNivel + separator + $vue.getComputadosEpg(item);
                } else if (item.ordenMeritoCarreraNivel !== "") {
                    return item.ordenMeritoCarreraNivel + separator + item.computadosCarreraNivel;
                }
                return "";
            },
            getMeritoEpg(item) {
                if (item.cuadroHonorCarrera !== "") {
                    return "C.Honor";
                } else if (item.quintoSuperiorCarrera !== "") {
                    return "5to.Super.";
                } else if (item.tercioSuperiorCarrera !== "") {
                    return "3cio.Super.";
                }
                return "";
            },
            getMeritoNivelEpg(item) {
                if (item.cuadroHonorCarreraNivel !== "") {
                    return "C.Honor";
                } else if (item.quintoSuperiorCarreraNivel !== "") {
                    return "5to.Super.";
                } else if (item.tercioSuperiorCarreraNivel !== "") {
                    return "3cio.Super.";
                }
                return "";
            },
            getComputadosEpg(item) {
                if (item.nivel === 1) {
                    return item.controlMeritoCarrera.computadosNivel1;
                } else if (item.nivel === 2) {
                    return item.controlMeritoCarrera.computadosNivel2;
                } else if (item.nivel === 3) {
                    return item.controlMeritoCarrera.computadosNivel3;
                } else if (item.nivel === 4) {
                    return item.controlMeritoCarrera.computadosNivel4;
                } else if (item.nivel === 5) {
                    return item.controlMeritoCarrera.computadosNivel5;
                }
                return "";
            },
            tieneMeritoNivelEpg(item) {
                if (item.cuadroHonorCarreraNivel == "" && item.quintoSuperiorCarreraNivel == "" && item.tercioSuperiorCarreraNivel == "") {
                    return false;
                }
                return true;
            },
            tieneMeritoEpg(item) {
                if (item.cuadroHonorCarrera == "" && item.quintoSuperiorCarrera == "" && item.tercioSuperiorCarrera == "") {
                    return false;
                }
                return true;
            }
        }
    };
</script>