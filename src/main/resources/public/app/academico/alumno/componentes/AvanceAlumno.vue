<template>
    <div>

        <section class="panel-body">
            <div class="m-b-sm" >        
                <h3 class="m-t-n" style="display: inline-block" v-text="titulo"></h3>
                <div class="pull-right m-t-n">
                </div>
            </div>
            <form class="form-horizontal m-b-lg" >
                <div class="form-group">
                    <label class="control-label col-sm-2" for="email">Plan Curricular: </label>
                    <div class="col-sm-2">
                        <select id='planes' class="form-control" v-model="planTemp.id">
                            <option v-for="plan in planes" 
                                    v-bind:value="plan.id" 
                                    v-text="plan.cicloInicioVigencia.descripcion"></option>
                        </select>
                    </div>
                    <div class="col-sm-2" v-if="puedeCalcular">
                        <a v-on:click="generarAvance" class="btn btn-primary btn-sm"> Actualizar </a> 
                    </div>

                </div>
            </form>
            <div class="tabbable-line m-b-md" v-if="cantidadCursos>0">
                <div class="tabbable-line" id="subBar">
                    <ul class="nav nav-tabs">
                        <li class="">
                            <a style="text-transform: none;"> Ciclos </a>
                        </li>
                        <li v-for="(ciclocurricula,index) in ciclosCurricula" 
                            v-bind:class="active(index)" class="ver-tab-ciclo-cur-obl" >
                            <a class="pointer" style="text-transform: none;"
                               v-text="ciclocurricula.numeroRoman"  
                               v-on:click="cicloSelecc(ciclocurricula.numero)" >
                                <small v-text="ciclocurricula.cantidad" class="text-primary"></small>
                            </a>  
                        </li>
                        <li v-if="cantidadCursos>0" v-bind:class="active(200)" class="ver-tab-ciclo-cur-obl">
                            <a style="text-transform: none;" class="pointer"
                               v-on:click="verResumen()"> Resumen </a>
                        </li>
                    </ul>
                </div>
            </div>
            <div v-if="showCursos">
                <table class="table table-striped" v-if="cantidadCursos>0">
                    <thead class="panel panel-heading">
                        <tr>
                            <th class="col-md-1 v-middle text-center">Tipo</th>
                            <th class="col-md-1 v-middle"></th>
                            <th class="col-md-2 v-middle">Curso</th>
                            <th class="col-md-1 v-middle">Créditos Requisito</th>
                            <th class="col-md-1 v-middle">Pre requisitos</th>
                            <th class="col-md-1 v-middle text-center">Veces Cursado</th>
                            <th class="col-md-1 v-middle text-center">Créditos</th>
                            <th class="col-md-1 v-middle text-center">Nota</th>
                            <th class="col-md-1 v-middle text-center">Ciclo de aprobación</th>            
                            <th class="col-md-1 v-middle text-center">Estado</th>
                        </tr>
                    </thead>
                    <tbody>           
                        <tr v-for="item in cursosCurricula"
                            v-if="item.numeroCiclo == showCiclo  &amp;&amp; item.estadoRegistro != 'INA' &amp;&amp; validar(item)">
                            <td  class="v-middle text-center"> 
                                <span class="block" v-text="item.tipoCursoCurricula.nombre"></span>
                            </td>
                            <td  class="v-middle text-center"> 
                                <span class="block" v-text="item.curso.codigo"></span>
                                <small class="block bold text-primary" v-text="item.curso.codigoAnterior1"> </small>
                            </td>
                            <td  class="v-middle text-left" > 
                                <span v-text="item.curso.nombre" v-bind:class="styleEquivalente(item.estado)" v-on:click="modalEqui(item)"></span>
                            </td>
                            <td  class="v-middle text-left" > 
                                <span v-if="item.cursoCurricula.creditosRequisito != null" class="estado-gray" v-text="item.cursoCurricula.creditosRequisito"></span>
                            </td>
                            <td  class="v-middle text-center" > 
                                <span v-if="item.cantRequisitos != null" v-bind:class="styleCountRequisitos(item.cantRequisitos) " v-text="item.cantRequisitos" v-on:click="modalPreRequisitos(item)"></span>
                            </td>
                            <td  class="v-middle text-center" > 
                                <span v-if="item.vecesCursado != '0' " v-text="item.vecesCursado" ></span>
                            </td>
                            <td  class="v-middle text-center"> 
                                <span class="estado-gray" v-text="item.creditos"></span>
                            </td>
                            <td class="v-middle text-center">
                                <span v-bind:class="styleNotaCurri(item.nota)" v-text="item.nota"></span>
                            </td>
                            <td class="v-middle text-center">
                                <span v-text="item.cicloAprobado.descripcion"></span>
                            </td>
                            <td class="v-middle text-center">
                                <span v-if="item.estadoMatriculaEnum != null &amp;&amp; (item.estado == 'SIM' || item.estado == 'HAB')" v-bind:class="styleEstadoCurr(item.estadoMatriculaEnum.name)" v-text="item.estadoMatriculaEnum.value"></span>
                                <span v-else="" v-bind:class="styleEstadoCurr(item.estadoEnum.name)" v-text="item.estadoEnum.value"></span>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <div v-if="!showCursos">
                <div v-if="resumenPlan.length > 0  " class="col-lg-8">
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th class="v-middle text-center col-md-4">Tipo de Cursos</th>
                                <th class="v-middle text-center col-md-2">Créditos plan</th>
                                <th class="v-middle text-center col-md-2">Máximo Créditos</th>
                                <th class="v-middle text-center col-md-2">Mínimo Créditos</th>
                                <th class="v-middle text-center col-md-2">Créditos Aprobados</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="res in resumenPlan">
                                <td v-text="res.tipoCursoCurricula.nombre"></td>
                                <td v-if="verCreditos(res)" class="text-center v-middle" 
                                    v-bind:rowspan="getRowspan(res)"
                                    v-bind:class="getClassELC(res)">
                                    <span v-if="res.tipoCursoCurricula.codigo != 'EEP' " v-text="res.creditos"></span>
                                </td>
                                <td class="text-center" v-text="getCreditosMax(res)"></td>
                                <td class="text-center" >
                                    <span v-if="verCreditosMin(res)" 
                                          v-text="res.minimoCreditos"></span>
                                </td>
                                <td class="text-center">
                                    <span v-if="tipoConRango(res)">{{creditosAlumno(res)}} de {{res.creditos}}</span>
                                    <span v-else="">{{creditosAlumno(res)}}</span>
                                </td>
                            </tr>
                            <tr class="bold">
                                <td class="text-right">Total</td>
                                <td class="text-center" v-text="totalPlan('CREDITOS')"></td>
                                <td class="text-center"></td>
                                <td class="text-center"></td>
                                <td class="text-center">
                                    {{totalAlumno('CREDITOS')}} de {{totalPlan('CREDITOS')}}
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
            showTitle: true,
            showActions: true,
            alumno: {}
        },

        components: {
            ModalConfirm, ModalInfo
        },

        data() {
            return {
                puedeCalcular : JSON.parse(puedeCalcularJSON),
                planes: [],
                ident: true,
                ciclosCurricula: [],
                cursosCurricula: [],
                resumenAlumno: [],
                resumenPlan: [],
                cantidadCursos: 0,
                showCursos: true,
                showCiclo: 1,
                planTemp: {id: 0}
            };
        },

        beforeMount() {
            this.loadPlanes();
        },

        mounted() {
            this.planTemp.id = this.alumno.planCurricular.id;
        },

        computed: {
            titulo() {
                if (this.alumno.carrera)
                    return 'Avance Curricular en ' + this.alumno.carrera.nombre;
                else
                    return 'wwww';
            }
        },

        methods: {
            validar(curso) {
                if (curso.tipoCursoCurricula.codigo == 'ELE' && curso.estado == 'HAB') {
                    return false;
                }
                return true;
            },
            totalAlumno(tipo) {
                let $vue = this;
                let cred = 0;
                let cur = 0;

                for (var i = 0; i < $vue.resumenAlumno.length; i++) {
                    let res = $vue.resumenAlumno[i];
                    if (res.tipoCursoCurricula.codigo == 'EEP') {
                        continue;
                    }
                    cred += res.creditos;
                    cur += res.cursos;
                }
                return (tipo == 'CREDITOS') ? cred : cur;
            },
            totalPlan(tipo) {
                let $vue = this;
                let cred = 0;
                let cur = 0;

                for (var i = 0; i < $vue.resumenPlan.length; i++) {
                    let res = $vue.resumenPlan[i];
                    if ("/ELE/CULT/PROD/TECIND/EEP/".indexOf(res.tipoCursoCurricula.codigo) > 0) {
                        continue;
                    }
                    cred += res.creditos;
                    cur += res.cursos;
                }
                return (tipo == 'CREDITOS') ? cred : cur;
            },
            creditosAlumno(item) {
                let $vue = this;
                for (var i = 0; i < $vue.resumenAlumno.length; i++) {
                    let res = $vue.resumenAlumno[i];
                    if (res.tipoCursoCurricula.codigo == item.tipoCursoCurricula.codigo) {
                        return res.creditos;
                    }
                }
                return 0;

            },
            cursosAlumnos(item) {
                let $vue = this;
                for (var i = 0; i < $vue.resumenAlumno.length; i++) {
                    let res = $vue.resumenAlumno[i];
                    if (res.tipoCursoCurricula.codigo == item.tipoCursoCurricula.codigo) {
                        return res.cursos;
                    }
                }
                return 0;
            },
            tipoConRango(item) {
                return "/OBL/GEN/DEP/".indexOf(item.tipoCursoCurricula.codigo) > 0;
            },
            verCreditos(item) {
                return "/ELE/CULT/PROD/TECIND/".indexOf(item.tipoCursoCurricula.codigo) < 0;
            },
            verCreditosMin(item) {
                return "/ELC/ELE/CULT/PROD/TECIND/EEP/".indexOf(item.tipoCursoCurricula.codigo) > 0;
            },
            getCreditosMax(item) {
                let $vue = this;
                if ("/ELC/ELE/CULT/PROD/TECIND/EEP/".indexOf(item.tipoCursoCurricula.codigo) < 0) {
                    return "";
                }
                if ("/ELE/CULT/PROD/TECIND/EEP/".indexOf(item.tipoCursoCurricula.codigo) > 0) {
                    return item.creditos;
                }
                let credEspeciales = 0;
                for (var i = 0; i < $vue.resumenPlan.length; i++) {
                    if ("/CULT/PROD/TECIND/".indexOf($vue.resumenPlan[i].tipoCursoCurricula.codigo) > 0) {
                        credEspeciales += $vue.resumenPlan[i].creditos;
                    }
                }
                return item.creditos - credEspeciales;
            },
            getRowspan(item) {
                let $vue = this;
                if ("ELC" !== item.tipoCursoCurricula.codigo) {
                    return 1;
                }
                let rows = 0;
                for (var i = 0; i < $vue.resumenPlan.length; i++) {
                    if ("/ELC/ELE/CULT/PROD/TECIND/".indexOf($vue.resumenPlan[i].tipoCursoCurricula.codigo) > 0) {
                        rows++;
                    }
                }
                return rows;
            },
            getClassELC(item) {
                if ("ELC" !== item.tipoCursoCurricula.codigo) {
                    return "";
                }
                return "b-r b-l";
            },
            verResumen() {
                let $vue = this;
                $vue.showCiclo = 201;
                $vue.showCursos = false;
            },
            active(index) {
                let $vue = this;
                let tabSize = 0;
                if ($vue.cursosCurricula[0].numeroCiclo == 0) {
                    tabSize = $vue.showCiclo;
                } else {
                    tabSize = $vue.showCiclo - 1;
                }

                if (index === tabSize) {
                    return "active";
                }
            },
            styleNotaCurri(nota) {
                if (nota === "") {

                } else {
                    return "estado-blue";
                }
            },
            styleEstadoCurr(nombre) {
                if (nombre === 'APR' || nombre === 'EQUIV' || nombre === 'CONV') {
                    return "text-success";
                } else if (nombre === 'SIM') {
                    return "text-warning";
                } else if (nombre === 'NREQ') {
                    return "text-secondary";
                } else if (nombre === 'HAB') {
                    return "text-primary";
                } else if (nombre === 'MAT') {
                    return "text-primary bold";
                } else if (nombre === 'PEND') {
                    return "text-warning ";
                }
            },
            styleCountRequisitos(prerequisitos) {

                if (prerequisitos == 0) {
                    return "estado-red";
                } else {
                    return "estado-blue";
                }
            },
            cargaAvance() {
                let $vue = this;

                $.ajax({
                    method: 'GET',
                    url: APP.url('academico/alumno/' + $vue.alumno.id + '/avance'),
                    contentType: "application/json",
                    success: function (response) {
                        if (response.success) {
                            $vue.cursosCurricula = response.data.cursos;
                            if ($vue.cursosCurricula[0].numeroCiclo == 0) {
                                $vue.showCiclo = 0;
                            }
                            $vue.ciclosCurricula = response.data.ciclos;
                            $vue.resumenAlumno = response.data.resumenAlumno;
                            $vue.resumenPlan = response.data.resumenPlan;
                            $vue.cantidadCursos = $vue.cursosCurricula.length;
                        } else {
                            notify(response.message, 'error');
                        }
                    }
                });
            },
            generarAvance() {
                let $vue = this;

                MODAL.showWait("Espere un momento por favor");
                $.ajax({
                    method: 'GET',
                    url: APP.url('academico/alumno/' + $vue.alumno.id + '/' + $vue.planTemp.id + '/cambiarplan'),
                    contentType: "application/json",
                    success: function (response) {
                        if (response.success) {
                            $vue.$parent.reloadPlanAlumno();
                            $vue.cargaAvance();
                            notify(response.message, 'info');
                        } else {
                            notify(response.message, 'error');
                        }
                        MODAL.hideWait();
                    },
                    error: function () {
                        notify(Messages.errorComunicacion, "error");
                    }
                });
            },
            cicloSelecc: function (cicloSelecc) {
                let $vue = this;
                $vue.showCiclo = cicloSelecc;
                $vue.showCursos = true;
            },
            loadPlanes() {
                let $vue = this;
                $.ajax({
                    method: 'POST',
                    async: true,
                    url: APP.url('academico/alumno/' + $vue.alumno.id + '/planes'),
                    contentType: "application/json",
                    success: function (response) {
                        if (response.success) {
                            $vue.planes = response.data;
                            $vue.planTemp.id = $vue.alumno.planCurricular.id;
                        } else {
                            notify("No se pudo cargar la lista de planes curriculares disponibles para el alumno", "error");
                        }
                    },
                    error() {
                        notify("No se pudo cargar la lista de planes curriculares disponibles para el alumno", "error");
                    }
                });
            },
            modalPreRequisitos(cursoCurricula) {
                let $vue = this;
                console.log(cursoCurricula.prerrequisitos);
                $vue.$root.modalPreRequisitos(cursoCurricula);
            },
            styleEquivalente(estado) {
                if (estado === 'EQUIV') {
                    return "text-primary pointer";
                }
            },
            modalEqui(item) {

                if (item.estado === 'EQUIV') {
                    $.ajax({
                        method: 'POST',
                        async: true,
                        url: APP.url('academico/alumno/dataEquivalente'),
                        contentType: "application/json",
                        data: JSON.stringify(item),
                        success: function (response) {
                            if (response.success) {
                                var table = "<table class='table table-striped'> \n\
                                            <thead class='panel panel-heading'> \n\
                                            <tr> <th class='col-md-1 v-middle text-center'>CÃ³digo</th> \n\
                                            <th class='col-md-6 v-middle'>Curso</th> \n\
                                            <th class='col-md-2 v-middle'>Nota</th> \n\
                                            <th class='col-md-3 v-middle'>Ciclo Apr.</th>  \n\
                                            </tr></thead>  \n\
                                            <tbody> value </tbody>\n\
                                          </table>";
                                var msg = "";
                                for (var i = 0; i < response.data.length; i++) {
                                    msg = msg + "<tr><td>" + response.data[i].curso.codigo + "</td><td>" + response.data[i].curso.nombre + "</td><td>" + response.data[i].nota + "</td><td>" + response.data[i].alumnoCiclo.cicloAcademico.descripcion + "</td></tr>"
                                }
                                table = table.replace("value", msg);
                                bootbox.alert({
                                    title: "Equivalencia con ...",
                                    message: table,
                                    size: 'large'
                                });
                            }
                        }
                    });

                }
            }
        }
    };
</script>