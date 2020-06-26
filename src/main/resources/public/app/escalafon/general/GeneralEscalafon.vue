<template>
    <div class="panel-body">
        <h3 class="page-header"> Datos Generales </h3>
        <form id="form-validar-escalafon">
            <div class="form-group col-xs-6">
                <label>Persona</label>
                <multiselect 
                    v-model="escalafon.persona"
                    label='apellidosNombres'
                    track-by='id'
                    v-bind:options='listPersona'
                    placeholder="Ingrese y selecciona la persona"
                    v-on:search-change="searchPersona"
                    v-bind:allow-empty="false"
                    v-bind:show-labels="false"
                    v-bind:hide-selected="false"> 
                    <template slot="noOptions">La lista se encuentra vacía</template>
                    <template slot="noResult">No se encontraron resultados</template>
                </multiselect>
                <input v-model="escalafon.persona"  class="form-control hide" required="true"/>
            </div>
            <div class="form-group col-xs-6">
                <label>País</label>
                <multiselect  
                    v-model="escalafon.paisNacimiento"
                    label='nombre'
                    track-by='id'
                    v-bind:options='listPais'
                    placeholder="Seleccione el país"
                    v-on:search-change="searchPais"
                    v-bind:allow-empty="false"
                    v-bind:show-labels="false"
                    v-bind:hide-selected="false">              
                    <template slot="noOptions">La lista se encuentra vacía</template>
                    <template slot="noResult">No se encontraron resultados</template>
                </multiselect>                 
                <input type="text" required="true" class="hide" v-model="escalafon.paisNacimiento"/>      
            </div>
            <div class="form-group  col-xs-6">
                <label>Correo Electrónico Personal</label>
                <input style='height: 40px' type="email" v-model="escalafon.emailPersonal" class="form-control verificar-email"/>
            </div>
            <div class="form-group  col-xs-6">
                <label>Celular</label>
                <input  style='height: 40px' type="text" v-model="escalafon.celular" class="form-control sin-espacios numerico"/>
            </div>
            <div class="form-group  col-xs-4">
                <label>Código Dina</label>
                <input  style='height: 40px' type="text" v-model="escalafon.codigoDina" class="form-control"/>
            </div>
            <div class="form-group  col-xs-4">
                <label>Código Scopus</label>
                <input  style='height: 40px' type="text" v-model="escalafon.codigoScopus" class="form-control"/>
            </div>
            <div class="form-group  col-xs-4">
                <label>Código Orcid</label>
                <input  style='height: 40px' type="text" v-model="escalafon.codigoOrcid" class="form-control"/>
            </div>
            <div class="form-group col-xs-6">
                <label>Website</label>
                <input style='height: 40px' type="text" v-model="escalafon.website" class="form-control"/>
            </div>
            <div class="col-xs-2"></div>
            <div class="form-group col-xs-6">
                <label>Archivo Curriculum <a v-if="escalafon.archivoCurriculum != null &amp;&amp; escalafon.archivoCurriculum.substr(0,4) == 'http'" class="pointer" v-bind:href="escalafon.archivoCurriculum" target="_blank">(Ver Archivo)</a></label>
                <div class="row">
                    <div class="col-xs-6">
                        <input type="text"
                               v-model="escalafon.archivoCurriculum"
                               class="form-control"
                               disabled="true"/>
                    </div>
                    <div class="col-xs-6">
                        <file-upload
                            class="input-group-text align-middle pointer"
                            post-action="/comun/archivo/uploadFile"
                            v-model="files"
                            v-on:input-filter="inputFilter"
                            v-on:input-file="inputFile"
                            ref="upload">
                            <button class="btn btn-primary">Subir</button>
                        </file-upload>
                    </div>
                </div>
            </div>
            <div class="col-xs-12">
                <label>Resumen</label>
                <textarea type="text" required="true" class="form-control" rows="2" v-model="escalafon.resumen" ></textarea>
            </div>
            <div class="col-xs-12 text-center">
                <a class="btn btn-success" v-on:click="updateGeneral" v-bind:disabled="isLoading">
                    <i v-show="isLoading" class="fa fa-spinner fa-spin text-white"></i>
                    Actualizar
                </a> 
            </div>
        </form>
    </div>
</template>
<script>
    Vue.component("multiselect", window.VueMultiselect.default);
    Vue.component('file-upload', VueUploadComponent);

    module.exports = {
        data() {
            return{
                listPais: [],
                listPersona: [],
                files: [],
                isUpdated: false,
                isLoading: false
            };
        },
        computed: {
            ...Vuex.mapState(['escalafon'])
        },
        mounted() {
        },
        methods: {
            inputFilter(newFile, oldFile, prevent) {
                if (newFile && !oldFile) {
                    if (!/\.(doc|docx|jpg|jpeg|png|pdf)$/i.test(newFile.name)) {
                        swal('¡Este tipo de  archivo no esta permitido!', ' ', 'error', {buttons: {ok: "Aceptar"}});
                        return prevent();
                    }
                }
            },
            inputFile(newFile, oldFile) {
                let $vue = this;
                if (newFile) {
                    $('#progress-bar').css('width', newFile.progress + '%');
                    if (newFile.progress !== '100.00') {
                        $vue.escalafon.archivoCurriculum = 'Cargando...';
                        $vue.isUpdated = false;
                    }
                    if (Boolean(newFile) !== Boolean(oldFile) || oldFile.error !== newFile.error) {
                        if (!$vue.$refs.upload.active) {
                            $vue.$refs.upload.active = true;
                        }
                    }
                }
                if (oldFile && newFile) {
                    if (newFile.success) {
                        $vue.escalafon.archivoCurriculum = newFile.response.data;
                    }
                }
            },
            searchPersona(nombre) {
                let $vue = this;
                if (nombre == null || nombre.trim().length == 0) {
                    return;
                }
                $vue.listPersona = [];
                axios.get("/comun/buscar/allPersona", {params: {nombre: nombre}})
                        .then(response => {
                            $vue.listPersona = response.data.data;
                        });
            },
            searchPais(nombre) {
                let $vue = this;
                if (nombre == null || nombre.trim().length == 0) {
                    return;
                }
                $vue.listPais = [];
                axios.get("/comun/buscar/allPaises", {params: {nombre: nombre}})
                        .then(response => {
                            $vue.listPais = response.data.data;
                        });
            },
            updateGeneral() {
                let $vue = this;
                if (!$("#form-validar-escalafon").parsley().validate() || $vue.isLoading) {
                    return;
                }
                $vue.isLoading = true;
                axios.post("/escalafon/updateGeneral", $vue.escalafon)
                        .then(function (response) {
                            if (response.data.success) {
                                $vue.setterEscalafon(response.data.data);
                                notify(response.data.message, "success");
                                $vue.isUpdated = true;
                                $vue.isLoading = false;
                            } else {
                                notify(response.data.message, "warning");
                                $vue.isLoading = false;
                            }
                        })
                        .catch(function (error) {
                            notify(error.errorComunicacion, "error");
                            $vue.isLoading = false;
                        });
            },
            setterEscalafon(item) {
                let $vue = this;
                $vue.escalafon.persona = item.persona;
                $vue.escalafon.paisNacimiento = item.paisNacimiento;
                $vue.escalafon.emailPersonal = item.emailPersonal;
                $vue.escalafon.celular = item.celular;
                $vue.escalafon.codigoDina = item.codigoDina;
                $vue.escalafon.codigoScopus = item.codigoScopus;
                $vue.escalafon.codigoOrcid = item.codigoOrcid;
                $vue.escalafon.website = item.website;
                $vue.escalafon.archivoCurriculum = item.archivoCurriculum;
                $vue.escalafon.resumen = item.resumen;
            }
        }
    };
</script>
