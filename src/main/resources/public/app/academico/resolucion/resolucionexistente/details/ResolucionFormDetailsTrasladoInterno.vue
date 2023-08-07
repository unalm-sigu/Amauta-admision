<template>
    <div>

        <h4 class="text-primary m-b-lg"> Trámites {{resolucion.tipoResolucion.nombre}}</h4>

        <table class="table table-striped">
            <thead>
                <tr>
                    <th class="col-sm-10 text-center" >Persona</th>
                    <th class="col-sm-1 text-center" >Estado</th>
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
                                             v-bind:disabled="tramiteTraslado.id != null">
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
                                <!--<multiselect v-model="tramiteTraslado.alumno" 
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
                                </multiselect>-->
                                <input v-model="tramiteTraslado.alumno" required="true" type="text" class="hide"/>
                            </div>
                        </div>
                    </td>

                    <td class="v-middle">
                        <div>
                            <select-state v-bind:disabled="tramiteTraslado.id !=null" v-model='tramiteTraslado.estado'></select-state>
                            <!--<select-state v-bind:disabled="isEdicion &amp;&amp; tramiteTraslado.id !=null" v-model='tramiteTraslado.estado'></select-state>-->
                        </div>
                    </td>

                    <td class="v-middle text-center">
                        <button type="button" v-on:click.prevent="del(index)" class="btn btn-danger" 
                                v-bind:disabled="isEdicion &amp;&amp; tramiteTraslado.id != null">
                            <i class="fa fa-trash-o " aria-hidden="true"></i>
                        </button>
                    </td>

                </tr>

            </tbody>
        </table>

        <!--<button type="button" v-on:click="add" class="btn btn-default pull-right m-t-md">Agregar Alumno</button>-->
        <button type="button" v-if="isEdicion == true" v-on:click="add" class="btn btn-default pull-right m-t-md">Agregar Alumno</button>
        <button type="button" v-if="isAnular == false" v-on:click="add" class="btn btn-default pull-right m-t-md">Agregar Alumno</button>

    </div>
</template>

<script>
    const SelectState = httpVueLoader('/app/academico/resolucion/resolucionexistente/SelectState.vue');

    module.exports = {
        mixins: [VueLoader],
        components: {
            selectState: SelectState,
        },
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
                isEdicion: IS_EDICION,
                isAnular: IS_ANULAR
            };
        },
        mounted: function () {
            let $vue = this;
            /*if (!$vue.isEdicion) {
                $vue.allTraslados();
            }*/
        },
        methods: {
            add() {
                let $vue = this;
                $vue.resolucion.tramiteTraslado.push({seleccionado: true, estado: 'ACEP'});
                $vue.$forceUpdate();
            },
            del(index) {
                let $vue = this;
                if($vue.isAnular) {                
                    bootbox.confirm({
                        message: "<div class='form-group'>" +
                                "<h4 class='text-center bold'>¿Seguro que desea retirar al alumno de esta resolución?</h4><br/>" +
                                "<p class='bold'>Ingrese motivo: </p>" +
                                "<textarea class='form-control' id='motivo' rows='3' maxLength='200' placeholder='Describa un motivo, máximo 200 caracteres'></textarea>" +
                                "</div>",                        
                        buttons: {
                            confirm: {label: 'Sí, anular', className: "btn-danger"},
                            cancel: {label: 'Cancelar', className: "btn-default"}
                        },
                        inputType: 'textarea',
                        callback: function (result) {                            
                            if (result) {
                                if ($("#motivo").val().trim().length < 1) {
                                    return false;
                                }
                                $vue.showLoader("Espere un momento por favor");
                                $vue.errores = [];
                                $vue.resolucion.tramiteTraslado[index].motivoAnulacion = $("#motivo").val();

                                axios_.post(APP.url('academico/resolucion/existentes/anularTramiteTrasladoInterno'), {"alumno": $vue.resolucion.tramiteTraslado[index].alumno, "tramiteTraslado": $vue.resolucion.tramiteTraslado[index], "resolucion": $vue.resolucion})
                                .then(({data}) => {
                                    if (data.success) {
                                        notify(data.message, 'info');
                                        location.href = APP.url('academico/resolucion/existentes/'+ $vue.resolucion.id + "/anularTramite");
                                    } else {
                                        notify(data.message, 'error');
                                    }
                                    $vue.hideLoader();
                                    $vue.$forceUpdate();
                                }, () => $vue.hideLoader());
                            }
                        }
                    });
                } else {
                  $vue.resolucion.tramiteTraslado.splice(index, 1);
                  $vue.$forceUpdate();
                }
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