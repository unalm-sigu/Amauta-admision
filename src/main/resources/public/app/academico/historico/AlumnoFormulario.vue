<template>
    <div>

        <form data-parsley-validate="true" method="POST" id="formAlumno" >


            <div class="m-b-md m-t-md col-xs-12 bold">
                <h4>Datos Generales</h4>
            </div>

            <div class="col-sm-6">
                <div class="form-group">

                    <label>Tipo de documento de identidad</label>

                    <multiselect
                        v-model="alumno.persona.tipoDocumento"
                        v-bind:options="tiposDocumentos"
                        v-bind:allow-empty="true"
                        track-by="id"
                        label='nombre'
                        placeholder=" "
                        v-bind:internal-search="true"
                        v-bind:hide-selected="false"
                        v-bind:showNoOptions="true"
                        v-bind:show-labels="false">

                        <template slot="singleLabel" slot-scope="props">
                            <span class="option__title">
                                {{ props.option.nombre }}
                            </span>
                        </template>

                        <template slot="option" slot-scope="props">
                            <span class="option_title">
                                {{props.option.nombre}}
                            </span> 
                        </template>

                        <template slot="noOptions">&nbsp</template>
                        <template slot="noResult">&nbsp</template>

                    </multiselect>

                    <input type="text"
                           v-model="alumno.persona.tipoDocumento"
                           class="hide" required="true"/>

                </div>
            </div>

            <div class="col-sm-6">
                <div class="form-group">

                    <label>Número de documento de identidad</label>

                    <input type="text"
                           v-on:blur="cambiarNumDoc"
                           v-model="alumno.persona.numeroDocIdentidad"
                           class="form-control" required="true"/>

                </div>
            </div>

            <div class="row">
                <div class="col-xs-12">

                    <div class="col-sm-4">
                        <div class="form-group">

                            <label>Apellido Paterno</label>

                            <input type="text"
                                   required="true"
                                   v-model="alumno.persona.paterno"
                                   v-on:change="nombrePersona"
                                   class="form-control"/>

                        </div>
                    </div>

                    <div class="col-sm-4">
                        <div class="form-group">

                            <label>Apellido Materno</label>

                            <input type="text"
                                   v-model="alumno.persona.materno"
                                   v-on:change="nombrePersona"
                                   class="form-control"/>

                        </div>
                    </div>

                    <div class="col-sm-4">
                        <div class="form-group">
                            <label>Nombres</label>

                            <input type="text"
                                   required="true"
                                   v-model="alumno.persona.nombres"
                                   v-on:change="nombrePersona"
                                   class="form-control"/>

                        </div>
                    </div>

                </div>
            </div>






            <div class="col-sm-6">
                <div class="form-group">
                    <label>Sexo</label>

                    <div class="form-group">
                        <div class="col-sm-10">
                            <div class="col-sm-5">

                                <label class="radio inline">

                                    <input  type="radio"
                                            required="true"
                                            name="alumno.persona.sexo"
                                            id="inlineCheckbox1"
                                            value="M"
                                            v-model="alumno.persona.sexo"
                                            /> Masculino
                                </label>

                            </div>
                            <div class="col-sm-5">

                                <label class="radio inline">
                                    <input  type="radio"
                                            required="true"
                                            name="alumno.persona.sexo"
                                            id="inlineCheckbox2"
                                            value="F"
                                            v-model="alumno.persona.sexo"
                                            /> Femenino
                                </label>

                            </div>
                        </div>
                    </div>

                </div>
            </div>

            <div class="m-b-md m-t-md col-xs-12 bold">
                <h4> Lugar y fecha de nacimiento </h4>
            </div>

            <div class="col-sm-6">
                <div class="form-group">
                    <label>País</label>




                    <multiselect
                        v-model="alumno.persona.paisNacer"
                        v-bind:options="paises"
                        v-bind:allow-empty="true"
                        v-on:search-change="searchPais"
                        track-by="id"
                        placeholder=" "
                        v-bind:internal-search='false'
                        v-bind:hide-selected="false"
                        v-bind:showNoOptions="true"
                        v-bind:show-labels="false">

                        <template slot="singleLabel" slot-scope="props">
                            <span class="option__title">
                                {{ props.option.nombre }}
                            </span>
                        </template>

                        <template slot="option" slot-scope="props">
                            <span class="option_title">
                                {{props.option.nombre}}
                            </span> 
                        </template>

                        <template slot="noOptions">&nbsp</template>
                        <template slot="noResult">&nbsp</template>

                    </multiselect>

                    <input type="text"
                           v-model="alumno.persona.paisNacer"
                           class="hide" required="true"/>


                </div>
            </div>

            <div class="col-sm-6">

                <div  class="form-group" >

                    <label>Distrito de nacimiento (en Perú)</label>


                    <multiselect
                        v-model="alumno.persona.ubicacionNacer"
                        v-bind:options="ubicaciones"
                        v-bind:allow-empty="true"
                        v-on:search-change="searchUbicacion"
                        track-by="id"
                        placeholder=" "
                        v-bind:internal-search='false'
                        v-bind:hide-selected="false"
                        v-bind:showNoOptions="true"
                        v-bind:show-labels="false">

                        <template slot="singleLabel" slot-scope="props">
                            <span class="option__title">
                                {{ props.option.distrito }}
                            </span>
                        </template>

                        <template slot="option" slot-scope="props">
                            <span class="option_title">
                                {{props.option.distrito}}
                            </span> 
                        </template>

                        <template slot="noOptions">&nbsp</template>
                        <template slot="noResult">&nbsp</template>

                    </multiselect>

                </div>

            </div>

            <div class="col-sm-6">
                <div class="form-group">

                    <label>Fecha ( día / mes / año )</label>

                    <div class="input-group date">
                        <date-picker v-model="alumno.persona.fechaNacer" 
                                     required="true" 
                                     v-bind:config="configDate" 
                                     data-parsley-id="2" 
                                     v-bind:wrap="true" >
                        </date-picker>  
                        <div class="input-group-addon">
                            <span class="fa fa-calendar"></span>
                        </div>
                    </div>

                </div>
            </div>

            <div class="col-sm-6">
                <div class="form-group">

                    <label>Nacionalidad (Ingresa el país de tu nacionalidad)</label>



                    <multiselect
                        v-model="alumno.persona.nacionalidad"
                        v-bind:options="paises"
                        v-bind:allow-empty="true"
                        v-on:search-change="searchPais"
                        track-by="id"
                        placeholder=" "
                        v-bind:internal-search='false'
                        v-bind:hide-selected="false"
                        v-bind:showNoOptions="true"
                        v-bind:show-labels="false">

                        <template slot="singleLabel" slot-scope="props">
                            <span class="option__title">
                                {{ props.option.nombre }}
                            </span>
                        </template>

                        <template slot="option" slot-scope="props">
                            <span class="option_title">
                                {{props.option.nombre}}
                            </span> 
                        </template>

                        <template slot="noOptions">&nbsp</template>
                        <template slot="noResult">&nbsp</template>

                    </multiselect>



                    <input type="text"
                           v-model="alumno.persona.nacionalidad"
                           class="hide" required="true"/>



                </div>
            </div>

            <div class="m-b-md m-t-md col-xs-12 bold">
                <h4>Contacto</h4>
            </div>

            <div class="col-sm-4">
                <div class="form-group">

                    <label>Teléfono</label>

                    <input type="text"
                           v-model="alumno.persona.telefono"
                           class="form-control sin-espacios numerico"/>

                </div>
            </div>

            <div class="col-sm-4">
                <div class="form-group">

                    <label>Celular</label>

                    <input type="text"
                           v-model="alumno.persona.celular"
                           class="form-control sin-espacios numerico"/>

                </div>
            </div>

            <div class="col-sm-4">
                <div class="form-group">

                    <label>Correo Electrónico Personal</label>

                    <input type="text"
                           v-model="alumno.persona.email"
                           class="form-control verificar-email"/>

                </div>
            </div>

            <div class="col-sm-4">
                <div class="form-group">

                    <label>Correo Electrónico UNALM</label>

                    <input type="text"
                           v-model="alumno.persona.emailCompania"
                           class="form-control verificar-email"/>

                </div>
            </div>
            <div class="m-b-md m-t-md col-xs-12 bold">
                <h4>Domicilio</h4>
            </div>

            <div class="col-sm-6">
                <div class="form-group">

                    <label>País Domicilio</label>

                    <multiselect
                        v-model="alumno.persona.paisDomicilio"
                        v-bind:options="paises"
                        v-bind:allow-empty="true"
                        v-on:search-change="searchPais"
                        track-by="id"
                        placeholder=" "
                        v-bind:internal-search='false'
                        v-bind:hide-selected="false"
                        v-bind:showNoOptions="true"
                        v-bind:show-labels="false">

                        <template slot="singleLabel" slot-scope="props">
                            <span class="option__title">
                                {{ props.option.nombre }}
                            </span>
                        </template>

                        <template slot="option" slot-scope="props">
                            <span class="option_title">
                                {{props.option.nombre}}
                            </span> 
                        </template>

                        <template slot="noOptions">&nbsp</template>
                        <template slot="noResult">&nbsp</template>

                    </multiselect>

                    <input type="text"
                           v-model="alumno.persona.paisDomicilio"
                           class="hide" required="true"/>


                </div>
            </div>

            <div    class="col-sm-6">
                <div class="form-group">

                    <label>Ubicación Domicilio (Perú)</label>

                    <multiselect
                        v-model="alumno.persona.ubicacionDomicilio"
                        v-bind:options="ubicaciones"
                        v-bind:allow-empty="true"
                        v-on:search-change="searchUbicacion"
                        track-by="id"
                        placeholder=" "
                        v-bind:internal-search='false'
                        v-bind:hide-selected="false"
                        v-bind:showNoOptions="true"
                        v-bind:show-labels="false">

                        <template slot="singleLabel" slot-scope="props">
                            <span class="option__title">
                                {{ props.option.nombre }}
                            </span>
                        </template>

                        <template slot="option" slot-scope="props">
                            <span class="option_title">
                                {{props.option.nombre}}
                            </span> 
                        </template>

                        <template slot="noOptions">&nbsp</template>
                        <template slot="noResult">&nbsp</template>

                    </multiselect>




                </div>
            </div>

            <div class="col-sm-6">
                <div class="form-group">

                    <label class="bold">Dirección</label>

                    <input type="text"
                           class="form-control"
                           v-model="alumno.persona.direccion"
                           data-trigger="change" required="true" />

                </div>
            </div>



            <div class="m-b-md m-t-md col-xs-12 bold">
                <h4>Académico</h4>
            </div>


            <div class="col-sm-6">
                <div class="form-group">

                    <label class="bold">Código matrícula</label>

                    <input type="text"
                           class="form-control"
                           v-model="alumno.codigo"
                           data-trigger="change" required="true" />

                </div>
            </div>


            <div class="col-sm-6">
                <div class="form-group">

                    <label>Ciclo Académico Ingreso</label>



                    <multiselect
                        v-model="alumno.cicloIngreso"
                        v-bind:options="ciclos"
                        v-bind:allow-empty="true"
                        track-by="id"
                        placeholder=" "
                        v-bind:internal-search="true"
                        v-bind:hide-selected="false"
                        v-bind:showNoOptions="true"
                        v-bind:show-labels="false">

                        <template slot="singleLabel" slot-scope="props">
                            <span class="option__title">
                                {{ props.option.descripcion }}
                            </span>
                        </template>

                        <template slot="option" slot-scope="props">
                            <span class="option_title">
                                {{props.option.descripcion}}
                            </span> 
                        </template>

                        <template slot="noOptions">&nbsp</template>
                        <template slot="noResult">&nbsp</template>

                    </multiselect>


                    <input type="text"
                           class="hide"
                           v-model="alumno.cicloIngreso" required="true" />


                </div>
            </div>


            <div class="col-sm-6">
                <div class="form-group">

                    <label>Ciclo Académico Activo</label>



                    <multiselect
                        v-model="alumno.cicloActivo"
                        v-bind:options="ciclos"
                        v-bind:allow-empty="true"
                        track-by="id"
                        placeholder=" "
                        v-bind:internal-search="true"
                        v-bind:hide-selected="false"
                        v-bind:showNoOptions="true"
                        v-bind:show-labels="false">

                        <template slot="singleLabel" slot-scope="props">
                            <span class="option__title">
                                {{ props.option.descripcion }}
                            </span>
                        </template>

                        <template slot="option" slot-scope="props">
                            <span class="option_title">
                                {{props.option.descripcion}}
                            </span> 
                        </template>

                        <template slot="noOptions">&nbsp</template>
                        <template slot="noResult">&nbsp</template>

                    </multiselect>




                </div>
            </div>



            <div class="col-sm-6">
                <div class="form-group">

                    <label class="bold">Situación Académica</label>

                    <multiselect
                        v-model="alumno.situacionAcademica"
                        v-bind:options="situaciones"
                        v-bind:allow-empty="true"
                        track-by="id"
                        label='nombre'
                        placeholder=" "
                        v-bind:internal-search="true"
                        v-bind:hide-selected="false"
                        v-bind:showNoOptions="true"
                        v-bind:show-labels="false">

                        <template slot="singleLabel" slot-scope="props">
                            <span class="option__title">
                                {{ props.option.nombre }}
                            </span>
                        </template>

                        <template slot="option" slot-scope="props">
                            <span class="option_title">
                                {{props.option.nombre}}
                            </span> 
                        </template>

                        <template slot="noOptions">&nbsp</template>
                        <template slot="noResult">&nbsp</template>

                    </multiselect>

                    <input type="text"
                           class="hide"
                           v-model="alumno.situacionAcademica"
                           data-trigger="change" required="true" />

                </div>
            </div>

            <div class="col-sm-6">
                <div class="form-group">

                    <label class="bold">Modalidad de Estudio</label>

                    <multiselect
                        v-model="alumno.modalidadEstudio"
                        v-bind:options="modalidades"
                        v-bind:allow-empty="true"
                        label='nombre'
                        track-by="id"
                        placeholder=" "
                        v-bind:internal-search="true"
                        v-bind:hide-selected="false"
                        v-bind:showNoOptions="true"
                        v-bind:show-labels="false">

                        <template slot="singleLabel" slot-scope="props">
                            <span class="option__title">
                                {{ props.option.nombre }}
                            </span>
                        </template>

                        <template slot="option" slot-scope="props">
                            <span class="option_title">
                                {{props.option.nombre}}
                            </span> 
                        </template>

                        <template slot="noOptions">&nbsp</template>
                        <template slot="noResult">&nbsp</template>

                    </multiselect>


                    <input type="text"
                           class="hide"
                           v-model="alumno.modalidadEstudio" required="true" />

                </div>
            </div>

            <div class="col-sm-6">
                <div class="form-group">

                    <label class="bold">Carrera</label>

                    <multiselect
                        v-model="alumno.carrera"
                        v-bind:options="carreras"
                        v-bind:allow-empty="true"
                        v-on:search-change="searchCarrera"
                        track-by="id"
                        placeholder=" "
                        v-bind:internal-search='false'
                        v-bind:hide-selected="false"
                        v-bind:showNoOptions="true"
                        v-bind:show-labels="false">

                        <template slot="singleLabel" slot-scope="props">
                            <span class="option__title">
                                {{ props.option.nombre }}
                            </span>
                        </template>

                        <template slot="option" slot-scope="props">
                            <span class="option_title">
                                {{props.option.nombre}}
                            </span> 
                        </template>

                        <template slot="noOptions">&nbsp</template>
                        <template slot="noResult">&nbsp</template>

                    </multiselect>


                    <input type="text"
                           class="hide"
                           v-model="alumno.carrera" required="true" />


                </div>
            </div>

            <div class="col-sm-6">
                <div class="form-group">

                    <label class="bold">Orientación Carrera</label>



                </div>
            </div>

            <div class="col-sm-6">
                <div class="form-group">

                    <label class="bold">Plan Curricular</label>



                </div>
            </div>


            <div class="col-xs-12 text-center m-t-md m-b-md">

                <button type="button" v-if="!alumno.id"
                        class="btn btn-primary" 
                        v-text="alumno.id?'Actualizar':'Guardar'"  
                        v-on:click.prevent="submitForm">Guardar Alumno</button>

            </div>

        </form>

    </div>
</template>

<script>
    module.exports = {
        mixins: [VueLoader],
        components: {

        },
        data() {
            return {
                configDate: {
                    format: "DD/MM/YYYY",
                    useCurrent: false
                },
                persona: {},
                tiposDocumentos: [],
                paises: [],
                ubicaciones: [],
                situaciones: [],
                ciclos: [],
                carreras: [],
                modalidades: [],
            };
        },
        computed: {
            ...Vuex.mapState(["alumno"])
        },
        mounted: function () {
            let $vue = this;
            $vue.loadData();
            if (ID_ALUMNO) {
                $vue.fetchAlumno();
            }
        },
        methods: {
            ...Vuex.mapActions(['fetchAlumno']),
            nombrePersona() {

            },
            cambiarNumDoc() {

                var $vue = this;
                $vue.showLoader();

                var isvalid = $vue.alumno.persona.tipoDocumento ? true : false;
                isvalid &= $vue.alumno.persona.numeroDocIdentidad ? true : false;

                if (!isvalid) {
                    $vue.hideLoader();
                    return;
                }

                let postPersona = {...$vue.alumno.persona};
                
                axios_.post(APP.url('academico/historico/alumno/existealumno'), postPersona).
                        then(({data}) => {
                            if (data.id) {
                                $vue.alumno.persona = {...data};
                                $vue.$forceUpdate();
                            } else {
                                $vue.alumno.persona = {};
                                $vue.alumno.persona.numeroDocIdentidad = postPersona.numeroDocIdentidad;
                                $vue.alumno.persona.tipoDocumento = postPersona.tipoDocumento;
                            }
                            $vue.hideLoader();
                        }, () => {
                            $vue.alumno.persona = {};
                            $vue.alumno.persona.tipoDocumento = postPersona.tipoDocumento;
                            $vue.hideLoader();
                        });
            },
            loadData() {
                let $vue = this;
                axios_.get(APP.url('academico/historico/alumno/datos')).
                        then(({data}) => {
                            $vue.tiposDocumentos = data.tiposDocumentos;
                            $vue.ciclos = data.ciclos;
                            $vue.modalidades = data.modalidades;
                            $vue.situaciones = data.situaciones;
                        }, () => {
                        });
            },
            submitForm() {
                let $vue = this;
                if ($('#formAlumno').parsley().validate() != true) {
                    return;
                }
                $vue.showLoader();
                axios_.post(APP.url('academico/historico/alumno/save'), {...$vue.alumno}).
                        then(({data}) => {
                            notify(data.message, "info");
                            $vue.updateAlumano(data.id);
                            $vue.hideLoader();
                        }, () => {
                            $vue.hideLoader();
                        });
            },
            searchPais(nombre) {
                let $vue = this;
                if (!nombre) {
                    return;
                }
                axios.get("/comun/buscar/allPaises", {params: {nombre: nombre}})
                        .then(({data}) => {
                            $vue.paises = data.data;
                        });
            },
            searchUbicacion(nombre) {
                let $vue = this;
                if (!nombre) {
                    return;
                }
                axios.get("/comun/buscar/allDistritos", {params: {nombre: nombre}})
                        .then(({data}) => {
                            $vue.ubicaciones = data.data;
                        });
            },
            searchCarrera(nombre) {
                let $vue = this;
                if (!nombre) {
                    return;
                }
                axios.get("/comun/buscar/allCarrera", {params: {nombre: nombre}})
                        .then(({data}) => {
                            $vue.carreras = data.data;
                        });
            },
            updateAlumano(id) {
                let $vue = this;
                ID_ALUMNO = id;
                $vue.fetchAlumno();
            }
        }
    };
</script>