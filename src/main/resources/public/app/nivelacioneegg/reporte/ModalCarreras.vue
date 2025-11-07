<template>
    <modal-vik ref="modalCarrerasPre"
               v-bind="modalCarrerasPre"
               v-bind:okaction="reporteCarrera">
        <div slot="body">
            <div class="form-group">
                <label></label>
                <div class="input-group">
                    <multiselect                                                   
                        v-model='becaestudioEdit.institucion'
                        v-bind:internal-search='true'
                        v-bind:limit='15'
                        v-bind:options='instituciones'
                        placeholder="Seleccione una beca"
                        label="razonSocial"
                        v-bind:show-labels="false"
                        v-bind:allow-empty="false"
                        track-by="id">
                    </multiselect>
                    <div class="input-group-addon">
                        <i class="fa fa-plus pointer" v-on:click.prevent="addInstitucion()" aria-hidden="true"></i>
                    </div>
                </div>

            </div>
        </div>
    </modal-vik>
</template>
<script>
    
     module.exports = {

        data() {
            return {
                form: "id-form-carrera",
                title: "",
                carreras: [],
                modalRelacionCursoConTema: VUE_MODAL.structFormAjax({
                    id: "id-modal-carrera",
                    okbtn: "Guardar",
                    okclass: "btn-primary"
                })
            };
        },
        mounted() {

        },
        methods: {

            abrirModalRelacion(item, raptor) {
                var form = $("#" + this.form);
                form.parsley().destroy();
                this.cursoListTemas.curso = item;

                this.title = item.codigo + " " + item.nombre;
                this.$refs.modalRelacionCursoConTema.open();
                this.raptor = raptor;

                let $vue = this;
                $vue.getTemas();
                $vue.getCursoTemasSeleccionados();
            },

            getCarreras() {
                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/allCarreras`
                })).then((resp) => this.temas = resp.data.data);
            },
            saveRelacion() {
                var form = $("#" + this.form);
                if (!form.parsley().validate()) {
                    return;
                }

                this.cursoListTemas.ids = this.seleccionados;

                myUtils.axios(VUE_AXIOS.structModalClose({
                    url: `/${rutaModulo}/saveRelacion`,
                    modal: this.$refs.modalRelacionCursoConTema,
                    raptor: this.raptor,
                    body: this.cursoListTemas
                }));
            }

        }
    };
    

</script>
