<template>
    <div>

        <h4 class="text-primary m-b-lg"> Trámites {{resolucion.tipoResolucion.nombre}}</h4>

        <resolucion-form-filter></resolucion-form-filter>

        <table class="table table-striped">
            <thead>
                <tr>
                    <th class="col-sm-10 text-center">Persona</th>
                    <th class="col-sm-1">Seleccionado</th>
                    <th class="col-sm-1 text-center"></th>
                </tr>
            </thead>
            <tbody>
                <tr v-if="filtroFacultadSeleccionado(filterFacultad,bachiller)" v-for="(bachiller , index) in resolucion.tramiteBachiller" >
                    <td class="v-middle text-center">
                        <div class="form-group">
                            <multiselect v-model="bachiller.alumno" 
                                         v-bind:options='alumnos'
                                         v-on:search-change="searchAlumno"
                                         track-by='id'
                                         v-bind:show-labels="false"
                                         v-bind:allow-empty="false"
                                         deselect-label="No se puede eliminar este valor"
                                         v-bind:internal-search='false'
                                         placeholder=" " 
                                         v-bind:disabled="isEdicion &amp;&amp; bachiller.id">

                                <template slot="singleLabel" slot-scope="props">
                                    <span class="">{{props.option.codigo}} - {{ props.option.persona.apellidosNombres }}</span>
                                </template>

                                <template slot="option" slot-scope="props">
                                    <div class="option__desc">
                                        <span class="option__title block bold">{{ props.option.codigo }} - {{ props.option.persona.apellidosNombres }} </span>
                                        <span class="option__small">{{ props.option.persona.tipoDocumento.simbolo }} - {{ props.option.persona.numeroDocIdentidad }}</span>
                                        <span class="option__small block bold text-success">{{ props.option.carrera.nombre }} </span>
                                    </div>
                                </template>
                            </multiselect>
                            <input v-model="bachiller.alumno" required="true" type="text" class="hide"/>
                        </div>
                    </td>
                    <td class="v-middle">
                        <label class="switch">
                            <input type="checkbox" 
                                   v-model="bachiller.seleccionado"
                                   checked="1"
                                   v-bind:disabled="isEdicion &amp;&amp; bachiller.id !=null"/>
                            <span class="slider round"></span>
                        </label>
                    </td>
                    <td class="v-middle text-center">
                        <button type="button" v-on:click.prevent="del(index)" class="btn btn-danger"  v-bind:disabled="isEdicion &amp;&amp; bachiller.id">
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
                $vue.allBachillers();
            }

        },
        methods: {
            add() {
                let $vue = this;
                $vue.resolucion.tramiteBachiller.push({seleccionado: true});
            },
            del(index) {
                let $vue = this;
                $vue.resolucion.tramiteBachiller.splice(index, 1);
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
            allBachillers() {
                let $vue = this;
                $vue.showLoader("Espere un momento por favor");
                AXIOS.get(APP.url("academico/resolucion/existentes/allBachiller"))
                        .then(({data}) => {
                            $vue.resolucion.tramiteBachiller = data.data;
                            $vue.hideLoader();
                        }, () => {
                            notify(Messages.errorComunicacion, "error");
                            $vue.hideLoader();
                        });

            }
        }
    };
</script>