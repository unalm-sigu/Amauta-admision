<template>
    <div>
        <form ref="formCurso" data-parsley-validate="true">
            <table class="table table-striped col-md-12 m-b-lg" >
                <thead>
                    <tr>
                        <td colspan="8" class="text-center">
                            <h4><span class="block label bg-light">{{alumnoCiclo.cicloAcademico.descripcion2}}</span></h4>
                        </td>
                    </tr>
                </thead>
                <tbody>
                    <tr class="bold h5">
                        <td scope="col" class="col-md-5 text-center">
                            Curso
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
                            Activo
                        </td>
                        <td scope="col" class="col-md-1 text-center">

                        </td>
                    </tr>
                    <tr v-for="( alumnoCicloCurso, jindex ) in alumnoCiclo.alumnoCicloCursos">
                        <td class="v-middle text-center" >
                            <div>
                                <multiselect  
                                    v-model="alumnoCicloCurso.curso" 
                                    v-bind:options='cursos'
                                    v-on:search-change="searchCurso"
                                    label='nombre'
                                    track-by='id'
                                    deselect-label=" "
                                    select-label=" "
                                    placeholder=" "
                                    v-bind:allow-empty="false"
                                    v-bind:hide-selected="true"
                                    v-bind:showNoOptions="true"
                                    v-bind:show-labels="false"
                                    >

                                    <template slot="singleLabel" slot-scope="props">
                                        <span class="option__title">
                                            {{props.option.nombre}}
                                        </span>
                                    </template>

                                    <template slot="option" slot-scope="props">
                                        <span class="option_title">
                                            <span class="block text-primary h4 m-t-xs m-b-xs">{{props.option.nombre}}</span>
                                            <span class="block"><b>{{props.option.codigo}}</b> &nbsp;&nbsp; <i class="fa fa-bookmark-o"></i> {{props.option.tpc}}</span>
                                            <span class="block">{{props.option.tipoCursoEnum.value}}</span>
                                            <small v-if="props.option.codigoAnterior1 != '' " class="block bold">{{props.option.codigoAnterior1}}</small>
                                        </span> 
                                    </template>

                                    <template slot="noOptions">&nbsp</template>
                                    <template slot="noResult">&nbsp</template>
                                </multiselect>

                                <input type="text" class="hide" required="true"  v-model="alumnoCicloCurso.curso"  />

                            </div>
                        </td>
                        <td class="v-middle text-center">
                            <input class="form-control numerico" required="true"  v-model="alumnoCicloCurso.creditos" v-bind:disabled="alumnoCicloCurso.isEstadoNotaModificada"/>
                            <span v-else="" v-text="alumnoCicloCurso.creditos"></span>
                        </td>
                        <td class="v-middle text-center">
                            <input class="form-control numerico" required="true"   v-model="alumnoCicloCurso.nota" v-bind:disabled="alumnoCicloCurso.isEstadoNotaModificada"/>
                            <span v-else="" v-text="alumnoCicloCurso.nota"></span>
                        </td>
                        <td class="v-middle text-center"  >
                            <span v-if='alumnoCicloCurso.estadoEnum' v-text="alumnoCicloCurso.estadoEnum.value"></span>
                        </td>
                        <td class="v-middle text-center">
                            <i class="fa fa-check-circle text-success fa-lg" v-if="alumnoCicloCurso.estaAprobado==1"></i>
                            <i class="fa fa-times-circle text-danger fa-lg" v-if="alumnoCicloCurso.estaAprobado==0"></i>
                        </td>
                        <td class="v-middle text-center" >{{alumnoCicloCurso.vecesCursado}}</td>
                        <td  class="v-middle text-center" >
                            <span v-if="alumnoCicloCurso.estaActivo"> Sí</span>
                            <span v-if="!alumnoCicloCurso.estaActivo"> No</span>
                        </td>
                        <td class="v-middle">
                            <a href="#" >
                                <i class="fa fa-trash-o text-danger fa-2x"
                                   v-on:click.prevent="removerAlumnoCicloCurso( jindex, alumnoCiclo.alumnoCicloCursos )"></i>
                            </a>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="8" class="text-center">
                            <a class="btn btn-default"  v-on:click.prevent="addCurso(alumnoCiclo.alumnoCicloCursos)"  href="#">Nuevo Curso</a>
                            <a class="btn btn-primary"  v-on:click.prevent="saveAlumnoCiclo(alumnoCiclo)"  href="#">Grabar {{alumnoCiclo.cicloAcademico.descripcion}}</a>
                            <a class="btn btn-danger"  v-on:click.prevent="removeAlumnoCiclo()"  href="#">Eliminar ciclo {{alumnoCiclo.cicloAcademico.descripcion}}</a>
                        </td>
                    </tr>
                </tbody>
            </table>
        </form>
    </div>
</template>

<script>
    module.exports = {
        props: {
            alumnoCiclo: {type: Object, default: {}},
            index: {type: Number, default: 0},
        },
        data() {
            return {
                cursos: [],
            };
        },
        computed: {
            ...Vuex.mapState(["alumno"])
        },
        mounted: function () {
            $(".numerico").numeric({negative: false});
        },
        updated: function () {
            $(".numerico").numeric({negative: false});
        },
        methods: {
            removerAlumnoCicloCurso(jindex, alumnoCicloCursos) {
                alumnoCicloCursos.splice(jindex, 1);
            },
            removeAlumnoCiclo() {
                let $vue = this;
                $vue.$parent.removeAlumnoCiclo($vue.index);
            },
            saveAlumnoCiclo() {
                let $vue = this;
                if ($($vue.$refs.formCurso).parsley().validate() != true) {
                    return;
                }
                $vue.alumnoCiclo.alumnoCicloCurso=$vue.alumnoCiclo.alumnoCicloCursos
                axios_.post(APP.url('academico/historico/alumno/saveCicloAlumno'), $vue.alumnoCiclo)
                        .then(({data}) => {
                           notify(data,'info');
                        }, () => {
                        });
            },
            addCurso(alumnoCicloCursos) {
                alumnoCicloCursos.push({});
            },
            searchCurso(nombre) {
                let $vue = this;
                axios_.get(APP.url("academico/tramiteacademico/asyncFindCursos"), {params: {nombreCurso: nombre}})
                        .then(({data}) => {
                            $vue.cursos = data.data;
                        }, () => {
                        });
            },
        }
    };
</script>