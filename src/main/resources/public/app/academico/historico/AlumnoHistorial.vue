<template>
    <div>

        <section class="panel-body m-t-md">
            <div class="row m-t-md m-b-sm">

                <div class="col-sm-4 pull-right" >

                    <div class="col-sm-6">

                        <multiselect 
                            v-model="ciclo" 
                            v-bind:options='ciclos'
                            v-on:search-change="searchCiclo"
                            label='descripcion'
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
                                    {{props.option.descripcion}}
                                </span>
                            </template>

                            <template slot="option" slot-scope="props">
                                <span class="option_title">
                                    <span class="block text-primary m-t-xs m-b-xs">{{props.option.descripcion2}}</span>
                                    <span v-if='props.option.modalidadEstudio' class="block">{{props.option.modalidadEstudio.nombre}}</span>
                                </span> 
                            </template>

                            <template slot="noOptions">&nbsp</template>
                            <template slot="noResult">&nbsp</template>

                        </multiselect>

                    </div>

                    <div class="col-sm-2">
                        <a class="btn btn-default" @click="addAlumnoCiclo()"   href="#">Agregar</a>
                    </div>

                </div>

            </div>


            <div class="row m-t-md m-b-sm">

                <ul class="nav nav-pills">
                    <li v-bind:class="{'active':active==index}" v-for="( alumnoCiclo, index ) in alumnoCiclos"><a @click='active=index' href="#">{{alumnoCiclo.cicloAcademico.descripcion}}</a></li>
                </ul>

            </div>

            <div v-if="active==index" v-for="( alumnoCiclo, index ) in alumnoCiclos"> 

                <alumno-ciclo-curso v-bind:alumno-ciclo='alumnoCiclo' v-bind:index='index'></alumno-ciclo-curso>

            </div>

        </section>

    </div>
</template>

<script>
    const AlumnoCicloCurso = httpVueLoader('/app/academico/historico/AlumnoCicloCurso.vue');
    module.exports = {
        components: {
            alumnoCicloCurso: AlumnoCicloCurso,
        },
        data() {
            return {
                alumnoCiclos: [],
                ciclos: [],
                ciclo: {},
                active: 0
            };
        },
        computed: {
            ...Vuex.mapState(["alumno"])
        },
        mounted: function () {
            let $vue = this;
            if ($vue.alumno.id) {
                $vue.cargaHistorial();
            }
        },
        methods: {
            removeAlumnoCiclo(index) {
                let $vue = this;
                $vue.alumnoCiclos.splice(index, 1);
            },
            addAlumnoCiclo() {
                let $vue = this;
                if (!$vue.ciclo) {
                    return;
                }
                if (!$vue.ciclo.id) {
                    return;
                }
                let existe = $vue.alumnoCiclos.find(x => x.cicloAcademico.id == $vue.ciclo.id);
                if (existe) {
                    notify("El ciclo ya se encuentra agregado", "error");
                    return;
                }
                $vue.alumnoCiclos.push({cicloAcademico: {...$vue.ciclo}, alumno: {...$vue.alumno}, alumnoCicloCursos: []});
            },
            cargaHistorial() {
                let $vue = this;
                axios.get(APP.url('academico/alumno/' + $vue.alumno.id + '/historial'))
                        .then(({data}) => {
                            $vue.promedios = data.data.promedios;
                            $vue.aluCicCursos = data.data.cursos;
                            $vue.cicloSelect = {};
                        }, () => {
                        });
            },
            searchCiclo(nombre) {
                let $vue = this;
                axios.get(APP.url("academico/tramiteacademico/asyncFindCiclosAcad"), {params: {nombreCiclo: nombre,
                        alumno: $vue.alumno.id}}).then(({data}) => {
                    $vue.ciclos = data.data;
                }, () => {
                });
            }
        }
    };
</script>