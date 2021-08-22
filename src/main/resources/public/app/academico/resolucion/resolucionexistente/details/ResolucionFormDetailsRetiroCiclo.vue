<template>
    <div>

        <h4 class="text-primary m-b-lg"> Trámites {{resolucion.tipoResolucion.nombre}}</h4>

        <resolucion-form-filter></resolucion-form-filter>

        <table class="table table-striped">
            <thead>
                <tr>
                    <th class="col-sm-9 text-center" >Persona</th>
                    <th class="col-sm-1 text-center" >Aprobado</th>
                    <th class="col-sm-1 text-center" >Rechazado</th>
                    <th class="col-sm-1 text-center"></th>
                </tr>
            </thead>
            <tbody>

                <tr v-for="(retiroCiclo , index) in resolucion.retiroCiclo" v-if="filtroFacultadSeleccionado(filterFacultad, retiroCiclo)"> 
                    <td class="v-middle text-center">
                        <div class="form-group">
                            <div class="col-md-12">
                                <multiselect v-model="retiroCiclo.alumno" 
                                             v-bind:options='alumnos'
                                             v-on:search-change="searchAlumno"
                                             track-by='id'
                                             v-bind:show-labels="false"
                                             v-bind:allow-empty="false"
                                             deselect-label="No se puede eliminar este valor"
                                             v-bind:internal-search='false'
                                             placeholder=" " 
                                             v-bind:disabled="isEdicion &amp;&amp; retiroCiclo.id != null"
                                             >
                                    <template slot="singleLabel" slot-scope="props">
                                        <span class="">{{props.option.codigo}} - {{ props.option.persona.apellidosNombres }}</span>
                                    </template>
                                    <template slot="option" slot-scope="props">
                                        <div class="option__desc">
                                            <span class="option__title block bold">{{ props.option.codigo }} - {{ props.option.persona.nombreCompleto }} </span>
                                            <span class="option__small">{{ props.option.persona.tipoDocumento.simbolo }} - {{ props.option.persona.numeroDocIdentidad }}</span>
                                            <span class="option__small block bold text-success">{{ props.option.carrera.nombre }} </span>
                                        </div>
                                    </template>
                                </multiselect>
                                <input v-model="retiroCiclo.alumno" required="true" type="text" class="hide"/>
                            </div>
                        </div>
                    </td>
                    <td class="v-middle">
                        <label class="switch">
                            <input type="checkbox" 
                                   v-model="retiroCiclo.seleccionado"
                                   v-on:change="cambioSeleccionado(retiroCiclo)"
                                   v-bind:disabled="isEdicion &amp;&amp; retiroCiclo.id != null"/>
                            <span class="slider round"></span>
                        </label>
                    </td>
                    <td class="v-middle">
                        <label class="switch">
                            <input type="checkbox" 
                                   v-model="retiroCiclo.rechazado"
                                   v-on:change="cambioRechazado(retiroCiclo)"
                                   v-bind:disabled="isEdicion &amp;&amp; retiroCiclo.id != null"/>
                            <span class="slider round"></span>
                        </label>
                    </td>
                    <td class="v-middle">
                        <button type="button"  v-on:click.prevent="del(index)" class="btn btn-danger" v-bind:disabled="isEdicion &amp;&amp; retiroCiclo.id != null">
                            <i class="fa fa-trash-o " aria-hidden="true"></i>
                        </button>
                    </td>
                </tr>


            </tbody>
        </table>

        <button type="button" v-on:click="add" class="btn btn-default pull-right m-t-md">Agregar Alumno</button>


    </div>
</template>

<script>
    const ResolucionFormFilter = httpVueLoader('/app/academico/resolucion/resolucionexistente/ResolucionFormFilter.vue');
    module.exports = {
        mixins: [AppliedFilter, VueLoader],
        components: {
            resolucionFormFilter: ResolucionFormFilter,
        },
        computed: {
            ...Vuex.mapState(["resolucion", "visualizarSoloSeleccionados", "filterFacultad", "isEdicion"])
        },
        data() {
            return {
                alumnos: [],
            };
        },
        mounted: function () {
            let $vue = this;
            if (!$vue.isEdicion) {
                $vue.allRetiroCiclo();
            }
        },
        methods: {
            add() {
                let $vue = this;
                $vue.resolucion.retiroCiclo.push({seleccionado: true});
            },
            del(index) {
                let $vue = this;
                $vue.resolucion.retiroCiclo.splice(index, 1);
            },
            searchAlumno(nombre) {

                let $vue = this;
                if ($vue.resolucion.oficina == null) {
                    notify("Seleccione una oficina.");
                    return;
                }

                AXIOS.get(APP.url("academico/resolucion/existentes/findAlumno"),
                        {params: {nombre: nombre, instanciaOficina: $vue.resolucion.oficina.id}})
                        .then(({data}) => {
                            if (data.success) {
                                $vue.alumnos = data.data;
                        }
                        });

            },
            allRetiroCiclo() {
                let $vue = this;
                $vue.showLoader("Espere un momento por favor");
                AXIOS.get(APP.url("academico/resolucion/existentes/allRetiroCiclo"))
                        .then(({data}) => {
                            $vue.resolucion.retiroCiclo = data.data;
                            $vue.hideLoader();
                        }, () => {
                            notify(Messages.errorComunicacion, "error");
                            $vue.hideLoader();
                        });
            },
            cambioRechazado(readmision) {
                readmision.seleccionado = false;
            },
            cambioSeleccionado(readmision) {
                readmision.rechazado = false;
            }
        }
    };
</script>