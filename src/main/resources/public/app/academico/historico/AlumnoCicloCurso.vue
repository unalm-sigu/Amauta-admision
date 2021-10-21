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
                    <tr v-for="( itemAlumnoCicloCurso, jindex ) in alumnoCicloCurso">
                        <td class="v-middle text-center" >
                            <div>
                                <multiselect  
                                    v-model="itemAlumnoCicloCurso.curso" 
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

                                <input type="text" class="hide" required="true"  v-model="itemAlumnoCicloCurso.curso"  />

                            </div>
                        </td>
                        <td class="v-middle text-center">
                            <input class="form-control numerico" required="true"  v-model="itemAlumnoCicloCurso.creditos" v-bind:disabled="itemAlumnoCicloCurso.isEstadoNotaModificada"/>
                            <span v-else="" v-text="itemAlumnoCicloCurso.creditos"></span>
                        </td>
                        <td class="v-middle text-center">
                            <input class="form-control numerico" required="true"   v-model="itemAlumnoCicloCurso.nota" v-bind:disabled="itemAlumnoCicloCurso.isEstadoNotaModificada"/>
                            <span v-else="" v-text="itemAlumnoCicloCurso.nota"></span>
                        </td>
                        <td class="v-middle text-center"  >
                            <span v-if='itemAlumnoCicloCurso.estadoEnum' v-text="itemAlumnoCicloCurso.estadoEnum.value"></span>
                        </td>
                        <td class="v-middle text-center">
                            <i class="fa fa-check-circle text-success fa-lg" v-if="itemAlumnoCicloCurso.estaAprobado==1"></i>
                            <i class="fa fa-times-circle text-danger fa-lg" v-if="itemAlumnoCicloCurso.estaAprobado==0"></i>
                        </td>
                        <td class="v-middle text-center" >{{itemAlumnoCicloCurso.vecesCursado}}</td>
                        <td  class="v-middle text-center" >
                            <span v-if="itemAlumnoCicloCurso.estaActivo"> Sí</span>
                            <span v-if="!itemAlumnoCicloCurso.estaActivo"> No</span>
                        </td>
                        <td class="v-middle">
                            <a href="#" >
                                <i class="fa fa-trash-o text-danger fa-2x"
                                   v-on:click.prevent="removerAlumnoCicloCurso( jindex, itemAlumnoCicloCurso )"></i>
                            </a>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="8" class="text-center">
                            <a class="btn btn-default"  v-on:click.prevent="addCurso()"  href="#">Nuevo Curso</a>
                            <a class="btn btn-primary"  v-on:click.prevent="saveAlumnoCicloCurso(alumnoCiclo)"  href="#">Grabar {{alumnoCiclo.cicloAcademico.descripcion}}</a>
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
        mixins: [VueLoader],
        props: {
            alumnoCiclo: {type: Object, default: {}},
            index: {type: Number, default: 0},
        },
        data() {
            return {
                cursos: [],
                alumnoCicloCurso: [],
            };
        },
        computed: {
            ...Vuex.mapState(["alumno"])
        },
        mounted: function () {
            let $vue = this;
            $vue.allAlumnoCicloCurso();
        },
        updated: function () {
            $(".numerico").numeric({negative: false});
        },
        methods: {
            removerAlumnoCicloCurso(jindex, itemAlumnoCicloCurso) {
                let $vue = this;
                if (itemAlumnoCicloCurso.id) {

                    swal('¿Seguro que desea eliminar el curso?', {
                        icon: "warning",
                        closeOnClickOutside: false,
                        closeOnEsc: false,
                        dangerMode: true,
                        buttons: {
                            cancel: {text: "Cancelar", closeModal: true, visible: true},
                            confirm: {text: "Aceptar", closeModal: false}
                        }}).then((value) => {

                        if (value != true) {
                            return;
                        }

                        axios_.get(APP.url('academico/historico/alumno/' + itemAlumnoCicloCurso.id + '/deleteAlumnoCicloCurso'))
                                .then(({data}) => {
                                    notify(data, 'info');
                                    $vue.allAlumnoCicloCurso();
                                    swal.stopLoading();
                                    swal.close();
                                }, () => {
                                    swal.stopLoading();
                                    swal.close();
                                });

                    });

                } else {
                    $vue.alumnoCicloCurso.splice(jindex, 1);
                }
            },
            removeAlumnoCiclo() {
                let $vue = this;
                swal('¿Seguro que desea eliminar el ciclo académico?', {
                    icon: "warning",
                    closeOnClickOutside: false,
                    closeOnEsc: false,
                    dangerMode: true,
                    buttons: {
                        cancel: {text: "Cancelar", closeModal: true, visible: true},
                        confirm: {text: "Aceptar", closeModal: false}
                    }}).then((value) => {

                    if (value != true) {
                        return;
                    }
                    axios_.get(APP.url('academico/historico/alumno/' + $vue.alumnoCiclo.id + '/deleteAlumnoCiclo'))
                            .then(({data}) => {
                                notify(data, 'info');
                        
                                $vue.$parent.cargaHistorial();
                                
                                swal.stopLoading();
                                swal.close();
                            }, () => {
                                swal.stopLoading();
                                swal.close();
                            });
                });
            },
            saveAlumnoCicloCurso() {
                let $vue = this;
                if ($($vue.$refs.formCurso).parsley().validate() != true) {
                    return;
                }
                $vue.showLoader();
                axios_.post(APP.url('academico/historico/alumno/saveCicloAlumnoCurso'), {...$vue.alumnoCiclo, alumnoCicloCurso: $vue.alumnoCicloCurso})
                        .then(({data}) => {
                            notify(data, 'info');
                            $vue.allAlumnoCicloCurso();
                            $vue.hideLoader();
                        }, () => {
                            $vue.hideLoader();
                        });
            },
            addCurso() {
                let $vue = this;
                $vue.alumnoCicloCurso.push({});
            },
            searchCurso(nombre) {
                let $vue = this;
                axios_.get(APP.url("academico/tramiteacademico/asyncFindCursos"), {params: {nombreCurso: nombre}})
                        .then(({data}) => {
                            $vue.cursos = data.data;
                        }, () => {
                        });
            },
            allAlumnoCicloCurso() {
                let $vue = this;
                axios_.get(APP.url('academico/historico/alumno/' + $vue.alumnoCiclo.id + '/alumnoCicloCurso'))
                        .then(({data}) => {
                            $vue.alumnoCicloCurso = data;
                            $vue.$forceUpdate();
                        }, () => {
                        });
            }
        }
    };
</script>