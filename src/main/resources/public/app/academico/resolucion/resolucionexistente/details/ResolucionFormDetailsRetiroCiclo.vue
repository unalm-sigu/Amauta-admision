<template>
    <div>

        <table class="table table-striped">
            <thead>
                <tr>
                    <th class=" text-center">Persona</th>
                    <th class=" text-center" >Tipo Tramite</th>
                    <th class=" text-center" v-if="isCambioNota||isCursoDirigido">Motivo Rechazo</th>
                    <th class="col-md-2 text-center" v-if="isTraslado">Ciclo  </th>
                    <th class=" text-center" v-if="isCambioNota || isNotaBaja">Curso</th>
                    <th class="col-sm-1 text-center" v-if="isCambioNota">Nota</th>
                    <th class=" text-center" v-if="isCursoDirigido">Docente</th>
                    <th v-if=" !isCambioNota &amp;&amp; !isNotaBaja  &amp;&amp; !isPracticas">Aprobado</th>
                    <th v-if=" isPracticas &amp;&amp; validColumCreditos(resolucion)">Créditos</th>
                    <th class="col-sm-1 text-center"></th>
                </tr>
            </thead>
            <tbody>

                <tr v-for="(retiroCiclo , index) in resolucion.retiroCiclo" v-if="isRetiroCiclo &amp;&amp; validFilter(filterFacultad, retiroCiclo)"> 
                    <td class="v-middle text-center">
                        <div class="form-group">
                            <div class="col-md-12">
                                <multiselect v-model="retiroCiclo.alumno" 
                                             v-bind:options='alumnos'
                                             v-on:search-change="loadAlumno"
                                             v-bind:custom-label='customLabel'
                                             track-by='id'
                                             v-bind:show-labels="false"
                                             v-bind:allow-empty="false"
                                             deselect-label="No se puede eliminar este valor"
                                             v-bind:internal-search='false'
                                             placeholder=" " 
                                             v-bind:disabled="isEdicion &amp;&amp; retiroCiclo.id != null"
                                             >
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
                    <td class="v-middle text-center">
                        <span v-if="resolucion.tipoResolucion != null" class="block text-muted" v-text="resolucion.tipoResolucion.nombre"></span>
                    </td>

                    <td>
                        <label data-theme="none" class="switch">
                            <input data-theme="none" type="checkbox" 
                                   v-model="retiroCiclo.seleccionado"
                                   checked="1"
                                   v-bind:disabled="isEdicion &amp;&amp; retiroCiclo.id != null"/>
                            <span data-theme="none" class="slider round"></span>
                        </label>
                    </td>
                    <td>
                        <button v-on:click="del(index)" class="btn btn-danger" v-bind:disabled="isEdicion &amp;&amp; retiroCiclo.id != null">
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
        computed: {
            ...Vuex.mapState(["resolucion"])
        },
        data() {
            return {
                alumnos: [],
                isEdicion: false
            };
        },
        mounted: function () {
            let $vue = this;
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
        }
    };
</script>