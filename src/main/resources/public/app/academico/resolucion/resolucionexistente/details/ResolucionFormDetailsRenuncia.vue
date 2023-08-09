<template>
    <div>

        <h4 class="text-primary m-b-lg"> Trámites {{resolucion.tipoResolucion.nombre}}</h4>

        <resolucion-form-filter v-bind:callfilter="applyFilter"  v-if="!isEdicion"></resolucion-form-filter>

        <table class="table table-striped">
            <thead>
                <tr>
                    <th class="col-sm-10 text-center">Persona</th>
                    <th class="col-sm-1">Seleccionadox</th>
                    <th class="col-sm-1 text-center"></th>
                </tr>
            </thead>
            <tbody>
                <tr v-if="filtroFacultadSeleccionado(filterFacultad, renuncia)" v-for="(renuncia , index) in resolucion.tramiteRenunciaAlumno">
                    <td class="v-middle text-center">
                        <div class="form-group">
                            <div class="col-md-12">
                                <multiselect v-model="renuncia.alumno" 
                                             v-bind:options='alumnos'
                                             v-on:search-change="searchAlumno"
                                             track-by='id'
                                             v-bind:show-labels="false"
                                             v-bind:allow-empty="false"
                                             deselect-label="No se puede eliminar este valor"
                                             v-bind:internal-search='false'
                                             placeholder=" " 
                                             v-bind:disabled="isEdicion &amp;&amp; renuncia.id != null">
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
                                <input v-model="renuncia.alumno" required="true" type="text" class="hide"/>
                            </div>
                        </div>
                    </td>
                    <td class="v-middle">
                        <label class="switch">
                            <input type="checkbox" 
                                   v-model="renuncia.seleccionado"
                                   checked="1"
                                   v-bind:disabled="isEdicion &amp;&amp; renuncia.id != null"/>
                            <span class="slider round"></span>
                        </label>
                    </td>
                    <td class="v-middle">
                        <button type="button"  v-on:click.prevent="del(index)" class="btn btn-danger"  v-bind:disabled="isEdicion &amp;&amp; renuncia.id != null">
                            <i class="fa fa-trash-o " aria-hidden="true"></i>
                        </button>
                    </td>
                </tr>


            </tbody>
        </table>

        <button type="button" v-if="isEdicion == true" v-on:click="add" class="btn btn-default pull-right m-t-md">Agregar Alumno</button>
        <button type="button" v-if="isAnular == false" v-on:click="add" class="btn btn-default pull-right m-t-md">Agregar Alumno</button>
        <!--<button type="button" v-on:click="add" class="btn btn-default pull-right m-t-md">Agregar Alumno</button>-->

    </div>
</template>

<script>
    const ResolucionFormFilter = httpVueLoader('/app/academico/resolucion/resolucionexistente/ResolucionFormFilter.vue');
    module.exports = {
        mixins: [AppliedFilter, VueLoader],
        components: {
            resolucionFormFilter: ResolucionFormFilter,
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
                isEdicion: IS_EDICION,
                isAnular: IS_ANULAR
            };
        },
        mounted: function () {
            let $vue = this;
            console.log($vue.resolucion.tipoResolucion.id)

            if ($vue.isEdicion === false || $vue.isAnular === false) {

                     $vue.allRenunciaAlumno();
            }
        },
        methods: {
            add() {
                let $vue = this;
                $vue.resolucion.tramiteRenunciaAlumno.push({seleccionado: true});
                $vue.$forceUpdate();
            },
//            del(index) {
//                let $vue = this;
//                if ($vue.isAnular) {
//                    bootbox.confirm({
//                        message: "<h4 class='text-center bold'>¿Seguro que desea retirar al alumno de esta resolución?</h4><br/>" +
//                                "<p>Ingrese motivo: </p>" +
//                                "<textarea id='motivo' cols='75' rows='3'></textarea>",
//                        //message: '¿Seguro que desea retirar al alumno de esta resolución? ',
//                        buttons: {
//                            confirm: {label: 'Sí, aceptar', className: "btn-warning"},
//                            cancel: {label: 'Cancelar', className: "btn-link"}
//                        },
//                        inputType: 'textarea',
//                        callback: function (result) {
//                            if (result) {
//                                $vue.showLoader("Espere un momento por favor");
//                                $vue.errores = [];
//                                $vue.resolucion.tramiteRenunciaAlumno[index].motivo = $("#motivo").val();
//                                axios_.post(APP.url('academico/resolucion/existentes/anularTramiteTitulo'), {"tramiteTitulo": $vue.resolucion.tramiteRenunciaAlumno[index], "resolucion": $vue.resolucion})
//                                        .then(({data}) => {
//                                            if (data.success) {
//                                                notify(data.message, 'info');
//                                                location.href = APP.url('academico/resolucion/existentes/' + $vue.resolucion.id + "/anularTramite");
//                                            } else {
//                                                notify("Se produjo un error al anular el Trámite Titulo de la Resolución", 'error');
//                                            }
//                                            $vue.hideLoader();
//                                            $vue.$forceUpdate();
//                                        }, () => $vue.hideLoader());
//                            }
//                        }
//                    });
//                } else {
//                    $vue.resolucion.tramiteRenunciaAlumno.splice(index, 1);
//                    $vue.$forceUpdate();
//                }
//            },
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
            allRenunciaAlumno() {
                let $vue = this;
                $vue.showLoader("Espere un momento por favor");
                axios_.get(APP.url("academico/resolucion/existentes/allRenunciaAlumno"))
                        .then(({data}) => {
                            $vue.resolucion.tramiteRenunciaAlumno = data;
                            $vue.hideLoader();
                            $vue.$forceUpdate();
                        }, () => {
                            $vue.hideLoader();
                        });
            },
        }
    };
</script>