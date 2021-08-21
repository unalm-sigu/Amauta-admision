<template>
    <div>

        <table class="table table-striped">
            <thead>
                <tr>
                    <th class="col-sm-3 text-center">Persona</th>
                    <th class="col-sm-2 text-center">Tipo Tramite</th>
                    <th class="col-sm-3 text-center">Motivo Rechazo</th>
                    <th class="col-sm-2 text-center">Curso</th>
                    <th class="col-sm-1 text-center">Nota</th>
                    <th class="col-sm-1 text-center"></th>
                </tr>
            </thead>
            <tbody>

                <tr v-for="(cambioNota , index) in resolucion.cambioNota" > 
                    <td class="v-middle text-center">
                        <div class="">
                            <multiselect v-model="cambioNota.alumno" 
                                         v-bind:options='alumnos'
                                         v-on:search-change="searchAlumno"
                                         track-by='id'
                                         v-bind:loading="isLoading"
                                         v-bind:show-labels="false"
                                         v-bind:allow-empty="false"
                                         deselect-label="No se puede eliminar este valor"
                                         v-bind:internal-search='false'
                                         v-on:select="cicloCambioNota($event,resolucion)"
                                         placeholder=" " 
                                         v-bind:disabled="isEdicion &amp;&amp; !cambioNota.id">

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
                            <input v-model="cambioNota.alumno" required="true" type="text" class="hide"/>
                        </div>
                    </td>

                    <td class="v-middle text-center">
                        <span v-if="resolucion.tipoResolucion" class="block text-muted" v-text="resolucion.tipoResolucion.nombre"></span>
                    </td>

                    <td class="v-middle text-left">
                        <input class="form-control" v-model="cambioNota.motivo" required="true" type="text"  v-bind:disabled="isEdicion &amp;&amp; !cambioNota.id"/>
                    </td>

                    <td  class="v-middle text-left">
                        <div class="">
                            <multiselect v-model="cambioNota.curso" 
                                         v-bind:options='cursos'
                                         label='nombre'
                                         track-by='id'
                                         v-bind:show-labels="false"
                                         v-bind:allow-empty="false"
                                         v-bind:internal-search='true'
                                         placeholder=" "                                         
                                         required="true"  
                                         v-bind:disabled="isEdicion &amp;&amp; !cambioNota.id">
                            </multiselect>
                        </div>
                        <input v-model="cambioNota.curso" required="true" type="text" class="hide"/>
                    </td>

                    <td  class="v-middle text-left">                                                
                        <input class="form-control numerico" v-model="cambioNota.nota" required=""  v-bind:disabled="isEdicion &amp;&amp; !cambioNota.id"></input>
                    </td>

                    <td class="v-middle">
                        <button type="button" v-on:click.prevent="del(index)" class="btn btn-danger"  v-bind:disabled="isEdicion &amp;&amp; !cambioNota.id">
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
            ...Vuex.mapState(["resolucion", "isEdicion:"])
        },
        data() {
            return {
                alumnos: [],
                cursos: [],
                isLoading: false
            };
        },
        mounted: function () {
            let $vue = this;
            $vue.loadTramites();
        },
        methods: {
            add() {
                let $vue = this;
                $vue.resolucion.cambioNota.push({seleccionado: false});
            },
            del(index) {
                let $vue = this;
                $vue.resolucion.cambioNota.splice(index, 1);
            },
            searchAlumno(nombre) {

                let $vue = this;
                $vue.isLoading = true

                if ($vue.resolucion.oficina == null) {
                    notify("Seleccione una oficina.");
                    return;
                }

                if ($vue.resolucion.cicloAplica == null) {
                    notify("Seleccione un ciclo.");
                    return;
                }

                if (nombre) {

                    AXIOS.get(APP.url("academico/resolucion/existentes/findAlumno"),
                            {params: {nombre: nombre, instanciaOficina: $vue.resolucion.oficina.id}})
                            .then(({data}) => {
                                if (data.success) {
                                    $vue.alumnos = data.data;
                                }
                                $vue.isLoading = false;
                            }, error => {
                                $vue.isLoading = false;
                            });

                }
            },
            cicloCambioNota(alumno, resolucion) {
                let $vue = this;
                AXIOS.get(APP.url("academico/tramitecondicional/allCursosAlumnoByName"),
                        {params: {idAlumno: alumno.id, idCiclo: resolucion.cicloAplica.id}})
                        .then(({data}) => {
                            if (data.success) {
                                $vue.cursos = data.data;
                            }
                        });
            },
        }
    };
</script>