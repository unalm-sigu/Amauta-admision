<template>
    <div>

        <h4 class="text-primary m-b-lg"> Trámites {{resolucion.tipoResolucion.nombre}}</h4>

        <table class="table table-striped">

            <thead>
                <tr>
                    <th class="col-sm-9 text-center">Persona</th>
                    <th class="col-sm-2 text-center" >
                        <span v-if="validColumCreditos(resolucion)">Créditos</span>
                    </th>
                    <th class="col-sm-1 text-center"></th>
                </tr>
            </thead>

            <tbody>

                <tr v-for="(practicas , index) in resolucion.tramitePracticasPreProfesionales">
                    <td class="v-middle text-center">
                        <div class="form-group">
                            <div class="col-md-12">
                                <multiselect v-model="practicas.alumno" 
                                             v-bind:options='alumnos'
                                             v-on:search-change="searchAlumno"
                                             track-by='id'
                                             v-bind:show-labels="false"
                                             v-bind:allow-empty="false"
                                             deselect-label="No se puede eliminar este valor"
                                             v-bind:internal-search='false'
                                             placeholder=" " 
                                             v-bind:disabled="isEdicion &amp;&amp; practicas.id != null">
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
                                <input v-model="practicas.alumno" required="true" type="text" class="hide"/>
                            </div>
                        </div>
                    </td>

                    <td class="v-middle text-center" >
                        <input v-if="validColumCreditos(resolucion)" class="form-control" required="true" v-model="practicas.creditos"  v-bind:disabled="isEdicion &amp;&amp; practicas.id != null"/>
                    </td>

                    <td class="v-middle">
                        <button type="button"  v-on:click.prevent="del(index)" class="btn btn-danger"  v-bind:disabled="isEdicion &amp;&amp; practicas.id != null">
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
    module.exports = {
        mixins: [VueLoader],
        computed: {
            ...Vuex.mapState(["resolucion", "isEdicion"])
        },
        data() {
            return {
                alumnos: [],
            };
        },
        mounted: function () {
            let $vue = this;
            if (!$vue.isEdicion) {
                $vue.allPracticas();
            }
        },
        methods: {
            add() {
                let $vue = this;
                $vue.resolucion.tramitePracticasPreProfesionales.push({seleccionado: true});
            },
            del(index) {
                let $vue = this;
                $vue.resolucion.tramitePracticasPreProfesionales.splice(index, 1);
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
                        }});
            },
            allPracticas() {
                let $vue = this;
                $vue.showLoader("Espere un momento por favor");
                axios_.get(APP.url("academico/resolucion/existentes/allPracticas"))
                        .then(({data}) => {
                            $vue.resolucion.tramitePracticasPreProfesionales = data;
                            $vue.hideLoader();
                        }, () => {
                            $vue.hideLoader();
                        });
            },
            validColumCreditos(item) {
                if (item.oficina != null && item.oficina.codigo == "F040") {
                    return true;
                }
                return false;
            }
        }
    };
</script>