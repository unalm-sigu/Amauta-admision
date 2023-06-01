<template>
    <modal-vik ref="modalPostergarCita"
               v-bind="modalPostergarCita"
               v-bind:okaction="saveCita">
        <div slot="body">

            <h4 class="text-primary block">{{title}}</h4>
            
            <form v-bind:id="form">
                <template v-if='visible'>
                    <div class="block text-danger m-t-lg h4">
                        <b>Fecha actual:</b> {{citaTempo.fecha}} - 
                        <b>Hora actual:</b> {{citaTempo.hora}}
                    </div>
                    
                    <div class="form-group">
                        <label class="text-danger">Motivo de la postergación</label>
                        <textarea v-model="citaNueva.motivoPostergacion" class="form-control" rows="3" required="yes"
                                  placeholder="Indique el motivo de la postergación de la cita"></textarea>
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label>Nueva fecha</label>
                                <div class="input-group date">
                                    <date-picker v-model="citaNueva.fecha"
                                                 required="true"
                                                 v-bind:config="configDate"
                                                 v-bind:wrap="true" >
                                    </date-picker>
                                    <div class="input-group-addon">
                                        <span class="fa fa-calendar"></span>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="form-group">
                                <label>Nueva hora</label>
                                <date-picker v-model="citaNueva.hora"
                                             required="true"
                                             v-bind:config="configDateStd">
                                </date-picker>
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <label>Objetivos de la cita</label>
                        <multiselect  
                            v-model="citaNueva.planesTutoriales" 
                            v-bind:options='objetivos'
                            v-bind:multiple='true'
                            v-bind:close-on-select='false'
                            v-bind:multiple='true'
                            label='objetivo'
                            track-by='id'
                            deselect-label=" "
                            select-label=" "
                            placeholder=" "
                            v-bind:allow-empty="false">

                            <template slot="noOptions">No existe objetivos disponibles</template>
                            <template slot="noResult">No hay objetivos seleccionados</template>
                        </multiselect>

                        <input type="text" class="hide" required="true"  v-bind:value="getListIds(citaNueva.planesTutoriales)"  />
                    </div>

                    <div class="form-group">
                        <label>Asunto</label>
                        <textarea v-model="citaNueva.asunto" class="form-control" rows="3" required="yes"></textarea>
                    </div>


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
                citaTempo: null,
                citaNueva: null,
                raptor: null,
                visible: false,
                editar: false,
                objetivos: [],
                form: "id-form-postergar-cita",
                title: "Postergar cita tutorizada",
                modalPostergarCita: VUE_MODAL.structFormAjax({
                    id: "id-modal-postergar-cita",
                    okbtn: "Postergar cita",
                    okclass: "btn-primary",
                    modalsize: "modal-lg"
                }),
                configDateStd: {
                    format: 'HH:mm',
                    locale: 'es'
                },
                configDate: {
                    format: 'DD/MM/YYYY',
                    locale: 'es'
                }
            };
        },
        methods: {
            open(config, raptor) {
                var form = $("#" + this.form);
                form.parsley().destroy();

                this.raptor = raptor;
                this.citaNueva = config.cita;
                this.citaTempo = JSON.parse(JSON.stringify(config.cita));
                this.alumno = config.alumno;
                this.visible = true;
                this.loadObjetivos();

                this.$refs.modalPostergarCita.open();
            },
            loadObjetivos() {
                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/allPlanesTutoria`,
                    body: {id: this.alumno.id}
                })).then((resp) => this.objetivos = resp.data.data);
            },
            saveCita() {
                var form = $("#" + this.form);
                if (!form.parsley().validate()) {
                    return;
                }

                myUtils.axios(VUE_AXIOS.structModalClose({
                    url: `/${rutaModulo}/postergarCitaTutorado`,
                    modal: this.$refs.modalPostergarCita,
                    raptor: this.raptor,
                    body: this.citaNueva
                }));
            },
            getModal() {
                return this.$refs.modalPostergarCita;
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