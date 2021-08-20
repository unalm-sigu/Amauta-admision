<template>
    <div>

        <table class="table table-striped">
            <thead>
                <tr>
                    <th class=" text-center">Persona</th>
                    <th class=" text-center" >Tipo Tramite</th>
                    <th >Aprobado</th>
                    <th >Rechazado</th>
                    <th class="col-sm-1 text-center"></th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(bachiller , index) in resolucion.tramiteBachiller" >
                    <td class="v-middle text-center">
                        <div class="form-group">
                            <div class="col-md-12">
                                <multiselect v-model="bachiller.alumno" 
                                             v-bind:options='alumnos'
                                             v-on:search-change="loadAlumno"
                                             v-bind:custom-label='customLabel'
                                             track-by='id'
                                             v-bind:show-labels="false"
                                             v-bind:allow-empty="false"
                                             deselect-label="No se puede eliminar este valor"
                                             v-bind:internal-search='false'
                                             placeholder=" " 
                                             v-bind:disabled="isEdicion &amp;&amp; bachiller.id != null">
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
                        </div>
                    </td>
                    <td class="v-middle text-center">
                        <span v-if="resolucion.tipoResolucion" class="block text-muted" v-text="resolucion.tipoResolucion.nombre"></span>
                    </td>
                    <td>
                        <label class="switch">
                            <input type="checkbox" 
                                   v-model="bachiller.seleccionado"
                                   checked="1"
                                   v-bind:disabled="isEdicion &amp;&amp; bachiller.id != null"/>
                            <span class="slider round"></span>
                        </label>
                    </td>
                    <td>
                        <label class="switch">
                            <input type="checkbox" 
                                   v-model="bachiller.seleccionado"
                                   checked="1"
                                   v-bind:disabled="isEdicion &amp;&amp; bachiller.id != null"/>
                            <span class="slider round"></span>
                        </label>
                    </td>
                    <td>
                        <button v-on:click="del(index)" class="btn btn-danger"  v-bind:disabled="isEdicion &amp;&amp; bachiller.id != null">
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
        data() {
            return {
                resolucion: {tramiteBachiller: []},
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
                $vue.resolucion.tramiteBachiller.push({seleccionado: true});
            },
            del(index) {
                let $vue = this;
                $vue.resolucion.tramiteBachiller.splice(index, 1);
            },
        }
    };
</script>