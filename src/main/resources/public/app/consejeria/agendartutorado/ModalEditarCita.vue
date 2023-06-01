<template>
    <modal-vik ref="modalEditarCita"
               v-bind="modalEditarCita"
               v-bind:okaction="saveCita">
        <div slot="body">

            <h4 class="text-primary block">{{title}}</h4>

            <form v-bind:id="form">
                <template v-if='visible'>
                    <div class="row m-t-lg">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label>Fecha cita</label>
                                <div class="item-form-control item-form-gray text-primary">{{citaNueva.fecha}}</div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="form-group">
                                <label>Hora</label>
                                <div class="item-form-control item-form-gray text-primary">{{citaNueva.hora}}</div>
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
                citaNueva: null,
                raptor: null,
                visible: false,
                editar: false,
                objetivos: [],
                form: "id-form-editar-cita",
                title: "Modificar información de cita tutorizada",
                modalEditarCita: VUE_MODAL.structFormAjax({
                    id: "id-modal-editar-cita",
                    okbtn: "Modificar cita",
                    okclass: "btn-warning",
                    modalsize: "modal-lg"
                })
            };
        },
        methods: {
            open(config, raptor) {
                var form = $("#" + this.form);
                form.parsley().destroy();

                this.raptor = raptor;
                this.citaNueva = config.cita;
                this.alumno = config.alumno;
                this.visible = true;
                this.loadObjetivos();

                this.$refs.modalEditarCita.open();
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
                    url: `/${rutaModulo}/updateCitaTutorado`,
                    modal: this.$refs.modalEditarCita,
                    raptor: this.raptor,
                    body: this.citaNueva
                }));
            },
            getModal() {
                return this.$refs.modalEditarCita;
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