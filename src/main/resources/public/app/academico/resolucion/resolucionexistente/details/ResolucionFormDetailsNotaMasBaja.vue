<template>
    <div>

        <h4 class="text-primary m-b-lg"> Trámites {{resolucion.tipoResolucion.nombre}}</h4>

        <table class="table table-striped">
            <thead>
                <tr>
                    <th class="col-sm-8 text-center" >Persona</th>
                    <th class="col-sm-3 text-center" >Curso</th>
                    <th class="col-sm-1 text-center"></th>
                </tr>
            </thead>
            <tbody>

                <tr v-for="(tramiteNotabaja , index) in resolucion.cambioNotaMasBajas">
                    <td class="v-middle">
                        <div class="form-group">
                            <div class="col-md-12">
                                <multiselect v-model="tramiteNotabaja.alumno" 
                                             v-bind:options='alumnos'
                                             v-on:search-change="searchAlumno"
                                             track-by='id'
                                             v-bind:show-labels="false"
                                             v-bind:allow-empty="false"
                                             deselect-label="No se puede eliminar este valor"
                                             v-bind:internal-search='false'
                                             v-on:select="allAlumnoCiclo($event,tramiteNotabaja)"
                                             placeholder=" " >

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
                                <input v-model="tramiteNotabaja.alumno" required="true" type="text" class="hide"/>
                            </div>
                        </div>
                    </td>
                    <td class="v-middle">
                        <div class="form-group">
                            <div class="col-md-12">
                                <multiselect v-model="tramiteNotabaja.alumnoCicloCursoBean" 
                                             v-bind:options='tramiteNotabaja.alumnoCicloCursoBeans'                                                                  
                                             track-by='key'
                                             v-bind:show-labels="false"
                                             v-bind:allow-empty="false"
                                             deselect-label="No se puede eliminar este valor"
                                             v-bind:internal-search='true'
                                             placeholder=" " >
                                    <template slot="option" slot-scope="props">
                                        <div class="option__desc">
                                            <span class="option__title block bold">{{ props.option.cicloAcademico.descripcion }} -{{ props.option.curso.nombre }} - ( {{ props.option.nota }} ) </span>
                                        </div>
                                    </template>
                                    <template slot="singleLabel" slot-scope="{ option }"><strong>{{ option.cicloAcademico.descripcion }} - {{ option.curso.nombre }} - ( {{ option.nota }} ) </strong></template>

                                </multiselect>
                                <input v-model="tramiteNotabaja.alumnoCicloCursoBean" required="true" type="text" class="hide"/>
                            </div>
                        </div>
                    </td>
                    <td class="v-middle text-center">
                        <button type="button"  v-on:click.prevent="del(index)" class="btn btn-danger" v-bind:disabled="isEdicion &amp;&amp; tramiteTraslado.id != null">
                            <i class="fa fa-trash-o " aria-hidden="true"></i>
                        </button>
                    </td>
                </tr>


            </tbody>
        </table>

        <button type="button" v-on:click.prevent="add" class="btn btn-default pull-right m-t-md">Agregar Alumno</button>


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
                alumnoCicloCursoBeans: [],
                isEdicion: IS_EDICION,
            };
        },
        mounted: function () {
            let $vue = this;
        },
        methods: {
            add() {
                let $vue = this;
                $vue.resolucion.cambioNotaMasBajas.push({seleccionado: true, alumnoCicloCursoBeans: []});
                $vue.$forceUpdate();
            },
            del(index) {
                let $vue = this;
                $vue.resolucion.cambioNotaMasBajas.splice(index, 1);
                $vue.$forceUpdate();
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
            allAlumnoCiclo(item, tramiteNotabaja) {
                let $vue = this;
                AXIOS.get(APP.url("academico/resolucion/existentes/allCiclosRepetido/" + item.id))
                        .then(({data}) => {
                            if (data.success) {
                                tramiteNotabaja.alumnoCicloCursoBeans = data.data;
                        }
                        });
            },
        }
    };
</script>