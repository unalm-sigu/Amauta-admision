<template>
    <div>

        <h4 class="text-primary m-b-lg"> Trámites {{resolucion.tipoResolucion.nombre}}</h4>

        <table class="table table-striped">
            <thead>
                <tr>
                    <th class="col-sm-6 text-center" >Persona</th>
                    <th class="col-sm-4 text-center" >Ciclo</th>
                    <th class="col-sm-1 text-center" >Seleccionado</th>
                    <th class="col-sm-1 text-center"></th>
                </tr>
            </thead>
            <tbody>

                <tr v-for="(tramiteTraslado , index) in resolucion.tramiteTraslado"> 
                    <td class="v-middle text-center">
                        <div class="form-group">
                            <div class="col-md-12">
                                <multiselect v-model="tramiteTraslado.alumno" 
                                             v-bind:options='alumnos'
                                             v-on:search-change="searchAlumno"
                                             track-by='id'
                                             v-bind:show-labels="false"
                                             v-bind:allow-empty="false"
                                             deselect-label="No se puede eliminar este valor"
                                             v-bind:internal-search='false'
                                             placeholder=" " 
                                             v-bind:disabled="isEdicion &amp;&amp; tramiteTraslado.id != null">
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
                                <input v-model="tramiteTraslado.alumno" required="true" type="text" class="hide"/>
                            </div>
                        </div>
                    </td>
                    <td class="v-middle text-left" >
                        <div class="form-group">
                            <multiselect 
                                v-model="tramiteTraslado.cicloAcademico" 
                                v-bind:options="ciclos"
                                placeholder=" "
                                label="descripcion"
                                track-by="id" 
                                required="true"
                                v-bind:allow-empty="true"
                                >
                            </multiselect>
                            <input v-model="tramiteTraslado.cicloAcademico" required="true" type="text" class="hide"/>
                        </div>
                    </td>
                    <td class="v-middle">
                        <label class="switch">
                            <input type="checkbox" 
                                   v-model="tramiteTraslado.seleccionado" />
                            <span class="slider round"></span>
                        </label>
                    </td>
                    <td class="v-middle text-center">
                        <button type="button"  v-on:click.prevent="del(index)" class="btn btn-danger" v-bind:disabled="isEdicion &amp;&amp; tramiteTraslado.id != null">
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
                carreras: JSON.parse(carrerasJson),
                ciclos: JSON.parse(ciclosJson),
                isEdicion: IS_EDICION,
            };
        },
        mounted: function () {
            let $vue = this;
            if (!$vue.isEdicion) {
                $vue.allTraslados();
            }
        },
        methods: {
            add() {
                let $vue = this;
                $vue.resolucion.tramiteTraslado.push({seleccionado: false});
                $vue.$forceUpdate();
            },
            del(index) {
                let $vue = this;
                $vue.resolucion.tramiteTraslado.splice(index, 1);
                $vue.$forceUpdate();
            },
            searchAlumno(nombre) {

                let $vue = this;
                if ($vue.resolucion.oficina == null) {
                    notify("Seleccione una oficina.");
                    return;
                }

                AXIOS.get(APP.url("academico/resolucion/existentes/findAlumno"),
                        {params: {nombre: nombre}})
                        .then(({data}) => {
                            if (data.success) {
                                $vue.alumnos = data.data;
                        }
                        });
            },
            allTraslados() {
                let $vue = this;
                $vue.resolucion.tramiteTraslado = [];
            }
        }
    };
</script>