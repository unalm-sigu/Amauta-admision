<template>
    <div>

        <h4 class="text-primary m-b-lg"> Trámites {{resolucion.tipoResolucion.nombre}}</h4>

        <table class="table table-striped">
            <thead>
                <tr>
                    <th class="col-sm-4 text-center" >Persona</th>
                    <th class="col-sm-3 text-center" >Docente</th>
                    <th class="col-sm-2 text-center" >Motivo Rechazo</th>
                    <th class="col-sm-1 text-center" >Aprobado</th>
                    <th class="col-sm-1 text-center" >Rechazado</th>
                    <th class="col-sm-1 text-center"></th>
                </tr>
            </thead>
            <tbody>

                <tr v-for="(cursoDirigido , index) in resolucion.cursoDirigido"> 
                    <td class="v-middle">
                        <div class="form-group">
                            <div class="col-md-12">
                                <multiselect v-model="cursoDirigido.alumno" 
                                             v-bind:options='alumnos'
                                             v-on:search-change="searchAlumno"
                                             track-by='id'
                                             v-bind:show-labels="false"
                                             v-bind:allow-empty="false"
                                             deselect-label="No se puede eliminar este valor"
                                             v-bind:internal-search='false'
                                             placeholder="Ingresa un caracter como mínimo" 
                                             v-bind:disabled="isEdicion &amp;&amp; cursoDirigido.id != null"
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
                                <input v-model="cursoDirigido.alumno" required="true" type="text" class="hide"/>
                            </div>
                        </div>
                    </td>
                    <td class="v-middle">
                        <div class="col-md-12" v-if="cursoDirigido.isSeleccionado">
                            <div class="form-group">
                                <multiselect
                                    v-model="cursoDirigido.docenteAsignado"
                                    v-bind:options="docentes"
                                    v-on:search-change="findDocente"
                                    placeholder="Seleccione un docente"
                                    v-bind:show-labels="false"
                                    v-bind:allow-empty="false"
                                    deselect-label="No se puede eliminar este valor"
                                    track-by="id"
                                    required="true"
                                    v-bind:internal-search="false"
                                    v-bind:hide-selected="true"
                                    v-bind:showNoOptions="true"
                                    v-bind:disabled="isEdicion &amp;&amp; cursoDirigido.id != null" >
                                    <template slot="singleLabel" slot-scope="props">
                                        <span class=""> {{ props.option.persona.apellidosNombres }}</span>
                                    </template>
                                    <template slot="option" slot-scope="props">
                                        <div class="option__desc">
                                          <span class="option__title block bold"> {{ props.option.persona.nombreCompleto }}</span>
                                        </div>
                                    </template>
                                </multiselect>
                                <input v-model="cursoDirigido.docenteAsignado" required="true" type="text" class="hide"/>
                            </div>
                        </div>
                    </td>
                    <td class="v-middle">
                        <input class="form-control" v-if="cursoDirigido.rechazado" v-model="cursoDirigido.motivoRechazo" required="true" type="text"  v-bind:disabled="isEdicion &amp;&amp; cursoDirigido.id != null"/>
                    </td>
                    <td class="v-middle">
                        <label class="switch">
                            <input type="checkbox" 
                                   v-model="cursoDirigido.isSeleccionado"
                                   v-on:change="cambioSeleccionado(cursoDirigido)"
                                   v-bind:disabled="isEdicion &amp;&amp; cursoDirigido.id != null"/>
                            <span class="slider round"></span>
                        </label>
                    </td>
                    <td class="v-middle">
                        <label class="switch">
                            <input type="checkbox" 
                                   v-model="cursoDirigido.rechazado"
                                   v-on:change="cambioRechazado(cursoDirigido)"
                                   v-bind:disabled="isEdicion &amp;&amp; cursoDirigido.id != null"/>
                            <span class="slider round"></span>
                        </label>
                    </td>
                    <td class="v-middle">
                        <button type="button" v-on:click.prevent="del(index)" class="btn btn-danger"  v-bind:disabled="isEdicion &amp;&amp; cursoDirigido.id != null">
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
        props: {
            resolucion: {type: Object, default: {}},
        },
        model: {
            prop: 'resolucion',
            event: 'change'
        },
        data() {
            return {
                alumnos: [],
                docentes: [],
                isEdicion: IS_EDICION,
            };
        },
        mounted: function () {
            let $vue = this;
        },
        methods: {
            add() {
                let $vue = this;
                $vue.resolucion.cursoDirigido.push({seleccionado: false, rechazado: false});
                $vue.$forceUpdate();
            },
            del(index) {
                let $vue = this;
                $vue.resolucion.cursoDirigido.splice(index, 1);
                $vue.$forceUpdate();
            },
            searchAlumno(nombre) {
                let $vue = this;
                if (!$vue.resolucion.oficina) {
                    notify("Seleccione una oficina.");
                    return;
                }
                AXIOS.get(APP.url("academico/resolucion/existentes/findAlumno"),
                        {params: {nombre: nombre, instanciaOficina: $vue.resolucion.oficina.id}})
                        .then(({data}) => {
                            if (data.success) {
                                $vue.alumnos = data.data;
                                
                        }
                        }, error => {
                        });
            },
            findDocente(nombre) {
                let $vue = this;
                axios_.get(APP.url("academico/tramiteacademico/findDocente"),
                        {params: {nombre: nombre}})
                        .then(({data}) => {
                            $vue.docentes = data;
                        }, () => {
                        });
            },
            /*cambioRechazado(cursoDirigido) {
                cursoDirigido.isSeleccionado = true;
            },
            cambioSeleccionado(cursoDirigido) {
                cursoDirigido.rechazado = false;
            }*/
            cambioRechazado(cursoDirigido) {
                cursoDirigido.rechazado != cursoDirigido.rechazado;
                if(cursoDirigido.isSeleccionado){
                    cursoDirigido.seleccionado = false;
                    cursoDirigido.isSeleccionado = false
                }
            },
            cambioSeleccionado(cursoDirigido) {
                cursoDirigido.motivoRechazo = "";
                cursoDirigido.seleccionado = cursoDirigido.isSeleccionado;
                if(cursoDirigido.rechazado){
                    cursoDirigido.rechazado = false;
                }
            }
        }
    };
</script>