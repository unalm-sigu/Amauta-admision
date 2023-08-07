<template>
    <modal-vik ref="modalAddDerivacion"
               v-bind="modalAddDerivacion"
               v-bind:okaction="saveDerivacion">
        <div slot="body">

            <h4 class="text-primary block">{{title}}</h4>

            <form v-bind:id="form">
                <template v-if='visible'>
                    <h4 class="text-primary bold">¿Que tipo de atención requiere el estudiante?</h4>

                    <div class="row">
                        <div class="col-sm-1"></div>
                        <div class="col-md-3">
                            <div class="form-group">
                                <label class="radio inline">

                                    <input  type="radio"
                                            required="true"
                                            name="tipoAtencion"
                                            v-on:click="verificarAtencion"
                                            v-model="tipoAtencion"
                                            id="inlineCheckbox1"
                                            value="ESPECIALIZADA"
                                            /> Especializada
                                </label>

                            </div>
                        </div>

                        <div class="col-md-3">
                            <div class="form-group">
                                <label class="radio inline">
                                    <input  type="radio"
                                            required="true"
                                            name="tipoAtencion"
                                            v-on:click="verificarAtencion"
                                            v-model="tipoAtencion"
                                            id="inlineCheckbox2"
                                            value="OTRO"
                                            /> Otras
                                </label>

                            </div>
                        </div>
                    </div>

                    <template v-if="tipoAtencion">
                        <div class="form-group">
                            <multiselect  
                                v-model="derivacion.tipoAtencionTutorado"
                                v-on:input="selectTipoAtencion"
                                v-bind:options='tipos'
                                label='nombre'
                                track-by='id'
                                placeholder="Seleccione un tipo de atención"
                                v-bind:allow-empty="false">

                                <template slot="noOptions">No existe tipos disponibles</template>
                                <template slot="noResult">No hay un tipo seleccionado</template>
                            </multiselect>

                            <input type="text" class="hide" required="true" v-bind:value="getObjectId(derivacion.tipoAtencionTutorado)"  />
                        </div>

                        <template v-if="derivacion.tipoAtencionTutorado">
                            <div v-if="derivacion.tipoAtencionTutorado.codigo == 'ASESORIA_CURSO' " class="form-group">
                                <label>¿Sobre que curso?</label>
                                <multiselect  
                                    v-model="derivacion.curso" 
                                    v-bind:options='cursos'
                                    label='nombre'
                                    track-by='id'
                                    placeholder="Seleccione un curso"
                                    v-bind:allow-empty="false">
                                    <template slot="noOptions">No existe cursos disponibles</template>
                                    <template slot="noResult">No hay un curso seleccionado</template>
                                </multiselect>
                                <input type="text" class="hide" required="true" v-bind:value="getObjectId(derivacion.curso)"  />
                            </div>

                            <div v-if="derivacion.tipoAtencionTutorado.codigo == 'SEMINARIO_REFORZAR' " class="form-group">
                                <label>¿Sobre que curso?</label>
                                <multiselect  
                                    v-model="derivacion.curso" 
                                    v-bind:options='cursos'
                                    label='nombre'
                                    track-by='id'
                                    placeholder="Seleccione un curso"
                                    v-bind:allow-empty="false">
                                    <template slot="noOptions">No existe cursos disponibles</template>
                                    <template slot="noResult">No hay un curso seleccionado</template>
                                </multiselect>
                                <input type="text" class="hide" required="true" v-bind:value="getObjectId(derivacion.curso)"  />
                            </div>
                        </template>

                        <div class="form-group">
                            <label>Motivo de la derivación</label>
                            <textarea v-model="derivacion.motivoDerivacion" class="form-control" rows="3" required="yes"></textarea>
                        </div>
                    </template>
                </template>
            </form>
        </div>
    </modal-vik>
</template>

<script>

    module.exports = {

        data() {
            return {
                alumno: null,
                derivacion: null,
                raptor: null,
                tipoAtencion: null,
                visible: false,
                cursos: [],
                form: "id-form-derivar-tutorado",
                title: "Derivar estudiante",
                tiposAtenciones: JSON.parse(tiposAtencionesJson),
                tipos: [],
                modalAddDerivacion: VUE_MODAL.structFormAjax({
                    id: "id-modal-add-cita",
                    okbtn: "Registrar derivación",
                    okclass: "btn-primary",
                    showaccept: false
                })
            };
        },
        mounted() {
        },
        methods: {
            open(config, raptor) {
                var form = $("#" + this.form);
                form.parsley().destroy();

                this.raptor = raptor;
                this.derivacion = {tipoAtencionTutorado: null, curso: null};

                this.alumno = config.alumno;
                this.visible = true;
                this.tipoAtencion = null;
                this.loadCursos();

                this.$refs.modalAddDerivacion.open();
            },
            loadCursos() {
                if (this.cursos.length === 0) {
                    myUtils.axios(VUE_AXIOS.structGetData({
                        url: `/${rutaModulo}/${this.alumno.id}/allCursosMatriculados`
                    })).then((resp) => this.cursos = resp.data.data);
                }
            },
            verificarAtencion() {
                setTimeout(() => {
                    this.tipos = this.tiposAtenciones.filter(el => el.grupoAtencion === this.tipoAtencion);
                    this.derivacion.tipoAtencionTutorado = null;
                    this.derivacion.curso = null;
                    this.modalAddDerivacion.showaccept = true;
                }, 200);

            },
            selectTipoAtencion(item) {
                this.derivacion.curso = null;
            },
            saveDerivacion() {
                var form = $("#" + this.form);
                if (!form.parsley().validate()) {
                    return;
                }

                myUtils.axios(VUE_AXIOS.structModalClose({
                    url: `/${rutaModulo}/${this.alumno.id}/saveDerivacion`,
                    modal: this.$refs.modalAddDerivacion,
                    raptor: this.raptor,
                    body: this.derivacion
                }));
            },
            getModal() {
                return this.$refs.modalAddDerivacion;
            },

            // metodos genericos
            getListIds(list) {
                return list.map(item => item.id).join(',');
            },
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };
</script>